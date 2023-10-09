/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Board.*;
import Game.*;
import Pieces.*;

import java.io.*;
import java.util.logging.*;

/**
 *
 * @author chern
 */
public class SaveGame {
    
    private Logger logger = Logger.getLogger(SaveGame.class.getName());
    
    /**
     * collects information from objects and write them to a text file
     * 
     * @param out - a writer that is created based on the file stream selected by the player
     * @param game - game to load
     * @param board - board from game
     * @param stopwatch - game timer to load
     * @throws FileNotFoundException 
     */
    public SaveGame(Writer out, LocalGame game, Board Board, Stopwatch stopwatch) throws IOException {
        
        out.write(game.getCurrentTurn() ? "white\n" : "black\n");
        out.write(Integer.toString(game.getNumberOfTurns(true)) + " ");
        out.write(Integer.toString(game.getNumberOfTurns(false)) + "\n");
        out.write(Integer.toString((int)stopwatch.getTimeWhite()) + " ");
        out.write(Integer.toString((int)stopwatch.getTimeBlack()) + "\n");
        
        out.write(Piece.isCheck() ? (Piece.checkColor() ? "wh\n" : "bl\n") : "no\n");
        if(Piece.isCheck()) {
            for(int i = 0; i < Piece.getAttackTrajectory().size(); i++) {
                out.write(Integer.toString(Piece.getAttackTrajectory().get(i)[0]) + " ");
                out.write(Integer.toString(Piece.getAttackTrajectory().get(i)[1]) + " ");
            }
            out.write("\n");
        }
        
        Cell[][] field = Board.getInfoForSave();
       
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(Board.isPiece(i, j)) {
                    out.write(Integer.toString(i) + " " + Integer.toString(j) + " ");
                    out.write(field[i][j].getPiece().getType().name());
                    out.write(field[i][j].getPiece().getColor() ? " white\n" : " black\n");
                }
            }
        }
        
        out.write("9\n");
        out.flush();
        logger.info("Game saved\n");
    }
    
}
