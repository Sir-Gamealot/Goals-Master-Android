package com.goalsmaster.goalsmaster.data;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Goal implements Serializable, Parcelable {

    private static final String TAG = Goal.class.getSimpleName();

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("timestamp")
    @Expose
    private long timestamp;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("photoId")
    @Expose
    private String photoId;


    private final static long serialVersionUID = 1936016512126908651L;

public final static Parcelable.Creator<Goal> CREATOR = new Creator<Goal>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Goal createFromParcel(Parcel in) {
            Goal instance = new Goal();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            instance.userId = ((String) in.readValue((String.class.getClassLoader())));
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.timestamp = ((long) in.readValue((long.class.getClassLoader())));
            instance.priority = ((String) in.readValue((String.class.getClassLoader())));
            instance.photoId = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Goal[] newArray(int size) {
            return (new Goal[size]);
        }

    };

    /**
     * No args constructor for use in serialization
     */
    public Goal() {
    }

    /**
     * @param id
     * @param title
     * @param priority
     * @param description
     * @param userId
     * @param timestamp
     * @param photoId
     */
    public Goal(String id, String userId, String title, String description, long timestamp, String priority, String photoId) {
        super();
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.priority = priority;
        this.photoId = photoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                //.append(userId)
                //.append(title)
                //.append(description)
                //.append(timestamp)
                //.append(priority)
                //.append(photoId)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Goal) == false) {
            return false;
        }
        Goal rhs = ((Goal) other);
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(userId, rhs.userId)
                .append(title, rhs.title)
                .append(description, rhs.description)
                .append(timestamp, rhs.timestamp)
                .append(priority, rhs.priority)
                .append(photoId, rhs.photoId)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(timestamp);
        dest.writeString(priority);
        dest.writeString(photoId);
    }

    public int describeContents() {
        return 0;
    }

}