package com.travel.expensekeeper;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText etAmount, etDate;
    private Spinner spinnerCategory;
    private Button btnAddExpense, btnViewReport;
    private ExpenseDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etAmount = findViewById(R.id.etAmount);
        etDate = findViewById(R.id.etDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnViewReport = findViewById(R.id.btnViewReport);

        dbHelper = new ExpenseDbHelper(this);

        String[] categories = {"Food", "Transport", "Entertainment", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        spinnerCategory.setAdapter(adapter);

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpense();
            }
        });

        // View report button click listener
        btnViewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewReport();
            }
        });
    }

    private void addExpense() {
        String amountText = etAmount.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();
        String date = etDate.getText().toString();

        if (amountText.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseDbHelper.COLUMN_CATEGORY, category);
        values.put(ExpenseDbHelper.COLUMN_AMOUNT, amount);
        values.put(ExpenseDbHelper.COLUMN_DATE, date);

        long newRowId = db.insert(ExpenseDbHelper.TABLE_NAME, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error adding expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewReport() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String reportQuery = "SELECT " + ExpenseDbHelper.COLUMN_CATEGORY + ", SUM(" + ExpenseDbHelper.COLUMN_AMOUNT + ") as total, "
                + "strftime('%Y-%m', " + ExpenseDbHelper.COLUMN_DATE + ") as month_year "
                + "FROM " + ExpenseDbHelper.TABLE_NAME
                + " GROUP BY strftime('%Y-%m', " + ExpenseDbHelper.COLUMN_DATE + "), " + ExpenseDbHelper.COLUMN_CATEGORY;

        Cursor cursor = db.rawQuery(reportQuery, null);

        if (cursor.moveToFirst()) {
            ArrayList<String> categories = new ArrayList<>();
            ArrayList<String> totals = new ArrayList<>();
            ArrayList<String> months = new ArrayList<>();

            do {
                String category = cursor.getString(0);
                double total = cursor.getDouble(1);
                String monthYear = cursor.getString(2);

                categories.add(category);
                totals.add(String.valueOf(total));
                months.add(monthYear);
            } while (cursor.moveToNext());

            Intent intent = new Intent(this, ReportActivity.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("categories", categories);
            bundle.putStringArrayList("totals", totals);
            bundle.putStringArrayList("months", months);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }
}