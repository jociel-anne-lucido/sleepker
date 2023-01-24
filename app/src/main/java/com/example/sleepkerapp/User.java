package com.example.sleepkerapp;

public class User {

    public String Fullname, Age, Gender, EmailId, Password;

    public User(){

    }

    public User(String fullname, String age, String gender, String emailId, String pass) {
        this.Fullname = fullname;
        this.Age = age;
        this.Gender = gender;
        this.EmailId = emailId;
        this.Password = pass;
    }
    public String getFullname() {
        return Fullname;
    }

    public void setFullname(String Fullname) {
        this.Fullname = Fullname;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String Age) {
        this.Age = Age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String EmailId) {
        this.EmailId = EmailId;
    }

    public String getPassword() { return Password; }

    public void setPassword(String Password) {
        this.Password = Password;
    }


}
