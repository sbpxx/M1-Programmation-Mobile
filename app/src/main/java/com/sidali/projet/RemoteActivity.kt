package com.sidali.projet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidtp2.Api
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule

class RemoteActivity : AppCompatActivity() {
    private lateinit var token: String
    private lateinit var waitMsg: TextView
    private var refreshJob: Job? = null
    private val refreshInterval = 15_000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_remote)

        token = getToken()
        loadDevices()
        initializeDevicesList()
        setupBottomNavUtils(intent.getStringExtra("houseId").toString(),token)
        setupTopNavUtils(intent.getStringExtra("houseId").toString(),token)

    }



    private var Ldevices: DevicesListData = DevicesListData(ArrayList())

    private fun loadDevices() {
        val houseId: String = intent.getStringExtra("houseId").toString()


        Api().get(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
            ::RemoteListSuccess,
            token
        )
    }

    private fun RemoteListSuccess(responseCode: Int, listDevices: DevicesListData?) {
        waitMsg = findViewById<TextView>(R.id.waitingMessage)

        if (responseCode == 200 && listDevices != null) {
            if (waitMsg.visibility == View.VISIBLE) {waitMsg.visibility = View.GONE}
            println(Ldevices)
            Ldevices.devices.clear()
            Ldevices.devices.addAll(listDevices.devices)
            updateDevicesList()
            refreshJob?.cancel()
        } else {
            if (waitMsg.visibility == View.GONE) {waitMsg.visibility = View.VISIBLE}
            println("AAAAAAAAAAAA"+responseCode)
            waitReloader()
        }
    }

    private fun waitReloader() {
        // Annule le précédent job s'il existe
        refreshJob?.cancel()

        // Crée un nouveau job
        refreshJob = lifecycleScope.launch {
            while (isActive) {  // Boucle tant que la coroutine est active
                loadDevices()   // Exécute la requête
                delay(refreshInterval) // Attend 15 secondes
            }
        }
    }

    private fun updateDevicesList() {
        val listV = findViewById<ListView>(R.id.listview2)


        runOnUiThread {
            (listV.adapter as RemoteAdapter).notifyDataSetChanged()
        }
    }

    private fun initializeDevicesList() {
        val listV = findViewById<ListView>(R.id.listview2)
        listV.adapter = RemoteAdapter(this, Ldevices.devices, intent.getStringExtra("houseId").toString(),token){ deviceId, availableCommands ,type,power ,opening ->

            onDeviceSelected(deviceId, availableCommands,type,power,opening)}
    }

    private fun onDeviceSelected(deviceId: String,availableCommands: ArrayList<String>,type:String, power :Int?, opening :Int?) {

        var selectedCommand = ""
        println("Device sélectionné : $deviceId")
        println("Commandes disponibles : $availableCommands")

        if (availableCommands.isNotEmpty()) {
            if (type == "light" && power == 0){
                selectedCommand = availableCommands[0]
                sendCommandToDevice(deviceId, selectedCommand, power, opening)
        }else if (type == "light" && power == 1){
                selectedCommand = availableCommands[1]
                sendCommandToDevice(deviceId, selectedCommand, power, opening)
        }else if (type == "rolling shutter" || type == "garage door"){
                val intentRinterface= Intent(this,RollingInterface::class.java)


                intentRinterface.putExtra("houseId",intent.getStringExtra("houseId").toString())
                intentRinterface.putExtra("deviceId",deviceId)
                intentRinterface.putExtra("availableCommands",availableCommands)

                startActivity(intentRinterface)
        }


        }

    }

    private fun sendCommandToDevice(deviceId: String, command: String, power :Int?, opening :Int?) {
        val token = intent.getStringExtra("token")
        val houseId = intent.getStringExtra("houseId")
        val deviceCommand = DeviceCommand(command)
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command",deviceCommand,::onCommandSuccess,token)

    }

    private fun onCommandSuccess(responseCode: Int) {
        if (responseCode == 200) {
            println("Commande envoyée avec succès")
            loadDevices()
        } else {
            println("Erreur lors de l'envoi de la commande")}
        }

    override fun onResume() {
        super.onResume()
        updateSelectedNavItem(findViewById(R.id.bottom_navigation))
    }

    override fun onDestroy() {
        refreshJob?.cancel()
        super.onDestroy()
    }
}