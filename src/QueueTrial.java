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


        queueTrial.send(connectionFactory);
//        queueTrial.sendDurable(connectionFactory);

//        queueTrial.receive(connectionFactory);
        queueTrial.receiveWithDefaultConsumer(connectionFactory);
    }

    private void receiveWithDefaultConsumer(ConnectionFactory connectionFactory) {


        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);//流速控制
            System.out.println(channel);

            channel.queueDeclare(QUEUE, false, false, false, null);


            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleConsumeOk(String consumerTag) {
                    System.out.println("~~handleConsumeOk~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                }

                @Override
                public void handleCancelOk(String consumerTag) {
                    System.out.println("~~handleCancelOk~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);

                    try {
                        connection.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void handleCancel(String consumerTag) throws IOException {
                    System.out.println("~~handleCancel~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                }

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.out.println("~~handleShutdownSignal~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                    System.out.println("sig is " + sig);

                    try {
                        connection.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void handleRecoverOk(String consumerTag) {
                    System.out.println("~~handleRecoverOk~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                }

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("~~handleDelivery~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                    System.out.println("envelope is " + envelope);
                    System.out.println("properties is " + properties);
                    System.out.println("body is " + new String(body));

                    if (envelope.getDeliveryTag() == 5) channel.basicCancel(consumerTag);
                    if (envelope.getDeliveryTag() == 10) {
                        try {
                            channel.close();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    } else {
                        channel.basicAck(envelope.getDeliveryTag(), false);//手动确认
                    }

//                    try {
//                        Thread.sleep(1000L);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                }
            };

            String consumerTag = channel.basicConsume(QUEUE, false, consumer);
            System.out.println("consumerTag is " + consumerTag);


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
//        factory.setPort(port);
        factory.setHost(host);

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

            channel.queueDeclare(QUEUE, false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                System.out.println("~~deliverCallback~~");
                System.out.println(Thread.currentThread());
                System.out.println("consumerTag is " + consumerTag);
                System.out.println("delivery.getEnvelope is " + delivery.getEnvelope());
                System.out.println("delivery.getProperties is " + delivery.getProperties());
                String message = new String(delivery.getBody());
                System.out.println("delivery.getBody is " + message);


                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


//                if (message.equals("[Msg]3")) {
//                    try {
//                        System.out.println("channel.close!");
//                        channel.close();//关闭通道
//                    } catch (TimeoutException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//手动确认
//                }


                if (message.equals("[Msg]4")) {
                    channel.basicCancel(consumerTag);
                    System.out.println("channel.close!");
                    connection.close();//关闭连接
                } else {
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//手动确认
                }


//                if (message.equals("[Msg]5")) {
//                    channel.queuePurge(QUEUE);//清空队列
//                    System.out.println("channel.queuePurge!");
//                    try {
//                        System.out.println("channel.close!");
//                        channel.close();//关闭通道
//                    } catch (TimeoutException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//手动确认
//                }

//                if (message.equals("[Msg]7")) {
//                    channel.queueDelete(QUEUE);//删除队列
//                    System.out.println("channel.queueDelete!");
//                } else {
//                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//手动确认
//                }

//                if (message.equals("[Msg]9")) {
//                    try {
//                        System.out.println("channel.close!");
//                        channel.close();
//                    } catch (TimeoutException e) {
//                        e.printStackTrace();
//                    }
//                }
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

            String consumerTag = channel.basicConsume(QUEUE, false,
                    deliverCallback,
                    cancelCallback,
                    consumerShutdownSignalCallback);
            System.out.println(consumerTag);

//            try {
//                Thread.sleep(3000L);
//                channel.basicCancel(consumerTag);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

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
            channel.queuePurge(QUEUE);


//            String queueName =  channel.queueDeclare().getQueue();//声明临时队列
//            System.out.println("queueName is " + queueName);


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
