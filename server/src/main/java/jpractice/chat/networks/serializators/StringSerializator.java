package jpractice.chat.networks.serializators;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;


/**
 * @author Alexander Vlasov
 */
public class StringSerializator extends Serializator<String> {
    public static void main(String[] args) {
        Serializator<String> serializator = new StringSerializator();
        byte[] bytes = serializator.debuild("Саша");
//        bytes[0]=9;
        System.out.println(Arrays.toString(bytes));
        System.out.println(serializator.build(bytes));
    }

    @Override
    public byte[] debuild(String string) {
        byte[] stringB = Charset.defaultCharset().encode(string).array();
        int stringBlen = stringB.length;
        byte[] res = new byte[stringBlen + 6];
        res[0] = Serializator.String;
        byte[] len = setLength(stringBlen);
        System.arraycopy(len, 0, res, 1, 4);
        res[5] = 1;
        System.arraycopy(stringB, 0, res, 6, stringBlen);
        return res;
    }

    @Override
    public String build(byte[] bytes) {
        if (bytes[0] != Serializator.String) {
            throw new NotExpectedContent(bytes[0] + " instead of " + Serializator.String);
        }
        int len = getLength(bytes);
        String res = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes, 6, len)).toString();
        return res;
    }


}
