package jpractice.chat.networks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Через этот класс можно посылать объекты через заданный ObjectOutputStream
 *
 * @author Alexander Vlasov
 */
public class ObjectSender {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectOutputStream objectOutputStream;

    public ObjectSender(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public void sendObject(Object object) {
        logger.info(Thread.currentThread().getName() + " ObjectSender пытается послать сообщение " + object);
        try {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
        } catch (IOException e) {
            logger.error(Thread.currentThread().getName() + "ObjectReceiver (" + Thread.currentThread().getName() + ") IOException: ", e);
        }
        logger.info(Thread.currentThread().getName() + " ObjectSender послал сообщение " + object);
    }
}
