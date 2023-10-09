/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Game.Host;
import Game.Join;
import Game.LocalGame;
import Game.VSBot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * game start page, contains game modes
 * 
 * @author chern
 */
public class MainMenu extends JFrame {
    
    
    private JLabel label; // name of the game
    private JButton localGame;
    private JButton withBot;
    private JButton onlineGameHost;
    private JButton onlineGameJoin;

    public MainMenu(String title) throws HeadlessException {
        super(title);
        
        GridLayout layout = new GridLayout(3, 1, 10, 10);
        this.setLayout(layout);
        this.setSize(720, 480);
        
        label = new JLabel("Swing Chess", SwingConstants.CENTER);
        label.setFont(new Font(Font.SERIF, Font.PLAIN,  45));
        JPanel forlab = new JPanel();
        forlab.setLayout(new BorderLayout());
        forlab.add(label);
        
        localGame = new JButton("Local Game");
        localGame.setFont(new Font(Font.SERIF, Font.PLAIN,  15));
        localGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        LocalGame locgame = new LocalGame();
                    }
                });
            }
        });
        
        withBot = new JButton("Game vs bot (demo)");
        withBot.setFont(new Font(Font.SERIF, Font.PLAIN,  15));
        withBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        VSBot vsbot = new VSBot();
                    }
                });
            }
        });
        
        onlineGameHost = new JButton("Online Game(host)");
        onlineGameHost.setFont(new Font(Font.SERIF, Font.PLAIN,  15));
        onlineGameHost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Host online = new Host();
                    }
                });
            }
        });
        onlineGameJoin = new JButton("Online Game(client)");
            onlineGameJoin.setFont(new Font(Font.SERIF, Font.PLAIN,  15));
            onlineGameJoin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Join online = new Join();
                    }
                });
                // Close main menu or not???
            }
        });
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 3, 15, 15));
        panel.add(new JLabel("")); panel.add(localGame); panel.add(new JLabel("")); 
        panel.add(new JLabel("")); panel.add(withBot); panel.add(new JLabel(""));
        panel.add(new JLabel("")); panel.add(onlineGameHost); panel.add(new JLabel(""));
        panel.add(new JLabel("")); panel.add(onlineGameJoin); panel.add(new JLabel(""));   
        
        this.add(forlab);
        this.add(panel);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainMenu menu = new MainMenu("SwingChess");
            }
        });
    }
}