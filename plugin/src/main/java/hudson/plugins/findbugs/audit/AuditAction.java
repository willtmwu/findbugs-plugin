package hudson.plugins.findbugs.audit;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.plugins.analysis.util.model.FileAnnotation;
import org.kohsuke.stapler.StaplerProxy;

import java.util.Collection;

/**
 * The action added to the side of a job page. Actual functionality of auditing and saving state is performed
 * by the underlying {@link FindBugsAudit}. Delegation is performed through the StaplerProxy
 *
 * @author William Wu
 */
public class AuditAction implements Action, StaplerProxy {
    private AbstractBuild<?,?> build;
    private FindBugsAudit auditView;

    public AuditAction(){
        this.build = null;
    }

    public AuditAction(AbstractBuild<?,?> build, Collection<FileAnnotation> deltaNumberOfAnnotationsDuringFiltering){
        this.build = build;
        this.auditView = new FindBugsAudit(build, deltaNumberOfAnnotationsDuringFiltering);
    }

    public FindBugsAudit getAuditView(){
        this.auditView.loadClassData();
        return this.auditView;
    }

    public AbstractBuild<?, ?> getBuild() {
        return this.build;
    }

    @Override
    public final Object getTarget() {
        this.auditView.loadClassData();
        return this.auditView;
    }

    @Override
    public String getIconFileName() {
        return "/plugin/findbugs/icons/findbugs-24x24.png";
    }

    @Override
    public String getDisplayName() {
        return "FindBugs Auditing";
    }

    @Override
    public String getUrlName() {
        return "findbugsAudit";
    }
}
