/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Board;

import Game.*;
import Pieces.*;
import Utils.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.*;

/**
 * class create and contain chess board
 * 
 * @author chern
 */
public class Board {
    
	private Logger logger = Logger.getLogger(Board.class.getName());
    
    /** game class reference. */
    private LocalGame game;
    
    /** variable for manual arrangement of figures. */
    private boolean manualReplacement;
    
    /** array of cells {@link Cell#Cell(int, int)}. */
    private Cell[][] board; //was static
   
    /** constructor - create a new object. */
    public Board(LocalGame game) {
        this.game = game;
        this.manualReplacement = false;
        this.setBoard();
        logger.info("Board created\n");
    }
    
    /**
     * fill array of cells 
     * @see Board#board 
     */
    public void setBoard() {
        board = new Cell[8][8];
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                board[y][x] = new Cell(y, x);
                
            }
        }
        board[0][0].addPiece(new Rook(false, Textures.black_rook, this));
        board[0][7].addPiece(new Rook(false, Textures.black_rook, this));
        board[7][0].addPiece(new Rook(true, Textures.white_rook, this));
        board[7][7].addPiece(new Rook(true, Textures.white_rook, this));
        
        board[0][1].addPiece(new Knight(false, Textures.black_knight, this));
        board[0][6].addPiece(new Knight(false, Textures.black_knight, this));
        board[7][1].addPiece(new Knight(true, Textures.white_knight, this));
        board[7][6].addPiece(new Knight(true, Textures.white_knight, this));
        
        board[0][2].addPiece(new Bishop(false, Textures.black_bishop, this));
        board[0][5].addPiece(new Bishop(false, Textures.black_bishop, this));
        board[7][2].addPiece(new Bishop(true, Textures.white_bishop, this));
        board[7][5].addPiece(new Bishop(true, Textures.white_bishop, this));
        
        board[0][3].addPiece(new Queen(false, Textures.black_queen, this));
        board[7][3].addPiece(new Queen(true, Textures.white_queen, this));
        
        board[0][4].addPiece(new King(false, Textures.black_king, this));
        board[7][4].addPiece(new King(true, Textures.white_king, this));
        Piece.updateKingsCoordinates(7, 4, 0, 4);
        
        for(int l = 0; l < 8; l++) {
            board[1][l].addPiece(new Pawn(false, Textures.black_pawn, this, game));
            board[6][l].addPiece(new Pawn(true, Textures.white_pawn, this, game));
        }
    }
    
    /** 
     * fill array of cells @see Board#board, needed for loading game
     * @param board - existing array of cells 
     * @see Board#board
     */
    public void setBoard(Cell[][] board) {
        this.board = board;
        game.updateBoard();
    }
    
    /**
     * function return cell
     * @see Board#board
     * @param Y - y-coordinate of board
     * @param X - x-coordinate of board
     * @return needed cell
     */
    public Cell getCell(int Y, int X) { //was static
        return board[Y][X];
    }
    
    /**
     * the function returns true if there is a figure at the given coordinate
     * @param Y - y-coordinate of board
     * @param X - x-coordinate of board
     * @return confirmation of the presence of a figure
     */
    public boolean isPiece(int Y, int X) { //was static
        return (board[Y][X].getPiece() != null);
    }
    
    /**
     * the function returns true if there is a figure at the given coordinate is enemy
     * @param color - color of checker
     * @param Y - y-coordinate of board
     * @param X - x-coordinate of board
     * @return confirmation of the presence of enemy
     */
    public boolean isEnemy(boolean color, int Y, int X) { //was static
        if(isPiece(Y, X))
            return (board[Y][X].getPiece().getColor() != color);
        else
            return false;
    }
    
    /**
     * the function returns true if there is a figure what can move in this turn
     * @param fromY - y-coordinate of piece
     * @param fromX - x-coordinate of piece
     * @return confirmation of the possibility of move
     */
    public boolean canMove(int fromY, int fromX) {
        return (isPiece(fromY, fromX)) && (game.getCurrentTurn() == game.getCurrentColor()) && (game.getCurrentTurn() == board[fromY][fromX].getPiece().getColor());
    }
    
    /**
     * implementation of moving chess pieces
     * @param fromY - y-coordinate where is the move coming from
     * @param fromX - x-coordinate where is the move coming from
     * @param toY - y-coordinate where the move is going
     * @param toX - x-coordinate where the move is going
     * @param msg - parameter for pawns turning in game vs bot and online game
     */
    public void executeMove(int fromY, int fromX, int toY, int toX, String msg) {
        boolean move = false;
        ArrayList<int[]> posMoves = board[fromY][fromX].getPiece().possibleMoves(fromY, fromX);
        
        for(int i = 0; i < posMoves.size(); i++) {
            move = posMoves.get(i)[0] == toY && posMoves.get(i)[1] == toX;
            if(move) break;
        }
        
        boolean castlingLeft = (board[fromY][fromX].getPiece().getType() == Piece.Type.king) && (fromY == toY) &&
                                (toX == fromX-2);
        boolean castlingRight = (board[fromY][fromX].getPiece().getType() == Piece.Type.king) && (fromY == toY) &&
                                (toX == fromX+2);
        boolean enPassant = (board[fromY][fromX].getPiece().getType() == Piece.Type.pawn) && (Math.abs(fromY-toY) == 1) &&
                            (Math.abs(fromX-toX) == 1) && (isPiece(fromY, toX)) && 
                            (board[fromY][toX].getPiece().getType() == Piece.Type.pawn) && (!isPiece(toY, toX));
        boolean pawnsTurning = (board[fromY][fromX].getPiece().getType() == Piece.Type.pawn) && (toY == 7 || toY == 0);
        
        if(move) {
            board[toY][toX].remPiece();
            board[toY][toX].addPiece(board[fromY][fromX].getPiece());
            board[fromY][fromX].remPiece();
            if(castlingLeft) {
                board[toY][toX+1].addPiece(board[toY][0].getPiece());
                board[toY][0].remPiece();
            } else if(castlingRight) {
                board[toY][toX-1].addPiece(board[toY][7].getPiece());
                board[toY][7].remPiece();
            }
            if(enPassant) {
                board[fromY][toX].remPiece();
            }
            if(pawnsTurning) {
                String pieceName;
                if(msg.contains("bot")) { // if turn executed by bit, choice wil be random
                    String[] pawnsTur = {"bishop", "knight", "queen", "rook"};
                    Random rnd = new Random();
                    pieceName = pawnsTur[rnd.nextInt(pawnsTur.length)];
                }
                else if(msg.contains("local")) {
                    pieceName = game.menuOfChangePawn();
                }
                else {
                    pieceName = msg;
                }
                if(board[toY][toX].getPiece().getColor()) {
                    if(pieceName.equals("bishop")) board[toY][toX].addPiece(new Bishop(true, Textures.white_bishop, this));
                    else if(pieceName.equals("knight")) board[toY][toX].addPiece(new Knight(true, Textures.white_knight, this));
                    else if(pieceName.equals("queen")) board[toY][toX].addPiece(new Queen(true, Textures.white_queen, this));
                    else if(pieceName.equals("rook")) board[toY][toX].addPiece(new Rook(true, Textures.white_rook, this));
                } else {
                    if(pieceName.equals("bishop")) board[toY][toX].addPiece(new Bishop(false, Textures.black_bishop, this));
                    else if(pieceName.equals("knight")) board[toY][toX].addPiece(new Knight(false, Textures.black_knight, this));
                    else if(pieceName.equals("queen")) board[toY][toX].addPiece(new Queen(false, Textures.black_queen, this));
                    else if(pieceName.equals("rook")) board[toY][toX].addPiece(new Rook(false, Textures.black_rook, this));
                }
            }
            board[toY][toX].getPiece().controlStatus(fromY, fromX, toY, toX, //
                                       game.getNumberOfTurns(board[toY][toX].getPiece().getColor()));
            String log = " ~ ";
            log += (board[toY][toX].getPiece().getColor() ? "white " : "black ");
            log += pawnsTurning ? "pawn" : board[toY][toX].getPiece().getType();
            log += " from " + (char)(fromX+65) + Integer.toString(8 - fromY);
            log += " to " + (char)(toX+65) + Integer.toString(8 - toY);
            log += pawnsTurning ? ("\n(turning to " + board[toY][toX].getPiece().getType() + ")\n") : "\n";
            game.addLog(log);
            boolean mate = false;
            if(Piece.isCheck()) {
                game.addLog(" ~ check ~\n");
                mate = itIsMate();
            }
            if(!mate) game.nextTurn();
            logger.info("Execute move\n");
        }
    }
    
    /**
     * implementation of manual arragement of chess pieces
     * @param fromY - y-coordinate where is the move coming from
     * @param fromX - x-coordinate where is the move coming from
     * @param toY - y-coordinate where the move is going
     * @param toX - x-coordinate where the move is going
     * @param log - if needed to forward movement information
     */
    public void manualReplacement(int fromY, int fromX, int toY, int toX, boolean log) {
        if(fromY == toY && fromX == toX || !isPiece(fromY, fromX)) return;
        board[toY][toX].remPiece();
        board[toY][toX].addPiece(board[fromY][fromX].getPiece());
        board[fromY][fromX].remPiece();
        if(board[toY][toX].getPiece().getType() == Piece.Type.king) {
            Piece.updateKingsCoordinates(board[toY][toX].getPiece().getColor(), toY, toX);
        }
        if(log) game.addLog("change " + fromY+""+fromX+""+toY+""+toX+"\n");
        logger.info("Change position: " + fromY+""+fromX+""+toY+""+toX+"\n");
    } 
    
    /**
     * activate the ability to manually move chess 
     * @see Board#manualReplacement(int, int, int, int, boolean) 
     */
    public void activateManualReplacement() {
        manualReplacement = true;
        logger.info("Manual replacement activated\n");
    }
    
    /**
     * deactivate the ability to manually move chess 
     * @see Board#manualReplacement(int, int, int, int, boolean) 
     */
    public void deactivateManualReplacement() {
        manualReplacement = false;
        logger.info("Manual replacement deactivated\n");
    }
    
    /**
     * returns true if ability to manually move chess was activated
     * @see Board#manualReplacement(int, int, int, int, boolean) 
     */
    public boolean replaceMode() {
        return manualReplacement;
    }
    
    /**
     * returns forbidden cells for the king
     * @param color - color of the king
     * @return ArrayList of forbidden cells for the king
     */
    public ArrayList<int[]> movesForKing(boolean color) { //was static
        ArrayList<int[]> movesForKing = new ArrayList<int[]>();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(isEnemy(color, i, j) && board[i][j].getPiece().getType() != Piece.Type.king) {
                    if(board[i][j].getPiece().getType() == Piece.Type.pawn) {
                        if(board[i][j].getPiece().getColor()) {
                            movesForKing.add(new int[]{i-1, j+1});
                            movesForKing.add(new int[]{i-1, j-1});
                        } else {
                            movesForKing.add(new int[]{i+1, j+1});
                            movesForKing.add(new int[]{i+1, j-1});
                        }
                    } else {
                        movesForKing.addAll(board[i][j].getPiece().possibleMoves(i, j));
                    }
                }
                else if(isEnemy(color, i, j) && board[i][j].getPiece().getType() == Piece.Type.king) {
                    // to avoid recursion
                    int[][] vectors = new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                    for(int[] vec: vectors) {
                        movesForKing.add(new int[]{i + vec[0], j + vec[1]});
                    }
                }
            }
        }
        return movesForKing;
    }
    
    /**
     * provides information for the save function
     * @return game board 
     * @see Board#board
     */
    public Cell[][] getInfoForSave() {
        return board;
    }
    
    /**
     * reports information about the impossibility to make a move
     * @return true if there are no more moves
     */
    public boolean itIsMate() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(Piece.isCheck()) {
                    if(isPiece(i, j) && board[i][j].getPiece().getColor() == Piece.checkColor()) {
                        moves.addAll(board[i][j].getPiece().possibleMoves(i, j));
                    }
                }
                else {
                    if(isPiece(i, j) && board[i][j].getPiece().getColor() == game.getCurrentTurn()) {
                        moves.addAll(board[i][j].getPiece().possibleMoves(i, j));
                    }
                }
            }
        }
        if(moves.size() == 0) {
            if(Piece.isCheck()) game.end(Piece.checkColor());
            else game.end();
            logger.info("End of the game\n");
            return true;
        }
        return false;
    }
}
