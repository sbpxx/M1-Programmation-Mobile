package com.sidali.projet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GuestAdapter(
private var context : Context,
private var dataSource : ArrayList<GuestData>,
private var isOwner : Boolean,
private val deleteCallback: (String) -> Unit
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
        val view = inflater.inflate(R.layout.activity_guest_adapter,parent,false)
        val crown = view.findViewById<ImageView>(R.id.ic_crown)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)
        val guestNameTxt = view.findViewById<TextView>(R.id.textView9)
        val guest = getItem(position) as GuestData
        guestNameTxt.text = guest.userLogin

        if (position == 0){
            deleteButton.visibility = View.GONE
            crown.visibility = View.VISIBLE
        }else{
            deleteButton.visibility = View.VISIBLE
            crown.visibility = View.GONE
            deleteButton.setOnClickListener {
                deleteCallback(guest.userLogin)
            }

            if (isOwner&&position>0){
                deleteButton.visibility = View.VISIBLE
            }else{
                deleteButton.visibility = View.GONE
            }
        }




        deleteButton.setOnClickListener {
            deleteCallback(guest.userLogin)
        }

        return view
    }
}