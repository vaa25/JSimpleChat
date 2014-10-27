package jpractice.chat.networks.serializators;

import jpractice.chat.Person;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * @author Alexander Vlasov
 */
public class ObjectSerializator<T extends Object> {
    public static void main(String[] args) {
        ObjectSerializator<Person> serializator = new ObjectSerializator<>();
        serializator.debuild(new Person("Alex"));
    }

    public void debuild(T object) {
        Class clazz = object.getClass();
        System.out.println(clazz);

        Field[] fields = clazz.getFields();
        System.out.println(Arrays.toString(fields));
        for (Field field : fields) {
            Object value = null;
            //todo
        }
    }
}
