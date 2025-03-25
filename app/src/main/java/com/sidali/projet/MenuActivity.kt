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
    lateinit var maisonsAdapter : ArrayAdapter<MaisonData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)


        loadMaisons()
        initializeMaisonsList()
        findViewById<ListView>(R.id.ListView).setOnItemClickListener(::onItemClicked)
    }


    private val maisons : ArrayList<MaisonData> = ArrayList()

    private fun loadMaisons(){
        Api().get<ArrayList<MaisonData>>("https://polyhome.lesmoulinsdudev.com/api/houses",::getMaisonSuccess,intent.getStringExtra("token"))
    }

    private fun getMaisonSuccess(responseCode:Int,listMaisons:ArrayList<MaisonData>?){
        if (responseCode == 200 && listMaisons != null){
            maisons.clear()
            maisons.addAll(listMaisons)
            updateMaisonsList()
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
        listV.adapter = MaisonAdapter(this,maisons)
    }



    fun onItemClicked(parent: AdapterView<*>, view: View, position:Int, id:Long) {
        val clickedItem = parent.getItemAtPosition(position) as MaisonData

        println("Item cliqué : "+clickedItem.houseId)
        val intentRemote = Intent(this,RemoteActivity::class.java)
        intentRemote.putExtra("houseId",clickedItem.houseId.toString())
        intentRemote.putExtra("token",intent.getStringExtra("token"))
        startActivity(intentRemote)
    }


}