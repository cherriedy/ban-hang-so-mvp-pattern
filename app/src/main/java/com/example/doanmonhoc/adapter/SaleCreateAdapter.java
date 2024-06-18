package com.example.doanmonhoc.adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.doanmonhoc.R;
import com.example.doanmonhoc.model.CartItem;
import com.example.doanmonhoc.model.Product;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SaleCreateAdapter extends BaseAdapter {
    private Product product;
    private Context context;
    private List<Product> list;
    private List<CartItem> cartItems;
    private int totalQuantity;
    private double totalAmount;
    private TextView tvTotalQuantity, tvTotalAmount;
    private CardView layoutBtnThanhToan;


    public SaleCreateAdapter(Context context, List<Product> list, CardView layoutBtnThanhToan, TextView tvTotalQuantity, TextView tvTotalAmount, List<CartItem> cartItems) {
        this.context = context;
        this.list = list;
        this.layoutBtnThanhToan = layoutBtnThanhToan;
        this.tvTotalQuantity = tvTotalQuantity;
        this.tvTotalAmount = tvTotalAmount;
        this.cartItems = cartItems;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }
    public static class ViewHolder{
        TextView tvQuantity, tvName, tvPrice;
        ImageButton btnPlus, btnMinus;
        ImageView imageView;
        CardView thanhTangGiamSoLuong;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.sales_create_item, parent, false);

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvQuantity = convertView.findViewById(R.id.tvQuantity);
            holder.btnPlus = convertView.findViewById(R.id.btnPlus);
            holder.btnMinus = convertView.findViewById(R.id.btnMinus);
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.thanhTangGiamSoLuong = convertView.findViewById(R.id.thanhTangGiamSoLuong);

            convertView.setOnClickListener(v -> {
                // Nếu thanhTangGiamSoLuong đã hiển thị
                if (holder.thanhTangGiamSoLuong.getVisibility() == View.VISIBLE) {
                    int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
                    holder.tvQuantity.setText(String.valueOf(currentQuantity + 1));
                    updateCart(position, currentQuantity + 1);
                } else {
                    holder.thanhTangGiamSoLuong.setVisibility(View.VISIBLE);
                    holder.tvQuantity.setText("1");
                    updateCart(position, 1);
                }
            });

            holder.btnPlus.setOnClickListener(v -> {
                int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
                holder.tvQuantity.setText(String.valueOf(currentQuantity + 1));
                updateCart(position, currentQuantity + 1);
            });

            holder.btnMinus.setOnClickListener(v -> {
                int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
                if (currentQuantity > 0) {
                    holder.tvQuantity.setText(String.valueOf(currentQuantity - 1));
                    updateCart(position, currentQuantity - 1);
                    if (currentQuantity - 1 == 0) {
                        holder.thanhTangGiamSoLuong.setVisibility(View.INVISIBLE);
                    }
                }
            });

            convertView.setTag(holder);
        } else{
            holder = (ViewHolder) convertView.getTag();
        }

        product = list.get(position);
        String imageName = product.getAvatarPath();
        // Lấy ID của tài nguyên drawable từ tên ảnh
        int resID = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        holder.imageView.setImageResource(resID);

        holder.tvName.setText(product.getProductName());
        // Áp dụng giảm giá
        float discountedPrice = product.getOutPrice() * (1 - product.getDiscount());
        holder.tvPrice.setText(String.valueOf(discountedPrice));

        return convertView;
    }
    private void updateCart(int position, int quantity) {
        Product product = list.get(position);
        // Áp dụng giảm giá
        float discountedPrice = product.getOutPrice() * (1 - product.getDiscount());

        boolean found = false;
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProduct().getId() == product.getId()) {
                if (quantity == 0) {
                    iterator.remove();  // Remove item from cartItems
                } else {
                    item.setQuantity(quantity);
                    item.setPrice(quantity * discountedPrice);
                }
                found = true;
                break;
            }
        }
        if (!found && quantity > 0) {
            CartItem newItem = new CartItem(product, quantity, quantity * discountedPrice);
            cartItems.add(newItem);
        }
        updateTotalUI();
    }

    private void updateTotalUI() {
        totalQuantity = 0;
        totalAmount = 0;
        for (CartItem item : cartItems) {
            totalQuantity += item.getQuantity();
            totalAmount += item.getPrice();
        }
        if (totalQuantity > 0) {
            layoutBtnThanhToan.setVisibility(View.VISIBLE);
            tvTotalQuantity.setText(String.valueOf(totalQuantity));
            tvTotalAmount.setText(String.valueOf(totalAmount));
        }else if (totalQuantity == 0) {
            layoutBtnThanhToan.setVisibility(View.INVISIBLE);
        }
    }

}


