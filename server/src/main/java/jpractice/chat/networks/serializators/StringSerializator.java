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

        byte[] strNameB = "String".getBytes();
        int strNBLen = strNameB.length;
        byte[] res = new byte[5 + strNBLen + stringB.length];
        byte[] len = setLength(res.length);
        System.arraycopy(len, 0, res, 0, 4);
        res[4] = (byte) strNBLen;
        System.arraycopy(strNameB, 0, res, 5, strNBLen);
        System.arraycopy(stringB, 0, res, 5 + strNBLen, stringB.length);
        return res;

    }

    @Override
    public String build(byte[] bytes) {
//        if (bytes[0] != Serializator.STRING) {
//            throw new NotExpectedContent(bytes[0] + " instead of " + Serializator.STRING);
//        }
        int start = bytes[4] + 5;
        int len = bytes.length - start;
        String res = Charset.defaultCharset().decode(ByteBuffer.wrap(bytes, start, len)).toString();
        return res;
    }


}
