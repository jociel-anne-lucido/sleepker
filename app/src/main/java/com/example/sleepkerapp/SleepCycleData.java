package com.example.sleepkerapp;

public class SleepCycleData {

    private String lastRecorded, newRecorded, totalDur, moodQual, sleepTime, wakeTime, dateRecorded, sleepQual;
    private String pushId;

    public SleepCycleData() {

    }

    public SleepCycleData(String lastRecorded, String newRecorded, String totalDur, String moodQual, String sleepTime, String wakeTime, String dateRecorded, String sleepQual) {
        this.lastRecorded = lastRecorded;
        this.newRecorded = newRecorded;
        this.totalDur = totalDur;
        this.moodQual = moodQual;
        this.sleepTime = sleepTime;
        this.wakeTime = wakeTime;
        this.dateRecorded = dateRecorded;
        this.sleepQual = sleepQual;
    }

    public String getLastRecorded() {
        return lastRecorded;
    }

    public void setLastRecorded(String lastRecorded) {
        this.lastRecorded = lastRecorded;
    }

    public String getNewRecorded() {
        return newRecorded;
    }

    public void setNewRecorded(String newRecorded) {
        this.newRecorded = newRecorded;
    }

    public String getTotalDur() {
        return totalDur;
    }

    public void setTotalDur(String totalDur) {
        this.totalDur = totalDur;
    }

    public String getMoodQual() {
        return moodQual;
    }

    public void setMoodQual(String moodQual) {
        this.moodQual = moodQual;
    }

    public String getSleepTime() { return sleepTime; }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getWakeTime() {
        return wakeTime;
    }

    public void setWakeTime(String wakeTime) {
        this.wakeTime = wakeTime;
    }

    public String getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(String dateRecorded) {
        this.dateRecorded = dateRecorded;
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
