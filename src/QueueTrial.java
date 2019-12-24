import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2019/12/25 4:32.
 */
public class QueueTrial {
    private final static String QUEUE_NAME = "ooo";

    public static void main(String[] args) {
        QueueTrial queueTrial = new QueueTrial();

        queueTrial.send();
    }

    private void send() {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            System.out.println(channel);


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
