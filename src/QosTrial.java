import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2020/1/12.
 */
public class QosTrial {
    private final String QUEUE = "ooo";


    public static void main(String[] args) {

        QosTrial qosTrial = new QosTrial();
        ConnectionFactory connectionFactory = qosTrial.getFactory();

        qosTrial.send(connectionFactory);
        qosTrial.receive(connectionFactory);


    }

    private void receive(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(5, true);//流速控制

            channel.basicConsume(QUEUE, false, new Customer(channel));
            channel.basicConsume(QUEUE, false, new Customer(channel));


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


            channel.queueDeclare(QUEUE, false, false, false, null);
            channel.queueBind(QUEUE, "amq.direct", "one");

            for (int i = 0; i < 100; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish("amq.direct", "one", null, message.getBytes());
            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Customer extends DefaultConsumer {
        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public Customer(Channel channel) {
            super(channel);
        }


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
                getChannel().getConnection().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleCancel(String consumerTag) throws IOException {
            System.out.println("~~handleCancel~~");
            System.out.println(Thread.currentThread());
            System.out.println("consumerTag is " + consumerTag);

            getChannel().getConnection().close();
        }

        @Override
        public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
            System.out.println("~~handleShutdownSignal~~");
            System.out.println(Thread.currentThread());
            System.out.println("consumerTag is " + consumerTag);
            System.out.println("sig is " + sig);

            try {
                getChannel().getConnection().close();
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

//            getChannel().basicAck(envelope.getDeliveryTag(), false);//手动确认


        }

    }

}
