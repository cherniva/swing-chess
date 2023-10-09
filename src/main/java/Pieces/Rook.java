/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pieces;

import Board.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author chern
 */
public class Rook extends Piece {
    
    private boolean canCastling;
    private Type type;

    public Rook(boolean color, String iconPath, Board board) {
        super(color, iconPath, Type.rook, board);
        canCastling = true;
    }
    
    @Override
    public boolean canCastling() {
        return canCastling;
    }

    @Override
    public void afterMove() {
        canCastling = false;
    }

}
