import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2020/1/10 17:08.
 */
public class CapabilitiesTrial {
    private final String QUEUE = "ooo";

    public static void main(String[] args) {
        CapabilitiesTrial capabilitiesTrial = new CapabilitiesTrial();
        capabilitiesTrial.cancelNotification();

    }

    private void cancelNotification() {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            System.out.println(factory.getClientProperties());

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            System.out.println(connection.getClientProperties());
            System.out.println(connection.getServerProperties());

//            channel.queueDeclare(QUEUE, false, false, false, null);
//            channel.queueBind(QUEUE, "amq.direct", "one");




//            for (int i = 0; i < 1000; i++) {
//                String message = "[Msg]" + i;
//                channel.basicPublish("amq.direct", "one", true, null, message.getBytes());
//            }
//            System.out.println("done!");


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
