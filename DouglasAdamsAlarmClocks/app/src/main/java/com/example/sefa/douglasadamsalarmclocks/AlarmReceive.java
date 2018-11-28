package com.example.sefa.douglasadamsalarmclocks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,MainActivit.class);
        String message = intent1.getStringExtra("message");
        intent1.putExtra("message",message);
        context.startService(intent1);
        Log.e("We are in the receiver","Yay!");
        String getYourString = intent.getExtras().getString("extra"); // buda
        Log.e("What is your kay",getYourString);                        // buda
        Intent service_intent = new Intent(context,RingtonePlayingService.class);
        service_intent.putExtra("extra",getYourString);
        context.startService(service_intent);
    }
}
