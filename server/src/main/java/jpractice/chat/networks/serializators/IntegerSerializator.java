package jpractice.chat.networks.serializators;

import java.util.Arrays;

/**
 * @author Alexander Vlasov
 */
public class IntegerSerializator extends Serializator<Integer> {
    public static void main(String[] args) {
        Serializator serializator = new Serializator();
        byte[] bytes = serializator.debuild(1239487);
//        bytes[0]=9;
        System.out.println();
        System.out.println(Arrays.toString(bytes));
        System.out.println(serializator.build(1, bytes));
    }

    public static int getLength(byte[] bytes) {
        return 5;
    }

    @Override
    public <T> byte[] debuild(T k) {
        int l = (Integer) k;
        byte[] res = new byte[5];
        res[0] = INTEGER;
        res[1] = (byte) (l);
        res[2] = (byte) (l >> 8);
        res[3] = (byte) (l >> 16);
        res[4] = (byte) (l >> 24);
        return res;
    }

    @Override
    public Integer build(byte[] bytes, int off) {
        return (Byte.toUnsignedInt(bytes[off + 4]) << 24) +
                (Byte.toUnsignedInt(bytes[off + 3]) << 16) +
                (Byte.toUnsignedInt(bytes[off + 2]) << 8) +
                Byte.toUnsignedInt(bytes[off + 1]);
    }
}