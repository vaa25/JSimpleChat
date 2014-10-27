package jpractice.chat.networks.serializators;

import java.util.Arrays;

/**
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

    @Override
    public byte[] debuild(Boolean k) {
        byte[] res = new byte[2 + 4 + 1];
        res[0] = Serializator.Boolean;
        byte[] len = setLength(1);
        System.arraycopy(len, 0, res, 1, 4);
        res[5] = 1;
        res[6] = (byte) (k ? 1 : 0);
        return res;
    }

    @Override
    public Boolean build(byte[] bytes) {
        if (bytes[0] != Serializator.Boolean) {
            throw new NotExpectedContent(bytes[0] + " instead of " + Serializator.Boolean);
        }
        return bytes[6] == 1;
    }

    @Override
    public int getLength(byte[] bytes) {
        return 7;
    }
}