package hudson.plugins.findbugs.audit;

import hudson.model.AbstractBuild;
import hudson.model.ModelObject;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import java.io.Serializable;

/**
 * Created by William on 10/07/2015.
 */
public class FindBugsAudit implements ModelObject, Serializable{

    private AbstractBuild<?,?> build;

    public FindBugsAudit(AbstractBuild<?,?> build){
        this.build = build;
    }


    //Need to test action/buildresult manipulation and filtering. Both here and back in publisher
    // Here for the view
    // in publisher for initial filtering











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

    @JavaScriptMethod
    public void bindingLogger(String message){
        System.out.println(message);
    }
}
