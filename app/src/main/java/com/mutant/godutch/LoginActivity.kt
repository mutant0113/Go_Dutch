package com.mutant.godutch

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.mutant.godutch.widget.EmailEditText
import com.mutant.godutch.widget.PasswordEditText
import io.fabric.sdk.android.Fabric

class LoginActivity : AppCompatActivity() {

    companion object {
        val TAG = LoginActivity::class.java.simpleName
    }

    internal var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    internal var mFirebaseAuthStateListener: FirebaseAuth.AuthStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupFireBase()
        autoLoginIfGotAuth()
        if (DebugHelper.bUseCrashlytics) {
            Fabric.with(this.applicationContext, Crashlytics())
        }
    }

    private fun setupFireBase() {
        mFirebaseAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Toast.makeText(this@LoginActivity, "user logged in", Toast.LENGTH_SHORT).show()
                saveUserDataToDatabase(user)
                intentToMainActivity()
            } else {
                Toast.makeText(this@LoginActivity, "user logged out", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserDataToDatabase(user: FirebaseUser) {
        val database = FirebaseDatabase.getInstance().reference.child("users")
        database.child(user.uid).child("uid").setValue(user.uid)
        database.child(user.uid).child("name").setValue(user.displayName)
        database.child(user.uid).child("email").setValue(user.email)
        database.child(user.uid).child("photo_url").setValue(user.photoUrl)
    }

    private fun autoLoginIfGotAuth() {
        if (mFirebaseAuth.currentUser != null) {
            intentToMainActivity()
        }
    }

    private fun intentToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun login(view: View) {
        val editTextEmail = findViewById(R.id.editText_email) as EmailEditText
        val editTextPassword = findViewById(R.id.editText_password) as PasswordEditText
        if (editTextEmail.isEmpty) {
            editTextEmail.setError(getString(R.string.error_empty))
        } else if (editTextPassword.isEmpty) {
            editTextPassword.setError(getString(R.string.error_empty))
        } else if (!editTextEmail.isValid) {
            editTextEmail.setError(getString(R.string.error_invalid_email))
        } else if (!editTextPassword.isValid) {
            editTextPassword.setError(getString(R.string.error_incorrect_password))
        } else {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener { e -> e.printStackTrace() }.addOnCompleteListener { task ->
                // TODO com.google.firebase.FirebaseNetworkException
                // TODO password failed
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "user login succeed", Toast.LENGTH_SHORT).show()
                    val refreshedToken = FirebaseInstanceId.getInstance().token
                    if (refreshedToken != null) {
                        MyInstanceIDListenerService.sendRegistrationToServer(refreshedToken)
                        intentToMainActivity()
                    } else {
                        // TODO
                    }
                } else {
                    register(email, password)
                }
            }
        }
    }

    private fun register(email: String, password: String) {
        AlertDialog.Builder(this@LoginActivity).setTitle("登入問題").setMessage("無此帳號，是否要以此帳號與密碼註冊?")
                .setPositiveButton("註冊") { dialog, which -> createUser(email, password) }.setNeutralButton("取消", null).show()
    }

    private fun createUser(email: String, password: String) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            // TODO com.google.firebase.FirebaseNetworkException
            val message = if (task.isSuccessful) "註冊成功" else "註冊失敗"
            AlertDialog.Builder(this@LoginActivity).setMessage(message)
                    .setPositiveButton("OK", null).show()
        }
    }

    override fun onStart() {
        super.onStart()
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthStateListener!!)
    }

}