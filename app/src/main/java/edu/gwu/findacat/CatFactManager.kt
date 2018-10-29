package edu.gwu.findacat

import android.content.Context
import android.provider.SyncStateContract
import android.util.Log
import android.widget.TextView
import com.squareup.picasso.Picasso
import edu.gwu.findacat.R.id.catsfact_textView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class CatFactManager(private val context: Context, private val textView: TextView) {
    private val TAG = "CatFactManager"
    var catFactCompletionListener: CatFactCompletionListener ?= null

    interface CatFactCompletionListener{
        fun catFactLoaded(fact: String)
        fun catFactNotLoaded()
    }

    interface ApiEndpointInterface {
        @GET("fact")
        fun getFactResponse(): Call<CatFactResponse>
    }

    fun searchStrings() {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://catfact.ninja/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        val apiEndpoint = retrofit.create(ApiEndpointInterface::class.java)

        apiEndpoint.getFactResponse().enqueue(object: Callback<CatFactResponse> {
            override fun onFailure(call: Call<CatFactResponse>, t: Throwable) {
                Log.d(TAG, "API call failed!")
                catFactCompletionListener?.catFactNotLoaded()
            }

            override fun onResponse(call: Call<CatFactResponse>, response: Response<CatFactResponse>) {

                val catFactResponseBody = response.body()

                if(catFactResponseBody != null){
                    catFactCompletionListener?.catFactLoaded(catFactResponseBody.fact)
                    Log.d(TAG, "CatFact API call successful!")
                }else{
                    catFactCompletionListener?.catFactNotLoaded()
                }

            }
        })
    }

}