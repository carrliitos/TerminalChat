/**
 * @author Benzon Carlitos Salazar
 */

import java.io.Serializable;

/**
 * A class that defines the different types of messages that will be exchanged
 * between the Client and Server.
 */

public class Chat implements Serializable {
	/**
	 * Different types of messages sent by the Client
	 * ONLINE - to receive the list of users connected
	 * MESSAGE - text message
	 * LOGOUT - to disconnect from the Server
	 */
	static final int ONLINE = 0, MESSAGE = 1, LOGOUT = 2;
	private int type;
	private String message;

	Chat(int type, String message) {
		this.type = type;
		this.message = message;
	}

	int getType() { return type; }
	String getMessage() { return message; }
}