/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Board.*;
import Game.*;
import Pieces.*;

import java.util.*;
import java.util.logging.*;

/**
 *
 * @author chern
 */
public class Bot implements Runnable {
    
    /** true if the color of the bot's figures is white. */
    private boolean color;
    
    /** reference to this game. */
    private VSBot game;
    
    /** reference to game board. */
    private Board Board;

    
    public Bot(boolean color, Board board, VSBot game) {
        this.color = color;
        this.game = game;
        this.Board = board;
    }
    
    /** function changing the color of the bot at the request of the player. */
    public void changeColor() {
        color = !color;
    }
    
    /**
     * function that selects a random move from the available bot
     * 
     * @return coordinates of where from and where to the entrance will be made
     */
    public int[] makeTurn() {
        int[] ret = new int[4];
        Random rnd = new Random();
        ArrayList<Piece> myPieces = new ArrayList<Piece>();
        ArrayList<int[]> coords = new ArrayList<int[]>();
        // looking for piece under bot control
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(Board.isPiece(i, j) && !Board.isEnemy(color, i, j)) {
                    myPieces.add(Board.getCell(i, j).getPiece());
                    coords.add(new int[]{i, j});
                }
            }
        }        
        
        int choice = rnd.nextInt(myPieces.size());
        while(myPieces.get(choice).possibleMoves(coords.get(choice)[0], coords.get(choice)[1]).size() == 0) { 
            choice = rnd.nextInt(myPieces.size());
        }
        
        ret[0] = coords.get(choice)[0];
        ret[1] = coords.get(choice)[1];
        int moveto = rnd.nextInt(myPieces.get(choice).possibleMoves(ret[0], ret[1]).size());
        ret[2] = myPieces.get(choice).possibleMoves(ret[0], ret[1]).get(moveto)[0];
        ret[3] = myPieces.get(choice).possibleMoves(ret[0], ret[1]).get(moveto)[1];
        
        return ret;
    }
    
    /**
     * monitors when it can make a move, after which it makes a move
     * there is a random delay to prevent instant moves.
     */
    @Override
    public void run() {
        while(true) {
            if(color == game.getCurrentTurn() && LocalGame.isStarted()) {
                try {
                    Thread.sleep((int)(Math.random()*1000*3));
                } catch (InterruptedException ex) {
                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                }
                int[] move = makeTurn();
                Board.executeMove(move[0], move[1], move[2], move[3], "bot");
                game.updateBoard();
            }
            try {
                Thread.sleep((int)(Math.random()*1000) + 500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
