/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 * the class contains a link to the bot instance
 * carries out moves and presents functionality for setting up the game
 * 
 * @author chern
 */
public class VSBot extends LocalGame {
    
    /** bot instance. */
    private Bot bot;
    
    /** color switch button. */
    private JButton holdColor;
    
    /** super class constructor call, create bot and start new Thread. */
    public VSBot() {
        super();
        color = true;
        this.setTitle("Swing Chess: game vs bot");
        bot = new Bot(false, this.board, this);
        Thread th = new Thread(bot);
        th.start();
    }
    
    /** does not change color in the game, because the opposite belongs to the bot. */
    @Override
    public void nextTurn() {
        if(currentTurn) numberOfTurnsWhite++;
        else numberOfTurnsBlack++;
        this.currentTurn = !this.currentTurn;
        //this.color = !this.color;
        boolean mate = board.itIsMate();
    }
    
    /** overwrites the function for the choice of color. */
    @Override
    public void optionsWindow() {
        //frame = new JFrame();
        frame = new JDialog();
        frame.setLayout(new GridLayout(1, 5));
        
        JToggleButton manualPlacement = new JToggleButton(new ImageIcon(Textures.replacement_icon));
        manualPlacement.setToolTipText("manual arrangement");
        manualPlacement.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(board.replaceMode()) board.deactivateManualReplacement();
                else board.activateManualReplacement();
            }
        });
        
        JButton start = new JButton(new ImageIcon(Textures.play_icon));
        start.setToolTipText("start the game");
        
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                started = true;
                board.deactivateManualReplacement();
                stopwatch.start();
                frame.dispose();
            }
            
        });
        
        JButton upload = new JButton(new ImageIcon(Textures.upload_icon));
        upload.setToolTipText("load saved game");
        
        upload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileopen = new JFileChooser();
                    int ret = fileopen.showDialog(null, "Load file");
                    if(ret == JFileChooser.APPROVE_OPTION) {
                        Scanner sc = new Scanner(new FileInputStream(fileopen.getSelectedFile().getAbsolutePath()), "UTF8");
                        LoadGame sg = new LoadGame(sc, thisGame, board, stopwatch);
                    }
                } catch (FileNotFoundException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            
        });
        
        JButton stopw = new JButton(new ImageIcon(Textures.stopwatch_icon));
        stopw.setToolTipText("set timer");
        
        stopw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTimer();
            }
            
        });
        
        holdColor = new JButton(new ImageIcon(Textures.holdw));
        holdColor.setToolTipText("change color");
        
        holdColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                color = !color;
                bot.changeColor();
                if(color) holdColor.setIcon(new ImageIcon(Textures.holdw));
                else holdColor.setIcon(new ImageIcon(Textures.holdb));
            }
            
        });
        
        frame.add(start);
        frame.add(manualPlacement);
        frame.add(upload);
        frame.add(stopw);
        frame.add(holdColor);
        
        frame.addWindowListener(wl);
        
        frame.setSize(200, 100);
        frame.setLocationRelativeTo(utils);
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
    
}
