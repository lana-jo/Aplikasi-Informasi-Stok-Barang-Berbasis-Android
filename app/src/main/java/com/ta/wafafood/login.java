package com.ta.wafafood;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class login extends AppCompatActivity {
    EditText edtemail, edtpw;
    FirebaseAuth firebaseAuth;
    Button btnlogin, btnregister, metu;
    private ProgressDialog progressDialog;
    boolean pweye;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        edtemail= findViewById(R.id.email);
        edtpw= findViewById(R.id.password);
        btnlogin= findViewById(R.id.btnlogin);
        btnregister= findViewById(R.id.btnregister);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Silahkan tunggu......");
        progressDialog.setCanceledOnTouchOutside(false);
        edtpw.setOnTouchListener((view, motionEvent) -> {
            final int right = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (motionEvent.getRawX() >= edtpw.getRight() - edtpw.getCompoundDrawables()[right].getBounds().width()) {
                    int selection = edtpw.getSelectionEnd();
                    if (pweye) {
//                        mengatur gambar drawable
                        edtpw.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
//                        menyembunyikan password
                        edtpw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        pweye=false;
                    }else {
//                        mengatur gambar drawable
                        edtpw.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
//                        menyembunyikan password
                        edtpw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        pweye=true;
                    }
                    edtpw.setSelection(selection);
                    return true;
                }
            }
            return false;
        });
        btnlogin.setOnClickListener(view -> {
            if(edtemail.getText().length()>0 && edtpw.getText().length()>0){
                login(edtemail.getText().toString(),edtpw.getText().toString());
            }else {
                Toast.makeText(getApplicationContext(), "isi semua form", Toast.LENGTH_SHORT).show();
            }
        });
        btnregister.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), registrasi.class)));
    }

    private void login(String email, String password){
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult()!=null){
                if (task.getResult().getUser()!=null){
                    String uid= task.getResult().getUser().getUid();
                    FirebaseDatabase db= FirebaseDatabase.getInstance();
                    db.getReference().child("user").child(uid).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int status= snapshot.getValue(Integer.class);
                            if (status==0){
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                String nama = firebaseUser.getDisplayName();
                                Toast.makeText(login.this,nama +
                                        " Login Berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login.this, adminDashboard.class));
                            }
                            if (status==1){
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                String nama = firebaseUser.getDisplayName();
                                Toast.makeText(login.this,nama +
                                        " Login Berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login.this, userdasboard.class));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}});
                }else{Toast.makeText(getApplicationContext(), "login gagal!", Toast.LENGTH_SHORT).show();}
            }else{Toast.makeText(getApplicationContext(), "kata sandi atau email salah!", Toast.LENGTH_SHORT).show();}
            progressDialog.dismiss();
        });
    }
}