package com.example.exo6

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Seance")
data class Seance (@PrimaryKey var id : Int, var jour : String, var semaine : String,  var heureDebut : String, var heureFin : String, var module : String, var salle : String, var enseignant : String){
}