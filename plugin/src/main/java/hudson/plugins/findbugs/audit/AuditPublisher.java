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
        listener.getLogger().println("Setuping Up Auditing Process...");

        // Adds to only the build itself
        build.addAction(new AuditAction(build));

        //Think this adds to root?
        Jenkins.getInstance().getActions().add(new AuditAction(build));

        return true;
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        //super.getProjectActions(project);
        List<Action> actions = new ArrayList<Action>();
        actions.add(new AuditAction(this.build));

        return actions;
    }

    @Override
    public Descriptor<Publisher> getDescriptor() {
        return (new DescriptorImpl());
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<Publisher> {
        @Override
        public String getDisplayName() {
            return "Audit Findbugs Results";
        }
    }
}