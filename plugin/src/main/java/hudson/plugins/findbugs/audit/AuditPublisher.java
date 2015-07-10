package hudson.plugins.findbugs.audit;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Created by William on 9/07/2015.
 */

public class AuditPublisher extends Publisher {

    @DataBoundConstructor
    public AuditPublisher() {
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().print("HAHAHAHAHA!!!!");

        build.addAction(new AuditAction(build));
        //Jenkins.getInstance().getActions().add(new AuditAction(build));
        return true;
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