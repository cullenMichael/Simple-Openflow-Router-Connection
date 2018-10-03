
/**
 * 
 */
package cs.tcd.ie;

import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;

import tcdIO.*;

/**
 *
 * Client class
 * 
 * An instance accepts user input
 *
 */

public class Client extends Node implements Runnable {
	static final int DEFAULT_SRC_PORT1 = 40788;
	static final int DEFAULT_SRC_PORT2 = 40790;
	static final int DEFAULT_SRC_PORT3 = 40791;
	static final int DEFAULT_DST_PORT = 40000;
	static final String DEFAULT_DST_NODE = "localhost";
	public static int gatewayACK = 0;
	public static int[] listOfPorts;
	public static Client[] clients;
	public static final int ARRAYSIZE = 3;
	public static int port;
	public static int portUsed;
	public static int in;
	public static boolean loop;
	public static boolean isPicked = false;
	public int ports;
	public static byte ack = -1;
	public byte acks;
	public static String[] waiting;
	public String[] iswaiting;
	public boolean isResend = false;
	public boolean check = false;
	public String s1;
	int number;
	timer t;
	static timer t1;

	DatagramSocket temp;
	DatagramSocket temp1;
	Terminal terminal;
	InetSocketAddress dstAddress;
	DatagramPacket packet;
	Thread thread;
	static Client c;

	/**
	 * Constructor
	 * 
	 * Attempts to create socket at given port and create an InetSocketAddress
	 * for the destinations
	 */
	Client(Terminal terminal, String dstHost, int dstPort, int port, byte ack, String[] waiting, timer t1) {
		try {
			this.ports = port;
			this.acks = ack;
			this.iswaiting = waiting;
			this.terminal = terminal;
			this.t = t1;
			dstAddress = new InetSocketAddress(dstHost, dstPort);
			socket = new DatagramSocket(port);
			listener.go();

		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public /* synchronized */ void onReceipt(DatagramPacket packet) {
	
		byte[] income = packet.getData();
		byte frame = income[5];
		byte f = (byte) (income[3] + 1);
		if (frame == f) {
			check = true;
			this.iswaiting[this.acks] = null;
		}
	}

	/**
	 * Sender Method
	 * 
	 */
	public /* synchronized */ void sending(byte[] a, int q) {

		if (this.acks <= 9) {
			this.acks++;
		}
		if (this.acks >= 9) {
			this.acks = 0;
		}

		packet = null;
		byte[] payload = a;
		byte[] header = null;
		byte[] buffer = null;

		int portUsed = clients[q].ports;
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(portUsed);

		header = new byte[PacketContent.HEADERLENGTH];
		header[1] = bytes[2];
		header[2] = bytes[3];
		header[3] = this.acks;
		buffer = new byte[header.length + payload.length];
		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(payload, 0, buffer, header.length, payload.length);

		terminal.println("Sending packet...");
		packet = new DatagramPacket(buffer, buffer.length, dstAddress);
		StringContent cont = new StringContent(packet);
		this.iswaiting[this.acks] = cont.toString();
		this.terminal.println(Arrays.toString(this.iswaiting));

		try {
		//	this.t = new timer(c, s1, number);
			terminal.println("Packet sent");
			socket.send(packet);
		//	new Thread(this.t).start();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Test method
	 * 
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {
			Terminal terminal = new Terminal("Amount");
			int input = (Integer.parseInt(terminal.readString("Enter amount of Client/s: ")));
			listOfPorts = new int[input];
			clients = new Client[input];
			waiting = new String[10];
			port = DEFAULT_SRC_PORT1;
			for (int i = 0; i < input; i++) {
				port = port + 1;
				Terminal t = new Terminal("client" + i);
				c = new Client(t, DEFAULT_DST_NODE, DEFAULT_DST_PORT, port, ack, waiting, t1);
				in = i;
				clients[i] = c;
				new Thread(c).start();
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		boolean x = true;

		while (x) {
			String name = terminal.getTitle();
			s1 = terminal.readString("String to send: ");
			String l = name.substring(name.length() - 1);
			number = Integer.parseInt(l);

			terminal.println(s1);
			sending(s1.getBytes(), number);
			isPicked = true;

		}
	}
}