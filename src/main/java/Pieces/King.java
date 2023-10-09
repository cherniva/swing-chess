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
public class King extends Piece {
    
    private boolean canCastling;
    private Type type;

    public King(boolean color, String iconPath, Board board) {
        super(color, iconPath, Type.king, board);
        canCastling = true;
    }
    
    @Override
    public ArrayList<int[]> possibleMoves(int posY, int posX) {
        ArrayList<int[]> posMoves = new ArrayList<int[]>();
        for(int[] vec: moveDirections) {
            int y = posY, x = posX;
            if(isitonBoard(y+vec[0], x+vec[1]) && 
                (!Board.isPiece(y+vec[0], x+vec[1]) || Board.isEnemy(getColor(), y+vec[0], x+vec[1]))) {
                posMoves.add(new int[]{y+vec[0], x+vec[1]});
            }
        }
        
        ArrayList<int[]> movesForKing = Board.movesForKing(getColor());
        
        // castling move;
        if(canCastling && !isCheck()) {
            if(Board.isPiece(posY, 7) && Board.getCell(posY, 7).getPiece().canCastling()) {
                if(!Board.isPiece(posY, posX+1) && !Board.isPiece(posY, posX+2)) {
                    boolean ok = false; // if the cell does not go under attack
                    for(int j = 0; j < movesForKing.size(); j++) {
                        ok = Arrays.equals(new int[]{posY, posX+1}, movesForKing.get(j));
                        if(ok) break;
                    }
                    if(!ok) posMoves.add(new int[]{posY, posX+2});
                }
            }
            if(Board.isPiece(posY, 0) && Board.getCell(posY, 0).getPiece().canCastling()) {
                if(!Board.isPiece(posY, posX-1) && !Board.isPiece(posY, posX-2) && !Board.isPiece(posY, posX-3)) {
                    boolean ok = false; // if the cell does not go under attack
                    for(int j = 0; j < movesForKing.size(); j++) {
                        ok = Arrays.equals(new int[]{posY, posX-1}, movesForKing.get(j));
                        if(ok) break;
                    }
                    if(!ok) posMoves.add(new int[]{posY, posX-2});
                }
            }
        }
        
        // remove all cells under opponents attack
        for(int i = 0; i < posMoves.size(); i++) { 
            boolean ok = false;
            for(int j = 0; j < movesForKing.size(); j++) {
                ok = Arrays.equals(posMoves.get(i), movesForKing.get(j));
                if(ok) break;
            }
            if(ok) {
                posMoves.remove(i);
                i--;
            }
        }
        
        for(int i = 0; i < posMoves.size(); i++) {
            // the king cannot move if he is under attack as a result of the move
            if(Board.isEnemy(getColor(), posMoves.get(i)[0], posMoves.get(i)[1])) {
                boolean enemy = false;
                for(int[] vec: moveDirections) {
                    int y = posMoves.get(i)[0], x = posMoves.get(i)[1];
                    while(isitonBoard(y + vec[0], x + vec[1]) && !Board.isPiece(y + vec[0], x + vec[1])) {
                        y += vec[0]; x += vec[1];
                    }
                    if(isitonBoard(y + vec[0], x + vec[1]) && Board.isEnemy(getColor(), y + vec[0], x + vec[1]) &&
                            (vec[0] == 0 || vec[1] == 0) &&
                            (Board.getCell(y + vec[0], x + vec[1]).getPiece().getType() == Type.queen ||
                             Board.getCell(y + vec[0], x + vec[1]).getPiece().getType() == Type.rook)) {
                        enemy = true;
                        break;
                    }else if(isitonBoard(y + vec[0], x + vec[1]) && Board.isEnemy(getColor(), y + vec[0], x + vec[1]) &&
                            (vec[0] != 0 && vec[1] != 0) &&
                            (Board.getCell(y + vec[0], x + vec[1]).getPiece().getType() == Type.queen ||
                             Board.getCell(y + vec[0], x + vec[1]).getPiece().getType() == Type.bishop)) {
                        enemy = true;
                        break;
                    }
                }
                int[][] knightAttck = new int[][]{{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
                for(int[] vec: knightAttck) {
                    if(isitonBoard(posMoves.get(i)[0] + vec[0], posMoves.get(i)[1] + vec[1]) && 
                    Board.isEnemy(getColor(), posMoves.get(i)[0] + vec[0], posMoves.get(i)[1] + vec[1]) &&
                    Board.getCell(posMoves.get(i)[0] + vec[0], posMoves.get(i)[1] + vec[1]).getPiece().getType() == Type.knight)
                        enemy = true;
                }
                if(enemy) {
                    posMoves.remove(i);
                    i--;
                }
            }
        }
        if(isCheck()) {
        // if the check is declared, the king cannot cut down the attacking piece if he becomes attacked by another piece
            if(Math.abs(getAttackTrajectory().get(0)[0] - posY) == Math.abs(getAttackTrajectory().get(0)[1] - posX)) {
                //diagonal attack
                int dY, dX;
                if(getAttackTrajectory().get(0)[0] - posY > 0) dY = -1;
                else dY = 1;
                if(getAttackTrajectory().get(0)[1] - posX > 0) dX = -1;
                else dX = 1;
                if(isitonBoard(posY + dY, posX + dX)) {
                    for(int i = 0; i < posMoves.size(); i++) {
                        if(Arrays.equals(posMoves.get(i), new int[] {posY + dY, posX + dX})) {
                            posMoves.remove(i);
                            break;
                        }
                    }
                }
            } else if(getAttackTrajectory().get(0)[1] == posX) {
                //vertical attack
                int dY;
                if(getAttackTrajectory().get(0)[0] - posY > 0) dY = -1;
                else dY = 1;
                if(isitonBoard(posY + dY, posX)) {
                    for(int i = 0; i < posMoves.size(); i++) {
                        if(Arrays.equals(posMoves.get(i), new int[] {posY + dY, posX})) {
                            posMoves.remove(i);
                            break;
                        }
                    }
                }
            } else if (getAttackTrajectory().get(0)[0] == posY) {
                //horizontal attack
                int dX;
                if(getAttackTrajectory().get(0)[1] - posX > 0) dX = -1;
                else dX = 1;
                if(isitonBoard(posY, posX + dX)) {
                    for(int i = 0; i < posMoves.size(); i++) {
                        if(Arrays.equals(posMoves.get(i), new int[] {posY, posX + dX})) {
                            posMoves.remove(i);
                            break;
                        }
                    }
                }
            }else {
                //knight attack
            }
        }      
        return posMoves;
    }

    @Override
    public void afterMove() {
        canCastling = false;
    } 
    
}
