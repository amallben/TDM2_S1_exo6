package com.example.exo6

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emplois.R
import kotlinx.android.synthetic.main.activity_main.*

class SeanceActivity : AppCompatActivity() {

    var dataList = arrayListOf<Seance>()

    lateinit var adapter: SeanceAdapter
    lateinit var layoutManager : LinearLayoutManager

    private lateinit var seanceDatabase : SeanceDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seance)

        seanceDatabase = SeanceDatabase.getDatabase(this)

        val title = intent.getStringExtra("title")
        val value = intent.getStringExtra("value")


        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = SeanceAdapter(this)
        recyclerView.adapter = adapter

        getData(title, value)
    }

    fun getData(title : String, value : String){
        when(title){
            "Jour" -> getJourData(value)
            "Semaine" -> getWeekData(value)
            "Module" -> getModuleData(value)
            "Salle" -> getSalleData(value)
            "Enseignant" -> getEnsData(value)
        }
    }


    fun getWeekData(week : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesByWeek(week))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getJourData(day : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesByDay(day))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getModuleData(module : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesByModule(module))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getEnsData(ens : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesByEnseignant(ens))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getSalleData(salle : String){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().findSeancesBySalle(salle))
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }


    class SeanceAdapter(val activity : SeanceActivity) : RecyclerView.Adapter<SeanceAdapter.SeanceViewHolder>(){
        class SeanceViewHolder(v : View) : RecyclerView.ViewHolder(v){
            val Module = v.findViewById<TextView>(R.id.module)
            val Date = v.findViewById<TextView>(R.id.date)
            val HeureD = v.findViewById<TextView>(R.id.heurD)
            val HeureF = v.findViewById<TextView>(R.id.heureF)
            val Salle = v.findViewById<TextView>(R.id.salle)
            val Ens = v.findViewById<TextView>(R.id.EnsTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeanceViewHolder {
            return SeanceViewHolder(LayoutInflater.from(activity).inflate(R.layout.seance_layout, parent, false))
        }

        override fun getItemCount(): Int {
            return activity.dataList.size
        }

        override fun onBindViewHolder(holder: SeanceViewHolder, position: Int) {
            val seance = activity.dataList[position]

            holder.Date.text = seance.jour
            holder.Ens.text = seance.enseignant
            holder.HeureD.text = seance.heureDebut
            holder.HeureF.text = seance.heureFin
            holder.Module.text = seance.module
            holder.Salle.text = seance.salle

        }
    }
}
