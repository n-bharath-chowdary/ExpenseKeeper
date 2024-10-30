package com.travel.expensekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ReportActivity extends AppCompatActivity {

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tableLayout = findViewById(R.id.table_layout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String[] categories = bundle.getStringArray("categories");
        String[] totals = bundle.getStringArray("totals");
        String[] months = bundle.getStringArray("months");

        for (int i = 0; i < categories.length; i++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView categoryTextView = new TextView(this);
            categoryTextView.setText(categories[i]);
            tableRow.addView(categoryTextView);

            TextView totalTextView = new TextView(this);
            totalTextView.setText(totals[i]);
            tableRow.addView(totalTextView);

            TextView monthTextView = new TextView(this);
            monthTextView.setText(months[i]);
            tableRow.addView(monthTextView);

            tableLayout.addView(tableRow);
        }

    }
}