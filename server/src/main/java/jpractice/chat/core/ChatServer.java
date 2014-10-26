package jpractice.chat.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatServer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger("server");
	private ExecutorService executor;
	private ServerSocket serverSocket;

	public ChatServer(ExecutorService executor) throws IOException {
		this.executor = executor;
		Properties settings = new Properties();
		settings.load(ClassLoader
				.getSystemResourceAsStream("server.properties"));
		int port = Integer.valueOf(settings.getProperty("port", "7777"));
		this.serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		try {
			logger.debug("Start listening...");
			startListening();
		} catch (IOException ex) {
			logger.error("Server listening fail, chat server stoped");
		}
	}

	private void startListening() throws IOException {
		while (true) {
			Socket clientSocket = serverSocket.accept();
			logger.debug("New client connection from {}",
					clientSocket.getInetAddress());
			ClientHandler handler = new ClientHandler(clientSocket);
			executor.submit(handler);
		}
	}
}
