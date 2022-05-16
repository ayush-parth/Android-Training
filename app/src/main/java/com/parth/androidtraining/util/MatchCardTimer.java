package com.parth.androidtraining.util;

import android.app.Application;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MatchCardTimer extends Application {
    Timer timer;

    public MatchCardTimer(int seconds, TextView timerView) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(seconds,timerView),0, 1000L);
    }

    class RemindTask extends TimerTask {
        private long secondsLeft;
        private TextView timerView;

        public RemindTask(long secondsLeft, TextView timerView) {
            this.secondsLeft = secondsLeft;
            this.timerView = timerView;
        }

        @Override
        public void run() {
            timerView.setText(getDurationString(secondsLeft));
            secondsLeft--;
            if (secondsLeft < 0) {
                timerView.setText("DONE!");
                timer.cancel(); //Terminate the timer thread
            }
        }
    }

    private String getDurationString(long seconds) {
        int hours = (int) (seconds / 3600);
        int minutes = (int) ((seconds % 3600) / 60);
        seconds = seconds % 60;

        if(hours > 0){
            return twoDigitString(hours) + "h : " + twoDigitString(minutes) + "m";
        }else{
            return twoDigitString(minutes) + "m : " + twoDigitString((int) seconds)+"s";
        }
    }

    private String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
