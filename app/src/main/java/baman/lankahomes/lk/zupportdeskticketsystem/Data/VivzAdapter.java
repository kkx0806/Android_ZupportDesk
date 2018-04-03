package baman.lankahomes.lk.zupportdeskticketsystem.Data;

/**
 * Created by Baman on 7/15/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import baman.lankahomes.lk.zupportdeskticketsystem.R;

/**
 * Created by baman on 10/11/15.
 */
public class VivzAdapter extends RecyclerView.Adapter<VivzAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    List<baman.lankahomes.lk.zupportdeskticketsystem.Data.Information> data = Collections.emptyList();
    private  Context context;
    private ClickListener clicklistner;
    private SparseBooleanArray selectedItems;

    public VivzAdapter(Context context, List<Information> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

        selectedItems = new SparseBooleanArray();
    }

    public void delete(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void setClicklistner(ClickListener clicklistner){
        this.clicklistner = clicklistner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Information current = data.get(position);
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);
        holder.itemView.setBackgroundColor(selectedItems.get(position, false) ? ResourcesCompat.getColor(holder.itemView.getResources(), R.color.colorHeader, null) : Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void selectLastItem(int pos){
        selectedItems.clear();
        selectedItems.put(pos, true);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title;
        ImageView icon;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.listMessage);
            icon = (ImageView) itemView.findViewById(R.id.listIcon);
        }


        @Override
        public void onClick(View v) {

            selectLastItem(getAdapterPosition());

            if(clicklistner != null){
                clicklistner.itemClicked(v, getPosition());
            }
        }
    }

    public interface ClickListener{
        public void itemClicked(View view, int position);
    }
}
