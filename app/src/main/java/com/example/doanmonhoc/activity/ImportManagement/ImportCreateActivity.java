    package com.example.doanmonhoc.activity.ImportManagement;

    import android.app.Activity;
    import android.content.Intent;
    import android.os.Bundle;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.cardview.widget.CardView;

    import com.example.doanmonhoc.R;
    import com.example.doanmonhoc.activity.SaleManagement.SaleConfirmActivity;
    import com.example.doanmonhoc.activity.SaleManagement.SaleCreateActivity;
    import com.example.doanmonhoc.adapter.ImportCreateAdapter;
    import com.example.doanmonhoc.adapter.SaleCreateAdapter;
    import com.example.doanmonhoc.api.KiotApiService;
    import com.example.doanmonhoc.model.CartItem;
    import com.example.doanmonhoc.model.DetailedGoodsReceivedNote;
    import com.example.doanmonhoc.model.Product;

    import java.util.ArrayList;
    import java.util.List;

    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

    public class ImportCreateActivity extends AppCompatActivity {
        private ListView listView;
        private ImportCreateAdapter adapter;
        private TextView tvTotalQuantity, tvTotalAmount;
        private CardView layoutBtnThanhToan, btnTiepTuc;
        private List<DetailedGoodsReceivedNote> cartItems ;
        private ImageButton btnBack;
        private EditText etSearch;
        private List<Product> productList = new ArrayList<>();
        private List<Product> filteredProductList = new ArrayList<>();

        // Khởi tạo ActivityResultLauncher
        private final ActivityResultLauncher<Intent> activityResultFinish = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        finish();
                    }
                });
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_import_create);
            listView = findViewById(R.id.listView);
            layoutBtnThanhToan = findViewById(R.id.layoutBtnThanhToan);
            tvTotalQuantity = findViewById(R.id.tvTotalQuantityInCart);
            tvTotalAmount = findViewById(R.id.tvTotalPriceInCart);
            etSearch = findViewById(R.id.etSearch);

            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterProducts(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            loadProductList();
            cartItems = new ArrayList<>();

            btnTiepTuc = findViewById(R.id.btnTiepTuc);
            btnTiepTuc.setOnClickListener(v -> {
                Intent intent = new Intent(this, ImportConfirmActivity.class);
                intent.putExtra("cartItems", new ArrayList<>(cartItems));
                activityResultFinish.launch(intent);
            });

            btnBack = findViewById(R.id.btnBack);
            btnBack.setOnClickListener(v -> finish());
        }

        private void loadProductList() {
            KiotApiService.apiService.getProductList().enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.isSuccessful()) {
                        List<Product> list = response.body();
                        if (list != null && !list.isEmpty()) {
                            productList.clear();
                            productList.addAll(list);
                            filteredProductList.clear();
                            filteredProductList.addAll(list);
                            adapter = new ImportCreateAdapter(ImportCreateActivity.this, filteredProductList, layoutBtnThanhToan, tvTotalQuantity, tvTotalAmount, cartItems);
                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(ImportCreateActivity.this, "Không tìm thấy hóa đơn nào!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ImportCreateActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                    Toast.makeText(ImportCreateActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void filterProducts(String keyword) {
            filteredProductList.clear();
            if (keyword.isEmpty()) {
                filteredProductList.addAll(productList);
            } else {
                for (Product product : productList) {
                    if (product.getProductName().toLowerCase().contains(keyword.toLowerCase())) {
                        filteredProductList.add(product);
                    }
                }
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }