package jpractice.chat;

import javafx.scene.Node;
import javafx.scene.text.Text;

import java.io.Serializable;

/**
 * @author Alexander Vlasov
 */
public class Person implements Serializable {
    private String name;
    private boolean online;

    public Person(String name) {
        this.name = name;
        online = true;
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

    public Node getVisual() {
        return new Text(name);
    }
}
