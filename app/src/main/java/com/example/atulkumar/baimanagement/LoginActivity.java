package com.example.atulkumar.baimanagement;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LOGIN";
    private TextView error;
    private EditText phoneNumber;
    private EditText verifyCode;
    private Button send;
    private Button verify;
    private Button resend;
    private String phoneVerificationId;
    private boolean mVerificationInProgress =false;

    private LinearLayout verificationLayout;
    private LinearLayout signInLayout;
    private LinearLayout buttonLayout;

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumber = (EditText)findViewById(R.id.phone);
        verifyCode = (EditText)findViewById(R.id.verify_code);
        send = (Button)findViewById(R.id.send_code);
        resend = (Button)findViewById(R.id.resend_code);
        verify = (Button)findViewById(R.id.verify);
        error = (TextView)findViewById(R.id.error);

        verificationLayout = (LinearLayout) findViewById(R.id.verify_code_layout);
        signInLayout = (LinearLayout)findViewById(R.id.signin_layout);
        buttonLayout = (LinearLayout)findViewById(R.id.send_button_layout);
        send.setEnabled(true);
        resend.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = phoneNumber.getText().toString();


                if(phone ==null){
                    error.setText("please enter phone number");
                    error.setTextColor(getResources().getColor(R.color.errorColor));
                }else if(phone!=null && (phone.length() < 10 ) ){
                    error.setText("Wrong phone number!");
                    error.setTextColor(getResources().getColor(R.color.errorColor));
                }else{
                    sendCode(view,phone);
                    error.setVisibility(View.GONE);
                }

            }
        });



    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Toast.makeText(LoginActivity.this,""+ currentUser,Toast.LENGTH_SHORT).show();
        //    updateUI(currentUser);
    }



    public void sendCode(View view, String phone){
        //  String phone = phoneNumber.getText().toString();

        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                LoginActivity.this,
                verificationStateChangedCallbacks);


    }

    private void setUpVerificationCallbacks(){

        verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                mVerificationInProgress = false;
                Toast.makeText(LoginActivity.this,"verification complete",Toast.LENGTH_SHORT).show();
                signInLayout.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.GONE);
//                error.setVisibility(View.VISIBLE);
//                error.setText("Signed In...");
                      signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this,"verification Failed"+ e,Toast.LENGTH_SHORT).show();
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    Log.e(TAG,"Invalid Credentials");
                }else if(e instanceof FirebaseTooManyRequestsException){
                    Log.e(TAG,"SMS quota exceeded");
                }

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token){
                phoneVerificationId = verificationId;
                resendingToken = token;

                verificationLayout.setVisibility(View.VISIBLE);

                verify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        verifyCode(view);
                    }
                });
            }
        };
    }

    public void verifyCode(View view){
        String code= verifyCode.getText().toString(); //code verifiaction

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"successfully signed In",Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            if(user != null){
                                Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(mainIntent);
                            }
                            // ...
                        } else {

                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this,"invalid credential",Toast.LENGTH_SHORT).show();
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    public void resendCode(View view){
        String phone = phoneNumber.getText().toString();

        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                java.util.concurrent.TimeUnit.SECONDS,
                LoginActivity.this,
                verificationStateChangedCallbacks);


    }

//    public void signOut(){
//        mAuth.signOut();
//    }



}
