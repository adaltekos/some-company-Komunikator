package com.example.somecompanyomunikator

import android.Manifest
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var phoneNumber: String = ""
    private val SHARED_PREFS = "sharedPrefs"
    private val TEXT = "text"
    private val requestSendSms: Int = 2
    lateinit var alarmManager: AlarmManager
    lateinit var alarmIntent: PendingIntent
    val zarzadzanieAlarmem = newAlarm()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadData()

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, FullscreenNotification::class.java)
        intent.putExtra("nrTelefonu", phoneNumber)
        alarmIntent = PendingIntent.getActivity(applicationContext,0, intent, 0)

        switch1.setOnCheckedChangeListener { switch1, isChecked ->
            if (isChecked) {
                switch1.setBackgroundColor(Color.GREEN)
                switch1.setText("   ON")
                zarzadzanieAlarmem.setAlarm(alarmManager, alarmIntent)
                this.moveTaskToBack(true)
            } else {
                switch1.setBackgroundColor(Color.LTGRAY)
                switch1.setText("   OFF")
                zarzadzanieAlarmem.stopAlarm(alarmManager, alarmIntent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.numerTelefonuMenuButton -> {
                showDialogNrTelefonu()
            }
            R.id.testSms -> {
                showDialogTestSms()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showDialogNrTelefonu() {
        val dialogNrTlefonu = Dialog(this)
        dialogNrTlefonu.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogNrTlefonu.setCancelable(false)
        dialogNrTlefonu.setContentView(R.layout.nr_telefonu)
        val phoneField  = dialogNrTlefonu.findViewById(R.id.phoneEditField) as EditText
        phoneField.setText(phoneNumber)
        val okBtn = dialogNrTlefonu.findViewById(R.id.okBtn) as Button
        okBtn.setOnClickListener {
            phoneNumber = phoneField.text.toString()
            saveData(phoneNumber)
            dialogNrTlefonu.dismiss()
        }
        dialogNrTlefonu.show()
    }

    fun showDialogTestSms() {
        val dialogTestSms = Dialog(this)
        dialogTestSms.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogTestSms.setCancelable(true)
        dialogTestSms.setContentView(R.layout.test_sms)
        val phoneText  = dialogTestSms.findViewById(R.id.phoneNumberTextView) as TextView
        phoneText.setText(phoneNumber)
        val testBtn = dialogTestSms.findViewById(R.id.testSmsButton) as Button
        testBtn.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.SEND_SMS),requestSendSms)
            }
            else{
                SmsManager.getDefault().sendTextMessage(phoneNumber, null, "Test wiadomosci z aplikacji some company Komunikator", null,null)
                Toast.makeText(this@MainActivity, "Wysłano testowego SMSa", Toast.LENGTH_SHORT).show()
            }
            dialogTestSms.dismiss()
        }
        dialogTestSms.show()
    }

    fun saveData(string: String) {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TEXT, string)
        editor.apply()
    }

    fun loadData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        phoneNumber = sharedPreferences.getString(TEXT, "")!!
    }

    override fun onBackPressed() {
    }

}
