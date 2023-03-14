package com.ta.wafafood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ta.wafafood.Adapter.UserAdapter;
import com.ta.wafafood.Model.User;
import java.util.ArrayList;
import java.util.List;
public class userdasboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<User> list = new ArrayList<>();
    private UserAdapter userAdapter;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userdasboard);
        recyclerView = findViewById(R.id.recycler_view);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        ActionBar actionBar= getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
            String nama = firebaseUser.getDisplayName();
            actionBar.setTitle("hi, "+ nama);
        }
        progressDialog = new ProgressDialog(userdasboard.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mengambil data...");
        userAdapter = new UserAdapter(getApplicationContext(), list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(userAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                keluar();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    private void keluar() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("PERINGATAN!!!")
                .setMessage("Apakah kamu ingin keluar ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        finish();
                    }

                }).setNegativeButton("Tidak", null)
                .setNeutralButton("Hanya Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }
                }).show();
    }
    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }
    private void getData() {
        progressDialog.show();
        db.collection("DataBarang")
                .orderBy("nama")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = new User(document.getString("nama"), document.getString("harga"), document.getLong("jumlah"),document.getString("avatar"));
                                user.setId(document.getId());
                                list.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), "Data gagal di ambil!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }
}