import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2020/1/10 15:18.
 */
public class ReturnMessageTrial {

    public static void main(String[] args) {
        ReturnMessageTrial confirmTrial = new ReturnMessageTrial();
        confirmTrial.pubulish();
    }

    private void pubulish() {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.addReturnListener(new ReturnListener() {
                @Override
                public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("~~handleReturn~~");
                    System.out.println("replyCode is " + replyCode);
                    System.out.println("replyText is " + replyText);
                    System.out.println("exchange is " + exchange);
                    System.out.println("routingKey is " + routingKey);
                    System.out.println("properties is " + properties);
                    System.out.println("body is " + new String(body));
                }
            });

            channel.queueDeclare("", false, false, false, null);
            channel.queueBind("", "amq.direct", "one");


            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish("amq.direct", "one1", true,null, message.getBytes());
            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
