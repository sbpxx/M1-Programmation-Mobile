package com.sidali.projet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.androidtp2.Api
import com.sidali.projet.R
import com.sidali.projet.dataClass.GuestData
import com.sidali.projet.dataClass.HouseData

class MaisonAdapter(
    private var context: Context,
    private var dataSource: ArrayList<HouseData>,
    private var token: String
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var owner: String = ""

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.houses_adapter, parent, false)
        val maisonTxt = view.findViewById<TextView>(R.id.textView)
        val crown = view.findViewById<ImageView>(R.id.ic_maisonCrown)
        val maison = getItem(position) as HouseData

        if (position == 0) {
            maisonTxt.text = "Votre Maison"
            crown.visibility = View.VISIBLE
        } else {
            crown.visibility = View.GONE
            getMaisonOwner(maison.houseId.toString(), token)
            maisonTxt.text = "Maison de ${owner}"
        }

        return view
    }

    private fun getMaisonOwner(houseId: String, token: String) {
        val maisonUrl = "https://polyhome.lesmoulinsdudev.com/api/houses/$houseId/users"
        Api().get(maisonUrl, ::getMaisonOwnerSuccess, token)
    }

    private fun getMaisonOwnerSuccess(responseCode: Int, listGuests: ArrayList<GuestData>?) {
        if (responseCode == 200 && listGuests != null) {
            owner = listGuests[0].userLogin
        }
    }
}
