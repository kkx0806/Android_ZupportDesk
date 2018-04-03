package baman.lankahomes.lk.zupportdeskticketsystem.Data;
/**
 * Created by baman on 6/25/16.
 */

import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import baman.lankahomes.lk.zupportdeskticketsystem.R;

public class CompanyProfileAdapter extends RecyclerView.Adapter<CompanyProfileAdapter.MyViewHolder>{
    private List<CompanyProfileData> companyProfileDataList;
    private SparseBooleanArray selectedItems;

    public String companyName;
    public String helpdeskName;
    public String companyID;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView comName, hdName, company_id;



        public MyViewHolder(View view) {
            super(view);

            comName = (TextView) view.findViewById(R.id.tv_companyName);
            hdName = (TextView) view.findViewById(R.id.tv_helpDeskName);
            company_id = (TextView) view.findViewById(R.id.tv_companyID);

            comName.setOnClickListener(this);
            hdName.setOnClickListener(this);
            company_id.setOnClickListener(this);

//            comName.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    comName.setPressed(true);
//                    return true;
//                }
//            });
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            Log.d("clicked Position", String.valueOf(position));
            companyName = comName.getText().toString();
            helpdeskName = hdName.getText().toString();
            companyID = company_id.getText().toString();

            Log.d("clicked company name",String.valueOf(comName.getText().toString()));
            Log.d("clicked hd name",String.valueOf(hdName.getText().toString()));
            Log.d("clicked CompanyId",String.valueOf(company_id.getText().toString()));

            selectLastItem(getAdapterPosition());
        }




    }

    public CompanyProfileAdapter(List<CompanyProfileData> companyList) {
        this.companyProfileDataList = companyList;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_profile_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CompanyProfileData CPData = companyProfileDataList.get(position);
        holder.comName.setText(CPData.getComName());
        holder.hdName.setText(CPData.gethdName());
        holder.company_id.setText(CPData.getcompanyID());
        holder.itemView.setBackgroundColor(selectedItems.get(position, false) ? ResourcesCompat.getColor(holder.itemView.getResources(), R.color.colorHeader, null) : Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return companyProfileDataList.size();
    }

    public void selectLastItem(int pos){
        selectedItems.clear();
        selectedItems.put(pos, true);
        notifyDataSetChanged();
    }


}






