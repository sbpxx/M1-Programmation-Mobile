package com.sidali.projet.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidtp2.Api
import com.sidali.projet.R
import com.sidali.projet.dataClass.CommandData
import com.sidali.projet.dataClass.DeviceData
import com.sidali.projet.dataClass.DevicesListData
import com.sidali.projet.utils.getToken
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.sidali.projet.utils.showApiErrorToast

class RollingGarageActivity : AppCompatActivity() {

    private lateinit var houseId: String
    private lateinit var deviceId: String
    private lateinit var token: String
    private lateinit var availableCommands: ArrayList<String>
    private lateinit var commandData: CommandData

    private lateinit var titleRolling: TextView
    private lateinit var txtPercentage: TextView

    private var refreshJob: Job? = null
    private val refreshInterval = 500L

    private lateinit var commandUrl: String
    private lateinit var devicesUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rolling_garage)

        token = getToken()
        houseId = intent.getStringExtra("houseId").toString()
        deviceId = intent.getStringExtra("deviceId").toString()
        availableCommands = intent.getStringArrayListExtra("availableCommands") as ArrayList<String>

        commandUrl = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        devicesUrl = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"

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
        commandData = CommandData(availableCommands[0])
        sendCommand()
        onRefreshingPercentage()
    }

    fun stop(view: View) {
        commandData = CommandData(availableCommands[2])
        sendCommand()
        refreshPercentage()
    }

    fun descendre(view: View) {
        commandData = CommandData(availableCommands[1])
        sendCommand()
        onRefreshingPercentage()
    }

    private fun sendCommand() {
        Api().post(commandUrl, commandData, ::onCommandSuccess, token)
    }

    private fun onCommandSuccess(responseCode: Int) {
        if (responseCode == 200) {
            // rien à faire ici
        } else {
            showApiErrorToast(responseCode)
        }
    }

    private fun refreshPercentage() {
        Api().get(devicesUrl, ::onRefreshSuccess, token)
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
        } else {
            showApiErrorToast(responseCode)
        }
    }
}
