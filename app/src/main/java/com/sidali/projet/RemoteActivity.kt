package com.sidali.projet

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
        listV.adapter = RemoteAdapter(this, Ldevices.devices)
    }
}