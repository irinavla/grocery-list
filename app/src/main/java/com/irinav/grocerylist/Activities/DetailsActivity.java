package com.irinav.grocerylist.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.irinav.grocerylist.R;

public class DetailsActivity extends AppCompatActivity {

    private TextView itemName;
    private TextView quantity;
    private TextView dateAdded;
    private int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        itemName = (TextView) findViewById(R.id.itemNameDetails);
        quantity = (TextView) findViewById(R.id.quantityDetails);
        dateAdded = (TextView) findViewById(R.id.dateAddedDetails);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            itemName.setText(bundle.getString("name"));
            quantity.setText(bundle.getString("quantity"));
            dateAdded.setText(bundle.getString("dateAdded"));
            itemId = bundle.getInt("id");
        }
    }
}
