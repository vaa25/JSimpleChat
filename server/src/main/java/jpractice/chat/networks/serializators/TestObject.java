package jpractice.chat.networks.serializators;

import jpractice.chat.Person;

/**
 * @author Alexander Vlasov
 */
public class TestObject extends Person {
    private int anInt = 100;
    private String string = "строка";

    public TestObject() {
        super("Саша");
        setOnline(false);
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "super=" + super.toString() +
                ", anInt=" + anInt +
                ", string='" + string + '\'' +
                '}';
    }
}
