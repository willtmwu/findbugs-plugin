package hudson.plugins.findbugs.audit;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import org.kohsuke.stapler.StaplerProxy;

/**
 * Created by William on 9/07/2015.
 */
public class AuditAction implements Action, StaplerProxy {
    private AbstractBuild<?,?> build;
    private FindBugsAudit auditView;

    public AuditAction(){
        this.build = null;
    }

    public AuditAction(AbstractBuild<?,?> build){
        this.build = build;
        this.auditView = new FindBugsAudit(build);
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
        return "findbugsAudit";
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }

    @Override
    public final Object getTarget() {
        return this.auditView;
    }
}
