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
public class Queen  extends Piece {

    private Type type;
    
    public Queen(boolean color, String iconPath, Board board) {
        super(color, iconPath, Type.queen, board);
    }
}
