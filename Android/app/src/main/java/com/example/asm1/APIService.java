package com.example.asm1;



import retrofit2.Call;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {
    String DOMAIN = "http://10.0.2.2:3000";

    @GET("/api/list")
    Call<List<CarModel>> getCars(); // Lấy danh sách xe

    @POST("/api/add_xe")
    Call<List<CarModel>> addXe(@Body CarModel xe); // Thêm xe mới

    @DELETE("/api/xoa_xe/{id}")
    Call<List<CarModel>> xoaXe(@Path("id") String id); // Xóa xe

    @PUT("/api/update/{id}")
    Call<List<CarModel>> updateXe(@Path("id") String id, @Body CarModel car); // Cập nhật xe
}
