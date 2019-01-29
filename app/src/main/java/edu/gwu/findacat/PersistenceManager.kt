package edu.gwu.findacat

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import edu.gwu.findacat.petfinder.PetItem
import java.io.IOException
import java.util.*

class PersistenceManager(private val context: Context){
    private val savePreferences: SharedPreferences

    companion object {
        val FAVORITE_KEY = "favoriteData"

    }

    init {
        savePreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun saveFavorite(petItem: PetItem){
        val petItems = fetchFavorite().toMutableList()

        petItems.add(petItem)

        val editor = savePreferences.edit()

        //convert a list of petItems into a JSON
        val moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, PetItem::class.java)
        val jsonAdapter = moshi.adapter<List<PetItem>>(listType)
        val jsonString = jsonAdapter.toJson(petItems)

        editor.putString(FAVORITE_KEY, jsonString)

        editor.apply()

    }

    fun removeFavorite(petItem: PetItem){
        val petItems = fetchFavorite().toMutableList()

        petItems.remove(petItem)

        val editor = savePreferences.edit()

        //convert a list of petItems into a JSON
        val moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, PetItem::class.java)
        val jsonAdapter = moshi.adapter<List<PetItem>>(listType)
        val jsonString = jsonAdapter.toJson(petItems)

        editor.putString(FAVORITE_KEY, jsonString)

        editor.apply()
    }

    fun fetchFavorite(): List<PetItem> {
        val jsonString = savePreferences.getString(FAVORITE_KEY, null)

        //if null, this means no previous scores, so create an empty array list
        if (jsonString == null) {
            return arrayListOf<PetItem>()
        } else {
            //existing favorites, so convert the scores JSON string into Score objects, using Moshi
            val listType = Types.newParameterizedType(List::class.java, PetItem::class.java)
            val moshi = Moshi.Builder().build()
            val jsonAdapter = moshi.adapter<List<PetItem>>(listType)

            var petItems: List<PetItem>? = emptyList<PetItem>()
            try {
                petItems = jsonAdapter.fromJson(jsonString)
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, e.message)
            }

            if (petItems != null) {
                return petItems
            } else {
                return emptyList<PetItem>()
            }

        }
    }

    fun ifFavoritePetItem(petItem: PetItem): Boolean{
        return fetchFavorite().contains(petItem)
    }
}