package hudson.plugins.findbugs.audit;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
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
 * Created by William on 9/07/2015.
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
        this.build = build;
        listener.getLogger().println("[FINDBUGS_AUDIT] Setting Up Auditing Process...");

        // Adds to only the build itself
        build.addAction(new AuditAction(build));

        //This adds to root.... don't need it
        //Jenkins.getInstance().getActions().add(new AuditAction(build));

        // Look at previous action and check if this build has findbugs-warnings.xml
        // Override and force recalculation of current based on previous

        return true;
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
                return "Findbugs Auditing";
            }

            @Override
            public String getUrlName() {
                return project.getLastSuccessfulBuild().number + "/findbugsAudit";
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