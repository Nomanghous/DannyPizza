package com.example.dannypizza;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int SIGN_IN_CODE = 0;
    private static String TAG = "HASH";
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        checkUserAuth();
//        printHashKey();


    }

    private void checkUserAuth() {
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            // user already sign in
            Log.d("MainActivity",auth.getCurrentUser().getEmail());
        }else {

            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
                    ))
                    .setIsSmartLockEnabled(false)
                    .build(), SIGN_IN_CODE);

        }
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplication().getPackageName(),PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }
    public void logout(View v){
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                Toast.makeText(MainActivity.this,"You are Logged out!",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SIGN_IN_CODE){
            IdpResponse resp = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                // user logged in
                Log.d("MainActivity",auth.getCurrentUser().getEmail());
            }else{
                Log.d("MainActivity","Error Not Auth");
                if (resp == null) {
                    return;
                }

                if (resp.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    return;
                }

                if (resp.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    return;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // checkUserAuth();
    }
}
