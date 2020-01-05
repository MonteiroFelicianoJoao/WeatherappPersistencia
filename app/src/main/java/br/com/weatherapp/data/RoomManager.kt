package br.com.weatherapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.weatherapp.entity.Favorite


@Database(entities = [Favorite::class], version = 1)
abstract class RoomManager : RoomDatabase() {

    abstract fun getCityDao() : CityDao

    companion object {
        private var INSTANCE : RoomManager? = null

        fun getInstance(context: Context) : RoomManager? {
            synchronized(RoomManager::class.java) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        RoomManager::class.java,
                        "Weather.db")
//                        .allowMainThreadQueries()
                        .build()
                }
                return this.INSTANCE
            }
        }
    }
}