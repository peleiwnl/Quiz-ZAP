package com.example.factZAP.fetch_trivia

import android.content.Context
import android.content.SharedPreferences
import com.example.factZAP.data_classes.FactResponse
import com.example.factZAP.retrofit_interfaces.FactService
import com.example.factZAP.utilities.Constants
import com.example.factZAP.utilities.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class responsible for fetching random facts from the API-Ninjas service.
 *
 * @property context Application context used for network checks and preferences
 */

class FetchFact(private val context: Context) {

    /** SharedPreferences instance for persisting fact counter */
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "FactPreferences",
        Context.MODE_PRIVATE
    )

    /**
     * retrieves and increments the fact counter.
     * used to assign unique numbers to fetched facts.
     *
     * @return The next fact number in sequence
     */
    private fun getAndIncrementFactNumber(): Int {
        val currentNumber = sharedPreferences.getInt("factCounter", 0)
        sharedPreferences.edit().putInt("factCounter", currentNumber + 1).apply()
        return currentNumber + 1
    }

    /**
     * Fetches a random fact from the API-Ninjas service.
     *
     * how it works:
     * 1. it checks network availability
     * 2. it then shows progress indicator
     * 3. it makes an API request
     * 4. it processes response and delivers via callback
     * 5. finally, it handles error cases with appropriate messages
     *
     * @param callback Interface implementation to receive the fetched fact
     */
    fun getFact(callback: FactCallback) {
        if (Constants.hasInternet(context)) {
            val pb = Utils.showProgressBar(context)

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.api-ninjas.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: FactService = retrofit.create(FactService::class.java)
            val getDataCall: Call<List<FactResponse>> = service.getFact()

            getDataCall.enqueue(object : Callback<List<FactResponse>> {
                override fun onResponse(
                    call: Call<List<FactResponse>>,
                    response: Response<List<FactResponse>>
                ) {
                    pb.cancel()
                    if (response.isSuccessful) {
                        val factList = response.body()
                        if (!factList.isNullOrEmpty()) {
                            val factNumber = getAndIncrementFactNumber()
                            callback.onFactFetched(factNumber, factList[0].fact)
                        } else {
                            Utils.showToast(context, "No fact received")
                        }
                    } else {
                        Utils.showToast(context, "Error Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<FactResponse>>, t: Throwable) {
                    pb.cancel()
                    Utils.showToast(context, "API Call Failure")
                }
            })
        } else {
            Utils.showToast(context, "Network is Not Available")
        }
    }


    /**
     * callback interface for delivering fetched facts.
     *
     * Implementers receive:
     * - Fact number (unique identifier)
     * - Fact content (actual fact text)
     */
    interface FactCallback {
        fun onFactFetched(factNumber: Int, fact: String)
    }
}