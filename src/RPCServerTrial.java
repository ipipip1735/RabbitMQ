import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2019/12/25 16:40.
 */
public class RPCServerTrial {

    private final String QUEUE = "qone";

    private String userName = "guest";
    private String password = "guest";
    private String virtualHost = "/";
    private String host = "localhost";
    private int portTSL = 5671;
    private int port = 5672;


    public static void main(String[] args) {

        RPCServerTrial rpcTrial = new RPCServerTrial();


        ConnectionFactory connectionFactory = rpcTrial.getFactory();

        rpcTrial.receive(connectionFactory);
    }

    private void receive(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            System.out.println(channel);


            channel.basicConsume(QUEUE, new DefaultConsumer(channel) {
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

//                    try {
//                        connection.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
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

//                    try {
//                        connection.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
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


                    String reply = "server|" + new String(body);
                    AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                            .correlationId(properties.getCorrelationId())
                            .build();

                    channel.basicPublish("", properties.getReplyTo(), replyProps, reply.getBytes());

                }
            });


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
