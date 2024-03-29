package com.cmsc436.ms_diagnostic.tap_test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cmsc436.ms_diagnostic.R;
import com.cmsc436.ms_diagnostic.Results;

/*
* This is the Activity for the Tapping Test
* */
public class TapActivity extends Activity {

    int count = 0;
    int rightHand = 0;
    int leftHand = 0;

    TextView instructions;
    TextView hand;
    CountDownTimer recordTimer;
    Boolean isDone = false;
    Button tap_button ;
    Button start_button;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tap);


        tap_button = (Button) findViewById(R.id.button_tap);
        start_button = (Button) findViewById(R.id.start_button);
        instructions = (TextView) findViewById(R.id.text_instructions);
        hand = (TextView) findViewById(R.id.tap_hand_text);

        recordTimer = new CountDownTimer(10000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                instructions.setText("Timer: "+(int) millisUntilFinished/1000+" sec");
            }

            @Override
            public void onFinish() {
                instructions.setText("Done!" + " Count: " + count);
                start_button.setVisibility(View.VISIBLE);
                if(isDone){
                    //left hand updated
                    leftHand = count;
                    // when both hands are done, the intent in initialized
                    // both hand data is set in putExtra
                    intent = new Intent(TapActivity.this, Results.class);
                    intent.putExtra(getString(R.string.LEFT),""+leftHand);
                    intent.putExtra(getString(R.string.RIGHT),""+rightHand);

                    // The start button's text is changed to "Done"
                    //new listener is added to send to the Result Activity
                    start_button.setText(R.string.tap_activity_done);
                    start_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(intent);
                        }
                    });

                }else{
                    // now the next counter will go in the first if statment
                    isDone = true;
                    // right hand recorded
                    rightHand = count;
                    //count reset for next hand
                    count = 0;
                    // Set hand Instruction to Right
                    hand.setText("HAND: Left");
                }
            }

        };

        start_button.setOnClickListener(new View.OnClickListener(){
//
            @Override
            public void onClick(View v){
                recordTimer.start();
                count = 0;
                start_button.setVisibility(View.INVISIBLE);
                tap_button.setOnTouchListener(new View.OnTouchListener() {
                    int count_taps = 0;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                // PRESSED
                                count_taps++;
                                count = count_taps;
                                tap_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_to_tap_imt_white));
                                return true; // if you want to handle the touch event
                            case MotionEvent.ACTION_UP:
                                // RELEASED
                                tap_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_to_tap_img_black));
                                return true; // if you want to handle the touch event
                        }
                        return false;
                    }
                });
            }






        });
    }


}
