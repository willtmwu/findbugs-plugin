package hudson.plugins.findbugs.audit;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.ModelObject;
import hudson.model.RootAction;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * A project action displays a link on the side panel of a project.
 *
 * @param
 *
 * @author William Wu
 */
@Extension
public class AbstractAuditAction implements Action {

    @Override
    public String getIconFileName() {
        return "testAudit";
    }

    @Override @Exported
    public String getDisplayName() {
        return "testAudit";
    }

    @Override
    public String getUrlName() {
        return "testAudit";
    }

    public String getCloudUrl(){
        return "testAudit";
    }
}
