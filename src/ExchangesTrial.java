import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2019/12/25 11:43.
 */
public class ExchangesTrial {

    private final String QUEUE = "qone";
    private final String  EXCHANGE = "eone";

    private String userName = "guest";
    private String password = "guest";
    private String virtualHost = "/";
    private String host = "localhost";
    private int portTSL = 5671;
    private int port = 5672;

    public static void main(String[] args) {

        ExchangesTrial exchangesTrial = new ExchangesTrial();

        ConnectionFactory connectionFactory = exchangesTrial.getFactory();

        exchangesTrial.receive(connectionFactory);
//        exchangesTrial.send(connectionFactory);


        exchangesTrial.sendTopic(connectionFactory);


    }

    private void sendTopic(ConnectionFactory connectionFactory) {


        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);



            channel.exchangeDeclare(EXCHANGE, "topic");
//            channel.queueDeclare(QUEUE, false, false, false, null);
//            channel.queueBind(QUEUE, EXCHANGE, routingKey);


            String routingKey = "rrr.xy.y.xx";
            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish(EXCHANGE, routingKey, null, message.getBytes());
//                channel.basicPublish("", QUEUE, null, message.getBytes());
            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void receive(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);//流速控制

            System.out.println(channel);

            String routingKey = "rrr.#.xx";

//            channel.exchangeDeclare(EXCHANGE, "fanout");
            channel.queueDeclare(QUEUE, false, false, false, null);
            channel.queueBind(QUEUE, EXCHANGE, routingKey);


            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println("~~deliverCallback~~");
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("delivery.getBody is " + message);
                System.out.println("delivery.getEnvelope is " + delivery.getEnvelope());
                System.out.println("delivery.getProperties is " + delivery.getProperties());


//                try {
//                    Thread.sleep(1000L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

//                if (message.equals("[Msg]5")) {
//                    channel.basicCancel("basic.cancel");
//                }

                if (message.equals("[Msg]9")) {
                    try {
                        channel.close();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }


            };

            CancelCallback cancelCallback = consumerTag->{
                System.out.println("~~cancelCallback~~");
                System.out.println("consumerTag is " + consumerTag);
                System.out.println(Thread.currentThread());
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

            String consumerTag = channel.basicConsume(QUEUE, true,
                    deliverCallback,
                    cancelCallback,
                    consumerShutdownSignalCallback);
            System.out.println(consumerTag);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }

    private void send(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);



            channel.exchangeDeclare(EXCHANGE, "fanout");
//            channel.queueDeclare(QUEUE, false, false, false, null);
//            channel.queueBind(QUEUE, EXCHANGE, routingKey);


            String routingKey = "rrr";
            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish(EXCHANGE, routingKey, null, message.getBytes());
//                channel.basicPublish("", QUEUE, null, message.getBytes());
            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ConnectionFactory getFactory() {

        ConnectionFactory factory = new ConnectionFactory();
//        factory.setUsername(userName);
//        factory.setPassword(password);
//        factory.setVirtualHost(virtualHost);
        factory.setHost(host);
//        factory.setPort(port);

//        try {
//            URI uri = new URI("amqp://userName:password@hostName:portNumber/virtualHost");
//            factory.setUri(uri);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }

        return factory;
    }
}
