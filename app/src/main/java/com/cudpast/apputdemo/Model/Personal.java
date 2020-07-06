package com.cudpast.apputdemo.Model;

public class Personal {
    private String dni, name, last, age, address, born, date, phone1, phone2;


    public Personal() {

    }

    public Personal(String dni, String name, String last, String age, String address, String born, String date, String phone1, String phone2) {
        this.dni = dni;
        this.name = name;
        this.last = last;
        this.age = age;
        this.address = address;
        this.born = born;
        this.date = date;
        this.phone1 = phone1;
        this.phone2 = phone2;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBorn() {
        return born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
