package com.sidali.projet

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidtp2.Api
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RollingInterface : AppCompatActivity() {
    lateinit var houseId: String
    lateinit var deviceId: String
    lateinit var token: String
    lateinit var availableCommands: ArrayList<String>
    lateinit var deviceCommand: DeviceCommand

    private lateinit var titleRolling: TextView
    private lateinit var txtPercentage: TextView

    private var refreshJob: Job? = null
    private val refreshInterval = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rolling_interface)

        token = getToken()

        houseId = intent.getStringExtra("houseId").toString()
        deviceId = intent.getStringExtra("deviceId").toString()
        availableCommands = intent.getStringArrayListExtra("availableCommands") as ArrayList<String>

        titleRolling = findViewById(R.id.titleRolling)
        txtPercentage = findViewById(R.id.txtPercentage)

        titleRolling.text = "Contrôle du périphérique : $deviceId"

        refreshPercentage()
    }

    private fun onRefreshingPercentage() {
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            while (isActive) {
                refreshPercentage()
                delay(refreshInterval)
            }
        }
    }

    override fun onDestroy() {
        refreshJob?.cancel()
        super.onDestroy()
    }

    fun monter(view: View) {
        deviceCommand = DeviceCommand(availableCommands[0])
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", deviceCommand, ::onCommandSuccess, token)
        onRefreshingPercentage()
    }

    fun stop(view: View) {
        deviceCommand = DeviceCommand(availableCommands[2])
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", deviceCommand, ::onCommandSuccess, token)

        refreshPercentage()
    }

    fun descendre(view: View) {
        deviceCommand = DeviceCommand(availableCommands[1])
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", deviceCommand, ::onCommandSuccess, token)
        onRefreshingPercentage()
    }

    private fun onCommandSuccess(responseCode: Int) {
        if (responseCode == 200) {
            println("Commande envoyée avec succès")
        } else {
            println("Erreur lors de l'envoi de la commande : $responseCode")
        }
    }

    private fun refreshPercentage() {
        Api().get<DevicesListData>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices", ::onRefreshSuccess, token)
    }

    private fun onRefreshSuccess(responseCode: Int, data: DevicesListData?) {
        if (responseCode == 200 && data != null) {
            var deviceFound: DeviceData? = null
            for (device in data.devices) {
                if (device.id == deviceId) {
                    deviceFound = device
                    break
                }
            }
            val percentage: Float? = deviceFound?.opening?.times(100)
            if (percentage == 0f || percentage == 100f) {
                refreshJob?.cancel()
            }
            runOnUiThread {

                txtPercentage.text = percentage?.toInt().toString() + "%"
            }
        }
    }
}
