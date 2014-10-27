package jpractice.chat.networks.serializators;

/**
 * Сериализация - разложение объектов на байты.
 * Структура итогового массива байтов byte[]bytes:
 * xx - код класса
 * xx xx xx xx - длина класса, извлекается  getLength(byte[] bytes)
 * xx - количество полей
 * <p>
 * далее идут поля с такой же структурой
 *
 * @author Alexander Vlasov
 */
public abstract class Serializator<T> {
    static final byte String = 10;
    static final byte Integer = 9;
    static final byte Boolean = 8;
    static final byte Person = 11;
    private IntegerSerializator integerSerializator;

    public abstract T build(byte[] bytes);

    public abstract byte[] debuild(T name);
//    public abstract int length(byte[] bytes);

    /**
     * Извлекает длину массива преобразованного объекта
     *
     * @param bytes массив преобразованного объекта
     *
     * @return длина
     */
    protected int getLength(byte[] bytes) {
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
    protected int getLength(byte[] bytes, int off) {
        byte[] lengthB = new byte[4];
        System.arraycopy(bytes, off, lengthB, 0, 4);
        return (Byte.toUnsignedInt(lengthB[3]) << 24) +
                (Byte.toUnsignedInt(lengthB[2]) << 16) +
                (Byte.toUnsignedInt(lengthB[1]) << 8) +
                Byte.toUnsignedInt(lengthB[0]);
    }

    /**
     * Извлечение отдельных полей в виде массивов из главного массива
     *
     * @param bytes главный массив
     *
     * @return массив массивов полей
     */
    protected byte[][] split(byte[] bytes) {
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

    protected byte[] pack(byte code, byte[][] bytes) {
        int length = 1 + 4 + 1;
        for (int i = 0; i < bytes.length; i++) {
            length += bytes[i].length;
        }
        byte[] res = new byte[length];
        res[0] = code;
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
    protected byte[] setLength(int k) {
        byte[] res = new byte[4];
        res[0] = (byte) (k);
        res[1] = (byte) (k >> 8);
        res[2] = (byte) (k >> 16);
        res[3] = (byte) (k >> 24);
        return res;
    }


}
