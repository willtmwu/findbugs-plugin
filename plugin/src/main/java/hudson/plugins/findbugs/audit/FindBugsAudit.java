package hudson.plugins.findbugs.audit;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.ModelObject;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.findbugs.FindBugsResult;
import hudson.plugins.findbugs.FindBugsResultAction;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.io.Serializable;

/**
 * Created by William on 10/07/2015.
 */
public class FindBugsAudit implements ModelObject, Serializable{

    private AbstractBuild<?,?> build;
    private final AbstractProject<?,?> project;
    private int TEST_SERIALISATION_INTEGER = 0;

    public FindBugsAudit(AbstractBuild<?,?> build){
        this.build = build;
        this.project = build.getProject();
        TEST_SERIALISATION_INTEGER = 10;
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







    @Override
    public String getDisplayName() {
        return "Findbugs Auditing";
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }

    public int getNumber(){
        return this.build.number;
    }

    public int getLastSuccessfulBuildNumber(){
        return build.getProject().getLastSuccessfulBuild().number;
    }

    @JavaScriptMethod
    public void boundLogger(String message){
        System.out.println(message);
        TEST_SERIALISATION_INTEGER--;
    }


}
