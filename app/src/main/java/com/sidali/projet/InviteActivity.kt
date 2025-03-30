package com.sidali.projet

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtp2.Api

class InviteActivity : AppCompatActivity() {

    lateinit var guestsAdapter : ArrayAdapter<GuestData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_invite)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadGuests()
        initializeGuestsList()
        setupBottomNavUtils()
    }

    private val guests : ArrayList<GuestData> = ArrayList()

    private fun loadGuests(){
        val houseId = intent.getStringExtra("houseId")
        val token = intent.getStringExtra("token")
        Api().get<ArrayList<GuestData>>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",::getGuestSuccess,token)
    }

    private fun getGuestSuccess(responseCode:Int,listGuests:ArrayList<GuestData>?){
        if (responseCode == 200 && listGuests != null){
            guests.clear()
            guests.addAll(listGuests)
            updateGuestsList()
        }
    }

    private fun updateGuestsList(){
        val listV = findViewById<ListView>(R.id.listViewGuest)
        runOnUiThread{
            (listV.adapter as GuestAdapter).notifyDataSetChanged()
        }
    }

    private fun initializeGuestsList(){
        val listV = findViewById<ListView>(R.id.listViewGuest)
        listV.adapter = GuestAdapter(this,guests)
    }

    override fun onResume() {
        super.onResume()
        updateSelectedNavItem(findViewById(R.id.bottom_navigation))
    }
}