/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pieces;

import Board.*;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;

/**
 * chess piece class
 * 
 * @author chern
 */
public class Piece {
    
    /** types of chess pieces. */
    public enum Type {bishop, king, knight, pawn, queen, rook};
    
    /** variable containing the type of chess piece. */
    private Type type;
    
    /** true if color of piece is white. */
    private boolean color; // true - white, false - black;
    
    /** true if one of the king under check. */
    private static boolean check;
    
    /** used to determine which of the kings is under check. */
    private static boolean checkColor;
    
    /** array to determine the direction of the threat to the king. */
    private static ArrayList<int[]> attackTrajectory;
    
    /** an array that contains the coordinates of both kings on the board. */
    private static int[] kingsCoordinates = new int[4];
    
    /** an array containing vectors to form possible moves. */
    protected final int[][] moveDirections;
    
    /** contains a link to the current board for using its methods. */
    protected Board Board;
    
    /** contains an icon for drawing a piece on the board. */
    private ImageIcon icon;
    
    /**
     * constructor of the class
     * 
     * @param color - true if the color of piece is white
     * @param iconPath - path to create an icon
     * @param type - type of the piece
     * @param board - reference to voard
     */
    public Piece(boolean color, String iconPath, Type type, Board board) {
        this.color = color;
        this.icon = new ImageIcon(iconPath);
        this.type = type;
        this.Board = board;
        switch (type) {
            case bishop: moveDirections = new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}}; break;
            
