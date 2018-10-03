package cs.tcd.ie;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import tcdIO.Terminal;

public class Gateway extends Node {

	static final int DEFAULT_DST_PORT = 40000;
	static final String DEFAULT_DST_NODE = "localhost";
	static final int GO_To = 50000;
	static final int NEW_PORT = 40700;
	public boolean isRecieved = false;
	public static int counter = 0;
	public static byte gatewayACK = 0;
	public static int serverACK = 0;
	public static int[] ports;
	public static int portNo;
	public StringContent content;
	 DatagramSocket temp;
	static DatagramSocket soc;
	Terminal terminal;
	InetSocketAddress dstAddress;
	/*
	 * 
	 */

	Gateway(Terminal terminal, int port, int dest) {
		try {
			this.terminal = terminal;
			socket = new DatagramSocket(port);
			temp = socket;
			dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, GO_To);
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

			content = new StringContent(packet);
			System.out.println("hereee");
			if (packet.getPort() == 50000) {

				byte[] income = packet.getData();
				byte[] head = new byte[10];
				System.arraycopy(income, 0, head, 0, head.length);

				byte[] srcAdd = { 0, 0, head[1], head[2] };

				ByteBuffer wrapped = ByteBuffer.wrap(srcAdd);
				int sourceAddress = wrapped.getInt();
				System.out.println(sourceAddress);
				byte frame = head[3];
				gatewayACK = (byte) (frame + 1);
				head[5] = gatewayACK;

				dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, sourceAddress);
				packet = new DatagramPacket(head, head.length, dstAddress);
				try {
					soc.send(packet);

				} catch (IOException e) {
					e.printStackTrace();
				}
				isRecieved = true;
			}

			else {
				//socket = soc;
				dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, GO_To);

				content = new StringContent(packet);
				terminal.println(content.toString());
				DatagramPacket packet2 = new DatagramPacket(packet.getData(), packet.getLength(), dstAddress);
				socket.send(packet2);
				//socket = temp;

				isRecieved = false;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void send(StringContent content, DatagramPacket packet) {

		dstAddress = new InetSocketAddress(DEFAULT_DST_NODE, GO_To);
		byte[] payload = content.toString().getBytes();
		byte[] header = new byte[PacketContent.HEADERLENGTH];
		byte[] buffer = new byte[header.length + payload.length];

		System.arraycopy(header, 0, buffer, 0, header.length);
		System.arraycopy(payload, 0, buffer, header.length, payload.length);
		packet = new DatagramPacket(buffer, buffer.length, dstAddress);
		try {
			socket.send(packet);
			terminal.println("Package Sent");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void starts() throws Exception {
		boolean x = true;
		while (x) {
			this.wait();
			isRecieved = false;
		}
	}

	/*
	 * 
	 */
	public static void main(String[] args) {
		try {
			soc = new DatagramSocket(40700);
			Terminal terminal = new Terminal("Gateway");
			(new Gateway(terminal, DEFAULT_DST_PORT, NEW_PORT)).starts();
			terminal.println("Program completed");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

}