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

/**
 * Created by tudor on 5/8/2017.
 */


public class Role implements Serializable, Parcelable {

    public static final long MIN = 0;
    public static final int MAX = 2;
    public static final String[] roleTitles = {"User", "Admin", "Super"};
    public static final String BAD = "BAD";

    public static enum Names {
        USER,
        ADMIN,
        SUPER;
    }

    @SerializedName("role")
    @Expose
    private long role;
    @SerializedName("name")
    @Expose
    private String name;
    public final static Parcelable.Creator<Role> CREATOR = new Creator<Role>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Role createFromParcel(Parcel in) {
            Role instance = new Role();
            instance.role = ((long) in.readValue((long.class.getClassLoader())));
            instance.name = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Role[] newArray(int size) {
            return (new Role[size]);
        }

    };
    private final static long serialVersionUID = -6578920962165982499L;

    /**
     * No args constructor for use in serialization
     */
    public Role() {
    }

    /**
     * @param name
     * @param role
     */
    public Role(long role, String name) {
        super();
        this.role = role;
        this.name = name;
    }

    public long getRole() {
        return role;
    }

    public void setRole(long role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(role).append(name).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Role) == false) {
            return false;
        }
        Role rhs = ((Role) other);
        return new EqualsBuilder().append(role, rhs.role).append(name, rhs.name).isEquals();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(role);
        dest.writeValue(name);
    }

    public int describeContents() {
        return 0;
    }

}