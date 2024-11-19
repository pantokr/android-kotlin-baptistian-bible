package com.panto.bible.data.api

import com.panto.bible.data.model.api.Dictionary
import retrofit2.http.GET
import retrofit2.http.Query

interface DictionaryService {
    @GET("v1/search/encyc.json")

    suspend fun getDictionary(
        @Query("query") query: String = "",
        @Query("display") display: String = "100",
        @Query("start") start: String = "1",
        @Query("sort") sort: String = "sim",

        ): retrofit2.Response<Dictionary>
}