package com.example.tbaisyah;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {
    private EditText textNo,textNama,textEmail,textPassword;
    private TextView textDB;
    private String nama,email,password,isiDB,no;
    private Spinner spinnerSearch;
    protected Cursor cursor;
    private CheckBox ShowPass;
    dbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        textDB = (TextView) findViewById(R.id.textDB);
        textNo = (EditText) findViewById(R.id.editText_id);
        textNama = (EditText) findViewById(R.id.editText_nama);
        textEmail = (EditText) findViewById(R.id.editText_email);
        textPassword = (EditText) findViewById(R.id.editText_password);
        spinnerSearch = (Spinner) findViewById(R.id.spinner_search);
        ShowPass = findViewById(R.id.showPass);

        ShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ShowPass.isChecked()){
                    textPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    textPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        textDB.setMovementMethod(new ScrollingMovementMethod());
        setEnable(false);
        tampilData();
    }

    public void tampilData(){
        isiDB = "";
        dbHelper = new dbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cursor = db.query("user",null,null,null,null,
                null,null);
        cursor.moveToFirst();
        int i = 0;
        try {
            while (cursor.moveToNext()){
                cursor.moveToPosition(i);
                isiDB +=" No : " +cursor.getString(cursor.getColumnIndex("user_id"))+ " \n "+
                        "Name : " +cursor.getString(cursor.getColumnIndex("user_name"))+ " \n "+
                        "Email : " +cursor.getString(cursor.getColumnIndex("user_email"))+ " \n "+
                        "Password : " +cursor.getString(cursor.getColumnIndex("user_password"))+ " \n "+
                        "-----------------------------------------------------------------------------\n";
                i++;
            }
        }finally {
            textDB.setText(isiDB);
            tampilSpinner();
            cursor.close();
        }
    }

    public void tampilSpinner(){
        ArrayList<String> arraySpinner = new ArrayList<>();
        dbHelper = new dbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.query("user",null,null,null,null,
                null,null);
        cursor.moveToFirst();
        int i = 0;
        try {
            while (cursor.moveToNext()){
                cursor.moveToPosition(i);
                arraySpinner.add(cursor.getString(1));
                i++;
            }
        }finally {
            ArrayAdapter<String> adapterarray = new ArrayAdapter<String>(this, android.R.layout.
                    simple_spinner_item, arraySpinner);
            spinnerSearch.setAdapter(adapterarray);
            spinnerSearch.setSelection(0);
            cursor.close();
        }
    }

    public void search(View view) {
        nama = spinnerSearch.getSelectedItem().toString();
        String[] namaargs = {nama};
        dbHelper = new dbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.query("user",null,"user_name = ?",namaargs,null,
                null,null);
        int i = 0;
        try {
            while (cursor.moveToNext()){
                cursor.moveToPosition(i);
                no = cursor.getString(cursor.getColumnIndex("user_id"));
                nama = cursor.getString(cursor.getColumnIndex("user_name"));
                email = cursor.getString(cursor.getColumnIndex("user_email"));
                password = cursor.getString(cursor.getColumnIndex("user_password"));
                i++;
            }
        }catch (Exception e){
            Message.message(getApplicationContext(),"Error "+e);
        }finally {
            textNo.setText(no);
            textNama.setText(nama);
            textEmail.setText(email);
            textPassword.setText(password);
            setEnable(true);
            cursor.close();
        }
    }

    public void update(View view) {
        no = textNo.getText().toString();
        nama = textNama.getText().toString();
        email = textEmail.getText().toString();
        password = textPassword.getText().toString();
        if (no.isEmpty() || nama.isEmpty() || email.isEmpty() || password.isEmpty()){
            Message.message(getApplicationContext(),"Enter Data");
        } else {
            int a = dbHelper.update(no,nama,email,password);
            if (a <= 0){
                Message.message(getApplicationContext(),"Unsuccessful");
                textNama.setText("");
                textEmail.setText("");
                textPassword.setText("");
            } else {
                Message.message(getApplicationContext(),"UPDATED");
                textNo.setText("");
                textNama.setText("");
                textEmail.setText("");
                textPassword.setText("");
                tampilData();
                setEnable(false);
            }
        }
    }

    public void setEnable(Boolean A){
        textNama.setEnabled(A);
        textEmail.setEnabled(A);
        textPassword.setEnabled(A);
    }
}
