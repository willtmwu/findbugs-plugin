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
    private boolean confirmedWarning = false;
    private boolean falsePositive = false;
    private boolean trackedInCloud = false;
    private String trackingUrl; // Bugzilla tracking

    private T2 fingerprint; // Unique fingerprint depending on what it will be ... might need to be abstract and
    //let the implementer decided on the outcome.

    public AuditFingerprint(T1 annotation){
        this.annotation = annotation;
    }

    public T1 getAnnotation(){
        return this.annotation;
    }

    public boolean isConfirmedWarning(){
        return this.confirmedWarning;
    }

    public void setConfirmedWarning(boolean val){
        this.confirmedWarning = val;
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
        this.trackingUrl = url;
        setTrackedInCloud(true);
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
