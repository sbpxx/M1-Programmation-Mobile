package com.sidali.projet

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api

class RollingInterface : AppCompatActivity() {
    lateinit var houseId:String
    lateinit var deviceId:String
    lateinit var token: String
    lateinit var availableCommands:ArrayList<String>
    lateinit var deviceCommand: DeviceCommand
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_rolling_interface)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = getToken()

        houseId = intent.getStringExtra("houseId").toString()
        deviceId = intent.getStringExtra("deviceId").toString()
        availableCommands = intent.getStringArrayListExtra("availableCommands") as ArrayList<String>

        println("HouseId : $houseId")
        println("DeviceId : $deviceId")
        println("Token : $token")

    }



    public fun monter(view:View){
        println("test click btn $houseId")
        println("test click btn $deviceId")
        println("test click btn $token")
        println("test click btn $availableCommands")
        deviceCommand = DeviceCommand(availableCommands[0])
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command",deviceCommand,::onCommandSuccess,token)
    }

    public fun stop(view: View){
        deviceCommand = DeviceCommand(availableCommands[2])
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command", deviceCommand,::onCommandSuccess,token)
    }

    public fun descendre(view:View){
        println("test click btn $houseId")
        println("test click btn $deviceId")
        println("test click btn $token")
        println("test click btn $availableCommands")
        deviceCommand = DeviceCommand(availableCommands[1])
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/devices/$deviceId/command",deviceCommand,::onCommandSuccess,token)
    }

    private fun onCommandSuccess(responseCode: Int) {
        if (responseCode == 200) {
            println("Commande envoyée avec succès")
        } else {
            println("Erreur lors de l'envoi de la commande")
            println("Code de réponse : $responseCode")
        }

    }
}