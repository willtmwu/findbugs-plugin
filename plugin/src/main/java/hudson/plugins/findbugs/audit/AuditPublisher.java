package hudson.plugins.findbugs.audit;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;

import java.io.IOException;

/**
 * Created by William on 9/07/2015.
 */
public class AuditPublisher extends Publisher {

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                           BuildListener listener) throws InterruptedException, IOException {

        //build.getActions().add(new AuditAction(build));
        build.addAction(new AuditAction(build));

        return true;
    }

}