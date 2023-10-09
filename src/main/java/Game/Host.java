/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Board.ChessBoard;
import Pieces.*;
import Utils.*;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * network game server, implement Runnable
 * 
 * @author chern
 */
public class Host extends LocalGame implements Runnable {
    
    /** server socket. */
    private ServerSocket server;
    
    //private Socket client;
    
    /** server writer. */
    private BufferedWriter wr;
    
    /** server reader. */
    private BufferedReader br;
    
    
    /** variable for transferring the state of the board when connecting the client for the first time. */
    private boolean firstConnection;
    
    /** color switch button */
    private JButton holdColor;
    
    /** super class constructor call and server socket connection. */
    public Host() {
        super();
        color = true;
        firstConnection = true;
        this.setTitle("Swing Chess: online host");
        try {
            server = new ServerSocket(5300);
            Thread th = new Thread(this);
            th.start();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }        
    }
    
    /** does not change color in the game, because the opposite belongs to the other player. */
    @Override
    public void nextTurn() {
        if(currentTurn) numberOfTurnsWhite++;
        else numberOfTurnsBlack++;
        this.currentTurn = !this.currentTurn;
        boolean mate = board.itIsMate();
    }
    
    /**
     * rewrites a function to send game status messages over a network
     * 
     * @param msg - messages for display and / or transmission
     */
    @Override
    public void addLog(String msg) {
        if(started && !msg.contains("start")) log.append(msg);
        if((color == currentTurn || msg.contains("change") || msg.contains("start") || msg.contains("time") || msg.contains("color")) && !msg.equals(" ~ check ~\n") && wr != null) {
            if(msg.contains("turning")) {
                msg = msg.substring(0, msg.indexOf('\n')) + msg.substring(msg.indexOf('\n')+1);
                // delete '\n' for correct processing
            }
            try {
                wr.write(msg);
                wr.flush();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /** rewrites function to close sockets when exiting application. */
    @Override
    public void uiPart() {
        
        this.game = this;
        this.setLayout(new BorderLayout());
        this.setTitle("Swing Chess: local game");
        optionsBar();
        
        chessBoard = new ChessBoard(this.board);
        JPanel panelForBoard = new JPanel(new BorderLayout());
        
        JPanel letterBarTop = new JPanel(new GridLayout(1, 8));
        JPanel forlet = new JPanel(new BorderLayout());
        forlet.setPreferredSize(new Dimension(600, 15));
        forlet.add(letterBarTop, BorderLayout.CENTER);
        forlet.add(new JLabel("~~", SwingUtilities.CENTER), BorderLayout.EAST);
        forlet.add(new JLabel("~~", SwingUtilities.CENTER), BorderLayout.WEST);
        
        JPanel letterBarBot = new JPanel(new GridLayout(1, 8));
        JPanel forbot = new JPanel(new BorderLayout());
        forbot.setPreferredSize(new Dimension(600, 15));
        forbot.add(letterBarBot, BorderLayout.CENTER);
        forbot.add(new JLabel("~~", SwingUtilities.CENTER), BorderLayout.EAST);
        forbot.add(new JLabel("~~", SwingUtilities.CENTER), BorderLayout.WEST);
        
        JPanel digitBarLeft = new JPanel(new GridLayout(8, 1));
        digitBarLeft.setPreferredSize(new Dimension(15, 600));
        
        JPanel digitBarRight = new JPanel(new GridLayout(8, 1));
        digitBarRight.setPreferredSize(new Dimension(15, 600));
        
        for(int i = 8; i >= 1; i--) {
            letterBarTop.add(new JLabel(Character.toString((char)(73-i)), SwingUtilities.CENTER));
            letterBarBot.add(new JLabel(Character.toString((char)(73-i)), SwingUtilities.CENTER));
            digitBarLeft.add(new JLabel(i+"", SwingUtilities.CENTER));
            digitBarRight.add(new JLabel(i+"", SwingUtilities.CENTER));
        }
        panelForBoard.add(chessBoard, BorderLayout.CENTER);
        panelForBoard.add(forlet, BorderLayout.NORTH);
        panelForBoard.add(forbot, BorderLayout.SOUTH);
        panelForBoard.add(digitBarLeft, BorderLayout.WEST);
        panelForBoard.add(digitBarRight, BorderLayout.EAST);
        this.add(panelForBoard, BorderLayout.CENTER);
        
        sidePanel();
        
        this.wl = new WindowListener() {

            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                game.dispose();
                if(endframe != null) endframe.dispose();
                try {
                    server.close();
                    if(br != null)br.close();
                    if(wr != null)wr.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
                
            }

            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
            
        };
        this.addWindowListener(wl);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        optionsWindow();
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
                addLog("start\n");
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
                addLog("color " + (color ? "white\n" : "black\n"));
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
    
    /** rewrites function for time synchronization. */
    @Override
    public void setTimer() {
        minutes = 0; seconds = 0;
        JFrame timerWindow = new JFrame();
        timerWindow.setTitle("Timer settings");
        timerWindow.setLayout(new GridLayout(1, 2));
        
        ActionListener incrmin = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(minutes == 95) minutes = 0;
                else minutes += 5;
                stopwatch.setTime(minutes, seconds);
                addLog("time " + minutes + " " + seconds + "\n");
            }
        };
        ActionListener decrmin = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                if(minutes == 0) minutes = 95;
                else minutes -= 5; 
                stopwatch.setTime(minutes, seconds);
                addLog("time " + minutes + " " + seconds + "\n");
            }
        };
        ActionListener incrsec = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(seconds == 30) {
                    seconds = 0;
                    if(minutes == 99) minutes = 0;
                    else minutes += 1;
                }
                else seconds += 30;
                stopwatch.setTime(minutes, seconds); 
                addLog("time " + minutes + " " + seconds + "\n");
            }
        };
        ActionListener decrsec = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(seconds == 0) {
                    seconds = 30;
                    if(minutes == 0) minutes = 99;
                    else minutes -= 1;
                }
                else seconds -= 30;
                stopwatch.setTime(minutes, seconds); 
                addLog("time " + minutes + " " + seconds + "\n");
            }
        };
        
        JPanel formin = new JPanel();
        formin.setLayout(new GridLayout(3, 1));
        
        JLabel min = new JLabel("Minutes", SwingUtilities.CENTER);
        min.setFont(new Font(Font.SERIF, Font.PLAIN,  20));
        
        JButton minpl = new JButton("+");
        minpl.setFont(new Font(Font.SERIF, Font.PLAIN,  30));
        minpl.addActionListener(incrmin);
        
        JButton minmi = new JButton("-");
        minmi.setFont(new Font(Font.SERIF, Font.PLAIN,  40));
        minmi.addActionListener(decrmin);
        
        formin.add(minpl); formin.add(min); formin.add(minmi);
        
        
        JPanel forsec = new JPanel();
        forsec.setLayout(new GridLayout(3, 1));
        
        JLabel sec = new JLabel("Seconds", SwingUtilities.CENTER);
        sec.setFont(new Font(Font.SERIF, Font.PLAIN,  20));
        
        JButton secpl = new JButton("+");
        secpl.setFont(new Font(Font.SERIF, Font.PLAIN,  30));
        secpl.addActionListener(incrsec);
        
        JButton secmi = new JButton("-");
        secmi.setFont(new Font(Font.SERIF, Font.PLAIN,  40));
        secmi.addActionListener(decrsec);
        
        forsec.add(secpl); forsec.add(sec); forsec.add(secmi);
        
        
        timerWindow.add(formin); timerWindow.add(forsec);
        
        timerWindow.setSize(300, 200);
        timerWindow.setLocationRelativeTo(null);
        timerWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        timerWindow.setAlwaysOnTop(true);
        timerWindow.setVisible(true);
    }

    /** activates continuous messaging between client and server. */
    @Override
    public void run() {
        //while(true) {
            try {
                Socket client = server.accept();
                logger.info("Connection complete\n");
                wr = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF8"));
                br = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF8"));
                if(firstConnection) {
                    SaveGame sg = new SaveGame(wr, thisGame, board, stopwatch);
                    firstConnection = false;
                }
                while(!client.isClosed()) {
                    String data;
                    try {
                        if(br.ready()) {
                            data = br.readLine();
                            readData(data);
                        }
                    } catch (IOException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            firstConnection = true;
            try {
                //server.close();
                if(br != null)br.close();
                if(wr != null)wr.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        //}
    }
    
    /** decrypts the received data from other player and applies them to the game. */
    public void readData(String data) {
        if(data.contains("time")) {
            Scanner sc = new Scanner(data.substring(5));
            stopwatch.setTime(sc.nextInt(), sc.nextInt());
        }
        else if(data.contains("change")) {
            data = data.substring(7);
            int fromY, fromX, toY, toX;
            fromY = Character.getNumericValue(data.charAt(0));
            fromX = Character.getNumericValue(data.charAt(1));
            toY = Character.getNumericValue(data.charAt(2));
            toX = Character.getNumericValue(data.charAt(3));
            board.manualReplacement(fromY, fromX, toY, toX, false);
            updateBoard();
        }
        else if(data.contains("start")) {
            board.deactivateManualReplacement();
            stopwatch.start();
            frame.dispose();
        }
        else if(data.contains("color")) {
            color = data.contains("white") ? false : true;
            
            if(color) holdColor.setIcon(new ImageIcon(Textures.holdw));
            else holdColor.setIcon(new ImageIcon(Textures.holdb));
        }
        else {
            char[] msg = data.toCharArray();
            int idx = 0;
            while(msg[idx] > 72 || msg[idx] == ' ') idx++;
            int fromY, fromX, toY, toX;
            fromX = (int)(msg[idx++] - 65);
            fromY = 8 - Character.getNumericValue(msg[idx]);
            idx += 5;
            toX = (int)(msg[idx++] - 65);
            toY = 8 - Character.getNumericValue(msg[idx]);
            if(data.contains("turning")) {
                if(data.contains("bishop")) board.executeMove(fromY, fromX, toY, toX, "bishop");
                else if(data.contains("knight")) board.executeMove(fromY, fromX, toY, toX, "knight");
                else if(data.contains("queen")) board.executeMove(fromY, fromX, toY, toX, "queen");
                else if(data.contains("rook")) board.executeMove(fromY, fromX, toY, toX, "rook");
            }
            else board.executeMove(fromY, fromX, toY, toX, "online");
            //lastex = new int[] {toY, toX};
            updateBoard();
        }
    }
}
