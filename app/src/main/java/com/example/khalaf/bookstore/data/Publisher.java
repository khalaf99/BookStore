package com.example.khalaf.bookstore.data;

import java.io.Serializable;

/**
 * Created by KHALAF on 10/20/2017.
 */

public class Publisher implements Serializable{
    private String email;
    private String name;
    private String id;
    private String imageuri;
    private String address;
    private String password;

    public Publisher(String email,  String id ) {
        this.email = email;
        this.id = id;

    }

    // default constrauctor
    public Publisher() {

    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageuri() {
        return imageuri;
    }

    public void setImageuri(String imageuri) {
        this.imageuri = imageuri;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
