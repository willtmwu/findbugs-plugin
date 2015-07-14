package hudson.plugins.findbugs.audit;

import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.ModelObject;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.findbugs.FindBugsResult;
import hudson.plugins.findbugs.FindBugsResultAction;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The view will be responsible for tracking all things related to the task of auditing. Important persistent data
 * will be serialised into the file system.
 *
 * @author William Wu
 */
public class FindBugsAudit implements ModelObject, Serializable{

    private AbstractBuild<?,?> build;
    private final AbstractProject<?,?> project;

    //Not sure if needing transient, will need to create new XmlFile to persist to memory
    //Submit/update button is only used on latest build
    private transient Collection<AuditFingerprint> auditWarnings;


    public FindBugsAudit(AbstractBuild<?,?> build){
        this.build = build;
        this.project = build.getProject();

        //Direct copy over for now..... need to define reference

    }


    @JavaScriptMethod
    public void updateWarnings(){
        System.out.println("Checking update history: latest " + build.getProject().getLastSuccessfulBuild().number);

        //Now let's try forcing remove a single FileAnnotation and persist it into underlying serialisation memory
        //build.xml and findbugs-warnings.xml


        //Current findbugs warning action, same build reference space
        int index = indexOfFindBugsResultAction();
        if (index != -1) {
            FindBugsResultAction fbAction = (FindBugsResultAction) build.getAllActions().get(index);
            FindBugsResult fbResult = fbAction.getResult();


            //Let's start experimentation
            //If marked as tracking, don't remove. Else if false positive then remove.


            //Graph seems to update on next build, after modification
            //wonder if I should intercept the parser result and re-clone the BuildResult
            FileAnnotation fileAnnotation = null;
            for (FileAnnotation fa : fbResult.getAnnotations()) {
                fileAnnotation = fa;
            }
            fbResult.removeAnnotation(fileAnnotation);

        }
    }


    private int indexOfFindBugsResultAction(){
        for (Action action : this.build.getAllActions()) {
            if (action instanceof FindBugsResultAction) {
                return this.build.getAllActions().indexOf(action);
            }
        }
        return -1;
    }


    private void serialiseAuditFingerprints(){

    }

    private void loadAuditFingerprints(){

    }


    private XmlFile getAuditFingerprintsFile(){

        return ;
    }

    public Set<FileAnnotation> getUnconfirmedWarnings(){

    }

    public Set<FileAnnotation> getConfirmedWarnings(){

    }

    @Override
    public String getDisplayName() {
        return "FindBugs Auditing";
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }

    public int getBuildNumber(){
        return this.build.number;
    }

    public int getLastSuccessfulBuildNumber(){
        return build.getProject().getLastSuccessfulBuild().number;
    }

    // Is this current build the latest successful one?
    // Or has it been superceded
    public boolean isLatestSuccessfulBuild(){
        return (getBuildNumber() == getLastSuccessfulBuildNumber());
    }

    @JavaScriptMethod
    public void boundSystemLogger(String message){
        System.out.println(message);
    }


}
