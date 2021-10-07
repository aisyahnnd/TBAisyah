package com.example.tbaisyah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private static final boolean DEBUG_MODE = false;
    private AppCompatActivity activity = DetailActivity.this;
    private TextView textViewName;
    dbHelper dbHelper;
    ListView listView;
    ArrayList<Movie> list;
    MovieAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 50 * 512 * 512); //the 100MB is the new size
        } catch (Exception e) {
            if (DEBUG_MODE) {
                e.printStackTrace();
            }
        }

        getSupportActionBar().setTitle("List Movie");
        initViews();
        initObjects();

        listView = findViewById(R.id.lv_list);
        list = new ArrayList<>();
        adapter = new MovieAdapter(this, R.layout.item_movie, list);
        listView.setAdapter(adapter);
        dbHelper = new dbHelper(this);

        // get all data from sqlite
        Cursor cursor = dbHelper.getDataMovie("SELECT * FROM movie");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String year = cursor.getString(2);
            String genre = cursor.getString(3);
            String desc = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            list.add(new Movie(title,year,genre,desc,image,id));
        }
        adapter.notifyDataSetChanged();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // update
                            Cursor c = dbHelper.getDataMovie("SELECT id_movie FROM movie");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            // show dialog update at here
                            showDialogUpdate(DetailActivity.this, arrID.get(position));
                        } else {
                            // delete
                            Cursor c = dbHelper.getDataMovie("SELECT id_movie FROM movie");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    private void initViews() {
        textViewName = findViewById(R.id.textViewName);
    }

    private void initObjects() {
        dbHelper = new dbHelper(activity);
        String emailFromIntent = getIntent().getStringExtra("EMAIL");
        textViewName.setText(emailFromIntent);
    }

    public void all_user(View view) {
        Intent intent = new Intent(activity,UserListActivity.class);
        startActivity(intent);
    }

    public void add_movie(View view) {
        Intent intent = new Intent(activity,AddMovieActivity.class);
        startActivity(intent);
    }

    ImageView imageViewMovie;
    private void showDialogUpdate(Activity activity, final int position){

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.activity_update_movie);
        dialog.setTitle("Update");

        imageViewMovie = (ImageView) dialog.findViewById(R.id.imageViewMovie);
        final EditText edtTitle = (EditText) dialog.findViewById(R.id.edtTitle);
        final EditText edtYear = (EditText) dialog.findViewById(R.id.edtYear);
        final Spinner spinnerGenre = (Spinner) dialog.findViewById(R.id.spinnerGenre);
        final EditText edtDesc = (EditText) dialog.findViewById(R.id.edtDesc);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        DetailActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbHelper.updateDataMovie(
                            edtTitle.getText().toString().trim(),
                            edtYear.getText().toString().trim(),
                            spinnerGenre.getSelectedItem().toString().trim(),
                            edtDesc.getText().toString().trim(),
                            AddMovieActivity.imageViewToByte(imageViewMovie),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successfully!!!",Toast.LENGTH_SHORT).show();
                }
                catch (Exception error) {
                    Log.e("Update error", error.getMessage());
                }
                updateMovieList();
            }
        });
    }

    private void showDialogDelete(final int id_movie){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(DetailActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        dialogDelete.setTitle("Warning!!");
        dialogDelete.setMessage("Are you sure you want to this delete?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dbHelper.deleteDataMovie(id_movie);
                    Toast.makeText(getApplicationContext(), "Delete successfully!!!",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("error", e.getMessage());
                }
                updateMovieList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateMovieList(){
        // get all data from sqlite
        Cursor cursor = dbHelper.getDataMovie("SELECT * FROM movie");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String year = cursor.getString(2);
            String genre = cursor.getString(3);
            String desc = cursor.getString(4);
            byte[] image = cursor.getBlob(5);

            list.add(new Movie(title,year,genre,desc,image,id));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 888 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewMovie.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
