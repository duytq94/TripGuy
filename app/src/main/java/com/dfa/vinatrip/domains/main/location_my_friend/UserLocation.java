package com.dfa.vinatrip.domains.main.location_my_friend;

import com.google.gson.annotations.SerializedName;

public class UserLocation {

    @SerializedName("from_user")
    private String fromUser;
    @SerializedName("to_group")
    private String toGroup;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;

    private String avatar = "";

    public UserLocation(String fromUser, String toGroup, double latitude, double longitude, String avatar) {
        this.fromUser = fromUser;
        this.toGroup = toGroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToGroup() {
        return toGroup;
    }

    public void setToGroup(String toGroup) {
        this.toGroup = toGroup;
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
}
