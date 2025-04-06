package com.sidali.projet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import com.sidali.projet.R
import com.sidali.projet.dataClass.DeviceData

class RemoteAdapter(
    private var context: Context,
    private var dataSource: ArrayList<DeviceData>,
    private var houseId: String,
    private var token: String,
    private val onDeviceClick: (String, ArrayList<String>, String, Int?, Float?) -> Unit
) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
        val device = getItem(position) as DeviceData
        val view = inflater.inflate(R.layout.devices_adapter, parent, false)

        val button = view.findViewById<Button>(R.id.deviceButton)
        val deviceIcon = view.findViewById<ImageView>(R.id.deviceIcon)
        val openingPercentage: Float? = device.opening?.times(100)

        button.text = device.id

        if (device.type == "light" && device.power == 1) {
            deviceIcon.setImageResource(R.drawable.ic_light_on)
        } else if (device.type == "light" && device.power == 0) {
            deviceIcon.setImageResource(R.drawable.ic_light_off)
        } else if (device.type == "rolling shutter" && openingPercentage == 0f) {
            deviceIcon.setImageResource(R.drawable.ic_shutter_closed)
        } else if (device.type == "rolling shutter" && openingPercentage == 100f) {
            deviceIcon.setImageResource(R.drawable.ic_shutter_open)
        } else if (device.type == "rolling shutter" && openingPercentage != 0f && openingPercentage != 100f) {
            deviceIcon.setImageResource(R.drawable.ic_shutter_middle)
        } else if (device.type == "garage door" && openingPercentage == 0f) {
            deviceIcon.setImageResource(R.drawable.ic_garage_closed)
        } else if (device.type == "garage door" && openingPercentage == 100f) {
            deviceIcon.setImageResource(R.drawable.ic_garage_open)
        } else if (device.type == "garage door" && openingPercentage != 0f && openingPercentage != 100f) {
            deviceIcon.setImageResource(R.drawable.ic_garage_middle)
        }

        button.setOnClickListener {
            onDeviceClick(device.id, device.availableCommands, device.type, device.power, device.opening)
        }

        return view
    }
}
