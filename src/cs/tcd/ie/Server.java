package cs.tcd.ie;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

import tcdIO.Terminal;

public class Server extends Node {
	static final int DEFAULT_PORT = 50000;
	public static int counter = 0;
	InetSocketAddress dstAddress;
	static final String DEFAULT_DST_NODE = "localhost";
	static final int NEW_SOCKET = 40788;
	public static byte serverACK = 1;
	public String[] received;
	Terminal terminal;

	/*
	 * 
	 */
	Server(Terminal terminal, int port, String[] rec) {
		try {
			this.terminal = terminal;
			socket = new DatagramSocket(port);
			this.received = rec;
			listener.go();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		try {
			int count = 0;
			for (int i = 0; i < 10; i++) {
				if (received[i] != null) {
					count++;
				}
			}
			if (count >= 9) {
				serverACK = 0;
				count = 0;
			}
			this.notify();
			StringContent content = new StringContent(packet);

			terminal.println(content.toString());
			byte[] income = packet.getData();
			byte[] head = new byte[10];
			System.arraycopy(income, 0, head, 0, head.length);
			if (head[6] != 0) {
				int value = head[6];
				received[value] = content.toString();
			} else {
				received[serverACK] = content.toString();
			}
			head[4] = serverACK;
			dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, 40000);
			packet = new DatagramPacket(head, head.length, dstAddress);
			socket.send(packet);
			terminal.println("Packet Sent");
			serverACK++;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void start() throws Exception {
		boolean x = true;
		while (x) {
			this.wait();
		}
	}

	/*
	 * 
	 */
	public static void main(String[] args) {
		try {
			String[] rec = new String[10];
			Terminal terminal = new Terminal("Server");
			(new Server(terminal, DEFAULT_PORT, rec)).start();
			terminal.println("Program completed");

		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}