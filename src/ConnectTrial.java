import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2020/1/2 4:48.
 */
public class ConnectTrial {
    private String QUEUE = "ooo";
    private String userName = "guest";
    private String password = "guest";
    private String virtualHost = "/";
    private String host = "localhost";
    private int portTSL = 5671;
    private int port = 5672;


    public static void main(String[] args) {
        ConnectTrial connectTrial = new ConnectTrial();

//        connectTrial.connect();
        connectTrial.addListener();
//        connectTrial.addProperties();


    }

    private void addProperties() {

        ConnectionFactory factory = new ConnectionFactory();

        //获取属性集
        System.out.println(factory.getClientProperties());

        //修改属性
//        Map<String, Object> properties = factory.getClientProperties();
//        Map capabilities = (Map) properties.get("capabilities");
//        capabilities.put("consumer_cancel_notify", false);


        //增加属性
//        factory.getClientProperties().put("x-queue-mode", "lazy");


        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addListener() {


        ConnectionFactory factory = new ConnectionFactory();

        try {

            Connection connection = factory.newConnection();
            connection.addBlockedListener(new BlockedListener() {
                @Override
                public void handleBlocked(String reason) throws IOException {
                    System.out.println("~~handleBlocked~~");
                    System.out.println(Thread.currentThread());
                    System.out.println("reason is " + reason);
                }

                @Override
                public void handleUnblocked() throws IOException {
                    System.out.println("~~handleUnblocked~~");
                    System.out.println(Thread.currentThread());

                }
            });

            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE, false, false, false, null);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        String message = "[Msg]" + i;
                        System.out.println("send " + message);
                        try {
                            channel.basicPublish("", QUEUE, null, message.getBytes());
                            Thread.sleep(100L);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();


        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {


        ConnectionFactory factory = new ConnectionFactory();
//        //factory.setUsername(userName);
//        //factory.setPassword(password);
//        //factory.setVirtualHost(virtualHost);
//        //factory.setPort(port);
//        //factory.setHost(host);

        factory.setRequestedChannelMax(10);
//        //factory.setSslContextFactory(sslContext);


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


        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
