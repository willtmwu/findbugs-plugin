package hudson.plugins.findbugs.audit;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.plugins.analysis.core.AbstractResultAction;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.security.AccessControlled;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Builder;
import hudson.tasks.Publisher;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Entry point to filter out False Positives generated by {@link hudson.plugins.analysis.core.BuildResult}
 * The {@link AuditAction} will be added to a successful build page and will be delegated the task of auditing
 * results in a new view.
 *
 * @author William Wu
 */

public class AuditPublisher extends Publisher{

    private AbstractBuild<?,?> build;

    @DataBoundConstructor
    public AuditPublisher() {
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        if (build == null) {
            return true;
        }

        this.build = build;
        listener.getLogger().println("[FINDBUGS_AUDIT] Setting Up Auditing Process, filtering out previous false positives");

        //Filter based on false positives from the previous audit
        BuildResult currentBuildResult = getCurrentBuildResult();
        FindBugsAudit previousAudit = getPreviousAudit();
        Collection<FileAnnotation> deltaNumberOfAnnotationsDuringFiltering = new ArrayList<FileAnnotation>();
        if (currentBuildResult != null && previousAudit != null) {
            List<FileAnnotation> falsePositiveAnnotations = new ArrayList<FileAnnotation>();
            for (AuditFingerprint auditFingerprint : getPreviousAudit().getFalsePositiveWarnings()) {
                falsePositiveAnnotations.add(auditFingerprint.getAnnotation());
            }
            deltaNumberOfAnnotationsDuringFiltering = currentBuildResult.removeAnnotations(falsePositiveAnnotations);
        }

        // Add a new audit action
        build.addAction(new AuditAction(build, deltaNumberOfAnnotationsDuringFiltering));

        return true;
    }

    private FindBugsAudit getPreviousAudit(){
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

    private BuildResult getCurrentBuildResult(){
        List<? extends Action> currentActions = this.build.getAllActions();
        for( Action action : currentActions ) {
            if (action instanceof AbstractResultAction) {
                return ((AbstractResultAction) action).getResult();
            }
        }
        return null;
    }

    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return (new Action() {
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
                AbstractBuild<?,?> lastBuild = project.getLastSuccessfulBuild();
                if (lastBuild != null) {
                    return lastBuild.number + "/findbugsAudit";
                }
                return "";
            }
        });
    }

    @Override
    public Descriptor<Publisher> getDescriptor() {
        return (new DescriptorImpl());
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Publisher> {
        @Override
        public String getDisplayName() {
            return "Configure Findbugs Auditing";
        }
    }
}