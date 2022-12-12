package com.example.sleepkerapp;

public class AssessmentData {

    public String bedTime, deepSleep, troubleSleep, totalSleep, qualitySleep;
    private String pushId;

    public AssessmentData() {

    }

    public AssessmentData(String bedTime, String deepSleep, String troubleSleep, String totalSleep, String qualitySleep) {
        this.bedTime = bedTime;
        this.deepSleep = deepSleep;
        this.troubleSleep = troubleSleep;
        this.totalSleep = totalSleep;
        this.qualitySleep = qualitySleep;
    }

    public String getBedTime() {
        return bedTime;
    }

    public void setBedTime(String bedTime) {
        this.bedTime = bedTime;
    }

    public String getDeepSleep() {
        return deepSleep;
    }

    public void setDeepSleep(String deepSleep) {
        this.deepSleep = deepSleep;
    }

    public String getTroubleSleep() {
        return troubleSleep;
    }

    public void setTroubleSleep(String troubleSleep) {
        this.troubleSleep = troubleSleep;
    }

    public String getTotalSleep() {
        return totalSleep;
    }

    public void setTotalSleep(String totalSleep) {
        this.totalSleep = totalSleep;
    }

    public String getQualitySleep() {
        return qualitySleep;
    }

    public void setQualitySleep(String qualitySleep) {
        this.qualitySleep = qualitySleep;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }
}
