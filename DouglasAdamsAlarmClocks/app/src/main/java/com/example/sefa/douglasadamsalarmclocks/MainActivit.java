package com.example.sefa.douglasadamsalarmclocks;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivit extends AppCompatActivity {

    AlarmManager alarmManager;
    TimePicker timePicker;
    TextView update_text;
    Context context;
    PendingIntent pendingIntent;
    EditText ephone,emessage;
    int MY_PERMISSION_REQUEST_SEND_SMS=1;
    String sent ="SMS SENT";
    String DELIVERED = "SMS DELIVERED";
    PendingIntent sentPI,deliveredPI;
    BroadcastReceiver smsSentReceiver , smsDeliverReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context=this;

        sentPI = PendingIntent.getBroadcast(this,0,new Intent(sent),0);
        deliveredPI = PendingIntent.getBroadcast(this,0,new Intent(DELIVERED),0);
        ephone = (EditText)findViewById(R.id.editText);
        emessage = (EditText)findViewById(R.id.editText2);
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        timePicker = (TimePicker)findViewById(R.id.timepicker);
        update_text = (TextView)findViewById(R.id.update_text);
        final Calendar calendar = Calendar.getInstance();
        final Button alarm_on = (Button)findViewById(R.id.alarm_on);
        final Intent myIntent = new Intent(this,AlarmReceive.class);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Telefon saatinden veri çekme
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String hour_string = String.valueOf(hour);
                String minute_string = String.valueOf(minute);

                if(hour>12){
                    hour_string = String.valueOf(hour-12);
                }
                if(minute<10){
                    minute_string = "0" +String.valueOf(minute);
                }

                set_alarm_text("Alarm set to : " + hour_string + ":" + minute_string);

                myIntent.putExtra("extra","yes");
                pendingIntent = PendingIntent.getBroadcast(MainActivit.this,0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
            }
        });


        Button alarm_off = (Button)findViewById(R.id.alarm_off);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_alarm_text("Alarm off");
                alarmManager.cancel(pendingIntent);
                myIntent.putExtra("extra","alarm off");
                sendBroadcast(myIntent);        //En son bunu ekledik program bundan once gayet iyi çalısıyordu.
            }
        });

    }

    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent12 = getIntent();
        String mesag = intent12.getStringExtra("message");

        if(mesag.equals("message")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivit.this);
            alertDialog.setTitle("Uyarı !");
            alertDialog.setMessage("Mesaj Gönderiliyor!!!");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    btn_SendSMS_OnClick();

                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Mesaj Gönderilemedi.", Toast.LENGTH_LONG).show();
                }
            });

            alertDialog.show();
        }
        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        Toast.makeText(MainActivit.this,"SMS sent" ,Toast.LENGTH_SHORT ).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(MainActivit.this,"GENERIC failure!",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(MainActivit.this,"No service!",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(MainActivit.this,"NULL pdu",Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(MainActivit.this,"Radio off",Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        };
        smsDeliverReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                switch(getResultCode()){

                    case Activity.RESULT_OK:
                        Toast.makeText(MainActivit.this,"OK!",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(MainActivit.this,"Sms don't delivered!",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(smsSentReceiver,new IntentFilter(sent));
        registerReceiver(smsDeliverReceiver,new IntentFilter(DELIVERED));
    }


    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(smsDeliverReceiver);
        unregisterReceiver(smsSentReceiver);
    }

    public void btn_SendSMS_OnClick(){

        final String message=emessage.getText().toString();
        final String number=ephone.getText().toString();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSION_REQUEST_SEND_SMS);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSION_REQUEST_SEND_SMS);
        }
        else{
            try {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(number, null, message, sentPI, deliveredPI);

                Toast.makeText(MainActivit.this, "Message Granted!", Toast.LENGTH_SHORT).show();
                emessage.setText("");
                ephone.setText("");
            }
            catch (Exception e){
                Toast.makeText(MainActivit.this,"FAILED!",Toast.LENGTH_SHORT).show();
            }
        }

    }


}
