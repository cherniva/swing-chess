/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pieces;

import Game.*;
import Board.*;

import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author chern
 */
public class PieceTest {
    
    public PieceTest() {
    }

    /**
     * Test of getColor method, of class Piece.
     */
    @Test
    public void testGetColorBlack() {
        System.out.println("getColorNull");
        Piece instance = new Piece(false, "", Piece.Type.bishop, new Board(new LocalGame()));
        boolean expResult = false;
        boolean result = instance.getColor();
        assertEquals(expResult, result);
        fail("Wrong return color black.");
    }
    
    /**
     * Test of getColor method, of class Piece.
     */
    @Test
    public void testGetColorWhite() {
        System.out.println("getColorNull");
        Piece instance = new Piece(true, "", Piece.Type.bishop, new Board(new LocalGame()));
        boolean expResult = true;
        boolean result = instance.getColor();
        assertEquals(expResult, result);
        fail("Wrong return color white.");
    }

    /**
     * Test of getType method, of class Piece.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Piece instance = new Piece(true, "", Piece.Type.queen, new Board(new LocalGame()));
        Piece.Type expResult = Piece.Type.queen;
        Piece.Type result = instance.getType();
        assertEquals(expResult, result);
        fail("Wrong return type.");
    }
    
    /**
     * Test of resetCheck method, of class Piece.
     */
    @Test
    public void testResetCheck() {
        System.out.println("resetCheck");
        Piece instance = new Piece(true, "", Piece.Type.queen, new Board(new LocalGame()));
        instance.setCheck();
        boolean before = Piece.isCheck();
        Piece.resetCheck();
        boolean after = Piece.isCheck();
        assertNotEquals(before, after);
        fail("Wrong resetCheck.");
    }
    

    /**
     * Test of isCheck method, of class Piece.
     */
    @Test
    public void testIsCheck() {
        System.out.println("isCheck");
        Piece instance = new Piece(true, "", Piece.Type.queen, new Board(new LocalGame()));
        instance.setCheck();
        boolean expResult = true;
        boolean result = Piece.isCheck();
        assertEquals(expResult, result);
        fail("Wrong return check.");
    }

    /**
     * Test of checkColor method, of class Piece.
     */
    @Test
    public void testCheckColor() {
        System.out.println("isCheck");
        Piece instance = new Piece(true, "", Piece.Type.queen, new Board(new LocalGame()));
        instance.setCheck();
        boolean expResult = false;
        boolean result = Piece.checkColor();
        assertEquals(expResult, result);
        fail("Wrong return check.");
    }

    /**
     * Test of getAttackTrajectory method, of class Piece.
     */
    @Test
    public void testGetAttackTrajectory() {
        System.out.println("getAttackTrajectory");
        Cell[][] board = new Cell[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j);
            }
        }
        Board chessB = new Board(new LocalGame());
        chessB.setBoard(board);
        board[0][4].addPiece(new King(false, "", chessB));
        board[7][3].addPiece(new Queen(true, "", chessB));
        chessB.executeMove(7, 3, 7, 4, "local");
        
        assertEquals(7, Piece.getAttackTrajectory().size());
        fail("Wrong size");
        
        int[][] cells = {{7, 4}, {6, 4}, {5, 4}, {4, 4}, {3, 4}, {2, 4}, {1, 4}};
        boolean expResult = true;
        boolean result = true;
        for(int i = 0; i < Piece.getAttackTrajectory().size(); i++) {
            boolean ok = false;
            for(int[] point: cells) {
                if(Arrays.equals(Piece.getAttackTrajectory().get(i), point)) ok = true;
                if(ok) break;
            }
            if(!ok) {
                result = false;
                break;
            }
        }
        assertEquals(expResult, result);
        fail("Wrong trajectory");
    }

    /**
     * Test of isitonBoard method, of class Piece.
     */
    @Test
    public void testIsitonBoard() {
        System.out.println("isitonBoard");
        int Y = -1;
        int X = 0;
        Piece instance = null;
        boolean expResult = false;
        boolean result = instance.isitonBoard(Y, X);
        assertEquals(expResult, result);
        fail("Wrong isitonBoard less then min");
        
        
        Y = 0;
        X = 9;
        expResult = false;
        result = instance.isitonBoard(Y, X);
        assertEquals(expResult, result);
        fail("Wrong isitonBoard more then max");
        
        Y = 5;
        X = 6;
        expResult = true;
        result = instance.isitonBoard(Y, X);
        assertEquals(expResult, result);
        fail("Wrong isitonBoard");
    }

    /**
     * Test of possibleMoves method, of class Piece.
     */
    @Test
    public void testPossibleMoves() {
        System.out.println("possibleMoves");
        Cell[][] board = new Cell[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                board[i][j] = new Cell(i, j);
            }
        }
        Board chessB = new Board(new LocalGame());
        chessB.setBoard(board);
        board[0][0].addPiece(new Rook(false, "", chessB));
        board[0][1].addPiece(new Bishop(true, "", chessB));
        
        ArrayList<int[]> posMoves = board[0][0].getPiece().possibleMoves(0, 0);
        int[][] cells = {{1, 0}, {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}, {0, 1}};
        boolean expResult = true;
        boolean result = true;
        for(int i = 0; i < posMoves.size(); i++) {
            boolean ok = false;
            for(int[] point: cells) {
                if(Arrays.equals(posMoves.get(i), point)) ok = true;
                if(ok) break;
            }
            if(!ok) {
                result = false;
                break;
            }
        }
        assertEquals(expResult, result);
        fail("Wrong return rook");
        
        posMoves = board[0][1].getPiece().possibleMoves(0, 0);
        cells = new int[][]{{1, 0}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, {6, 7}, {1, 2}};
        expResult = true;
        result = true;
        for(int i = 0; i < posMoves.size(); i++) {
            boolean ok = false;
            for(int[] point: cells) {
                if(Arrays.equals(posMoves.get(i), point)) ok = true;
                if(ok) break;
            }
            if(!ok) {
                result = false;
                break;
            }
        }
        assertEquals(expResult, result);
        fail("Wrong return bishop");
    }
    
}
