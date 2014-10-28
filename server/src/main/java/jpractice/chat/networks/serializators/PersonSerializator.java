package jpractice.chat.networks.serializators;

import jpractice.chat.Person;

import java.util.Arrays;

/**
 * @author Alexander Vlasov
 */
public class PersonSerializator extends Serializator<Person> {
    public static void main(String[] args) {
        Serializator serializator = new Serializator();
        byte[] bytes = serializator.debuild(new Person("Саша"));
//        bytes[0]=9;
        System.out.println(Arrays.toString(bytes));
        System.out.println(((Person) (serializator.build(new Person(""), bytes))).getName());

    }

    @Override
    public Person build(byte[] bytes, int off) {
        byte[][] splitted = split(bytes);
        StringSerializator stringSerializator = new StringSerializator();
        String name = stringSerializator.build(splitted[0]);
        BooleanSerializator booleanSerializator = new BooleanSerializator();
        boolean online = booleanSerializator.build(splitted[1]);
        System.out.println(name);
        System.out.println(online);
        Person person = new Person(name);
        person.setOnline(online);
        return person;

    }

    //    @Override
    public byte[] debuild(Person person) {
        byte[][] bytes = new byte[2][];

        StringSerializator stringSerializator = new StringSerializator();
        byte[] name = stringSerializator.debuild(person.getName());
        BooleanSerializator booleanSerializator = new BooleanSerializator();
        byte[] online = booleanSerializator.debuild(person.isOnline());
        bytes[0] = name;
        System.out.println(Arrays.toString(bytes[0]));
        bytes[1] = online;
        System.out.println(Arrays.toString(bytes[1]));
        return pack(Person.class, bytes);
    }
}
