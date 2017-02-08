package com.moore.tipcalculator;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity
        implements OnEditorActionListener, OnCheckedChangeListener {

    //variable for logging purposes
    private static final String TAG = "MainActivity";

    //define variables for the widgets
    private EditText billAmountET;
    private TextView percentTV;
    private TextView tipAmountTV;
    private TextView totalTV;
    private Button resetButton;
    private SeekBar percentSeekBar;
    private RadioGroup roundingRadioGroup;
    private RadioButton roundNoneRadioButton;
    private RadioButton roundTipRadioButton;
    private RadioButton roundTotalRadioButton;
    private Spinner splitSpinner;
    private TextView perPersonLbl;
    private TextView perPersonTV;

    // define the SharedPreferences object
    private SharedPreferences savedValues;
    //define rounding constant
    private final int ROUND_NONE =0;
    private final int ROUND_TIP = 1;
    private final int ROUND_TOTAL = 2;

    //define instance variable that should be saved
    private String billAmountString = "";
    private float tipPercent = 0.15f;
    private int split = 1;
    private int rounding = ROUND_NONE;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get references to widgets
        billAmountET = (EditText) findViewById(R.id.billAmountET);
        percentTV = (TextView) findViewById(R.id.percentTV);
        tipAmountTV = (TextView) findViewById(R.id.tipAmountTV);
        totalTV = (TextView) findViewById(R.id.totalTV);
        resetButton = (Button) findViewById(R.id.resetButton);
        percentSeekBar = (SeekBar) findViewById(R.id.percentSeekBar);
        roundingRadioGroup = (RadioGroup) findViewById(R.id.roundingRadioGroup);
        roundNoneRadioButton = (RadioButton) findViewById(R.id.roundingNoneRadioButton);
        roundTipRadioButton = (RadioButton) findViewById(R.id.roundingTipRadioButton);
        roundTipRadioButton = (RadioButton) findViewById(R.id.roundingTotalRadioButton);
        splitSpinner = (Spinner) findViewById(R.id.splitSinner);
        perPersonLbl = (TextView) findViewById(R.id.perPersonLbl);
        perPersonTV = (TextView) findViewById(R.id.perPersonTV);


        //set array adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.split_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        splitSpinner.setAdapter(adapter);

        //set the listeners

        //Current Class as Listener
        billAmountET.setOnEditorActionListener(this);
        roundingRadioGroup.setOnCheckedChangeListener(this);

        //Named Class as Listener
        KeyListener keyListener = new KeyListener();
        billAmountET.setOnKeyListener(keyListener);

        //Anonymous Class
        percentSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        //Anonymous Inner Class
        splitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                split = position + 1;
                calculateAndDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        resetButton.setOnClickListener(clickListener);

        // get SharedPerferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void calculateAndDisplay() {

        // get Bill Amount
        billAmountString = billAmountET.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        } else {
            billAmount = Float.parseFloat(billAmountString);
        }

        //get tip progress
        int progress = percentSeekBar.getProgress();
        tipPercent = (float) progress/100;

        //perform calculations
        float tipAmount = 0;
        float total = 0;

        if (rounding == ROUND_NONE){
            tipAmount = billAmount * tipPercent;
            total = billAmount + tipAmount;
        } else if (rounding == ROUND_TIP){
            tipAmount = StrictMath.round(billAmount * tipPercent);
            total = billAmount + tipAmount;
        } else if (rounding==ROUND_TOTAL){
            float tipNotRounded = billAmount * tipPercent;
            total = StrictMath.round(billAmount + tipNotRounded);
            tipAmount = total - billAmount;
        }


        Log.d(TAG, "Bill Amount: " + billAmount);


       // float tipAmount = billAmount * tipPercent;
       // float total = billAmount + tipAmount;

        //Display data on the Layout

        float splitAmount = 0;
        if (split==1){ // no split
            perPersonLbl.setVisibility(View.GONE);
            perPersonTV.setVisibility(View.GONE);
        } else{
            splitAmount = total/split;
            perPersonLbl.setVisibility(View.VISIBLE);
            perPersonTV.setVisibility(View.VISIBLE);
        }

        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTV.setText(percent.format(tipPercent));

        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipAmountTV.setText(currency.format(tipAmount));
        totalTV.setText(currency.format(total));
        perPersonTV.setText(currency.format(splitAmount));
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

            calculateAndDisplay();

        }

        //Toast.makeText(getApplicationContext(), "ActionID: " + actionId, Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    protected void onPause() {
        //save the instance variables
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.putInt("rounding",rounding);
        editor.putInt("split",split);
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get the instance variables
        tipPercent = savedValues.getFloat("tipPercent", .15f);

        // set the bill amount on widget
        billAmountET.setText(billAmountString);

        rounding = savedValues.getInt("rounding", ROUND_NONE);
        split = savedValues.getInt("split", 1);

        //set tip percent on its widget
        int progress = Math.round(tipPercent * 100);
        percentSeekBar.setProgress(progress);

        //set rounding on radio button
        // note: this execute the onCheckedChanged
        // which executes the calculateAndDisplay method

        if (rounding == ROUND_NONE){
            roundNoneRadioButton.setChecked(true);
        }else if (rounding == ROUND_TIP){
            roundTipRadioButton.setChecked(true);
        } else if (rounding == ROUND_TOTAL){
            roundTotalRadioButton.setChecked(true);
        }

        //set spilit on spinner
        //note this executes the onitemselected method
        //which executes calc and display
        int position = split -1;
        splitSpinner.setSelection(position);
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            billAmountET.setText("");
            tipPercent = .15f;
            percentSeekBar.setProgress(15);
            roundNoneRadioButton.setChecked(true);
            splitSpinner.setSelection(0);
            calculateAndDisplay();
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId){
            case R.id.roundingNoneRadioButton:
                rounding = ROUND_NONE;
                break;
            case R.id.roundingTipRadioButton:
                rounding = ROUND_TIP;
                break;
            case R.id.roundingTotalRadioButton:
                rounding = ROUND_TOTAL;
                break;
        }
        calculateAndDisplay();
    }

    class KeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            switch (keyCode) {
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    calculateAndDisplay();
                    imm.hideSoftInputFromInputMethod(billAmountET.getWindowToken(), 0);
                    return true; //consume the event
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    calculateAndDisplay();
                    imm.hideSoftInputFromInputMethod(billAmountET.getWindowToken(), 0);
                    break; // don't consume the event
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (v.getId() == R.id.percentSeekBar) {
                        calculateAndDisplay();
                    }
                    break; // don't consume the event
            }

            return false;
        }
    }

    private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            percentTV.setText(progress + "%");
            calculateAndDisplay();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

}
