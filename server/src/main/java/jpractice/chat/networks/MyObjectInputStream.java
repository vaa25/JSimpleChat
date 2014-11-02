package jpractice.chat.networks;

import jpractice.chat.networks.serializators.Serializator;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Vlasov
 */
public class MyObjectInputStream implements Runnable {
    private InputStream in;
    private byte[] data;
    private volatile boolean transferComplete;
    private boolean closed;
    private ConcurrentHashMap map;

    public MyObjectInputStream(InputStream in, ConcurrentHashMap<MyObjectInputStream, Object> received) {
        this.in = in;
        map = received;
    }

    @Override
    public void run() {
        transferComplete = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("Interrupt connection");
            try {
                in.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        while (!transferComplete) {

            try {
                int value = in.read();
                if (value == -1) {
                    closed = true;
                    continue;
                }
                byte code = (byte) value;
                int len = Serializator.getLength(code);
                byte[] length = new byte[0];
                if (len == -1) {
                    length = new byte[4];
                    for (int i = 0; i < 4; i++) {
                        value = in.read();
                        if (value == -1) {
                            closed = true;
                            continue;
                        }
                        length[i] = (byte) value;
                    }
                    len = Serializator.getLength(length);
                }
                data = new byte[len];
                data[0] = code;
                System.arraycopy(length, 0, data, 1, length.length);
                for (int i = 1 + length.length; i < len; i++) {
                    value = in.read();
                    if (value == -1) {
                        closed = true;
                        continue;
                    }
                    data[i] = (byte) value;
                }
                map.put(this, Serializator.build(data));
            } catch (SocketException e) {
                if ("Connection reset".equals(e.getMessage())) {
                    System.out.println("Connection reset");
                    closed = true;
                    map.put(this, Special.LostConnection);
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
