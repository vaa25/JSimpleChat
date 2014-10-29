package jpractice.chat.networks;

import jpractice.chat.networks.serializators.Serializator;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Alexander Vlasov
 */
public class MyObjectInputStream implements Runnable {
    private InputStream in;
    private byte[] data;
    private byte type;
    private volatile boolean transferComplete;

    public MyObjectInputStream(InputStream in) {
        this.in = in;

    }

    public boolean hasNext() {
        return transferComplete;
    }

    public Object get() {
        Serializator serializator = null;
        switch (type) {
            case Serializator.BOOLEAN:
//                serializator = new BooleanSerializator();
                break;
            case Serializator.STRING:
//                serializator = new StringSerializator();
                break;
            case Serializator.INTEGER:
//                serializator = new IntegerSerializator();
                break;
        }
        Object res = serializator.build(next());
        return res;
    }

    private byte[] next() {

        byte[] res = data;
        transferComplete = false;
        return res;
    }

    @Override
    public void run() {
        transferComplete = false;
        while (!transferComplete) {
            try {
                byte[] header = new byte[6];
                for (int i = 0; i < 6; i++) {
                    int value = in.read();
                    if (value == -1) return;
                    header[i] = (byte) value;
                }
                int len = Serializator.getLength(header);
                data = new byte[len];
                System.arraycopy(header, 0, data, 0, header.length);
                for (int i = header.length; i < data.length; i++) {
                    int value = in.read();
                    if (value == -1) return;
                    data[i] = (byte) value;
                }
                transferComplete = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
