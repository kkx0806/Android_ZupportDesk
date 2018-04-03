package baman.lankahomes.lk.zupportdeskticketsystem.Data;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import baman.lankahomes.lk.zupportdeskticketsystem.R;

/**
 * Created by Baman on 7/26/2016.
 */
public class TicketDetailsRecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView    Tk_dtl_user_avatar;
    public TextView     Tk_dtls_name;
    public TextView     Tk_dtls_reported_date;
    public TextView     Tk_dtls_hide_show;
    public TextView     Tk_dtls_cc_address;
    public TextView     Tk_dtls_message_date;
    public TextView     Tk_dtls_message;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public TicketDetailsRecyclerViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        Tk_dtl_user_avatar = (ImageView) itemView.findViewById(R.id.IV_tfd_avatar);
        Tk_dtls_name= (TextView) itemView.findViewById(R.id.TV_tfd_name);
        Tk_dtls_reported_date = (TextView) itemView.findViewById(R.id.TV_tfd_reported_date);
        Tk_dtls_hide_show = (TextView) itemView.findViewById(R.id.TV_tfd_hide_show);
        Tk_dtls_cc_address = (TextView) itemView.findViewById(R.id.TV_tfd_cc_address);
        Tk_dtls_message_date = (TextView) itemView.findViewById(R.id.TV_tfd_message_date);
        Tk_dtls_message = (TextView) itemView.findViewById(R.id.TV_tfd_message);
    }


    @Override
    public void onClick(View v) {

    }
}
