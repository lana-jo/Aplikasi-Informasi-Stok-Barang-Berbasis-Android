package com.ta.wafafood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.ta.wafafood.Model.Users;


public class registrasi extends AppCompatActivity {

    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    boolean pweye;
    Button registerbtn, loginbtn;
    EditText nama, emailEt, passwordEt, noHp;
    private FirebaseAuth firebaseAuth;
    /*proses dialog*/
    private ProgressDialog progressDialog;

    //    actionbar
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

//        init Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
//        konfigurasi progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar");
        progressDialog.setMessage("Membuat akun anda......");
        progressDialog.setCanceledOnTouchOutside(false);

        nama = findViewById(R.id.nama_register);
        emailEt = findViewById(R.id.email_register);
        passwordEt = findViewById(R.id.password_register);
        noHp = findViewById(R.id.nomor_register);
        loginbtn = findViewById(R.id.btnlogin_register);
        registerbtn = findViewById(R.id.btnregister_register);

        passwordEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int right = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= passwordEt.getRight() - passwordEt.getCompoundDrawables()[right].getBounds().width()) {
                        int selection = passwordEt.getSelectionEnd();
                        if (pweye) {
                            //mengatur gambar drawable
                            passwordEt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0);
                            // menyembunyikan password
                            passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                        menyembunyikan password
                            passwordEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            pweye = false;
                        } else {
//                        mengatur gambar drawable
                            passwordEt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
//                        menyembunyikan password
                            passwordEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            pweye = true;
                        }
                        passwordEt.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        loginbtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), login.class)));

        registerbtn.setOnClickListener(view -> {
            validasi();
        });
    }

    private void validasi() {
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEt.getText().toString().trim()).matches()) {
//            email format is invalid, dont proceed further
            emailEt.setError("Format Email yang anda masukan salah");
        } else if (passwordEt.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password minimal panjangnya 6 karakter", Toast.LENGTH_SHORT).show();
        } else if (noHp.getText().length() < 10){
            Toast.makeText(getApplicationContext(), "Masukkan Nomor hp yang benar", Toast.LENGTH_SHORT).show();
        }
        else if (nama.getText().length() > 0 && emailEt.getText().length() > 0  && noHp.getText().length()>0 && passwordEt.getText().length() > 0) {
            firebaseSignUp(nama.getText().toString(), emailEt.getText().toString(), passwordEt.getText().toString());
        }
        else {
            Toast.makeText(getApplicationContext(), "Silahkan Isi Form", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseSignUp(String nama1, String email, String password) {
//        melihatkan progress
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {

                    String uid= task.getResult().getUser().getUid();

                    Users user = new Users(uid, nama.getText().toString(), noHp.getText().toString(), emailEt.getText().toString(), passwordEt.getText().toString(), 1 );
                    firebaseDatabase.getReference().child("user").child(uid).setValue(user);

                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    Toast.makeText(registrasi.this,
                            "Register Berhasil\n", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(registrasi.this, login.class));

                    if (firebaseUser != null) {
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nama1).build();
                        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(registrasi.this, login.class));
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Register gagal", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}