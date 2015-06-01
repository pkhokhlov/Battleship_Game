package battleship_game;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Pavel Khokhlov
 * @date 30 May 2015
 */
public class ChatServer_Panel extends Frame implements ActionListener, Runnable
{

	private static final long serialVersionUID = -7340026324999863674L;
	public static final int PORT = 1337;
	
	TextField inputField_;
	TextArea chatField_;
	Button bSend_;
	ServerSocket serverSocket_;
	Socket socket_;
	PrintWriter printWriter_;
	BufferedReader bufferedReader_;
	Thread thread_;
	JPanel serverPanel_;
	
	String messageToSend_;
	
	boolean stayRunning_ = true;

	public ChatServer_Panel()
	{
		// frame setup
		serverPanel_ = new JPanel();
		serverPanel_.setLayout(new FlowLayout());
		serverPanel_.setBackground(Color.BLUE);
		// button setup
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
		// adds all components to the frame
		setFont(new Font("Arial", Font.BOLD, 20));
		serverPanel_.add(inputField_);
		serverPanel_.add(bSend_);
		serverPanel_.add(chatField_);
		setFont(new Font("Arial", Font.BOLD, 20));
		serverPanel_.validate();
		chatField_.append("Awaiting connection..." + "\n");
		// socket and stream setup
		try
		{
			serverSocket_ = new ServerSocket(ChatServer_Panel.PORT);
			socket_ = serverSocket_.accept();
			System.out.println(socket_);
			bufferedReader_ = new BufferedReader(new InputStreamReader(socket_.getInputStream()));
			printWriter_ = new PrintWriter(socket_.getOutputStream(), true);
		} 
		catch (Exception e)
		{
		}
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
	
	/**
	 * cleans the input textfield
	 */
	public void clearInput()
	{
		inputField_.setText("");
	}
	
	public void closeAndExit()
	{
		try
		{
			socket_.getOutputStream().close();
			socket_.getInputStream().close();
			serverSocket_.close();
			System.exit(0);
		}
		catch(Exception e)
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
					chatField_.append("Opponent: " + messageToSend_ + "\n");
			} 
			catch (Exception e)
			{
				stayRunning_ = false;
				showExitWindow();
			}
		}
	}
}
