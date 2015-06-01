package battleship_game;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author Pavel Khokhlov
 * @date 30 May 2015
 */
public class ChatClient_Panel extends Frame implements ActionListener, Runnable
{
	private static final long serialVersionUID = 5527802362100172206L;
	
	static Button bSend_;
	TextField inputField_;
	TextArea chatField_;
	Socket socket_;
	PrintWriter printWriter_;
	BufferedReader bufferedReader_;
	Thread thread_;
	Frame exitFrame_;
	Button bExit_;
	JPanel clientPanel_;
	String server_PC_Name_;
	boolean stayRunning_ = true;
	boolean connected = false;
	
	String messageToSend_;

	public ChatClient_Panel(String server_PC_Name)
	{
		JFrame waitingConWindow = new JFrame();
		JLabel label1 = new JLabel("Waiting for server...");
		waitingConWindow.setLayout(new BorderLayout());
		waitingConWindow.add(label1, BorderLayout.CENTER);
		JButton bExit = new JButton();
		bExit.setText("Cancel");
		bExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		waitingConWindow.add(bExit, BorderLayout.SOUTH);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		waitingConWindow.setLocation(dim.width / 2 - waitingConWindow.getSize().width / 2, dim.height / 2 - waitingConWindow.getSize().height / 2);
		waitingConWindow.pack();
		waitingConWindow.setSize(130, 75);
		waitingConWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		waitingConWindow.setVisible(true);
		server_PC_Name_ = server_PC_Name;
		// client panel setup
		clientPanel_ = new JPanel();
		clientPanel_.setLayout(new FlowLayout());
		clientPanel_.setBackground(Color.blue);
		// send button setup
		bSend_ = new Button("send");
		bSend_.setBackground(Color.gray);
		// field size setup
		inputField_ = new TextField(15);
		inputField_.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == (KeyEvent.VK_ENTER))
				{
					send();
				}
			}

			@Override
			public void keyReleased(KeyEvent e){}

			@Override
			public void keyTyped(KeyEvent e){}
			
		});
		chatField_ = new TextArea(20, 30);
		chatField_.setBackground(Color.WHITE);
		clientPanel_.add(inputField_);
		clientPanel_.add(bSend_);
		clientPanel_.add(chatField_);
		setFont(new Font("Arial", Font.BOLD, 20));
		chatField_.append("Awaiting connection..." + "\n");
		clientPanel_.validate();
		// socket and stream setup
		connected = false;
		while(!connected)
		{
			try
			{
				socket_ = new Socket(server_PC_Name_, ChatServer_Panel.PORT);
				bufferedReader_ = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
				printWriter_ = new PrintWriter(socket_.getOutputStream(), true);
				connected = true;
			}
			catch(ConnectException ce)
			{
				waitingConWindow.setVisible(false);
				JFrame frame = new JFrame();
				String s = (String)JOptionPane.showInputDialog(
	                    frame,
	                    "Failed to connect.\n" 
	                    + "Hit ok to retry the connection.\n"
	                    + "Hit cancel to quit the game.",
	                    "Battleship: Failed to connect",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    null,
	                    server_PC_Name_);
				
				if(s == null)
					System.exit(0);
				else
				{
					waitingConWindow.setVisible(true);
					server_PC_Name_ = s;
					continue;
				}
			}
			catch(java.net.UnknownHostException e)
			{
				waitingConWindow.setVisible(false);
				JFrame frame = new JFrame();
				String s = (String)JOptionPane.showInputDialog(
	                    frame,
	                    "Host not found.\n"
	                    + "Hit ok to retry the connection.\n"
	                    + "Hit cancel to quit the game.",
	                    "Battleship: Host not found",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    null,
	                    server_PC_Name_);
				if(s == null)
					System.exit(0);
				else
				{
					waitingConWindow.setVisible(true);
					server_PC_Name_ = s;
					continue;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		waitingConWindow.setVisible(false);
		thread_ = new Thread(this);
		thread_.setDaemon(true);
		thread_.start();
		chatField_.append("Connected!" + "\n");
		bSend_.addActionListener(this);
	}

	/**
	 *  Method called when send button is pressed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		send();
	}
	
	public void send()
	{
		printInput();
		clearInput();
	}
	
	/**
	 * write the value of textfield into PrintWriter
	 */
	public void printInput()
	{
		chatField_.append("me: " + inputField_.getText() + "\n");
		printWriter_.println(inputField_.getText());
	}
	
	/**
	 * cleans the input textfield
	 */
	public void clearInput()
	{
		inputField_.setText("");
	}
	
	public void showExitWindow()
	{
		JFrame parent = new JFrame();
        JButton button = new JButton();
        
        button.setText("Your opponent has disconnected.");
        parent.setTitle("Battleship");
        parent.add(button);
        parent.setSize(300, 75);
        parent.setVisible(true);
        parent.setLocationRelativeTo(null);

        button.addActionListener(new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
            	
                System.exit(0);
            }
        });
	}
	
	public void closeAndExit()
	{
		try
		{
			socket_.getOutputStream().close();
			socket_.getInputStream().close();
			System.exit(0);
		} 
		catch (IOException e)
		{
			showExitWindow();
		}
		
	}

	public void run()
	{
		while (stayRunning_)
		{
			try
			{
				messageToSend_ = bufferedReader_.readLine();
				if(messageToSend_ == null)
				{
					stayRunning_ = false;
					showExitWindow();
				}
				else
					chatField_.append("Opponent: " + messageToSend_ + "\n"); // adds to text area
			}
			catch (Exception e)
			{
				e.printStackTrace();
				stayRunning_ = false;
				showExitWindow();
			}
		}
	}
}
