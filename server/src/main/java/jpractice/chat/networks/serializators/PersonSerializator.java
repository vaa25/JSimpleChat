package jpractice.chat.networks.serializators;

import jpractice.chat.Person;

/**
 * @author Alexander Vlasov
 */
public class PersonSerializator extends Serializator<Person> {
    public static void main(String[] args) {
        PersonSerializator personSerializator = new PersonSerializator();
        byte[] bytes = personSerializator.debuild(new Person("Alex"));
//        bytes[0]=9;
//        System.out.println(Arrays.toString(bytes));
        System.out.println(personSerializator.build(bytes).getName());

    }

    @Override
    public Person build(byte[] bytes) {
        if (bytes[0] != Serializator.Person) {
            throw new NotExpectedContent(bytes[0] + " instead of " + Serializator.Person);
        }
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

    @Override
    public byte[] debuild(Person person) {
        byte[][] bytes = new byte[2][];
        StringSerializator stringSerializator = new StringSerializator();
        byte[] name = stringSerializator.debuild(person.getName());
        BooleanSerializator booleanSerializator = new BooleanSerializator();
        byte[] online = booleanSerializator.debuild(person.isOnline());
        bytes[0] = name;
        bytes[1] = online;
        return pack(Serializator.Person, bytes);
    }
}
