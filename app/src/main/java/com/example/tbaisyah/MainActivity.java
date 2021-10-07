package com.example.tbaisyah;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = MainActivity.this;
    private ConstraintLayout constraintLayout;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private AppCompatButton appCompatButtonLogin;
    private AppCompatTextView textViewLinkRegister;
    private InputValidation inputValidation;
    private CheckBox ShowPass;
    private dbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ShowPass = findViewById(R.id.showPass);
        // Untuk menampilkan password
        ShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ShowPass.isChecked()){
                    textInputEditTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    textInputEditTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        getSupportActionBar().hide();

        initViews();
        initListeners();
        initObjects();
    }

    // Metode ini untuk menginisialisasi views
    private void initViews() {
        constraintLayout = findViewById(R.id.constraintLayout);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputEditTextEmail = findViewById(R.id.textInputEditTextEmail);
        textInputEditTextPassword = findViewById(R.id.textInputEditTextPassword);
        appCompatButtonLogin = findViewById(R.id.appCompatButtonLogin);
        textViewLinkRegister = findViewById(R.id.textViewLinkRegister);
    }

    // Metode ini untuk menginisialisasi listeners
    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }

    // Metode ini untuk menginisialisasi objek yang akan digunakan
    private void initObjects() {
        dbHelper = new dbHelper(activity);
        inputValidation = new InputValidation(activity);
    }

    // Metode yang diterapkannya adalah onClick pada tampilan
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.appCompatButtonLogin:
                verifyFromSQLite();
                break;
            case R.id.textViewLinkRegister:
                Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

    // Metode ini untuk memvalidasi teks yg diinputkan dan memverifikasi saat login dari SQLite
    private void verifyFromSQLite() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, textInputLayoutEmail,
                getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail, textInputLayoutEmail,
                getString(R.string.error_message_email))) {
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword,
                getString(R.string.error_message_email))) {
            return;
        }

        if (dbHelper.checkUser(Objects.requireNonNull(textInputEditTextEmail.getText()).toString().trim()
                , Objects.requireNonNull(textInputEditTextPassword.getText()).toString().trim())) {

            Intent accountsIntent = new Intent(activity, DetailActivity.class);
            accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());
            emptyInputEditText();
            startActivity(accountsIntent);
        } else {
            Snackbar.make(constraintLayout, getString(R.string.error_valid_email_password),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    // Metode ini untuk mengosongkan semua input edittext
    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }

    // Digunakan untuk perpindahan halaman
    public void about(View view) {
        Intent intent = new Intent(MainActivity.this,About.class);
        startActivity(intent);
    }
}
