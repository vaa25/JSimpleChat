package jpractice.chat.networks.serializators;

import jpractice.chat.Person;

import java.util.HashMap;
import java.util.Map;

/**
 * Сериализация - разложение объектов на байты.
 *
 * Структура итогового массива байтов byte[]bytes:
 *
 * xx xx xx xx - длина итогового массива, извлекается  getLength(byte[] bytes)
 * xx - длина имени класса
 * xx - количество полей
 * xx ... xx - поля
 * поля:
 * xx xx xx xx - длина массива поля, извлекается  getLength(byte[] bytes)
 * xx - длина имени класса поля
 * xx - длина имени поля
 * xx ... xx - массив имени класса поля
 * xx ... xx - массив имени поля
 * xx ... xx - массив значения поля, может содержать свои поля.
 *
 *  *длина имени класса (поля) включает в себя себя и остальные данные, принадлежащие классу (полю)
 *
 * @author Alexander Vlasov
 */
public abstract class Serializator<T> {
    public static final byte STRING = 10;
    public static final byte INTEGER = 9;
    public static final byte BOOLEAN = 8;
    public static final byte PERSON = 11;

    private static Map<Class, Byte> codes = new HashMap<>();

    static {
        codes.put(Boolean.class, BOOLEAN);
        codes.put(boolean.class, BOOLEAN);
        codes.put(Integer.class, INTEGER);
        codes.put(int.class, INTEGER);
        codes.put(String.class, STRING);
        codes.put(Person.class, PERSON);
    }

    public static byte getCode(Class clazz) {
        return codes.get(clazz);
    }

    public static boolean containsCode(Class clazz) {
        return codes.containsKey(clazz);
    }

    /**
     * Извлекает длину массива преобразованного объекта
     *
     * @param bytes массив преобразованного объекта
     *
     * @return длина
     */

    public static synchronized int getLength(byte[] bytes) {
        return getLength(bytes, 1);

    }

    /**
     * Длина в массиве указанная в массиве со смещением
     *
     * @param bytes массив
     * @param off   смещение
     *
     * @return длина
     */
    protected static int getLength(byte[] bytes, int off) {
        byte[] lengthB = new byte[4];
        System.arraycopy(bytes, off, lengthB, 0, 4);
        return (Byte.toUnsignedInt(lengthB[3]) << 24) +
                (Byte.toUnsignedInt(lengthB[2]) << 16) +
                (Byte.toUnsignedInt(lengthB[1]) << 8) +
                Byte.toUnsignedInt(lengthB[0]);
    }
//    public abstract int length(byte[] bytes);

    public static byte[] getBytes(Object object) {
        Serializator serializator;
        switch (getCode(object.getClass())) {
            case BOOLEAN:
                serializator = new BooleanSerializator();
                break;
            case INTEGER:
                serializator = new IntegerSerializator();
                break;
            case STRING:
                serializator = new StringSerializator();
                break;
            case PERSON:
                serializator = new PersonSerializator();
                break;
            default:
                throw new NotExpectedContent("Class not supported");
        }
        return serializator.debuild(object);
    }

    public abstract T build(byte[] bytes);

    public abstract byte[] debuild(T name);

    /**
     * Извлечение отдельных полей в виде массивов из главного массива
     *
     * @param bytes главный массив
     *
     * @return массив массивов полей
     */
    public byte[][] split(byte[] bytes) {
        int amount = bytes[5];
        if (amount == 1) return new byte[][]{bytes};
        int lenIndex = 7;
        int startIndex = 6;
        byte[][] res = new byte[amount][];
        for (int i = 0; i < amount; i++) {
            int len = getLength(bytes, lenIndex);
            byte[] body = new byte[len + 1 + 4 + 1];
            System.arraycopy(bytes, startIndex, body, 0, body.length);
            res[i] = body;
            startIndex += body.length;
            lenIndex += body.length;
        }
        return res;
    }

    public byte[] pack(Class clazz, byte[][] bytes) {
        int length = 1 + 4 + 1;
        for (int i = 0; i < bytes.length; i++) {
            length += bytes[i].length;
        }
        byte[] res = new byte[length];
        res[0] = getCode(clazz);
        byte[] len = setLength(length);
        System.arraycopy(len, 0, res, 1, 4);
        res[5] = (byte) bytes.length;
        int startIndex = 6;
        for (int i = 0; i < bytes.length; i++) {
            System.arraycopy(bytes[i], 0, res, startIndex, bytes[i].length);
            startIndex += bytes[i].length;
        }
        return res;
    }

    /**
     * Разложение int в четыре байта. Используется как для данных.
     *
     * @param k int
     *
     * @return byte[4]
     */
    public byte[] setLength(int k) {
        byte[] res = new byte[4];
        res[0] = (byte) (k);
        res[1] = (byte) (k >> 8);
        res[2] = (byte) (k >> 16);
        res[3] = (byte) (k >> 24);
        return res;
    }


}
