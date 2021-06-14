package com.example.somecompanyomunikator

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*


class FullscreenNotification : AppCompatActivity() {

    private val requestSendSms: Int = 2
    var timerSms: Timer? = null
    private val TIME_IN_MILIS: Long = 300000
    private var mTimeLeftInMillis = TIME_IN_MILIS
    lateinit var alarmManager: AlarmManager
    lateinit var alarmIntent: PendingIntent
    val zarzadzanieAlarmem = newAlarm()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_notification)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )

        val data = intent.extras!!.getString("nrTelefonu", "000000000")

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intentNewAlarm = Intent(this, FullscreenNotification::class.java)
        intentNewAlarm.putExtra("nrTelefonu", data)
        alarmIntent = PendingIntent.getActivity(applicationContext,0, intentNewAlarm, 0)

        zarzadzanieAlarmem.setAlarm(alarmManager, alarmIntent)

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val alarm:Ringtone = RingtoneManager.getRingtone(this, uri)

        if (!alarm.isPlaying){
            alarm.play()
        }

        timerSms = Timer()
        timerSms!!.schedule(object : TimerTask() {
            override fun run() {
                if (ActivityCompat.checkSelfPermission(this@FullscreenNotification, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this@FullscreenNotification, arrayOf(Manifest.permission.SEND_SMS),requestSendSms)
                }
                else{
                    alarm.stop()
                    sendSms(data)
                    this@FullscreenNotification.moveTaskToBack(true)
                    this@FullscreenNotification.finish()
                }
            }
        }, TIME_IN_MILIS)

        val button = findViewById<Button>(R.id.buttonOk)
        button?.setOnClickListener() {
            timerSms?.cancel()
            alarm.stop()
            this@FullscreenNotification.moveTaskToBack(true)
            this@FullscreenNotification.finish()
        }

        val timerTextView = findViewById<TextView>(R.id.timerTextView)
        val timerCountDown = object: CountDownTimer(TIME_IN_MILIS, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                var minutes: Int = (mTimeLeftInMillis.toInt() / 1000) / 60
                var seconds: Int = (mTimeLeftInMillis.toInt() / 1000) % 60
                timerTextView.setText(String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds))
            }
            override fun onFinish() {
                timerTextView.setText("00:00")
            }
        }
        timerCountDown.start()
    }

    fun onRequestPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        if(requestCode == requestSendSms) sendSms("")
    }

    private fun sendSms(string: String){
        var number = string
        val text = "Coś jest nie tak"
        SmsManager.getDefault().sendTextMessage(number, null, text, null,null)
    }

    override fun onBackPressed() {
    }

}
