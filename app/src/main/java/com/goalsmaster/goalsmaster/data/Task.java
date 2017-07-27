package com.goalsmaster.goalsmaster.data;

import java.io.Serializable;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Task implements Serializable, Parcelable {

    private static final String TAG = Task.class.getSimpleName();

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private Date date;
    @SerializedName("duration")
    @Expose
    private long duration;
    @SerializedName("userId")
    @Expose
    private long userId;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;

    private final static long serialVersionUID = -6799738948104495261L;

    public final static Parcelable.Creator<Task> CREATOR = new Creator<Task>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Task createFromParcel(Parcel in) {
            Task instance = new Task();
            instance.id = ((long) in.readValue((long.class.getClassLoader())));
            instance.userId = ((long) in.readValue((long.class.getClassLoader())));
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.date = ((Date) in.readValue((Date.class.getClassLoader())));
            instance.duration = ((long) in.readValue((long.class.getClassLoader())));
            instance.latitude = ((double) in.readValue((double.class.getClassLoader())));
            instance.longitude = ((double) in.readValue((double.class.getClassLoader())));
            return instance;
        }

        public Task[] newArray(int size) {
            return (new Task[size]);
        }
    };

    /**
     * No args constructor for use in serialization
     *
     */
    public Task() {
    }

    /**
     *
     * @param id
     * @param userId
     * @param title
     * @param description
     * @param duration
     * @param date
     * @param latitude
     * @param longitude
     */
    public Task(long id, long userId, String title, String description, Date date, long duration, double latitude, double longitude) {
        super();
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.duration = duration;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        /*HashCodeBuilder hashCodeBuilder = new HashCodeBuilder()
                    .append(id)
                    .append(title)
                    .append(notes)
                    .append(date)
                    .append(duration)
                    .append(userId)
                    .append(distance)
                    .append(latitude)
                    .append(longitude);

        int hashCode = hashCodeBuilder.toHashCode();
        Log.d(TAG, String.format("Task( [id=%d] -> hash[%d] );", getId(), Long.valueOf(hashCode)));
        return hashCode;*/
        return new HashCodeBuilder()
                .append(id)
                //.append(userId)
                //.append(title)
                //.append(description)
                //.append(date)
                //.append(duration)
                //.append(latitude)
                //.append(longitude)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Task) == false) {
            return false;
        }
        Task rhs = ((Task) other);
        return new EqualsBuilder()
                    .append(id, rhs.id)
                    .append(userId, rhs.userId)
                    .append(title, rhs.title)
                    .append(description, rhs.description)
                    .append(date, rhs.date)
                    .append(duration, rhs.duration)
                    .append(latitude, rhs.latitude)
                    .append(longitude, rhs.longitude)
                    .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(userId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeValue(date);
        dest.writeValue(duration);
        dest.writeValue(latitude);
        dest.writeValue(longitude);
    }

    public int describeContents() {
        return 0;
    }

    public boolean hasLocation() {
        return (Math.abs(getLatitude()) > 0.0) && (Math.abs(getLongitude()) > 0.0);
    }
}