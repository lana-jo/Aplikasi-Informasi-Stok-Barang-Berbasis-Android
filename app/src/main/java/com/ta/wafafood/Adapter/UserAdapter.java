package com.ta.wafafood.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ta.wafafood.Model.User;
import com.ta.wafafood.R;


import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {



    private Context context;
    private List<User> list;
    private Dialog dialog;

    public interface Dialog{
        void onClick(int pos, User user);
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }
    public UserAdapter(Context context, List<User> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.harga.setText (formatRupiah(Double.parseDouble(list.get(position).getharga())));
        holder.jumlah.setText(String.format("%s pcs", list.get(position).getJumlah()));
        Glide.with(context).load(list.get(position).getAvatar()) .into(holder.avatar);
        holder.setUser(list.get(position));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, harga, jumlah;
        ImageView avatar;
        View wrapper;
        User user;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            harga = itemView.findViewById(R.id.harga);
            jumlah= itemView.findViewById(R.id.jumlah);
            avatar= itemView.findViewById(R.id.avatar);
            wrapper = itemView.findViewById(R.id.user_wrapper);
            itemView.setOnClickListener(view -> {
                if (dialog!=null){
                    dialog.onClick(getLayoutPosition(), user);
                }
            });


        }

        public void setUser(User user) {
            this.user = user;
        }

    }
    private String formatRupiah(Double number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}
