/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Board;

import Game.*;

import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * graphical representation of the class Board and extends JPanel
 * 
 * @author chern
 */
public class ChessBoard extends JPanel {

    /** reference to board {@link Board#board}. */
    private Board board;
    
    /** coordinates that are used for displaying. */
    private int Y, X, dY, dX;
    
    /** help variables for displaying. */
    private boolean pressed = false, canMove = false;
    
    /** help variable for displaying. */
    private ArrayList<int[]> posMoves;

    /**
     * creates and fills graphical representation of board
     * 
     * @param board {@link Board#board}
     */
    public ChessBoard(Board board) {
        this.board = board;
        this.setPreferredSize(new Dimension(600, 600));
        
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                Y = (e.getY()/75);
                X = (e.getX()/75);
                if(board.replaceMode()) {
                    pressed = true;
                    dY = e.getY()-37; dX = e.getX()-37; // shift for better displaying
                }
                else {
                    canMove = board.canMove(Y, X);
                    if(canMove || (!LocalGame.isStarted() && board.isPiece(Y, X))) {
                        pressed = true;
                        posMoves = board.getCell(Y, X).getPiece().possibleMoves(Y, X);
                        dY = e.getY()-37; dX = e.getX()-37;
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(board.replaceMode()) {
                    board.manualReplacement(Y, X, e.getY()/75, e.getX()/75, true);
                }
                else {
                    if(canMove && LocalGame.isStarted() && !(Y == e.getY()/75 && X == e.getX()/75)) 
                        board.executeMove(Y, X, e.getY()/75, e.getX()/75, "local");
                }
                pressed = false; canMove = false;
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(!board.replaceMode()) canMove = board.canMove(Y, X);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                canMove = false;
            }

        });
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                dY = e.getY()-37; dX = e.getX()-37; // shift for better displaying
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {}
            
        });
        
    }
    
    /**
     * fill the board, draw pieces
     * 
     * @param g - graphics compenent of JPanel
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(45, 45, 45));
        g.fillRect(0, 0, 600, 600);
        g.setColor(Color.lightGray);
        for (int i = 0; i < 600; i += 150) {
            for (int j = 0; j < 600; j += 150) {
                g.fillRect(i, j, 75, 75);
                g.fillRect(i + 75, j + 75, 75, 75);
            }
        }
            
        if(pressed && LocalGame.isStarted()) {
            for(int i = 0; i < posMoves.size(); i++) {
                g.setColor(new Color(0, 180, 0));
                g.fillRect(posMoves.get(i)[1]*75, posMoves.get(i)[0]*75, 75, 75);
                g.setColor(Color.black);
                g.drawRect(posMoves.get(i)[1]*75, posMoves.get(i)[0]*75, 75, 75);
            }
            g.setColor(new Color(100, 200, 100));
            g.fillRect(X*75, Y*75, 75, 75);
        }
        
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(board.getCell(i, j).getPiece() != null) {
                    if(pressed && i == Y && j == X) continue;
                    g.drawImage(board.getCell(i, j).getPiece().getIcon().getImage(), j*75, i*75, 75, 75, this);
                }
            }
        }
        
        if(pressed && board.isPiece(Y, X)) {
            g.drawImage(board.getCell(Y, X).getPiece().getIcon().getImage(), dX, dY, 75, 75, this);
        }
    }
}
