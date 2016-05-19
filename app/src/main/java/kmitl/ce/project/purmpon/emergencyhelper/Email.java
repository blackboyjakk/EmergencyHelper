package kmitl.ce.project.purmpon.emergencyhelper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Purmpon on 20-Mar-16.
 */
public class Email {
    Activity mActivity;
    String fromEmail;
    String fromPassword;
    List toEmailList;
    String toEmails;
    String emailSubject;
    String emailBody;

    public Email(Activity activity,String fromEmail, String fromPassword){
        mActivity = activity;
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        myLocation= new MyLocation(mActivity.getApplicationContext());
    }MyLocation myLocation;
    public void sendMail(String toEmails, String emailSubject, String emailBody){
        Log.i("SendMailActivity", "Send Button Clicked.");
        this.toEmails = toEmails;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        double lat=0;
        double lon=0;

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity.getApplicationContext());
         lat= myLocation.getLatitude();
         lon=myLocation.getLongtitude();
        if (settings.getBoolean("sendGPS",false)) {

            if (settings.getBoolean("sendGPS",false)) {
                if (lat != 0 && lon != 0) {
                    emailBody += "I\'m here<br/>" + "http://maps.google.com/?q=" + lat + "," + lon + "'>  http://maps.google.com/?q=" + lat + "," + lon ;
                }else {
                    emailBody += "\nCannot get location";
                }
            }
        }
        toEmailList = Arrays.asList(toEmails.split("\\s*,\\s*"));
        Log.i("SendMailActivity", "To List: " + toEmailList);

       //
        new SendMailTask(mActivity).execute(fromEmail, fromPassword, toEmailList, emailSubject, emailBody);
    }
}
