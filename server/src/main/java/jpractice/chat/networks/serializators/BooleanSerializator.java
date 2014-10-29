package jpractice.chat.networks.serializators;

import java.util.Arrays;

/**
 * xx - код boolean
 * xx xx xx xx - длина массива поля, извлекается  getLength(byte[] bytes)
 * xx - длина имени класса поля (byte)
 * xx ... xx - массив имени класса поля
 * xx ... xx - массив значения поля, может содержать свои поля.
 * @author Alexander Vlasov
 */
public class BooleanSerializator extends Serializator<Boolean> {

    public static void main(String[] args) {
        Serializator serializator = new BooleanSerializator();
        byte[] bytes = serializator.debuild(false);
        System.out.println(Arrays.toString(bytes));
        System.out.println(serializator.build(bytes));
    }

    public static int getLength(byte[] bytes) {
        return 2;
    }

    @Override
    public byte[] debuild(Boolean l) {
        byte[] res = new byte[2];
        res[0] = BOOLEAN;
        res[1] = (byte) (l ? 1 : 0);
        return res;
    }

    @Override
    public Boolean build(byte[] bytes) {
        return build(bytes, 0);
    }
    @Override
    public Boolean build(byte[] bytes, int off) {
        return bytes[off + 1] == 1;
    }
}