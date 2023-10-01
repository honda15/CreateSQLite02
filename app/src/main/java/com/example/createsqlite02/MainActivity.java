package com.example.createsqlite02;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    private static final String CREATE_TABLE = "CREATE TABLE table01 (_id INTEGER PRIMARY KEY, name TEXT, price INTEGER)";
    private SQLiteDatabase db = null;

    private EditText editID;
    private Button btnSearch, btnSearchAll;
    private ListView listview01;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editID = findViewById(R.id.edtID);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearchAll = findViewById(R.id.btnSearchAll);
        listview01 = findViewById(R.id.ListView01);

        btnSearch.setOnClickListener(new MyListener());
        btnSearchAll.setOnClickListener(new MyListener());

        db = openOrCreateDatabase("db1.db", MODE_PRIVATE, null);
        try {
            db.execSQL(CREATE_TABLE);
            db.execSQL("INSERT INTO table01 (name, price) VALUES ('香蕉', 30)");
            db.execSQL("INSERT INTO table01 (name, price) VALUES ('西瓜', 120)");
            db.execSQL("INSERT INTO table01 (name, price) VALUES ('梨子', 250)");
            db.execSQL("INSERT INTO table01 (name, price) VALUES ('水蜜桃', 280)");
        } catch (Exception e) {
            Log.e("SQLite Error", e.toString());
        }

        cursor = getAll();
        UpdateAdapter(cursor);

        listview01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Cursor c = null;
                try {
                    c = get(id);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                String s = "id=" + id + "\r\n" + "name=" + c.getString(1) + "\r\n" + "price=" + c.getInt(2);
//                Log.v("lee", s);
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(s)
                        .setTitle("詳細資訊")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    public void UpdateAdapter(Cursor cursor) {
        if (cursor != null && cursor.getCount() >= 0) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    R.layout.mylayout,
                    cursor,
                    new String[]{"_id", "name", "price"},
                    new int[]{R.id.txtId, R.id.txtName, R.id.txtPrice},
                    0);
            listview01.setAdapter(adapter);
        }
    }

    public Cursor get(long rowId) throws SQLException {
        Cursor cursor = db.rawQuery("SELECT _id, name, price FROM table01 WHERE _id=" + rowId, null);
        if (cursor.getCount() > 0)
            cursor.moveToFirst();
        else
            Toast.makeText(getApplicationContext(), "查無此筆資料!", Toast.LENGTH_SHORT).show();
        return cursor;
    }

    public Cursor getAll() {
        Cursor cursor = db.rawQuery("SELECT _id, name, price FROM table01", null);
        return cursor;
    }

    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            try {
                switch (view.getId()) {
                    case R.id.btnSearch:
                        long id = Integer.parseInt(editID.getText().toString());
                        cursor = get(id);
                        UpdateAdapter(cursor);
                        break;

                    case R.id.btnSearchAll:
                        cursor = getAll();
                        UpdateAdapter(cursor);
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "查無此資料!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
