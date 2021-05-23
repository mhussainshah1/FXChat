package client;
import common.ChatMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

//The client.Client that can be run as a console
public class Client {

	// notification
	private final String notif = " *** ";

	// for I/O
	private ObjectInputStream sInput;        // to read from the socket
	private ObjectOutputStream sOutput;        // to write on the socket
	private Socket socket;                    // socket object

	private final String server;
	private String username;    // main.java.server and username
	private final int port;                    //port

	public Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

	/*
	 * To start the client.Client in console mode use one of the following command
	 * > java client.Client
	 * > java client.Client username
	 * > java client.Client username portNumber
	 * > java client.Client username portNumber serverAddress
	 * at the console prompt
	 * If the portNumber is not specified 1500 is used
	 * If the serverAddress is not specified "localHost" is used
	 * If the username is not specified "Anonymous" is used
	 */
	public static void main(String[] args) {
		// default values if not entered
		int portNumber = 1436;
		String serverAddress = "localhost";
		String userName = "Anonymous";
		Scanner scan = new Scanner(System.in);

		System.out.println("Enter the username: ");
		userName = scan.nextLine();

		// different case according to the length of the arguments.
		switch (args.length) {
			case 3:
				// for > javac client.Client username portNumber serverAddr
				serverAddress = args[2];
			case 2:
				// for > javac client.Client username portNumber
				try {
					portNumber = Integer.parseInt(args[1]);
				} catch (Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java client.Client [username] [portNumber] [serverAddress]");
					return;
				}
			case 1:
				// for > javac client.Client username
				userName = args[0];
			case 0:
				// for > java client.Client
				break;
			// if number of arguments are invalid
			default:
				System.out.println("Usage is: > java client.Client [username] [portNumber] [serverAddress]");
				return;
		}
		// create the client.Client object
		Client client = new Client(serverAddress, portNumber, userName);
		// try to connect to the main.java.server and return if not connected
		if (!client.start())
			return;

		System.out.println("\nHello.! Welcome to the chatroom.");
		System.out.println("Instructions:");
		System.out.println("1. Simply type the message to send broadcast to all active clients");
		System.out.println("2. Type '@username<space>yourmessage' without quotes to send message to desired client");
		System.out.println("3. Type 'WHOISIN' without quotes to see list of active clients");
		System.out.println("4. Type 'LOGOUT' without quotes to logoff from main.java.server");

		// infinite loop to get the input from the user
		while (true) {
			System.out.print("> ");
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if (msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
				break;
			}
			// message to check who are present in chatroom
			else if (msg.equalsIgnoreCase("WHOISIN")) {
				client.sendMessage(new ChatMessage(ChatMessage.LIST, ""));
			}
			// regular text message
			else {
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
			}
		}
		// close resource
		scan.close();
		// client completed its job. disconnect client.
		client.disconnect();
	}

	/**
	 *  Constructor to set below things main.java.server: the main.java.server address  port: the port number username: the username
	 */

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/*
	 * To start the chat
	 */
	public boolean start() {
		// try to connect to the main.java.server
		try {
			socket = new Socket(server, port);
		}
		// exception handler if it failed
		catch (Exception ec) {
			display("Error connectiong to main.java.server:" + ec);
			return false;
		}

		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		/* Creating both Data Stream */
		try {
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the main.java.server
		new ListenFromServer().start();
		// Send our username to the main.java.server this is the only message that we
		// will send as a String. All other messages will be common.ChatMessage objects
		try {
			sOutput.writeObject(username);
		} catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console
	 */
	private void display(String msg) {
		System.out.println(msg);
	}

	/*
	 * To send a message to the main.java.server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		} catch (IOException e) {
			display("Exception writing to main.java.server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect
	 */
	private void disconnect() {
		try {
			if (sInput != null)
				sInput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (sOutput != null)
				sOutput.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * a class that waits for the message from the main.java.server
	 */
	class ListenFromServer extends Thread {
		public void run() {
			while (true) {
				try {
					// read the message form the input datastream
					String msg = (String) sInput.readObject();
					// print the message
					System.out.println(msg);
					System.out.print("> ");
				} catch (IOException e) {
					display(notif + "main.java.server.Server has closed the connection: " + e + notif);
					break;
				} catch (ClassNotFoundException e2) {
					e2.printStackTrace();
				}
			}
		}
	}
}