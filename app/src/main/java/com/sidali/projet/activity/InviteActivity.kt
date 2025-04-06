package com.sidali.projet.activity

import android.os.Bundle
import android.view.View
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
import com.sidali.projet.utils.showApiErrorToast

// Classe pour l'activité d'invitation

class InviteActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var textViewInvite: TextView
    private lateinit var layoutInvite: LinearLayout
    private lateinit var houseId: String
    private lateinit var currentUserLogin: String
    private lateinit var guestsUrl: String
    private lateinit var editTextInviteName: EditText
    private lateinit var listViewGuest: ListView

    private val guests: ArrayList<GuestData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_invite)

        token = getToken()
        houseId = intent.getStringExtra("houseId").toString()
        currentUserLogin = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            .getString("login", "").toString()
        guestsUrl = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users"

        textViewInvite = findViewById(R.id.textViewInvitation)
        layoutInvite = findViewById(R.id.linearLayoutInvite)
        editTextInviteName = findViewById(R.id.editTextInviteName)
        listViewGuest = findViewById(R.id.listViewGuest)

        loadGuests()
        initializeGuestsList()

        setupBottomNavUtils(houseId, token)
        setupTopNavUtils(houseId, token)
    }

    // Fonction pour charger les invités

    private fun loadGuests() {
        Api().get<ArrayList<GuestData>>(guestsUrl, ::getGuestSuccess, token)
    }


    // Fonction pour mettre à jour la liste des invités

    private fun getGuestSuccess(responseCode: Int, listGuests: ArrayList<GuestData>?) {
        if (responseCode == 200 && listGuests != null) {
            isOwnerHouse(listGuests[0].userLogin)
            guests.clear()
            guests.addAll(listGuests)
            updateGuestsList()
        }else{
            runOnUiThread {
                showApiErrorToast(responseCode)
            }
        }
    }

    // Fonction pour vérifier si l'utilisateur est propriétaire de la maison séléctionnée

    private fun isOwnerHouse(owner: String) {
        runOnUiThread {
            val isOwner = owner == currentUserLogin

            // Si l'utilisateur est propriétaire, on affiche le champ d'invitation

            textViewInvite.visibility = if (isOwner) View.VISIBLE else View.GONE
            layoutInvite.visibility = if (isOwner) View.VISIBLE else View.GONE
        }
    }

    // Fonction pour mettre à jour la liste des invités

    private fun updateGuestsList() {
        runOnUiThread {
            (listViewGuest.adapter as GuestAdapter).notifyDataSetChanged()
        }
    }

    // Fonction pour initialiser la liste des invités

    private fun initializeGuestsList() {
        listViewGuest.adapter = GuestAdapter(this, guests, currentUserLogin) { userLogin ->
            removeGuest(userLogin)
        }
    }

    // Fonction pour ajouter un invité

    fun addGuest(view: View) {
        val guest = UserData(editTextInviteName.text.toString())
        Api().post(guestsUrl, guest, ::addGuestSuccess, token)
        editTextInviteName.setText("")
    }

    // Fonction pour supprimer un invité

    private fun removeGuest(userLogin: String) {
        val guest = UserData(userLogin)
        Api().delete(guestsUrl, guest, ::removeGuestSuccess, token)
    }

    // La liste des invités est rechargée après l'ajout ou la suppression d'un invité

    private fun addGuestSuccess(responseCode: Int) {
        if (responseCode == 200) {
            loadGuests()
        } else {
            runOnUiThread {
                showApiErrorToast(responseCode)
            }
        }
    }

    private fun removeGuestSuccess(responseCode: Int) {
        if (responseCode == 200) {
            loadGuests()
        } else {
            runOnUiThread {
                showApiErrorToast(responseCode)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        updateSelectedNavItem(findViewById(R.id.bottom_navigation))
        loadGuests()
    }

    // Sauvegarde de l'entrée de l'utilisateur dans le champ de texte

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        guests.clear()
    }
}
