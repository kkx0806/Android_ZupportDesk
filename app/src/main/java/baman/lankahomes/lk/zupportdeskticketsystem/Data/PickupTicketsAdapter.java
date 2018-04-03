package baman.lankahomes.lk.zupportdeskticketsystem.Data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import baman.lankahomes.lk.zupportdeskticketsystem.R;

/**
 * Created by Baman on 8/12/2016.
 */
public class PickupTicketsAdapter extends RecyclerView.Adapter<PickupTicketsAdapter.ViewHolder> {

    ArrayList<PickupTicketsItemObject> all_tickets;

    public PickupTicketsAdapter(List<PickupTicketsItemObject> tickets) {
        this.all_tickets = new ArrayList<>(tickets);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_pick_tickets, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindData(all_tickets.get(position));

        //in some cases, it will prevent unwanted situations
        holder.checkbox.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkbox.setChecked(all_tickets.get(position).isSelected());

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                all_tickets.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return all_tickets.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView priority;
        public TextView sts_open;
        public TextView sts_overdue;
        public TextView tkt_from;
        public TextView tkt_subject;
        public TextView tkt_assignedto;
        public TextView tkt_created_date;
        public TextView txt_ticket_id;
        public CheckBox checkbox;

        public ViewHolder(View v) {
            super(v);

            checkbox = (CheckBox) v.findViewById(R.id.pick_checkbox);
            priority = (ImageView) v.findViewById(R.id.priority_status_icon);
            sts_open= (TextView) v.findViewById(R.id.tv_Tk_opn_status);
            sts_overdue = (TextView) v.findViewById(R.id.tv_Tk_overdue);
            tkt_from = (TextView) v.findViewById(R.id.tv_Tk_from);
            tkt_subject = (TextView) v.findViewById(R.id.tv_Tk_subject);
            tkt_assignedto = (TextView) v.findViewById(R.id.tv_Tk_Assignedto);
            tkt_created_date = (TextView) v.findViewById(R.id.tv_Tk_Created_date);
            txt_ticket_id = (TextView) v.findViewById(R.id.tv_Tk_TicketID);
        }

        public void bindData(PickupTicketsItemObject ticket) {

            priority.setImageResource(ticket.getStatus_priority());
            sts_open.setText(ticket.getStatus_open());
            sts_overdue.setText(ticket.getStatus_overdue());
            tkt_from.setText(ticket.getTicket_from());
            tkt_subject.setText(ticket.getTicket_subject());
            tkt_assignedto.setText(ticket.getTicket_assignedto());
            tkt_created_date.setText(ticket.getTicket_created_date());
            txt_ticket_id.setText(ticket.getTicket_id());
        }
    }
}