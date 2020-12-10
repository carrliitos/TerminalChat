# TerminalChat
> Benzon Carlitos Salazar

## About
Terminal-based chat system that utilizes socket programming.

## How to run
1. Open a terminal, locate the [src](src/) folder, and compile all files
```
$ javac *.java
```
2. Run Server side
```
$ java Server
```
3. Open another terminal, and locate the [src](src/) folder, and run the Client
```
$ java Client
```

## Example
### Server Side
```
00:09:32 Server waiting for Clients on port 3000.
Creating Object I/O Streams.
00:09:43  *** Carlitos has joined. *** 
00:09:43 Server waiting for Clients on port 3000.
Creating Object I/O Streams.
00:09:54  *** Benzon has joined. *** 
00:09:54 Server waiting for Clients on port 3000.
00:10:01 Carlitos: hello Benzon!
00:10:09 Benzon: oh, hello Carlitos!
00:10:14 Benzon: let me PM you real quick
00:10:58 Carlitos: hello world!
00:11:03 Carlitos: bye world!
00:11:11 Benzon: see ya later man
00:11:13 Carlitos disconnected with a LOGOUT message.
00:11:13  *** Carlitos has left the chat room. *** 
00:11:18 Benzon disconnected with a LOGOUT message.
00:11:18  *** Benzon has left the chat room. *** 
```

### Client Side
#### User 1:
```
Enter username: 
Carlitos
Connected localhost/127.0.0.1:3000

Hello Carlitos! Welcome to the UWW chat room.
**Instructions**
1. To send a broadcast to all active members, type your message.
2. To send a private message, type '@<username> <message>'
3. To see list of active clients, type ONLINE
4. To logout, type LOGOUT

[Carlitos]>> 00:09:54  *** Benzon has joined. *** 
[Carlitos]>> hello Benzon!
[Carlitos]>> 00:10:01 Carlitos: hello Benzon!
[Carlitos]>> 00:10:09 Benzon: oh, hello Carlitos!
[Carlitos]>> 00:10:14 Benzon: let me PM you real quick
[Carlitos]>> 00:10:22 [Private] Benzon:hey man, what's up?
[Carlitos]>> @Benzon hey, nothing much, just demoing this program
[Carlitos]>> 00:10:51 [Private] Benzon:oh yeah? cool!! me too!
[Carlitos]>> hello world!
[Carlitos]>> 00:10:58 Carlitos: hello world!
[Carlitos]>> bye world!
[Carlitos]>> 00:11:03 Carlitos: bye world!
[Carlitos]>> 00:11:11 Benzon: see ya later man
[Carlitos]>> logout
 *** Server has closed: java.net.SocketException: Socket closed *** 
```
#### User 2:
```
Enter username: 
Benzon
Connected localhost/127.0.0.1:3000

Hello Benzon! Welcome to the UWW chat room.
**Instructions**
1. To send a broadcast to all active members, type your message.
2. To send a private message, type '@<username> <message>'
3. To see list of active clients, type ONLINE
4. To logout, type LOGOUT

[Benzon]>> 00:10:01 Carlitos: hello Benzon!
[Benzon]>> online
[Benzon]>> 
List of users connected at 00:10:04
[Benzon]>> 1) Carlitos since Thu Dec 10 00:09:43 CST 2020
[Benzon]>> 2) Benzon since Thu Dec 10 00:09:54 CST 2020
[Benzon]>> oh, hello Carlitos!
[Benzon]>> 00:10:09 Benzon: oh, hello Carlitos!
[Benzon]>> let me PM you real quick 
[Benzon]>> 00:10:14 Benzon: let me PM you real quick
[Benzon]>> @Carlitos hey man, what's up?
[Benzon]>> 00:10:40 [Private] Carlitos:hey, nothing much, just demoing this program
[Benzon]>> @Carlitos oh yeah? cool!! me too!
[Benzon]>> 00:10:58 Carlitos: hello world!
[Benzon]>> 00:11:03 Carlitos: bye world!
[Benzon]>> see ya later man
[Benzon]>> 00:11:11 Benzon: see ya later man
[Benzon]>> 00:11:13  *** Carlitos has left the chat room. *** 
[Benzon]>> logout
 *** Server has closed: java.net.SocketException: Socket closed ***
```