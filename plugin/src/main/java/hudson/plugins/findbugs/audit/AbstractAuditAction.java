package hudson.plugins.findbugs.audit;

import hudson.model.Action;
import hudson.model.ModelObject;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * A project action displays a link on the side panel of a project.
 *
 * @param
 *
 * @author William Wu
 */
@ExportedBean
public class AbstractAuditAction implements ModelObject {

    //@Override
    public String getIconFileName() {
        return "testAudit";
    }

    @Override @Exported
    public String getDisplayName() {
        return "testAudit";
    }

    //@Override
    public String getUrlName() {
        return "testAudit";
    }

    public String getCloudUrl(){
        return "testAudit";
    }
}
