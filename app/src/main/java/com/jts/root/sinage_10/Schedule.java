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
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;

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

import static com.jts.root.sinage_10.Device_list.unitdesc;
import static com.jts.root.sinage_10.Gridview_list.clickpos;
import static com.jts.root.sinage_10.Gridview_list.gridboolen;
import static com.jts.root.sinage_10.Gridview_list.gridclickpos;
import static com.jts.root.sinage_10.Gridview_list.macid;
import static com.jts.root.sinage_10.Gridview_list.unitid;
import static com.jts.root.sinage_10.Login.passwordE;
import static com.jts.root.sinage_10.Login.usernameE;
import static com.jts.root.sinage_10.MapsActivity.langS;
import static com.jts.root.sinage_10.MapsActivity.latS;

public class Schedule extends AppCompatActivity implements MqttCallback {

    EditText name, latitude, langitude,macidE;
    Button changeB, delB, addB, cancelschB, refreshB,scheduleB;
    private ProgressDialog dialog_progress;
    AlertDialog.Builder builderLoading;
    com.android.volley.RequestQueue sch_RequestQueue;
    private static String TAG = "MQTT_android";
    MqttAndroidClient Schedclint;
    MqttAndroidClient Schedclintadd;
    MqttAndroidClient Schedclintdel;

    MqttConnectOptions options = new MqttConnectOptions();
    static String latSJ,longSJ;

     String topic = "jts/dtd/response";
     String topicadd = "jts/dtd/response";
     String topicdel = "jts/dtd/response";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        name = (EditText) findViewById(R.id.name);
        macidE = (EditText) findViewById(R.id.macidE);
        latitude = (EditText) findViewById(R.id.latitude);
        langitude = (EditText) findViewById(R.id.langitude);
        changeB = (Button) findViewById(R.id.changebtn);
        delB = (Button) findViewById(R.id.delbtn);
        addB = (Button) findViewById(R.id.addbtn);
        cancelschB = (Button) findViewById(R.id.cancelbtn);
        //refreshB = (Button) findViewById(R.id.refreshsch);
        scheduleB = (Button) findViewById(R.id.Schedule);

        latitude.setEnabled(false);
        langitude.setEnabled(false);

        dialog_progress = new ProgressDialog(Schedule.this);
        builderLoading = new AlertDialog.Builder(Schedule.this);

        //mqttconnectioagain();

       /* if (gridboolen == true) {

        }*/
        mqttconnectio();

        Log.d("...clickpos#####....", "---" + clickpos);

        latitude.setText(latS);
        langitude.setText(langS);
        name.setText(clickpos);
        macidE.setText(macid[gridclickpos]);


