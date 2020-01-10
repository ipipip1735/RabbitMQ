import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2020/1/10 16:12.
 */
public class PublishConfirmTrial {
    private final String QUEUE = "ooo";

    public static void main(String[] args) {
        PublishConfirmTrial publishConfirmTrial = new PublishConfirmTrial();
//        publishConfirmTrial.AsyncPubulishConfirm();
        publishConfirmTrial.syncPubulishConfirm();
    }

    private void syncPubulishConfirm() {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.confirmSelect();
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

            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("~~handleAck~~");
                    System.out.println("deliveryTag is " + deliveryTag);
                    System.out.println("multiple is " + multiple);
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("~~handleNack~~");
                    System.out.println("deliveryTag is " + deliveryTag);
                    System.out.println("multiple is " + multiple);

                }
            });

            channel.queueDeclare(QUEUE, false, false, false, null);
            channel.queueBind(QUEUE, "amq.direct", "one");

            for (int i = 0; i < 1000; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish("amq.direct", "one", true, null, message.getBytes());
            }
            channel.waitForConfirms();
            System.out.println("done!");


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void AsyncPubulishConfirm() {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.confirmSelect();
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

            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("~~handleAck~~");
                    System.out.println("deliveryTag is " + deliveryTag);
                    System.out.println("multiple is " + multiple);
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    System.out.println("~~handleNack~~");
                    System.out.println("deliveryTag is " + deliveryTag);
                    System.out.println("multiple is " + multiple);

                }
            });

            channel.queueDeclare(QUEUE, false, false, false, null);
            channel.queueBind(QUEUE, "amq.direct", "one");

            for (int i = 0; i < 100; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish("amq.direct", "one", true, null, message.getBytes());
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
