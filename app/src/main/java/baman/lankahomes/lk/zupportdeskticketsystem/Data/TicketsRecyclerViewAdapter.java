package baman.lankahomes.lk.zupportdeskticketsystem.Data;

/**
 * Created by Baman on 7/18/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import baman.lankahomes.lk.zupportdeskticketsystem.R;

public class TicketsRecyclerViewAdapter extends RecyclerView.Adapter<TicketsRecyclerViewHolders>{

    public List<TicketsItemObject> TicketsItemList;
    private Context context;


    public TicketsRecyclerViewAdapter(Context context, List<TicketsItemObject> itemList) {
        this.TicketsItemList = itemList;
        this.context = context;
    }

    @Override
    public TicketsRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_tickets_row, null);
        TicketsRecyclerViewHolders rcv = new TicketsRecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(TicketsRecyclerViewHolders holder, int position) {

            holder.priority.setImageResource(TicketsItemList.get(position).getStatus_priority());
            holder.sts_open.setText(TicketsItemList.get(position).getStatus_open());
            holder.sts_overdue.setText(TicketsItemList.get(position).getStatus_overdue());
            holder.tkt_from.setText(TicketsItemList.get(position).getTicket_from());
            holder.tkt_subject.setText(TicketsItemList.get(position).getTicket_subject());
            holder.tkt_assignedto.setText(TicketsItemList.get(position).getTicket_assignedto());
            holder.tkt_created_date.setText(TicketsItemList.get(position).getTicket_created_date());
            holder.txt_ticket_id.setText(TicketsItemList.get(position).getTicket_id());
    }

    @Override
    public int getItemCount() {
        return this.TicketsItemList.size();
    }



}