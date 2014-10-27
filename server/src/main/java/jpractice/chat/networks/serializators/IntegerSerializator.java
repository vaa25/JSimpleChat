package jpractice.chat.networks.serializators;

import java.util.Arrays;

/**
 * @author Alexander Vlasov
 */
public class IntegerSerializator extends Serializator<Integer> {
    public static void main(String[] args) {
        IntegerSerializator serializator = new IntegerSerializator();
        byte[] bytes = serializator.debuild(1239487);
//        bytes[0]=9;
        System.out.println();
        System.out.println(Arrays.toString(bytes));
        System.out.println(serializator.build(bytes));
    }

    @Override
    public byte[] debuild(Integer k) {

        byte[] res = new byte[10];
        res[0] = Serializator.Integer;
        byte[] len = setLength(4);
        System.arraycopy(len, 0, res, 1, 4);
        res[6] = (byte) ((int) k);
        res[7] = (byte) (k >> 8);
        res[8] = (byte) (k >> 16);
        res[9] = (byte) (k >> 24);
        res[5] = 1;
        return res;
    }

    @Override
    public Integer build(byte[] bytes) {
        if (bytes[0] != Serializator.Integer) {
            throw new NotExpectedContent(bytes[0] + " instead of " + Serializator.Integer);
        }
        return (Byte.toUnsignedInt(bytes[9]) << 24) +
                (Byte.toUnsignedInt(bytes[8]) << 16) +
                (Byte.toUnsignedInt(bytes[7]) << 8) +
                Byte.toUnsignedInt(bytes[6]);
    }

    @Override
    public int getLength(byte[] bytes) {
        return 10;
    }
}