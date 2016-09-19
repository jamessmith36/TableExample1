package com.tae.james.tableexample1.cache;

import android.graphics.Bitmap;

/**
 * Created by jamessmith on 15/09/2016.
 */
public class DatabaseModel {

    private String forename;
    private String surname;
    private String role;
    private String dob;
    private Bitmap image;

    public DatabaseModel() {

    }

    public DatabaseModel(String forename, String surname, String role, String dob, Bitmap image) {
        this.forename = forename;
        this.surname = surname;
        this.role = role;
        this.dob = dob;
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
