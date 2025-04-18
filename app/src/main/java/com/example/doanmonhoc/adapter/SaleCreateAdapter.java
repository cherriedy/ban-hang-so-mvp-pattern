package com.example.doanmonhoc.adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.doanmonhoc.R;
import com.example.doanmonhoc.model.CartItem;
import com.example.doanmonhoc.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SaleCreateAdapter extends BaseAdapter {
    private Context context;
    private List<Product> list;
    private List<CartItem> cartItems;
    private int totalQuantity;
    private int totalAmount;
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

    public static class ViewHolder {
        TextView tvQuantity, tvName, tvPrice;
        ImageButton btnPlus, btnMinus;
        ImageView imageView;
        CardView thanhTangGiamSoLuong;
        int currentPosition; // Lưu vị trí hiện tại của item
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sales_create_item, parent, false);

            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvQuantity = convertView.findViewById(R.id.tvQuantity);
            holder.btnPlus = convertView.findViewById(R.id.btnPlus);
            holder.btnMinus = convertView.findViewById(R.id.btnMinus);
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.thanhTangGiamSoLuong = convertView.findViewById(R.id.thanhTangGiamSoLuong);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = list.get(position);
        holder.currentPosition = position; // Lưu vị trí hiện tại của item
        if (product.getAvatarPath() != null && !product.getAvatarPath().isEmpty()) {
            Picasso.get().load(product.getAvatarPath()).into(holder.imageView);
        }

        holder.tvName.setText(product.getProductName());
        float discountedPrice = product.getOutPrice() * (1 - product.getDiscount());
        holder.tvPrice.setText(String.valueOf(discountedPrice));

        // Cập nhật trạng thái của thanhTangGiamSoLuong và số lượng
        updateQuantityView(holder, product);

        convertView.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (currentQuantity == 0) {
                if (checkProductStock(product.getId(), 1)) {
                    holder.thanhTangGiamSoLuong.setVisibility(View.VISIBLE);
                    holder.tvQuantity.setText("1");
                    updateCart(position, 1);
                } else {
                    Toast.makeText(context, "Số lượng tồn kho không đủ", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (checkProductStock(product.getId(), currentQuantity + 1)) {
                    holder.tvQuantity.setText(String.valueOf(currentQuantity + 1));
                    updateCart(position, currentQuantity + 1);
                } else {
                    Toast.makeText(context, "Số lượng tồn kho không đủ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (checkProductStock(product.getId(), currentQuantity + 1)) {
                holder.tvQuantity.setText(String.valueOf(currentQuantity + 1));
                updateCart(position, currentQuantity + 1);
            } else {
                Toast.makeText(context, "Số lượng tồn kho không đủ", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (currentQuantity > 0) {
                holder.tvQuantity.setText(String.valueOf(currentQuantity - 1));
                updateCart(position, currentQuantity - 1);
            }else{
                holder.thanhTangGiamSoLuong.setVisibility(View.INVISIBLE);
            }
        });

        return convertView;
    }

    private void updateQuantityView(ViewHolder holder, Product product) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                holder.thanhTangGiamSoLuong.setVisibility(View.VISIBLE);
                return;
            }
        }
        holder.tvQuantity.setText("0");
        holder.thanhTangGiamSoLuong.setVisibility(View.INVISIBLE);
    }
    private boolean checkProductStock(long productId, int requiredQuantity) {
        // Logic để kiểm tra số lượng tồn kho
        for (Product product : list) {
            if (product.getId() == productId) {
                return product.getInventoryQuantity() >= requiredQuantity;
            }
        }
        return false;
    }

    private void updateCart(int position, int quantity) {
        Product product = list.get(position);
        float discountedPrice = product.getOutPrice() * (1 - product.getDiscount());

        boolean found = false;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                if (quantity == 0) {
                    cartItems.remove(item);
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
        notifyDataSetChanged(); // Cập nhật lại giao diện
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
        } else {
            layoutBtnThanhToan.setVisibility(View.INVISIBLE);
        }
    }
}
