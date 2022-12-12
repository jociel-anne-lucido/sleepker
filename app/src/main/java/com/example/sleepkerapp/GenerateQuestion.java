package com.example.sleepkerapp;

public class GenerateQuestion {

    public static String questions [] = {
            "What time do you usually go to sleep?",
            "How long does it take you to fall into a deep sleep?",
            "In a week, how many times do you feel you have trouble sleeping?",
            "On average, how many hours do you sleep soundly?",
            "Can you define how your sleeping quality is?"
    };

    public static String choices [][] = {
            {"Between 8 PM to 9 PM", "Between 10 PM to 11 PM", "Between 11 PM and midnight",
                    "Depends if school nights or weekends", "Depends on my mood"},
            {"0 to 15 minutes", "16 to 30 minutes", "31 to 45 minutes ", "46 to 60 minutes", "More than 61 minutes"},
            {"0 to 1 night", "2 nights", "3 nights", "4 nights", "5 to 7 nights"},
            {"0 to 4 hours", "5 to 6 hours", "7 to 8 hours", "9 to 10 hours", "11 hours and above"},
            {"Relaxing", "Good", "So-so", "Bad", "Worst"}
    };

    public String getQuestion(int a) {
        String question = questions[a];
        return  question;
    }

    public String getChoice1(int a) {
        String choice0 = choices[a][0];
        return choice0;
    }

    public String getChoice2(int a) {
        String choice1 = choices[a][1];
        return choice1;
    }

    public String getChoice3(int a) {
        String choice2 = choices[a][2];
        return choice2;
    }

    public String getChoice4(int a) {
        String choice3 = choices[a][3];
        return choice3;
    }

    public String getChoice5(int a) {
        String choice4 = choices[a][4];
        return choice4;
    }

}
