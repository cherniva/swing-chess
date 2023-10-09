/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Board.*;
import Pieces.*;
import Utils.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import java.util.logging.*;

/**
 * the central class of the program, contains graphic components 
 * and variables for the implementation of the game
 *
 * @author chern
 */
public class LocalGame extends JFrame {
    
	protected Logger logger = Logger.getLogger(LocalGame.class.getName());
    
    /** true if the right move belongs to white, false for black. */
    protected boolean currentTurn;
    
    /** true if white should move now. */
    protected boolean color;
    
    /** counter for each side. */
    protected int numberOfTurnsWhite, numberOfTurnsBlack; //was static
    
    /** true if the game was started. */
    protected static boolean started;
    
    /** contains a game board {@link Board#board}. */
    protected Board board;
    
    /** contains a graphic implementation of a game board. */
    protected ChessBoard chessBoard;
    
    /** contains a chess timer. */
    protected Stopwatch stopwatch;
    
    /**
     * text space to display game events on the screen
     * in the network version it transmits information about the game over the network.
     */
    protected JTextArea log;
    
    /** 
     * links to objects to close them
     * @see LocalGame#wl
     */
    protected JFrame game, endframe;
    protected JDialog frame;
    
    /** sidebar link. */
    protected JPanel utils;
    
    /** reference to the current class to pass to another class. */
    protected LocalGame thisGame;
    
    /** control actions when closing the application window. */
    protected WindowListener wl;

    
    /** creation object. */
    public LocalGame() {
        thisGame = this;
        modelPart();
        uiPart();
        logger.info("Game created\n");
    }
    
    /** adjusts game constants to initial values.  */
    public void modelPart() {
        currentTurn = true; // currentTurn = white
        color = true;
        numberOfTurnsWhite = 1;
        numberOfTurnsBlack = 1;
        started = false;
        this.board = new Board(this);
        Piece.resetCheck();
        this.stopwatch = new Stopwatch(this, 0, 0);
        logger.info("Configurate model complete\n");
    }
    
    /**
     * used to load a saved game
     * 
     * @param currentTurn - current move in saved game
     * @param numberOfTurnsWhite - white moves in saved game
     * @param numberOfTurnsBlack - black moves in saved game
     */
    public void modelPart(boolean currentTurn, int numberOfTurnsWhite, int numberOfTurnsBlack) {
        this.currentTurn = currentTurn;
        this.numberOfTurnsWhite = numberOfTurnsWhite;
        this.numberOfTurnsBlack = numberOfTurnsBlack;
    }
    
    /***/
    public Board getBoard() {
        return board;
    }
    
    /**
     * @return link to a game board
     */
    public Stopwatch getStopwatch() {
        return stopwatch;
    }
     
    /**
     * @return variable value of started
     * @see LocalGame#started
     */
    public static boolean isStarted() {
        return started;
    }
    
    /**
     * @return variable value of currentTurn
     * @see LocalGame#currentTurn
     */
    public boolean getCurrentTurn() {
        return currentTurn;
    }
    
    /**
     * @return variable value of color
     * @see LocalGame#color
     */
    public boolean getCurrentColor() {
        return color;
    }
    
    /**
     * 
     * @param color - color for which moves are needed
     * @return value corresponding to the color of the variable
     * @see LocalGame#numberOfTurnsBlack
     * @see LocalGame#numberOfTurnsWhite
     */
    public int getNumberOfTurns(boolean color) { //was static
        return color ? numberOfTurnsWhite : numberOfTurnsBlack;
    }
    
    /**
     * switches the game to the next move
     * checks for stalemate.
     */
    public void nextTurn() {
        if(currentTurn) numberOfTurnsWhite++;
        else numberOfTurnsBlack++;
        this.currentTurn = !this.currentTurn;
        this.color = !this.color;
        boolean mate = board.itIsMate();
    }
    
    /** updates the graphic component of the board. */
    public void updateBoard() {
        chessBoard.repaint();
    }
    
