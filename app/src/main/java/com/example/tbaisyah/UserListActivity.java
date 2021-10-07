package com.example.tbaisyah;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewUsers;
    private List<User> listUsers;
    private UserRecyclerAdapter usersRecyclerAdapter;
    private Spinner spinnerDelete;
    protected Cursor cursor;
    private dbHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        spinnerDelete = findViewById(R.id.spinner_delete);

        getSupportActionBar().setTitle("All User Account");
        initViews();
        initObjects();
    }

    private void initViews() {
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
    }

    private void initObjects() {
        listUsers = new ArrayList<>();
        usersRecyclerAdapter = new UserRecyclerAdapter(listUsers);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewUsers.setLayoutManager(mLayoutManager);
        recyclerViewUsers.setItemAnimator(new DefaultItemAnimator());
        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setAdapter(usersRecyclerAdapter);

        getDataFromSQLite();
        tampilSpinner();
    }

    // Metode ini untuk mengambil semua data user dari SQLite
    private void getDataFromSQLite() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                listUsers.clear();
                listUsers.addAll(dbHelper.getAllUser());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                usersRecyclerAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    // Digunakan untuk perpindahan halaman
    public void UpdatePage(View view) {
        Intent intent = new Intent(UserListActivity.this,UpdateActivity.class);
        startActivity(intent);
    }

    // Menampilkan spinner dengan pilihan nama user yg akan dihapus
    public void tampilSpinner(){
        ArrayList<String> arraySpinner = new ArrayList<>();
        dbHelper = new dbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.query("user",null,null,null,null,
                null,null);
        int i = 0;
        try {
            while (cursor.moveToNext()){
                cursor.moveToPosition(i);
                arraySpinner.add(cursor.getString(cursor.getColumnIndex("user_name")));
                i++;
            }
        }finally {
            ArrayAdapter<String> adapterarray = new ArrayAdapter<String>(this, android.R.layout.
                    simple_spinner_item, arraySpinner);
            spinnerDelete.setAdapter(adapterarray);
            spinnerDelete.setSelection(0);
            cursor.close();
        }
    }

    // Menghapus user dengan memilih nama user yg akan dihapus pada spinner
    public void delete(View view) {
        String user_name = spinnerDelete.getSelectedItem().toString();
        if (user_name.isEmpty()){
            Message.message(getApplicationContext(),"Enter Data");
        } else {
            int a = dbHelper.delete(user_name);
            if (a <= 0){
                Message.message(getApplicationContext(),"Unsuccessful");
                spinnerDelete.setSelection(0);
            } else {
                Message.message(this,"DELETED");
                spinnerDelete.setSelection(0);
            }
            initObjects();
        }
    }
}