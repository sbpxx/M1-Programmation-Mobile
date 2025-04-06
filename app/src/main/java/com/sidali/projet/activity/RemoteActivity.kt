package com.sidali.projet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidtp2.Api
import com.sidali.projet.R
import com.sidali.projet.adapter.RemoteAdapter
import com.sidali.projet.dataClass.CommandData
import com.sidali.projet.dataClass.DevicesListData
import com.sidali.projet.utils.getToken
import com.sidali.projet.utils.setupBottomNavUtils
import com.sidali.projet.utils.setupTopNavUtils
import com.sidali.projet.utils.updateSelectedNavItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.sidali.projet.utils.showApiErrorToast

class RemoteActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var waitMsg: TextView
    private var refreshJob: Job? = null
    private val refreshInterval = 10_000L
    private lateinit var houseId: String

    private val Ldevices: DevicesListData = DevicesListData(ArrayList())
    private lateinit var devicesUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_remote)

        token = getToken()
        houseId = intent.getStringExtra("houseId").toString()
        devicesUrl = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices"

        waitMsg = findViewById(R.id.waitingMessage)

        loadDevices()
        initializeDevicesList()

        setupBottomNavUtils(houseId, token)
        setupTopNavUtils(houseId, token)
    }

    private fun loadDevices() {
        Api().get(devicesUrl, ::RemoteListSuccess, token)
    }

    private fun RemoteListSuccess(responseCode: Int, listDevices: DevicesListData?) {
        runOnUiThread {
            val btnContainer = findViewById<View>(R.id.buttonContainer)
            waitMsg.text = "En attente de connexion à votre maison ... (Maison : $houseId)"

            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val displayUsefulButtons = prefs.getBoolean("usefulButtons", true)

            if (responseCode == 200 && listDevices != null) {
                waitMsg.visibility = View.GONE
                Ldevices.devices.clear()
                Ldevices.devices.addAll(listDevices.devices)
                updateDevicesList()
                btnContainer.visibility = if (displayUsefulButtons) View.VISIBLE else View.GONE
                refreshJob?.cancel()
            } else {
                waitMsg.visibility = View.VISIBLE
                btnContainer.visibility = View.GONE
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
        runOnUiThread {
            (findViewById<ListView>(R.id.listview2).adapter as RemoteAdapter).notifyDataSetChanged()
        }
    }

    private fun initializeDevicesList() {
        val listV = findViewById<ListView>(R.id.listview2)
        listV.adapter = RemoteAdapter(this, Ldevices.devices, houseId, token) { deviceId, availableCommands, type, power, opening ->
            onDeviceSelected(deviceId, availableCommands, type, power, opening)
        }
    }

    private fun onDeviceSelected(deviceId: String, availableCommands: ArrayList<String>, type: String, power: Int?, opening: Float?) {
        if (availableCommands.isEmpty()) return

        if (type == "light" && power != null) {
            val command = if (power == 0) availableCommands[0] else availableCommands[1]
            sendCommandToDevice(deviceId, command, power, opening)
        } else if (type == "rolling shutter" || type == "garage door") {
            val intent = Intent(this, RollingGarageActivity::class.java)
            intent.putExtra("houseId", houseId)
            intent.putExtra("deviceId", deviceId)
            intent.putExtra("availableCommands", availableCommands)
            startActivity(intent)
        }
    }

    private fun sendCommandToDevice(deviceId: String, command: String, power: Int?, opening: Float?) {
        val commandUrl = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        val commandData = CommandData(command)
        Api().post(commandUrl, commandData, ::onCommandSuccess, token)
    }

    private fun onCommandSuccess(responseCode: Int) {
        if (responseCode == 200) {
            loadDevices()
        } else {
            showApiErrorToast(responseCode)
        }
    }

    fun turnOffAllLights(view: View) {
        for (device in Ldevices.devices) {
            if (device.type == "light") {
                sendCommandToDevice(device.id, device.availableCommands[1], device.power, device.opening)
            }
        }
    }

    fun openAllShutters(view: View) {
        for (device in Ldevices.devices) {
            if (device.type == "rolling shutter") {
                sendCommandToDevice(device.id, device.availableCommands[0], device.power, device.opening)
            }
        }
    }

    fun closeAllShutters(view: View) {
        for (device in Ldevices.devices) {
            if (device.type == "rolling shutter") {
                sendCommandToDevice(device.id, device.availableCommands[1], device.power, device.opening)
            }
        }
    }

    fun openGarageInterface(view: View) {
        for (device in Ldevices.devices) {
            if (device.type == "garage door") {
                val intent = Intent(this, RollingGarageActivity::class.java)
                intent.putExtra("houseId", houseId)
                intent.putExtra("deviceId", device.id)
                intent.putExtra("availableCommands", device.availableCommands)
                startActivity(intent)
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
        if (waitMsg.visibility == View.GONE) {
            refreshUsefulButtonsVisibility()
        }
    }

    override fun onDestroy() {
        refreshJob?.cancel()
        super.onDestroy()
    }
}
