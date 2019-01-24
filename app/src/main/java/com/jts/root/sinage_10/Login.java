package com.jts.root.sinage_10;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Login extends AppCompatActivity implements MqttCallback {

    EditText username,password;
    TextView fogetpwd;
    Button loginB,registerB;
    private ProgressDialog dialog_progress ;
    AlertDialog.Builder builderLoading;
    com.android.volley.RequestQueue sch_RequestQueue;
    private static String TAG = "MQTT_android";
    String payload = "the payload";
    static MqttAndroidClient client;
    MqttConnectOptions options = new MqttConnectOptions();
    static String usernameE,passwordE;
     String topic="jts/dtd/response";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username=(EditText)findViewById(R.id.usernameid);
        password=(EditText)findViewById(R.id.pwdid);
        fogetpwd=(TextView) findViewById(R.id.forgetpwdid);
        loginB=(Button) findViewById(R.id.loginbtn);
        registerB=(Button) findViewById(R.id.regbtn);
        dialog_progress = new ProgressDialog(Login.this);
        builderLoading = new AlertDialog.Builder(Login.this);
        subscribe_scada();

        fogetpwd.setPaintFlags(fogetpwd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        fogetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Forgotpwd.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().trim().length()==0){
                    username.setError("Username is not entered");
                    username.requestFocus();
                }
                if(password.getText().toString().trim().length()==0){
                    password.setError("Password is not entered");
                    password.requestFocus();
                }

                 usernameE = username.getText().toString();
                Log.d("...usernameE....", "---" + usernameE);
                 passwordE = password.getText().toString();
                Log.d("...passwordE....", "---" + passwordE);


                //subscribe_scada();

                String result1 = "{" + "\"username\": \"" + usernameE + "\"," + "\"password\": \"" + passwordE + "\"" + "}";
                Log.d(">>>publish string11>>>", result1);
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = result1.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    dialog_progress.setMessage("connecting ...");
                    dialog_progress.show();

                    client.publish("jts/dtd/login", message);
                } catch (UnsupportedEncodingException | MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        registerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    public void subscribe_scada() {
        Log.d("Enetered ", "in sub func ");
        //Bundle b = getIntent().getExtras();
        String clientId = MqttClient.generateClientId();
         //topic = "jts/dtd/response";
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
                    client.setCallback(Login.this);
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

        Log.d("messege arriv", "login");

        JSONObject json = null;  //your response
        try {
            json = new JSONObject(String.valueOf(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String errcode = json.getString("error_code");
        Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

        if (errcode.contentEquals("0")) {
           // Toast.makeText(getApplicationContext(), "Response=successfully ", Toast.LENGTH_LONG).show();
           //client.unsubscribe(topic);
           client.disconnect();
            dialog_progress.dismiss();
            Intent intent = new Intent(Login.this, Device_list.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();

            Log.d("login_StateS", "" + errcode);
            final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage("Reasponse=Failed to login");

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
   /*@Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
       if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

           finish();
           return true;
       }
       return super.onKeyDown(keyCode, event);
   }*/
}
