package br.com.weatherapp.data

import androidx.room.*
import br.com.weatherapp.entity.Favorite

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favorite: Favorite)

    @Delete
    fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM TB_FAVORITE WHERE id = :id")
    fun favoriteById(id: Int): Favorite

    @Query("SELECT * FROM TB_FAVORITE")
    fun allFavorites(): List<Favorite>

    @Query("SELECT id FROM TB_FAVORITE")
    fun allFavoritesIds(): List<Int>

}