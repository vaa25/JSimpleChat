package jpractice.chat.core;

import java.net.Socket;

public class ClientHandler implements Runnable {
	private Socket clientSocket;

	public ClientHandler(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		// Stub
		System.out.println("Client accepted, processed and stoped");
	}
}
