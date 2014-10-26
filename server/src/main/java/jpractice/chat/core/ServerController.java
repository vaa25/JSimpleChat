package jpractice.chat.core;

public class ServerController {

	private Thread serverThread;
	private ChatServer server;

	public ServerController(ChatServer server) {
		this.server = server;
		this.serverThread = new Thread(server);
	}

	public void startServer() {
		serverThread.start();
	}
}
