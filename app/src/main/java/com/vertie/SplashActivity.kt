package com.vertie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

            val preferences = this.getSharedPreferences(Constants.user, 0)
            val email : String? = preferences?.getString(Constants.user_email,null)
            if (email != null) {
                if(email.isEmpty()){
                    startActivity(Intent(this, LoginActivity::class.java))
                }else{
                    startActivity(Intent(this, DashboardActivity::class.java))
                }
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
    }
}