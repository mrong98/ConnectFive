package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.BasicStroke;
import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import controller.DumbAI;
import controller.RandomAI;
import controller.SmartAI;
import model.Game;
import model.GameListener;
import model.Location;
import model.Player;

public class Window extends JFrame{

	JLabel winner; //a label that displays the winner of the game, or whose turn it is
	GameListener x; //the player who plays X
	GameListener o; //the player who plays O
	ArrayList<Space> spaces; //stores each space on the grid
	Container grid; //the grid where the spaces are placed
	boolean humanX; //true if the player who plays X is of type human
	boolean humanO; //true if the player who plays O is of type human
	boolean gameStarted; //true if the game has started, if the startGame button is pressed

	Game g; //the game that is currently being played

	JComboBox<String> playerX; //the combobox that offers options for playerX
	JComboBox<String> playerO; //the combobox that offers options for playerO

	/** an instance is the window of the five in a row game*/
	public Window() {

		//create the overall frame
		super("Five in a Row");
		setLayout(new BorderLayout());
		getContentPane().setBackground(Color.WHITE);

		addElements();

		//final setup
		setPreferredSize(new Dimension(600,500)); //set window's preferred size
		pack(); //putting everything in window

	}

	/** Adds the components into the window*/
	public void addElements() {

		//LEFTSIDE
		spaces = new ArrayList<Space>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				spaces.add(new Space(i, j));
			}
		}

		grid = new Container();
		grid.setLayout(new GridLayout(9, 9));
		for (int i = 0; i < spaces.size(); i++) {
			grid.add(spaces.get(i));
		}
		add(grid);


		//RIGHTSIDE
		Container gameInfo = new Container();
		gameInfo.setLayout(new GridLayout(9,1));

		String[] playerOptions = {"Human", "DumbAI", "RandomAI", "SmartAI"};

		//creation of jPanels
		playerX = new JComboBox<String>(playerOptions);
		JLabel vs = new JLabel("versus");
		vs.setHorizontalAlignment(SwingConstants.CENTER);
		playerO = new JComboBox<String>(playerOptions);
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new startGame());

		winner = new JLabel("");
		winner.setHorizontalAlignment(SwingConstants.CENTER);

		JButton closeWindowButton = new JButton("Close Game");
		closeWindowButton.addActionListener(new close());

		//adding of jPanels
		gameInfo.add(playerX);
		gameInfo.add(vs);
		gameInfo.add(playerO);
		gameInfo.add(startButton);
		gameInfo.add(winner);
		gameInfo.add(closeWindowButton);

		add(gameInfo, BorderLayout.EAST);
	}

	/** returns the players selected in the combobox */
	public GameListener getPlayer(String selected, Player p) {
		if (selected.equals("DumbAI")) return new DumbAI(p);
		else if (selected.equals("RandomAI")) return new RandomAI(p);
		else if (selected.equals("SmartAI")) return new SmartAI(p);
		return null; //selected Human
	}

	/** begins the game when button startGame is pressed */
	class startGame implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {			
			x = getPlayer((String)(playerX.getSelectedItem()), Player.X);
			o = getPlayer((String)(playerO.getSelectedItem()), Player.O);

			g = new Game();
			g.addListener(new BoardPainter(Window.this));

			//only add as listeners if not a human
			if (x != null) { //not a human
				g.addListener(x);
			}
			else {
				humanX = true;
			}
			if (o != null) {
				g.addListener(o);
			}
			else {
				humanO = true;
			}
			
			gameStarted = true;


		}
	}

	/** closes the window when closeWindow button is pressed*/
	class close implements ActionListener { 
		@Override
		public void actionPerformed(ActionEvent e) {
			Window.this.dispose();
		}
	}

	/** returns the space at row i and column j */
	public Space getSpace(int i, int j) {
		for (Space s : spaces) {
			if (s.i == i && s.j == j) {
				return s;
			}
		}
		return null;
	}

	/** the Spaces on the grid that can be played */
	class Space extends JPanel {
		public int i; //row of this space
		public int j; //column of this space
		boolean playedByX; // true if this space has been played by playerX
		boolean playedByO; // true if this space has been played by playerO
		boolean hovered; //true if the mouse has hovered over the space
		boolean winningSpace; //true if this space is a part of the winning line

		/** an instance is a JPanel on the grid with row i and column j,
		 * where a player can place moves */
		public Space(int i, int j) {
			this.i = i;
			this.j = j;
			playedByX = false;
			playedByO = false;
			hovered = false;
			winningSpace = false;

			addMouseListener(new Listener());
			setPreferredSize(new Dimension(100,100));
			setBackground(Color.WHITE);
		}

		/** an instance where paint information is updated when a mouseEvent occurs*/
		class Listener extends MouseInputAdapter {
			@Override
			/** information is updated when mouse clicked */
			public void mouseClicked(MouseEvent e) {

				if ( (g.nextTurn() == Player.X) && humanX ) {
					g.submitMove(Player.X, new Location(i, j));
					playedByX = true;
				}

				else if ( (g.nextTurn() == Player.O) && humanO ) {
					g.submitMove(Player.O, new Location(i, j));
					playedByO = true;
				}
				repaint();
			}

			/** information is updated when mouse hovers over a space */
			@Override
			public void mouseEntered(MouseEvent e) {
				if (gameStarted) hovered = true;
				repaint();
			}

			/** information is updated when mouse exits the space */
			@Override
			public void mouseExited(MouseEvent e) {
				if (gameStarted) hovered = false;
				repaint();
			}

		}

		/** updates the visual of the space:
		 * - if played by PlayerX, will have X painted over space
		 * - if played by PlayerO, will have O painted over space
		 * - if an empty space and hovered over,
		 *   will have the symbol of the Player who will be playing next move
		 * - if a part of a winning line, symbol will be red
		 */
		public void paint(Graphics g1) { //this will be diff if u link to the game
			g1.setColor(Color.BLACK);
			g1.drawRect(0, 0, getWidth(), getHeight());

			if (winningSpace) {
				g1.setColor(Color.RED);
			}

			if (playedByX) {
				Graphics2D g2 = (Graphics2D) g1;
				g2.setStroke(new BasicStroke(3));

				g2.drawLine(2, 2, getWidth()-2, getHeight()-2);
				g2.drawLine(getWidth()-2, 2, 2, getHeight()-2);
			}

			if (playedByO) {
				Graphics2D g2 = (Graphics2D) g1;
				g2.setStroke(new BasicStroke(3));
				g2.drawArc(2, 2, getWidth()-2, getHeight()-2, 0, 360);
			}

			if (hovered && !playedByX && !playedByO) {
				g1.setColor(Color.GRAY);

				if (g.nextTurn() == Player.X && humanX ) {
					g1.drawLine(2, 2, getWidth()-2, getHeight()-2);
					g1.drawLine(getWidth()-2, 2, 2, getHeight()-2);
				}
				if (g.nextTurn() == Player.O && humanO) {
					g1.drawArc(2, 2, getWidth()-2, getHeight()-2, 0, 360);
				}
			}
		}
	}
}