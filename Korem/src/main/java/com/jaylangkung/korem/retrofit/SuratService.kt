package com.jaylangkung.korem.retrofit

import com.jaylangkung.korem.dataClass.SuratKeluarResponse
import com.jaylangkung.korem.dataClass.SuratMasukResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SuratService {
    @GET("surat/getSuratMasuk")
    fun getSuratMasuk(
        @Query("sumber_surat") sumber: String,
        @Query("bentuk") bentuk: String,
        @Query("disposisi") disposisi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<SuratMasukResponse>

    @GET("surat/getSuratKeluar")
    fun getSuratKeluar(
        @Query("bentuk") bentuk: String,
        @Query("disposisi") disposisi: String,
        @Header("Authorization") tokenAuth: String
    ): Call<SuratKeluarResponse>
}