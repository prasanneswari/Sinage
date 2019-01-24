package com.jts.root.sinage_10;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.jts.root.sinage_10.Gridview_list.gridboolen;
import static com.jts.root.sinage_10.Login.passwordE;
import static com.jts.root.sinage_10.Login.usernameE;

public class Scan_setup extends AppCompatActivity implements MqttCallback  {

    EditText id,macid;
    Button scanB,cancelB,addB;
     IntentIntegrator qrScan;
    String[] Uname;
    String[] Pwd;
    ArrayAdapter<String> arrayAdapter;
    static List<String> nameL = new ArrayList<String>();
    List<String> pwdL = new ArrayList<String>();
    static String ssid;
    String password;
    private ProgressDialog dialog_progress ;
    AlertDialog.Builder builderLoading;
    private static String TAG = "MQTT_android";
    MqttAndroidClient Scanclint;
    MqttConnectOptions options = new MqttConnectOptions();
    String topic="jts/dtd/response";
    String idE,macE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_setup);
        id=(EditText)findViewById(R.id.id);
        macid=(EditText)findViewById(R.id.macid);
        scanB=(Button) findViewById(R.id.scanB);
       // cancelB=(Button) findViewById(R.id.cancelbtn);
        addB=(Button) findViewById(R.id.addscan);

        dialog_progress = new ProgressDialog(Scan_setup.this);
        builderLoading = new AlertDialog.Builder(Scan_setup.this);

        //scanB.setOnClickListener(this);

        qrScan = new IntentIntegrator(this);


            subscribe_scada();

        /*cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Scan_setup.this, Gridview_list.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                try {
                    Scanclint.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });*/
        scanB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.initiateScan();


            }
        });

        addB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                idE = id.getText().toString();
                Log.d("...idE....", "---" + idE);

                macE = macid.getText().toString();
                Log.d("...macE....", "---" + macE);

                String result1 ="{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"name\":\"" + idE + "\",\"macid\":\"" + macE + "\"}";

                //String result1 = "{" + "\"username\": \"" + usernameE + "\"," + "\"password\": \"" + passwordE + "\"," + "\"name\": \"" + idE + "\""+ "}";
                Log.d(">>>publish Scansetup>>>", result1);
                result1=result1.replaceAll(" ","");
                result1 = result1.trim();

                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = result1.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    dialog_progress.setMessage("connecting ...");
                    dialog_progress.show();
                    try {

                        Scanclint.publish("jts/dtd/add_units1", message);
                        Log.d(">>>publish enetr>>>","enter%%%%%%");

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();

            } else {
                //if qr contains data
                System.out.println("this is mac : " + result.getContents());
                //getIpFromArpCache(result.getContents());

                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    ssid = obj.getString("username");
                    password = obj.getString("password");

                    System.out.println("this is username : " + ssid);
                    System.out.println("this is password : " + password);
                    nameL.add(ssid);
                    pwdL.add(password);

                    Uname = new String[nameL.size()];
                    Pwd = new String[pwdL.size()];
                    for (int l = 0; l < pwdL.size(); l++) {
                        Uname[l] = nameL.get(l);
                        Pwd[l] = pwdL.get(l);

                        Log.d("username11 ", Uname[l]);
                        Log.d("password22 ", Pwd[l]);

                    }
                    /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, Uname);
                    ssidlst.setAdapter(adapter);*/
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void subscribe_scada() {
        Log.d("Enetered ", "in sub func ");
        //Bundle b = getIntent().getExtras();
        String clientId = MqttClient.generateClientId();
        //final String topic = "jts/dtd/response";
        //String server_ip = "tcp://jtha.in:1883";
        String server_ip = "tcp://cld003.jts-prod.in:1883";
        Log.d("scan11 ", "subscribeScada");
        Scanclint = new MqttAndroidClient(this.getApplicationContext(), server_ip,
                clientId);

        Log.d("scan22 ", "subscribeScada1");
        try {
            options.setUserName("esp");
            options.setPassword("ptlesp01".toCharArray());
            IMqttToken token = Scanclint.connect(options);
            Log.d("scan33 ", "subscribeScada2");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //t.cancel();
                    Log.d("scan44 ", "subscribeScada3");
                    Scanclint.setCallback(Scan_setup.this);
                    int qos = 2;
                    try {
                        IMqttToken subToken = Scanclint.subscribe(topic, qos);
                        Log.d("scan55 ", "subscribeScada4");
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
        JSONObject json = null;  //your response
        Log.d("messege arriv", "devicelist");

        try {
            json = new JSONObject(String.valueOf(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String errcode = json.getString("error_code");
        Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

        if (errcode.contentEquals("0")) {
           // Toast.makeText(getApplicationContext(), "Response=successfully ", Toast.LENGTH_LONG).show();
            Scanclint.unsubscribe(topic);
            Scanclint.disconnect();
            Intent intent = new Intent(Scan_setup.this, Gridview_list.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else {
            // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();

            Log.d("ScansetupS", "" + errcode);
            final AlertDialog.Builder builder = new AlertDialog.Builder(Scan_setup.this);
            builder.setMessage("Reasponse=Failed to Scansetup");

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
    public void ReturnHome(View view){
        super.onBackPressed();
        try {
            Scanclint.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
