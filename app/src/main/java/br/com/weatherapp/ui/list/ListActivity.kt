package br.com.weatherapp.ui.list

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import br.com.weatherapp.R
import br.com.weatherapp.api.RetrofitManager
import br.com.weatherapp.common.Constants
import br.com.weatherapp.data.RoomManager
import br.com.weatherapp.entity.*
import br.com.weatherapp.ui.setting.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListActivity : AppCompatActivity(), Callback<FindResult> {

    private lateinit var asyncTask: FavoritesAsyncTask

    private val sp : SharedPreferences by lazy {
        getSharedPreferences(Constants.PREFS, Context.MODE_PRIVATE)
    }

    val db: RoomManager? by lazy {
        RoomManager.getInstance(this)
    }

    private val adapter: ListAdapter by lazy {
        ListAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        initializeLists()
    }

    private fun initRecyclerView() {
        rvWeather.adapter = adapter
        //rvWeather.layoutManager = LinearLayoutManager(this)
    }

    private fun btnSearch(){
        btnSearch.setOnClickListener {
            if (isDeviceConnected()) getCities()
            else Toast.makeText(this, "Device not connected", Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeLists(){
        edtCity.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isEmpty()) {
                    btnSearch.isEnabled = false
                    loadFavoritesCities()

                } else {
                    btnSearch.isEnabled = true
                    btnSearch()
                }
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setUnit(unit:String){
        if(unit==UnitSettings.CELSIUS.unit){
            sp.edit {
                putString("tempType", "ºC")
            }
        }else{
            sp.edit {
                putString("tempType", "ºF")
            }
        }
    }

    private fun getCities() {
        progressBar.visibility = View.VISIBLE
        val isCelsius = sp.getBoolean(Constants.PREFS_TEMP, true)
        val isEnglish = sp.getBoolean(Constants.PREFS_LANG, true)

        val unit = if(isCelsius)UnitSettings.CELSIUS.unit
                            else UnitSettings.FAHRENHEIT.unit

        val lang = if(isEnglish) LangSettings.ENGLISH.lang
                            else LangSettings.PORTUGUESE.lang

        setUnit(unit)

        val call = RetrofitManager.getWeatherService()
            .find(edtCity.text.toString(), unit, lang,  Constants.API_KEY)

        call.enqueue(this)
    }

    private fun getCitiesFavorites(favorites: List<Int>?) =
        if(favorites != null && favorites.isNotEmpty()){
            progressBar.visibility = View.VISIBLE
            val isCelsius = sp.getBoolean(Constants.PREFS_TEMP, true)
            val unit = if(isCelsius) UnitSettings.CELSIUS.unit
                        else UnitSettings.FAHRENHEIT.unit

            val isEnglish = sp.getBoolean(Constants.PREFS_LANG, true)
            val lang = if(isEnglish) LangSettings.ENGLISH.lang
                        else LangSettings.PORTUGUESE.lang

            setUnit(unit)

            val favoritesIds = TextUtils.join(",", favorites)

            val call = RetrofitManager.getWeatherService()
                .findFavorites(favoritesIds, unit, lang, Constants.API_KEY)
            call.enqueue(this)

        }else adapter.updateData(null)

    fun switchFavorite(city: City){
        SwitchFavoriteAsync(this, city).execute()
    }

    private fun loadFavoritesCities(){
        asyncTask = FavoritesAsyncTask(this, object: TaskListener {
            override fun onTaskComplete(favorites: List<Int>?) {
                getCitiesFavorites(favorites)
            }
        })
        asyncTask.execute()
    }

    private fun getFavorites(){
        asyncTask = FavoritesAsyncTask(this, object: TaskListener {
            override fun onTaskComplete(favorites: List<Int>?) {
                favorites?.let { adapter.updateFavorites(it) }
            }
        })
        asyncTask.execute()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting_item) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    private fun isDeviceConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    override fun onFailure(call: Call<FindResult>, t: Throwable) {
        Log.e("WELL", "Error", t)
        progressBar.visibility = View.GONE
    }

    override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
        if (response.isSuccessful) {
            adapter.updateData(response.body()?.list)
            getFavorites()
        }
        progressBar.visibility = View.GONE
    }

    class SwitchFavoriteAsync(private val context: Context, private val city: City)
        : AsyncTask<Void, Void, Boolean>() {

        private val db = RoomManager.getInstance(context)

        override fun doInBackground(vararg p0: Void?): Boolean {
            var favorite: Favorite? = db?.getCityDao()?.favoriteById(city.id)
            return if(favorite == null){
                favorite = Favorite(this.city.id, this.city.name)
                db?.getCityDao()?.insertFavorite(favorite)
                true
            }else{
                db?.getCityDao()?.deleteFavorite(favorite)
                false

            }
        }

        override fun onPostExecute(saved: Boolean) {
            super.onPostExecute(saved)
            val activity = context as ListActivity
            activity.getFavorites()
            if(activity.edtCity.text.trim().isEmpty())
                activity.loadFavoritesCities()

        }
    }

    class FavoritesAsyncTask (Context: Context, private val Listener: TaskListener): AsyncTask<Void, Void, List<Int>?>(){

        private val db = RoomManager.getInstance(Context)

        override fun doInBackground(vararg p0: Void?): List<Int>? {
            return db?.getCityDao()?.allFavoritesIds()
        }

        override fun onPostExecute(favorites: List<Int>?) {
            super.onPostExecute(favorites)
            Listener.onTaskComplete(favorites)
        }
    }

    interface TaskListener{
        fun onTaskComplete(favorites: List<Int>?)
    }

}

