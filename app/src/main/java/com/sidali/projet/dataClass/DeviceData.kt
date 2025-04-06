package com.sidali.projet.dataClass

// Data class pour les informations d'un appareil

data class DeviceData(
val id:String,
    val type:String,
    val availableCommands: ArrayList<String>,
    val opening : Float?,
    val power : Int?
)
