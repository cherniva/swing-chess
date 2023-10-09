/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Board;

import Game.*;
import Board.*;
import Pieces.*;


import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author chern
 */
public class BoardTest {
    
    public BoardTest() {
    }

    /**
     * Test of isPiece method, of class Board.
     */
    @Test
    public void testIsPiece() {
        System.out.println("isPiece");
        LocalGame game = new LocalGame();
        Board board = game.getBoard();
        
        boolean expResult = true;
        boolean result = board.isPiece(0, 0);
        assertEquals(expResult, result);
        
        expResult = false;
        result = board.isPiece(3, 3);
        assertEquals(expResult, result);
        
        //fail("Wrong return isPiece");
    }

    /**
     * Test of isEnemy method, of class Board.
     */
    @Test
    public void testIsEnemy() {
        System.out.println("isEnemy");
        LocalGame game = new LocalGame();
        Board board = game.getBoard();
        
        boolean expResult = false;
        boolean result = board.isEnemy(true, 7, 4);
        assertEquals(expResult, result);
        
        expResult = true;
        result = board.isEnemy(true, 0, 3);
        assertEquals(expResult, result);
        
        //fail("Wrong return isEnemy");
    }

    /**
     * Test of movesForKing method, of class Board.
     */
    @Test
    public void testMovesForKing() {
        System.out.println("movesForKing");
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
        Piece.updateKingsCoordinates(true, 0, 0);
        
        ArrayList<int[]> movesForKing = board.movesForKing(true);
        assertEquals(3, movesForKing.size());
        
        
        cells[7][7].addPiece(new Queen(false, "", board));
        
        movesForKing = board.movesForKing(true);
        assertEquals(2, movesForKing.size());
        
        //fail("Wrong return of movesForKing");
    }
    
}
