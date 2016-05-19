package kmitl.ce.project.purmpon.emergencyhelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;

import kmitl.ce.project.purmpon.emergencyhelper.R;

public class SoundDetectService extends Service implements  RecognitionListener{

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    protected AudioManager mAudioManager;
    private onResultsReady mListener;
    protected boolean mIsListening;
    private boolean mIsStreamSolo;
    private boolean mMute=true;
    private boolean mRunning;
    Intent intent;
    String keyword;

    private String LOG_TAG = "SoundDetectService";

    public void onCreate() {

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        keyword = settings.getString("keyword","help me");

        mRunning = false;
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, settings.getString("lang","th-TH"));
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent = new Intent(this, BlackActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        showResult("LOCAL_UPDATED","results","service start");
        Log.i(LOG_TAG,"service start");

        startListening();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    protected void openApp(){
        try {
            Log.i(LOG_TAG, "Open App");

            //           intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            Log.d(LOG_TAG, "afterDestroy");
            this.startActivity(intent);
        }catch (Exception e){
            Log.e(LOG_TAG,e.toString());
        }
    }

    private void showResult(String location,String key,String text){
        Intent i = new Intent(location);
        i.putExtra(key, text);
        sendBroadcast(i);
    }

    private void listenAgain()
    {
        if(mIsListening) {
            mIsListening = false;
            speech.cancel();
            startListening();
        }
    }
    private void startListening()
    {
        if(!mIsListening)
        {
            mIsListening = true;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                // turn off beep sound
//                if (!mIsStreamSolo && mMute) {
//                    mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
//                    mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
//                    mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//                    mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
//                    mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
//                    mIsStreamSolo = true;
//                }
//            }
            speech.startListening(recognizerIntent);
        }
    }
    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        Intent i = new Intent("LOCATION_UPDATED");
        i.putExtra("results", "beginningOfSpeech");
        sendBroadcast(i);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        if(error==SpeechRecognizer.ERROR_RECOGNIZER_BUSY)
        {
            if(mListener!=null) {
                ArrayList<String> errorList=new ArrayList<String>(1);
                errorList.add("ERROR RECOGNIZER BUSY");
                if(mListener!=null)
                    mListener.onResults(errorList);
            }
            return;
        }

        if(error==SpeechRecognizer.ERROR_NO_MATCH)
        {
            if(mListener!=null)
                mListener.onResults(null);
        }

        if(error==SpeechRecognizer.ERROR_NETWORK)
        {
            ArrayList<String> errorList=new ArrayList<String>(1);
            errorList.add("STOPPED LISTENING");
            if(mListener!=null)
                mListener.onResults(errorList);
        }
        Log.d(LOG_TAG, "error = " + error);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listenAgain();
            }
        }, 1000);

    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results)
    {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches) {
            if(result.trim().equals(keyword)){
                Log.i(LOG_TAG, "correct key word" + result);
                openApp();
            }
            Log.i(LOG_TAG,"Show word : "+result);
            text += result + "\n";
        }
        showResult("LOCATION_UPDATED","results",text);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listenAgain();
            }
        }, 1000);

    }

    @Override
    public void onRmsChanged(float rmsdB) {
       // Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);


    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void destroy(){
        mIsListening=false;
        if (!mIsStreamSolo) {
            mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
            mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            mIsStreamSolo = true;
        }
        Log.d(LOG_TAG, "onDestroy");
        if (speech != null)
        {
            speech.stopListening();
            speech.cancel();
            speech.destroy();
            speech=null;
        }
        super.onDestroy();
    }
    public boolean ismIsListening() {
        return mIsListening;
    }


    public interface onResultsReady
    {
        public void onResults(ArrayList<String> results);
    }

    public void mute(boolean mute)
    {
        mMute=mute;
    }

    public boolean isInMuteMode()
    {
        return mMute;
    }
}
