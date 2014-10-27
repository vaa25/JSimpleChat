package jpractice.chat.networks.serializators;

import java.util.Arrays;

/**
 * xx xx xx xx - длина массива поля, извлекается  getLength(byte[] bytes)
 * xx - длина имени класса поля (byte)
 * xx ... xx - массив имени класса поля
 * xx ... xx - массив значения поля, может содержать свои поля.
 * @author Alexander Vlasov
 */
public class BooleanSerializator extends Serializator<Boolean> {

    public static void main(String[] args) {
        BooleanSerializator serializator = new BooleanSerializator();
        byte[] bytes = serializator.debuild(false);
//        bytes[0]=9;
        System.out.println(Arrays.toString(bytes));
        System.out.println(serializator.build(bytes));
    }

    public static int getLength(byte[] bytes) {
        return 7;
    }

    @Override
    public byte[] debuild(Boolean k) {
        byte[] boolNameB = "boolean".getBytes();
        int bNBLen = boolNameB.length;
        byte[] res = new byte[6 + bNBLen];
        byte[] len = setLength(res.length);
        System.arraycopy(len, 0, res, 0, 4);
        res[4] = (byte) bNBLen;
        System.arraycopy(boolNameB, 0, res, 5, bNBLen);
        res[5 + bNBLen] = (byte) (k ? 1 : 0);
        return res;
    }

    @Override
    public Boolean build(byte[] bytes) {
//        if (bytes[0] != Serializator.BOOLEAN) {
//            throw new NotExpectedContent(bytes[0] + " instead of " + Serializator.BOOLEAN);
//        }
        return bytes[bytes.length - 1] == 1;
    }
}