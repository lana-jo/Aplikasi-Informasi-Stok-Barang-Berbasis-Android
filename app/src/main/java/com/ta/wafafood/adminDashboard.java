package com.ta.wafafood;
import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.ta.wafafood.Adapter.UserAdapter;
import com.ta.wafafood.Model.User;
import java.util.ArrayList;
import java.util.List;

public class adminDashboard extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<User> list = new ArrayList<>();
    private BarangAdapter barangAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private UserAdapter userAdapter;
    private TextInputEditText edtjumlah;
    private ProgressDialog progressDialog;
    private final String id = "";
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        recyclerView = findViewById(R.id.recycler_view);
        btnAdd = findViewById(R.id.btn_add);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            String nama = firebaseUser.getDisplayName();
            actionBar.setTitle("hi, " + nama);
        }
        progressDialog = new ProgressDialog(adminDashboard.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Mengambil data...");
        barangAdapter = new BarangAdapter((pos, user) -> {
            final CharSequence[] dialogItem = {"Edit", "Hapus", "Barang Masuk", "Barang Keluar"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(adminDashboard.this);
            dialog.setItems(dialogItem, (dialogInterface, i) -> {
                switch (i) {
                    case 0:
                        Intent intent = new Intent(adminDashboard.this, editor.class);
                        intent.putExtra("id", list.get(pos).getId());
                        intent.putExtra("name", list.get(pos).getName());
                        intent.putExtra("harga", list.get(pos).getharga());
                        intent.putExtra("jumlah", list.get(pos).getJumlah().toString());
                        intent.putExtra("avatar", list.get(pos).getAvatar());
                        startActivity(intent);
                        break;
                    case 1:
                        deleteData(list.get(pos).getId(), list.get(pos).getAvatar());
                        break;
                    case 2:
                        transaction(user, 1);
                        break;
                    case 3:
                        transaction(user, 2);
                        break;
                }
            });
            dialog.show();
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(barangAdapter);
        btnAdd.setOnClickListener(view -> startActivity(new Intent(adminDashboard.this, editor.class)));
    }
    private void transaction(User user, int apa) {
        if (user == null || user.getId() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(adminDashboard.this);
        builder.setTitle(String.format("Barang %s", apa == 1 ? "Masuk": "Keluar"));
        View view = getLayoutInflater().inflate(R.layout.layout_barangmasuk, null);
        edtjumlah = view.findViewById(R.id.barangmasuk);
        edtjumlah.setHint(String.format("Barang %s", apa == 1 ? "Masuk": "Keluar"));
        builder.setView(view);
        builder.setPositiveButton("TAMBAH", (dialog, i) -> {
            if (edtjumlah.getText().length()>0){
                String string = edtjumlah.getText().toString();
                long a = Long.parseLong(string);
                final DocumentReference docRef = db.collection("DataBarang").document(user.getId());
                db.runTransaction(transaction -> {
                    DocumentSnapshot snapshot = transaction.get(docRef);
                    Long barang = null;
                    Log.d(TAG, "transaction: " + snapshot.get("jumlah"));
                    if (snapshot.contains("jumlah")) {
                        try {
                            if (snapshot.getLong("jumlah") != null) {
                                barang = snapshot.getLong("jumlah");
                                if (apa == 1) {
                                    barang += a;
                                } else {
                                    if (barang != null && barang.compareTo(a) >= 0) {
                                        barang = barang - a;
                                    }
                                }
                                transaction.update(docRef, "jumlah", barang);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return barang;
                }).addOnSuccessListener(result -> {
                    getData();
                    Log.d(TAG, "Transaction success: " + result);
                })/*.addOnFailureListener(e -> Log.w(TAG, "Transaction failure.", e))*/;
            } else{
                Toast.makeText(adminDashboard.this, "Tidak ada perubahan\nAnda Belum memasukkan data!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("BATAL", (dialogInterface1, i) -> dialogInterface1.dismiss());
        dialog = builder.create();
        dialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            keluar();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    private void keluar() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("PERINGATAN!!!")
                .setMessage("Apakah kamu ingin keluar ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    moveTaskToBack(true);
                    finish();
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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = new User(document.getString("nama"), document.getString("harga"), document.getLong("jumlah"), document.getString("avatar"));
                            user.setId(document.getId());
                            list.add(user);
                        }
                       barangAdapter.setList(list);
                    } else {
                        Toast.makeText(getApplicationContext(), "Data gagal di ambil!", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                });
    }
    private void deleteData(String id, String avatar) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("PERINGATAN!!!")
                .setMessage("apakah anda ingin menghapus data?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    progressDialog.show();
                    db.collection("DataBarang").document(id)
                            .delete()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Data gagal di hapus!", Toast.LENGTH_SHORT).show();
                                } else {
                                    FirebaseStorage.getInstance().getReferenceFromUrl(avatar).delete().addOnCompleteListener(task1 -> {
                                        progressDialog.dismiss();
                                        getData();
                                    });
                                }
                                progressDialog.dismiss();
                                getData();
                            });
                }).setNegativeButton("No", null).show();

    }
}