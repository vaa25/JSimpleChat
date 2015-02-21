package jpractice.chat.networks;

import jpractice.chat.networks.serializators.Serializator;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Alexander Vlasov
 */
public class MyObjectInputStream implements Runnable {
    private InputStream in;
    private byte[] data;
    private volatile boolean transferComplete;
    private boolean closed;
    private ConcurrentHashMap<MyObjectInputStream, BlockingQueue> map;

    public MyObjectInputStream(InputStream in, ConcurrentHashMap<MyObjectInputStream, BlockingQueue> received) {
        this.in = in;
        map = received;
        map.put(this, new LinkedBlockingQueue<>());
    }

    @Override
    public void run() {
        transferComplete = false;
        while (!transferComplete) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.out.println("Interrupt connection");
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                break;
            }

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
                Object received = Serializator.build(data);
                map.get(this).add(received);
//                map.put(this, received);
//                System.out.println("Принял "+received);
            } catch (SocketTimeoutException | SocketException e) {
                System.out.println("Connection reset");
                closed = true;
                map.get(this).add(Special.LostConnection);
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
