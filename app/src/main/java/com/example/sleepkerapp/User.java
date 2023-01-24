package com.example.sleepkerapp;

public class User {

    public String Fullname, Age, Gender, EmailId, Password;

    public User(){

    }

    public User(String fullname, String age, String gender, String emailId, String pass) {
        Fullname = fullname;
        Age = age;
        Gender = gender;
        EmailId = emailId;
        Password = pass;
    }

}