package hudson.plugins.findbugs;

import org.apache.commons.lang.StringUtils;

// CHECKSTYLE:OFF
public class Warning {
    private String type;
    private String category;
    private String priority;
    private String message;
    private String lineNumber;
    private String classname;
    private JavaClass javaClass;

    /**
     * Returns the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    public void linkClass(final JavaClass owningClass) {
        if (!owningClass.isRoleClass()) {
            javaClass = owningClass;
            classname = owningClass.getClassname();
        }
    }

    /**
     * Returns the javaClass.
     *
     * @return the javaClass
     */
    public JavaClass getJavaClass() {
        return javaClass;
    }

    /**
     * Returns the bug pattern description.
     *
     * @return the bug pattern description.
     */
    public String getDescription() {
        return FindBugsMessages.getInstance().getMessage(getType());
    }

    /**
     * Sets the type to the specified value.
     *
     * @param type the value to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * Returns the category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category to the specified value.
     *
     * @param category the value to set
     */
    public void setCategory(final String category) {
        this.category = category;
    }

    /**
     * Sets the priority to the specified value.
     *
     * @param priority the value to set
     */
    public void setPriority(final String priority) {
        this.priority = priority;
    }

    /**
     * Returns the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message to the specified value.
     *
     * @param message the value to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Returns the lineNumer.
     *
     * @return the lineNumer
     */
    public String getLineNumber() {
        return StringUtils.defaultIfEmpty(lineNumber, "Not available");
    }

    /**
     * Sets the lineNumer to the specified value.
     *
     * @param lineNumber the value to set
     */
    public void setLineNumber(final String lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the priority.
     *
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the classname.
     *
     * @param classname the new classname
     */
    public void setClassname(final String classname) {
        this.classname = classname;
    }

    /**
     * Returns the classname.
     *
     * @return the classname
     */
    public String getClassname() {
        return classname;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classname == null) ? 0 : classname.hashCode());
        result = prime * result + ((lineNumber == null) ? 0 : lineNumber.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Warning other = (Warning)obj;
        if (classname == null) {
            if (other.classname != null) {
                return false;
            }
        }
        else if (!classname.equals(other.classname)) {
            return false;
        }
        if (lineNumber == null) {
            if (other.lineNumber != null) {
                return false;
            }
        }
        else if (!lineNumber.equals(other.lineNumber)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        }
        else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
