package hudson.plugins.findbugs.audit;

import com.google.common.collect.ImmutableSortedSet;
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
import hudson.plugins.findbugs.FindBugsResult;
import hudson.plugins.findbugs.FindBugsResultAction;
import hudson.util.FormValidation;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
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
    private boolean classDataLoaded = false;

    private Collection<AuditFingerprint> auditWarnings;

    public FindBugsAudit(AbstractBuild<?,?> build){
        this.build = build;
        this.project = build.getProject();
        this.auditWarnings = new ArrayList<AuditFingerprint>();

        FindBugsAudit previousAudit = getReferenceAudit();
        if (previousAudit != null && previousAudit.getAllWarnings().size() > 0) {
            Collection<AuditFingerprint> referenceWarnings = previousAudit.getAllWarnings();
            for (AuditFingerprint fingerprint : referenceWarnings) {
                AuditFingerprint newFingerprint = new AuditFingerprint(fingerprint.getAnnotation());
                newFingerprint.setFalsePositive(fingerprint.isFalsePositive());
                newFingerprint.setTrackedInCloud(fingerprint.isTrackedInCloud());
                newFingerprint.setTrackingUrl(fingerprint.getTrackingUrl());
                this.auditWarnings.add(newFingerprint);
            }

            for (FileAnnotation annotation : getCurrentBuildResult().getNewWarnings()) {
                this.auditWarnings.add(new AuditFingerprint(annotation));
            }

        } else {
            BuildResult currentBuildResult = getCurrentBuildResult();
            if (currentBuildResult != null) {
                for (FileAnnotation annotations : currentBuildResult.getAnnotations()) {
                    auditWarnings.add(new AuditFingerprint(annotations));
                }
            }
        }
        serialiseAuditFingerprints();
    }

    public void loadClassData(){
        if (!loadAuditFingerprints()){
            BuildResult currentBuildResult = getCurrentBuildResult();
            this.auditWarnings = new ArrayList<AuditFingerprint>();
            if (currentBuildResult != null) {
                for (FileAnnotation annotations : currentBuildResult.getAnnotations()) {
                    auditWarnings.add(new AuditFingerprint(annotations));
                }
            }
            serialiseAuditFingerprints();
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

    public int getLastSuccessfulBuildNumber(){
        return this.project.getLastSuccessfulBuild().number;
    }

    public boolean isLatestSuccessfulBuild(){
        return (getBuildNumber() == getLastSuccessfulBuildNumber());
    }

    @JavaScriptMethod
    public void boundSystemLogger(String message){
        System.out.println(message);
    }

    @JavaScriptMethod
    public void boundUpdateWarnings(String message){
        System.out.println("Removing annotations: [ID] " + message);

        String[] stringID = message.split(", ");
        for (int i = 0; i< stringID.length ; i++){
            long annotationID = Long.parseLong(stringID[i]);
            for (AuditFingerprint fingerprint : this.auditWarnings) {
                if (fingerprint.getAnnotation().getKey() == annotationID) {
                    fingerprint.setFalsePositive(true);
                }
            }
        }

        FindBugsResult findBugsResult = (FindBugsResult) getCurrentBuildResult();
        if(findBugsResult != null){
            Collection<FileAnnotation> removeAnnotations = new ArrayList<FileAnnotation>();
            for (AuditFingerprint fingerprint : this.getFalsePositiveWarnings()) {
                removeAnnotations.add(fingerprint.getAnnotation());
            }
            findBugsResult.removeAnnotations(removeAnnotations);
        }

        serialiseAuditFingerprints();
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
            if (!this.classDataLoaded) {
                XmlFile file = getSerializationAuditFile();
                if (file.exists()) {
                    this.auditWarnings = (Collection<AuditFingerprint>) file.read();
                    this.classDataLoaded = true;
                    return true;
                }
            } else {
                return true;
            }
        } catch (Exception e){
            // Failed file
            System.out.println(e);
        }
        return false;
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








}
