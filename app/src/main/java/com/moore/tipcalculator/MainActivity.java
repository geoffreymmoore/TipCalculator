package com.moore.tipcalculator;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivateKey;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity
implements TextView.OnEditorActionListener, View.OnClickListener{

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
        billAmountET.setOnEditorActionListener(this);
        percentPlusButton.setOnClickListener(this);
        percentMinusButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()){
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


}
