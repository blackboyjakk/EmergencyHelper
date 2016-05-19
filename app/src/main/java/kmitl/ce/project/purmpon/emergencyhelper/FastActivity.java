package kmitl.ce.project.purmpon.emergencyhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import kmitl.ce.project.purmpon.emergencyhelper.R;

public class FastActivity extends AppCompatActivity {

    private static final String TAG = FastActivity.class.getSimpleName();

    private Phone phone;
    private Media media;
    private Email email;
    Intent myIntent;
    TextView status_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        registerReceiver(uiUpdated, new IntentFilter("LOCATION_UPDATED"));


        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        phone = new Phone(getApplicationContext());
        media = new Media(getApplicationContext());

        email = new Email(FastActivity.this,settings.getString("Youremail",""),settings.getString("pass","").toString());

        FrameLayout myCameraPreview = (FrameLayout) findViewById(R.id.videoview);
        myCameraPreview.addView(media);


        Button bt_call = (Button) findViewById(R.id.bt_call);
        Button bt_message = (Button)findViewById(R.id.bt_message);
        Button bt_mail = (Button)findViewById(R.id.bt_mail);
        final ToggleButton bt_sound = (ToggleButton) findViewById(R.id.bt_sound);
        Button bt_photo = (Button) findViewById(R.id.bt_photo);
        final ToggleButton bt_video = (ToggleButton) findViewById(R.id.bt_video);


        myIntent = new Intent(getApplicationContext(), SoundDetectService.class);




        bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.call(settings.getString("phone", ""));
            }
        });

        bt_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.message(settings.getString("phone", ""),settings.getString("sms",""));
            }
        });

        bt_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.sendMail(settings.getString("destEmail", ""), settings.getString("emailSubject", ""), settings.getString("emailBody", ""));
            }
        });

        bt_sound.setTextOff("Sound recorder");
        bt_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    bt_sound.setTextOn("Recording");
                    media.startSoundRecord();
                } else {
                    // The toggle is disabled
                    bt_sound.setTextOff("Sound recorder");
                    media.stopSoundRecord();
                }
            }
        });

        bt_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                media.takePicture();
            }
        });

        bt_video.setTextOff("Video recorder");
        bt_video.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    bt_video.setTextOn("Recording");
                    media.startVideoRecord();
                } else {
                    // The toggle is disabled
                    bt_video.setTextOff("Video recorder");
                    media.stopVideoRecord();
                }
            }
        });


        if (savedInstanceState != null) {
            Log.d(TAG, "onCreate() Restoring previous state");
            /* restore state */
        } else {
            Log.d(TAG, "onCreate() No saved state available");
            /* initialize app */
        }
    }

//    private BroadcastReceiver uiUpdated= new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//
//
//        }
//    };

    public void onResume(){
        super.onResume();
        getApplicationContext().stopService(myIntent);
    }

    public void onPause(){
        getApplicationContext().startService(myIntent);
        super.onPause();
    }

    public void onStop(){
//        media.releaseMediaRecorder();
//        media.releaseCamera();
//        unregisterReceiver(uiUpdated);
        getApplicationContext().startService(myIntent);
        super.onStop();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu:
                Intent intent_setting = new Intent(FastActivity.this,SettingsActivity.class);
                startActivity(intent_setting);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
