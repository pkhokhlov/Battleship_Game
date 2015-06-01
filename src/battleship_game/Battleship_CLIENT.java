package battleship_game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ships.*;

/**
 * @author Pavel Khokhlov
 * @date 30 May 2015
 */
public class Battleship_CLIENT
{
	JPanel playerPanel_   = new JPanel();
	JPanel playerButtonPanel_ = new JPanel();
	JPanel opponentPanel_ = new JPanel();
	JPanel opponentButtonPanel_ = new JPanel();
	JFrame window_ = new JFrame();
	
	JButton bToggle;
	JButton bClear;
	JButton bExit;
	
	JButton[][] playerGrid_;
	JButton[][] opponentGrid_;
	
	static final int length = 10;
	static final int width  = 10;
	boolean shipPosition_ = false; // false is horizontal
	
	int timesEmpty_ = 0;
	
	ArrayList<Ship> ships = new ArrayList<Ship>();
	ArrayList<Ship> usedShips = new ArrayList<Ship>();
	
	public Battleship_CLIENT()
	{
		String text = JOptionPane.showInputDialog("Please enter the name of the PC the server is running on.");
		System.out.println(text);
		playerPanel_.setLayout(new GridLayout(width, length));
		playerPanel_.setBorder(BorderFactory.createTitledBorder("Player Board"));
		playerGrid_ = new JButton[width][length];
		for(int y = 0; y < length; y++)
		{
			for(int x = 0; x < width; x++)
			{
				playerGrid_[x][y] = new playerButton(x, y);
				playerGrid_[x][y].setBackground(Color.WHITE);
				playerPanel_.add(playerGrid_[x][y]);
				
				if(x == 0 && y > 0)
				{
					playerGrid_[x][y].setText(Integer.toString(y));
				}
				else if(y == 0 && x > 0)
				{
					playerGrid_[x][y].setText(Character.toString((char)(x + 64)));
				}
				else if(y != 0 && x != 0)
				{
					playerGrid_[x][y].addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e){placeShip(e);}
					});
				}
			}
		}
		
		bToggle = new JButton("Toggle Orientation");
		bToggle.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                shipPosition_ = !shipPosition_;
                
                if(shipPosition_)
                	bToggle.setText("Vertical");
                else
                	bToggle.setText("Horizontal");
            }
        });
		playerButtonPanel_.add(bToggle);
		
		bClear = new JButton("Clear Boards");
		bClear.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                clearBoard();
            }
        });
		playerButtonPanel_.add(bClear);
		
		fillShipArrayList();
		
		opponentPanel_.setLayout(new GridLayout(width, length));
		opponentPanel_.setBorder(BorderFactory.createTitledBorder("Opponent Board"));
		opponentGrid_ = new JButton[width][length];
		for(int y = 0; y < length; y++)
		{
			for(int x = 0; x < width; x++)
			{
				opponentGrid_[x][y] = new playerButton(x, y);
				opponentGrid_[x][y].setBackground(Color.WHITE);
				opponentPanel_.add(opponentGrid_[x][y]);
				
				if(x == 0 && y > 0)
				{
					opponentGrid_[x][y].setText(Integer.toString(y));
				}
				else if(y == 0 && x > 0)
				{
					opponentGrid_[x][y].setText(Character.toString((char)(x + 64)));
				}
				else if(y != 0 && x != 0)
				{
					opponentGrid_[x][y].addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e){changeOpponentButtonColor(e);}
					});
				}
			}
		}
		ChatClient_Panel clientP = new ChatClient_Panel(text);
		
		bExit = new JButton("Exit");
		bExit.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                clientP.closeAndExit();
            }
        });
		playerButtonPanel_.add(bExit);
		
		BorderLayout windowBL = new BorderLayout();
		window_.setTitle("Battleship Client");
		window_.setLayout(windowBL);
		window_.add(playerPanel_, BorderLayout.WEST);
		window_.add(opponentPanel_, BorderLayout.CENTER);
		window_.add(clientP.clientPanel_, BorderLayout.EAST);
		window_.add(playerButtonPanel_, BorderLayout.SOUTH);
		window_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window_.pack(); // sets size for frame
		window_.setVisible(true);
		window_.setResizable(false);
	}
	
	public void placeShip(ActionEvent e)
	{
		if(ships.size() == 0)
		{
			timesEmpty_++;
			if(timesEmpty_ == 1)
				JOptionPane.showMessageDialog(window_, "You have placed all of your ships.");
			changePlayerButtonColor(e);
			return;
		}
		
		JButton b = ((JButton)e.getSource());
		playerButton playerb = (playerButton)b;
		
		int x = playerb.xpos_; // the x coordinate of the top of the ship
		int y = playerb.ypos_; // the y coordinate of the top of the ship
		
		if(checkIfValidPlace(x, y, ships.get(0).getSize()))
		{
			placeCheckedShip(x, y, ships.get(0).getSize());
			usedShips.add(ships.remove(0));
		}
		else
		{
			JOptionPane.showMessageDialog(window_, "Please put the ship in a valid place.");
		}
	}
	
	public void changePlayerButtonColor(ActionEvent e)
	{
		JButton b = ((JButton)e.getSource());
		playerButton playerb = (playerButton)b;
		
		if(!playerb.getBackground().equals(Color.WHITE) && !playerb.getBackground().equals(Color.RED))
		{
			playerb.prevColor = playerb.getBackground();
			playerb.setBackground(Color.RED);
		}
		else if(playerb.getBackground().equals(Color.RED))
		{
			playerb.setBackground(playerb.prevColor);
		}
	}
	
	public void changeOpponentButtonColor(ActionEvent e)
	{
		if(((JButton)e.getSource()).getBackground().equals(Color.WHITE))
		{
			((JButton)e.getSource()).setBackground(Color.RED);
		}
		else if(((JButton)e.getSource()).getBackground().equals(Color.RED))
		{
			((JButton)e.getSource()).setBackground(Color.GRAY);
		}
		else if(((JButton)e.getSource()).getBackground().equals(Color.GRAY))
		{
			((JButton)e.getSource()).setBackground(Color.WHITE);
		}
	}
	
	public void fillShipArrayList()
	{
		ships.add(new AircraftCarrier("Aircraft", 5));
		
		for(int i = 1; i <= 2; i++)
		{
			ships.add(new Battleship("Battleship", 4));
		}
		
		for(int i = 1; i <= 2; i++)
		{
			ships.add(new Submarine("Submarine", 3));
		}
		
		for(int i = 1; i <= 2; i++)
		{
			ships.add(new Destroyer("Destroyer", 3));
		}
		
		for(int i = 1; i <= 4; i++)
		{
			ships.add(new PatrolBoat("Patrol", 2));
		}
	}
	
	public boolean checkIfValidPlace(int xpos, int ypos, int size)
	{	
		if(!shipPosition_) // horizontal
		{
			if(size + xpos > width)
				return false;
			
			for(int x = xpos; x < xpos + size; x++)
			{
				if(!playerGrid_[x][ypos].getBackground().equals(Color.WHITE))
					return false;
			}
		}
		else // vertical
		{
			if(size + ypos > length)
				return false;
			
			for(int y = ypos; y < ypos + size; y++)
			{
				if(!playerGrid_[xpos][y].getBackground().equals(Color.WHITE))
					return false;
			}
		}
		return true;
	}
	
	public void placeCheckedShip(int xpos, int ypos, int size)
	{
		if(!shipPosition_) // horizontal
		{
			for(int x = xpos; x < xpos + size; x++)
			{
				playerGrid_[x][ypos].setBackground(ships.get(0).getColor());
			}
		}
		else
		{
			for(int y = ypos; y < ypos + size; y++)
			{
				playerGrid_[xpos][y].setBackground(ships.get(0).getColor());
			}
		}
	}
	
	public void clearBoard()
	{
		for(int y = 0; y < length; y++)
		{
			for(int x = 0; x < width; x++)
			{
				playerGrid_[x][y].setBackground(Color.WHITE);
				opponentGrid_[x][y].setBackground(Color.WHITE);
			}
		}
		ships = new ArrayList<Ship>();
		fillShipArrayList();
		usedShips = new ArrayList<Ship>();
	}
}
