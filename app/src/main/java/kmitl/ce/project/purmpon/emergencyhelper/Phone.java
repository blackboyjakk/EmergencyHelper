package kmitl.ce.project.purmpon.emergencyhelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

public class Phone  {

    private Context mContext;

    MyLocation myLocation;
    public Phone(Context context){
        mContext = context;

        myLocation= new MyLocation(mContext);
    }
    public void call(String num){
        String uri = "tel:"+num;
        Intent i1 = new Intent (Intent.ACTION_CALL, Uri.parse(uri));
        i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i1.addCategory(Intent.CATEGORY_DEFAULT);

        mContext.startActivity(i1);

    }
    protected void message(String number,String messageToSend) {
        //String messageToSend = "this is a message";


        SmsManager sms = SmsManager.getDefault();

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);

        double lat= myLocation.getLatitude();
        double lon=myLocation.getLongtitude();
        if (settings.getBoolean("sendGPS",false)) {

            if (settings.getBoolean("sendGPS",false)) {
                if (lat != 0 && lon != 0) {
                    messageToSend += "I\'m here<br/>" + "http://maps.google.com/?q=" + lat + "," + lon + "'>  http://maps.google.com/?q=" + lat + "," + lon ;
                }else {
                    messageToSend += "\nCannot get location";
                }
            }
        }
        sms.sendTextMessage(number, null, messageToSend, null, null);

    }
}
