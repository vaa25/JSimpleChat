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

    public static int getLength(byte[] bytes) {
        return 10;
    }

    @Override
    public byte[] debuild(Integer k) {
        byte[] intNameB = "int".getBytes();
        int intNBLen = intNameB.length;
        byte[] res = new byte[9 + intNBLen];
        byte[] len = setLength(res.length);
        System.arraycopy(len, 0, res, 0, 4);
        res[4] = (byte) intNBLen;
        System.arraycopy(intNameB, 0, res, 5, intNBLen);
        res[5 + intNBLen] = (byte) ((int) k);
        res[6 + intNBLen] = (byte) (k >> 8);
        res[7 + intNBLen] = (byte) (k >> 16);
        res[8 + intNBLen] = (byte) (k >> 24);
        return res;
    }

    @Override
    public Integer build(byte[] bytes) {
//        if (bytes[0] != Serializator.INTEGER) {
//            throw new NotExpectedContent(bytes[0] + " instead of " + Serializator.INTEGER);
//        }
        int bytesLen = bytes.length;
        return (Byte.toUnsignedInt(bytes[bytesLen - 1]) << 24) +
                (Byte.toUnsignedInt(bytes[bytesLen - 2]) << 16) +
                (Byte.toUnsignedInt(bytes[bytesLen - 3]) << 8) +
                Byte.toUnsignedInt(bytes[bytesLen - 4]);
    }
}