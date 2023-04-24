package com.vertie

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.internal.LinkedTreeMap
import com.vertie.data.user.LoginResponse
import com.vertie.data.user.ProxyUsers
import com.vertie.data.user.Userr
import com.vertie.javacode.activities.ForgotPassword
import com.vertie.javacode.apiManager.APIManager
import com.vertie.javacode.models.UserData
import com.vertie.javacode.utility.Debugger
import com.vertie.javacode.utility.Globals
import com.vertie.utils.bindingUtils.EmailUtils
import com.vertie.utils.extenstions.notifyObserver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.log


class LoginActivity : AppCompatActivity() {


    var TAG: String = "LoginActivity"
    lateinit var email1: TextInputEditText
    lateinit var password1: TextInputEditText
    val email: MutableLiveData<String> = MutableLiveData<String>()
    val password: MutableLiveData<String> = MutableLiveData<String>()
    val emailError: MutableLiveData<String> = MutableLiveData<String>()
    val passwordError: MutableLiveData<String> = MutableLiveData<String>()
    lateinit var proxyUsers : List<Userr>
    //    val errorBinder : MutableLiveData<LoginErrorBinder?> = MutableLiveData<LoginErrorBinder?>(
//        LoginErrorBinder()
//    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email1 = findViewById(R.id.editTextUI)
        password1 = findViewById(R.id.editTextUP)

//        email.value = "sachin.prajapati0533@gmail.com"
//        password.value = "P@ssw0rd"

//        editTextUI
        findViewById<TextView>(R.id.forget_password_button).setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }

        findViewById<Button>(R.id.btlogin).setOnClickListener {
            Log.d("LoginActivity ", "" + email1.text)
            Log.d("LoginActivity ", "" + password1.text)

            loginAPI()
        }

    }

    fun loginAPI() {
//
//        email.value = "sachin.prajapati0533@gmail.com"
//        password.value = "P@ssw0rd"

        email.value = "loricmarshall16@gmail.com"
        password.value = "M490RN8"

//        email.value = email1.text.toString()
//        password.value = password1.text.toString()
        var enter: Boolean = true

//        emailError.value = ""
//        passwordError.value = ""

        // Validate email
        val mail = validateEmail()

        // Validate password
        val pass = validatePassword()

        if (mail && pass) {
            val mapFields = HashMap<String, String>()
            mapFields["EmailorVIN"] = email.value!!
            mapFields["Password"] = password.value!!
            val call = APIManager
                .getUserManagerService(
                    this,
                    GsonConverterFactory.create(GsonBuilder().setLenient().create())
                )
                .login(mapFields)
            Globals.showProgressDialog(this)
            call.enqueue(object : Callback<Any?> {
                override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                    val body = response.body() as LinkedTreeMap<String, String>?

                    Debugger.debugD(TAG, response.toString())
                    Globals.hideProgressDialog()

                    val jsonObject1: JsonObject = Gson().toJsonTree(body).getAsJsonObject()
                    val a1 = Gson().fromJson(jsonObject1, LoginResponse::class.java)

                    val myArrayList = ArrayList<Userr>()
                    a1.user?.let { it1 -> myArrayList.add(it1) }

                    proxyUsers = ArrayList()
                    if(a1?.proxyUsers?.size!! > 0){
                        for (item in a1.proxyUsers){
                            item.registration?.let { it1 -> myArrayList.add(it1) }
                        }
                        proxyUsers = myArrayList
                    }

                    val editor = getSharedPreferences(
                        Constants.userList,
                        MODE_PRIVATE
                    ).edit()
                    editor.putString(
                        Constants.proxyUserList,
                       Gson().toJson(proxyUsers)
                    )
                    editor.commit()
//Gson().toJson(viewModel.proxyUsers))
                    //a1.proxyUsers.toString()
                    val editor2 = getSharedPreferences(
                        Constants.user,
                        MODE_PRIVATE
                    ).edit()

                    editor2.putString(
                        Constants.user_email,
                        email.value
                    )
                    editor2.putString(
                        Constants.token,
                        a1.jwtToken
                    )

                    editor2.putString(
                        Constants.user_id,
                        a1.user?.id
                    )
//                    val jsonObject1: LinkedTreeMap<String, String> = body?.get("user")
//                    gson.fromJson(jsonObject1, UserData::class.java)
//                    val jsonObject: JsonObject? = gson.toJsonTree(body?.get("user")).getAsJsonObject()
//                    val a:UserData =  body?.get("user")
//                    editor2.putString(
//                        Constants.user_id,
//                        user.id
//                    )
//                    editor2.putBoolean(Constant.MM_SHARED_USER_ID_PASS_SAVE, true)
                    editor2.commit()

                    startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                    finish()
                }

                override fun onFailure(call: Call<Any?>, t: Throwable) {
                    Globals.hideProgressDialog()
                    Debugger.debugE(TAG, "API Failed: " + t.localizedMessage)
                }
            })
        }
    }

    fun validateEmail(): Boolean {
        //val errorBinder = LoginErrorBinder()
        var enter = true
        if (email.value.isNullOrEmpty()) {
            enter = false
            emailError.value = "Email is required"// Resources.getSystem().getString(R.string.emailrequired)
            emailError.notifyObserver()
        } else {
            if (!EmailUtils.isValidEmail(email.value!!)) {
                enter = false
                emailError.value = "Email is not valid"//Resources.getSystem().getString(R.string.emailisnotvalid)
                emailError.notifyObserver()
            } else {
                enter = true
                emailError.value = ""
                emailError.notifyObserver()
            }
        }
        return enter
    }

    fun validatePassword(): Boolean {
        //val errorBinder = LoginErrorBinder()
        var enter = true
        if (password.value.isNullOrEmpty()) {
            enter = false
            passwordError.value = "Password is required"//Resources.getSystem().getString(R.string.passwordrequired)
            passwordError.notifyObserver()
        } else {
            enter = true
            passwordError.value = ""
            passwordError.notifyObserver()
        }
        return enter
    }