        /*refreshB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribe_scadasche();
            }
        });*/
        scheduleB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Schedule.this, Schedule_getdata.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        cancelschB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Schedule.this, Gridview_list.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        changeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Schedule.this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        delB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //subscribe_scadaadel();
                String result1 = "{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"unit_id\":\"" + unitid[gridclickpos] + "\"}";
                // String result1 ="{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"unit_id\":\"" + unitid[gridclickpos] + "\",\"long\":\"" + langitudeE+ "\",\"lat\":\"" + latitudeE + "\"}";
                Log.d(">>publish Schedle del>>", result1);
                //result1=result1.replaceAll(" ","");
                // result1 = result1.trim();
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = result1.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    dialog_progress.setMessage("connecting ...");
                    dialog_progress.show();
                    try {
                        Schedclint.publish("jts/dtd/delete_unit", message);
                        Log.d(">>publih del entr>>", "enter%%%%%%");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
               /* try {
                    Schedclint.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }*/
            }
        });
        addB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //subscribe_scadaadd();
                String latitudeE = latitude.getText().toString();
                Log.d("...latitudeE....", "---" + latitudeE);
                String langitudeE = langitude.getText().toString();
                Log.d("...langitudeE....", "---" + langitudeE);
                String nameE = name.getText().toString();
                Log.d("...nameE....", "---" + nameE);

                String result1 = "{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"unit_id\":\"" + unitid[gridclickpos] + "\",\"name\":\"" + nameE + "\",\"macid\":\"" + macid[gridclickpos] + "\",\"long\":\"" + langitudeE + "\",\"lat\":\"" + latitudeE + "\"}";
                // String result1 ="{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"unit_id\":\"" + unitid[gridclickpos] + "\",\"long\":\"" + langitudeE+ "\",\"lat\":\"" + latitudeE + "\"}";
                Log.d(">>publish Schedle add>>", result1);
                //result1=result1.replaceAll(" ","");
                // result1 = result1.trim();
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = result1.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    dialog_progress.setMessage("connecting ...");
                    dialog_progress.show();
                    try {
                        Schedclint.publish("jts/dtd/update_unit_details", message);
                        Log.d(">>publih schead entr>>", "enter%%%%%%");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    Schedclint.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void mqttconnectio() {
        Log.d("Enetered ", "in sub func ");
        //Bundle b = getIntent().getExtras();
        String clientId = MqttClient.generateClientId();
        //String server_ip = "tcp://jtha.in:1883";
        String server_ip = "tcp://cld003.jts-prod.in:1883";
        Log.d("sched11 ", "subscribeScada");
        Schedclint = new MqttAndroidClient(this.getApplicationContext(), server_ip,
                clientId);

        Log.d("sched22 ", "subscribeScada1");
        try {
            options.setUserName("esp");
            options.setPassword("ptlesp01".toCharArray());
            IMqttToken token = Schedclint.connect(options);
            Log.d("sched33 ", "subscribeScada2");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //t.cancel();
                    Log.d("sched44 ", "subscribeScada3");
                    Schedclint.setCallback(Schedule.this);
                    int qos = 2;
                    try {
                        IMqttToken subToken = Schedclint.subscribe(topic, qos);
                        Log.d("sched55 ", "subscribeScada4");
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // successfully subscribed
                                //tv.setText("Successfully subscribed to: " + topic);
                                Log.d("success", "came here");
                                String result1 = "{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"unit_id\":\"" + unitid[gridclickpos] + "\"}";
                                Log.d(">>>publish Scheduled>>>", result1);
                                byte[] encodedPayload = new byte[0];
                                try {
                                    encodedPayload = result1.getBytes("UTF-8");
                                    MqttMessage message = new MqttMessage(encodedPayload);
                                    dialog_progress.setMessage("connecting ...");
                                    dialog_progress.show();
                                    try {

                                        Schedclint.publish("jts/dtd/get_unit_details", message);
                                        Log.d(">>publish sche entr>>", "enter%%%%%%");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }                            }
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
        try {

            JSONObject json = null;  //your response
            Log.d("messege arriv", "Schedlued");
            json = new JSONObject(String.valueOf(message));
            Log.d("json::", "jsonresponse json:::::" + json);


            String funname = json.getString("function");
            Log.d("funname::", "jsonresponse funname:::" + funname);

            if (funname.contentEquals("get_unit_details")) {
                try {
                    String errcode = json.getString("error_code");
                    Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

                    JSONArray get_unit_details = new JSONArray(json.getString("get_unit_details"));

                    for (int i = 0; i < get_unit_details.length(); i++) {
                        JSONObject objget_units = new JSONObject(get_unit_details.get(i).toString());

                        String unit_descS = objget_units.getString("unit_desc");
                        Log.d("unit_descS ", unit_descS);

                        String macS = objget_units.getString("macid");
                        Log.d("macS ", macS);

                        longSJ = objget_units.getString("long");
                        Log.d("longSJ ", longSJ);
                        latSJ = objget_units.getString("lat");
                        Log.d("latSJ ", latSJ);
                        String operationS = objget_units.getString("operation");
                        Log.d("operationS ", operationS);

                        if (gridboolen == false) {
                            latitude.setText(latSJ);
                            langitude.setText(longSJ);
                        }

                        name.setText(unit_descS);
                        macidE.setText(macS);

                        //gridboolen = true;
                    }

                if (errcode.contentEquals("0")) {
                    //Toast.makeText(getApplicationContext(), "Response=successfully ", Toast.LENGTH_LONG).show();
                    dialog_progress.dismiss();
                    //Schedclint.disconnect();

                    // Schedclint.unsubscribe(topic);
                    // Schedclint.disconnect();


           /* Intent intent = new Intent(Device_list.this, Device_list.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
                } else {
                    // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();
                    Log.d("ScheduledS", "" + errcode);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Schedule.this);
                    builder.setMessage("Reasponse=Failed to scheduled");
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface1, int i) {
                            dialogInterface1.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (funname.contentEquals("update_unit_details")){
                String errcode = json.getString("error_code");
                Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

                if (errcode.contentEquals("0")) {
                    // Toast.makeText(getApplicationContext(), "Response=successfully add ", Toast.LENGTH_LONG).show();
                    dialog_progress.dismiss();
                    Schedclint.unsubscribe(topic);
                    Intent intent = new Intent(Schedule.this, Gridview_list.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();
                    Log.d("ScheduledS", "" + errcode);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Schedule.this);
                    builder.setMessage("Reasponse=Failed to scheduled");
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
            else if (funname.contentEquals("delete_unit") ){
                String errcode = json.getString("error_code");
                Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

                if (errcode.contentEquals("0")) {
                    // Toast.makeText(getApplicationContext(), "Response=successfully delete", Toast.LENGTH_LONG).show();
                    dialog_progress.dismiss();
                    Schedclint.unsubscribe(topic);
                    Schedclint.disconnect();
                    Intent intent = new Intent(Schedule.this, Gridview_list.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();

                    Log.d("delete stateS", "" + errcode);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Schedule.this);
                    builder.setMessage("Reasponse=Failed to delete");

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
   /* public void ReturnHome(View view){
        super.onBackPressed();
    }*/
}

