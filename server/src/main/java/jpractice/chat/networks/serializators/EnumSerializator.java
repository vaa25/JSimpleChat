package jpractice.chat.networks.serializators;

/**
 * @author Alexander Vlasov
 */
public class EnumSerializator implements SerializatorInterface {


    public byte[] debuild(Object value) {
        Enum anEnum = (Enum) value;
        StringSerializator stringSerializator = new StringSerializator();
        byte[] name = stringSerializator.debuild(anEnum.name());
        byte[] clazz = stringSerializator.debuild(anEnum.getDeclaringClass().getName());
        byte[] res = new byte[5 + name.length + clazz.length];
        byte[] len = Serializator.setLength(5 + name.length + clazz.length);
        res[0] = Serializator.ENUM;
        System.arraycopy(len, 0, res, 1, 4);
        System.arraycopy(clazz, 0, res, 5, clazz.length);
        System.arraycopy(name, 0, res, 5 + clazz.length, name.length);
        return res;

    }

    public Object build(byte[] bytes) {
        return build(bytes, 0);
    }

    public Object build(byte[] bytes, int off) {
        StringSerializator stringSerializator = new StringSerializator();
        String clazz = (String) stringSerializator.build(bytes, 5 + off);
        int clazzL = Serializator.getLength(bytes, 5 + 1 + off);
        System.out.println(clazzL);
        String name = (String) stringSerializator.build(bytes, 5 + clazzL + off);
        Enum res = null;
        try {
            res = Enum.valueOf((Class<? extends Enum>) Class.forName(clazz), name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
}
