package hudson.plugins.findbugs.audit;

import com.thoughtworks.xstream.XStream;
import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.ModelObject;
import hudson.plugins.analysis.core.AbstractResultAction;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.util.model.AnnotationStream;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.findbugs.FindBugsResultAction;

import hudson.security.ACL;
import hudson.security.AccessControlled;
import hudson.security.Permission;
import jenkins.model.Jenkins;
import org.acegisecurity.AccessDeniedException;
import org.kohsuke.stapler.bind.JavaScriptMethod;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * The view will be responsible for tracking all things related to the task of auditing. Important persistent data
 * will be serialised into the file system.
 *
 * @author William Wu
 */
@ExportedBean
@SuppressWarnings({"PMD.ExcessiveClassLength"})
public class FindBugsAudit implements ModelObject, Serializable{

    private AbstractBuild<?,?> build;
    private final AbstractProject<?,?> project;
    private boolean auditFingerprintsLoaded = false;

    private Collection<AuditFingerprint> auditWarnings;

    //For the summary table
    private int previousNumberOfUnconfirmedWarnings = 0;
    private int previousNumberOfConfirmedWarnings = 0;
    private int newNumberOfUnconfirmedWarnings = 0;
    private int newNumberOfConfirmedWarnings = 0;
    private int fixedNumberOfUnconfirmedWarnings = 0;
    private int fixedNumberOfConfirmedWarnings = 0;

    public FindBugsAudit(AbstractBuild<?,?> build, Collection<FileAnnotation> deltaNumberOfAnnotationsDuringFiltering){
        this.build = build;
        this.project = build.getProject();
        this.auditWarnings = new ArrayList<AuditFingerprint>();

        FindBugsAudit previousAudit = getReferenceAudit();
        if (previousAudit != null) {
            Collection<AuditFingerprint> referenceWarnings = previousAudit.getAllWarnings();
            for (AuditFingerprint fingerprint : referenceWarnings) {
                AuditFingerprint newFingerprint = new AuditFingerprint(fingerprint.getAnnotation());
                newFingerprint.setFalsePositive(fingerprint.isFalsePositive());
                newFingerprint.setTrackedInCloud(fingerprint.isTrackedInCloud());
                newFingerprint.setTrackingUrl(fingerprint.getTrackingUrl());
                this.auditWarnings.add(newFingerprint);
            }

            for (Iterator<AuditFingerprint> iterator = this.auditWarnings.iterator(); iterator.hasNext();){
                AuditFingerprint currentFingerprint = iterator.next();
                for (FileAnnotation deltaAnnotation : deltaNumberOfAnnotationsDuringFiltering) {
                    if (currentFingerprint.getAnnotation().equals(deltaAnnotation)) {
                        iterator.remove();
                    }
                }
            }

            Collection<FileAnnotation> newWarningsForCurrentBuild = getCurrentBuildResult().getNewWarnings();
            for (FileAnnotation annotation : newWarningsForCurrentBuild) {
                this.auditWarnings.add(new AuditFingerprint(annotation));
            }

            // These numbers are serialised into build.xml
            previousNumberOfUnconfirmedWarnings = previousAudit.getUnconfirmedWarnings().size();
            previousNumberOfConfirmedWarnings = previousAudit.getConfirmedWarnings().size();
            newNumberOfUnconfirmedWarnings = newWarningsForCurrentBuild.size();
            fixedNumberOfUnconfirmedWarnings = getCurrentBuildResult().getNumberOfFixedWarnings();
            //Essentially the number that was not able to be removed because it does not exist anymore
            fixedNumberOfConfirmedWarnings = deltaNumberOfAnnotationsDuringFiltering.size();
        } else {
            copyCurrentBuildResultAnnotations();
        }
        serialiseAuditFingerprints();
    }

    public void loadClassData(){
        if (!loadAuditFingerprints()){
            copyCurrentBuildResultAnnotations();
            serialiseAuditFingerprints();
        }
    }



