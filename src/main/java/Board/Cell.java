/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Board;

import Pieces.*;

/**
 * class for storing pieces and coordinates
 * 
 * @author chern
 */
public class Cell {
    /** reference to piece {@link Piece#Piece(boolean, java.lang.String, Pieces.Piece.Type, Board.Board) }. */
    private Piece piece;
    
    /** coordinete of the Cell {@link Board#board}. */
    private int Y, X;
    
    /**
     * create the empty cell
     * 
     * @param Y - y-coodinate of the Cell
     * @param X - x-coodinate of the Cell
     */
    public Cell(int Y, int X) {
        this.piece = null;
        this.Y = Y;
        this.X = X;
    }
    
    /**
     * create the cell
     * 
     * @param piece - piece
     * @see Cell#piece
     * @param Y - y-coodinate of the Cell
     * @param X - x-coodinate of the Cell
     */
    public Cell(Piece piece, int Y, int X) {
        addPiece(piece);
        this.Y = Y;
        this.X = X;
    }

    /**
     * @return piece that contains
     */
    public Piece getPiece() {
        return piece;
    }

    /**
     * @return y-coordinate of cell
     */
    public int getY() {
        return Y;
    }
    
    /**
     * @return x-coordinate of cell
     */
    public int getX() {
        return X;
    }
    
    /**
     * add piece on the cell
     * 
     * @param piece 
     * @see Cell#piece
     */
    public void addPiece(Piece piece) {
        this.piece = piece;
    }    
    
    /**
     * remove piece from cell
     * @see Cell#piece
     */
    public void remPiece() {
        this.piece = null;
    }
}
