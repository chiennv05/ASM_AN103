package com.example.asm1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView rcMain;
    List<CarModel> listCarModel;
    CarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcMain = findViewById(R.id.rcMain);
        rcMain.setLayoutManager(new LinearLayoutManager(this));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        // Gọi API để lấy danh sách xe
        Call<List<CarModel>> call = apiService.getCars();
        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    listCarModel = response.body();
                    adapter = new CarAdapter(MainActivity.this, listCarModel);
                    rcMain.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });

        // Xử lý sự kiện nhấn vào nút "Thêm xe"
        findViewById(R.id.btn_add).setOnClickListener(v -> showAddCarDialog(apiService));
    }

    private void showAddCarDialog(APIService apiService) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_car, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etYear = dialogView.findViewById(R.id.etYear);
        EditText etBrand = dialogView.findViewById(R.id.etBrand);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString();
            int year = Integer.parseInt(etYear.getText().toString());
            String brand = etBrand.getText().toString();
            int price = Integer.parseInt(etPrice.getText().toString());

            // Tạo đối tượng xe mới với ID null (sẽ được gán sau)
            CarModel newCar = new CarModel(null, name, year, brand, price);

            // Gọi API thêm xe mới
            Call<List<CarModel>> addCall = apiService.addXe(newCar);
            addCall.enqueue(new Callback<List<CarModel>>() {
                @Override
                public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                    if (response.isSuccessful()) {
                        // Cập nhật lại danh sách xe với thông tin mới (bao gồm ID đã được server gán)
                        listCarModel.clear();
                        listCarModel.addAll(response.body());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Thêm xe thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("AddCar", "Lỗi khi thêm xe: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<List<CarModel>> call, Throwable t) {
                    Log.e("AddCar", t.getMessage());
                }
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}
