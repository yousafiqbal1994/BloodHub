package com.giveblood.bloodhub.searchfeature;

public class DonorPerson{

    public String distance,ID,name,username,age,number,bloodgroup,latitude,longitude,gender,searchprivacy,mobileprivacy,internetprivacy;

    public DonorPerson(String distance,String ID, String name, String username, String age, String number, String bloodgroup, String latitude, String longitude, String gender, String searchprivacy, String mobileprivacy, String internetprivacy) {
        this.distance=distance;
        this.ID = ID;
        this.name = name;
        this.username = username;
        this.age = age;
        this.number = number;
        this.bloodgroup = bloodgroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gender = gender;
        this.searchprivacy = searchprivacy;
        this.mobileprivacy = mobileprivacy;
        this.internetprivacy = internetprivacy;
    }

    public String getDistance() {
        return distance;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getAge() {
        return age;
    }

    public String getNumber() {
        return number;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getGender() {
        return gender;
    }

    public String getSearchprivacy() {
        return searchprivacy;
    }

    public String getMobileprivacy() {
        return mobileprivacy;
    }

    public String getInternetprivacy() {
        return internetprivacy;
    }
}
