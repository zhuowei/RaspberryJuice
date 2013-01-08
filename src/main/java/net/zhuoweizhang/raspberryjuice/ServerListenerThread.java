package net.zhuoweizhang.raspberryjuice;

import java.io.*;
import java.net.*;

public class ServerListenerThread implements Runnable {

	public ServerSocket serverSocket;

	public SocketAddress bindAddress;

	public boolean running = true;

	private RaspberryJuicePlugin plugin;

	public ServerListenerThread(RaspberryJuicePlugin plugin, SocketAddress bindAddress) throws IOException {
		this.plugin = plugin;
		this.bindAddress = bindAddress;
		serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(bindAddress);
	}

	public void run() {
		while (running) {
			try {
				Socket newConnection = serverSocket.accept();
				if (!running) return;
				plugin.handleConnection(new RemoteSession(plugin, newConnection));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
