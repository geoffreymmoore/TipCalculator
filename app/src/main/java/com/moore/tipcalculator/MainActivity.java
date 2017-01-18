package com.moore.tipcalculator;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity {

    //define variables for the widgets
    private EditText billAmountET;
    private TextView percentTV;
    private TextView tipAmountTV;
    private TextView totalTV;
    private Button percentPlusButton;
    private Button percentMinusButton;
    private Button applyButton;
    private Button resetButton;

    // define the SharedPreferences object
    private SharedPreferences savedValues;

    //define instance variable that should be saved
    private String billAmountString= "";
    private float tipPercent = .15f;

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
        applyButton = (Button) findViewById(R.id.applyButton);
        resetButton = (Button) findViewById(R.id.resetButton);

    }
}
