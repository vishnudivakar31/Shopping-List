package edu.njit.shoppinglist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import edu.njit.shoppinglist.data.DatabaseHandler;
import edu.njit.shoppinglist.model.ShoppingItem;
import edu.njit.shoppinglist.ui.RecyclerViewAdapter;

public class ListActivity extends AppCompatActivity {

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private List<ShoppingItem> shoppingItems;
    private DatabaseHandler databaseHandler;
    private FloatingActionButton fab;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private EditText itemName, itemQty, itemColor, itemSize;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        databaseHandler = new DatabaseHandler(this);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoppingItems = new ArrayList<>();

        shoppingItems = databaseHandler.getAllItems();

        recyclerViewAdapter = new RecyclerViewAdapter(this, shoppingItems);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopDialog();
            }

        });

    }

    private void createPopDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.popup, null);
        itemName = view.findViewById(R.id.itemName);
        itemQty = view.findViewById(R.id.itemQuantity);
        itemColor = view.findViewById(R.id.itemColor);
        itemSize = view.findViewById(R.id.itemSize);

        saveBtn = view.findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!itemName.getText().toString().isEmpty() &&
                        !itemColor.getText().toString().isEmpty() &&
                        !itemQty.getText().toString().isEmpty() &&
                        !itemSize.getText().toString().isEmpty()) {
                    saveItem(view);
                } else {
                    Snackbar.make(view, "Empty fields not allowed", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void saveItem(View view) {
        String name = itemName.getText().toString().trim();
        String color = itemColor.getText().toString().trim();
        Integer qty = Integer.valueOf(itemQty.getText().toString().trim());
        Integer size = Integer.valueOf(itemSize.getText().toString().trim());

        ShoppingItem item = new ShoppingItem(name, qty, color, size);

        databaseHandler.addItem(item);
        Snackbar.make(view, "Item saved successfully", Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                startActivity(new Intent(ListActivity.this, ListActivity.class));
                finish();
            }
        }, 1200);
    }
}
