package network

import network.interfaces.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query

interface MainServerApi {
    @POST("/users/login")
    fun login(@Body loginData: LoginData): Call<LoginResponse>

    @POST("/users/signup")
    fun signup(@Body signupData: SignupDataRequest): Call<SignupResponse>

    @GET("/users/confirm")
    fun confirm(@Query("id") id: Int, @Query("token") token: String): Call<LoginResponse>

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