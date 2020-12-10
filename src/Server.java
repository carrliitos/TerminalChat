/**
 * @author Benzon Carlitos Salazar
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;

/* Console-based Server */
public class Server {
	private static int uniqueID;	// Unique ID for each connection
	private ArrayList<ClientThread> clientList;	// Array list of Clients
	private SimpleDateFormat sdf;	// Display time
	private int port;	// Port number to listen for connections
	private boolean serverIsRunning;	// Checking to see if server is running
	private String notification = " *** ";	// Notification

	/**
	 * Server constructor to listen to for connection
	 * @param port is the post to listen to
	 */
	public Server(int port) {
		this.port = port;
		sdf = new SimpleDateFormat("HH:mm:ss");
		clientList = new ArrayList<ClientThread>();
	}

	/**
	 * We start the socket server and wait for connection requests
	 */
	public void start() {
		serverIsRunning = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			// loop to wait for connections
			while(serverIsRunning) {
				display("Server waiting for Clients on port " + port + ".");
				// accept connect if requested from the Client
				Socket socket = serverSocket.accept();
				
				// break if the Server has stopped
				if(!serverIsRunning) break;

				// if Client is connected, create its thread, and add to arraylist
				ClientThread t = new ClientThread(socket);
				//add this client to arraylist and start
				clientList.add(t);
				t.start();
			}

			// Try to stop the Server
			try {
				serverSocket.close();
				for(int i = 0; i < clientList.size(); ++i) {
					ClientThread tc = clientList.get(i);
					try {
						// Close all data stream and socket
						tc.inputStream.close();
						tc.outputStream.close();
						tc.socket.close();
					}catch(IOException ioE) {
						System.out.println(ioE.getMessage());
					}
				}
			}catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}catch(IOException ioE) {
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + ioE + "\n";
			display(msg);
		}
	}

	/**
	 * Displays event on console
	 */
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		System.out.println(time);
	}

	/**
	 * Stops the server
	 */
	protected void stop() {
		serverIsRunning = false;
		try {
			new Socket("localhost", port);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Broadcasts a message to all clients
	 */
	private synchronized boolean broadcast(String message) {
		// timestamp
		String time = sdf.format(new Date());
		// Check if the message is private
		String[] w = message.split(" ",3);

		boolean isPrivate = false;
		if(w[1].charAt(0) == '@') 
			isPrivate = true;

		// if message is private, send the message to the mentioned user only
		if(isPrivate == true) {
			String toCheck = w[1].substring(1, w[1].length());
			message = w[0] + w[2];
			String messageLF = time + " " + message + "\n";
			boolean found = false;

			// we loop in reverse order to find the mentioned username
			for(int i = clientList.size(); --i >= 0;) {
				ClientThread thread1 = clientList.get(i);
				String check = thread1.getUsername();
				
				if(check.equals(toCheck)) {
					// write to the Client, if fails, remove it from the list
					if(!thread1.writeMessage(messageLF)) {
						clientList.remove(i);
						display("Disconnected Client " + thread1.username + " removed from list.");
					}

					// username was found and message delivered
					found = true;
					break;
				}
			}

			// return false if mentioned user was not found
			if(found != true) { return false; }
		}else {
			String messageLF = time + " " + message + "\n";
			// display the message
			System.out.print(messageLF);

			// we loop in case we have to remove a Client due to disconnection
			for(int i = clientList.size(); --i >= 0;) {
				ClientThread thread = clientList.get(i);

				// write to the Client, if fails, remove it from the list
				if(!thread.writeMessage(messageLF)) {
					clientList.remove(i);
					display("Disconnected Client " + thread.username + " removed from list.");
				} 
			}
		}

		return true;
	}

	/**
	 * Remove client if they sent LOGOUT message to exit
	 */
	synchronized void remove(int id) {
		String disconnectedClient = "";

		// scan entire client list until ID is found
		for(int i = 0; i < clientList.size(); ++i) {
			ClientThread clientThread = clientList.get(i);

			// if found, remove
			if(clientThread.id == id) {
				disconnectedClient = clientThread.getUsername();
				clientList.remove(i);
				break;
			}
		}

		broadcast(notification + disconnectedClient + " has left the chat room." + notification);
	}

	/**
	 * Run Server as a console app with one of the commands
	 * java Server
	 * java Server portNumber
	 * 
	 * default portNumber = 3000
	 */
	public static void main(String[] args) {
		// start the server on port 3000, unless specified
		int portNumber = 3000;
		switch(args.length) {
			case 1 : 
				try {
					portNumber = Integer.parseInt(args[0]);
				}catch(Exception e) {
					System.out.println("\nInvalid port number.");
					System.out.println("Usage: java Server [port number]");
					return;
				}
			case 0 : break;
			default : 
				System.out.println("Usage: java Server [port number]");
				return;
		}

		Server server = new Server(portNumber);
		server.start();
	}

	/**
	 * One instance of this thread will run for each client
	 */
	class ClientThread extends Thread {
		ObjectInputStream inputStream;
		ObjectOutputStream outputStream;
		Socket socket; // Socket to get messages from the client
		int id; // unique ID
		String username; // Client username
		Chat chatMessage; // message object to receive message and its type
		String date; // timestamp

		ClientThread(Socket socket) {
			id = ++uniqueID;
			this.socket = socket;

			// Create both data streams
			System.out.println("Creating Object I/O Streams.");
			try {
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream  = new ObjectInputStream(socket.getInputStream());
				// read the username
				username = (String) inputStream.readObject();
				broadcast(notification + username + " has joined." + notification);
			}catch(IOException ioE) {
				display("\nException creating new I/O Streams: " + ioE);
				return;
			}catch(ClassNotFoundException cnfe) {
				System.out.println(cnfe.getMessage());
			}
			date = new Date().toString() + "\n";
		}

		public String getUsername() { return username; }
		public void setUsername(String username) { this.username = username; }

		// Infinite loop to read and forward messages
		public void run() {
			// Loop until LOGOUT
			boolean serverIsRunning = true;

			while(serverIsRunning) {
				// read a string
				try {
					chatMessage = (Chat) inputStream.readObject();
				}catch(IOException ioE) {
					display("Exception reading Stream: " + ioE);
					break;
				}catch(ClassNotFoundException cnfe) {
					System.out.println(cnfe.getMessage());
					break;
				}

				// get the message
				String message = chatMessage.getMessage();

				// Different actions based on the message
				switch(chatMessage.getType()) {
					case Chat.MESSAGE :
						boolean confirmation = broadcast(username + ": " + message.toUpperCase());
						if(confirmation == false){
							String msg = notification + "Sorry. No such user exists." + notification;
							writeMessage(msg);
						}
						break;
					case Chat.LOGOUT :
						display(username + " disconnected with a LOGOUT message.");
						serverIsRunning = false;
						break;
					case Chat.ONLINE : 
						writeMessage("List of users connected at " + sdf.format(new Date()) + "\n");
						
						// send list of active Clients
						for(int i = 0; i < clientList.size(); ++i) {
							ClientThread clientThread = clientList.get(i);
							writeMessage((i + 1) + ") " + clientThread.username + " since " + clientThread.date);
						}
						break;
				}
			}
			// disconnect and remove the client list
			remove(id);
			close();
		}

		/**
		 * Closing everything
		 */
		private void close() {
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
		 * Write to the Client output stream
		 */
		private boolean writeMessage(String msg) {
			// if Client is still connected, send the message
			if(!socket.isConnected()) {
				close();
				return false;
			}

			// Write message to the stream
			try {
				outputStream.writeObject(msg);
			}catch(IOException ioE) {
				display(notification + "Error sending message to " + username + notification);
				display(ioE.toString());
			}
			return true;
		}
	}
}