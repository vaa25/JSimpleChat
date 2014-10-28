package jpractice.chat.networks.serializators;

import jpractice.chat.Person;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Сериализатор для POJO объектов
 *
 * Структура итогового массива байтов byte[]bytes:
 *
 * xx xx xx xx - длина итогового массива (int), извлекается  getLength(byte[] bytes)
 * xx - длина имени класса (byte)
 * xx ... xx - массив имени класса (String)
 * xx - количество полей (byte)
 * xx ... xx - поля
 * поля:
 * xx xx xx xx - длина массива поля, извлекается  getLength(byte[] bytes)
 * xx - длина имени класса поля (byte)
 * xx ... xx - массив имени класса поля
 * xx ... xx - массив значения поля, может содержать свои поля.
 *
 *  *длина имени класса (поля) включает в себя себя и остальные данные, принадлежащие классу (полю)
 *
 * @author Alexander Vlasov
 */
public class ObjectSerializator extends Serializator {
    public static void main(String[] args) {
        Serializator serializator = new ObjectSerializator();
        System.out.println(Arrays.toString(serializator.debuild(new Person("Alex"))));
        serializator = new PersonSerializator();
        System.out.println(Arrays.toString(serializator.debuild(new Person("Alex"))));
    }

    @Override
    public Object build(byte[] bytes, int off) {
        if (bytes[0] == CLASS) {
            Serializator serializator = new StringSerializator();

            String className = (String) serializator.build(bytes, 5);
            try {
                Class clazz = Class.forName(className);
                Object object = (clazz.newInstance());
                Field[] fields = clazz.getDeclaredFields();
                byte[][] splitted = split(bytes);

                for (int i = 0; i < fields.length; i++) {

                    Field field = fields[i];
                    Class fieldClass = field.getType();
                    String fieldName = field.getName();
                    Character.toUpperCase(fieldName.charAt(0));
                    String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                    System.out.println(setterName);
                    Object sample;
                    if (fieldClass == Boolean.class || fieldClass == boolean.class) sample = new Boolean(true);
                    else sample = fieldClass.newInstance();
                    Object value = build(sample, splitted[i]);
                    clazz.getMethod(setterName, fieldClass).invoke(object, value);
                }
                return object;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

//        StringSerializator stringSerializator = new StringSerializator();
//        String name = stringSerializator.build(splitted[0]);
//        BooleanSerializator booleanSerializator = new BooleanSerializator();
//        boolean online = booleanSerializator.build(splitted[1]);
//        System.out.println(name);
//        System.out.println(online);
//        Person person = new Person(name);
//        person.setOnline(online);
//        return person;
        return null;

    }

    @Override
    public byte[] debuild(Object object) {
        Class clazz = object.getClass();
//        System.out.println(clazz);

        Field[] fields = clazz.getDeclaredFields();
//        System.out.println(Arrays.toString(fields));
        byte[][] bytes = new byte[fields.length][];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
//            System.out.println(field.getType());
//            System.out.println(field.getName());
//            System.out.println(field.toString());
            Class fieldClass = field.getType();
            String getterPrefix;
            if (getCode(fieldClass) == BOOLEAN) {
                getterPrefix = "is";
            } else {
                getterPrefix = "get";
            }
            try {
                String fieldName = field.getName();
                Character.toUpperCase(fieldName.charAt(0));
                String getterName = getterPrefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                System.out.println(getterName);
                Object value = clazz.getMethod(getterName).invoke(object);
                if (containsCode(value.getClass())) {
//                    bytes[i] = getBytes(value);
                    bytes[i] = super.debuild(value);
                } else {
                    bytes[i] = debuild(value);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return pack(clazz, bytes);
    }
}
