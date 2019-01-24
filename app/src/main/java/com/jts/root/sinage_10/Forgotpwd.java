package com.jts.root.sinage_10;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Forgotpwd extends AppCompatActivity implements MqttCallback {

    EditText usernameF,phnumF,mailF,otpF;
    Button sendotpB,submitB,cancelB;
    private ProgressDialog dialog_progress ;
    AlertDialog.Builder builderLoading;
    com.android.volley.RequestQueue sch_RequestQueue;
    private static String TAG = "MQTT_android";
    String payload = "the payload";
    MqttAndroidClient client;
    MqttConnectOptions options = new MqttConnectOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpwd);
        usernameF=(EditText)findViewById(R.id.usernameF);
        phnumF=(EditText)findViewById(R.id.phnumF);
        mailF=(EditText)findViewById(R.id.emailF);
        otpF=(EditText)findViewById(R.id.otpF);
        sendotpB=(Button) findViewById(R.id.sendotpF);
        submitB=(Button) findViewById(R.id.submitF);
        cancelB=(Button) findViewById(R.id.cancelF);
        dialog_progress = new ProgressDialog(Forgotpwd.this);
        builderLoading = new AlertDialog.Builder(Forgotpwd.this);


        sendotpB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                subscribe_scada();
            }
        });
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Forgotpwd.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    public void subscribe_scada() {
        Log.d("Enetered ", "in sub func ");
        //Bundle b = getIntent().getExtras();
        String clientId = MqttClient.generateClientId();
        final String topic = "Vehicle_A";
        //String server_ip = "tcp://jtha.in:1883";
        String server_ip = "tcp://cld003.jts-prod.in:1883";
        Log.d("Enetered ", "subscribeScada");
        client = new MqttAndroidClient(this.getApplicationContext(), server_ip,
                clientId);

        Log.d("Enetered ", "subscribeScada1");
        try {
            options.setUserName("esp");
            options.setPassword("jtsesp01".toCharArray());
            IMqttToken token = client.connect(options);
            Log.d("Enetered ", "subscribeScada2");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //t.cancel();
                    Log.d("Enetered ", "subscribeScada3");
                    client.setCallback(Forgotpwd.this);
                    int qos = 2;
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
                        Log.d("Enetered ", "subscribeScada4");
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // successfully subscribed
                                //tv.setText("Successfully subscribed to: " + topic);
                                Log.d("success", "came here");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                // Toast.makeText(MainActivity.this, "Couldn't subscribe to: " + topic, Toast.LENGTH_SHORT).show();
                                Log.d("failure", "came here");
                                //tv.setText("Couldn't subscribe to: " + topic);

                            }
                        });
                        Log.d(TAG, "here we are");
                    } catch (MqttException e) {
                        e.printStackTrace();
                        Log.d("error", "!");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Log.d("error", "2");
                    }
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        client.publish(topic, message);
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "onFailure");
        }
    }


    @Override
    public void connectionLost(Throwable throwable) {

    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        Log.d(">>>MessageArrived>>>", String.valueOf(mqttMessage));

        String jsonResponse = String.valueOf(mqttMessage);
        Log.d(">>>JsonResponse>>>", String.valueOf(jsonResponse));

        // apref.edit().putString("jsonResponse", jsonResponse).commit();

        // String sample= {"e_load": 0, "clnt": 79, "maf_air": 0, "speed": 0, "acc_D": 0, "rpm": 0, "acc_E": 0, "maf_ap": 95, "air": 0};
        // MessageArrivedfn(jsonResponse);
        Log.d("--number of threads:--", String.valueOf(Thread.activeCount()));

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    public void MQTT_Publish(String payload) {
        Log.d(TAG, "entered in pub ");
        MqttClient client;
        String topic = "para_mqtt";
        //String topic = "gate_mqtt";
        String clientId = MqttClient.generateClientId();
        try {
            client = new MqttClient("tcp://cld002.jts-prod.in:1883", "androidClient", new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("esp");
            connOpts.setPassword("jtsesp01".toCharArray());
            client.connect(connOpts);
            client.publish(topic, new MqttMessage(payload.getBytes("UTF-8")));
        } catch (MqttException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