            case king: moveDirections = new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, 
                                                      {1, 0}, {-1, 0}, {0, 1}, {0, -1}}; break;
                    
            case knight: moveDirections = new int[][] {{2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                                                        {1, 2}, {1, -2}, {-1, 2}, {-1, -2}}; break;
            
            case pawn: 
                if(color) moveDirections = new int[][] {{-1, 0}, {-1, -1}, {-1, 1}}; // for white pawn
                else moveDirections = new int[][] {{1, 0}, {1, -1}, {1, 1}}; // for black pawn
                break;
                
            case queen: moveDirections = new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, 
                                                       {1, 0}, {-1, 0}, {0, 1}, {0, -1}}; break;
                    
            case rook: moveDirections = new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; break;
            
            default: moveDirections = null; break;
        }
    }

    /**
     * @return color of this piece
     */
    public boolean getColor() {
        return color;
    }
    
    /**
     * @return type of this piece
     */
    public Type getType() {
        return type;
    }
    
    /**
     * @return icon of this piece
     */
    public ImageIcon getIcon() {
        return this.icon;
    }
    
    /** sets the check and determines the color of the king for protection. */
    public void setCheck() {
        check = true;
        checkColor = !this.getColor();
    }
    
    /**
     * sets the check and sets the king's color for protection
     * @param color - king color to protect
     */
    public static void setCheck(boolean color) {
        check = true;
        checkColor = color;
    }
    
    /** removes check if defense is successful. */
    public static void resetCheck() {
        check = false;
    }
    
    /**
     * @return true if the king is under attack
     */
    public static boolean isCheck() {
        return check;
    }
    
    /**
     * @return true if color of the king under attack is white
     */
    public static boolean checkColor() {
        return checkColor;
    }
    
    /**
     * an array with the trajectory of attack on the king if king under check.
     */
    public static ArrayList<int[]> getAttackTrajectory() {
        return attackTrajectory;
    }
    
    /**
     * sets the attack path when loading a saved game
     * 
     * @param attackTrajectory 
     * @see Piece#attackTrajectory
     */
    public static void setAttackTrajectory(ArrayList<int[]> attackTrajectory) {
        Piece.attackTrajectory = attackTrajectory;
    }
    
    /**
     * updates the coordinates of kings
     * 
     * @param color - color of the king for update
     * @param y - new y-coordinate 
     * @param x - new x-coordinate
     */
    public static void updateKingsCoordinates(boolean color, int y, int x) {
        if(color) {
            kingsCoordinates[0] = y;
            kingsCoordinates[1] = x;
        } else {
            kingsCoordinates[2] = y;
            kingsCoordinates[3] = x;
        }
    }
    
    /**
     * updates the coordinates of both kings
     * 
     * @param wY - new y-coordinate of white king
     * @param wX - new x-coordinate of white king
     * @param bY - new y-coordinate of white king
     * @param bX - new x-coordinate of white king
     */
    public static void updateKingsCoordinates(int wY, int wX, int bY, int bX) {
        kingsCoordinates[0] = wY;
        kingsCoordinates[1] = wX;
        kingsCoordinates[2] = bY;
        kingsCoordinates[3] = bX;
    }
    
    /**
     * @return coordinates of kings
     */
    public int[] getKingsCoordinates() {
        return kingsCoordinates;
    }
    
    /**
     * @param Y - y-coordinate
     * @param X - x-coordinate
     * @return true if the coordinate is on the board
     */
    public boolean isitonBoard(int Y, int X) {
        return (Y >= 0 && Y <= 7 && X >= 0 && X <=7);
    }
    
    /**
     * updates information for figures for which it is necessary
     * determines the setting of the mat to the king and determines the path of attack
     * 
     * @param fromY - y-coordinate from where the input was made
     * @param fromX - x-coordinate from where the input was made
     * @param toY - y-coordinate where the input was made
     * @param toX - y-coordinate where the input was made
     * @param numberOfTurn - number to which the move was made
     */
    public void controlStatus(int fromY, int fromX, int toY, int toX, int numberOfTurn) {
        if(isCheck()) {
            // if this function is called, then the mat is not set, then the attack is repelled
            resetCheck();
            setAttackTrajectory(null);
        }
        switch (type) {
            // because these three functions have parameters on which the possible moves depend
            case pawn: afterMove(fromY, toY, numberOfTurn); break;
            case king: 
                afterMove(); 
                this.updateKingsCoordinates(getColor(), toY, toX);
                break;
            case rook: afterMove(); break;
        }
        
        ArrayList<int[]> posMoves = possibleMoves(toY, toX);
        for(int i = 0; i < posMoves.size(); i++) {
            if(Board.getCell(posMoves.get(i)[0], posMoves.get(i)[1]).getPiece() == null) continue;
            Piece piece = Board.getCell(posMoves.get(i)[0], posMoves.get(i)[1]).getPiece();
            if(piece.getType() == Type.king) {
                setCheck();
                attackTrajectory = new ArrayList<int[]>();
                attackTrajectory.add(new int[]{toY, toX});
                if(Math.abs(posMoves.get(i)[0] - toY) == Math.abs(posMoves.get(i)[1] - toX)) {
                    //diagonal attack
                    int dY, dX;
                    if(posMoves.get(i)[0] - toY > 0) dY = 1;
                    else dY = -1;
                    if(posMoves.get(i)[1] - toX > 0) dX = 1;
                    else dX = -1;
                    while(toY + dY != posMoves.get(i)[0]) {
                        attackTrajectory.add(new int[]{toY+dY, toX+dX});
                        toY += dY; toX += dX;
                    }
                } else if(posMoves.get(i)[1] == toX) {
                    //vertical attack
                    int dY;
                    if(posMoves.get(i)[0] - toY > 0) dY = 1;
                    else dY = -1;
                    while(toY + dY != posMoves.get(i)[0]) {
                        attackTrajectory.add(new int[]{toY+dY, toX});
                        toY += dY;
                    }
                } else if (posMoves.get(i)[0] == toY) {
                    //horizontal attack
                    int dX;
                    if(posMoves.get(i)[1] - toX > 0) dX = 1;
                    else dX = -1;
                    while(toX + dX != posMoves.get(i)[1]) {
                        attackTrajectory.add(new int[]{toY, toX+dX});
                        toX += dX;
                    }
                } else {
                    //knight attck
                }
                break;
            }
        }
    }
    
    /** class for forming possible moves of pieces. */
    public ArrayList<int[]> possibleMoves(int posY, int posX) {ArrayList<int[]> posMoves = new ArrayList<int[]>();
        for(int[] vec: moveDirections) {
            int y = posY, x = posX;
            while(isitonBoard(y+vec[0], x+vec[1]) && !Board.isPiece(y+vec[0], x+vec[1])) {
                posMoves.add(new int[]{y+vec[0], x+vec[1]});
                y += vec[0]; x += vec[1];
            }
            if(isitonBoard(y+vec[0], x+vec[1]) && Board.isEnemy(getColor(), y+vec[0], x+vec[1])) {
                posMoves.add(new int[]{y+vec[0], x+vec[1]});
            }
        }
        
        int kY = getColor() ? getKingsCoordinates()[0] : getKingsCoordinates()[2];
        int kX = getColor() ? getKingsCoordinates()[1] : getKingsCoordinates()[3];
        
        int[] defVec = new int[2];
        defVec[0] = posY - kY > 0 ? 1 : (posY - kY == 0 ? 0 : -1);
        defVec[1] = posX - kX > 0 ? 1 : (posX - kX == 0 ? 0 : -1);
        
        // if the figure is covered by an allied figure
        boolean defended = false; 
        kY+=defVec[0]; kX+=defVec[1];
        ArrayList<int[]> def = new ArrayList<int[]>();
        while(isitonBoard(kY, kX) && (kY != posY || kX != posX)) {
            if(Board.isPiece(kY, kX)) {
                defended = true;
                break;
            }
            def.add(new int[]{kY, kX});
            kY+=defVec[0]; kX+=defVec[1];
        }
        
        if(!defended) {
            // a piece cannot make a move if it opens the king
            while(isitonBoard(kY+defVec[0], kX + defVec[1]) && !Board.isPiece(kY+defVec[0], kX + defVec[1])) {
                kY += defVec[0]; kX += defVec[1];
                def.add(new int[] {kY, kX});
            }
            if(isitonBoard(kY+defVec[0], kX + defVec[1]) && Board.isEnemy(getColor(), kY+defVec[0], kX + defVec[1]) &&
              (Board.getCell(kY+defVec[0], kX + defVec[1]).getPiece().getType() == Type.bishop ||
               Board.getCell(kY+defVec[0], kX + defVec[1]).getPiece().getType() == Type.queen ||
               Board.getCell(kY+defVec[0], kX + defVec[1]).getPiece().getType() == Type.rook)) {
                def.add(new int[] {kY+defVec[0], kX + defVec[1]});
                
                for(int i = 0; i < posMoves.size(); i++) {
                    boolean ok = false;
                    for(int j = 0; j < def.size(); j++) {
                        if(Arrays.equals(posMoves.get(i), def.get(j))) {
                            ok = true;
                            break;
                        }
                    }
                    if(!ok) posMoves.remove(i--);
                }
            }
        }
        
        if(isCheck() && checkColor() == getColor()) {
            for(int i = 0; i < posMoves.size(); i++) {
                boolean ok = false;
                for(int j = 0; j < getAttackTrajectory().size(); j++) {
                    ok = Arrays.equals(posMoves.get(i), getAttackTrajectory().get(j));
                    if(ok) break;
                }
                if(!ok) {
                    posMoves.remove(i);
                    i--;
                }
            }
        }
        
        return posMoves;
        
    }
    
    /** @see Piece#afterMove(int, int, int) */
    public void afterMove() {}
    
    /** 
     * function for updating the status of figures
     * defined in figure classes that need it
     * 
     * this function for pawn
     * 
     * @param fromY - y-coordinate from where the input was made
     * @param toY - y-coordinate where the input was made
     * @param numberOfTurn
     */
    public void afterMove(int fromY, int toY, int numberOfTurn) {}
    
    /** if the piece can make the move en passant (only a pawn can) */
    public boolean canEnPassant() { return false; }
    
    /** if the piece can make the castling move (only a king can) */
    public boolean canCastling() { return false; }
}
