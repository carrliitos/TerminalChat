/**
 * @author Benzon Carlitos Salazar
 */

import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/* Console-based Client */
public class Client {
	private String notification = " *** "; // Notification
	private ObjectInputStream inputStream; // Read from the socket
	private ObjectOutputStream outputStream; // Write on the socket
	private Socket socket; // Socket object
	private String server; // Server
	private String username; // Username
	private int port; // port number

	/**
	 * Client constructor
	 * @param server address
	 * @param port number
	 * @param username
	 */
	Client(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

	public String getUsername() { return username; }
	public void setUsername(String username) {
		this.username = username;
	}

	/* Start the chat */
	public boolean start() {
		// Connection to Server
		try {
			socket = new Socket(server, port);
		}catch(Exception e) {
			display("\nError connecting to server: " + e);
			return false;
		}

		String msg = "Connected " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		// Create data streams
		try {
			inputStream = new ObjectInputStream(socket.getInputStream());
			outputStream = new ObjectOutputStream(socket.getOutputStream());
		}catch(IOException ioE) {
			display("\nError when creating new I/O Streams: " + ioE);
			return false;
		}

		/** 
		 * Create a thread to listen to from the Server
		 * The username is the only message we send as a string, other messages
		 * are Chat objects.
		 */
		new Listening().start();
		try {
			outputStream.writeObject(username);
		}catch(IOException ioE) {
			display("Exception during login: " + ioE);
			disconnect();
			return false;
		}
		// successful chat
		return true;
	}

	/**
	 * Send a message to console
	 */
	private void display(String msg) {
		System.out.println(msg);
	}

	/**
	 * Send a message to the Server
	 */
	void sendMessage(Chat msg) {
		try {
			outputStream.writeObject(msg);
		}catch(IOException ioE) {
			display("Exception when writing to server: " + ioE);
		}
	}

	/**
	 * Closing everything
	 */
	private void disconnect() {
		try {
			if(outputStream != null) outputStream.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			if(inputStream != null) inputStream.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}

		try {
			if(socket != null) socket.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Run Client as a console app with one of the commands
	 * java Client
	 * java Client username
	 * java Client username portNumber
	 * java Client username portNumber serverAddress
	 *
	 * default portNumber = 3000
	 * default username = "Visitor"
	 * default serverAddress = "localhost"
	 */
	public static void main(String[] args) {
		int portNumber = 3000;
		String serverAddress = "localhost";
		String username = "Visitor";
		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter username: ");
		username = scanner.nextLine();

		switch (args.length) {
			case 3 :
				serverAddress = args[2];
			case 2 :
				try {
					portNumber = Integer.parseInt(args[1]);
				}catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage: java Client [username] [port number] [server address]");
					return;
				}
			case 1 :
				username = args[0];
			case 0 :
				break;
			default : 
				System.out.println("Usage: java Client [username] [port number] [server address]");
			return;
		}

		// Client object
		Client client = new Client(serverAddress, portNumber, username);
		// if not connected, return
		if(!client.start()) 
			return;

		System.out.println("\nHello " + username + "! Welcome to the UWW chat room.");
		System.out.println("**Instructions**");
		System.out.println("1. To send a broadcast to all active members, type your message.");
		System.out.println("2. To send a private message, type '@<username> <message>'");
		System.out.println("3. To see list of active clients, type ONLINE");
		System.out.println("4. To logout, type LOGOUT");

		// Get input from user
		while(true) {
			System.out.print(">> ");
			String msg = scanner.nextLine();

			// message-based statements
			if(msg.equalsIgnoreCase("LOGOUT")){
				client.sendMessage(new Chat(Chat.LOGOUT, ""));
				break;
			}else if(msg.equalsIgnoreCase("ONLINE")) {
				client.sendMessage(new Chat(Chat.ONLINE, ""));
			}else {
				client.sendMessage(new Chat(Chat.MESSAGE, msg));
			}
		}
		scanner.close();
		client.disconnect();
	}

	/**
	 * Wait for the message from the Server
	 */
	class Listening extends Thread {
		public void run() {
			while(true) {
				try {
					// Read message from the input stream
					String msg = (String) inputStream.readObject();
					// Print the message
					System.out.print(msg);
					System.out.print(">> ");
				}catch(IOException ioE) {
					display(notification + "Server has closed: " + ioE + notification);
					break;
				}catch(ClassNotFoundException cnfe) {
					System.out.println(cnfe.getMessage());
				}
			}
		}
	}
}