package jpractice.chat.networks.serializators;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;


/**
 * @author Alexander Vlasov
 */
public class StringSerializator extends Serializator<String> {
    public static void main(String[] args) {
        Serializator<String> serializator = new Serializator();
        byte[] bytes = serializator.debuild("Саша the best");
//        bytes[0]=9;
        System.out.println(Arrays.toString(bytes));
//        System.out.println(serializator.build(bytes));
        System.out.println(serializator.build("", bytes));
    }

    @Override
    public <T> byte[] debuild(T string) {
        String value = (String) string;
        byte[] stringB = null;
        try {
            stringB = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        byte[] stringB = Charset.forName("UTF-8").encode(value).array();
        byte[] res = new byte[5 + stringB.length];
        byte[] len = setLength(res.length);
        res[0] = STRING;
        System.arraycopy(len, 0, res, 1, 4);
        System.arraycopy(stringB, 0, res, 5, stringB.length);
        return res;

    }

    @Override
    public String build(byte[] bytes, int off) {
        int start = off + 5;
        int len = getLength(bytes, off + 1) - 5;
        String res = Charset.forName("UTF-8").decode(ByteBuffer.wrap(bytes, start, len)).toString();
        return res;
    }


}
