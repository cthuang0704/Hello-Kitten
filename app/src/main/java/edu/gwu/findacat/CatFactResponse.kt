package edu.gwu.findacat

import com.squareup.moshi.Json


data class CatFactResponse(

	@Json(name="fact")
	val fact: String,

	@Json(name="length")
	val length: Int? = null
)