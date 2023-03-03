package com.example.caboconsultas.io

import com.example.caboconsultas.io.response.LoginResponse
import com.example.caboconsultas.io.response.SimpleResponse
import com.example.caboconsultas.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @GET("userinfo")
    @Headers("Accept: application/json")
    fun getUserInfo(@Header("Authorization") authHeader: String): Call<User>

    @POST("userinfo")
    @Headers("Accept: application/json")
    fun postUserInfo(
        @Header("Authorization") authHeader: String,
        @Query("name") name: String,
        @Query("phone") phone: String,
        @Query("address") address: String
    ): Call<Void>

    @GET("specialties")
    fun getSpecialties(): Call<ArrayList<Specialty>>

    @GET("specialties/{specialty}/doctors")
    fun getDoctors(@Path("specialty") specialtyId: Int): Call<ArrayList<Doctor>>

    @GET("schedule/hours")
    fun getHours(@Query("doctor_id") doctorId: Int, @Query("date") date: String):
            Call<Schedule>

    @POST("login")
    fun postLogin(@Query("email") email: String, @Query("password") date: String):
            Call<LoginResponse>

    @POST("logout")
    fun postLogout(@Header("Authorization") authHeader: String):
            Call<Void>

    @GET("appointments")
    fun getAppointments(@Header("Authorization") authHeader: String): Call<ArrayList<Appointment>>

    @POST("appointments")
    @Headers("Accept: application/json")
    fun storeAppointment(
        @Header("Authorization") authHeader: String,
        @Query("description") description: String,
        @Query("specialty_id") specialtyId: Int,
        @Query("doctor_id") doctorId: Int,
        @Query("scheduled_date") scheduledDate: String,
        @Query("scheduled_time") scheduleTime: String,
        @Query("type") type: String
    ): Call<SimpleResponse>

    @POST("register")
    @Headers("Accept: application/json")
    fun postRegister(
        @Query("name") name: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("password_confirmation") passwordConfirmation: String,
    ): Call<LoginResponse>

    @POST("fcm/token")
    fun storeToken(
        @Header("Authorization") authHeader: String,
        @Query("device_token") token: String
    ): Call<Void>


    companion object Factory {
        private const val BASE_URL="http://143.244.181.170/api/"

        fun create(): ApiService{
            val interceptor= HttpLoggingInterceptor()
            interceptor.level= HttpLoggingInterceptor.Level.BODY
            val client= OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit= Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client
                )
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}