package baman.lankahomes.lk.zupportdeskticketsystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.PickupTicketsItemObject;

/**
 * Created by Baman on 8/12/2016.
 */
public class PickupActivityFragment extends Fragment {

    public static ArrayList<PickupTicketsItemObject> support_ticket;
    public static RecyclerView Ticketslist;
    PickupActivity pick_up_activity;



    public PickupActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_pickup_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pick_up_activity = new PickupActivity();

        Ticketslist = (RecyclerView) view.findViewById(R.id.pickup_list);
        Ticketslist.setLayoutManager(new LinearLayoutManager(getActivity()));
        Ticketslist.setHasFixedSize(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pick_up_activity = new PickupActivity();


    }









}
