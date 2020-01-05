package br.com.weatherapp.ui.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.weatherapp.R
import br.com.weatherapp.common.Constants.PREFS
import br.com.weatherapp.entity.City
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_weather_layout.view.*


class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var list: List<City>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_weather_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = list?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list?.let {
            holder.bind(it[position])
        }
    }

    fun updateData(list: List<City>?) {
        this.list = list
        notifyDataSetChanged()
    }

    fun updateFavorites(favorites: List<Int>) {
        if(this.list != null){
            this.list?.forEach{
                if(favorites.contains(it.id)) it.favourite = true
            }
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(city: City) {

            itemView.tvWeatherValue.text = city.main.temp.toInt().toString()

            val prefs = itemView.context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            itemView.tvUnit.text = prefs.getString("tempType", null)

            itemView.tvCity.text = "${city.name}, ${city.sys.country}"
            itemView.tvDescription.text = city.weather[0].description
            itemView.tvWind.text = "${city.wind.speed} m/s   |"
            itemView.tvClouds.text = "clouds ${city.clouds.all} %   |"
            itemView.tvPressure.text = "${city.main.pressure} hpa"

            if (city.weather.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load("http://openweathermap.org/img/w/${city.weather[0].icon}.png")
                    .into(itemView.imgWeatherIcon)
            }


            if (city.favourite) itemView.fvButton.setImageResource(android.R.drawable.star_big_on)
                else itemView.fvButton.setImageResource(android.R.drawable.star_big_off)

            itemView.fvButton.setOnClickListener {

                city.favourite = !city.favourite
                if (city.favourite)
                    itemView.fvButton.setImageResource(android.R.drawable.star_big_on)
                   else itemView.fvButton.setImageResource(android.R.drawable.star_big_off)
                        (itemView.context as ListActivity).switchFavorite(city)
            }


        }
    }

}