package com.example.caboconsultas.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Constraints.TAG
import com.example.caboconsultas.R
import com.example.caboconsultas.io.ApiService
import com.example.caboconsultas.io.response.LoginResponse
import com.example.caboconsultas.ui.Modal.users
import com.example.caboconsultas.util.PreferenceHelper
import com.example.caboconsultas.util.PreferenceHelper.get
import com.example.caboconsultas.util.PreferenceHelper.set
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
enum class ProviderType{
    GOOGLE,
    FACEBOOK
}
class MainActivity : AppCompatActivity() {
    private val apiService:ApiService by lazy {
        ApiService.create()
    }
    private val snackBar by lazy {
        Snackbar.make(mainLayout, R.string.presiona_back_denuevo,Snackbar.LENGTH_SHORT)
    }
    private val GOOGLE_SIGN_IN=100
    private lateinit var auth:FirebaseAuth
    private lateinit var user:FirebaseUser
    private lateinit var googleSignInClient: GoogleSignInClient
    private val callbackManager= CallbackManager.Factory.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(200)
        setTheme(R.style.Theme_Caboconsultas)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Google auth
        auth= FirebaseAuth.getInstance()


        btnFacebookSignIn.setReadPermissions("email")
        btnFacebookSignIn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })











        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient=GoogleSignIn.getClient(this,gso)

        findViewById<Button>(R.id.btnGoogleSignIn).setOnClickListener {
            signInGoogle()
        }

//      Facebook auth
//        btnFacebookSignIn.setOnClickListener {
//            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
//            LoginManager.getInstance().registerCallback(callbackManager,
//                object: FacebookCallback<LoginResult>{
//                    override fun onSuccess(result: LoginResult?) {
//                        result?.let {
//                            val facebookToken = it.accessToken
//                            val credential = FacebookAuthProvider.getCredential(facebookToken.token)
//                            FirebaseAuth.getInstance().signInWithCredential(credential)
//                                .addOnCompleteListener {
//                                    if (it.isSuccessful) {
//                                        val googleEmail = it.result?.user?.email ?: ""
//                                        val providerType = ProviderType.FACEBOOK
//                                        toast(googleEmail)
//                                        toast(providerType.toString())
//                                    }
//                                }
//                        }
//                    }
//                    override fun onCancel() {
//                        toast("Se cancelo.")
//                    }
//
//                    override fun onError(error: FacebookException?) {
//                        toast("Se produjo un error.")
//                    }
//
//                })
//        }

//        val preferences= getSharedPreferences("general", Context.MODE_PRIVATE)
//        val session= preferences.getBoolean("session",false)
        val preferences= PreferenceHelper.defaultPrefs(this)

        if (preferences["token", ""].contains("|"))
            goToMenuActivity()

        btnLogin.setOnClickListener {
            performLogin()
        }
        goToRegister.setOnClickListener{
            Toast.makeText(this,getString(R.string.toast_completa_tus_datos),Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    val users1: users  =  users()
                    users1.setUserName(user?.displayName)
                    val intent=Intent(this, MainActivity2::class.java)
                    intent.putExtra("name",user?.displayName.toString())
                    startActivity(intent)
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }
    private fun signInGoogle() {
        val signInIntent= googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result -> if(result.resultCode== Activity.RESULT_OK){
            val task= GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account : GoogleSignInAccount?=task.result
            if(account!=null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        val credential=GoogleAuthProvider.getCredential(account?.idToken,null)
        performRegister(account?.displayName, account?.email, account?.id)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){

                val intent=Intent(this, MenuActivity::class.java)
                intent.putExtra("email",account?.email)
                intent.putExtra("name",account?.displayName)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performLogin() {
        val email=etEmail.text.toString()
        val password= etPassword.text.toString()
        val call=apiService.postLogin(email,password)
        if(email.trim().isEmpty() || password.trim().isEmpty()){
            toast("Porfavor ingrese un correo y contraseña")
            return
        }
        call.enqueue(object:  retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful){
                    val loginResponse= response.body()
                    if (loginResponse==null){
                        toast("Hubo un fallo.")
                        return
                    }
                    if (loginResponse.success){
                        createSessionPreference(loginResponse.token)
                        toast("Bienvenido ${loginResponse.user.name}!")
                        goToMenuActivity(true)
                    }
                    if(!loginResponse.success){
                        toast("las credenciales son incorrectas.")
                    }
                }else{
                    toast("las credenciales son incorrectas.")
//                    toast(response.body().toString())
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                t.localizedMessage?.let { toast(it) }
            }
        })
    }
    private fun performRegister(name: String?, email: String?, password: String?) {

        if(name != null || email != null || password != null){
            val call=apiService.postRegister(name!!,email!!,password!!, password)
            call.enqueue(object: Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if(response.isSuccessful){
                        val loginResponse= response.body()
                        if (loginResponse==null){
                            toast("Hubo un fallo.")
                            return
                        }
                        if (loginResponse.success){
                            createSessionPreference(loginResponse.token)
                            toast("Bienvenido ${loginResponse.user.name}!")
                            goToMenuActivity()
                        }
                        if(!loginResponse.success){
                            toast("las credenciales son incorrectas.")
                        }
                    }else{
                        Log.d(ContentValues.TAG, "onResponse: ${response.message()}")
                        toast("Ocurrio un error en el registro")
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    t.localizedMessage?.let { toast(it) }
                }
            })
        }

    }
    private fun goToMenuActivity(isUserInput: Boolean = false, provider: ProviderType? =null){
        // createSessionPreference()
        val intent=Intent(this, MenuActivity::class.java)
        if (isUserInput){
            intent.putExtra("store_token",true)
        }
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode==GOOGLE_SIGN_IN){
            val task= GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account= task.getResult(ApiException::class.java)
                if (account!=null){
                    val credential= GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
                        if(it.isSuccessful){
                            val googleEmail= account.email?:""
                            val providerType= ProviderType.GOOGLE
                            toast(googleEmail)
                            toast(providerType.toString())
                        }
                    }
                }
            } catch (e:ApiException){
                toast("exeption catched")
            }

        }
        else{
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun createSessionPreference(token:String) {
//        PreferenceManager.getDefaultSharedPreferences()

        /*
        val preferences= getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor=preferences.edit()
        editor.putBoolean("session",true)
        editor.apply()
         */
        val preferences= PreferenceHelper.defaultPrefs(this)
        preferences["token"]= token
    }
    override fun onBackPressed() {
        if (snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        updateUI(currentUser)
    }
}