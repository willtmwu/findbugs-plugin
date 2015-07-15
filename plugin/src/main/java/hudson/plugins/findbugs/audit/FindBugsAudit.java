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
import hudson.plugins.findbugs.FindBugsResult;
import hudson.plugins.findbugs.FindBugsResultAction;
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

    private Collection<AuditFingerprint> auditWarnings = null;

    public FindBugsAudit(AbstractBuild<?,?> build){
        this.build = build;
        this.project = build.getProject();

        Collection<AuditFingerprint> referenceWarnings = getReferenceAudit().getAllWarnings();
        if (referenceWarnings != null) {
            for (AuditFingerprint fingerprint : referenceWarnings) {
                AuditFingerprint newFingerprint = new AuditFingerprint(fingerprint.getAnnotation());
                newFingerprint.setConfirmedWarning(fingerprint.isConfirmedWarning());
                newFingerprint.setFalsePositive(fingerprint.isFalsePositive());
                newFingerprint.setTrackedInCloud(fingerprint.isTrackedInCloud());
                newFingerprint.setTrackingUrl(fingerprint.getTrackingUrl());
                this.auditWarnings.add(newFingerprint);
            }

            for (FileAnnotation annotation : getCurrentBuildResult().getNewWarnings()) {
                this.auditWarnings.add(new AuditFingerprint(annotation));
            }

        } else {
            Set<FileAnnotation> buildResultAnnotations = getCurrentBuildResult().getAnnotations();
            for (FileAnnotation annotations : buildResultAnnotations) {
                auditWarnings.add(new AuditFingerprint(annotations));
            }
        }
        serialiseAuditFingerprints();
    }

    public void loadClassData(){
        if (!loadAuditFingerprints()){
            Set<FileAnnotation> buildResultAnnotations = getCurrentBuildResult().getAnnotations();
            for (FileAnnotation annotations : buildResultAnnotations) {
                auditWarnings.add(new AuditFingerprint(annotations));
            }
            serialiseAuditFingerprints();
        }
    }

    @JavaScriptMethod
    public void updateWarnings(){
        System.out.println("Checking update history: latest " + build.getProject().getLastSuccessfulBuild().number);

        //Test single first remove
        FindBugsResult findBugsResult = (FindBugsResult) getCurrentBuildResult();
        if (findBugsResult != null) {
            for(FileAnnotation fileAnnotation : findBugsResult.getAnnotations()){
                findBugsResult.removeAnnotation(fileAnnotation);
                break;
            }
        }
    }

    public List<AuditFingerprint> getAllWarnings(){
        List<AuditFingerprint> warnings = new ArrayList<AuditFingerprint>(this.auditWarnings);
        Collections.sort(warnings);
        return warnings;
    }

    public List<AuditFingerprint> getUnconfirmedWarnings(){
        List<AuditFingerprint> warnings = new ArrayList<AuditFingerprint>();
        for (AuditFingerprint fingerprint : this.auditWarnings) {
            if (!fingerprint.isConfirmedWarning()) {
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
            if (fingerprint.isConfirmedWarning() && fingerprint.isTrackedInCloud()) {
                warnings.add(fingerprint);
            }
        }
        return warnings;
    }

    public List<AuditFingerprint> getFalsePositiveWarnings(){
        List<AuditFingerprint> warnings = new ArrayList<AuditFingerprint>();
        for (AuditFingerprint fingerprint : this.auditWarnings) {
            if (fingerprint.isConfirmedWarning() && fingerprint.isFalsePositive()) {
                warnings.add(fingerprint);
            }
        }
        return warnings;
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
                this.auditWarnings = (Collection<AuditFingerprint>) file.read();
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

    //ret or null if not found, reference as in previous build
    public FindBugsAudit getReferenceAudit(){
        AbstractBuild<?,?> previousBuild = this.build.getPreviousSuccessfulBuild();
        List<? extends Action> previousActions = previousBuild.getAllActions();
        for( Action action : previousActions ) {
            if (action instanceof AuditAction) {
                return ((AuditAction) action).getAuditView();
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

}
