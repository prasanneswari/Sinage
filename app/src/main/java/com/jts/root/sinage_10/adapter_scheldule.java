package com.jts.root.sinage_10;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.jts.root.sinage_10.Schedule_getdata.schedulehours;
import static com.jts.root.sinage_10.Schedule_getdata.schedulemns;
import static com.jts.root.sinage_10.Schedule_getdata.scheduleststus;
import static com.jts.root.sinage_10.Schedule_getdata.schedulesymbol;
import static com.jts.root.sinage_10.Schedule_getdata.scheduletype;
import static com.jts.root.sinage_10.Schedule_getdata.statusS;


public class adapter_scheldule extends ArrayAdapter<String> {

    private Context context;
    private List<String> schedule;
    static String selectedperspinner,selectedact_inact_spinner;
    static int statuspos;
    boolean[] checkBoxState;
    public static String lschedule,lscheduletype,lschedulesymol,lschedulehours,lscheudlemns;
    String [] spinner_onoff={"on","off"};
    String [] spinner_actinact={"active","inactive"};

    static  boolean rowclick;
    static String onval,offval,act_inactval;
    ArrayList<Integer> selchkboxlist=new ArrayList<Integer>();


    int index;
    public adapter_scheldule(Context context, List<String> scheduleS) {
        super(context, R.layout.activity_schedule, scheduleS);
        //Assinging the 'RequisitionData' array values to the local arrays inside adapter
        this.context = context;
        this.schedule = scheduleS;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adapter_scheldule, parent, false);  //Setting content view of xml
        CustomObjects holder = null;
        holder = new CustomObjects();
         index=position;
        //rowclick=false;


        holder.scheduleT = (TextView) rowView.findViewById(R.id.schedule);
        holder.on_offS = (Spinner) rowView.findViewById(R.id.on_off);
        holder.delchekC = (CheckBox) rowView.findViewById(R.id.checkBox);
        holder.act_inact = (Spinner) rowView.findViewById(R.id.act_inact);
        rowView.setTag(holder);

        try {
            holder.scheduleT.setText(schedule.get(position));
            try {

            }catch (Exception e){
                e.printStackTrace();
            }
            ArrayAdapter<String> on_offSA= new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, spinner_onoff);
            on_offSA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.on_offS.setAdapter(on_offSA);

            ArrayAdapter<String> act_inactA= new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item, spinner_actinact);
            act_inactA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.act_inact.setAdapter(act_inactA);


            holder.on_offS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // TODO Auto-generated method stub
                     selectedperspinner = parent.getItemAtPosition(position).toString();
                    Log.d("statuspos", "--------------" +selectedperspinner);
                    if(selectedperspinner.equals("on")){
                         onval= String.valueOf(1);
                        Log.d("onval---", "--------------" +onval);

                    }
                    else if (selectedperspinner.equals("off")){
                        onval=String.valueOf(0);
                        Log.d("offval---", "--------------" +onval);

                    }

                }


                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
            final CustomObjects finalHolder = holder;
            Log.d("sttsu position###", "--------------" +scheduleststus[index]);
            for (int i = 0; i < spinner_actinact.length; i++) {
                if (scheduleststus[index].trim().equals(spinner_actinact[i])) {
                    finalHolder.act_inact.setSelection(i);
                    Log.d(" spinertypeList34343434", "--------------" + scheduleststus[index]);
                    break;
                }
                Log.d(" spinertypeList67676767", "--------------" + scheduleststus[index]);

            }
            holder.act_inact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // TODO Auto-generated method stub
                    selectedact_inact_spinner = parent.getItemAtPosition(position).toString();
                    Log.d("statuspos", "--------------" +selectedact_inact_spinner);

                    /*for (int i = 0; i < spinner_actinact.length; i++) {
                        if (scheduleststus[index].trim().equals(spinner_actinact[i])) {
                            finalHolder.act_inact.setSelection(i);
                            Log.d(" spinertypeList34343434", "--------------" + schedulesymbol[statuspos]);
                            break;
                        }
                        Log.d(" spinertypeList67676767", "--------------" + schedulesymbol[statuspos]);

                    }
*/
                        if(selectedact_inact_spinner.equals("active")){
                        act_inactval= String.valueOf(1);
                        Log.d("on active val---", "--------------" +act_inactval);

                    }
                    else if (selectedact_inact_spinner.equals("inactive")){
                        act_inactval=String.valueOf(0);
                        Log.d("off inactive---", "--------------" +act_inactval);

                    }



                }


                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), Update_Schedule.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);

                lschedule = schedule.get(position);
                statuspos=position;

                lscheduletype=scheduletype[position];
                lschedulesymol=schedulesymbol[position];
                lschedulehours=schedulehours[position];
                lscheudlemns=schedulemns[position];


              /*  spinertypeList.add(lscheduletype);
                spinersymList.add(lschedulesymol);
                spinerhoursList.add(lschedulehours);
                spinerminList.add(lscheudlemns);
               // rowclick=true;


                Log.d("Location" ," statusrow pos :" + statuspos);
                Log.d("Location" ," lschedule1111 :" + lschedule);

                Log.d("Location" ," scheduArariddd2222 :" + scheduArar[position]);
                Log.d("Location" ," lschedulesymol3333 :" + lschedulesymol);
                Log.d("Location" ," lscheduletype4444 :" + lscheduletype);
                Log.d("Location" ," lschedulehours4444 :" + lschedulehours);
                Log.d("Location" ," lscheudlemns4444 :" + lscheudlemns);

                scheduletypeS = new String[spinertypeList.size()];
                schedulesymbolS = new String[spinersymList.size()];
                schedulehoursS = new String[spinerhoursList.size()];
                schedulemnS = new String[spinerminList.size()];

                for (int l = 0; l < spinerminList.size(); l++) {
                    scheduletypeS[l] = spinertypeList.get(l);
                    schedulesymbolS[l] = spinersymList.get(l);
                    schedulehoursS[l] = spinerhoursList.get(l);
                    schedulemnS[l] = spinerminList.get(l);

                    Log.d("scheduletypeS11 ", scheduletypeS[l]);
                    Log.d("schedulesymbolS22 ", schedulesymbolS[l]);
                    Log.d("schedulehoursS333 ", schedulehoursS[l]);
                    Log.d("schedulemnS333 ", schedulemnS[l]);

                }
*/
                //lon_off = on_off[position];
               // lactive_inactive = active_inactive[position];
            }
        });
        holder.delchekC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(((CompoundButton) v).isChecked())
                {
                    statuspos=position;
                    Log.d("Location" ," position1111 :" + statuspos);
                    //lschedule = schedule[position];
                    //Log.d("Location" ," delcheckbox :" + lschedule);
                    // Add to checkbox array

                }
                else
                {
                    // Remove from checkbox array
                }
            }
        });
        /*holder.act_inact.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(((CompoundButton) v).isChecked())
                {
                    // Add to checkbox array
                }
                else
                {
                    // Remove from checkbox array
                }
            }
        });*/
        return rowView;
    }
    public class CustomObjects
    {
        CheckBox delchekC;
        TextView scheduleT;
        Spinner on_offS,act_inact;
    }
}
