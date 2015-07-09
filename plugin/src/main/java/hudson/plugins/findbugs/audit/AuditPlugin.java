package hudson.plugins.findbugs.audit;

import hudson.FilePath;
import hudson.Plugin;
import hudson.model.Descriptor.FormException;
import hudson.model.Hudson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Created by William on 9/07/2015.
 */
public class AuditPlugin {

    private AuditAction auditAction = new AuditAction();

    /*@Override public void start() throws Exception {
        load();
        Jenkins.getInstance().getActions().add(auditAction);
    }

    @Override
    public void configure(StaplerRequest req, JSONObject formData) throws IOException, ServletException, FormException {
        save();
    }*/

    /**
     * Receive file upload from startUpload.jelly.
     * File is placed in $JENKINS_HOME/userContent directory.
     */
    /*public void doUpload(StaplerRequest req, StaplerResponse rsp)
            throws IOException, ServletException, InterruptedException {
        Hudson hudson = Hudson.getInstance();
        hudson.checkPermission(Hudson.ADMINISTER);
        FileItem file = req.getFileItem("linkimage.file");
        String error = null, filename = null;
        if (file == null || file.getName().isEmpty())
            error = Messages.NoFile();
        else {
            filename = "userContent/"*/
                    // Sanitize given filename:
         //           + file.getName().replaceFirst(".*/", "").replaceAll("[^\\w.,;:()#@!=+-]", "_");
       /*     FilePath imageFile = hudson.getRootPath().child(filename);
            if (imageFile.exists())
                error = Messages.DupName();
            else {
                imageFile.copyFrom(file.getInputStream());
                imageFile.chmod(0644);
            }
        }
        rsp.setContentType("text/html");
        rsp.getWriter().println(
                (error != null ? error : Messages.Uploaded("<tt>/" + filename + "</tt>"))
                        + " <a href=\"javascript:history.back()\">" + Messages.Back() + "</a>");
    }*/


}
