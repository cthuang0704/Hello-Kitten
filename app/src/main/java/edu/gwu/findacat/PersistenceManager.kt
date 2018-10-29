package edu.gwu.findacat

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import edu.gwu.findacat.petfinder.PetItem

class PersistenceManager(private val context: Context){
    private val savePreferences: SharedPreferences

    init {
        savePreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun saveFavorite(favoriteCats: PetItem){

    }

    fun fetchFavorite(): List<PetItem> {




        return emptyList()
    }


}