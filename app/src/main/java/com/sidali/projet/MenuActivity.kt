package com.sidali.projet

import android.content.Intent
import android.media.session.MediaSession.Token
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api

class MenuActivity : AppCompatActivity() {

    private lateinit var token : String
    private val maisons : ArrayList<MaisonData> = ArrayList()
    private var firstLaunch : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        token = getToken()
        loadMaisons()
        initializeMaisonsList()


        findViewById<ListView>(R.id.ListView).setOnItemClickListener(::onItemClicked)
        setupTopNavUtils(null, token)
    }


    private fun loadMaisons(){
        Api().get<ArrayList<MaisonData>>("https://polyhome.lesmoulinsdudev.com/api/houses",::getMaisonSuccess,token)
    }

    private fun getMaisonSuccess(responseCode:Int,listMaisons:ArrayList<MaisonData>?){
        if (responseCode == 200 && listMaisons != null){
            maisons.clear()
            maisons.addAll(listMaisons)
            updateMaisonsList()

            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val autoLoadHome = prefs.getBoolean("loadHome", false)

            if (autoLoadHome && maisons.isNotEmpty() && firstLaunch) {
                firstLaunch = false
                openHouse(maisons[0].houseId.toString())
            }
        }
    }

    private fun updateMaisonsList(){
        val listV = findViewById<ListView>(R.id.ListView)
        runOnUiThread{
            (listV.adapter as MaisonAdapter).notifyDataSetChanged()
        }
    }

    private fun initializeMaisonsList(){
        val listV = findViewById<ListView>(R.id.ListView)
        listV.adapter = MaisonAdapter(this,maisons,token)
    }


    private fun openHouse(houseId:String){
        val intentRemote = Intent(this, RemoteActivity::class.java)
        intentRemote.putExtra("houseId", houseId)
        startActivity(intentRemote)
    }

    fun onItemClicked(parent: AdapterView<*>, view: View, position:Int, id:Long) {
        val clickedItem = parent.getItemAtPosition(position) as MaisonData
        openHouse(clickedItem.houseId.toString())
    }

    override fun onResume() {
        super.onResume()
        loadMaisons()
    }

}