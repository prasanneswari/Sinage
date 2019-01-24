package com.jts.root.sinage_10;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import static com.jts.root.sinage_10.adapter_scheldule.act_inactval;
import static com.jts.root.sinage_10.adapter_scheldule.onval;
import static com.jts.root.sinage_10.adapter_scheldule.selectedact_inact_spinner;
import static com.jts.root.sinage_10.adapter_scheldule.statuspos;

public class Schedule_getdata extends AppCompatActivity implements MqttCallback {
    Button  newB,canceldataB,delcheckB,refreshidB,macB;
    private ProgressDialog dialog_progress;
    AlertDialog.Builder builderLoading;
    com.android.volley.RequestQueue sch_RequestQueue;
    private static String TAG = "MQTT_android";
    ListView schedulelst;
    String[] schedule = {"1", "5"};
    String[] on_off = {"on", "off"};
    String[] active_inactive = {"active", "inactive"};
    MqttAndroidClient clientdata;
    MqttAndroidClient clientdel;
    MqttAndroidClient clientmac;
    MqttConnectOptions options = new MqttConnectOptions();
    static String shedule_idS,typeS,statusS;
    static String[] scheduleststus,scheduArar,schedulesymbol,scheduletype,schedulehours,schedulemns;
     String topicmac = "jts/dtd/response";
     String topic = "jts/dtd/response";
     String topicdel = "jts/dtd/response";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_getdata);
        newB = (Button) findViewById(R.id.newbtn);
        delcheckB=(Button)findViewById(R.id.delcheck);
        //canceldataB = (Button) findViewById(R.id.canceldt);
        schedulelst = (ListView) findViewById(R.id.schedlist);
        //refreshidB = (Button) findViewById(R.id.refreshschid);
        macB = (Button) findViewById(R.id.macbtn);

        dialog_progress = new ProgressDialog(Schedule_getdata.this);
        builderLoading = new AlertDialog.Builder(Schedule_getdata.this);
        gridboolen=false;
        mqttconnectio();

        /*refreshidB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //subscribe_scadaget();
            }
        });*/
        newB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Schedule_getdata.this, Update_Schedule.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        delcheckB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //subscribe_scadaadelck();
                String result1 = "{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"shedule_id\":\"" + scheduArar[statuspos] + "\"}";
                // String result1 ="{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"unit_id\":\"" + unitid[gridclickpos] + "\",\"long\":\"" + langitudeE+ "\",\"lat\":\"" + latitudeE + "\"}";
                Log.d(">>publish Schedle del>>", result1);
                //result1=result1.replaceAll(" ","");
                // result1 = result1.trim();
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = result1.getBytes("UTF-8");
                    MqttMessage massage = new MqttMessage(encodedPayload);
                    dialog_progress.setMessage("connecting ...");
                    dialog_progress.show();
                    try {
                        clientdata.publish("jts/dtd/delete_shedule", massage);
                        Log.d(">>publih del entr>>", "enter%%%%%%");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    clientdata.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }
        });
        /*canceldataB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Schedule_getdata.this, Schedule.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });*/
        macB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //subscribe_scadamacadd();
                Log.d("Enetered ", "unitdesc "+clickpos);
                String resmac = "{\"time\":\"" + scheduletype[statuspos] + "\",\"offset\":\"" + schedulesymbol[statuspos]+ schedulehours[statuspos]+":"+schedulemns[statuspos]+ "\",\"operation\":\"" + onval + "\",\"status\":\"" + act_inactval + "\"}";
                // String result1 ="{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"unit_id\":\"" + unitid[gridclickpos] + "\",\"long\":\"" + langitudeE+ "\",\"lat\":\"" + latitudeE + "\"}";
                String resserver = "{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE+ "\",\"shedule_id\":\"" + scheduArar[statuspos] + "\",\"status\":\"" + selectedact_inact_spinner + "\"}";
                Log.d(">>publish  server>>", resserver);

                Log.d(">>publish Schedle mac>>", resmac);
                //result1=result1.replaceAll(" ","");
                // result1 = result1.trim();
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = resmac.getBytes("UTF-8");
                    MqttMessage massage = new MqttMessage(encodedPayload);
                    encodedPayload = resserver.getBytes("UTF-8");
                    MqttMessage messageserver = new MqttMessage(encodedPayload);

                    //dialog_progress.setMessage("connecting ...");
                    //dialog_progress.show();
                    try {
                        clientdata.publish("jts/dtd/"+macid[gridclickpos], massage);
                        Log.d(">>publish valu>>", "jts/dtd/"+macid[gridclickpos]);
                        clientdata.publish("jts/dtd/update_shedule_status", messageserver);
                        //Log.d(">>publish valu>>", "jts/dtd/"+macid[gridclickpos]);
                        dialog_progress.setMessage("connecting ...");
                        dialog_progress.show();

                        Log.d(">>publih mac entr>>", "enter%%%%%%");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
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
        clientdata = new MqttAndroidClient(this.getApplicationContext(), server_ip,
                clientId);

        Log.d("sched22 ", "subscribeScada1");
        try {
            options.setUserName("esp");
            options.setPassword("ptlesp01".toCharArray());
            IMqttToken token = clientdata.connect(options);
            Log.d("sched33 ", "subscribeScada2");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //t.cancel();
                    Log.d("sched44 ", "subscribeScada3");
                    clientdata.setCallback(Schedule_getdata.this);
                    int qos = 2;
                    try {
                        IMqttToken subToken = clientdata.subscribe(topic, qos);
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
                                    MqttMessage massage = new MqttMessage(encodedPayload);
                                    dialog_progress.setMessage("connecting ...");
                                   dialog_progress.show();
                                    try {

                                        clientdata.publish("jts/dtd/get_shedule", massage);
                                        Log.d(">>publish sche entr>>", "enter%%%%%%");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (UnsupportedEncodingException e) {
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
    public void messageArrived(String topic, MqttMessage massage) throws Exception {
        try {
            JSONObject json = null;  //your response
            Log.d("messege arriv", "Schedlued");
            json = new JSONObject(String.valueOf(massage));
            Log.d("json::", "jsonresponse json:::::" + json);
            List<String> scheduleL= new ArrayList<>();
            List<String> schedidL= new ArrayList<>();
            List<String> typesL= new ArrayList<>();
            List<String> symbolL= new ArrayList<>();
            List<String> hoursL= new ArrayList<>();
            List<String> mnsL= new ArrayList<>();
            List<String> statusL= new ArrayList<>();

            String funname = json.getString("function");
            Log.d("funname::", "jsonresponse funname:::" + funname);

            if (funname.contentEquals("get_shedule")) {
                String errcode = json.getString("error_code");
                Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

                if (errcode.contentEquals("0")) {
                    // Toast.makeText(getApplicationContext(), "Response=successfully ", Toast.LENGTH_LONG).show();
                    dialog_progress.dismiss();
                    //clientdata.unsubscribe(topic);

                    // clientdata.unsubscribe(topic);

           /* Intent intent = new Intent(Schedule_getdata.this, Schedule.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
                } else if (errcode.contentEquals("3")) {
                    // Toast.makeText(getApplicationContext(), "Response=successfully ", Toast.LENGTH_LONG).show();
                    dialog_progress.dismiss();
                   // clientdata.disconnect();
                    Toast.makeText(getApplicationContext(), "Response=Failed to get the shedule records, NO_DATA_FOUND", Toast.LENGTH_LONG).show();

           /* Intent intent = new Intent(Schedule_getdata.this, Schedule.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
                } else {

                    //Toast.makeText(getApplicationContext(), "Response=Failed to get the shedule records, NO_DATA_FOUND", Toast.LENGTH_LONG).show();
                    Log.d("ScheduledS", "" + errcode);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(Schedule_getdata.this);
                    builder.setMessage("Reasponse=Reasponse=Failed to scheduled");
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface1, int i) {
                            dialogInterface1.dismiss();

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                JSONArray get_shedule = new JSONArray(json.getString("get_shedule"));
                for (int i = 0; i < get_shedule.length(); i++) {
                    JSONObject objget_units = new JSONObject(get_shedule.get(i).toString());
                    shedule_idS = objget_units.getString("shedule_id");
                    Log.d("shedule_idS ", shedule_idS);
                    typeS = objget_units.getString("type");
                    Log.d("typeS ", typeS);
                    String symbolS = objget_units.getString("symbol");
                    Log.d("symbolS ", symbolS);
                    String hoursS = objget_units.getString("hours");
                    Log.d("hoursS ", hoursS);
                    String mnsS = objget_units.getString("mns");
                    Log.d("mnsS ", mnsS);
                    statusS = objget_units.getString("status");
                    Log.d("statusS ", statusS);

                    scheduleL.add(typeS + symbolS + hoursS + ":" + mnsS);

                    schedidL.add(shedule_idS);
                    typesL.add(typeS);
                    symbolL.add(symbolS);
                    hoursL.add(hoursS);
                    mnsL.add(mnsS);
                    statusL.add(statusS);

                    scheduArar = new String[schedidL.size()];
                    scheduletype = new String[schedidL.size()];
                    schedulesymbol = new String[symbolL.size()];
                    schedulehours = new String[hoursL.size()];
                    schedulemns = new String[mnsL.size()];
                    scheduleststus = new String[statusL.size()];

                    for (int l = 0; l < mnsL.size(); l++) {
                        scheduArar[l] = schedidL.get(l);
                        scheduletype[l] = typesL.get(l);
                        schedulesymbol[l] = symbolL.get(l);
                        schedulehours[l] = hoursL.get(l);
                        schedulemns[l] = mnsL.get(l);
                        scheduleststus[l] = statusL.get(l);

                        Log.d("unitid11 ", scheduArar[l]);
                        Log.d("scheduletype22 ", scheduletype[l]);
                        Log.d("schedulesymbol333 ", schedulesymbol[l]);
                        Log.d("schedulehoursl333 ", schedulehours[l]);
                        Log.d("schedulemns333 ", schedulemns[l]);
                        Log.d("scheduleststus333 ", scheduleststus[l]);

                    }
                    gridboolen = false;

                    adapter_scheldule reqAdapter = new adapter_scheldule(Schedule_getdata.this, scheduleL);
                    schedulelst.setAdapter(reqAdapter);
                }
            }
            else if (funname.contentEquals("update_shedule_status")){
                String errcode = json.getString("error_code");
                Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

                if (errcode.contentEquals("0")) {
                    // Toast.makeText(getApplicationContext(), "Response=successfully add ", Toast.LENGTH_LONG).show();
                    dialog_progress.dismiss();
                   //clientdata.disconnect();
                }
                else {
                    // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();
                    Log.d("ScheduledS", "" + errcode);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Schedule_getdata.this);
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
            else if (funname.contentEquals("delete_shedule")){
                String errcode = json.getString("error_code");
                Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

                if (errcode.contentEquals("0")) {
                    // Toast.makeText(getApplicationContext(), "Response=successfully delete", Toast.LENGTH_LONG).show();
                    // clientdel.unsubscribe(topic);

                    clientdata.unsubscribe(topic);
                    clientdata.disconnect();
                    Intent intent = new Intent(Schedule_getdata.this, Schedule.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else {
                    // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();

                    Log.d("delete stateS", "" + errcode);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Schedule_getdata.this);
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
    public void ReturnHome(View view){
        super.onBackPressed();
        try {
            clientdata.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
