package baman.lankahomes.lk.zupportdeskticketsystem.Data;

/**
 * Created by Baman on 7/15/2016.
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import baman.lankahomes.lk.zupportdeskticketsystem.PickupActivity;
import baman.lankahomes.lk.zupportdeskticketsystem.R;
import baman.lankahomes.lk.zupportdeskticketsystem.Tickets;

/**
 * A simple {@link Fragment} subclass.
 */
public class navigationDrawerFragment extends android.support.v4.app.Fragment implements VivzAdapter.ClickListener{

    private RecyclerView recyclerView2;
    private VivzAdapter adapter;
    public static final String PREF_FILE_Name = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;
    private View containerView;
    static List<Information> data = new ArrayList<>();

    public navigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER,  "false"));
        if(savedInstanceState != null){
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView2 = (RecyclerView) layout.findViewById(R.id.drawerList);

        adapter = new VivzAdapter(getActivity(), getData());
        adapter.setClicklistner(this);
        recyclerView2.setAdapter(adapter);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public static List<Information> getData(){
        if(data.size() ==0){
            int[] icons = {
                    R.drawable.ticket,
                    R.drawable.ticket_overdue,
                    R.drawable.ticket_open,
                    R.drawable.ticket_hold,
                    R.drawable.ticket_today_due,
                    R.drawable.ticket_unasigned,};

            String[] titles = { "All Tickets","Overdue Tickets","Open Tickets","On Hold", "Due Today",
                                "Unassigned"};
            for (int k=0; k < icons.length && k < titles.length; k++){
                Information current = new Information();
                current.iconId = icons[k];
                current.title = titles[k];
                data.add(current);
            }
        }
        return data;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView =getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close ){
            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer+"");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset < 0.3) {
                    toolbar.setAlpha(1 - slideOffset);
                }
            }
        };

        // Checking if the user is first time  user and show him we have a drawer
        if (!mUserLearnedDrawer && !mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // add a hamburger icon to toggle drawer
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences (Context context, String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_Name, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_Name, context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    @Override
    public void itemClicked(View view, int position) {

        Toast.makeText(getActivity(), "Loading. Please wait...", Toast.LENGTH_LONG).show();

        String clicked = String.valueOf(data.get(position).getTitle());
        Intent intent = new Intent(getActivity(), Tickets.class);

        switch (clicked){
            case "All Tickets":
                Log.d("Title", "All Tickets");
                Intent intent2 = new Intent(getActivity(), PickupActivity.class);
                intent2.putExtra("activity_title", clicked);
                startActivity(intent2);
                break;
            case "Overdue Tickets":
                Log.d("Title", "Overdue Tickets");
                intent.putExtra("activity_title", clicked);
                startActivity(intent);
                break;
            case "Open Tickets":
                Log.d("Title", "Overdue Tickets");
                intent.putExtra("activity_title", clicked);
                startActivity(intent);
                break;
            case "On Hold":
                Log.d("Title", "Open Tickets");
                intent.putExtra("activity_title", clicked);
                startActivity(intent);
                break;
            case "Due Today":
                Log.d("Title", "Due Today");
                intent.putExtra("activity_title", clicked);
                startActivity(intent);
                break;
            case "Unassigned":
                Log.d("Title", "Unassigned");
                intent.putExtra("activity_title", clicked);
                startActivity(intent);
                break;
            default:
                Log.d("Title", "default");
                intent.putExtra("activity_title", clicked);
                startActivity(intent);
                break;
        }

    }


}


