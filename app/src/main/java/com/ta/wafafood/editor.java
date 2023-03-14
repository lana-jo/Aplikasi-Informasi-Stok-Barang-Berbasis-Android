package com.ta.wafafood;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class editor extends AppCompatActivity {
    private EditText editNama, editharga, edtjumlah;
    private Button btnSave;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private String id = "";
    private ImageView avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        ActionBar actionBar= getSupportActionBar();
        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
            String nama = firebaseUser.getDisplayName();
            actionBar.setTitle("hi, "+ nama);
        }
        editNama = findViewById(R.id.nama);
        editharga = findViewById(R.id.harga);
        edtjumlah= findViewById(R.id.jumlah);
        btnSave = findViewById(R.id.btn_save);
        avatar = findViewById(R.id.avatar);
        avatar.setOnClickListener(v -> {
            selectImage();
        });
        progressDialog = new ProgressDialog(editor.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Menyimpan...");
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editNama.getText().length()>0 && editharga.getText().length()>0 && edtjumlah.getText().length()>0){
                    upload(editNama.getText().toString(), editharga.getText().toString(), Long.valueOf(edtjumlah.getText().toString()));
                }else{
                    Toast.makeText(getApplicationContext(), "Silahkan isi semua data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Intent intent = getIntent();
        if (intent!=null){
            id = intent.getStringExtra("id");
            editNama.setText(intent.getStringExtra("name"));
            editharga.setText(intent.getStringExtra("harga"));
            edtjumlah.setText(intent.getStringExtra( "jumlah"));
            Glide.with(getApplicationContext()).load(intent.getStringExtra("avatar")).into(avatar);
        }
    }
    private void selectImage(){
        final CharSequence[] items= {"Ambil Foto Barang", "Pilih Dari Galeri", "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(editor.this);
        builder.setTitle("Masukkan foto barang");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Ambil Foto Barang")){
                Intent intent= new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 10);
            }
            else if (items[item].equals("Pilih Dari Galeri")){
                Intent intent= new Intent (Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Pilih Gambar"), 20);
            }
            else if (items[item].equals("Batal")){
                dialog.dismiss();
            }
        });
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            final Uri path = data.getData();
            Thread thread= new Thread(() -> {
                try {
                    InputStream inputStream= getContentResolver().openInputStream(path);
                    Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                    avatar.post(()->{
                        avatar.setImageBitmap(bitmap);
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        if (requestCode == 10 && resultCode == RESULT_OK) {
            final Bundle extras = data.getExtras();
            Thread thread= new Thread(() -> {
                Bitmap bitmap= (Bitmap) extras.get("data");
                avatar.post(()->{
                    avatar.setImageBitmap(bitmap);
                });
            });
            thread.start();
        }
    }
    private void upload(String name, String harga, Long jumlah){
        progressDialog.show();
        avatar.setDrawingCacheEnabled(true);
        avatar.buildDrawingCache();
        Bitmap bitmap= ((BitmapDrawable) avatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
//        upload
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("image").child(name+new Date().getTime()+".jpeg");
        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Toast.makeText(getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            if(taskSnapshot.getMetadata()!=null){
                if (taskSnapshot.getMetadata().getReference()!=null){
                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(task -> {
                        if (task.getResult()!=null){
                            saveData(name, harga, Long.valueOf(jumlah), task.getResult().toString());
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveData(String name, String harga, Long jumlah, String avatar){
        Map<String, Object> user = new HashMap<>();
        user.put("nama", name);
        user.put("harga", harga);
        user.put("jumlah", jumlah);
        user.put("avatar", avatar);
        progressDialog.show();
        if (id!=null){
            db.collection("DataBarang").document(id)
                    .set(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Berhasil!", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Gagal!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            db.collection("DataBarang")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "Berhasil!", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }
}