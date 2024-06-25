package com.example.doanmonhoc.activity.CreateImportProduct;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanmonhoc.R;
import com.example.doanmonhoc.adapter.ListManagementImportAdapter;
import com.example.doanmonhoc.api.KiotApiService;
import com.example.doanmonhoc.model.DetailedGoodsReceivedNote;
import com.example.doanmonhoc.model.GoodsReceivedNote;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagementImportActivity extends AppCompatActivity {
    private ListView listView;
    private ListManagementImportAdapter adapter;
  private List<DetailedGoodsReceivedNote> detailList = new ArrayList<>();
    private List<GoodsReceivedNote> goodsReceivedNotes = new ArrayList<>();

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_management);

        listView = findViewById(R.id.lv_Mangement);

        fetchDataFromApi();
    }

    private void fetchDataFromApi() {
        KiotApiService.apiService.getGoodsReceivedNoteList().enqueue(new Callback<List<GoodsReceivedNote>>() {
            @Override
            public void onResponse(Call<List<GoodsReceivedNote>> call, Response<List<GoodsReceivedNote>> response) {
                if (response.isSuccessful()) {
                    List<GoodsReceivedNote> list = response.body();
                    goodsReceivedNotes.clear();
                    goodsReceivedNotes.addAll(list);
                    adapter = new ListManagementImportAdapter(ManagementImportActivity.this, goodsReceivedNotes);
                    listView.setAdapter(adapter);
                } else {
                    Log.e("API Error", "Server returned error: " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<List<GoodsReceivedNote>> call, Throwable t) {
                Log.e("API Error", "Network error: " + t.getMessage());
            }



        });

        KiotApiService.apiService.getDetailedGoodsReceivedNoteList().enqueue(new Callback<List<DetailedGoodsReceivedNote>>() {
            @Override
            public void onResponse(Call<List<DetailedGoodsReceivedNote>> call, Response<List<DetailedGoodsReceivedNote>> response) {
                if (response.isSuccessful()) {
                    List<DetailedGoodsReceivedNote> detailslist = response.body();
                    detailList.clear();
                    detailList.addAll(detailslist);
                    adapter = new ListManagementImportAdapter(ManagementImportActivity.this, detailList);
                    listView.setAdapter(adapter);
                } else {
                    Log.e("API Error", "Server returned error: " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<List<DetailedGoodsReceivedNote>> call, Throwable t) {
                Log.e("API Error", "Network error: " + t.getMessage());
            }



        });
    }

    public void navigateToCreateImportActivity(View view) {
        Intent intent = new Intent(this, GoodsImportActivity.class);
        startActivity(intent);
    }



}
