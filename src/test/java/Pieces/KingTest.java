/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pieces;

import Board.Board;
import Board.Cell;
import Game.LocalGame;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author chern
 */
public class KingTest {
    
    public KingTest() {
    }

    /**
     * Test of possibleMoves method, of class King.
     */
    @Test
    public void testPossibleMoves() {
        System.out.println("possibleMoves");
        LocalGame game = new LocalGame();
        Board board = game.getBoard();
        
        ArrayList<int[]> posMoves = board.getCell(7, 4).getPiece().possibleMoves(7, 4);
        assertEquals(0, posMoves.size());
        
        Cell[][] cells = new Cell[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        board.setBoard(cells);
        cells[4][4].addPiece(new King(true, "", board));
        posMoves = board.getCell(4, 4).getPiece().possibleMoves(4, 4);
        
        int[][] points = new int[][]{{3, 3}, {3, 4}, {3, 5}, {4, 3}, {4, 5}, {5, 3}, {5, 4}, {5, 5}};
        boolean expResult = true;
        boolean result = true;
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
     * Test of possibleMoves method, of class King.
     */
    @Test
    public void testCastlingMove() {
        System.out.println("castlingMove");
        LocalGame game = new LocalGame();
        Board board = game.getBoard();
        
        board.getCell(7, 5).remPiece();
        board.getCell(7, 6).remPiece();
        
        ArrayList<int[]> posMoves = board.getCell(7, 4).getPiece().possibleMoves(7, 4);
        
        int[][] points = new int[][]{{7, 5}, {7, 6}};
        boolean expResult = true;
        boolean result = true;
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
        
        Piece.setCheck(true);
        
        posMoves = board.getCell(7, 4).getPiece().possibleMoves(7, 4);
        assertEquals(1, posMoves.size());
        
        fail("Wrong return of possibleMoves");
    }
    
}
