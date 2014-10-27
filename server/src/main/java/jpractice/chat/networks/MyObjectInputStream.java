package jpractice.chat.networks;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Alexander Vlasov
 */
public class MyObjectInputStream {
    public static final int NICKNAME = 1;
    public static final int TEXT = 2;
    private InputStream in;
    private byte[] data;
    private int type;


    public MyObjectInputStream(InputStream in) {
        this.in = in;

    }

    public boolean hasData() throws IOException {
        if (in.available() == 0) return false;
        in.mark(2000);
        int type = in.read();
        if (type == -1 || type == 0) return false;
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

}
