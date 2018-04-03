package baman.lankahomes.lk.zupportdeskticketsystem.Data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import baman.lankahomes.lk.zupportdeskticketsystem.R;

/**
 * Created by Baman on 7/26/2016.
 */
public class TicketDetailsRecyclerViewAdapter extends RecyclerView.Adapter<TicketDetailsRecyclerViewHolders>{

    public List<TicketDetailsItemObject> TicketDetailsItemList;
    private Context context;
    public int expandedPosition = -1;


    public TicketDetailsRecyclerViewAdapter(Context context, List<TicketDetailsItemObject> itemList) {
        this.TicketDetailsItemList = itemList;
        this.context = context;
    }

    @Override
    public TicketDetailsRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_ticket_details_row, null);
        TicketDetailsRecyclerViewHolders rcv = new TicketDetailsRecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(TicketDetailsRecyclerViewHolders holder, int position) {

        holder.Tk_dtl_user_avatar.setImageResource(TicketDetailsItemList.get(position).getTk_dtls_avatar());
        holder.Tk_dtls_name.setText(TicketDetailsItemList.get(position).getTk_dtls_name());
        holder.Tk_dtls_reported_date.setText(TicketDetailsItemList.get(position).getTk_dtls_reported_date());
        holder.Tk_dtls_hide_show.setText(TicketDetailsItemList.get(position).getTk_dtls_hide_show());
        holder.Tk_dtls_cc_address.setText(TicketDetailsItemList.get(position).getTk_dtls_cc_address());
        holder.Tk_dtls_message_date.setText(TicketDetailsItemList.get(position).getTk_dtls_msg_date());
        holder.Tk_dtls_message.setText((Html.fromHtml(TicketDetailsItemList.get(position).getTk_dtls_message())));

    }

    @Override
    public int getItemCount() {
        return this.TicketDetailsItemList.size();
    }
}
