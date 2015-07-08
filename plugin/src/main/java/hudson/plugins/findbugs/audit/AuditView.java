package hudson.plugins.findbugs.audit;

import hudson.model.ModelObject;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * A project action displays a link on the side panel of a project.
 *
 * @param
 *
 * @author William Wu
 */
@ExportedBean
public class AuditView implements ModelObject{


    @Override
    public String getDisplayName() {
        return "Auditing Test";
    }
}
