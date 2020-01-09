import com.rabbitmq.client.*;

import java.io.IOException;
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
//        connectTrial.addListener();
        connectTrial.addProperties();


    }

    private void addProperties() {

        ConnectionFactory factory = new ConnectionFactory();

        System.out.println(factory.getClientProperties());


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

        try  {

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
