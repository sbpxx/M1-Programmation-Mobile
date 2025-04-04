package com.sidali.projet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button

class RemoteAdapter(
    private var context: Context,
    private var dataSource: ArrayList<DeviceData>,
    private var houseId: String,
    private var token: String,
    private val onDeviceClick: (String, ArrayList<String>,String, Int?, Float?) -> Unit, // Ajout du callbackString) -> Unit // Ajout du callback

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
        val view = inflater.inflate(R.layout.devices_layout,parent,false)
        val button = view.findViewById<Button>(R.id.button3)
        val device = getItem(position) as DeviceData
        button.text= device.id
        button.setOnClickListener {
             println("test click btn "+device.type)
             println("test click btn "+device.availableCommands)
             println("test click btn power "+device.power)
             println("test click btn opening "+device.opening)
            onDeviceClick(device.id,device.availableCommands,device.type,device.power,device.opening)
        }



        return view
    }



}
