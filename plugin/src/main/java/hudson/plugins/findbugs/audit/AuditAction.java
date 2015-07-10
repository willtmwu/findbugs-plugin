package hudson.plugins.findbugs.audit;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.Hudson;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Created by William on 9/07/2015.
 */
public class AuditAction implements Action{
    AbstractBuild<?,?> build;

    public AuditAction(){
        this.build = null;
    }

    public AuditAction(AbstractBuild<?,?> build){
        this.build = build;
    }

    @Override
    public String getIconFileName() {
        return "/plugin/findbugs/icons/findbugs-24x24.png";
    }

    @Override
    public String getDisplayName() {
        return "Findbugs Auditing";
    }

    @Override
    public String getUrlName() {
        return "findbugsResult";
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }
}
