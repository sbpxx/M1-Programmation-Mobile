package com.sidali.projet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class MaisonAdapter(
    private var context : Context,
    private var dataSource : ArrayList<MaisonData>
): BaseAdapter(){
    private val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
        val view = inflater.inflate(R.layout.maison_layout,parent,false)

        val maisonTxt = view.findViewById<TextView>(R.id.textView)

        val maison = getItem(position) as MaisonData

        maisonTxt.text = "maison de "+maison.houseId.toString()

        return view
    }


}