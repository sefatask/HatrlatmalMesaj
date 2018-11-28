package com.example.sefa.douglasadamsalarmclocks;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class RingtonePlayingService extends Service {
    String message = "mesaj";
    int startId; // buda
    boolean isRunning;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId){

        Log.i("LocalService","Received start id" + startId + ";" + intent);

        String state = intent.getStringExtra("extra"); // buda

        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        if(!isRunning && startId==1){
            Toast.makeText(getApplicationContext(),"uygulama açıldı",Toast.LENGTH_LONG).show();
            this.isRunning = true;
            this.startId = 0;
        }
        else if(isRunning && startId==0){
            Toast.makeText(getApplicationContext(),"Uygulama kapatıldı",Toast.LENGTH_LONG).show();
            this.isRunning=false;
            this.startId=0;
        }
        else if(!isRunning && startId==0){

            this.isRunning=false;
            this.startId=0;
        }
        else if(isRunning && startId==1){
            this.isRunning =true;
            this.startId=1;
        }
        else{
            Log.e("else","somehow you reached this");
        }


        Intent intent3 = new Intent(RingtonePlayingService.this,AlarmReceive.class);
        intent3.putExtra("message",message);
        startActivity(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        Log.e("on Destroy called","ta da");
        super.onDestroy();
        this.isRunning =false;
    }
}
