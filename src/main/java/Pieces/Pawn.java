/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pieces;

import Board.*;
import Game.LocalGame;

import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author chern
 */
public class Pawn extends Piece {
    
    private boolean firstMove;
    private boolean canBeTaken; // en passant
    private int numberForEnPassant;
    private LocalGame game;

    public Pawn(boolean color, String iconPath, Board board, LocalGame game) {
        super(color, iconPath, Type.pawn, board);
        this.firstMove = true;
        this.canBeTaken = false;
        this.game = game;
    }

    @Override
    public ArrayList<int[]> possibleMoves(int posY, int posX) {
        // does not use the vector from the Piece class, 
        // because this method turned out to be more convenient for perception
        ArrayList<int[]> posMoves = new ArrayList<int[]>();
        if(getColor()) {
            if(isitonBoard(posY-1, posX) && !Board.isPiece(posY-1, posX)) {
                posMoves.add(new int[] {posY-1, posX});
            }
            if(isitonBoard(posY-1, posX-1) && Board.isEnemy(getColor(), posY-1, posX-1)) {
                posMoves.add(new int[] {posY-1, posX-1});
            }
            if(isitonBoard(posY-1, posX+1) && Board.isEnemy(getColor(), posY-1, posX+1)) {
                posMoves.add(new int[] {posY-1, posX+1});
            }
            if(firstMove && !Board.isPiece(posY-1, posX) && !Board.isPiece(posY-2, posX)) {
                posMoves.add(new int[] {posY-2, posX});
            }
        }
        else {
            if(isitonBoard(posY+1, posX) && !Board.isPiece(posY+1, posX)) {
                posMoves.add(new int[] {posY+1, posX});
            }
            if(isitonBoard(posY+1, posX-1) && Board.isEnemy(getColor(), posY+1, posX-1)) {
                posMoves.add(new int[] {posY+1, posX-1});
            }
            if(isitonBoard(posY+1, posX+1) && Board.isEnemy(getColor(), posY+1, posX+1)) {
                posMoves.add(new int[] {posY+1, posX+1});
            }
            if(firstMove && !Board.isPiece(posY+1, posX) && !Board.isPiece(posY+2, posX)) {
                posMoves.add(new int[] {posY+2, posX});
            }
        }
        
        if(isitonBoard(posY, posX+1) && Board.isPiece(posY, posX+1) && Board.getCell(posY, posX+1).getPiece().getType() == Type.pawn) {
            if(Board.getCell(posY, posX+1).getPiece().canEnPassant()) {
                if(getColor()) posMoves.add(new int[] {posY-1, posX+1});
                else posMoves.add(new int[] {posY+1, posX+1});
            }
        }
        if(isitonBoard(posY, posX-1) && Board.isPiece(posY, posX-1) && Board.getCell(posY, posX-1).getPiece().getType() == Type.pawn) {
            if(Board.getCell(posY, posX-1).getPiece().canEnPassant()) {
                if(getColor()) posMoves.add(new int[] {posY-1, posX-1});
                else posMoves.add(new int[] {posY+1, posX-1});
            }
        }
        
        int kY = getColor() ? getKingsCoordinates()[0] : getKingsCoordinates()[2];
        int kX = getColor() ? getKingsCoordinates()[1] : getKingsCoordinates()[3];
        
        int[] defVec = new int[2];
        defVec[0] = posY - kY > 0 ? 1 : (posY - kY == 0 ? 0 : -1);
        defVec[1] = posX - kX > 0 ? 1 : (posX - kX == 0 ? 0 : -1);
        //System.out.println(kY + " " + kX);
        //System.out.println(posY + " " + posX);
        
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
    
    public boolean isFirstMove() {
        return firstMove;
    }
    
    @Override
    public void afterMove(int fromY, int toY, int numberOfTurn) {
        firstMove = false;
        if(Math.abs(fromY-toY) == 2) {
            canBeTaken = true;
            numberForEnPassant = numberOfTurn;
        }
    }
    
    @Override
    public boolean canEnPassant() {
        if(canBeTaken) {
            if(getColor()) {
                if(game.getNumberOfTurns(!getColor())-numberForEnPassant == 0) {
                    return true;
                }
            }
            else {
                if(game.getNumberOfTurns(getColor())-numberForEnPassant == 1) {
                    return true;
                }
            }
        }
        return false;
    }

}
