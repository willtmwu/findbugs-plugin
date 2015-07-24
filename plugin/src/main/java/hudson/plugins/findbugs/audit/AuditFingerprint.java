package hudson.plugins.findbugs.audit;

import hudson.plugins.analysis.util.model.FileAnnotation;

import java.io.Serializable;

/**
 * Wrapper for FileAnnotation to provide compatibility
 *
 * @author William Wu
 */
public class AuditFingerprint<T1 extends FileAnnotation, T2 extends Serializable> implements Comparable<AuditFingerprint>, Serializable{


    private T1 annotation;
    private boolean falsePositive = false; // If an audit fingerprint become FP true, it should be automatically tracked to FP cloud
    private boolean trackedInCloud = false; // This tracking refers to Bugzilla not the general FP cloud, will be needed when something is not FP but is an actual issue
    private String trackingUrl = ""; // Bugzilla tracking

    private T2 fingerprint; // Unique fingerprint depending on what it will be ... might need to be abstract and
    //let the implementer decided on the outcome.
    // This fingerprint will later need to update the actual file annotation itself
    // Should if possible, merge both byte and source code

    public AuditFingerprint(T1 annotation){
        this.annotation = annotation;
    }

    public T1 getAnnotation(){
        return this.annotation;
    }

    public boolean isTrackedInCloud(){
        return this.trackedInCloud;
    }

    public void setTrackedInCloud(boolean val){
        this.trackedInCloud = val;
    }

    public String getTrackingUrl(){
        return this.trackingUrl;
    }

    public void setTrackingUrl(String url){
        if (url != null && !("").equals(url)) {
            this.trackingUrl = url;
            this.trackedInCloud = true;
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
