package com.jts.root.sinage_10;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
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
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.jts.root.sinage_10.Device_list.macid;
import static com.jts.root.sinage_10.Device_list.unit_descS;
import static com.jts.root.sinage_10.Device_list.unitdesc;


public class Adapter_devicelst extends ArrayAdapter<String> implements MqttCallback {
    customButtonListener customListner;


    public interface customButtonListener {
        public void onButtonClickListner(int position, String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;
     String [] data;
     static String switchpos;
     static int switchindex;
    AlertDialog.Builder builderLoading;
    private static String TAG = "MQTT_android";
    MqttAndroidClient clientmac;
    String time;




    MqttConnectOptions options = new MqttConnectOptions();


    public Adapter_devicelst(Context context, String[] dataItem) {
        super(context, R.layout.activity_device_list, dataItem);
        this.data = dataItem;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.adapter_devicelst, null);
            viewHolder = new ViewHolder();
            //viewHolder.Switch.setChecked(isChecked);

            viewHolder.text = (TextView) convertView.findViewById(R.id.childTextView);
            viewHolder.Switch = (Switch) convertView.findViewById(R.id.Switch);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final String temp = getItem(position);
        viewHolder.text.setText(temp);

        viewHolder.Switch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if(isChecked) {
                            switchindex=position;
                            subscribe_scadamacadd();

                            switchpos= String.valueOf(1);
                            Log.d("onswitch---", "--------------" +switchpos);

                          //  Toast.makeText(context, "Switch On", Toast.LENGTH_SHORT).show();
                        }else {
                            switchindex=position;
                            subscribe_scadamacadd();

                            switchpos= String.valueOf(0);
                            Log.d("offseitch---", "--------------" +switchpos);

                           // Toast.makeText(context, "Switch off", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        return convertView;
    }

    public class ViewHolder {
        TextView text;
        Switch Switch;
    }

    public void subscribe_scadamacadd() {
        Log.d("Enetered ", "in sub func ");
        //Bundle b = getIntent().getExtras();
        String clientId = MqttClient.generateClientId();
        final String topicmac = "jts/dtd/response";
        //String server_ip = "tcp://jtha.in:1883";
        String server_ip = "tcp://cld003.jts-prod.in:1883";
        Log.d("devicemac11 ", "subscribeScada");
        clientmac = new MqttAndroidClient(context, server_ip,
                clientId);
        Log.d("devicemac22 ", "subscribeScada1");
        try {
            options.setUserName("esp");
            options.setPassword("ptlesp01".toCharArray());
            IMqttToken token = clientmac.connect(options);
            Log.d("devicemac33 ", "subscribeScada2");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //t.cancel();
                    Log.d("devicemac44 ", "subscribeScada3");
                    clientmac.setCallback(Adapter_devicelst.this);
                    int qos = 2;
                    try {
                        IMqttToken subToken = clientmac.subscribe(topicmac, qos);
                        Log.d("devicemac55 ", "subscribeScada4");
                        subToken.setActionCallback(new IMqttActionListener() {

                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // successfully subscribed
                                //tv.setText("Successfully subscribed to: " + topic);
                                Log.d("success", "came here");

                                try {
                                    Calendar calendar1=Calendar.getInstance();
                                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
                                     time=simpleDateFormat.format(calendar1.getTime());
                                    System.out.println("time in 12 hour format : " + time.toString());

/*
                                    String now = new SimpleDateFormat("hh:mm aa").format(new java.util.Date().getTime());
                                    System.out.println("time in 12 hour format : " + now);
                                    SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
                                    SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");
                                     time = outFormat.format(inFormat.parse(now));
                                    System.out.println("time in 24 hour format : " + time);*/
                                } catch (Exception e) {
                                    System.out.println("Exception : " + e.getMessage());
                                }
                                /*Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
                                String strDate =  mdformat.format(calendar.getTime());
*/
                                String result1 = "{\"time\":\"AB\",\"offset\":\"" + time + "\",\"operation\":\"" + switchpos + "\"}";
                                // String result1 ="{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"unit_id\":\"" + unitid[gridclickpos] + "\",\"long\":\"" + langitudeE+ "\",\"lat\":\"" + latitudeE + "\"}";
                                Log.d(">>publish device mac>>", result1);
                                //result1=result1.replaceAll(" ","");
                                // result1 = result1.trim();
                                byte[] encodedPayload = new byte[0];
                                try {
                                    encodedPayload = result1.getBytes("UTF-8");
                                    MqttMessage messagemac = new MqttMessage(encodedPayload);
                                    //dialog_progress.setMessage("connecting ...");
                                   // dialog_progress.show();
                                    try {
                                        clientmac.publish("jts/dtd/"+unitdesc[switchindex], messagemac);
                                        Log.d(">>publish valu>>", "jts/dtd/"+macid[switchindex]);

                                        Log.d(">>publih devmac entr>>", "enter%%%%%%");
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
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
