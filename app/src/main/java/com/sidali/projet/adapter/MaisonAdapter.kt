package com.sidali.projet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sidali.projet.R
import com.sidali.projet.dataClass.HouseData

class MaisonAdapter(
    private var context: Context,
    private var dataSource: ArrayList<HouseData>,
    private var houseOwners: Map<String, String>,
    private var token: String
) : BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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

        // La première maison est celle de l'utilisateur actuel, on affiche une couronne
        // sinon on affiche le nom de l'utilisateur qui la possède

        if (position == 0) {
            maisonTxt.text = "Votre Maison"
            crown.visibility = View.VISIBLE
        } else {
            crown.visibility = View.GONE
            val owner = houseOwners[maison.houseId.toString()]
            maisonTxt.text = "Maison de $owner"
        }

        return view
    }


}