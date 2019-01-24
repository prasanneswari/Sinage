package com.jts.root.sinage_10;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
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

import static com.jts.root.sinage_10.Login.passwordE;
import static com.jts.root.sinage_10.Login.usernameE;
import static com.jts.root.sinage_10.Scan_setup.nameL;

public class Gridview_list extends AppCompatActivity implements MqttCallback {

    GridView gridlist;
    Button addbtn,cancelB;
    private ProgressDialog dialog_progress ;
    AlertDialog.Builder builderLoading;
    private static String TAG = "MQTT_android";
    MqttAndroidClient clientgrid;
    MqttConnectOptions options = new MqttConnectOptions();
    static String[] unitid = {};
    static String[] macid = {};
    String[] unitdesc = {};
    String[] operation = {};
    static String operationS,unit_idS,unit_descS,macidS;
    static int gridclickpos;
    static  boolean gridboolen;
    static String clickpos;
     String topic = "jts/dtd/response";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gridview_list);
        gridlist=(GridView)findViewById(R.id.gridlist);
        addbtn=(Button) findViewById(R.id.addbtn);
        cancelB=(Button) findViewById(R.id.cancelbtn);
        dialog_progress = new ProgressDialog(Gridview_list.this);
        builderLoading = new AlertDialog.Builder(Gridview_list.this);
        mqttconnection();
        gridboolen=false;

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Gridview_list.this, Device_list.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Gridview_list.this, Scan_setup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        //gridvaluesL.add(name);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.grid_back,R.id.gridback, unitdesc);
        gridlist.setAdapter(adapter);
        gridlist.setAdapter(adapter);
        gridlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                clickpos= parent.getAdapter().getItem(position).toString();
                int index=position;
                gridclickpos= index;
                Log.d("statuspos", "--------------" +clickpos);
                //Log.d("statuspos ing444", "--------------" +unitid[Integer.parseInt(clickpos)]);

                Intent intent = new Intent(Gridview_list.this, Schedule.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // Toast.makeText(getApplicationContext(), clickpos,Toast.LENGTH_SHORT).show();
            }
        });
        gridlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                // Toast.makeText(Gridview_list.this, "LONG PRESS", Toast.LENGTH_SHORT).show();
                //set the image as wallpaper
                return true;
            }
        });
    }
    public void publish_topic(){
        String result1 = "{" + "\"username\": \"" + usernameE + "\"," + "\"password\": \"" + passwordE + "\"}";
        Log.d(">>>publish Devicelst>>>", result1);
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = result1.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            dialog_progress.setMessage("connecting ...");
            dialog_progress.show();
            try {

                clientgrid.publish("jts/dtd/get_units", message);
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
    public void mqttconnection() {
        Log.d("Enetered ", "in sub func ");
        //Bundle b = getIntent().getExtras();
        String clientId = MqttClient.generateClientId();
        //String server_ip = "tcp://jtha.in:1883";
        String server_ip = "tcp://cld003.jts-prod.in:1883";
        Log.d("Device11 ", "subscribeScada");
        clientgrid = new MqttAndroidClient(this.getApplicationContext(), server_ip,
                clientId);
        Log.d("device22 ", "subscribeScada1");
        try {
            options.setUserName("esp");
            options.setPassword("ptlesp01".toCharArray());
            IMqttToken token = clientgrid.connect(options);
            Log.d("device33 ", "subscribeScada2");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //t.cancel();
                    Log.d("device44 ", "subscribeScada3");
                    clientgrid.setCallback(Gridview_list.this);
                    int qos = 2;
                    try {
                        IMqttToken subToken = clientgrid.subscribe(topic, qos);
                        Log.d("device55 ", "subscribeScada4");
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // successfully subscribed
                                //tv.setText("Successfully subscribed to: " + topic);
                                Log.d("success", "came here");
                                publish_topic();
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
    public void messageArrived(final String topic, MqttMessage message) throws Exception {
        JSONObject json = null;  //your response
        try {
            Log.d("messege arriv", "devicelist");
            List<String> unit_idL= new ArrayList<>() ;
            List<String> unit_descL= new ArrayList<>() ;
            List<String> operationL= new ArrayList<>() ;
            List<String> macidL= new ArrayList<>() ;

            json = new JSONObject(String.valueOf(message));
            Log.d("json::", "jsonresponse json:::::" + json);

            String errcode = json.getString("error_code");
            Log.d("login_accesstokenS::", "jsonresponse errcode:::" + errcode);

            if (errcode.contentEquals("0")) {
                // Toast.makeText(getApplicationContext(), "Response=successfully ", Toast.LENGTH_LONG).show();
                dialog_progress.dismiss();
               // clientgrid.unsubscribe(topic);
                clientgrid.disconnect();
           /* Intent intent = new Intent(Device_list.this, Device_list.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            }
            else if (errcode.contentEquals("3")) {
                // Toast.makeText(getApplicationContext(), "Response=successfully ", Toast.LENGTH_LONG).show();
                dialog_progress.dismiss();
               // clientgrid.unsubscribe(topic);
               // clientgrid.disconnect();
                Toast.makeText(getApplicationContext(), "Response=Failed to get the Device records, NO_DATA_FOUND", Toast.LENGTH_SHORT).show();

          /*  Intent intent = new Intent(Schedule_getdata.this, Schedule.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            }

            else {
                // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();

                Log.d("devicelistS", "" + errcode);
                final AlertDialog.Builder builder = new AlertDialog.Builder(Gridview_list.this);
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        R.layout.grid_back,R.id.gridback, unitdesc);
                gridlist.setAdapter(adapter);
                gridlist.setAdapter(adapter);
                gridlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                         clickpos= parent.getAdapter().getItem(position).toString();
                        int index=position;
                        gridclickpos= index;
                        Log.d("statuspos", "--------------" +clickpos);
                        //Log.d("statuspos ing444", "--------------" +unitid[Integer.parseInt(clickpos)]);

                        Intent intent = new Intent(Gridview_list.this, Schedule.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        // Toast.makeText(getApplicationContext(), clickpos,Toast.LENGTH_SHORT).show();
                    }
                });
                gridlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                                   int position, long arg3) {
                       // Toast.makeText(Gridview_list.this, "LONG PRESS", Toast.LENGTH_SHORT).show();
                        //set the image as wallpaper
                        return true;
                    }
                });
            }
             }catch (Exception e){
                   e.printStackTrace();
             }
        }
        catch (JSONException e) {
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
