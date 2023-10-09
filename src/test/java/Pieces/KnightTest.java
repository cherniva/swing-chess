/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pieces;

import Board.*;
import Game.*;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author chern
 */
public class KnightTest {
    
    public KnightTest() {
    }

    /**
     * Test of possibleMoves method, of class Knight.
     */
    @Test
    public void testPossibleMoves() {
        System.out.println("possibleMoves");
        LocalGame game = new LocalGame();
        Board board = game.getBoard();
        
        ArrayList<int[]> posMoves = board.getCell(7, 6).getPiece().possibleMoves(7, 6);
        assertEquals(2, posMoves.size());
        
        int[][] points = {{5, 5}, {6, 7}};
        boolean expResult = true;
        boolean result = true;
        for(int i = 0; i < Piece.getAttackTrajectory().size(); i++) {
            boolean ok = false;
            for(int[] point: points) {
                if(Arrays.equals(Piece.getAttackTrajectory().get(i), point)) ok = true;
                if(ok) break;
            }
            if(!ok) {
                result = false;
                break;
            }
        }
        assertEquals(expResult, result);
        
        Cell[][] cells = new Cell[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        board.setBoard(cells);
        cells[4][4].addPiece(new Knight(true, "", board));
        posMoves = board.getCell(4, 4).getPiece().possibleMoves(4, 4);
        
        points = new int[][]{{6, 5}, {6, 3}, {5, 2}, {5, 6}, {3, 6}, {3, 2}, {2, 5}, {2, 3}};
        expResult = true;
        result = true;
        for(int i = 0; i < posMoves.size(); i++) {
            boolean ok = false;
            for(int[] point: points) {
                if(Arrays.equals(posMoves.get(i), point)) ok = true;
                if(ok) break;
            }
            if(!ok) {
                result = false;
                break;
            }
        }
        assertEquals(expResult, result);
        
        fail("Wrong return of possibleMoves");
    }
    
    /**
     * Test of possibleMoves method, of class Knight.
     */
    @Test
    public void testDefendKing() {
        System.out.println("defendKing");
        LocalGame game = new LocalGame();
        Board board = game.getBoard();
        
        Cell[][] cells = new Cell[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        board.setBoard(cells);
        cells[0][0].addPiece(new King(true, "", board));
        cells[1][1].addPiece(new Knight(true, "", board));
        cells[7][7].addPiece(new Queen(false, "", board));
        ArrayList<int[]> posMoves = board.getCell(1, 1).getPiece().possibleMoves(1, 1);
        
        assertEquals(1, posMoves.size());
        
        fail("Wrong return of possibleMoves");
    }
    
    /**
     * Test of possibleMoves method, of class Knight.
     */
    @Test
    public void testMovesUnderCheck() {
        System.out.println("movesUnderCheck");
        LocalGame game = new LocalGame();
        Board board = game.getBoard();
        
        Cell[][] cells = new Cell[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        board.setBoard(cells);
        cells[0][0].addPiece(new King(false, "", board));
        cells[0][3].addPiece(new Knight(false, "", board));
        cells[7][7].addPiece(new Queen(false, "", board));
        
        board.executeMove(7, 7, 5, 5, "local");
        
        ArrayList<int[]> posMoves = board.getCell(0, 3).getPiece().possibleMoves(0, 3);
        
        assertEquals(1, posMoves.size());
        
        int[][] points = {{1, 1}};
        boolean expResult = true;
        boolean result = true;
        for(int i = 0; i < Piece.getAttackTrajectory().size(); i++) {
            boolean ok = false;
            for(int[] point: points) {
                if(Arrays.equals(Piece.getAttackTrajectory().get(i), point)) ok = true;
                if(ok) break;
            }
            if(!ok) {
                result = false;
                break;
            }
        }
        assertEquals(expResult, result);
        
        fail("Wrong return of possibleMoves");
    }
    
}
