package com.jts.root.sinage_10;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static com.jts.root.sinage_10.Gridview_list.gridclickpos;
import static com.jts.root.sinage_10.Gridview_list.unitid;
import static com.jts.root.sinage_10.Login.passwordE;
import static com.jts.root.sinage_10.Login.usernameE;
import static com.jts.root.sinage_10.Schedule.latSJ;
import static com.jts.root.sinage_10.Schedule.longSJ;
import static com.jts.root.sinage_10.Schedule_getdata.scheduArar;
import static com.jts.root.sinage_10.Schedule_getdata.schedulesymbol;
import static com.jts.root.sinage_10.Schedule_getdata.scheduletype;
import static com.jts.root.sinage_10.adapter_scheldule.lschedulehours;
import static com.jts.root.sinage_10.adapter_scheldule.lscheudlemns;
import static com.jts.root.sinage_10.adapter_scheldule.statuspos;

public class Update_Schedule extends AppCompatActivity implements MqttCallback {
    Spinner spinner1,spinner2,spinner3;
    Button sendB,updateB,canclenewB,hoursdec,hoursinc,mindec,mininc;
    String [] spiner1S={"SR","SS","TB","TE","AB"};
    String [] spiner2S={"+","-"};
    TextView hoursT,minT;
    String hoursS,minS;
    MqttAndroidClient clinetupdate;
    private ProgressDialog dialog_progress ;
    AlertDialog.Builder builderLoading;
    com.android.volley.RequestQueue sch_RequestQueue;
    private static String TAG = "MQTT_android";
    MqttConnectOptions options = new MqttConnectOptions();
    String selectedspival, selectedperspinner;
    static boolean setval;
    int hrstartNum=0;
    int hrendNum=24;
    int minstartNum=0;
    int minendNum=60;
    int setnum;
    MqttAndroidClient clinetsend;
    int hrstartNum1=0;
    int hrendNum1=24;
    int minstartNum1=0;
    int minendNum1=60;
    int setnumhr1=0;
    int setnummin=0;

