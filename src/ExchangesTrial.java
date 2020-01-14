import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2019/12/25 11:43.
 */
public class ExchangesTrial {

    private final String E_TOPIC = "eTopic";
    private final String E_Fanout = "eFanout";
    private final String E_DIRECT = "eDirect";
    private final String E_HEADER = "eHeader";
    private final String E_ONE = "eOne";
    private final String E_TWO = "eTwo";

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


        //交换属性
//        exchangesTrial.create(connectionFactory);


        //扇出交换
//        exchangesTrial.fanoutReceive(connectionFactory);
//        exchangesTrial.fanoutSend(connectionFactory);


        //话题交换
//        exchangesTrial.topicReceive(connectionFactory);
//        exchangesTrial.topicSend(connectionFactory);


        //直接交换
//        exchangesTrial.directReceive(connectionFactory);
//        exchangesTrial.directSend(connectionFactory);


        //头信息交换
//        exchangesTrial.headerReceive(connectionFactory);
//        exchangesTrial.headerSend(connectionFactory);


        //交换链
//        exchangesTrial.bindExchange(connectionFactory);
//        exchangesTrial.sendToExchange(connectionFactory);

        //备用交换
        exchangesTrial.alterExchange(connectionFactory);
//        exchangesTrial.sendAlter(connectionFactory);


    }

    private void sendAlter(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish(E_ONE, "ete.one", null, message.getBytes());
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void alterExchange(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            channel.queueDeclare(Q_ONE, false, false, false, null);
            channel.queueDeclare(Q_TWO, false, false, false, null);

            channel.exchangeDeclare(E_ONE, BuiltinExchangeType.DIRECT, false, false, null);
            channel.exchangeDeclare(E_TWO, BuiltinExchangeType.DIRECT, false, false, null);

            channel.queueBind(Q_ONE, E_ONE, "one");
            channel.queueBind(Q_TWO, E_TWO, "two");

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendToExchange(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish(E_ONE, "ete.two", null, message.getBytes());
//                channel.basicPublish(E_ONE, "one", null, message.getBytes());
//                channel.basicPublish(E_TWO, "two", null, message.getBytes());
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void bindExchange(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);

            channel.queueDeclare(Q_ONE, false, false, false, null);
            channel.queueDeclare(Q_TWO, false, false, false, null);

//            channel.exchangeDeclare(E_ONE, BuiltinExchangeType.DIRECT, false, false, null);
//            channel.exchangeDeclare(E_TWO, BuiltinExchangeType.DIRECT, false, false, null);

            channel.exchangeDeclare(E_ONE, BuiltinExchangeType.TOPIC, false, true, null);
            channel.exchangeDeclare(E_TWO, BuiltinExchangeType.TOPIC, false, false, null);

            channel.queueBind(Q_ONE, E_ONE, "#.one");
            channel.queueBind(Q_TWO, E_TWO, "*.two");
            channel.exchangeBind(E_TWO, E_ONE, "ete.#");//绑定交换



            //解绑交换
//            channel.queueUnbind(Q_ONE, E_ONE, "#.one");
//            channel.exchangeUnbind(E_TWO, E_ONE, "ete.#");



        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void create(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(channel);


            //方式一：自动删除交换(所有队列解绑后，自动删除)
//                channel.exchangeDeclare(E_ONE, BuiltinExchangeType.DIRECT, false, true, null);//声明自动删除交换
//                channel.queueDeclare(Q_ONE, false, false, false, null);
//                channel.queueBind(Q_ONE, E_ONE, "one");//绑定队列
//                channel.queueUnbind(Q_ONE, E_ONE, "one");//解绑队，由于交换只绑定了一个队列，所以解绑后将自动删除交换


            //方式二：增加可选参数
//                Map<String, Object> map = new HashMap<>();
//                map.put("xxx", "yyy");
////                map.put("aaa", "bbbb");
//
//                channel.exchangeDeclare(E_ONE, BuiltinExchangeType.DIRECT, false, false, map);
//                channel.queueDeclare(Q_ONE, false, false, false, null);
//                channel.queueBind(Q_ONE, E_ONE, "one");


            //方式三：使用特性（X参数就是特性）
//                channel.exchangeDeclare(E_ONE, BuiltinExchangeType.DIRECT, false, false, null);

//            Map<String, Object> map = new HashMap<>();
//            map.put("x-max-length", 1048576);//增加X参数
//            channel.exchangeDeclare(E_TWO, BuiltinExchangeType.DIRECT, false, false, map);


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void headerSend(ConnectionFactory connectionFactory) {

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            System.out.println(channel);


            channel.exchangeDeclare(E_HEADER, "headers");
            channel.queueDeclare(Q_ONE, false, false, false, null);

            Map<String, Object> map = new HashMap<>();
            map.put("x-match", "any");
            map.put("xxx", "yyy");
            channel.queueBind(Q_ONE, E_HEADER, "ddd", map);


            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

            Map<String, Object> pros = new HashMap<>();
            pros.put("xxx", "yyy");
            AMQP.BasicProperties basicProperties = builder.headers(pros).build();


            for (int i = 0; i < 10; i++) {
                String message = "[Msg]" + i;
                channel.basicPublish(E_HEADER, "rdo", basicProperties, message.getBytes());
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void headerReceive(ConnectionFactory connectionFactory) {

        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();


            System.out.println(channel);


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

            CancelCallback cancelCallback = consumerTag -> {
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

            CancelCallback cancelCallback = consumerTag -> {
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

            CancelCallback cancelCallback = consumerTag -> {
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

            CancelCallback cancelCallback = consumerTag -> {
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
//        factory.setHost(host);
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
