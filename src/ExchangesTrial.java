import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2019/12/25 11:43.
 */
public class ExchangesTrial {

    private final String E_TOPIC = "eTopic";
    private final String E_Fanout = "eFanout";
    private final String E_DIRECT = "eDirect";

    private final String Q_ONE = "qOne";
    private final String Q_TWO = "qTwo";


    private String userName = "guest";
    private String password = "guest";
    private String virtualHost = "/";
    private String host = "localhost";
    private int portTSL = 5671;
    private int port = 5672;

    public static void main(String[] args) {

        ExchangesTrial exchangesTrial = new ExchangesTrial();

        ConnectionFactory connectionFactory = exchangesTrial.getFactory();


//        exchangesTrial.topicReceive(connectionFactory);
//        exchangesTrial.topicSend(connectionFactory);


//        exchangesTrial.directReceive(connectionFactory);
//        exchangesTrial.directSend(connectionFactory);


        exchangesTrial.fanoutReceive(connectionFactory);
        exchangesTrial.fanoutSend(connectionFactory);

    }


    private void directReceive(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();


            System.out.println(channel);

            channel.exchangeDeclare(E_DIRECT, "direct");
            channel.queueDeclare(Q_ONE, false, false, false, null);
            channel.queueBind(Q_ONE, E_DIRECT, "ddd");


            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println("~~deliverCallback~~");
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
                String message = new String(delivery.getBody());
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
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
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

            channel.basicConsume(Q_ONE, true,
                    deliverCallback,
                    cancelCallback,
                    consumerShutdownSignalCallback);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }

    private void fanoutReceive(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();


            System.out.println(channel);

            channel.exchangeDeclare(E_Fanout, "fanout");
            channel.queueDeclare(Q_ONE, false, false, false, null);
            channel.queueBind(Q_ONE, E_Fanout, "rrr.#");


            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println("~~deliverCallback~~");
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
                String message = new String(delivery.getBody());
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
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
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

            String consumerTag = channel.basicConsume(Q_ONE, true,
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

private void topicReceive(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();


            System.out.println(channel);

            channel.exchangeDeclare(E_TOPIC, "topic");
            channel.queueDeclare(Q_ONE, false, false, false, null);
            channel.queueBind(Q_ONE, E_TOPIC, "rrr.#");


            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println("~~deliverCallback~~");
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
                String message = new String(delivery.getBody());
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
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
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

            String consumerTag = channel.basicConsume(Q_ONE, true,
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

    private void directSend(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish(E_DIRECT, "ddd", null, message.getBytes());
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void fanoutSend(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish(E_Fanout, "rdo", null, message.getBytes());
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void topicSend(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish(E_TOPIC, "rrr", null, message.getBytes());
//                channel.basicPublish(E_TOPIC, "rrtr", null, message.getBytes());
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
