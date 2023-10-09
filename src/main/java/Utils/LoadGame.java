/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Board.*;
import Game.*;
import Pieces.*;

import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.*;

/**
 *
 * @author chern
 */
public class LoadGame {

    protected Logger logger = Logger.getLogger(LoadGame.class.getName());
    
    /**
     * collects information from objects and read them from a text file
     * 
     * @param sc - a scanner that is created based on the file stream selected by the player
     * @param game - game to load
     * @param board - board from game
     * @param stopwatch - game timer to load
     * @throws FileNotFoundException 
     */
    public LoadGame(Scanner sc, LocalGame game, Board board, Stopwatch stopwatch) throws FileNotFoundException {
        
        game.modelPart(sc.nextLine().equals("white")?true:false, sc.nextInt(), sc.nextInt());
        
        stopwatch.setTimeSeparate(sc.nextInt(), sc.nextInt()); sc.nextLine();
        
        String check = sc.nextLine();
        boolean isCheck, checkColor;
        isCheck = check.equals("wh") ||  check.equals("bl") ? true : false;
        checkColor = isCheck ? (check.equals("wh") ? true : false) : false;
        
        String attackTrajectory = null;
        if(isCheck) {
            attackTrajectory = sc.nextLine();
        }
        ArrayList<int[]> att = new ArrayList<int[]>();
        
        Cell[][] newBoard = new Cell[8][8];
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                newBoard[y][x] = new Cell(y, x);
            }
        }
        
        while(sc.hasNext()) {
            int y = sc.nextInt();
            if(y == 9) break;
            int x = sc.nextInt();
            String nameAndColor = sc.nextLine();
            int sep = nameAndColor.indexOf(' ', 1);
            String name = nameAndColor.substring(1, sep);
            String color = nameAndColor.substring(sep+1);
            createPiece(name, color, newBoard[y][x], board, game);
        }
        
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                if(newBoard[y][x].getPiece() != null) {
                    if(isCheck) Piece.setCheck(checkColor);
                    else Piece.resetCheck();
                    if(isCheck) {
                        Scanner sc2 = new Scanner(attackTrajectory);
                        while(sc2.hasNextInt()) {
                            att.add(new int[]{sc2.nextInt(), sc2.nextInt()});
                        }
                        Piece.setAttackTrajectory(att);
                    }
                    break;
                    
                }
            }
        }
        
        board.setBoard(newBoard);
        logger.info("Game loaded\n");
    }
    
    /**
     * creates pieces according to the parameter specified in the file for transferring to the game
     * 
     * @param name - type of piece
     * @param color - color of piece
     * @param cell - the coordinates of the cell on which the figure is “standing”
     * @param board - reference to the game board for pieces
     */
    public void createPiece(String name, String color, Cell cell, Board board, LocalGame game) {
        if(color.equals("white")) {
            switch (name) {
                case "bishop": cell.addPiece(new Bishop(true, Textures.white_bishop, board)); break;
                case "king": cell.addPiece(new King(true, Textures.white_king, board)); break;
                case "knight": cell.addPiece(new Knight(true, Textures.white_knight, board)); break;
                case "pawn": cell.addPiece(new Pawn(true, Textures.white_pawn, board, game)); break;
                case "queen": cell.addPiece(new Queen(true, Textures.white_queen, board)); break;
                case "rook": cell.addPiece(new Rook(true, Textures.white_rook, board)); break;
            }
        }
        else {
            switch (name) {
                case "bishop": cell.addPiece(new Bishop(false, Textures.black_bishop, board)); break;
                case "king": cell.addPiece(new King(false, Textures.black_king, board)); break;
                case "knight": cell.addPiece(new Knight(false, Textures.black_knight, board)); break;
                case "pawn": cell.addPiece(new Pawn(false, Textures.black_pawn, board, game)); break;
                case "queen": cell.addPiece(new Queen(false, Textures.black_queen, board)); break;
                case "rook": cell.addPiece(new Rook(false, Textures.black_rook, board)); break;
            }
        }
    }
    
}
