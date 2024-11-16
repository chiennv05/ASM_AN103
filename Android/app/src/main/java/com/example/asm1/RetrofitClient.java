package com.example.asm1;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private APIService requestInterface;

    // URL API được khai báo trong ApiServices
    public static final String BASE_URL = "http://10.0.2.2:3000/"; // Hoặc thay thế với URL API của bạn

    // Constructor RetrofitClient để khởi tạo Retrofit
    public RetrofitClient() {
        // Khởi tạo Retrofit với BASE_URL và Gson Converter
        requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService.class);  // Khởi tạo ApiServices
    }

    // Phương thức trả về API interface để sử dụng trong các hoạt động gọi API
    public APIService callAPI() {
        return requestInterface;
    }
}
