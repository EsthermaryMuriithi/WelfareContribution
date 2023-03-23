package com.example.welfarecontribution;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendSMS extends AppCompatActivity {
    EditText etPhone,etMessage;
    Button btSend;
    private static final int REQUEST_SEND_SMS = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        etPhone = findViewById(R.id.edtPhone);
        etMessage = findViewById(R.id.edMessage);
        btSend = findViewById(R.id.btnSend);

        btSend.setOnClickListener(v -> {
            // Request send sms permissions from user
            if(ContextCompat.checkSelfPermission(SendSMS.this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                //WHEN permission is granted
                //create method
                sendMessage();
            }else{
                //when permission is not granted
                //request permission
                ActivityCompat.requestPermissions(SendSMS.this
                        ,new String[]{Manifest.permission.SEND_SMS}
                        ,REQUEST_SEND_SMS);
            }

        });

    }
    private void sendMessage() {
        //Get Values from edit Text
        String sPhone = etPhone.getText().toString().trim();
        String sMessage = etMessage.getText().toString().trim();
        if(sPhone.length()<10 || sPhone.length()>10) {
            etPhone.setError("Enter Valid Phone Number");
            etPhone.requestFocus();
            return;
        }
        if (sMessage.equals("")){
            etMessage.setError("Please enter message");
            etMessage.requestFocus();
            return;
        }

        //check condition
        if(!sPhone.equals("") && !sMessage.equals("")) {
            //when both edit text value not equal to blank
            SmsManager smsManager = SmsManager.getDefault();
            //send text message
            smsManager.sendTextMessage(sPhone, null, sMessage, null, null);
            //Display toast
            Toast.makeText(getApplicationContext()
                    , "SMS sent Successfully", Toast.LENGTH_LONG).show();
            clearInputs();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//check condition
        if(requestCode == REQUEST_SEND_SMS &&  grantResults.length > 0 && grantResults[0]
                == PackageManager.PERMISSION_GRANTED){
            //when Permission is granted
            //call method
            sendMessage();
        }else{
            //when permission is denied
            //display toast
            Toast.makeText(getApplicationContext()
                    ,"Permission Denied!",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Clear what the user had entered in the messages
     */
    private void clearInputs(){
        etPhone.setText("");
        etMessage.setText("");
    }

}