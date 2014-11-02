package jpractice.chat;

import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * @author Alexander Vlasov
 */
public class Person {
    private String name;
    private boolean online;
    private transient Text image;

    public Person() {
        this("Alex");
    }

    public Person(String name) {
        this.name = name;
        online = true;
//        image=new Text(name);
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getVisual() {
        if (image == null) {
            image = new Text(name);
        }
        return image;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", online=" + online +
                '}';
    }
}
