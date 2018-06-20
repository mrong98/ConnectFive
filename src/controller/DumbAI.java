package controller;

import java.util.ArrayList;
import java.util.List;
import model.Board;
import model.Game;
import model.Location;
import model.Player;

/**
 * A DumbAI is a Controller that always chooses the blank space with the
 * smallest column number from the row with the smallest row number.
 */
public class DumbAI extends Controller {

	public DumbAI(Player me) {
		super(me);
		// TODO Auto-generated constructor stub
		//throw new NotImplementedException();
	}
	
	/**returns a location with the smallest column number from the smallest row number
	 * of available locations
	 */
	protected @Override Location nextMove(Game g) {
		// Note: Calling delay here will make the CLUI work a little more
		// nicely when competing different AIs against each other.
		
		
		List<Location> avail = new ArrayList<Location>();
		for (Location l : Board.LOCATIONS) {
			if (g.getBoard().get(l) == null) { //location is available
				avail.add(l);
			}
		}
		
		delay();

		
		//locations are in order first by row, and then by column
		if (!avail.isEmpty()) {
			return avail.get(0);
		}
		return null;

	}
}
