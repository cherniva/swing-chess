/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Game.LocalGame;

import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalTime;
import java.util.Calendar;
import javax.swing.*;
import java.util.logging.*;

/**
 *
 * @author chern
 */
public class Stopwatch implements Runnable {
    
    /** a variable that stores time for each player. */
    private long timeLimitWhite, timeLimitBlack;
    
    /** graphical implementation of timer. */
    private JLabel whiteTimer, blackTimer;
    
    /** new thread for timer. */
    private Thread th;
    
    /** reference to the game. */
    private LocalGame game;
    
    /** true if the player decided to use a timer. */
    private boolean useStopwatch;

    /**
     * constructor for the new timer
     * 
     * @param game - reference to the game
     * @param minutes
     * @param seconds 
     */
    public Stopwatch(LocalGame game, int minutes, int seconds) {
        whiteTimer = new JLabel(makeString(0), SwingUtilities.CENTER);
        whiteTimer.setFont(new Font(Font.SERIF, Font.PLAIN,  45));
        blackTimer = new JLabel(makeString(0), SwingUtilities.CENTER);
        blackTimer.setFont(new Font(Font.SERIF, Font.PLAIN,  45));
        setTime(minutes, seconds);
        this.game = game;
        th = new Thread(this);
    }
    
    /**
     * sets the time for a timer 
     * 
     * @param minutes
     * @param seconds 
     */
    public void setTime(int minutes, int seconds) {
        timeLimitWhite = timeLimitBlack = (60*minutes + seconds)*1000;
        if(timeLimitWhite > 0) useStopwatch = true;
        else useStopwatch = false;
        whiteTimer.setText(makeString(timeLimitWhite));
        whiteTimer.setPreferredSize(new Dimension(200, 100));
        blackTimer.setText(makeString(timeLimitBlack));
        blackTimer.setPreferredSize(new Dimension(200, 100));
    }
    
    
    /**
     * separately sets the time for each of the timers
     * used to load a game
     * 
     * @param timeW - time for white timer
     * @param timeB - time for black timer
     */
    public void setTimeSeparate(int timeW, int timeB) {
        timeLimitWhite = timeW;
        timeLimitBlack = timeB;
        if(timeLimitWhite > 0) useStopwatch = true;
        else useStopwatch = false;
        whiteTimer.setText(makeString(timeLimitWhite));
        blackTimer.setText(makeString(timeLimitBlack));
    }
    
    /**
     * 
     * @return remaining timer white time
     */
    public long getTimeWhite() {
        return timeLimitWhite;
    }
    
    /**
     * 
     * @return remaining timer black time
     */
    public long getTimeBlack() {
        return timeLimitBlack;
    }
    
    
    /**
     * starts the thread for the timer to work.
     */
    @Override
    public void run() {
        while(useStopwatch) {
            while(game.getCurrentTurn() && timeLimitWhite > 0) {
//                try {
                    
//                    timeLimitWhite-=10;
//                    if(timeLimitWhite%1000 == 0) whiteTimer.setText(makeString(timeLimitWhite));
//                    Thread.sleep(9);                    
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(Stopwatch.class.getName()).log(Level.SEVERE, null, ex);
//                }
                long start = System.currentTimeMillis();
                long finish = System.currentTimeMillis();
                while(finish - start < 10) {
                    finish = System.currentTimeMillis();
                }
                timeLimitWhite -= 10;
                if(timeLimitWhite%1000 == 0) whiteTimer.setText(makeString(timeLimitWhite));
                if(timeLimitWhite <= 0) {
                    game.end(true); break;
                }
            }
            while(!game.getCurrentTurn() && timeLimitBlack > 0) {
//                try {
//                    timeLimitBlack-=10;
//                    if(timeLimitBlack%1000 == 0) blackTimer.setText(makeString(timeLimitBlack));
//                    Thread.sleep(9);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(Stopwatch.class.getName()).log(Level.SEVERE, null, ex);
//                }
                long start = System.currentTimeMillis();
                long finish = System.currentTimeMillis();
                while(finish - start < 10) {
                    finish = System.currentTimeMillis();
                }
                timeLimitBlack -= 10;
                if(timeLimitBlack%1000 == 0) blackTimer.setText(makeString(timeLimitBlack));
                
                if(timeLimitBlack == 0) {
                    game.end(false); break;
                }
            }
        }
    }
    
    /**
     * translates time to string format
     * 
     * @param time - time for translation
     * @return time in string format
     */
    public String makeString(long time) {
        String str = "";
        time /= 1000;
        str += time/60 >= 10 ? time/60 : "0" + time/60;
        str += ":";
        str += time%60 >= 10 ? time%60 : "0" + time%60;
        return str;
    }
    
    
    /**
     * 
     * @return link to a graphical implementation of a white timer
     */
    public JLabel getWhiteTimer() {
        return whiteTimer;
    }
    
    /**
     * 
     * @return link to a graphical implementation of a black timer
     */
    public JLabel getBlackTimer() {
        return blackTimer;
    }
    
    /** starts the timer. */
    public void start() {
        th.start();
    }
    
    /** stop the timer. */
    public void stop() {
        th.interrupt();
    }
    
    
    
}
