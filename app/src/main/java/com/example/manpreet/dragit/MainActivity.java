package com.example.manpreet.dragit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity implements View.OnDragListener, View.OnTouchListener {
    private GridLayout viewLayout;
    private Button redButton, greenButton, yellowButton, blueButton;
    private String redStringText, blueStringText, greenStringText, yellowStringText;
    private String redStringButton, blueStringButton, greenStringButton, yellowStringButton;
    private TextView text1, text2, text3, text4, timer, chooser;
    private boolean startGame = false;
    private CountDownTimer countDownTimer;
    private ArrayList<String> textTags = new ArrayList<>();
    private ArrayList<String> buttonTags = new ArrayList<>();
    private Thread tagsThread;
    private int correctAttempts = 0;
    private int wrongAttempts = 0;
    private String dragText;
    private boolean isBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redButton = (Button) findViewById(R.id.redButton);
        blueButton = (Button) findViewById(R.id.blueButton);
        greenButton = (Button) findViewById(R.id.greenButton);
        yellowButton = (Button) findViewById(R.id.yellowButton);

        chooser = (TextView) findViewById(R.id.chooser);
        timer = (TextView) findViewById(R.id.timer);

        viewLayout = (GridLayout) findViewById(R.id.gridLayout);


        textTags.add("Red");
        textTags.add("Blue");
        textTags.add("Green");
        textTags.add("Yellow");

        buttonTags.add("Red");
        buttonTags.add("Blue");
        buttonTags.add("Green");
        buttonTags.add("Yellow");

        // textColors.put("Red", (long)0xff5dff68); // blue
        //textColors.put("Blue", (long)0xfff9ff83); // yellow
        //textColors.put("Green", (long)0xffff5a51); //red
        //textColors.put("Yellow",(long) 0xff5dff68); //green


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;


        viewLayout.setPadding((((width / 4) / 2) - 10), 50, 0, 0);

        viewLayout.setUseDefaultMargins(true);
        viewLayout.setColumnOrderPreserved(false);
        viewLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);

        text1 = new TextView(getApplication());
        text1.setLeft(10);
        text1.setText("Red");
        text1.setWidth((width - 120) / 3);
        text1.setHeight((width - 120) / 3);
        text1.setTextSize(18);
        text1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        text1.setBackgroundColor(getResources().getColor(R.color.realRed));
        text1.setTypeface(Typeface.DEFAULT_BOLD);
        text1.setTextColor(Color.BLACK);

        viewLayout.addView(text1);


        text2 = new TextView(getApplication());
        text2.setText("Blue");
        text2.setWidth((width - 120) / 3);
        text2.setHeight((width - 120) / 3);
        text2.setTextSize(18);
        text2.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        text2.setBackgroundColor(getResources().getColor(R.color.realBlue));
        text2.setTypeface(Typeface.DEFAULT_BOLD);
        text2.setTextColor(Color.BLACK);
        viewLayout.addView(text2);

        text3 = new TextView(getApplication());
        text3.setText("Green");
        text3.setWidth((width - 120) / 3);
        text3.setHeight((width - 120) / 3);
        text3.setTextSize(18);
        text3.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        text3.setBackgroundColor(getResources().getColor(R.color.realGreen));
        text3.setTypeface(Typeface.DEFAULT_BOLD);
        text3.setTextColor(Color.BLACK);
        viewLayout.addView(text3);

        text4 = new TextView(getApplication());
        text4.setText("Yellow");
        text4.setWidth((width - 120) / 3);
        text4.setHeight((width - 120) / 3);
        text4.setTextSize(18);
        text4.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        text4.setBackgroundColor(getResources().getColor(R.color.realYellow));
        text4.setTypeface(Typeface.DEFAULT_BOLD);
        text4.setTextColor(Color.BLACK);
        viewLayout.addView(text4);

        redButton.setOnDragListener(this);
        blueButton.setOnDragListener(this);
        greenButton.setOnDragListener(this);
        yellowButton.setOnDragListener(this);


        text1.setOnTouchListener(this);
        text2.setOnTouchListener(this);
        text3.setOnTouchListener(this);
        text4.setOnTouchListener(this);


        countDownTimer = new CountDownTimer(30000, 5) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimer(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                startGame = false;
                if (!isBackPressed) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Well Done !")
                            .setMessage("You Score is : " + correctAttempts)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
                timer.setText(" Stop!   Stop! ");
            }
        };

        text1.setTag("Red");
        text2.setTag("Blue");
        text3.setTag("Green");
        text4.setTag("Yellow");

        redButton.setTag("Red");
        blueButton.setTag("Blue");
        greenButton.setTag("Green");
        yellowButton.setTag("Yellow");

        countDownTimer.start();
        startGame = true;

        tagsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startGame) {
                    try {
                        Thread.currentThread().sleep(6000);
                        if (startGame)
                            setViewTags();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        tagsThread.start();
        handler.sendEmptyMessage(5);
    }

    public void setViewTags() {
        Collections.shuffle(textTags);
        Collections.shuffle(textTags);
        Collections.shuffle(textTags);

        redStringText = textTags.get(0);
        blueStringText = textTags.get(1);
        greenStringText = textTags.get(2);
        yellowStringText = textTags.get(3);

        Collections.shuffle(buttonTags);
        Collections.shuffle(buttonTags);
        Collections.shuffle(buttonTags);

        redStringButton = buttonTags.get(0);
        blueStringButton = buttonTags.get(1);
        greenStringButton = buttonTags.get(2);
        yellowStringButton = buttonTags.get(3);

        Collections.shuffle(textTags);
        Collections.shuffle(textTags);
        Collections.shuffle(textTags);

        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            if (Integer.parseInt(getMessageName(msg).charAt(2) + "") == 0) {
                text1.setText(redStringText);
                text1.setTag(redStringText);
                setTextColor(text1, redStringText);


                text2.setText(blueStringText);
                text2.setTag(blueStringText);
                setTextColor(text2, blueStringText);

                text3.setText(greenStringText);
                text3.setTag(greenStringText);
                setTextColor(text3, greenStringText);

                text4.setText(yellowStringText);
                text4.setTag(yellowStringText);
                setTextColor(text4, yellowStringText);

                Collections.shuffle(textTags);
                Collections.shuffle(textTags);
                Collections.shuffle(textTags);
                Collections.shuffle(textTags);

                redButton.setText(redStringButton);
                redButton.setTag(redStringButton);
                setColor(redButton, redStringButton);

                blueButton.setText(blueStringButton);
                blueButton.setTag(blueStringButton);
                setColor(blueButton, blueStringButton);

                greenButton.setText(greenStringButton);
                greenButton.setTag(greenStringButton);
                setColor(greenButton, greenStringButton);

                yellowButton.setText(yellowStringButton);
                yellowButton.setTag(yellowStringButton);
                setColor(yellowButton, yellowStringButton);
            }

            if (Integer.parseInt(getMessageName(msg).charAt(2) + "") == 5) {
                Collections.shuffle(textTags);
                dragText = textTags.get(0);
                chooser.setText("Drag --> " + dragText);
            }
        }

    };

    void setTextColor(TextView view, String color) {
        switch (color) {
            case "Red":
                view.setBackgroundColor(getResources().getColor(R.color.Red));
                break;
            case "Blue":
                view.setBackgroundColor(getResources().getColor(R.color.Blue));
                break;
            case "Green":
                view.setBackgroundColor(getResources().getColor(R.color.Green));
                break;
            case "Yellow":
                view.setBackgroundColor(getResources().getColor(R.color.Yellow));
                break;
        }
    }

    void setColor(Button view, String color) {
        switch (color) {
            case "Red":
                view.setBackgroundColor(getResources().getColor(R.color.realRed));
                break;
            case "Blue":
                view.setBackgroundColor(getResources().getColor(R.color.realBlue));
                break;
            case "Green":
                view.setBackgroundColor(getResources().getColor(R.color.realGreen));
                break;
            case "Yellow":
                view.setBackgroundColor(getResources().getColor(R.color.realYellow));
                break;
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) // Calculating and verifying results
    {

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        int action = event.getAction();
        String viewTag = v.getTag().toString(), dropTag;

        switch (action) {
            case DragEvent.ACTION_DRAG_ENTERED:
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                return true;
            case DragEvent.ACTION_DROP:

                ClipData clipData = event.getClipData();
                dropTag = clipData.getItemAt(0).getText().toString();

                if (dropTag.equals(viewTag) && dropTag.equals(dragText)) {
                    correctAttempts++;
                    handler.sendEmptyMessage(5);
                } else {
                    wrongAttempts++;
                    vibrator.vibrate(500);
                    handler.sendEmptyMessage(5);
                }
                break;
        }

        return true;
    }

    public void updateTimer(long s) {
        String seconds = (s / 1000) + "";
        timer.setText(seconds + " : " + (s % 10));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackPressed = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

        ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(v);

        v.startDrag(dragData, myShadow, null, 0);
        return true;
    }
}

