package com.sidali.projet.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api
import com.sidali.projet.adapter.MaisonAdapter
import com.sidali.projet.R
import com.sidali.projet.dataClass.HouseData
import com.sidali.projet.utils.getToken
import com.sidali.projet.utils.setupTopNavUtils
import com.sidali.projet.utils.showApiErrorToast

// Classe pour la liste des maisons de l'utilisateur

class HousesActivity : AppCompatActivity() {

    private lateinit var token: String
    private val maisons: ArrayList<HouseData> = ArrayList()
    private var firstLaunch: Boolean = true
    private var skipFirstLaunch: Boolean = false
    private val housesUrl = "https://polyhome.lesmoulinsdudev.com/api/houses"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_houses)

        token = getToken()

        loadMaisons()
        initializeMaisonsList()

        findViewById<ListView>(R.id.ListView).setOnItemClickListener(::onItemClicked)
        setupTopNavUtils(null, token)
    }

    // Fonction pour charger les maisons de l'utilisateur

    private fun loadMaisons() {
        Api().get(housesUrl, ::getMaisonSuccess, token)
    }

    // En cas de succès, on met à jour la liste des maisons

    private fun getMaisonSuccess(responseCode: Int, listMaisons: ArrayList<HouseData>?) {
        if (responseCode == 200 && listMaisons != null) {
            maisons.clear()
            maisons.addAll(listMaisons)
            updateMaisonsList()

            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val autoLoadHome = prefs.getBoolean("loadHome", false)

            // Si l'utilisateur avait coché "Charger la première maison" dans les paramètres, on l'ouvre

            if (autoLoadHome && maisons.isNotEmpty() && firstLaunch && !skipFirstLaunch) {
                firstLaunch = false
                openHouse(maisons[0].houseId.toString())
            }
        }else{
            runOnUiThread {
                showApiErrorToast(responseCode)
            }
        }
    }

    // Fonction pour mettre à jour la liste des maisons

    private fun updateMaisonsList() {
        val listV = findViewById<ListView>(R.id.ListView)
        runOnUiThread {
            (listV.adapter as MaisonAdapter).notifyDataSetChanged()
        }
    }

    // Fonction pour initialiser la liste des maisons

    private fun initializeMaisonsList() {
        val listV = findViewById<ListView>(R.id.ListView)
        listV.adapter = MaisonAdapter(this, maisons, token)
    }
    // Fonction pour ouvrir une maison

    private fun openHouse(houseId: String) {
        val intentRemote = Intent(this, RemoteActivity::class.java)
        intentRemote.putExtra("houseId", houseId)
        startActivity(intentRemote)
    }
    // Fonction pour gérer le clic sur une maison

    fun onItemClicked(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val clickedItem = parent.getItemAtPosition(position) as HouseData
        openHouse(clickedItem.houseId.toString())
    }

    override fun onResume() {
        super.onResume()
        skipFirstLaunch = true
        loadMaisons()
    }
}
