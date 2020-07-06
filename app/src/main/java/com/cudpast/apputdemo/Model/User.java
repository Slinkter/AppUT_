package com.cudpast.apputdemo.Model;

public class User {
    private String uid;
    private String email;
    private String password;
    private String name;
    private String dni;
    private String phone;
    private Boolean status;
    private int numUT;


    public User() {
    }



    public User(String uid, String email, String password, String name, String dni, String phone, Boolean status, int numUT) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.name = name;
        this.dni = dni;
        this.phone = phone;
        this.status = status;
        this.numUT = numUT;
    }

    public int getNumUT() {
        return numUT;
    }

    public void setNumUT(int numUT) {
        this.numUT = numUT;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
