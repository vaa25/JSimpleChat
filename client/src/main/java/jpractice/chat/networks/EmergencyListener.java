package jpractice.chat.networks;

/**
 * @author Alexander Vlasov
 */
public interface EmergencyListener<T> {
    public void takeFromParser(T object);
}
