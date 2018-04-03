package baman.lankahomes.lk.zupportdeskticketsystem.Data;

/**
 * Created by Baman on 7/18/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import baman.lankahomes.lk.zupportdeskticketsystem.R;
import baman.lankahomes.lk.zupportdeskticketsystem.TicketDetails;


public class TicketsRecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView priority;
    public TextView sts_open;
    public TextView sts_overdue;
    public TextView tkt_from;
    public TextView tkt_subject;
    public TextView tkt_assignedto;
    public TextView tkt_created_date;
    public TextView txt_ticket_id;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public TicketsRecyclerViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        priority = (ImageView) itemView.findViewById(R.id.priority_status_icon);
        sts_open= (TextView) itemView.findViewById(R.id.tv_Tk_opn_status);
        sts_overdue = (TextView) itemView.findViewById(R.id.tv_Tk_overdue);
        tkt_from = (TextView) itemView.findViewById(R.id.tv_Tk_from);
        tkt_subject = (TextView) itemView.findViewById(R.id.tv_Tk_subject);
        tkt_assignedto = (TextView) itemView.findViewById(R.id.tv_Tk_Assignedto);
        tkt_created_date = (TextView) itemView.findViewById(R.id.tv_Tk_Created_date);
        txt_ticket_id = (TextView) itemView.findViewById(R.id.tv_Tk_TicketID);

    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition();
        String ticket_id = txt_ticket_id.getText().toString();
        Log.d("ZD-clicked : ", "Position => "+String.valueOf(position)+", Ticket Id => "+ticket_id);

        Context context = view.getContext();
        Intent intent = new Intent(view.getContext(), TicketDetails.class);
        intent.putExtra("ticket_id", ticket_id);
        context.startActivity(intent);

    }




}