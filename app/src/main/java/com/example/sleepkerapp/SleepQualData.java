package com.example.sleepkerapp;

public class SleepQualData {

    private String sleepQual;
    private String pushId;

    public SleepQualData() {

    }

    public SleepQualData(String  sleepQual) {

        this.sleepQual = sleepQual;
    }



    public String getSleepQual() {
        return sleepQual;
    }

    public void setSleepQual(String sleepQual) {
        this.sleepQual = sleepQual;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
