package com.example.exo6

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emplois.R
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream


class MainActivity : AppCompatActivity() {


    var dataList = arrayListOf<DataCount>()
    var currentPosition = 0
    var Spinnerch : Int = 0
    lateinit var layoutManager : LinearLayoutManager
    lateinit var adapter: GlobalDataAdapter
    private lateinit var seanceDatabase : SeanceDatabase




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seanceDatabase = SeanceDatabase.getDatabase(this)

        ArrayAdapter.createFromResource(
            this,
            R.array.data_menu,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
            Spinner.adapter = adapter
        }

        Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(++Spinnerch > 1) {
                    currentPosition = position
                    when(position){
                        0 -> getJour()
                        1 -> getSemaine()
                        2 -> getModule()
                        3 -> getSalle()
                        4 -> getEns()
                    }
                }
            }
        }

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = GlobalDataAdapter(this)
        recyclerView.adapter = adapter

        initData()
    }

    fun initData(){

        val jsonString = loadJson(this)


        val jsonObject = JSONObject(jsonString)

        val seances = jsonObject.getJSONArray("seances")

        Toast.makeText(this, "Data is loaded succefully", Toast.LENGTH_LONG).show()

        if (seances != null){
            try {
                for (i in 0 until seances.length()){
                    val seance = seances.getJSONObject(i)
                    val id = seance.getInt("seanceId")
                    val jour = seance.getString("jour")
                    val semaine = seance.getString("semaine")
                    val hD = seance.getString("heureD")
                    val hF = seance.getString("heureF")
                    val module = seance.getString("module")
                    val salle = seance.getString("salle")
                    val ens = seance.getString("enseignant")

                    val newS = Seance(id, jour, semaine, hD, hF, module, salle, ens)
                    addSeance(newS)
                }
            }catch (e : JSONException){
                e.printStackTrace()
            }

            getJour()
        }

    }


    private fun loadJson(context: Context): String? {
        var input: InputStream? = null
        var jsonString: String

        try {
            // Create InputStream
            input = context.assets.open("data.json")

            val size = input.available()

            // Create a buffer with the size
            val buffer = ByteArray(size)

            // Read data from InputStream into the Buffer
            input.read(buffer)

            // Create a json String
            jsonString = String(buffer)
            return jsonString;
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            // Must close the stream
            input?.close()
        }

        return null
    }

    fun addSeance(seance: Seance) {
        AppExecutors.instance!!.diskIO().execute {
            seanceDatabase.seanceDao().addSeance(seance)

        }
    }



    fun getSemaine(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getWeekList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }


    fun getModule(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getModuleList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }

    fun getJour(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getDayList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }
    fun getSalle(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getSalleList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }
    fun getEns(){
        AppExecutors.instance!!.diskIO().execute(Runnable {
            dataList.clear()
            dataList.addAll(seanceDatabase.seanceDao().getEnsList())
            //refreshList()
            AppExecutors.instance!!.mainThread().execute( Runnable {
                adapter.notifyDataSetChanged()
            })
        })
    }





    class GlobalDataAdapter(val activity : MainActivity) : RecyclerView.Adapter<GlobalDataAdapter.GlobalDataViewHolder>(){
        class GlobalDataViewHolder(v : View) : RecyclerView.ViewHolder(v){
            val titlecontent = v.findViewById<TextView>(R.id.title_content)
            val nbrSeances = v.findViewById<TextView>(R.id.nbrSeanceContent)
            val title =  v.findViewById<TextView>(R.id.title_data)
            val layout = v.findViewById<RelativeLayout>(R.id.dataItemLayout)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlobalDataViewHolder {
            return GlobalDataViewHolder(LayoutInflater.from(activity).inflate(R.layout.semaine_layout, parent, false))
        }

        override fun getItemCount(): Int {
            return activity.dataList.size
        }

        override fun onBindViewHolder(holder: GlobalDataViewHolder, position: Int) {
            val title = activity.resources.getStringArray(R.array.data_menu)[activity.currentPosition]
            val value = activity.dataList[position].data

            holder.nbrSeances.text = activity.dataList[position].count.toString()
            holder.titlecontent.text = value
            holder.title.text = title

            holder.layout.setOnClickListener {
                val intent = Intent(activity, SeanceActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("value", value)
                activity.startActivity(intent)
            }
        }
    }


}