    private void copyCurrentBuildResultAnnotations(){
        BuildResult currentBuildResult = getCurrentBuildResult();
        this.auditWarnings = new ArrayList<AuditFingerprint>();
        if (currentBuildResult != null) {
            for (FileAnnotation annotations : currentBuildResult.getAnnotations()) {
                auditWarnings.add(new AuditFingerprint(annotations));
            }
        }
    }

    public FindBugsAudit getReferenceAudit(){
        AbstractBuild<?,?> previousBuild = this.build.getPreviousSuccessfulBuild();
        if (previousBuild != null) {
            List<? extends Action> previousActions = previousBuild.getAllActions();
            for( Action action : previousActions ) {
                if (action instanceof AuditAction) {
                    return ((AuditAction) action).getAuditView();
                }
            }
        }
        return null;
    }

    public BuildResult getCurrentBuildResult(){
        for (Action action : this.build.getAllActions()) {
            if (action instanceof FindBugsResultAction) {
                return ((AbstractResultAction) action).getResult();
            }
        }
        return null;
    }

    @Override
    public String getDisplayName() {
        return "FindBugs Auditing";
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }

    @Exported
    public int getBuildNumber(){
        return this.build.number;
    }

    public int getPreviousBuildNumber(){
        return this.build.getPreviousSuccessfulBuild().number;
    }

    public int getLastSuccessfulBuildNumber() {
        return this.project.getLastSuccessfulBuild().number;
    }

    public boolean isLatestSuccessfulBuild(){
        return this.build.number == this.build.getProject().getLastSuccessfulBuild().number;
    }

    @JavaScriptMethod
    public void boundSystemLogger(String message){
        System.out.println(message);
    }

    @JavaScriptMethod
    public void boundUpdateWarnings(String message){
        System.out.println("Removing annotations: [ID] " + message);
        //Jenkins.getAuthentication().getAuthorities();

        String[] stringID = message.split(", ");
        Collection<FileAnnotation> removeFalsePositives = new ArrayList<FileAnnotation>();
        for (int i = 0; i< stringID.length ; i++){
            long annotationID = Long.parseLong(stringID[i]);
            for (AuditFingerprint fingerprint : this.auditWarnings) {
                if (fingerprint.getAnnotation().getKey() == annotationID) {
                    fingerprint.setFalsePositive(true);
                    removeFalsePositives.add(fingerprint.getAnnotation());
                    newNumberOfConfirmedWarnings++;
                    fixedNumberOfUnconfirmedWarnings++;
                }
            }
        }
        serialiseAuditFingerprints();

        BuildResult findBugsResult = getCurrentBuildResult();
        if(findBugsResult != null){
            findBugsResult.removeAnnotations(removeFalsePositives);
        }
    }

    private void serialiseAuditFingerprints(){
        try {
            XmlFile file = getSerializationAuditFile();
            file.write(this.auditWarnings);
        } catch (IOException io){
            System.out.println(io);
        }
    }

    private boolean loadAuditFingerprints(){
        try {
            XmlFile file = getSerializationAuditFile();
            if (file.exists()) {
                if (!this.auditFingerprintsLoaded) {
                    this.auditWarnings = (Collection<AuditFingerprint>) file.read();
                    this.auditFingerprintsLoaded = true;
                    calculateWarningNumbers();
                }
                return true;
            }
        } catch (Exception e){
            // Failed file
            System.out.println(e);
        }
        return false;
    }

    private void calculateWarningNumbers(){
        FindBugsAudit previousAudit = getReferenceAudit();
        if (previousAudit != null){
            int delta = getConfirmedWarnings().size() - (previousAudit.getConfirmedWarnings().size() - fixedNumberOfConfirmedWarnings);
            this.newNumberOfConfirmedWarnings = delta;
        } else {
            this.newNumberOfConfirmedWarnings = getConfirmedWarnings().size();
        }
        this.fixedNumberOfUnconfirmedWarnings += this.newNumberOfConfirmedWarnings;
    }

