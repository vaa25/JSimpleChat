package jpractice.chat.websocket;

/**
 * @author Alexander Vlasov
 */
public class Person {
    private String name;
    private boolean online;
    private String id;

    public Person() {
        this("Alex");
    }

    public Person(String name) {
        this.name = name;
        online = true;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        if (!id.equals(((Person) o).id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", online=" + online +
                '}';
    }
}
