package com.example.asm1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<CarModel> carModelsList;
    private Context context;
    private APIService apiService;

    public CarAdapter(Context context, List<CarModel> carModelsList) {
        this.context = context;
        this.carModelsList = carModelsList;

        // Khởi tạo RetrofitClient và lấy APIService
        RetrofitClient retrofitClient = new RetrofitClient();
        apiService = retrofitClient.callAPI();
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_car
        View view = LayoutInflater.from(context).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarModel car = carModelsList.get(position);

        // Set dữ liệu vào các TextView

        holder.tvId.setText(String.valueOf(car.get_id())); // Set car ID (hidden view)
        holder.tvName.setText(car.getTen());
        holder.tvNamSX.setText(String.valueOf(car.getNamSX()));
        holder.tvHang.setText(car.getHang());
        holder.tvGia.setText(String.valueOf(car.getGia()));

        // Lắng nghe sự kiện click của nút cập nhật
        holder.btnUpdate.setOnClickListener(v -> {
            // Tạo view dialog với các trường chỉnh sửa
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_car, null);

            // Kiểm tra null trước khi ánh xạ
            EditText etName = dialogView.findViewById(R.id.etName_ud);
            EditText etNamSX = dialogView.findViewById(R.id.etNamSX_ud);
            EditText etHang = dialogView.findViewById(R.id.etHang_ud);
            EditText etGia = dialogView.findViewById(R.id.etGia_ud);

            // Điền sẵn thông tin xe vào các trường chỉnh sửa
            etName.setText(car.getTen());
            etNamSX.setText(String.valueOf(car.getNamSX()));
            etHang.setText(car.getHang());
            etGia.setText(String.valueOf(car.getGia()));

            // Tạo AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Cập nhật thông tin xe");
            builder.setView(dialogView);

            builder.setPositiveButton("Cập nhật", (dialog, which) -> {
                String updatedName = etName.getText().toString();
                int updatedNamSX = Integer.parseInt(etNamSX.getText().toString());
                String updatedHang = etHang.getText().toString();
                int updatedGia = Integer.parseInt(etGia.getText().toString());

                // Tạo đối tượng CarModel với _id và các thông tin đã cập nhật
                CarModel updatedCar = new CarModel(
                        car.get_id(),    // Sử dụng _id hiện tại của xe
                        updatedName,
                        updatedNamSX,
                        updatedHang,
                        updatedGia
                );

                // Gọi API để cập nhật xe
                apiService.updateXe(String.valueOf(car.get_id()), updatedCar).enqueue(new Callback<List<CarModel>>() {
                    @Override
                    public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                        if (response.isSuccessful()) {
                            // Cập nhật lại danh sách sau khi cập nhật thành công
                            carModelsList.clear();
                            carModelsList.addAll(response.body());
                            notifyDataSetChanged(); // Làm mới Adapter
                        } else {
                            Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CarModel>> call, Throwable t) {
                        // Xử lý lỗi nếu có
                        Toast.makeText(context, "Lỗi khi kết nối API", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

            // Đảm bảo dialog được hiển thị
            builder.create().show();
        });
        holder.btnDelete.setOnClickListener(v -> {
            // Cảnh báo xóa xe
            new AlertDialog.Builder(context)
                    .setTitle("Xóa xe")
                    .setMessage("Bạn có chắc chắn muốn xóa xe này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        // Gọi API để xóa xe
                        apiService.xoaXe(String.valueOf(car.get_id())).enqueue(new Callback<List<CarModel>>() {
                            @Override
                            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                                if (response.isSuccessful()) {
                                    // Xóa xe khỏi danh sách và làm mới Adapter
                                    carModelsList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Xe đã được xóa", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                                // Xử lý lỗi khi gọi API
                                Toast.makeText(context, "Lỗi khi kết nối API", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return carModelsList.size();
    }

    // ViewHolder class
    public static class CarViewHolder extends RecyclerView.ViewHolder {

        TextView tvId, tvName, tvNamSX, tvHang, tvGia;
        ImageButton btnUpdate, btnDelete;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvName = itemView.findViewById(R.id.tvName);
            tvNamSX = itemView.findViewById(R.id.tvNamSX);
            tvHang = itemView.findViewById(R.id.tvHang);
            tvGia = itemView.findViewById(R.id.tvGia);
            btnUpdate = itemView.findViewById(R.id.btn_edit);

            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
