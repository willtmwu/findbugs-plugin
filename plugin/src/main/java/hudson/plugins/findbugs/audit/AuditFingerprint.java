package hudson.plugins.findbugs.audit;

import hudson.plugins.analysis.util.model.FileAnnotation;

import java.io.Serializable;

/**
 * Currently reserved for future use
 *
 * @author William Wu
 */
public class AuditFingerprint<T extends FileAnnotation> implements Comparable<AuditFingerprint>, Serializable{


    private T annotation; // yep....
    private boolean falsePositive;
    private boolean trackedInCloud;
    private String trackingUrl; // Bugzilla tracking
    private Object fingerprint; // Unique fingerprint depending on what it will be ... might need to be abstract and
    //let the implementer decided on the outcome.

    public AuditFingerprint(T annotation){
        this.annotation = annotation;
    }

    public T getAnnotation(){
        return this.annotation;
    }

    @Override
    public int compareTo(AuditFingerprint other) {
        return other.getAnnotation().compareTo(this.annotation);
    }
}
