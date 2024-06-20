package com.example.doanmonhoc.activity.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doanmonhoc.R;
import com.example.doanmonhoc.activity.ProductBrandManagement.ProductBrandManagementActivity;
import com.example.doanmonhoc.activity.ProductManagement.ProductManagementActivity;

import com.example.doanmonhoc.activity.SaleManagement.SaleCreateActivity;
import com.example.doanmonhoc.activity.SaleManagement.SaleManagementActivity;

import com.example.doanmonhoc.adapter.BottomSheetShortcutAdapter;

import com.example.doanmonhoc.adapter.ShortcutGridViewAdapter;
import com.example.doanmonhoc.databinding.FragmentHomepageBinding;
import com.example.doanmonhoc.model.Shortcut;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Arrays;
import java.util.List;

public class HomepageFragment extends Fragment {
    private FragmentHomepageBinding b;
    private List<Shortcut> shortcutList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentHomepageBinding.inflate(getLayoutInflater());
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shortcutList = Arrays.asList(
                new Shortcut("Sản phẩm", R.drawable.ic_product, R.color.primaryColor),
                new Shortcut("Thống kê", R.drawable.ic_chart, R.color.primaryColor),
                new Shortcut("Đơn hàng", R.drawable.ic_note, R.color.primaryColor),
                new Shortcut("Kiểm kho", R.drawable.ic_supplier, R.color.primaryColor),
                new Shortcut("Nhân viên", R.drawable.ic_customer, R.color.primaryColor),
                new Shortcut("Thêm", R.drawable.ic_circle_ellipsis, R.color.primaryColor)
        );

        ShortcutGridViewAdapter shortcutGridViewAdapter = new ShortcutGridViewAdapter(getContext());
        shortcutGridViewAdapter.setData(shortcutList);
        shortcutGridViewAdapter.setOnItemClickListener(new ShortcutOnItemClick());
        b.gridViewShortcut.setAdapter(shortcutGridViewAdapter);
    }


    private class ShortcutOnItemClick implements ShortcutGridViewAdapter.OnItemClickListener {
        @Override
        public void onItemClick(int position) {
            if (position == ShortcutGridViewAdapter.SHORTCUT_PRODUCT) {
                startActivity(new Intent(getContext(), ProductManagementActivity.class));
            } else if (position == ShortcutGridViewAdapter.SHORTCUT_INVENTORY) {
                Toast.makeText(requireContext(), "INVENTORY", Toast.LENGTH_SHORT).show();
            } else if (position == ShortcutGridViewAdapter.SHORTCUT_ORDER) {
                startActivity(new Intent(getContext(), SaleManagementActivity.class));
            } else if (position == ShortcutGridViewAdapter.SHORTCUT_REPORT) {
                Toast.makeText(requireContext(), "REPORT", Toast.LENGTH_SHORT).show();
            } else if (position == ShortcutGridViewAdapter.SHORTCUT_STAFF) {
                Toast.makeText(requireContext(), "STAFF", Toast.LENGTH_SHORT).show();
            } else if (position == ShortcutGridViewAdapter.SHORTCUT_MORE) {
                @SuppressLint("InflateParams")
                View viewDialog = getLayoutInflater().inflate(R.layout.modal_bottom_sheet, null);
                GridView gridViewShortcut = viewDialog.findViewById(R.id.grid_view_shortcut);

                List<Shortcut> shortcutList = Arrays.asList(
                        new Shortcut("Nhãn hàng", R.drawable.ic_product_brand, R.color.primaryColor),
                        new Shortcut("Loại sản phẩm", R.drawable.ic_product_type, R.color.bold_sky)
                );

                final BottomSheetShortcutAdapter bottomSheetShortcutAdapter = new BottomSheetShortcutAdapter(getContext());
                bottomSheetShortcutAdapter.setData(shortcutList);
                bottomSheetShortcutAdapter.setOnClickOnListener(new BottomSheetOnItemClick());
                gridViewShortcut.setAdapter(bottomSheetShortcutAdapter);

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(viewDialog);
                bottomSheetDialog.show();

                viewDialog.findViewById(R.id.action_close).setOnClickListener(v -> bottomSheetDialog.dismiss());
            }
        }
    }

    private class BottomSheetOnItemClick implements BottomSheetShortcutAdapter.OnItemClickListener {
        @Override
        public void onItemClick(int position) {
            if (position == BottomSheetShortcutAdapter.SHORTCUT_PRODUCT_BRAND) {
                startActivity(new Intent(getContext(), ProductBrandManagementActivity.class));
            } else if (position == BottomSheetShortcutAdapter.SHORTCUT_PRODUCT_TYPE) {
                Toast.makeText(getContext(), "Home Fragment Product Type", Toast.LENGTH_SHORT).show();
            }

        }
    }
}