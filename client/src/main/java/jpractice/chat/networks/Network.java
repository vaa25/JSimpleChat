package jpractice.chat.networks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Network {
    public static final int NICKNAME = 1;
    public static final int TEXT = 2;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Socket conn;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectSender sender;
    private ObjectReceiver receiver;
    private ObjectParser parser;
    private Thread receiverThread;
    private InetAddress host;
    private int port;
    private byte[] data;
    private int type;

    public Network(Socket conn) throws IOException {


        //        logger.info( "Пытаюсь создать исходящий поток");
        out = new ObjectOutputStream(conn.getOutputStream());
        System.out.println("1");
//        logger.info( "Иcходящий поток успешно создан " + out);
//        logger.info( "Пытаюсь создать входящий поток");
        in = new ObjectInputStream(conn.getInputStream());
        System.out.println("2");
//        logger.info( "Входящий поток успешно создан " + in);

        sender = new ObjectSender(out);
        System.out.println("3");
        parser = new ObjectParser();
        System.out.println("4");
        receiver = new ObjectReceiver(in, parser);
        System.out.println("5");
        receiverThread = new Thread(receiver);
        receiverThread.start();
//        logger.info("network (" + Thread.currentThread().getName() + ") starts receiverThread (" + receiverThread.getName() + ")");

    }

    public boolean hasData() throws IOException {
        if (in.available() == 0) return false;
        in.mark(2000);
        int type = in.read();
        int length = in.read();
        byte[] data = new byte[length];
        int len = in.read(data);
        if (len < length) {
            in.reset();
            return false;
        }
        this.data = data;
        this.type = type;
        return true;


    }

    public ObjectSender getSender() {
        return sender;
    }

    public ObjectParser getParser() {
        return parser;
    }


    public void close() {
        try {
            in.close();
            out.close();
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
//            logger.info("network (" + Thread.currentThread().getName() + ") try to join receiverThread (" + receiverThread.getName() + ")");
            receiverThread.join();
//            logger.info("network (" + Thread.currentThread().getName() + ") receiverThread (" + receiverThread.getName() + ") joined");
        } catch (InterruptedException e) {
            logger.error("Network (" + Thread.currentThread().getName() + ") receiverThread (" + receiverThread.getName() + ") join InterruptedException");
        }
    }


}
