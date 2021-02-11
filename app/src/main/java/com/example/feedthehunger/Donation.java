package com.example.feedthehunger;

public class Donation{
    public String donationid;
    public String type;
    public String quantity;
    public String description;
    public String address;
    public String expirydate;
    public double latitude;
    public double longitude;
    public String donorid;
    public int status;
    public  String doneeid;
    public Donation(){};

    public Donation(String donatonid, String type, String quantity, String description, String address, String expirydate, double latitude, double longitude, String donorid, int status, String doneeid) {
        this.donationid = donatonid;
        this.type = type;
        this.quantity = quantity;
        this.description = description;
        this.address = address;
        this.expirydate = expirydate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.donorid = donorid;
        this.status = status;
        this.doneeid = doneeid;
    }
}
