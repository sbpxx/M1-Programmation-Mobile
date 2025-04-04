package com.sidali.projet.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtp2.Api
import com.sidali.projet.adapter.GuestAdapter
import com.sidali.projet.R
import com.sidali.projet.dataClass.GuestData
import com.sidali.projet.dataClass.UserData
import com.sidali.projet.utils.getToken
import com.sidali.projet.utils.setupBottomNavUtils
import com.sidali.projet.utils.setupTopNavUtils
import com.sidali.projet.utils.updateSelectedNavItem

class InviteActivity : AppCompatActivity() {

    lateinit var guestsAdapter : ArrayAdapter<GuestData>
    lateinit var token : String
    lateinit var textViewInvite : TextView
    lateinit var layoutInvite : LinearLayout
    var isOwner : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_invite)



        token = getToken()

        textViewInvite = findViewById(R.id.textViewInvitation)
        layoutInvite = findViewById(R.id.linearLayoutInvite)

        loadGuests()
        initializeGuestsList()
        setupBottomNavUtils(intent.getStringExtra("houseId").toString(), token)
        setupTopNavUtils(intent.getStringExtra("houseId").toString(), token)



    }

    private val guests : ArrayList<GuestData> = ArrayList()





    private fun loadGuests(){
        val houseId = intent.getStringExtra("houseId")
        Api().get<ArrayList<GuestData>>("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",::getGuestSuccess,token)
    }

    private fun getGuestSuccess(responseCode:Int,listGuests:ArrayList<GuestData>?){
        if (responseCode == 200 && listGuests != null){
            isOwnerHouse(listGuests[0].userLogin)
            guests.clear()
            guests.addAll(listGuests)
            updateGuestsList()

        }
    }

    private fun isOwnerHouse(owner : String){
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val user = sharedPreferences.getString("login", "").toString()
        println("owner "+owner)
        println("user "+user)
        runOnUiThread{ // Modification sur thread principale
            if (owner != user) {

                if (textViewInvite.visibility == View.VISIBLE && layoutInvite.visibility == View.VISIBLE) {
                    textViewInvite.visibility = View.GONE
                    layoutInvite.visibility = View.GONE
                }
            } else {


                    if (textViewInvite.visibility == View.GONE && layoutInvite.visibility == View.GONE) {
                        textViewInvite.visibility = View.VISIBLE
                        layoutInvite.visibility = View.VISIBLE
                    }
                }
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
        println("IS OWNER ?"+isOwner)
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val user = sharedPreferences.getString("login", "").toString()
        listV.adapter = GuestAdapter(this,guests,user){ userLogin ->
            removeGuest(userLogin)
        }
    }

    public fun addGuest(view: View){
        val houseId = intent.getStringExtra("houseId")
        println("addGuest")
        println(houseId)
        println(token)

        val guest: UserData = UserData(findViewById<EditText>(R.id.editTextInviteName).text.toString())
        println(guest)
        Api().post("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",guest,::addGuestSuccess,token)
        findViewById<EditText>(R.id.editTextInviteName).setText("")
    }

    private fun addGuestSuccess(responseCode:Int){
        if (responseCode == 200){
            loadGuests()

        }else{
            println(responseCode)}
    }

    private fun removeGuest(userLogin:String){
        val houseId = intent.getStringExtra("houseId")
        val guest = UserData(userLogin)


        Api().delete("https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users",guest,::removeGuestSuccess,token)
    }

    private fun removeGuestSuccess(responseCode:Int) {
        if (responseCode == 200) {
            loadGuests()
        } else {
            println(responseCode)
        }
    }

    override fun onResume() {
        super.onResume()
        updateSelectedNavItem(findViewById(R.id.bottom_navigation))
        loadGuests()

    }
}