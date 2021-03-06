package com.irinav.grocerylist.Ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.irinav.grocerylist.Activities.DetailsActivity;
import com.irinav.grocerylist.Data.DatabaseHandler;
import com.irinav.grocerylist.Model.Grocery;
import com.irinav.grocerylist.R;

import java.util.List;

import static android.view.View.*;
import static com.irinav.grocerylist.R.layout.list_row;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Grocery> groceryItems;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;

    private LayoutInflater inflater;

    public RecyclerViewAdapter(Context context, List<Grocery> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder viewHolder, int i) {
        Grocery grocery = groceryItems.get(i);
        viewHolder.groceryItemName.setText(grocery.getName());
        viewHolder.quantity.setText(grocery.getQuantity());
        viewHolder.dateAdded.setText(grocery.getDateItemAdded());
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public TextView groceryItemName;
        public TextView quantity;
        public TextView dateAdded;
        public Button editItem;
        public Button deleteItem;
        public int id;


        public ViewHolder(@NonNull View view, Context ctx) {
            super(view);

            context = ctx;
            groceryItemName = (TextView) view.findViewById(R.id.name);
            quantity = (TextView) view.findViewById(R.id.quantity);
            dateAdded = (TextView) view.findViewById(R.id.dateAdded);

            editItem = (Button) view.findViewById(R.id.editButton);
            deleteItem = (Button) view.findViewById(R.id.deleteButton);

            editItem.setOnClickListener(this);
            deleteItem.setOnClickListener(this);

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // go to next screen / DetailsActivity
                int position = getAdapterPosition();

                Grocery grocery = groceryItems.get(position);

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("name", grocery.getName());
                intent.putExtra("quantity", grocery.getQuantity());
                intent.putExtra("id", grocery.getId());
                intent.putExtra("dateAdded", grocery.getDateItemAdded());

                context.startActivity(intent);

                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Grocery grocery = groceryItems.get(position);
            switch (v.getId()) {
                case R.id.editButton:
                    editItem(grocery);
                    break;


                case R.id.deleteButton:
                    deleteItem(grocery.getId());
                    break;

            }
        }

        public void deleteItem(final int id) {
            // create an alert dialog

            alertDialogBuilder = new AlertDialog.Builder(context);
            inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.confirmation_dialog, null);

            Button noButton = (Button) view.findViewById(R.id.noButton);
            Button yesButton = (Button) view.findViewById(R.id.yesButton);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();
            dialog.show();

            noButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            yesButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete the item
                    DatabaseHandler db = new DatabaseHandler(context);
                    db.deleteGrocery(id);
                    groceryItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    dialog.dismiss();
                }
            });
        }

        public void editItem(final Grocery grocery ) {

            alertDialogBuilder = new AlertDialog.Builder(context);

            inflater = LayoutInflater.from(context);

            View view = inflater.inflate(R.layout.popup, null);

            final EditText groceryItem = view.findViewById(R.id.groceryItem);
            final EditText groceryQty =  view.findViewById(R.id.groceryQty);

            final TextView title = view.findViewById(R.id.title);

            Button saveButton = view.findViewById(R.id.saveButton);

            alertDialogBuilder.setView(view);
            dialog = alertDialogBuilder.create();

            title.setText("Edit grocery item");
            groceryItem.setText(grocery.getName());
            groceryQty.setText(grocery.getQuantity());

            dialog.show();

            saveButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler db = new DatabaseHandler(context);

                    // update item
                    grocery.setName(groceryItem.getText().toString());
                    grocery.setQuantity(groceryQty.getText().toString());

                    if (!groceryItem.getText().toString().isEmpty() && !groceryQty.getText().toString().isEmpty()) {
                        db.updateGrocery(grocery);
                        notifyItemChanged(getAdapterPosition(), grocery);
                        dialog.dismiss();
                    } else {
                        Snackbar.make(v, "Add grocery name and quantity", Snackbar.LENGTH_LONG).show();
                    }

                }
            });
        }
    }
}
