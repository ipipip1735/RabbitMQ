import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2020/1/27 22:28.
 */
public class ClusterTrial {
    private final String QUEUE = "ooo";
    private final String QUEUE_ONE = "ha-one";
    private final String EXCHANGE = "eone";

    private String userName = "one";
    private String password = "one";
    private String virtualHost = "/";
        private String host = "192.168.0.125";
//    private String host = "192.168.0.127";
//        private String host = "192.168.0.27";
    private int port = 5672;

    public static void main(String[] args) {

        ClusterTrial clusterTrial = new ClusterTrial();

        ConnectionFactory connectionFactory = clusterTrial.getFactory();
        clusterTrial.receive(connectionFactory);
//        clusterTrial.send(connectionFactory);

    }


    private void receive(ConnectionFactory connectionFactory) {

        try {
            //方式一：使用单节点
            Connection connection = connectionFactory.newConnection();

            //方式二：使用多节点列表
//            Address[] addresses = new Address[3];
//            addresses[0] = new Address("192.168.0.127", port);
//            addresses[1] = new Address("192.168.0.125", port);
//            addresses[2] = new Address("192.168.0.27", port);
//            Connection connection = connectionFactory.newConnection(addresses);


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


                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//手动确认


//                if (message.equals("[Msg]9")) {
//                    try {
//                        System.out.println("channel.close!");
//                        channel.close();
//                    } catch (TimeoutException e) {
//                        e.printStackTrace();
//                    }
//                }

//                try {
//                    Thread.sleep(3000L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
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

                    channel.basicAck(envelope.getDeliveryTag(), false);//手动确认


                    if (envelope.getDeliveryTag() == 5) channel.basicCancel(consumerTag);

                    if (envelope.getDeliveryTag() == 10)
                        try {
                            channel.close();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }

//                    try {
//                        Thread.sleep(1000L);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                }
            };

            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-cancel-on-ha-failover", true);


            channel.basicConsume(QUEUE_ONE, false, args,
                    deliverCallback,
                    cancelCallback,
                    consumerShutdownSignalCallback);

//            channel.basicConsume(QUEUE_ONE, false, args, consumer);


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


        Address[] addresses = new Address[3];
        addresses[0] = new Address("192.168.0.127", port);
        addresses[1] = new Address("192.168.0.125", port);
        addresses[2] = new Address("192.168.0.27", port);

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            //非持久队列
//            channel.queueDeclare(QUEUE_ONE, false, false, false, null);
            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, false, false, null);
            channel.queueBind(QUEUE_ONE, EXCHANGE, "q.*");
            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish(EXCHANGE, "q.one", null, message.getBytes());
            }


            //持久队列
//            channel.queueDeclare(QUEUE, true, false, false, null);
//            channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC, false, false, null);
//            channel.queueBind(QUEUE, EXCHANGE, "q.*");
//            AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
//                    .deliveryMode(2)//持久模式
//                    .build();
//
//            for (int i = 0; i < 10; i++) {
//                String message = "[Msg]" + i;
////                channel.basicPublish(EXCHANGE, "q.one", null, message.getBytes());
//                channel.basicPublish(EXCHANGE, "q.one", props, message.getBytes());
//            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
