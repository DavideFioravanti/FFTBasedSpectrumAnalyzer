package somitsolutions.android.audio;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import ca.uol.aig.fftpack.RealDoubleFFT;


public class SoundRecordAndAnalysisActivity extends Activity implements OnClickListener{
	
	int frequency = 8000;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;


    private RealDoubleFFT transformer;
    int blockSize = 256;
    Button startStopButton;
    boolean started = false;

    RecordAudio recordTask;

    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;
    static SoundRecordAndAnalysisActivity mainActivity;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.main);
        
        startStopButton = (Button) this.findViewById(R.id.StartStopButton);
        startStopButton.setOnClickListener(this);

        transformer = new RealDoubleFFT(blockSize);

        imageView = (ImageView) this.findViewById(R.id.imageView1);
        bitmap = Bitmap.createBitmap((int)256,(int)100,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        imageView.setImageBitmap(bitmap);
        //imageView.
        mainActivity = this;*/
    }

    private class RecordAudio extends AsyncTask<Void, double[], Void> {
        @Override
        protected Void doInBackground(Void... params) {
        //try {
            int bufferSize = AudioRecord.getMinBufferSize(frequency,
                    channelConfiguration, audioEncoding);
                    AudioRecord audioRecord = new AudioRecord(
                    MediaRecorder.AudioSource.DEFAULT, frequency,
                    channelConfiguration, audioEncoding, bufferSize);

                    short[] buffer = new short[blockSize];
                    double[] toTransform = new double[blockSize];
                    try{
                    	audioRecord.startRecording();
                    }
                    catch(IllegalStateException e){
                    	Log.e("Recording failed", e.toString());
                    	//Toast.makeText(getMainActivity().getApplicationContext(), "Recording Failed", 300).show();
                    }
                    while (started) {
                    int bufferReadResult = audioRecord.read(buffer, 0, blockSize);

                    for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                        toTransform[i] = (double) buffer[i] / 32768.0; // signed 16 bit
                        }

                    transformer.ft(toTransform);
                    publishProgress(toTransform);
                    }
                    try{
                    	audioRecord.stop();
                    }
                    catch(IllegalStateException e){
                    	Log.e("Stop failed", e.toString());
                    	//Toast.makeText(getMainActivity().getApplicationContext(), "Stop Failed", 300).show();
                    }
                    //} catch (Throwable t) {
                   // Log.e("AudioRecord", "Recording Failed");
                    //Toast.makeText(getMainActivity().getApplicationContext(), "Recording Failed", 300).show();
                    //}
                    return null;
                    
                    }
        
        protected void onProgressUpdate(double[]... toTransform) {
        	Log.e("RecordingProgress", "Displaying in progress");
        	//Toast.makeText(getMainActivity().getApplicationContext(), "Recording on progree...", 1000).show();
            canvas.drawColor(Color.BLACK);
            for (int i = 0; i < toTransform[0].length; i++) {
            int x = i;
            int downy = (int) (100 - (toTransform[0][i] * 10));
            int upy = 100;
            canvas.drawLine(x, downy, x, upy, paint);
            }
            imageView.invalidate();
            }
        
        }

   /* protected void onProgressUpdate(double[]... toTransform) {
    	Log.e("RecordingProgress", "Displaying in progress");
        canvas.drawColor(Color.BLACK);
        for (int i = 0; i < toTransform[0].length; i++) {
        int x = i;
        int downy = (int) (100 - (toTransform[0][i] * 10));
        int upy = 100;
        canvas.drawLine(x, downy, x, upy, paint);
        }
        imageView.invalidate();
        }
    */
   /* protected void onPostExecute(){
    	//Toast.makeText(this, "Done...", 1000).show();
    	//Toast.makeText(getMainActivity().getApplicationContext(), "Done...", 1000).show();
    }*/

        public void onClick(View v) {
        if (started) {
        started = false;
        startStopButton.setText("Start");
        recordTask.cancel(true);
        } else {
        started = true;
        startStopButton.setText("Stop");
        recordTask = new RecordAudio();
        recordTask.execute();
        }
     }
        
        static SoundRecordAndAnalysisActivity getMainActivity(){
        	return mainActivity;
        }
        
        public void onStop(){
        	super.onStop();
        	started = false;
            startStopButton.setText("Start");
        	recordTask.cancel(true);
        }
        
        public void onStart(){
        	super.onStart();
        	
        	setContentView(R.layout.main);
            
            startStopButton = (Button) this.findViewById(R.id.StartStopButton);
            startStopButton.setOnClickListener(this);

            transformer = new RealDoubleFFT(blockSize);

            imageView = (ImageView) this.findViewById(R.id.imageView1);
            bitmap = Bitmap.createBitmap((int)256,(int)100,Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            paint = new Paint();
            paint.setColor(Color.GREEN);
            imageView.setImageBitmap(bitmap);
            //imageView.
            mainActivity = this;
        }
}
    
