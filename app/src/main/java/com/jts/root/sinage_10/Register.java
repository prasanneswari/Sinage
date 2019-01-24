package com.jts.root.sinage_10;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


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

import static com.jts.root.sinage_10.Gridview_list.gridclickpos;
import static com.jts.root.sinage_10.Gridview_list.macid;
import static com.jts.root.sinage_10.Gridview_list.unitid;
import static com.jts.root.sinage_10.Login.passwordE;
import static com.jts.root.sinage_10.Login.usernameE;

public class Register extends AppCompatActivity  implements MqttCallback {


    EditText regname,regpwd,regaddress,regemail,regmobile,regcompany;
    Button regsubmit,regcancel;
    private ProgressDialog dialog_progress ;
    AlertDialog.Builder builderLoading;
    com.android.volley.RequestQueue sch_RequestQueue;
    private static String TAG = "MQTT_android";
    String payload = "the payload";
    MqttAndroidClient client;
    MqttConnectOptions options = new MqttConnectOptions();
     String topic;

//    MqttAndroidClient mqttAndroidClient;
//
//    final String serverUri = "tcp://cld003.jts-prod.in :1883";
//
//    String clientId = "1232456";
//    final String subscriptionTopic = "jts/dtd/response";
//    final String publishTopic = "jts/dtd/user_register";
//    final String publishMessage = "Hello World!";
//    final String username = "esp";
//    final String password = "ptlesp01";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regname = (EditText) findViewById(R.id.regname);
        regpwd = (EditText) findViewById(R.id.regpwd);
        regaddress = (EditText) findViewById(R.id.regaddress);
        regemail = (EditText) findViewById(R.id.regemail);
        regcompany = (EditText) findViewById(R.id.Rcompany);
        regmobile = (EditText) findViewById(R.id.regmobilenum);
        regsubmit = (Button) findViewById(R.id.regsubmit);
        regcancel = (Button) findViewById(R.id.regcancel);
        dialog_progress = new ProgressDialog(Register.this);
        builderLoading = new AlertDialog.Builder(Register.this);
        subscribe_scada();

        regsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regnameE = regname.getText().toString();
                Log.d("..regnameE...", "---" + regnameE);
                String regpwdE = regpwd.getText().toString();
                Log.d("..regpwdE...", "---" + regpwdE);
                String regaddressE = regaddress.getText().toString();
                Log.d("...regaddressE....", "---" + regaddressE);
                String regemailE = regemail.getText().toString();
                Log.d("...regemailE....", "---" + regemailE);
                String regmobileE = regmobile.getText().toString();
                Log.d("...regmobileE....", "---" + regmobileE);
                String regcompanyE = regcompany.getText().toString();
                Log.d("...regcompanyE....", "---" + regcompanyE);


                //subscribe_scada();

                //String result1 = "{" + "\"username\": \"" + regnameE + "\"," + "\"password\": \"" + regpwdE + "\"," + "\"address\": \"" + regaddressE + "\"," + "\"company\": \"" + regcompanyE + "\"," + "\"mobile\": \"" + regmobileE + "\"," + "\"email\": \"" + regemailE + "\"" + "}";
                String result1 = "{\"username\":\"" + regnameE + "\",\"password\":\"" + regpwdE + "\",\"address\":\"" + regaddressE + "\",\"company\":\"" + regcompanyE + "\",\"mobile\":\"" + regmobileE + "\",\"email\":\"" + regemailE + "\"}";


                Log.d(">>>publish string11>>>", result1);
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = result1.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    dialog_progress.setMessage("connecting ...");
                    dialog_progress.show();

                    client.publish("jts/dtd/user_register", message);
                }
                catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }

            }
        });
        regcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                try {
                    client.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    public void subscribe_scada() {
        Log.d("Enetered ", "in sub func ");
        //Bundle b = getIntent().getExtras();
        String clientId = MqttClient.generateClientId();
        topic = "jts/dtd/response";
        //String server_ip = "tcp://jtha.in:1883";
        String server_ip = "tcp://cld003.jts-prod.in:1883";
        Log.d("Enetered ", "subscribeScada");
        client = new MqttAndroidClient(this.getApplicationContext(), server_ip,
                clientId);
        Log.d("Enetered ", "subscribeScada1");
        try {
            options.setUserName("esp");
            options.setPassword("ptlesp01".toCharArray());
            IMqttToken token = client.connect(options);
            Log.d("Enetered ", "subscribeScada2");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //t.cancel();
                    Log.d("Enetered ", "subscribeScada3");
                    client.setCallback(Register.this);
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
    public void connectionLost(Throwable cause) {

    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d("messege arriv", "register");

        JSONObject json = null;  //your response
        try {
            json = new JSONObject(String.valueOf(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String errcode = json.getString("error_code");
        Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

        if (errcode.contentEquals("0")) {
            Toast.makeText(getApplicationContext(), "Response=successfully register", Toast.LENGTH_LONG).show();
            client.unsubscribe(topic);
            //client.disconnect();
            Intent intent = new Intent(Register.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();
            Log.d("login_StateS", "" + errcode);
            final AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
            builder.setMessage("Reasponse=Failed to register");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface1, int i) {

                    dialogInterface1.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
