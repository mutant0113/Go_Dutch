package com.mutant.godutch

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.mutant.godutch.widget.EmailEditText
import com.mutant.godutch.widget.PasswordEditText
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*


class LoginActivity : AppCompatActivity() {

    companion object {
        val TAG = LoginActivity::class.java.simpleName!!
    }

    private var mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mCallbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupGoogleSignIn()
        setupFaceBookSignIn()
        autoLoginIfGotAuth()
        if (DebugHelper.bUseCrashlytics) {
            Fabric.with(this.applicationContext, Crashlytics())
        }
    }

    private fun setupGoogleSignIn() {
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        // Build a GoogleApiClient with access to the Google Sign-In API and the options specified by gso.
        mGoogleApiClient = GoogleApiClient.Builder(this).enableAutoManage(this, {}).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()
        sign_in_button_google.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun setupFaceBookSignIn() {
        sign_in_button_facebook.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"))
        sign_in_button_facebook.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                firebaseAuthWithFacebook(loginResult.accessToken)
                Toast.makeText(this@LoginActivity, "fb onSuccess", Toast.LENGTH_LONG)
            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity, "fb onCancel", Toast.LENGTH_LONG)
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(this@LoginActivity, "fb onError", Toast.LENGTH_LONG)
            }
        })
    }

    private fun onLoginComplete(task: Task<*>) {
        if (task.isSuccessful) {
            Toast.makeText(this@LoginActivity, "user login succeed", Toast.LENGTH_SHORT).show()
            val refreshedToken = FirebaseInstanceId.getInstance().token
            if (refreshedToken != null) {
                MyInstanceIDListenerService.sendRegistrationToServer(refreshedToken)
                loginSuccessfully()
            } else {
                // TODO
            }
        } else {
            // TODO register
//            register(email, password)
        }
    }

    private fun loginSuccessfully() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Toast.makeText(this@LoginActivity, "user logged in", Toast.LENGTH_SHORT).show()
            saveUserDataToDatabase(user)
            intentToMainActivity()
        } else {
            Toast.makeText(this@LoginActivity, "user logged out", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDataToDatabase(user: FirebaseUser) {
        val database = FirebaseDatabase.getInstance().reference.child("users")
        database.child(user.uid).child("uid").setValue(user.uid)
        database.child(user.uid).child("name").setValue(user.displayName)
        database.child(user.uid).child("email").setValue(user.email)
        database.child(user.uid).child("photoUrl").setValue(user.photoUrl.toString())
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
            mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnFailureListener { e -> e.printStackTrace() }
                    .addOnCompleteListener {
                        // TODO com.google.database.FirebaseNetworkException
                        // TODO password failed
                        onLoginComplete(it)
                    }
        }
    }

    val RC_SIGN_IN = 1

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(this.javaClass.simpleName, "handleSignInResult:" + result.isSuccess)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(result.signInAccount)
        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "sign in from google failed", Toast.LENGTH_LONG)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        Log.d(LoginActivity.TAG, "firebaseAuthWithGoogle:" + account?.id)

        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { onLoginComplete(it) }
    }

    private fun firebaseAuthWithFacebook(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + token)

        val credential: AuthCredential = FacebookAuthProvider.getCredential(token.token)
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { onLoginComplete(it) }

    }

    private fun register(email: String, password: String) {
        AlertDialog.Builder(this@LoginActivity).setTitle("登入問題").setMessage("無此帳號，是否要以此帳號與密碼註冊?")
                .setPositiveButton("註冊") { dialog, which -> createUser(email, password) }.setNeutralButton("取消", null).show()
    }

    private fun createUser(email: String, password: String) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            // TODO com.google.database.FirebaseNetworkException
            val message = if (task.isSuccessful) "註冊成功" else "註冊失敗"
            AlertDialog.Builder(this@LoginActivity).setMessage(message)
                    .setPositiveButton("OK", null).show()
        }
    }

}