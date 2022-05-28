**********
**About:**
**********

It is a multithreaded com.client/main.java.server ChatServer based on console which uses Java Socket programming. A main.java.server listens for connection requests from clients across the network or even from the same machine. Clients know how to connect to the main.java.server via an IP address and port number. After connecting to the main.java.server, the com.client gets to choose his/her username on the chat room. The com.client sends a message, the message is sent to the main.java.server using ObjectOutputStream in java. After receiving the message from the com.client, the main.java.server broadcasts the message if it is not a private message. And if it is a private message which is detect using ‘@’ followed by a valid username, then send the message only to that user. Java object serialization to transfer the messages.

*****************
**Instructions:**
*****************

**com.client.com.client.Client**

To start the com.client.Client in console mode use one of the following command
 1. java com.client.Client
 2. java com.client.Client username
 3. java com.client.Client username portNumber
 4. java com.client.Client username portNumber serverAddress

at the console prompt
If the portNumber is not specified 1500 is used
If the serverAddress is not specified "localHost" is used
If the username is not specified "Anonymous" is used

**main.java.server.Server**

To run as a console application
 1. java main.java.server.Server
 2. java main.java.server.Server portNumber
If the port number is not specified 1500 is used

**Chat**

While in com.client console:
1. Simply type the message to send broadcast to all active clients
2. Type '@username<space>yourmessage' without quotes to send message to desired com.client
3. Type 'WHOISIN' without quotes to see list of active clients
4. Type 'LOGOUT' without quotes to logoff from main.java.server
