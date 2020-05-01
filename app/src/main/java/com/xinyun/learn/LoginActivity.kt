package com.xinyun.learn

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.FormBody
import okio.ByteString.Companion.encode
import org.json.JSONObject
import java.net.URLEncoder

class LoginActivity : Activity() {
    private val Baseurl = "Please enter your airport url."
    @SuppressLint("ShowToast", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)  //全屏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_login)

        LoginBtn.setOnClickListener {
            LoginBtn.text = "登陆中"
            val Username = InputEmail.text.toString()
            val Password = InputPassword.text.toString()
            val LoginBody = FormBody.Builder()
                .add("email", Username)
                .add("passwd", Password)
                .add("code", "")
                .build()
            // 请求一波数据
            Http.post(Baseurl+"/auth/login",LoginBody,
                { _, response ->
                    val data:String = response.body!!.string()
                    var Obj = JSONObject(data)
                    var Status = Obj.getString("ret")
                    var msg = Obj.getString("msg")
                    if (Status == "0"){
                        LoginBtn.text = "$msg,请重试"
                    }else{
                        runOnUiThread {
                            var bundle = Bundle()
                            bundle.putString("Username", Username)
                            bundle.putString("Password", Password)
                            intent = Intent().setClass(this,GameActivity::class.java)
                            intent.putExtras(bundle)
                            startActivity(intent)}
                    }
                    Log.d("Success",data)

                },
                { _, ioException ->
                    Log.d("Error", ioException.toString())
                })

        }
        ForgotPassWord.setOnClickListener {
            Toast.makeText(this, "暂未开放,前往官网找回密码", Toast.LENGTH_SHORT).show()
        }

        Registered.setOnClickListener {
            Toast.makeText(this, "暂未开放,前往官网注册", Toast.LENGTH_SHORT).show()
            // startActivity(Intent().setClass(this,RegisteredActivity::class.java))
        }

    }
}