//    fun login(){
//
//        email.value = "sachin.prajapati0533@gmail.com"
//        password.value = "P@ssw0rd"
//
////        email.value = "loricmarshall16@gmail.com"
////        password.value = "M490RN8"
//
//        email.value = email1.text.toString()
//        password.value = password1.text.toString()
//        var enter : Boolean = true
//
//        emailError.value = ""
//        passwordError.value = ""
//
//        // Validate email
//        val mail = validateEmail()
//
//        // Validate password
//        val pass = validatePassword()
//
//        if(mail&&pass){
//            // Begin Login
//            loading.value = true
//            disposable.add(
//                userRepository.userLogin(
//                    loginRequest()
//                ).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe({
//                        if(it.resultCode == "-1"){
//                            loading.value = false
//                            //errorLogin.value = it.resultMessage
//                            snackMessage.value = it.resultMessage
//                        }else{
//                            //save to local
//                            SingletonClass.getInstance().email = email.value
//                            val myArrayList = ArrayList<Userr>()
//                            it.user?.let { it1 -> myArrayList.add(it1) }
//                            if(it?.proxyUsers?.size!! > 0){
//                                for (item in it?.proxyUsers!!){
//                                    item.registration?.let { it1 -> myArrayList.add(it1) }
//                                }
//                                proxyUsers = myArrayList
//                                goToChieldActivity.value = true
//                                saveToPrefs(Userr(email = email.value,password = password.value),it,true)
//                            }else{
//                                goToChieldActivity.value = false
//                                saveToPrefs(Userr(email = email.value,password = password.value),it,false)
//                                SingletonClass.getInstance().userId = it?.user?.id
//                            }
//                        }
//                    },{
//                        handleDataError("tag",it)
//                    })
//            )
//        }
//    }
//
//    fun saveToPrefs(user : Userr, response: LoginResponse, isChield:Boolean){
//        disposable.add(
//            userRepository.setUserPrefs(
//                user,response.jwtToken!!,response.user?.id!!
//            ).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    loading.value = false
//                    if(isChield){}else{goToDashBoard.value = true}
//                },{
//                    handleDataError("tag",it)
//                })
//        )
//    }
//
//    fun loginRequest() : LoginRequest {
//        return LoginRequest(
//            email = email.value,
//            password = password.value
//        )
//    }
//    fun forgetPassword(){
//    }
//
//    fun validateEmail() : Boolean{
//        //val errorBinder = LoginErrorBinder()
//        var enter = true
//        if(email.value.isNullOrEmpty()){
//            enter = false
//            errorBinder.value?.emailError = resourceProvider.getString(R.string.emailrequired)
//            errorBinder.notifyObserver()
//        }else{
//            if(!isValidEmail(email.value!!)){
//                enter = false
//                errorBinder.value?.emailError = resourceProvider.getString(R.string.emailisnotvalid)
//                errorBinder.notifyObserver()
//            }else{
//                enter = true
//                errorBinder.value?.emailError = ""
//                errorBinder.notifyObserver()
//            }
//        }
//        return enter
//    }
//    fun validatePassword() : Boolean{
//        //val errorBinder = LoginErrorBinder()
//        var enter = true
//        if(password.value.isNullOrEmpty()){
//            enter = false
//            errorBinder.value?.passwordError = resourceProvider.getString(R.string.passwordrequired)
//            errorBinder.notifyObserver()
//        }else{
//            enter = true
//            errorBinder.value?.passwordError = ""
//            errorBinder.notifyObserver()
//        }
//        return enter
//    }
//
//    data class LoginErrorBinder(
//        var emailError : String? = null,
//        var passwordError : String? = null
//    )

}