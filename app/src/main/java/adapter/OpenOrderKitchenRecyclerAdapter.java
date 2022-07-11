package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bosictsolution.quickwaiter.R;

import java.util.Collections;
import java.util.List;

import data.OpenOrderKitchenData;
import holder.OpenOrderKitchenViewHolder;

/**
 * Created by User on 9/19/2017.
 */
public class OpenOrderKitchenRecyclerAdapter extends RecyclerView.Adapter<OpenOrderKitchenViewHolder> {

    List<OpenOrderKitchenData> list = Collections.emptyList();
    Context context;

    public OpenOrderKitchenRecyclerAdapter(List<OpenOrderKitchenData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public OpenOrderKitchenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_child_open_order_kitchen, parent, false);
        OpenOrderKitchenViewHolder holder = new OpenOrderKitchenViewHolder(v);
        return holder;

    }

    @Override
    public void onBindViewHolder(OpenOrderKitchenViewHolder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.tvItem.setText(list.get(position).getItemName());
        holder.tvQuantity.setText(list.get(position).getStringQty());
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, OpenOrderKitchenData data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(OpenOrderKitchenData data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }
}
