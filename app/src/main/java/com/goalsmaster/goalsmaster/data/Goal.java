package com.goalsmaster.goalsmaster.data;

import java.io.Serializable;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Goal implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("user_id")
    @Expose
    private long userId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private Date date;
    @SerializedName("priority")
    @Expose
    private String priority;
    @SerializedName("photo")
    @Expose
    private String photo;
    public final static Parcelable.Creator<Goal> CREATOR = new Creator<Goal>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Goal createFromParcel(Parcel in) {
            Goal instance = new Goal();
            instance.id = ((long) in.readValue((long.class.getClassLoader())));
            instance.userId = ((long) in.readValue((long.class.getClassLoader())));
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            instance.date = ((Date) in.readValue((Date.class.getClassLoader())));
            instance.priority = ((String) in.readValue((String.class.getClassLoader())));
            instance.photo = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Goal[] newArray(int size) {
            return (new Goal[size]);
        }

    };
    private final static long serialVersionUID = 1936016512126908651L;

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
     * @param date
     * @param photo
     */
    public Goal(long id, long userId, String title, String description, Date date, String priority, String photo) {
        super();
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.priority = priority;
        this.photo = photo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Goal withId(long id) {
        this.id = id;
        return this;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Goal withUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Goal withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Goal withDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Goal withDate(Date date) {
        this.date = date;
        return this;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Goal withPriority(String priority) {
        this.priority = priority;
        return this;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Goal withPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(userId)
                .append(title)
                .append(description)
                .append(date)
                .append(priority)
                .append(photo)
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
                .append(date, rhs.date)
                .append(priority, rhs.priority)
                .append(photo, rhs.photo)
                .isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(userId);
        dest.writeValue(title);
        dest.writeValue(description);
        dest.writeValue(date);
        dest.writeValue(priority);
        dest.writeValue(photo);
    }

    public int describeContents() {
        return 0;
    }

}