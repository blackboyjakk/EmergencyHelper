package kmitl.ce.project.purmpon.emergencyhelper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AutoStartReceiver extends WakefulBroadcastReceiver {
    public AutoStartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i("SoundDetectService", "onReceiver");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.i("SoundDetectService", "screen off");
            Intent myIntent = new Intent(context, SoundDetectService.class);
            context.stopService(myIntent);
        }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)||intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("SoundDetectService", "screen on");
            Intent myIntent = new Intent(context, SoundDetectService.class);
            context.startService(myIntent);
        }
    }
}
