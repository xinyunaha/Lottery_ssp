package com.xinyun.learn

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.FormBody
import org.json.JSONObject
import java.math.RoundingMode
import java.text.DecimalFormat

class GameActivity : Activity() {
    private val BaseUrl = "Please enter your airport url."
    // 获取上一个activity传过来的数据
    @SuppressLint("ShowToast", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.activity_game)

        // 定义一些变量常量
        val bundle = this.intent.extras
        val Username = bundle?.get("Username").toString()
        val Password = bundle?.get("Password").toString()
        var GameTimes = 0  // 游戏次数
        var ThisGet  = 0.00  // 这一次获得的
        var AllGet = 0.00  // 所有得到的
        var WinTimes = 0  // 获胜次数
        var WinRate = "" // 综合获胜率
        var GameStatus = 0  // 游戏状态 0为空闲

        // 定义一波函数(方便调用
        fun GetUserInfo() {
            Game_btn.text = "同步数据中,请稍后"
            Game_btn.isClickable = false
            val PostBody = FormBody.Builder()
                .add("user_email", Username)
                .add("user_auth", Password)
                .build()
            Http.post(
                "$BaseUrl/gtisectapi/userinfo", PostBody,
                { _, response ->
                    val data: String = response.body!!.string()
                    Log.d("success", "$data")
                    val objects = JSONObject(data)
                    val jsondata = objects.getJSONObject("data")
                    val flow = jsondata.getString("flow")
                    val time = jsondata.getString("class_expire")
                    runOnUiThread {
                        usertime_text.text = time
                        userflow_text.text = flow
                        Game_btn.text = "Start !!!"
                        Game_btn.isClickable = true
                    }
                },
                { _, ioException ->
                    Log.d("Error", ioException.toString())
                    runOnUiThread {
                        Game_btn.text = "获取账号信息失败，重启app试试?"
                        Toast.makeText(this, "出了点问题，重启app试试?", Toast.LENGTH_LONG).show()
                    }
                })
        }
        fun Game(){
            GameStatus = 1
            runOnUiThread {
                Game_btn.text = "抽卡中，请稍后"
            }
            var Body = FormBody.Builder().build()
            Http.post("$BaseUrl/user/checkin",Body,
                { _, response ->
                    // 拿所有返回值
                    val data:String = response.body!!.string()
                    Log.d("Success",data)
                    val Obj = JSONObject(data)
                    val msg = Obj.getString("msg")
                    // 提取抽到的流量数
                    val Msg = msg.replace(" ","").replace("获得了", "").replace("MB流量.","")
                    GameTimes += 1  // 游戏次数+1
                    ThisGet = Msg.toDouble()
                    AllGet += ThisGet  // 计算总量
                    if (ThisGet > 0){
                        runOnUiThread {
                            WinTimes += 1  // 获胜次数+1
                        }
                    }
                    WinRate = ((WinTimes.toDouble()/GameTimes.toDouble()) * 100.00).toString()
                    val format = DecimalFormat("0.##")
                    format.roundingMode = RoundingMode.FLOOR
                    WinRate = format.format(WinRate.toDouble()).toString()
                    Log.d("Info","获胜次数->$WinTimes|游戏次数->$GameTimes|胜率->$WinRate")
                    // 显示部分
                    runOnUiThread {
                        ThisGet_text.text = "$ThisGet MB"  // 显示本次获得的流量
                        AllGet_text.text = "$AllGet MB"  // 显示总的流量
                        SuccessTimes_text.text = "$GameTimes 次"
                        WinRate_text.text = "$WinRate %"
                        Game_btn.text = "抽卡成功，再来一次吧 !!!"
                        GameStatus = 0
                    }
                    // 胜率颜色
                    if (WinRate.toDouble() < 50){
                        runOnUiThread {
                            WinRate_layout.setBackgroundColor(Color.parseColor("#f1441d"))
                        }
                    }else{
                        runOnUiThread {
                            WinRate_layout.setBackgroundColor(Color.parseColor("#20a162"))
                        }
                    }
                    // 本次所得颜色
                    if (ThisGet < 50){
                        runOnUiThread {
                            ThisGet_layout.setBackgroundColor(Color.parseColor("#f1441d"))
                        }
                    }else{
                        runOnUiThread {
                            ThisGet_layout.setBackgroundColor(Color.parseColor("#20a162"))
                        }
                    }
                    // 累计所得颜色
                    if (AllGet < 50){
                        runOnUiThread {
                            AllGet_layout.setBackgroundColor(Color.parseColor("#f1441d"))
                        }
                    }else{
                        runOnUiThread {
                            AllGet_layout.setBackgroundColor(Color.parseColor("#20a162"))
                        }
                    }

                },
                { _, ioException ->
                    GameStatus = 0
                    Log.d("Error", ioException.toString())
                })
        }

        // 开始就同步数据
        runOnUiThread{
            Game_btn.text = "同步数据中,请稍后"
            Game_btn.isClickable = false
        }
        GetUserInfo()


        sync.setOnClickListener {
            if(Game_btn.text != "同步数据中,请稍后"){
                GetUserInfo()
            }
        }
        Game_btn.setOnClickListener {
            if (GameStatus == 0) {
                Game()  // 抽卡
            }else{
                Toast.makeText(this,"请不要多次点击",Toast.LENGTH_SHORT)
            }
        }
    }
}