    /** generation of the graphic component of the game. */
    public void uiPart() {
        
        this.game = this;
        this.setLayout(new BorderLayout());
        this.setTitle("Swing Chess: local game");
        optionsBar(); // create the options bar
        
        // create the game board and coordinate bars
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
        
        
        sidePanel(); // create side panel with timers and TextArea for game massages
        
        // create custom WindowListener
        this.wl = new WindowListener() {

            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                game.dispose();
                if(endframe != null) endframe.dispose();
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
        
        optionsWindow(); // pre game options Frame
        
        logger.info("Configurate ui complete\n");
    }
    
    /** possible actions with the game. */
    public void optionsBar() {
        JMenuBar options = new JMenuBar();
        
        JMenu gameoptions = new JMenu("Game");
        
        JMenuItem gameoptions_save = new JMenuItem("Save game");
        gameoptions_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileopen = new JFileChooser();
                    int ret = fileopen.showDialog(null, "Saving file");
                    if(ret == JFileChooser.APPROVE_OPTION) {
                        Writer out = null;
                        try {
                            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileopen.getSelectedFile().getAbsolutePath()), "UTF8"));
                        } catch (FileNotFoundException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        } catch (UnsupportedEncodingException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                        SaveGame sg = new SaveGame(out, thisGame, board, stopwatch);
                        out.close();
                        addLog(" ~ Game saved ~\n");
                    }
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            }
            
        });
         
        gameoptions.add(gameoptions_save);
        options.add(gameoptions);
        this.add(options, BorderLayout.NORTH);
    }
    
    /** displaying messages about the progress of the game. */
    public void addLog(String msg) {
        if(!msg.contains("change")) log.append(msg); // it contains change in  online game only
    }
    
    /** sidebar for timer and message output. */
    public void sidePanel() {
        log = new JTextArea();
        log.setEditable(false);
        JScrollPane scroll = new JScrollPane (log);
        scroll.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        utils = new JPanel(); // main panel for components on side bar
        utils.setLayout(new BorderLayout());
        utils.setPreferredSize(new Dimension(200, 500));
        utils.add(stopwatch.getWhiteTimer(), BorderLayout.SOUTH);
        utils.add(scroll, BorderLayout.CENTER);
        utils.add(stopwatch.getBlackTimer(), BorderLayout.NORTH);
        
        this.add(utils, BorderLayout.EAST);
    }
    
    /** pre game options. */
    public void optionsWindow() {
        frame = new JDialog();
        frame.setLayout(new GridLayout(1, 3));
        
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
        
        frame.add(start);
        frame.add(manualPlacement);
        frame.add(upload);
        frame.add(stopw);
        
        frame.addWindowListener(wl);
        
        frame.setSize(200, 100);
        frame.setLocationRelativeTo(utils);
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }
    
    /** variable to set the timer. */
    protected int minutes, seconds;
    
    /** game clock setting. */
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
            }
        };
        ActionListener decrmin = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
                if(minutes == 0) minutes = 95;
                else minutes -= 5; 
                stopwatch.setTime(minutes, seconds);
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
            }
        };
        
        // create buttons for all timers functions
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
    
    /** variable for the selected piece when replacing a pawn. */
    private String choice;
    
    /** pawn replacement selection window. */
    public String menuOfChangePawn() {
        choice = null;
        JDialog menu = new JDialog(thisGame, "Choose piece", true);
        menu.setLayout(new GridLayout(1, 4, 5, 10));
        JButton bishop = new JButton(new ImageIcon(new ImageIcon(Textures.black_bishop).getImage().getScaledInstance(65, 65, Image.SCALE_DEFAULT)));
        bishop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choice = "bishop";
                menu.dispose();
            }
        });
        JButton knight = new JButton(new ImageIcon(new ImageIcon(Textures.white_knight).getImage().getScaledInstance(65, 65, Image.SCALE_DEFAULT)));
        knight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choice = "knight";
                menu.dispose();
            }
        });
        JButton queen = new JButton(new ImageIcon(new ImageIcon(Textures.black_queen).getImage().getScaledInstance(65, 65, Image.SCALE_DEFAULT)));
        queen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choice = "queen";
                menu.dispose();
            }
        });
        JButton rook = new JButton(new ImageIcon(new ImageIcon(Textures.white_rook).getImage().getScaledInstance(65, 65, Image.SCALE_DEFAULT)));
        rook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choice = "rook";
                menu.dispose();
            }
        });
        menu.add(bishop);
        menu.add(knight);
        menu.add(queen);
        menu.add(rook);

        menu.pack();

        menu.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
        return choice;
    }
    
    /**
     * final window at the end of the game, announcement of the winner
     * @param color - color of the losing side
     */
    public void end(boolean color) {
        started = false;
        endframe = new JFrame("Game over");
        endframe.setLayout(new GridLayout(2, 1));
        
        JLabel lab1 = new JLabel("Game over", SwingUtilities.CENTER);
        JLabel lab2 = new JLabel(((!color ? " White" : " Black") + " wins!"), SwingUtilities.CENTER);
        lab1.setFont(new Font(Font.SERIF, Font.PLAIN,  35));
        lab2.setFont(new Font(Font.SERIF, Font.PLAIN,  35));
        
        endframe.add(lab1);
        endframe.add(lab2);
        
        endframe.setSize(300, 150);
        endframe.setVisible(true);
        endframe.setLocationRelativeTo(null);
        endframe.addWindowListener(wl);
        
    }
    /**
     * final window at the end of the game, announcement draw
     * @param color - color of the losing side
     */
    public void end() {
        started = false;
        endframe = new JFrame("Game over");
        endframe.setLayout(new GridLayout(2, 1));
        
        JLabel lab1 = new JLabel("Game over", SwingUtilities.CENTER);
        JLabel lab2 = new JLabel("Draw", SwingUtilities.CENTER);
        lab1.setFont(new Font(Font.SERIF, Font.PLAIN,  35));
        lab2.setFont(new Font(Font.SERIF, Font.PLAIN,  35));
        
        endframe.add(lab1);
        endframe.add(lab2);
        
        endframe.setSize(300, 150);
        endframe.setVisible(true);
        endframe.setLocationRelativeTo(null);
        endframe.addWindowListener(wl);
        
    }
}