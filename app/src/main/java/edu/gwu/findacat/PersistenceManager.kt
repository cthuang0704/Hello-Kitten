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

    fun saveFavorite(favoriteCats: PetItem){
        val scores = fetchFavorite().toMutableList()
        scores.add(favoriteCats)

        val editor = savePreferences.edit()

        //convert a list of scores into a JSON string
        val moshi = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter()).build()
        val listType = Types.newParameterizedType(List::class.java, PetItem::class.java)
        val jsonAdapter = moshi.adapter<List<PetItem>>(listType)
        val jsonString = jsonAdapter.toJson(scores)

        editor.putString(FAVORITE_KEY, jsonString)
        editor.apply()

    }

    fun fetchFavorite(): List<PetItem> {
        val jsonString = savePreferences.getString(FAVORITE_KEY, null)

        //if null, this means no previous scores, so create an empty array list
        if (jsonString == null) {
            return arrayListOf<PetItem>()
        } else {
            //existing scores, so convert the scores JSON string into Score objects, using Moshi
            val listType = Types.newParameterizedType(List::class.java, PetItem::class.java)
            val moshi = Moshi.Builder()
                    .add(Date::class.java, Rfc3339DateJsonAdapter())
                    .build()
            val jsonAdapter = moshi.adapter<List<PetItem>>(listType)

            var favorites: List<PetItem>? = emptyList<PetItem>()
            try {
                favorites = jsonAdapter.fromJson(jsonString)
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, e.message)
            }

            if (favorites != null) {
                return favorites
            }
                else {
                    return emptyList<PetItem>()
                }
            }

        }


    }