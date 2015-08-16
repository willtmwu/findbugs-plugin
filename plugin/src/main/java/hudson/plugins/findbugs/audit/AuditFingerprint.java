package hudson.plugins.findbugs.audit;

import hudson.plugins.analysis.util.model.FileAnnotation;

import java.io.Serializable;

/**
 * Wrapper for FileAnnotation, to provide future functionality for referencing warnings in the cloud
 * and tracking the warning by byte code
 *
 * @author William Wu
 */
public class AuditFingerprint<T1 extends FileAnnotation, T2 extends Serializable> implements Comparable<AuditFingerprint>, Serializable{

    private T1 annotation;
    private boolean falsePositive = false;  // If an audit fingerprint become FP true, it should be automatically tracked to a FP cloud
    private boolean trackedAsIssue = false; // This tracking refers to Bugzilla or any other form of issue tracker
    private String trackingUrl = "";        // Bugzilla or other system tracking url

    private T2 fingerprint;
    // Unique fingerprint may need to merge byte code and other information such as line number or actual source code
    // let the implementer decided on the outcome.
    // Will need to be serialisable as all auditfingerprint are serialised and stored in a file

    public AuditFingerprint(T1 annotation){
        this.annotation = annotation;
    }

    public T1 getAnnotation(){
        return this.annotation;
    }

    public boolean isTrackedInCloud(){
        return this.trackedAsIssue;
    }

    public void setTrackedInCloud(boolean val){
        this.trackedAsIssue = val;
    }

    public String getTrackingUrl(){
        return this.trackingUrl;
    }

    public void setTrackingUrl(String url){
        if (url != null && !("").equals(url)) {
            this.trackingUrl = url;
            this.trackedAsIssue = true;
        }
    }

    public boolean isFalsePositive(){
        return this.falsePositive;
    }

    public void setFalsePositive(boolean val){
        this.falsePositive = val;
    }

    @Override
    public int compareTo(AuditFingerprint other) {
        return other.getAnnotation().compareTo(this.annotation);
    }

}
