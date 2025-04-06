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

// Classe pour la liste des appareils de la maison

class RemoteActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var waitMsg: TextView
    private var refreshJob: Job? = null
    private val refreshInterval = 10_000L
    private lateinit var houseId: String

    private val devicesList : DevicesListData = DevicesListData(ArrayList())
    private lateinit var devicesUrl: String

    private var isReloading = false
    private var errorShown = false

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

    // Fonction pour charger les appareils de la maison

    private fun loadDevices() {
        Api().get(devicesUrl, ::RemoteListSuccess, token)
    }

    // Fonction pour mettre à jour la liste des appareils

    private fun RemoteListSuccess(responseCode: Int, listDevices: DevicesListData?) {
        runOnUiThread {
            val btnContainer = findViewById<View>(R.id.buttonContainer)
            waitMsg.text = "En attente de connexion à votre maison ... (Maison : $houseId)"

            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val displayUsefulButtons = prefs.getBoolean("usefulButtons", true)

            if (responseCode == 200 && listDevices != null) {
                waitMsg.visibility = View.GONE
                devicesList.devices.clear()
                devicesList.devices.addAll(listDevices.devices)
                updateDevicesList()
                btnContainer.visibility = if (displayUsefulButtons) View.VISIBLE else View.GONE
                refreshJob?.cancel()
                isReloading = false
                errorShown = false
            } else {
                waitMsg.visibility = View.VISIBLE
                btnContainer.visibility = View.GONE
                if (!isReloading) {
                    waitReloader()
                }
                if (!errorShown) {
                    showApiErrorToast(responseCode)
                    errorShown = true
                }
            }
        }
    }

    // Fonction pour relancer le rafraichissement des appareils

    private fun waitReloader() {
        isReloading = true
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            while (isActive) {
                loadDevices()
                delay(refreshInterval)
            }
        }
    }

    // Fonction pour mettre à jour la liste des appareils

    private fun updateDevicesList() {
        runOnUiThread {
            (findViewById<ListView>(R.id.listview2).adapter as RemoteAdapter).notifyDataSetChanged()
        }
    }

    // Fonction pour initialiser la liste des appareils

    private fun initializeDevicesList() {
        val listV = findViewById<ListView>(R.id.listview2)
        listV.adapter = RemoteAdapter(this, devicesList.devices) { deviceId, availableCommands, type, power ->
            onDeviceSelected(deviceId, availableCommands, type, power)
        }
    }

    // Fonction pour gérer le clic sur un appareil

    private fun onDeviceSelected(deviceId: String, availableCommands: ArrayList<String>, type: String, power: Int?) {
        if (type == "light") {
            if (power == 0) {
                sendCommandToDevice(deviceId, availableCommands[0])
            } else {
                sendCommandToDevice(deviceId, availableCommands[1])
            }
        } else if (type == "rolling shutter" || type == "garage door") {
            val intent = Intent(this, RollingGarageActivity::class.java)
            intent.putExtra("houseId", houseId)
            intent.putExtra("deviceId", deviceId)
            intent.putExtra("availableCommands", availableCommands)
            startActivity(intent)
        }
    }

    // Fonction pour envoyer une commande à un ou plusieurs appareils

    private fun sendCommandToDevice(deviceId: String, command: String) {
        val commandUrl = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command"
        val commandData = CommandData(command)
        Api().post(commandUrl, commandData, ::onCommandSuccess, token)
    }

    // Fonction pour rafraichir la liste des appareils après l'envoi d'une commande

    private fun onCommandSuccess(responseCode: Int) {
        if (responseCode == 200) {
            loadDevices()
        } else {
            runOnUiThread {
                showApiErrorToast(responseCode)
            }
        }
    }

    // Fonctions pour les boutons utiles

    fun turnOffAllLights(view: View) {
        for (device in devicesList.devices) {
            if (device.type == "light") {
                sendCommandToDevice(device.id, device.availableCommands[1])
            }
        }
    }

    fun openAllShutters(view: View) {
        for (device in devicesList.devices) {
            if (device.type == "rolling shutter") {
                sendCommandToDevice(device.id, device.availableCommands[0])
            }
        }
    }

    fun closeAllShutters(view: View) {
        for (device in devicesList.devices) {
            if (device.type == "rolling shutter") {
                sendCommandToDevice(device.id, device.availableCommands[1])
            }
        }
    }

    fun openGarageInterface(view: View) {
        for (device in devicesList.devices) {
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
