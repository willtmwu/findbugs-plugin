package hudson.plugins.findbugs.model;

import java.util.Collection;

/**
 * Annotates a collection of line ranges in a file. An annotation consists of a
 * description and a tooltip.
 *
 * @author Ulli Hafner
 */
public interface FileAnnotation {
    /**
     * Returns the message of this annotation.
     *
     * @return the message of this annotation
     */
    String getMessage();

    /**
     * Returns the a detailed description that will be used as tooltip.
     *
     * @return the tooltip of this annotation
     */
    String getToolTip();

    /**
     * Returns a collection of line ranges for this annotation.
     *
     * @return the collection of line ranges for this annotation.
     */
    Collection<LineRange> getLineRanges();

    /**
     * Returns the unique key of this annotation.
     *
     * @return the unique key of this annotation.
     */
    long getKey();

    /**
     * Returns the priority of this annotation.
     *
     * @return the priority of this annotation
     */
    Priority getPriority();

    /**
     * Returns the workspace file that contains this annotation.
     *
     * @return the workspace file that contains this annotation
     */
    WorkspaceFile getWorkspaceFile();

    /**
     * Sets the workspace file that contains this annotation.
     *
     * @param workspaceFile
     *            the workspace file that contains this annotation
     */
    void setWorkspaceFile(WorkspaceFile workspaceFile);
}