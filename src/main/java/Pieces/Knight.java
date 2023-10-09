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
public class Knight extends Piece {
    
    private Type type;
    
    public Knight(boolean color, String iconPath, Board board) {
        super(color, iconPath, Type.knight, board);
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
        
        int kY = getColor() ? getKingsCoordinates()[0] : getKingsCoordinates()[2];
        int kX = getColor() ? getKingsCoordinates()[1] : getKingsCoordinates()[3];
        
        int[] defVec = new int[2];
        defVec[0] = posY - kY > 0 ? 1 : (posY - kY == 0 ? 0 : -1);
        defVec[1] = posX - kX > 0 ? 1 : (posX - kX == 0 ? 0 : -1);
        
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
            //ArrayList<int[]> def = new ArrayList<int[]>();
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

}
