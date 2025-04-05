package com.sidali.projet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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
    private val refreshInterval = 10_000L
    private lateinit var houseId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_remote)

        token = getToken()
        houseId = intent.getStringExtra("houseId").toString()
        waitMsg = findViewById<TextView>(R.id.waitingMessage)

        loadDevices()
        initializeDevicesList()
        setupBottomNavUtils(intent.getStringExtra("houseId").toString(),token)
        setupTopNavUtils(intent.getStringExtra("houseId").toString(),token)



    }



    private var Ldevices: DevicesListData = DevicesListData(ArrayList())

    private fun loadDevices() {
        Api().get(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
            ::RemoteListSuccess,
            token
        )
    }

    private fun RemoteListSuccess(responseCode: Int, listDevices: DevicesListData?) {
        runOnUiThread {
            val btnContainer = findViewById<View>(R.id.buttonContainer)
            waitMsg.text = "En attente de connexion à votre maison ... (Maison : $houseId)"

            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val displayUsefulButtons = prefs.getBoolean("usefulButtons", true)

            if (responseCode == 200 && listDevices != null) {
                waitMsg.visibility = View.GONE
                println(Ldevices)
                Ldevices.devices.clear()
                Ldevices.devices.addAll(listDevices.devices)
                updateDevicesList()


                btnContainer.visibility = if (displayUsefulButtons) View.VISIBLE else View.GONE

                refreshJob?.cancel()
            } else {
                waitMsg.visibility = View.VISIBLE
                btnContainer.visibility = View.GONE
                println("Erreur API : $responseCode")
                waitReloader()
            }
        }
    }

    private fun waitReloader() {
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            while (isActive) {
                loadDevices()
                delay(refreshInterval)
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
        listV.adapter = RemoteAdapter(this, Ldevices.devices, houseId,token){ deviceId, availableCommands ,type,power ,opening ->

            onDeviceSelected(deviceId, availableCommands,type,power,opening)}
    }

    private fun onDeviceSelected(deviceId: String,availableCommands: ArrayList<String>,type:String, power :Int?, opening :Float?) {

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

                intentRinterface.putExtra("houseId",houseId)
                intentRinterface.putExtra("deviceId",deviceId)
                intentRinterface.putExtra("availableCommands",availableCommands)

                startActivity(intentRinterface)
        }


        }

    }

    private fun sendCommandToDevice(deviceId: String, command: String, power :Int?, opening :Float?) {
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



    public fun turnOffAllLights(view: View){
        for (device in Ldevices.devices) {
            if (device.type == "light"){
                        sendCommandToDevice(device.id, device.availableCommands[1], device.power, device.opening)
            }
        }
    }

    public fun openAllShutters(view: View){
        for (device in Ldevices.devices) {
            if (device.type == "rolling shutter"){
                        sendCommandToDevice(device.id, device.availableCommands[0], device.power, device.opening)
            }
        }
    }

    public fun closeAllShutters(view: View){
        for (device in Ldevices.devices) {
            if (device.type == "rolling shutter"){
                        sendCommandToDevice(device.id, device.availableCommands[1], device.power, device.opening)
            }
        }
    }

    public fun openGarageInterface(view: View){
        for (device in Ldevices.devices){
            if (device.type == "garage door"){
                val intentGarage = Intent(this, RollingInterface::class.java)
                intentGarage.putExtra("houseId", houseId)
                intentGarage.putExtra("deviceId", device.id)
                intentGarage.putExtra("availableCommands", device.availableCommands)
                startActivity(intentGarage)
            }
        }
    }

    private fun refreshUsefulButtonsVisibility() {
        val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val displayUsefulButtons = prefs.getBoolean("usefulButtons", true)

        val btnContainer = findViewById<View>(R.id.buttonContainer)
        btnContainer.visibility = if (displayUsefulButtons) View.VISIBLE else View.GONE
    }


    override fun onResume() {
        super.onResume()
        updateSelectedNavItem(findViewById(R.id.bottom_navigation))
        loadDevices()
        if (waitMsg.visibility == View.GONE){
            refreshUsefulButtonsVisibility()
        }
    }

    override fun onDestroy() {
        refreshJob?.cancel()
        super.onDestroy()
    }
}