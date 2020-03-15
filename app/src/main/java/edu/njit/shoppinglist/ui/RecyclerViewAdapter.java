package edu.njit.shoppinglist.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.List;

import edu.njit.shoppinglist.R;
import edu.njit.shoppinglist.data.DatabaseHandler;
import edu.njit.shoppinglist.model.ShoppingItem;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<ShoppingItem> shoppingItems;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private EditText editItemSize, editItemName, editItemQty, editItemColor;
    private Button saveBtn;

    public RecyclerViewAdapter(Context context, List<ShoppingItem> shoppingItems) {
        this.context = context;
        this.shoppingItems = shoppingItems;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listrow, viewGroup, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingItems.get(position);
        holder.itemName.setText(String.format("Item: %s", shoppingItem.getName()));
        holder.itemColor.setText(MessageFormat.format("Color: {0}", shoppingItem.getColor()));
        holder.itemQty.setText(MessageFormat.format("Quantity: {0}", shoppingItem.getQty()));
        holder.itemSize.setText(MessageFormat.format("Size: {0}", String.valueOf(shoppingItem.getSize())));
        holder.dateAdded.setText(MessageFormat.format("Date: {0}", shoppingItem.getDateItemAdded()));
        holder.id = shoppingItem.getId();
    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView itemName, itemColor, itemQty, itemSize, dateAdded;
        public Button editBtn, deleteBtn;
        int id;
        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            itemName = itemView.findViewById(R.id.dispName);
            itemColor = itemView.findViewById(R.id.dispColor);
            itemQty = itemView.findViewById(R.id.dispQty);
            itemSize = itemView.findViewById(R.id.dispSize);
            dateAdded = itemView.findViewById(R.id.dispDate);
            editBtn = itemView.findViewById(R.id.editButton);
            deleteBtn = itemView.findViewById(R.id.deleteButton);

            editBtn.setOnClickListener(this);
            deleteBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            switch (view.getId()) {
                case R.id.editButton:
                    editItem(shoppingItems.get(position));
                    break;
                case R.id.deleteButton:
                    deleteItem(shoppingItems.get(position).getId());
                    break;
            }
        }

        private void editItem(final ShoppingItem item) {
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.popup, null);
            editItemName = view.findViewById(R.id.itemName);
            editItemQty = view.findViewById(R.id.itemQuantity);
            editItemColor = view.findViewById(R.id.itemColor);
            editItemSize = view.findViewById(R.id.itemSize);

            saveBtn = view.findViewById(R.id.saveButton);
            saveBtn.setText(R.string.update);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!editItemName.getText().toString().isEmpty() &&
                            !editItemColor.getText().toString().isEmpty() &&
                            !editItemQty.getText().toString().isEmpty() &&
                            !editItemSize.getText().toString().isEmpty()) {
                        updateItem(view, item, getAdapterPosition());
                    } else {
                        Snackbar.make(view, "Empty fields not allowed", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

            editItemName.setText(item.getName());
            editItemQty.setText(String.valueOf(item.getQty()));
            editItemColor.setText(item.getColor());
            editItemSize.setText(String.valueOf(item.getSize()));

            builder.setView(view);
            dialog = builder.create();
            dialog.show();
        }

        private void updateItem(View view, final ShoppingItem item, final int position) {
            DatabaseHandler databaseHandler = new DatabaseHandler(context);
            String name = editItemName.getText().toString().trim();
            String color = editItemColor.getText().toString().trim();
            Integer qty = Integer.valueOf(editItemQty.getText().toString().trim());
            Integer size = Integer.valueOf(editItemSize.getText().toString().trim());

            item.setName(name);
            item.setColor(color);
            item.setQty(qty);
            item.setSize(size);

            databaseHandler.updateItem(item);
            Snackbar.make(view, "Item updated successfully", Snackbar.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    notifyItemChanged(position, item);
                }
            }, 1200);
        }

        private void deleteItem(final int id) {
            builder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.confirmationpop, null);

            Button noButton = view.findViewById(R.id.confirm_no_btn);
            Button yesButton = view.findViewById(R.id.confirm_yes_btn);

            builder.setView(view);
            dialog = builder.create();
            dialog.show();

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);
                    databaseHandler.deleteItem(id);
                    shoppingItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });

        }
    }
}
