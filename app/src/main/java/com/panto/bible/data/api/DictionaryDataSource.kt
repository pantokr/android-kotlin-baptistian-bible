package com.panto.bible.data.api

import com.panto.bible.data.model.api.Dictionary
import retrofit2.Response

class DictionaryDataSource {
    private val apiService = RetrofitClient.getInstance().create(DictionaryService::class.java)

    suspend fun getDictionary(query: String): Dictionary? {
        return try {
            val response: Response<Dictionary> = apiService.getDictionary(query)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}