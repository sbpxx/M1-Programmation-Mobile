package com.sidali.projet

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api

class RemoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_remote)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadDevices()
        initializeDevicesList()

    }

    private var Ldevices: DevicesListData = DevicesListData(ArrayList())

    private fun loadDevices() {
        val houseId: String = intent.getStringExtra("houseId").toString()
        val token: TokenData = TokenData(intent.getStringExtra("token").toString())

        Api().get(
            "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices",
            ::RemoteListSuccess,
            token.token
        )
    }

    private fun RemoteListSuccess(responseCode: Int, listDevices: DevicesListData?) {
        if (responseCode == 200 && listDevices != null) {
            println(Ldevices)
            Ldevices.devices.clear()
            Ldevices.devices.addAll(listDevices.devices)
            updateDevicesList()
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
        listV.adapter = RemoteAdapter(this, Ldevices.devices, intent.getStringExtra("houseId").toString(),intent.getStringExtra("token").toString()){ deviceId, availableCommands ,type,power ,opening ->

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


                intentRinterface.putExtra("token",intent.getStringExtra("token").toString())
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

}