package hudson.plugins.findbugs.audit;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.AbstractBuild;
import hudson.model.Action;

/**
 * Created by William on 9/07/2015.
 */
@Extension(ordinal = 100)
public class AuditAction extends Plugin implements Action{
    AbstractBuild<?,?> build;

    public AuditAction(){
    }

    public AuditAction(AbstractBuild<?,?> build){
        this.build = build;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Findbugs Auditing";
    }

    @Override
    public String getUrlName() {
        return "findbugs-audit";
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }
}
