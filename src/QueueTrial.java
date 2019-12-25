import com.rabbitmq.client.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2019/12/25 4:32.
 */
public class QueueTrial {
    private final String QUEUE = "ooo";

    private String userName = "guest";
    private String password = "guest";
    private String virtualHost = "/";
    private String host = "localhost";
    private int portTSL = 5671;
    private int port = 5672;


    public static void main(String[] args) {
        QueueTrial queueTrial = new QueueTrial();


        ConnectionFactory connectionFactory = queueTrial.getFactory();


//        queueTrial.sendDurable(connectionFactory);
        queueTrial.send(connectionFactory);

        queueTrial.receive(connectionFactory);
//        queueTrial.receiveWithDefaultConsumer(connectionFactory);
    }

    private void receiveWithDefaultConsumer(ConnectionFactory connectionFactory) {


        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);//流速控制

            System.out.println(channel);

            channel.queueDeclare(QUEUE, false, false, false, null);//不能声明同名队列，否则将覆盖前面队列的数据


            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleConsumeOk(String consumerTag) {
                    System.out.println("~~handleConsumeOk~~");
                }

                @Override
                public void handleCancelOk(String consumerTag) {
                    System.out.println("~~handleCancelOk~~");
                }

                @Override
                public void handleCancel(String consumerTag) throws IOException {
                    System.out.println("~~handleCancel~~");
                }

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.out.println("~~handleShutdownSignal~~");
                }

                @Override
                public void handleRecoverOk(String consumerTag) {
                    System.out.println("~~handleRecoverOk~~");
                }

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("~~handleDelivery~~");
                    System.out.println(new String(body));
//                    channel.basicCancel("ccc");
                }
            };

            String consumerTag = channel.basicConsume(QUEUE, true, consumer);
            System.out.println(consumerTag);




        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


    }

    private void sendDurable(ConnectionFactory factory) {

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            channel.queueDeclare(QUEUE, true, false, false, null);

            for (int i = 0; i < 10; i++) {

                String message = "[Msg]" + new Random().nextInt(100);
                channel.basicPublish("", QUEUE,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes());
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

    private void receive(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);//流速控制

            System.out.println(channel);

            channel.queueDeclare(QUEUE, false, false, false, null);//不能声明同名队列，否则将覆盖前面队列的数据

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println("~~deliverCallback~~");
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("delivery.getBody is " + message);
                System.out.println("delivery.getEnvelope is " + delivery.getEnvelope());
                System.out.println("delivery.getProperties is " + delivery.getProperties());


                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (message.equals("[Msg]9")) {
                    try {
                        channel.close();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                }

                if (message.equals("[Msg]5")) {
                    channel.basicCancel("basic.cancel");
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

            channel.queueDeclare(QUEUE, false, false, false, null);

            for (int i = 0; i < 10; i++) {

//                String message = "[Msg]" + new Random().nextInt(100);
                String message = "[Msg]" + i;
                channel.basicPublish("", QUEUE, null, message.getBytes());
            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
