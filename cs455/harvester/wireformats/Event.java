package cs455.harvester.wireformats;
/*
 *Author: Tiger Barras
 *Event.java
 *Parent interface for all message wireformats
 */

import java.net.Socket;

public interface Event{

	public int getType(); //Returns the integer ID for this message

	public byte[] getBytes(); //Returns the bytes of this event, for unmarshalling

	public String toString();

	//Only exists because I need to get the socket back to the registery
		//in message type 2
	public Socket getSocket();
}
