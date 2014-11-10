package jpractice.chat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Alexander Vlasov
 */
public class Buffer {
    private final int length;
    private Queue<String> buffer;

    public Buffer(int length) {
        this.length = length;
        buffer = new ConcurrentLinkedQueue<>();
    }

    public void add(String string) {
        while (buffer.size() >= length) buffer.remove();
        buffer.add(string + "\n");
    }

    public String get() {
        String res = "";
        for (String s : buffer) {
            res += s;
        }
        return res;
    }

    public int size() {
        return buffer.size();
    }
}
