package com.jatmika.e_complaintrangkasbitung.API;

import com.jatmika.e_complaintrangkasbitung.Model.DataBerita;
import com.jatmika.e_complaintrangkasbitung.Model.DataUser;
import com.jatmika.e_complaintrangkasbitung.Model.Komentar;
import com.jatmika.e_complaintrangkasbitung.Model.Komplain;
import com.jatmika.e_complaintrangkasbitung.Model.Penduduk;
import com.jatmika.e_complaintrangkasbitung.Model.Suka;
import com.jatmika.e_complaintrangkasbitung.Model.TokenApi;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface API {
    @POST("/api/sanctum/token")
    @FormUrlEncoded
    Call<TokenApi> login(@Field("email") String username, @Field("password") String password, @Field("device_name") String devices_name);
    @GET("/api/komplain/by/{kategori}")
    Call<List<Komplain>> getComplain(@Header("Authorization") String token, @Path("kategori") String kategori);
    @GET("/api/komplain/pengguna/{id}")
    Call<List<Komplain>> getComplainByPengguna(@Header("Authorization") String token, @Path("id") String id);
    @GET("/api/balasan/{idKomplain}")
    Call<List<Komentar>> getComentar(@Header("Authorization") String token, @Path("idKomplain") String idKomplain);
    @POST("/api/balasan")
    @FormUrlEncoded
    Call<Komentar> addComentar(@Header("Authorization") String token, @Field("id_komplain") String id_komplain, @Field("balasan") String balasan);
    @POST("/api/status-komplain")
    @FormUrlEncoded
    Call<ResponseBody> updateStatusComplain(@Header("Authorization") String token, @Field("id_komplain") String id_komplain, @Field("nama_pemeroses") String nama_pemeroses, @Field("pesan") String pesan, @Field("status") String status);
    @Multipart
    @POST("/api/komplain")
    Call<ResponseBody> postComplain(@Header("Authorization") String token, @Part("alamat") RequestBody alamat, @Part("berkas") RequestBody berkas, @Part("isi") RequestBody isi, @Part("kategori")
                                    RequestBody kategori, @Part("status") RequestBody status, @Part("no_komplain") RequestBody no_komplain, @Part MultipartBody.Part foto);

    @GET("/api/penduduk/{id}")
    Call<Penduduk> getPenduduk(@Header("Authorization") String token, @Path("id") Integer id);
    @GET("/api/status-suka/{id}")
    Call<ResponseBody> checkStatusLike(@Header("Authorization") String token, @Path("id") String id);
    @POST("/api/suka")
    @FormUrlEncoded
    Call<ResponseBody> addLike(@Header("Authorization") String token, @Field("id_komplain") String id_komplain);
    @POST("/api/logout")
    Call<ResponseBody> logout(@Header("Authorization") String token);
    @GET("/api/berita")
    Call<List<DataBerita>> getBerita(@Header("Authorization") String token);
    @POST("/api/berita-balasan")
    @FormUrlEncoded
    Call<ResponseBody> addComentarBerita(@Header("Authorization") String token, @Field("id_berita") String id_berita, @Field("balasan") String balasan);
    @GET("/api/berita-balasan/{id}")
    Call<List<Komentar>> getComentarBerita(@Header("Authorization") String token, @Path("id") String id);
    @GET("/api/penduduk-search/{nik}")
    Call<Penduduk> searchPenduduk(@Path("nik") String nik);
    @POST("/api/pengguna")
    @FormUrlEncoded
    Call<ResponseBody> register(@Field("id_penduduk") String id_penduduk, @Field("email") String email, @Field("password") String password, @Field("no_telpon") String no_telpon);
    @Multipart
    @POST("/api/berkas-komplain")
    Call<ResponseBody> postComplainBerkas(@Header("Authorization") String token, @Part("alamat") RequestBody alamat, @Part("isi") RequestBody isi, @Part("kategori")
            RequestBody kategori, @Part("status") RequestBody status, @Part("no_komplain") RequestBody no_komplain, @Part MultipartBody.Part berkas);
    @GET("/api/pengguna/{id}")
    Call<DataUser> getProfie(@Header("Authorization") String token, @Path("id") String id);
}
