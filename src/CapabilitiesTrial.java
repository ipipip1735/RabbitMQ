import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2020/1/10 17:08.
 */
public class CapabilitiesTrial {
    private final String QUEUE = "ooo";

    public static void main(String[] args) {
        CapabilitiesTrial capabilitiesTrial = new CapabilitiesTrial();

        ConnectionFactory connectionFactory = capabilitiesTrial.getFactory();

//        capabilitiesTrial.send(connectionFactory);
//        capabilitiesTrial.abnormalCancel(connectionFactory);

    }

    private void abnormalCancel(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);//流速控制
            System.out.println(channel);

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
                    connection.close();
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

                    if (envelope.getDeliveryTag() == 5) channel.queueDelete(QUEUE);
//                    if (envelope.getDeliveryTag() == 5) channel.basicCancel(consumerTag);


                    if (envelope.getDeliveryTag() == 10)
                        try {
                            channel.close();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }

                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

    private ConnectionFactory getFactory() {

        ConnectionFactory factory = new ConnectionFactory();

//        Map<String, Object> properties = factory.getClientProperties();
//        Map capabilities = (Map) properties.get("capabilities");
//        capabilities.put("consumer_cancel_notify", false);

        System.out.println(factory.getClientProperties());


        return factory;


    }

    private void send(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

//            System.out.println(connection.getClientProperties());
//            System.out.println(connection.getServerProperties());


            AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(QUEUE, false, false, false, null);
            channel.queueBind(QUEUE, "amq.direct", "one");
            System.out.println(declareOk);
            System.out.println("getQueue is " + declareOk.getQueue());
            System.out.println("getMessageCount is " + declareOk.getMessageCount());
            System.out.println("getConsumerCount is " + declareOk.getConsumerCount());

            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish("amq.direct", "one", null, message.getBytes());
            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
