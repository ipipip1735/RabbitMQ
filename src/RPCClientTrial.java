import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2019/12/25 16:40.
 */
public class RPCClientTrial {

    private final String QUEUE = "qone";
    String callbackQueueName;
    List<UUID> uuids = new ArrayList<>();

    private String userName = "guest";
    private String password = "guest";
    private String virtualHost = "/";
    private String host = "localhost";
    private int portTSL = 5671;
    private int port = 5672;


    public static void main(String[] args) {

        RPCClientTrial rpcTrial = new RPCClientTrial();


        ConnectionFactory connectionFactory = rpcTrial.getFactory();

//        rpcTrial.send(connectionFactory);
        rpcTrial.directSend(connectionFactory);
    }

    private void directSend(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            System.out.println(channel);



            channel.basicConsume("amq.rabbitmq.reply-to", true, new DefaultConsumer(channel) {
                @Override
                public void handleConsumeOk(String consumerTag) {
                    System.out.println("~~receive.handleConsumeOk~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                }

                @Override
                public void handleCancelOk(String consumerTag) {
                    System.out.println("~~receive.handleCancelOk~~");
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
                    System.out.println("~~receive.handleCancel~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                }

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.out.println("~~receive.handleShutdownSignal~~");
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
                    System.out.println("~~receive.handleRecoverOk~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                }

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("~~receive.handleDelivery~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                    System.out.println("envelope is " + envelope);
                    System.out.println("properties is " + properties);
                    System.out.println("body is " + new String(body));

                    uuids.remove(properties.getCorrelationId());
                }
            });



            UUID uuid = UUID.randomUUID();
            uuids.add(uuid);
            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(uuid.toString())
                    .replyTo("amq.rabbitmq.reply-to")
                    .build();

            String message = "xxx";
            channel.basicPublish("", "qone", props, message.getBytes());

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

    private void send(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            System.out.println(channel);

            callbackQueueName = channel.queueDeclare().getQueue();

            UUID uuid = UUID.randomUUID();
            uuids.add(uuid);

            AMQP.BasicProperties props = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(uuid.toString())
                    .replyTo(callbackQueueName)
                    .build();

            String message = "xxx";

            channel.basicPublish("", "qone", props, message.getBytes());
            channel.basicConsume(callbackQueueName, false, new DefaultConsumer(channel) {
                @Override
                public void handleConsumeOk(String consumerTag) {
                    System.out.println("~~receive.handleConsumeOk~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                }

                @Override
                public void handleCancelOk(String consumerTag) {
                    System.out.println("~~receive.handleCancelOk~~");
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
                    System.out.println("~~receive.handleCancel~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                }

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    System.out.println("~~receive.handleShutdownSignal~~");
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
                    System.out.println("~~receive.handleRecoverOk~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                }

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("~~receive.handleDelivery~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("consumerTag is " + consumerTag);
                    System.out.println("envelope is " + envelope);
                    System.out.println("properties is " + properties);
                    System.out.println("body is " + new String(body));

                    channel.basicAck(envelope.getDeliveryTag(), false);//手动确认
                    uuids.remove(properties.getCorrelationId());
                }
            });

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
