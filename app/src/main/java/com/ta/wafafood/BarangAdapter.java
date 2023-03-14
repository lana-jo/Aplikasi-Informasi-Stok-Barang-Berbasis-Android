package com.ta.wafafood;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ta.wafafood.Model.User;
import com.ta.wafafood.databinding.RowUserBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {
    private final List<User> users = new ArrayList<>();
    public interface BarangListener {
        void onClick(int pos, User user);
    }
    private final BarangListener barangListener;
     public BarangAdapter(BarangListener barangListener) {
        this.barangListener = barangListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowUserBinding.inflate(LayoutInflater.from(parent.getContext()),parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setHolder(users.get(position), barangListener, position);
    }
    @Override
    public int getItemCount() {
        return users.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RowUserBinding binding;
        public ViewHolder(RowUserBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
        public void setHolder(User user, BarangListener listener, int position) {
            binding.name.setText(user.getName());
            binding.harga.setText (formatRupiah(Double.parseDouble(user.getharga())));
            binding.jumlah.setText(String.format("%s pcs",user.getJumlah()));
            Glide.with(binding.avatar.getContext()).load(user.getAvatar()) .into(binding.avatar);
            itemView.setOnClickListener(view -> listener.onClick(position, user));
        }
        private String formatRupiah(Double number){
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            return formatRupiah.format(number);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<User> list) {
        users.clear();
        users.addAll(list);
        notifyDataSetChanged();
    }
}
