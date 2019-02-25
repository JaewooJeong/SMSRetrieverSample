package com.miichang.smsretrieversample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.Status
import android.content.IntentFilter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient


class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createSmsRetrieverClient()

        //Hashの作成
//        val helper = AppSignatureHelper(applicationContext)
//        Log.d("hash = ", helper.appSignatures.toString())//このハッシュをSMSに含める
    }

    private fun createSmsRetrieverClient() {
        val receiver = SMSBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(receiver, intentFilter)

        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            //SMSRetriever追加成功時リスナー
            Log.d("Listener", "SUCCESS")
        }

        task.addOnFailureListener {
            //SMSRetriever追加失敗時リスナー
            Log.d("Listener", "FAILURE")
        }
    }

    inner class SMSBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status
                var message = ""

                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        //自分のアプリ用のハッシュ値が含まれているSMSを受信したときにここに入ってくる
                        //対象のSMSの内容のみ取得可能
                        message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                        Log.d("SMSMessage = ", message)
                    }

                    CommonStatusCodes.TIMEOUT -> {
                        //レシーバーを開始して5分間SMSを受信しないとタイムアウトになる
                        Log.d("SMSMessage = ", "TIMEOUT")
                    }
                }
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnected(p0: Bundle?) {
    }
}