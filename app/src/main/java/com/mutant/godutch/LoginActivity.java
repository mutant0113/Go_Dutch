package com.mutant.godutch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mutant.godutch.widget.EmailEditText;
import com.mutant.godutch.widget.PasswordEditText;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mFirebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initFireBase();
        autoLoginIfGotAuth();
        Fabric.with(this.getApplicationContext(), new Crashlytics());
    }

    private void initFireBase() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(LoginActivity.this, "user logged in", Toast.LENGTH_SHORT).show();
                    intentToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "user logged out", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void autoLoginIfGotAuth() {
        if (mFirebaseAuth != null) {
            if (mFirebaseAuth.getCurrentUser() != null) {
                intentToMainActivity();
            }
        }
    }

    private void intentToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void login(View view) {
        final EmailEditText editTextEmail = ((EmailEditText) findViewById(R.id.editText_email));
        final PasswordEditText editTextPassword = ((PasswordEditText) findViewById(R.id.editText_password));
        if (editTextEmail.isEmpty()) {
            editTextEmail.setError(getString(R.string.error_empty));
        } else if (editTextPassword.isEmpty()) {
            editTextPassword.setError(getString(R.string.error_empty));
        } else if (!editTextEmail.isValid()) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
        } else if (!editTextPassword.isValid()) {
            editTextPassword.setError(getString(R.string.error_incorrect_password));
        } else {
            final String email = editTextEmail.getText().toString();
            final String password = editTextPassword.getText().toString();
            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // TODO com.google.firebase.FirebaseNetworkException
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "user login succeed", Toast.LENGTH_SHORT).show();
                        intentToMainActivity();
                    } else {
                        register(email, password);
                    }
                }
            });
        }
    }

    private void register(final String email, final String password) {
        new AlertDialog.Builder(LoginActivity.this).setTitle("登入問題").setMessage("無此帳號，是否要以此帳號與密碼註冊?")
                .setPositiveButton("註冊", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createUser(email, password);
                    }
                }).setNeutralButton("取消", null).show();
    }

    private void createUser(String email, String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // TODO com.google.firebase.FirebaseNetworkException
                        String message = task.isSuccessful() ? "註冊成功" : "註冊失敗";
                        new AlertDialog.Builder(LoginActivity.this).setMessage(message)
                                .setPositiveButton("OK", null).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthStateListener);
    }
}