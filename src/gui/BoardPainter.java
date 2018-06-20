package gui;

import javax.swing.JOptionPane;

import gui.Window.Space;
import model.*;
import model.Board.State;

public class BoardPainter implements GameListener {
	
	Window w;
	
	/** Creates an instance of the board printer for a Window w*/
	public BoardPainter(Window w) {
		this.w = w;
	}
	
	/** Updates and repaints the board every time game has changed	 */
	@Override
	public void gameChanged(Game g) {
		//TODO
		
		Board b = g.getBoard();
		
		Line wins;
		if ( b.getState() == State.HAS_WINNER ) {
			wins = b.getWinner().line;
		}
		else {
			wins = null;
		}
	
		for (int row = 0; row < Board.NUM_ROWS; row++) {
			for (int col = 0; col < Board.NUM_COLS; col++) {
				//if the space has been played
				if (b.get(row,col) != null) {
					//w.currentPlayer = b.get(row,col);
					if (wins != null && wins.contains(row,col)) {
						w.getSpace(row, col).winningSpace = true;
					}
					if (b.get(row,col) == Player.X) {
						w.getSpace(row, col).playedByX = true;
					}
					else { //Player that played is PlayerO
						w.getSpace(row, col).playedByO = true;

					}
					w.getSpace(row, col).paintImmediately(0, 0,
							w.getSpace(row, col).getWidth(),
							w.getSpace(row, col).getHeight());
				}
			}
		}
		
		//changes the text to indicate state of game
		switch(g.getBoard().getState()) {
		case HAS_WINNER:
			w.winner.setText(g.getBoard().getWinner().winner + " wins!");
			break;
		case DRAW:
			w.winner.setText("Game ended in a draw!");
			break;
		case NOT_OVER:
			w.winner.setText("It is " + g.nextTurn() + "'s turn");
		}
		
		
		
		

		
	}
	
	
	

}