    private XmlFile getSerializationAuditFile(){
        XmlFile file = new XmlFile(getXStream(),
                new File(this.build.getRootDir(), getSerializationFileName().replace(".xml", "-auditFingerprints.xml")));
        return file;
    }

    private XStream getXStream() {
        AnnotationStream xstream = new AnnotationStream();
        configure(xstream);
        return xstream;
    }

    protected void configure(final XStream xstream) {
        // empty default
    }

    protected String getSerializationFileName(){
        return "findbugs.xml";
    }












    // Stuff mainly for index.jelly
    public List<AuditFingerprint> getAllWarnings(){
        List<AuditFingerprint> warnings = new ArrayList<AuditFingerprint>(this.auditWarnings);
        Collections.sort(warnings);
        return warnings;
    }

    public List<AuditFingerprint> getUnconfirmedWarnings(){
        List<AuditFingerprint> warnings = new ArrayList<AuditFingerprint>();
        for (AuditFingerprint fingerprint : this.auditWarnings) {
            if (!fingerprint.isFalsePositive() && !fingerprint.isTrackedInCloud()) {
                warnings.add(fingerprint);
            }
        }
        return warnings;
    }

    public List<AuditFingerprint> getConfirmedWarnings(){
        List<AuditFingerprint> warnings = new ArrayList<AuditFingerprint>();
        warnings.addAll(getTrackedWarnings());
        warnings.addAll(getFalsePositiveWarnings());
        return warnings;
    }

    public List<AuditFingerprint> getTrackedWarnings(){
        List<AuditFingerprint> warnings = new ArrayList<AuditFingerprint>();
        for (AuditFingerprint fingerprint : this.auditWarnings) {
            if (fingerprint.isTrackedInCloud()) {
                warnings.add(fingerprint);
            }
        }
        return warnings;
    }

    public List<AuditFingerprint> getFalsePositiveWarnings(){
        List<AuditFingerprint> warnings = new ArrayList<AuditFingerprint>();
        for (AuditFingerprint fingerprint : this.auditWarnings) {
            if (fingerprint.isFalsePositive()) {
                warnings.add(fingerprint);
            }
        }
        return warnings;
    }

    // Methods for the summary table
    public int getPreviousNumberOfUnconfirmedWarnings(){
        return previousNumberOfUnconfirmedWarnings;
    }

    public String getPreviousUnconfirmedTooltip(){
        return "Previous number of unconfirmed warnings";
    }

    public int getPreviousNumberOfConfirmedWarnings(){
        return previousNumberOfConfirmedWarnings;
    }

    public String getPreviousConfirmedTooltip(){
        return "Previous number of confirmed warnings";
    }

    public int getNewNumberOfUnconfirmedWarnings(){
        return newNumberOfUnconfirmedWarnings;
    }

    public String getNewUnconfirmedTooltip(){
        return "New number of warnings from build result " + getBuildNumber();
    }

    public int getNewNumberOfConfirmedWarnings(){
        return newNumberOfConfirmedWarnings;
    }

    public String getNewConfirmedTooltip(){
        return "Updated based on auditing in this build only";
    }

    public int getFixedNumberOfUnconfirmedWarnings(){
        return fixedNumberOfUnconfirmedWarnings;
    }

    public String getFixedUnconfirmedTooltip(){
        return "Warnings no longer present in this build + confirmed FP by this audit";
    }

    public int getFixedNumberOfConfirmedWarnings(){
        return fixedNumberOfConfirmedWarnings;
    }

    public String getFixedConfirmedTooltip(){
        return "Confirmed false positives no longer in the current build";
    }

    public String getUpdateSelectionButtonTooltip(){
        return "Only activated on latest build, graph may update in next build";
    }


}
