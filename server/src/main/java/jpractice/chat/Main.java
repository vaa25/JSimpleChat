package jpractice.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;

import jpractice.chat.core.ChatServer;
import jpractice.chat.core.ServerController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alexander Vlasov
 */
public class Main {
	private static Logger logger = LoggerFactory.getLogger("server.launcher");
	private static ServerController controller;

	public static void main(String[] args) {
		ChatServer server = null;
		try {
			server = new ChatServer(Executors.newCachedThreadPool());
			controller = new ServerController(server);
			controller.startServer();
			startCommandLineLoop();
		} catch (IOException e) {
			// TODO: make more informative log
			logger.error("IO failure");
		}
	}

	private static void startCommandLineLoop() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		String msg = null;
		while ((msg = reader.readLine()) != null) {
			processCmd(msg);
		}
	}

	private static void processCmd(String cmdMessage) {
		// Stub now
		logger.debug("processCmd for string {}", cmdMessage);
	}
}
