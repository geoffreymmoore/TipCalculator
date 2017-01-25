package com.moore.tipcalculator;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.security.PrivateKey;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity
implements OnEditorActionListener{

    //variable for logging purposes
    private static final String TAG = "MainActivity";

    //define variables for the widgets
    private EditText billAmountET;
    private TextView percentTV;
    private TextView tipAmountTV;
    private TextView totalTV;
    private Button percentPlusButton;
    private Button percentMinusButton;
    private Button resetButton;

    // define the SharedPreferences object
    private SharedPreferences savedValues;

    //define instance variable that should be saved
    private String billAmountString= "";
    private float tipPercent = 0.15f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get references to widgets
        billAmountET = (EditText) findViewById(R.id.billAmountET);
        percentTV = (TextView) findViewById(R.id.percentTV);
        tipAmountTV = (TextView) findViewById(R.id.tipAmountTV);
        totalTV = (TextView) findViewById(R.id.totalTV);
        percentPlusButton = (Button) findViewById(R.id.plusButton);
        percentMinusButton = (Button) findViewById(R.id.minusButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        //set the listeners
        //Current Class as Listener
        billAmountET.setOnEditorActionListener(this);

        //Named Class as Listener
        KeyListener keyListener = new KeyListener();
        billAmountET.setOnKeyListener(keyListener);

        //Anonymous Inner Class
        percentPlusButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.plusButton:
                        tipPercent = tipPercent + 0.01f;
                        calculateAndDisplay();
                        break;
                    case R.id.minusButton:
                        tipPercent = tipPercent - 0.01f;
                        calculateAndDisplay();
                        break;
                    case R.id.resetButton:
                        billAmountET.setText("");
                        tipPercent = .15f;
                        calculateAndDisplay();
                        break;
                }
            }
        });

        //Anonymous Class
        percentMinusButton.setOnClickListener(clickListener);
        resetButton.setOnClickListener(clickListener);

        // get SharedPerferences object
        savedValues = getSharedPreferences("SavedValues",MODE_PRIVATE);

    }

    public void calculateAndDisplay(){

        // get Bill Amount
        billAmountString = billAmountET.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        } else{
            billAmount = Float.parseFloat(billAmountString);
        }

        Log.d(TAG, "Bill Amount: " + billAmount);


        float tipAmount = billAmount * tipPercent;
        float total = billAmount + tipAmount;

        //Display data on the Layout

        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTV.setText(percent.format(tipPercent));

        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipAmountTV.setText(currency.format(tipAmount));
        totalTV.setText(currency.format(total));
    }



    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED){

            calculateAndDisplay();

        }

        Toast.makeText(getApplicationContext(),"ActionID: " + actionId,Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    protected void onPause() {
        //save the instance variables
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("billAmountString",billAmountString);
        editor.putFloat("tipPercent",tipPercent);
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get the instance variables
        tipPercent = savedValues.getFloat("tipPercent",.15f);

        // set the bill amount on widget
        billAmountET.setText(billAmountString);


        // calculate and display
        calculateAndDisplay();
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.plusButton:
                    tipPercent = tipPercent + 0.01f;
                    calculateAndDisplay();
                    break;
                case R.id.minusButton:
                    tipPercent = tipPercent - 0.01f;
                    calculateAndDisplay();
                    break;
                case R.id.resetButton:
                    billAmountET.setText("");
                    tipPercent = .15f;
                    calculateAndDisplay();
                    break;
            }
        }
    };

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
                    if (v.getId() == R.id.plusButton) {
                        calculateAndDisplay();
                    }
                    break; // don't consume the event
            }

            return false;
        }
    }
}
