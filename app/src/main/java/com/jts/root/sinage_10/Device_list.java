package com.jts.root.sinage_10;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.jts.root.sinage_10.Login.passwordE;
import static com.jts.root.sinage_10.Login.usernameE;


public class Device_list extends AppCompatActivity implements MqttCallback {

    ListView devicelst;
    Button changeB,cancelB,refreshB;
    EditText searchB;
    String name="room1";
    Adapter_devicelst reqAdapter;
    private ProgressDialog dialog_progress ;
    AlertDialog.Builder builderLoading;
    com.android.volley.RequestQueue sch_RequestQueue;
    private static String TAG = "MQTT_android";
    MqttAndroidClient client1;
    MqttAndroidClient clientmac;


    MqttConnectOptions options = new MqttConnectOptions();
     String[] unitid = {};
    static String[] unitdesc = {};
    String[] operation = {};
    static String[] macid = {};

    static String operationS,unit_idS,unit_descS,macidS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        devicelst=(ListView)findViewById(R.id.devicelist);
        searchB=(EditText) findViewById(R.id.search);
        changeB=(Button) findViewById(R.id.change);
        cancelB=(Button) findViewById(R.id.cancel);
        //refreshB=(Button) findViewById(R.id.refresh);

        dialog_progress = new ProgressDialog(Device_list.this);
        builderLoading = new AlertDialog.Builder(Device_list.this);

        subscribe_scada();

        changeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Device_list.this, Gridview_list.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        /*submacB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });*/
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Device_list.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        //nameL.add(name);


        searchB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                reqAdapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

       /* refreshB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //subscribe_scada();

                String result1 = "{" + "\"username\": \"" + usernameE + "\"," + "\"password\": \"" + passwordE + "\"}";
                Log.d(">>>publish Devicelst>>>", result1);
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = result1.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    dialog_progress.setMessage("connecting ...");
                    dialog_progress.show();
                    try {

                        client1.publish("jts/dtd/get_units", message);
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
        });*/


    }
    public void subscribe_scada() {
        Log.d("Enetered ", "in sub func ");
        //Bundle b = getIntent().getExtras();
        String clientId = MqttClient.generateClientId();
        final String topic = "jts/dtd/response";
        //String server_ip = "tcp://jtha.in:1883";
        String server_ip = "tcp://cld003.jts-prod.in:1883";
        Log.d("Device11 ", "subscribeScada");
        client1 = new MqttAndroidClient(this.getApplicationContext(), server_ip,
                clientId);

        Log.d("device22 ", "subscribeScada1");
        try {
            options.setUserName("esp");
            options.setPassword("ptlesp01".toCharArray());
            IMqttToken token = client1.connect(options);
            Log.d("device33 ", "subscribeScada2");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //t.cancel();
                    Log.d("device44 ", "subscribeScada3");
                    client1.setCallback(Device_list.this);
                    int qos = 2;
                    try {
                        IMqttToken subToken = client1.subscribe(topic, qos);
                        Log.d("device55 ", "subscribeScada4");
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // successfully subscribed
                                //tv.setText("Successfully subscribed to: " + topic);
                                Log.d("success", "came here");
                                String result1 = "{" + "\"username\": \"" + usernameE + "\"," + "\"password\": \"" + passwordE + "\"}";
                                Log.d(">>>publish Devicelst>>>", result1);
                                byte[] encodedPayload = new byte[0];
                                try {
                                    encodedPayload = result1.getBytes("UTF-8");
                                    MqttMessage message = new MqttMessage(encodedPayload);
                                    dialog_progress.setMessage("connecting ...");
                                    dialog_progress.show();
                                    try {

                                        client1.publish("jts/dtd/get_units", message);
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
        try {
            Log.d("messege arriv", "devicelist");

            json = new JSONObject(String.valueOf(message));
            Log.d("json::", "jsonresponse json:::::" + json);

            String errcode = json.getString("error_code");
            Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);
            if (errcode.contentEquals("0")) {
                // Toast.makeText(getApplicationContext(), "Response=successfully ", Toast.LENGTH_LONG).show();
                dialog_progress.dismiss();
                //client1.unsubscribe(topic);
                client1.disconnect();

           /* Intent intent = new Intent(Device_list.this, Device_list.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            }
            else if (errcode.contentEquals("3")) {
                    // Toast.makeText(getApplicationContext(), "Response=successfully ", Toast.LENGTH_LONG).show();
                    dialog_progress.dismiss();
                     Toast.makeText(getApplicationContext(), "Response=Failed to get the Device records, NO_DATA_FOUND", Toast.LENGTH_SHORT).show();

           /* Intent intent = new Intent(Schedule_getdata.this, Schedule.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
                }

             else {
                // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();

                Log.d("devicelistS", "" + errcode);
                final AlertDialog.Builder builder = new AlertDialog.Builder(Device_list.this);
                builder.setMessage("Response=Failed to get the device");

                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface1, int i) {

                        dialogInterface1.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            try {

                JSONArray contactArray = new JSONArray(json.getString("get_units"));
                List<String> unit_idL = new ArrayList<>();
                List<String> unit_descL = new ArrayList<>();
                List<String> operationL = new ArrayList<>();
                List<String> macidL = new ArrayList<>();

                for (int i = 0; i < contactArray.length(); i++) {

                    JSONObject Devicelist = new JSONObject(contactArray.get(i).toString());
                    unit_idS = Devicelist.getString("unit_id");
                    Log.d("unit_idS::", "jsonresponse unit_idS:::" + unit_idS);
                    macidS = Devicelist.getString("macid");
                    Log.d("unit_descS::", "jsonresponse macidS::;" + macidS);
                    unit_descS = Devicelist.getString("unit_desc");
                    Log.d("unit_descS::", "jsonresponse unit_descS::;" + unit_descS);

                    operationS = Devicelist.getString("operation");
                    Log.d("operationS::", "jsonresponse operationS:::" + operationS);

                    unit_idL.add(unit_idS);
                    unit_descL.add(unit_descS);
                    operationL.add(operationS);
                    macidL.add(macidS);

                    unitid = new String[unit_idL.size()];
                    unitdesc = new String[unit_descL.size()];
                    operation = new String[operationL.size()];
                    macid = new String[macidL.size()];

                    for (int l = 0; l < operationL.size(); l++) {
                        unitid[l] = unit_idL.get(l);
                        unitdesc[l] = unit_descL.get(l);
                        operation[l] = operationL.get(l);
                        macid[l] = macidL.get(l);

                        Log.d("unitid ", unitid[l]);
                        Log.d("unitdesc ", unitdesc[l]);
                        Log.d("operation ", operation[l]);
                        Log.d("operation ", macid[l]);


                    }
                    reqAdapter = new Adapter_devicelst(Device_list.this, unitdesc);
                    devicelst.setAdapter(reqAdapter);

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            //code
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }
    /*public boolean onKeyDown(int keyCode, KeyEvent event) {
         final int PICK_CONTACT_REQUEST = 0;

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            // When the user center presses, let them pick a contact.
            startActivityForResult(
                    new Intent(Intent.ACTION_PICK),
                    PICK_CONTACT_REQUEST);
            return true;
        }
        return false;
    }
*/

}