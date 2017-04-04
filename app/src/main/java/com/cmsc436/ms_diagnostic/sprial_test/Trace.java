package com.cmsc436.ms_diagnostic.sprial_test;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cmsc436.ms_diagnostic.R;
import com.cmsc436.ms_diagnostic.Results;

import java.util.ArrayList;

public class Trace extends AppCompatActivity {

    private Chronometer timer;
    private Button start_button;
    private Button stop_button;

    private DrawView draw_event;

    public static final String DATA_LIST = "DATALIST";
    public static final String DATA = "DATA";

    public static final String STATE_LH = "Left_Hand_Sec";
    public static final String STATE_RH = "Right_Hand_Sec";

//    private long left_h_time = 0;
//    private long right_h_time = 0;

    private long totalTime = 0;
    private int testCount = 0;

    ArrayList<Object> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Transition of screen
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_trace);
        if (ContextCompat.checkSelfPermission(Trace.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Trace.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Trace.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }
        timer = (Chronometer) findViewById(R.id.timer_object);

        start_button = (Button) findViewById(R.id.start_button );
        stop_button = (Button) findViewById(R.id.stop_button);
        draw_event = (DrawView) findViewById(R.id.trace_draw_view);


        start_button.setEnabled(true);
        stop_button.setEnabled(false);

        draw_event.setVisibility(View.GONE);

        data = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        testCount = 0;
        super.onStart();
    }

    public void startTimer(View view){
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        testCount++;
        start_button.setEnabled(false);
        stop_button.setEnabled(true);
        draw_event.setVisibility(View.VISIBLE);
        
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopTimer(View view){
        timer.stop();
        start_button.setEnabled(true);
        stop_button.setEnabled(false);
        long elapsedTime = (SystemClock.elapsedRealtime() - timer.getBase() )/1000;

        //~~ updating the time to data
        data.add(elapsedTime);
        totalTime+=elapsedTime;
        final FrameLayout relativeLayout = (FrameLayout) findViewById(R.id.trace_frame_layout);
//        final RelativeLayout relativeLayout = (RelativeLayout) getWindow().getDecorView().getRootView();
        relativeLayout.setDrawingCacheEnabled(true);
        relativeLayout.buildDrawingCache();
        Bitmap backLayer = relativeLayout.getDrawingCache();

        Bitmap finalBM = finalBitmap(backLayer);

        String title = (testCount == 1) ? "left_spiral":"right_spiral";
        MediaStore.Images.Media.insertImage(getContentResolver(), finalBM, title , "");


        draw_event.setVisibility(View.GONE);
        draw_event.clearDrawing();
        Toast.makeText(this, "Ellapsed Time: " + elapsedTime +" seconds.", Toast.LENGTH_LONG).show();

//        if(testCount == 1){
//            left_h_time = elapsedTime;
//        }else{
        if(testCount == 3){

            Intent intent = new Intent();
            intent.putExtra(DATA,totalTime);
            intent.putExtra(DATA_LIST,data);
            setResult(RESULT_OK,intent);
            finish();

//            right_h_time = elapsedTime;
            // Exit transition
//            getWindow().setExitTransition(new Fade());
//            Intent myIntent = new Intent();
//            myIntent.putExtra(getString(R.string.LEFT),""+left_h_time);
//            myIntent.putExtra(getString(R.string.RIGHT),""+right_h_time);
//            startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            //startActivity(myIntent);
        }




    }

    private Bitmap finalBitmap(Bitmap b1){
        Bitmap overlay = Bitmap.createBitmap(b1.getWidth(),b1.getHeight(), b1.getConfig());
        Canvas canvas = new Canvas(overlay);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(b1,0,0,null);

        return overlay;
    }

}