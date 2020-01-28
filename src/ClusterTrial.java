import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2020/1/27 22:28.
 */
public class ClusterTrial {
    private final String QUEUE = "ooo";
    private final String EXCHANGE = "eone";

    private String userName = "one";
    private String password = "one";
    private String virtualHost = "vho";
    private String host = "192.168.0.126";
//    private String host = "192.168.0.110";
    private int port = 5672;

    public static void main(String[] args) {

        ClusterTrial clusterTrial = new ClusterTrial();

        clusterTrial.getFactory();


        ConnectionFactory connectionFactory = clusterTrial.getFactory();
        clusterTrial.receive(connectionFactory);
//        clusterTrial.send(connectionFactory);

    }


    private void receive(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);//流速控制

            System.out.println(channel);

//            channel.queueDeclare(QUEUE, false, false, false, null);
//            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, false, false, null);
//            channel.queueBind(QUEUE, EXCHANGE, "q.*");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println("~~deliverCallback~~");
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
                System.out.println("delivery.getEnvelope is " + delivery.getEnvelope());
                System.out.println("delivery.getProperties is " + delivery.getProperties());
                String message = new String(delivery.getBody());
                System.out.println("delivery.getBody is " + message);


//                try {
//                    Thread.sleep(2000L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//手动确认


                if (message.equals("[Msg]9")) {
                    try {
                        System.out.println("channel.close!");
                        channel.close();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }

            };

            CancelCallback cancelCallback = consumerTag -> {
                System.out.println("~~cancelCallback~~");
                System.out.println("consumerTag is " + consumerTag);
                System.out.println(Thread.currentThread());

                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };


            ConsumerShutdownSignalCallback consumerShutdownSignalCallback = (consumerTag, sig) -> {
                System.out.println("~~consumerShutdownSignalCallback~~");
                System.out.println("consumerTag is " + consumerTag);
                System.out.println("sig is " + sig);
                System.out.println(Thread.currentThread());
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            channel.basicConsume(QUEUE, false,
                    deliverCallback,
                    cancelCallback,
                    consumerShutdownSignalCallback);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }

    private ConnectionFactory getFactory() {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setPort(port);
        factory.setHost(host);

        return factory;
    }



    private void send(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            channel.queueDeclare(QUEUE, true, false, false, null);
            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, false, false, null);
            channel.queueBind(QUEUE, EXCHANGE, "q.*");

            AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
                    .deliveryMode(2)
                    .build();

            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
//                channel.basicPublish(EXCHANGE, "q.one", null, message.getBytes());
                channel.basicPublish(EXCHANGE, "q.one", props, message.getBytes());
            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
