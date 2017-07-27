package com.goalsmaster.goalsmaster.data;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Id implements Serializable, Parcelable {

    @SerializedName("id")
    @Expose
    private long id;
    public final static Parcelable.Creator<Id> CREATOR = new Creator<Id>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Id createFromParcel(Parcel in) {
            Id instance = new Id();
            instance.id = ((long) in.readValue((long.class.getClassLoader())));
            return instance;
        }

        public Id[] newArray(int size) {
            return (new Id[size]);
        }

    };
    private final static long serialVersionUID = -5057762126753912965L;

    /**
     * No args constructor for use in serialization
     */
    public Id() {
    }

    /**
     * @param id
     */
    public Id(long id) {
        super();
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Id) == false) {
            return false;
        }
        Id rhs = ((Id) other);
        return new EqualsBuilder().append(id, rhs.id).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
    }

    public int describeContents() {
        return 0;
    }
}