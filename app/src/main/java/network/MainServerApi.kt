package network

import network.interfaces.LoginData
import network.interfaces.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface MainServerApi {
    @POST("/users/login")
    fun login(@Body loginData: LoginData): Call<LoginResponse>

    companion object {
        private const val apiAddress = "http://10.0.2.2:8010/"
        fun getApi(): MainServerApi? {
            return try {
                val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(
                        RxJava2CallAdapterFactory.create()
                    )
                    .addConverterFactory(
                        GsonConverterFactory.create()
                    )
                    .baseUrl(apiAddress)
                    .build()

                retrofit.create(MainServerApi::class.java)
            } catch(e: Throwable) {
                null
            }
        }
    }
}