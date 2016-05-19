package kmitl.ce.project.purmpon.emergencyhelper;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Media extends android.view.SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private MediaRecorder mRecorder;


    public Media(Context context) {
        super(context);
        mContext = context;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

  //      mCamera=getCameraInstance();
    }

    protected void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.reset();   // clear recorder configuration
            mRecorder.release(); // release the recorder object
            mRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }
    protected void unLock(){
        mCamera=getCameraInstance();
    }

    protected void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    protected Camera getCameraInstance() {
        // TODO Auto-generated method stub
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        Camera.Parameters params = c.getParameters();
        params.setRotation(90);
        c.setParameters(params);
        return c; // returns null if camera is unavailable
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputPictureFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {

            } catch (IOException e) {
            }
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
            }
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
            }

        }
    };

    public void takePicture(){
        mCamera.takePicture(null, null, mPicture);

    }


    private static File getOutputPictureFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory("Project0"), "PICTURE");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Project0", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator+"_PICTURE_"+ timeStamp + ".jpg");

        return mediaFile;
    }
    private static String getOutputMediaFile(String format) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory("Project0"), format);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Project0", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String mediaFile = mediaStorageDir.getPath() + File.separator+"_"+format+"_"+timeStamp;
        return mediaFile;
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int weight,
                               int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(90);

        mCamera.setParameters(params);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera = Camera.open();
            setWillNotDraw(false);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mCamera.stopPreview();
        mCamera.release();
    }

    public void startVideoRecord(){

        if (!prepareMediaRecorder()) {
            Toast.makeText(mContext,"Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(mContext, "Start recording...",Toast.LENGTH_SHORT).show();
        mRecorder.start();
    }
    public void stopVideoRecord(){
        mRecorder.stop();  // stop the recording
        Toast.makeText(mContext, "Stop recording...",Toast.LENGTH_SHORT).show();
        releaseMediaRecorder(); // release the MediaRecorder object
    }
    private boolean prepareMediaRecorder() {
        String outputFile = getOutputMediaFile("VIDEO")+".mp4";
        mRecorder = new MediaRecorder();

        mCamera.unlock();
        mRecorder.setCamera(mCamera);
        mRecorder.setOrientationHint(90);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
        mRecorder.setOutputFile(outputFile);
        mRecorder.setMaxDuration(60000); // Set max duration 60 sec.
        mRecorder.setMaxFileSize(5000000); // Set max file size 5M
        mRecorder.setPreviewDisplay(getHolder().getSurface());

        try {
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private boolean prepareSoundRecorder() {

        String outputFile = getOutputMediaFile("SOUND")+".mp4";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setOutputFile(outputFile);

        try {
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            e.printStackTrace();
            return false;
        }
        return true;

    }


    public void startSoundRecord(){

        prepareSoundRecorder();
        mRecorder.start();
        Toast.makeText(mContext, "Start recording...",Toast.LENGTH_SHORT).show();
    }

    public void stopSoundRecord(){
        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder  = null;
            Toast.makeText(mContext, "Stop recording...", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            //  it is called before start()
            e.printStackTrace();
        } catch (RuntimeException e) {
            // no valid audio/video data has been received
            e.printStackTrace();
        }
    }
}
