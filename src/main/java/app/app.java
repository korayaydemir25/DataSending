package app;

import PushCallback.PushCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import javax.swing.*;
import java.util.Scanner;


public class app {
    public static void main(String[] args) {
        String subTopic = "v1/devices/me/attribute";
        String pubTopic = "v1/devices/me/telemetry";
        String content = "{'deneme' : 'FBB'}";

       // String subTopic = "v2/deneme/sub";
        //String pubTopic = "v2/deneme/pub";
        //String content = "asd";

        int qos=1;
        String broker = "tcp://smartapp.netcad.com";
        String clientId = "JavaSample";
        MemoryPersistence persistence = new MemoryPersistence();

        try{
            MqttClient client = new MqttClient(broker,clientId,persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            connOpts.setCleanSession(true);
            connOpts.setUserName("koray1");

//publish event sub event
            client.setCallback(new PushCallback());
            System.out.println("Connecting to broker " + broker);
            client.connect(connOpts);
            System.out.println("Connected");
            client.subscribeWithResponse(subTopic).getTopics();
            //client.subscribe(subTopic,1);

            System.out.println("Publishing message: " + content);

            Scanner girdi = new Scanner(System.in);
            System.out.println("Lütfen çıkmak için ext yazınız .");
            while (true) {
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                client.publish(pubTopic, message);
                System.out.println(message);
                System.out.println("Message published");
                client.publish(subTopic,message);
                System.out.println();

                if (girdi.next().equals("ext")) {
                    client.disconnect();
                    System.out.println("Disconnected");
                    client.close();
                    System.exit(1);
                }
            }
        }
        catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}
