package com.xinyun.learn

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_registered.*
import okhttp3.FormBody

class RegisteredActivity : Activity() {
    private val BaseUrl = "Please enter your airport url."
    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)  //全屏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_registered)
        fun TextView.checkBlank(message: String): String? {
            val text = this.text.toString()
            if (text.isBlank()) {
                Toast.makeText(this@RegisteredActivity, message, Toast.LENGTH_SHORT)
                error = message
                return null
            }
            return text
        }

        registeredBtn.setOnClickListener {
            val UserName = registeredUserName.checkBlank("用户名不能为空") ?: return@setOnClickListener
            val EmailAddress = registeredEmail.checkBlank("邮箱不能为空") ?: return@setOnClickListener
            val Password = registeredPassWord.checkBlank("密码不能为空") ?: return@setOnClickListener
            val EmailCode = registeredEmailCode.checkBlank("邮箱验证码不能为空") ?: return@setOnClickListener

            Log.d("Tag", "请求注册")
            val LoginBody = FormBody.Builder()
                .add("email", EmailAddress)
                .add("name", UserName)
                .add("passwd", Password)
                .add("repasswd", Password)
                .add("code", "admin")
                .add("emailcode", EmailCode)
                .build()
            // 请求一波数据
            Http.post("$BaseUrl/auth/register", LoginBody,
                { _, response ->
                    val data: String = response.body!!.string()
                    Log.d("Tag", data)

                },
                { _, ioException ->
                    Log.d("Tag", "访问失败")
                    Log.d("Tag", ioException.toString())
                })
        }

        registeredEmailCodeBtn.setOnClickListener {
            Toast.makeText(this, "正在发送验证码到您的邮箱", Toast.LENGTH_SHORT).show()
        }


    }
}