    TextView ssE,srE,tbE,teE,timediffer;
    String remov,diff ;
    String srgetval,ssgetval,tbgetval,tegetval,timesum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update__schedule);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        //spinner3=(Spinner)findViewById(R.id.spinner3);
        updateB = (Button) findViewById(R.id.updatenew);
        sendB = (Button) findViewById(R.id.submitnew);
        //canclenewB = (Button) findViewById(R.id.cancelnew);
        hoursdec = (Button) findViewById(R.id.hoursdec);
        hoursinc = (Button) findViewById(R.id.hoursinc);
        mindec = (Button) findViewById(R.id.mindec);
        mininc = (Button) findViewById(R.id.mininc);
        hoursT = (TextView) findViewById(R.id.integer_number);
        ssE = (TextView) findViewById(R.id.ss);
        srE = (TextView) findViewById(R.id.sr);
        tbE = (TextView) findViewById(R.id.tb);
        teE = (TextView) findViewById(R.id.te);
        timediffer = (TextView) findViewById(R.id.tmdiff);

        minT = (TextView) findViewById(R.id.integer_number1);
        dialog_progress = new ProgressDialog(Update_Schedule.this);
        builderLoading = new AlertDialog.Builder(Update_Schedule.this);
        subscribe_scadaadd();
        httpRequestsave();
        //timeset();
        //timediff();
        //timediffer.setText(diff);
        hoursT.setText(String.valueOf(hrstartNum1));
        minT.setText(String.valueOf(minstartNum1));
        hoursS= String.valueOf(hrstartNum1);
        minS= String.valueOf(minstartNum1);
        /*canclenewB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Update_Schedule.this, Schedule_getdata.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                try {
                    clinetsend.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });*/

        hoursinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lschedulehours != null) {
                    if (lschedulehours.equals(String.valueOf(hrendNum))) {
                        lschedulehours = String.valueOf(hrstartNum);
                        hoursT.setText(String.valueOf(lschedulehours));
                    } else {
                        setnum = Integer.parseInt(lschedulehours);
                        setnum += 1;
                        lschedulehours = String.valueOf(setnum);
                        hoursT.setText(String.valueOf(lschedulehours));
                        hoursS = String.valueOf(lschedulehours);
                        Log.d("enter hours", "increase" + hoursS);
                    }
                }
                else {
                    if (setnumhr1 == hrendNum1) {
                        setnumhr1 = hrstartNum1;
                        hoursT.setText(String.valueOf(setnumhr1));
                        hoursS = String.valueOf(setnumhr1);
                        Log.d("enter hours else", "increase" + hoursS);
                    } else {
                        setnumhr1 += 1;
                        hoursT.setText(String.valueOf(setnumhr1));
                        hoursS = String.valueOf(setnumhr1);
                        Log.d("enter hours else", "increase" + hoursS);
                    }
                    //stuff
                }

                String minadd = hoursS + ":" + 0;
                Log.d("enter hours", "minadd" + minadd);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Log.d("selectedspiva=====", "" + selectedspival);
                //timesum=timediffer.getText().toString();

                Log.d("selectedperspinner=====", "" + selectedperspinner);
                if (selectedperspinner == null) {

                    if (selectedspival.equals("AB")) {
                        try {
                            Date addsr2 = timeFormat.parse(minadd);
                           // Date addsr1 = timeFormat.parse(srgetval);
                            long sumsr =  addsr2.getTime();
                            String totalsum = timeFormat.format(new Date(sumsr));
                            System.out.println("The sum is " + totalsum);
                            timediffer.setText(totalsum);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (selectedperspinner.equals("+")) {

                        if (selectedspival.equals("SR")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("SS")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(ssgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tbgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TE")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tegetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (selectedspival.equals("SR")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("SS")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(ssgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tbgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TE")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tegetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                       else if (selectedspival.equals("AB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                // Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr =  addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        mininc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(lscheudlemns!=null){
                    if(lscheudlemns.equals(String.valueOf(minendNum))){
                        lscheudlemns=String.valueOf(minstartNum);
                        minT.setText(String.valueOf(lscheudlemns));
                    }else {
                        setnum = Integer.parseInt(lscheudlemns);
                        setnum += 1;
                        lscheudlemns = String.valueOf(setnum);
                        minT.setText(String.valueOf(lscheudlemns));
                        minS = String.valueOf(lscheudlemns);
                        Log.d("enter hours", "increase" + minS);
                    }

                }else {
                    if(setnummin==minendNum1){
                        setnummin=minstartNum1;
                        minT.setText(String.valueOf(setnummin));
                        minS = String.valueOf(setnummin);
                        Log.d("enter hours else", "increase" + minS);
                    }else{
                        setnummin += 1;
                        minT.setText(String.valueOf(setnummin));
                        minS = String.valueOf(setnummin);
                        Log.d("enter hours else", "increase" + hoursS);
                    }
                }

                String minadd=hoursS+":"+minS;
                Log.d("enter hours", "minadd" + minadd);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Log.d("selectedspiva=====", ""+selectedspival);
                //timesum=timediffer.getText().toString();

                Log.d("selectedperspinner=====", ""+selectedperspinner);
                if (selectedperspinner == null) {

                    if (selectedspival.equals("AB")) {
                        try {
                            Date addsr2 = timeFormat.parse(minadd);
                            // Date addsr1 = timeFormat.parse(srgetval);
                            long sumsr =  addsr2.getTime();
                            String totalsum = timeFormat.format(new Date(sumsr));
                            System.out.println("The sum is " + totalsum);
                            timediffer.setText(totalsum);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (selectedperspinner.equals("+")) {

                        if (selectedspival.equals("SR")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("SS")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(ssgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tbgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TE")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tegetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (selectedspival.equals("SR")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("SS")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(ssgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tbgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TE")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tegetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (selectedspival.equals("AB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                // Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr =  addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        hoursdec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(lschedulehours!=null){
                    if(lschedulehours.equals(String.valueOf(hrstartNum))){
                        lschedulehours=String.valueOf(hrendNum);
                        hoursT.setText(String.valueOf(lschedulehours));
                    }else {
                        setnum = Integer.parseInt(lschedulehours);
                        setnum -= 1;
                        lschedulehours = String.valueOf(setnum);
                        hoursT.setText(String.valueOf(lschedulehours));
                        hoursS = String.valueOf(lschedulehours);
                        Log.d("enter hours", "increase" + hoursS);
                    }

                }else {
                    if(setnumhr1==hrstartNum1){
                        setnumhr1=hrendNum1;
                        hoursT.setText(String.valueOf(setnumhr1));
                        hoursS = String.valueOf(setnumhr1);
                        Log.d("enter hours else", "increase" + hoursS);
                    }else{
                        setnumhr1 -= 1;
                        hoursT.setText(String.valueOf(setnumhr1));
                        hoursS = String.valueOf(setnumhr1);
                        Log.d("enter hours else", "increase" + hoursS);
                    }
                }

                String minadd=hoursS+":"+0;
                Log.d("enter hours", "minadd" + minadd);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Log.d("selectedspiva=====", ""+selectedspival);
                //timesum=timediffer.getText().toString();

                Log.d("selectedperspinner=====", ""+selectedperspinner);
                if (selectedperspinner == null) {

                    if (selectedspival.equals("AB")) {
                        try {
                            Date addsr2 = timeFormat.parse(minadd);
                            // Date addsr1 = timeFormat.parse(srgetval);
                            long sumsr =  addsr2.getTime();
                            String totalsum = timeFormat.format(new Date(sumsr));
                            System.out.println("The sum is " + totalsum);
                            timediffer.setText(totalsum);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (selectedperspinner.equals("+")) {

                        if (selectedspival.equals("SR")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("SS")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(ssgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tbgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TE")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tegetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (selectedspival.equals("SR")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("SS")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(ssgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tbgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TE")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tegetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (selectedspival.equals("AB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                // Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr =  addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            }
        });
        mindec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(lscheudlemns!=null){
                    if(lscheudlemns.equals(String.valueOf(minstartNum))){
                        lscheudlemns=String.valueOf(minendNum);
                        minT.setText(String.valueOf(lscheudlemns));
                    }else {
                        setnum = Integer.parseInt(lscheudlemns);
                        setnum -= 1;
                        lscheudlemns = String.valueOf(setnum);
                        minT.setText(String.valueOf(lscheudlemns));
                        minS = String.valueOf(lscheudlemns);
                        Log.d("enter hours", "increase" + minS);
                    }

                }else {
                    if(setnummin==minstartNum1){
                        setnummin=minendNum1;
                        minT.setText(String.valueOf(setnummin));
                        minS = String.valueOf(setnummin);
                        Log.d("enter hours else", "increase" + minS);
                    }else{
                        setnummin -= 1;
                        minT.setText(String.valueOf(setnummin));
                        minS = String.valueOf(setnummin);
                        Log.d("enter hours else", "increase" + hoursS);
                    }
                }
                String minadd=hoursS+":"+minS;
                Log.d("enter hours", "minadd" + minadd);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Log.d("selectedspiva=====", ""+selectedspival);
                //timesum=timediffer.getText().toString();

                Log.d("selectedperspinner=====", ""+selectedperspinner);
                if (selectedperspinner == null) {

                    if (selectedspival.equals("AB")) {
                        try {
                            Date addsr2 = timeFormat.parse(minadd);
                            // Date addsr1 = timeFormat.parse(srgetval);
                            long sumsr =  addsr2.getTime();
                            String totalsum = timeFormat.format(new Date(sumsr));
                            System.out.println("The sum is " + totalsum);
                            timediffer.setText(totalsum);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (selectedperspinner.equals("+")) {

                        if (selectedspival.equals("SR")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("SS")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(ssgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tbgetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TE")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tegetval);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        if (selectedspival.equals("SR")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("SS")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(ssgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tbgetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else if (selectedspival.equals("TE")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                Date addsr1 = timeFormat.parse(tegetval);
                                long sumsr = addsr1.getTime() - addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (selectedspival.equals("AB")) {
                            try {
                                Date addsr2 = timeFormat.parse(minadd);
                                // Date addsr1 = timeFormat.parse(srgetval);
                                long sumsr =  addsr2.getTime();
                                String totalsum = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is " + totalsum);
                                timediffer.setText(totalsum);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        if(lscheudlemns==null) {

            ArrayAdapter<String> spinner1A = new ArrayAdapter<String>(Update_Schedule.this, android.R.layout.simple_spinner_item, spiner1S);
            spinner1A.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner1.setAdapter(spinner1A);
        }
        else {
            ArrayAdapter<String> spinner1A = new ArrayAdapter<String>(Update_Schedule.this, android.R.layout.simple_spinner_item, spiner1S);
            spinner1A.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner1.setAdapter(spinner1A);
            for (int i = 0; i < spiner1S.length; i++) {
                if (scheduletype[statuspos].equals(spiner1S[i])) {
                    spinner1.setSelection(i);
                    Log.d(" entering to if loop", "--------------" + scheduletype[statuspos]);
                    Log.d(" entering to if loop", "--------------" + spiner1S[i]);

                    break;
                }
                Log.d(" entering to if loop333", "--------------" + scheduletype[statuspos]);
            }
        }
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                selectedspival = parent.getItemAtPosition(position).toString();
                //statuspos=index;
               // scheduletypeS.set(statuspos, selectedperspinner);

               // Log.d("spinertypeList value", "--------------" + spinertypeList);


                if (selectedspival.equals("SR")) {
                    srgetval=srE.getText().toString();
                    timediffer.setText(srgetval);
                }
                else if (selectedspival.equals("SS")) {
                    ssgetval=ssE.getText().toString();
                    timediffer.setText(ssgetval);

                }
                else if (selectedspival.equals("TB")) {
                    tbgetval=tbE.getText().toString();
                    timediffer.setText(tbgetval);

                }
                else if (selectedspival.equals("TE")) {
                    tegetval=teE.getText().toString();
                    timediffer.setText(tegetval);

                }
                else if (selectedspival.equals("AB")) {
                    String abval=hoursS+":"+minS;
                    timediffer.setText(abval);

                }

                if (selectedspival.equals("AB")){
                    spinner2.setVisibility(View.INVISIBLE);
                    selectedperspinner=null;

                }
                else {
                    spinner2.setVisibility(View.VISIBLE);
                }
                Log.d("selectedvalues", "--------------" + selectedspival);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        if(lscheudlemns==null) {

            ArrayAdapter<String> spinner2A = new ArrayAdapter<String>(Update_Schedule.this, android.R.layout.simple_spinner_item, spiner2S);
            spinner2A.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(spinner2A);
        }
        else {
            ArrayAdapter<String> spinner2A = new ArrayAdapter<String>(Update_Schedule.this, android.R.layout.simple_spinner_item, spiner2S);
            spinner2A.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(spinner2A);
            for (int i = 0; i < spiner2S.length; i++) {
                if (schedulesymbol[statuspos].trim().equals(spiner2S[i])) {
                    spinner2.setSelection(i);
                    Log.d(" spinertypeList34343434", "--------------" + schedulesymbol[statuspos]);
                    break;
                }
                Log.d(" spinertypeList67676767", "--------------" + schedulesymbol[statuspos]);

            }
        }
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                int indexspn2 = position;

                // TODO Auto-generated method stub
                selectedperspinner = parent.getItemAtPosition(position).toString();
                //statuspos=index;
                Log.d("selectedperspinner", "--------------" + selectedperspinner);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        sendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedspival.equals("AB")){
                    spinner2.setVisibility(View.INVISIBLE);
                    selectedperspinner=null;
                }
                else {
                    spinner2.setVisibility(View.VISIBLE);
                }
                String result1 = "{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"unit_id\":\"" + unitid[gridclickpos] + "\",\"type\":\"" + selectedspival + "\",\"symbol\":\"" + selectedperspinner+ "\",\"hours\":\"" + hoursS +  "\",\"mns\":\"" + minS + "\"}";
                Log.d(">>>publish Devicelst>>>", result1);
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = result1.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    dialog_progress.setMessage("connecting ...");
                    dialog_progress.show();
                    try {
                        clinetsend.publish("jts/dtd/add_shedule", message);
                        Log.d(">>>publish enetr>>>","enter%%%%%%");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    clinetsend.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        updateB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // subscribe_scadaupdate();
                if (selectedspival.equals("AB")){
                    spinner2.setVisibility(View.INVISIBLE);
                    selectedperspinner=null;

                }
                else {
                    spinner2.setVisibility(View.VISIBLE);
                }
                String result1 = "{\"username\":\"" + usernameE + "\",\"password\":\"" + passwordE + "\",\"shedule_id\":\"" + scheduArar[statuspos] + "\",\"type\":\"" + selectedspival + "\",\"symbol\":\"" + selectedperspinner+ "\",\"hours\":\"" + hoursS +  "\",\"mns\":\"" + minS + "\"}";
                Log.d(">>>publish updatesch>>>", result1);
                byte[] encodedPayload = new byte[0];
                try {
                    encodedPayload = result1.getBytes("UTF-8");
                    MqttMessage message = new MqttMessage(encodedPayload);
                    dialog_progress.setMessage("connecting ...");
                    dialog_progress.show();
                    try {
                        clinetsend.publish("jts/dtd/update_shedule", message);
                        Log.d(">>>publish enetr>>>","enter%%%%%%");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    clinetsend.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
        setval=false;
      if(lschedulehours!=null){
          hoursT.setText(lschedulehours);
          Log.d("enter true", "true2222" );
          hoursS=lschedulehours;
      }
        if(lscheudlemns!=null){
            minT.setText(lscheudlemns);
            Log.d("enter true", "true2222" );
            minS=lscheudlemns;
        }
    }
    public void subscribe_scadaadd() {
        Log.d("Enetered ", "in sub func ");
        //Bundle b = getIntent().getExtras();
        String clientId = MqttClient.generateClientId();
        final String topic = "jts/dtd/response";
        //String server_ip = "tcp://jtha.in:1883";
        String server_ip = "tcp://cld003.jts-prod.in:1883";
        Log.d("Enetered ", "subscribeScada");
        clinetsend = new MqttAndroidClient(this.getApplicationContext(), server_ip,
                clientId);
        Log.d("Enetered ", "subscribeScada1");
        try {
            options.setUserName("esp");
            options.setPassword("ptlesp01".toCharArray());
            IMqttToken token = clinetsend.connect(options);
            Log.d("Enetered ", "subscribeScada2");
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //t.cancel();
                    Log.d("Enetered ", "subscribeScada3");
                    clinetsend.setCallback(Update_Schedule.this);
                    int qos = 2;
                    try {
                        IMqttToken subToken = clinetsend.subscribe(topic, qos);
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
                    }
                    catch (MqttException e) {
                        e.printStackTrace();
                        Log.d("error", "!");
                    }
                    catch (NullPointerException e) {
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
        }
        catch (MqttException e) {
            e.printStackTrace();
            Log.d(TAG, "onFailure");
        }
    }
    @Override
    public void connectionLost(Throwable cause) {

    }
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d("messege arriv", "Counter");

        JSONObject json = null;  //your response
        try {
            json = new JSONObject(String.valueOf(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String errcode = json.getString("error_code");
        Log.d("counterS::", "jsonresponse errcode:::" + errcode);

        if (errcode.contentEquals("0")) {
           // Toast.makeText(getApplicationContext(), "Response=successfully counter", Toast.LENGTH_LONG).show();
            clinetsend.unsubscribe(topic);
            //dialog_progress.dismiss();
           // timediffer.setText(diff);

            Intent intent = new Intent(Update_Schedule.this, Schedule_getdata.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // Toast.makeText(getApplicationContext(), "Not added the notes", Toast.LENGTH_LONG).show();
            Log.d("counterS", "" + errcode);
            final AlertDialog.Builder builder = new AlertDialog.Builder(Update_Schedule.this);
            builder.setMessage("Reasponse=Failed to counter");
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
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void httpRequestsave() {
        RequestQueue queue = Volley.newRequestQueue(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = mdformat.format(calendar.getTime());
        Log.d("current time :", strDate);

        // String url="http://192.168.2.80/message?esplist=\""+wifiarray+"\"&&time=\""+spintime +"\"&&lightpercentage=\""+spinlight+"\"";
        String url = "http://api.sunrise-sunset.org/json?lat=\"" + latSJ + "\"&lng=\"" + longSJ + "\"&date=\"" + strDate + "\"";
        remov=url.replace("\"","");

        Log.d("sending string is :", String.valueOf(remov));

       /* for (String split : splitterString) {
            Log.d("sending string is :", String.valueOf(split));
        }*/
        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, remov,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("hello response :", response.toString());

                        JSONObject responseJSON = null;
                        JSONObject jsonReq;
                        try {
                            responseJSON = new JSONObject(response);
                            try {

                                String surname = responseJSON.getString("status");
                                Log.d("sattaus response :", surname);

                                JSONObject subObj = responseJSON.getJSONObject("results");
                                String sunrise = subObj.getString("sunrise");
                                Log.d("sunrise response :", sunrise);
                                String sunset = subObj.getString("sunset");
                                Log.d("sunset response :", sunset);
                                String solar_noon = subObj.getString("solar_noon");
                                Log.d("solar_noon response :", solar_noon);
                                String day_length = subObj.getString("day_length");
                                Log.d("day_length response :", day_length);
                                String civil_twilight_begin = subObj.getString("civil_twilight_begin");
                                Log.d("civil_twilight_begin :", civil_twilight_begin);
                                String civil_twilight_end = subObj.getString("civil_twilight_end");
                                Log.d("civil_twilight_end ", civil_twilight_end);

                                DateFormat f1 = new SimpleDateFormat("hh:mm:ss a"); //11:00 pm
                                Date d = null;
                                try {
                                    d = f1.parse(civil_twilight_end);
                                } catch (ParseException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                DateFormat f2 = new SimpleDateFormat("HH:mm");
                                String convert_24hrs = f2.format(d); // "23:00"
                                Log.d("convert_24hrs=== ", convert_24hrs);

                                String nautical_twilight_begin = subObj.getString("nautical_twilight_begin");
                                Log.d("nautical_twilight_begin", nautical_twilight_begin);
                                String nautical_twilight_end = subObj.getString("nautical_twilight_end");
                                Log.d("nautical_twilight_end", nautical_twilight_end);
                                String astronomical_twilight_begin = subObj.getString("astronomical_twilight_begin");
                                Log.d("astronomicaltwili_begin", astronomical_twilight_begin);
                                String astronomical_twilight_end = subObj.getString("astronomical_twilight_end");
                                Log.d("astronomicaltwilig_end", astronomical_twilight_end);

                                String addtime="5:30";
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                                Date addsr1 = timeFormat.parse(sunrise);
                                Date addsr2 = timeFormat.parse(addtime);
                                long sumsr = addsr1.getTime() + addsr2.getTime();
                                String totalsr = timeFormat.format(new Date(sumsr));
                                System.out.println("The sum is "+totalsr);
                                try {
                                    final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                    final Date dateObj = sdf.parse(totalsr);
                                    System.out.println("convert 12 hours formate"+dateObj);
                                    System.out.println(new SimpleDateFormat("K:mm").format(dateObj));
                                    String convert_12hrs=new SimpleDateFormat("K:mm").format(dateObj);
                                    Log.d("convert 12 rs ", "response......."+convert_12hrs);
                                    srE.setText(convert_12hrs);

                                } catch (final ParseException e) {
                                    e.printStackTrace();
                                }
                                Date addss1 = timeFormat.parse(sunset);
                                Date addss2 = timeFormat.parse(addtime);
                                long sumss = addss1.getTime() + addss2.getTime();
                                String totalss = timeFormat.format(new Date(sumss));
                                System.out.println("The sum totalss "+totalss);
                                ssE.setText(totalss);

                                Date addstb1 = timeFormat.parse(civil_twilight_begin);
                                Date addstb2 = timeFormat.parse(addtime);
                                long sumtb = addstb1.getTime() + addstb2.getTime();
                                String totaltb = timeFormat.format(new Date(sumtb));
                                System.out.println("The sum totaltb "+totaltb);

                                try {
                                    final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                                    final Date dateObj = sdf.parse(totaltb);
                                    System.out.println("convert 12 hours formate"+dateObj);
                                    System.out.println(new SimpleDateFormat("K:mm").format(dateObj));
                                    String convert_12hrs=new SimpleDateFormat("K:mm").format(dateObj);
                                    Log.d("convert 12 rs ", "response......."+convert_12hrs);
                                    tbE.setText(convert_12hrs);
                                } catch (final ParseException e) {
                                    e.printStackTrace();
                                }

                                Date addte1 = timeFormat.parse(convert_24hrs);
                                Date addte2 = timeFormat.parse(addtime);
                                long sumte = addte1.getTime() + addte2.getTime();
                                String totalte = timeFormat.format(new Date(sumte));
                                System.out.println("The sum totalte "+totalte);
                                teE.setText(totalte);
                            }
                            catch (JSONException e)
                            {

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.d("hello1 ", "error.......");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void ReturnHome(View view){
        super.onBackPressed();
        try {
            clinetsend.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
