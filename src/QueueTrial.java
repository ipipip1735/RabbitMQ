import com.rabbitmq.client.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
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

//        queueTrial.pull(connectionFactory);
    }

    private void pull(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            //方式一
//            GetResponse getResponse = channel.basicGet(QUEUE, true);
//            System.out.println(getResponse);
//            System.out.println("getBody is " + getResponse.getBody().length);
//            System.out.println("getEnvelope is " + getResponse.getEnvelope());
//            System.out.println("getMessageCount is " + getResponse.getMessageCount());
//            System.out.println("getProps is " + getResponse.getProps());


            //方式二
            GetResponse getResponse = channel.basicGet(QUEUE, false);
            System.out.println(getResponse);
            System.out.println("getBody is " + getResponse.getBody().length);
            System.out.println("getEnvelope is " + getResponse.getEnvelope());
            System.out.println("getMessageCount is " + getResponse.getMessageCount());
            System.out.println("getProps is " + getResponse.getProps());

            channel.basicAck(getResponse.getEnvelope().getDeliveryTag(), false);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void receiveWithDefaultConsumer(ConnectionFactory connectionFactory) {


        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);//流速控制
            System.out.println(channel);

            channel.queueDeclare(QUEUE, false, false, false, null);//声明队列


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
//        factory.setHost(host);


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
            channel.basicQos(5);//流速控制

            System.out.println(channel);

//            channel.queueDeclare(QUEUE, false, false, false, null);

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


//                if (message.equals("[Msg]4")) {
//                    channel.basicCancel(consumerTag);
//                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//手动确认
//                    System.out.println("channel.basicCancel!");
//                    connection.close();//关闭连接
//                } else {
//                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//手动确认
//                }


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


//                if ( delivery.getEnvelope().getDeliveryTag() % 5 == 0) {
//                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);//使用批量肯定确认
//                    System.out.println(delivery.getEnvelope().getDeliveryTag() + " ACK!");
//                }


//                if ( delivery.getEnvelope().getDeliveryTag() % 5 == 0) {
//                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), true, true);//使用批量否定确认
//                    System.out.println(delivery.getEnvelope().getDeliveryTag() + " Nack!");
//                }


//                if (delivery.getEnvelope().getDeliveryTag() == 3) {
//                    channel.basicReject(delivery.getEnvelope().getDeliveryTag(), true);//使用批量确认
//                    System.out.println(delivery.getEnvelope().getDeliveryTag() + " Reject!");
//                } else {
//                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);//使用批量确认
//                    System.out.println(delivery.getEnvelope().getDeliveryTag() + " ACK!");
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

//            System.out.println(channel);

            //方式一：手动设置队列名
            AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(QUEUE, false, false, false, null);//声明队列
            System.out.println(declareOk);
            System.out.println("getQueue is " + declareOk.getQueue());
            System.out.println("getMessageCount is " + declareOk.getMessageCount());
            System.out.println("getConsumerCount is " + declareOk.getConsumerCount());

            //方式二：自动生成队列名（队列名为空，那么RabbitMQ将创建一个随机字符串作为队列名，通道就要）
//            AMQP.Queue.DeclareOk declareOk = channel.queueDeclare("", false, false, false, null);//指定队列名
//            System.out.println(declareOk);
//            channel.queueBind("", "amq.direct", "one");//给内置交换绑定队列（每个虚拟主机都有7个内置交换）


            //方式三：使用临时队列
//            String queueName =  channel.queueDeclare().getQueue();//声明临时队列（断链后队列被自动删除）
//            System.out.println("queueName is " + queueName);


            //方式四：使用自动删除队列
//            channel.queueDeclare(QUEUE, false, false, true, null);


            //方式五：使用专属队列
//            channel.queueDeclare(QUEUE, false, true, false, null);


            //方式六：使用持久队列
//            channel.queueDeclare(QUEUE, true, false, false, null);


            //方式七：设置队列特性（官方把X参数称为队列拥有的特性）
//            Map<String, Object> args = new HashMap<String, Object>();
////            args.put("x-max-length", 10);//设置队列长度
////            args.put("x-max-priority", 10);//设置队列X参数最大个数
//            args.put("x-queue-type", "quorum");//设置队列类型为法定人数队列
//            channel.queueDeclare(QUEUE, true, false, false, args);//quorum必须为持久队列

//            AMQP.BasicProperties props = new AMQP.BasicProperties().builder()
//                    .deliveryMode(2)
//                    .build();

            for (int i = 0; i < 10; i++) {
//                String message = "[Msg]" + new Random().nextInt(100);
                String message = "[Msg]" + i;

                channel.basicPublish("", QUEUE, null, message.getBytes());
//                channel.basicPublish("amq.direct", "one", null, message.getBytes());//使用内置交换
//                channel.basicPublish("", QUEUE, props, message.getBytes());//发送信息给持久队列

            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
