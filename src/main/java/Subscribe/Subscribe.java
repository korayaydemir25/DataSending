package Subscribe;

import PushCallback.PushCallback;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.URI;
import java.net.URISyntaxException;


public class Subscribe implements MqttCallback {


public Subscribe(){

}
    public final int qos = 1 ;
    public String subTopic = "v1/devices/me/telemetry";
    public MqttClient client;

    public Subscribe (String uri) throws MqttException, URISyntaxException {
        this(new URI(uri));
    }
    public Subscribe (URI uri) throws  MqttException{
        client.subscribe("koray1");
        String host = String.format("tcp://smartapp.netcad.com",uri.getHost(),uri.getPort());
        String[] auth = this.getAuth (uri);
        String username = auth[0];
        String password = auth[1];
        String clientId = "JavaSample";
        if(!uri.getPath().isEmpty()){
            this.subTopic = uri.getPath().substring(1);
        }

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setUserName(username);
        connectOptions.setPassword(password.toCharArray());

        this.client = new MqttClient(host,clientId,new MemoryPersistence());
        this.client.setCallback(this);
        this.client.connect(connectOptions);

        this.client.subscribe(this.subTopic, qos);

    }
    private String[] getAuth(URI uri){
        String a = uri.getAuthority();
        String[] first = a.split("@");
        return first[0].split(":");
    }

    public void sendMessage(String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        this.client.publish(this.subTopic, message);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost because: " + cause);
        System.exit(1);
    }
    @Override
    public void messageArrived(String subTopic, MqttMessage message)  {
        System.out.println(message);
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}

