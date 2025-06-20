package com.example.factZAP.retrofit_interfaces

import com.example.factZAP.data_classes.FactResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Retrofit interface for interacting with the fact API.
 */
interface FactService {
    /**
     * Makes a GET request to the /v1/facts endpoint of the fact API.
     * This method fetches a list of facts from the API.
     */
    @Headers("X-Api-Key: gM8TEIQ2OA9X/eT64UH+Pg==wmvOUFOSvvzbQ3aY")
    @GET("v1/facts")
    fun getFact(): Call<List<FactResponse>>
}