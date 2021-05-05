package com.doe.topwisesdk

import android.content.*
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.ReaderCallback
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.doe.topwisesdk.domain.doecard.nfcCardReader.MyNfcManager
import com.doe.topwisesdk.domain.doecard.samndfelica.Utils
import com.doe.topwisesdk.domain.doecard.samndfelica.felica.FelicaOperations
import com.doe.topwisesdk.domain.doecard.samndfelica.sam.SAM
import com.topwise.cloudpos.aidl.AidlDeviceService
import com.topwise.cloudpos.aidl.psam.AidlPsam
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard
import com.topwise.cloudpos.data.PsamConstant
import timber.log.Timber
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var sam: SAM? = null

    var mSmartcardReader: AidlPsam? = null
    var rfcard: AidlRFCard? = null

    private var deviceManager: AidlDeviceService? = null

    val TOPWISE_SERVICE_ACTION = "topwise_cloudpos_device_service"

    private var oldTime: Long = -1
    val DELAY_TIME: Long = 200
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            bindService()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        nfcInitialization()

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Throws(IOException::class, RemoteException::class)
    fun readerInitialization() {
        if (isNormalVelocityClick(DELAY_TIME)) {

            // Open SAM
            try {
                val flag = mSmartcardReader!!.open()
                if (flag) {
                    Log.e("open", "open")
                    mSmartcardReader!!.reset(0x00)
                } else {
                    Log.e("not open", "not open")
                }
                rfcard!!.open()
                rfcard!!.reset(0x00)
                samInitialization()
            } catch (e: Exception) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
        }
    }

    private val conn: ServiceConnection = object : ServiceConnection {
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun onServiceConnected(name: ComponentName, serviceBinder: IBinder) {
            Log.d(ContentValues.TAG, "aidlService服务连接成功")
            if (serviceBinder != null) {    //绑定成功
                val serviceManager = AidlDeviceService.Stub.asInterface(serviceBinder)
                onDeviceConnected(serviceManager)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(ContentValues.TAG, "AidlService服务断开了")
        }
    }

    @Throws(IOException::class)
    fun bindService() {
        val intent = Intent()
        intent.action = TOPWISE_SERVICE_ACTION
        val eintent = Intent(createExplicitFromImplicitIntent(this, intent))
        val flag = bindService(eintent, conn, BIND_AUTO_CREATE)
        if (flag) {
            Log.d(ContentValues.TAG, "服务绑定成功")
        } else {
            Log.d(ContentValues.TAG, "服务绑定失败")
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun onDeviceConnected(serviceManager: AidlDeviceService) {
        deviceManager = serviceManager
        try {
            mSmartcardReader = AidlPsam.Stub.asInterface(
                serviceManager
                    .getPSAMReader(PsamConstant.PSAM_DEV_ID_1)
            )
            rfcard = AidlRFCard.Stub
                .asInterface(serviceManager.rfidReader)
            readerInitialization()
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }


    fun createExplicitFromImplicitIntent(context: Context, implicitIntent: Intent?): Intent? {
        // Retrieve all services that can match the given intent
        val pm = context.packageManager
        val resolveInfo = pm.queryIntentServices(implicitIntent!!, 0)

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size != 1) {
            return null
        }

        // Get component info and create ComponentName
        val serviceInfo = resolveInfo[0]
        val packageName = serviceInfo.serviceInfo.packageName
        val className = serviceInfo.serviceInfo.name
        val component = ComponentName(packageName, className)

        // Create a new intent. Use the old one for extras and such reuse
        val explicitIntent = Intent(implicitIntent)

        // Set the component to be explicit
        explicitIntent.component = component
        return explicitIntent
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @Throws(IOException::class, RemoteException::class)
    fun samInitialization() {
        Log.e("samInitialization", "samInitialization")
        sam = SAM(mSmartcardReader)
        sam!!.authenticateSAM()
        mFelicaOperations = FelicaOperations(sam, rfcard)
    }

    @Synchronized
    fun isNormalVelocityClick(time: Long): Boolean {
        val newTime = System.currentTimeMillis()
        if (oldTime == -1L) {
            oldTime = newTime
            return true
        } else {
            Log.v("asewang", "newTime : $newTime , oldTime : $oldTime")
            if (newTime - oldTime <= time) {
                oldTime = newTime
                return false
            }
            oldTime = newTime
        }
        return true
    }

    //application label begins......................................................................................................
    var mFelicaOperations: FelicaOperations? = null

    var myNfcManager: MyNfcManager? = null

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    fun nfcInitialization() {
        myNfcManager = MyNfcManager(this)
        val extras = Bundle()
        extras.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 60000)
        myNfcManager!!.nfcAdapter.enableReaderMode(
            this,
            object : ReaderCallback {
                override fun onTagDiscovered(tag: Tag) {
                    val intent = Intent().putExtra(NfcAdapter.EXTRA_TAG, tag)
                    onNewIntent(intent)
                }
            },
            NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            extras
        )
    }

     override fun onNewIntent(intent: Intent?) {
        if (!myNfcManager!!.readCard(intent)) {
            Log.e("WELCOME", "WELCOME")
        } else {
           // mFelicaOperations = FelicaOperations(sam, rfcard)
               Thread {
                   val data = mFelicaOperations!!.readStaticTollPassDetails()
                   Log.d("data from card", data.toString())
                  // Timber.d("genericDataWithBalance=${genericDataWithBalance}")
               }.start()
        }

        super.onNewIntent(intent)
    }

}