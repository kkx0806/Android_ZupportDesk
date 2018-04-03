package baman.lankahomes.lk.zupportdeskticketsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.ApplicationEnvironmentURL;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.MySSLSocketFactory;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.PickupTicketsAdapter;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.PickupTicketsItemObject;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.navigationDrawerFragment;

public class PickupActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private navigationDrawerFragment drawerFragment;

    ApplicationEnvironmentURL applicationEnvironment;
    ProgressDialog pDialog;
    Context context;
    String BASEURL;
    String FilteredData;
    String AllAgents;

    public String ProfileId;
    public String companyId;
    public String profileToken;

    private com.github.clans.fab.FloatingActionButton pik_fab1;
    private com.github.clans.fab.FloatingActionButton pik_fab2;
    private com.github.clans.fab.FloatingActionButton pik_fab3;
    private com.github.clans.fab.FloatingActionButton pik_fab4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Dashboard");
        setContentView(R.layout.activity_pickup);

        applicationEnvironment = new ApplicationEnvironmentURL(this.context);
        context = this.getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.app_bar_dashboard);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (navigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setup(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


        pik_fab1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.pickup_delete);
        pik_fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.pickup_close);
        pik_fab3 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.pickup_assignto);
        pik_fab4 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.pickup_pickup);
        pik_fab1.setOnClickListener(this);
        pik_fab2.setOnClickListener(this);
        pik_fab3.setOnClickListener(this);
        pik_fab4.setOnClickListener(this);

        SharedPreferences prefs = getSharedPreferences("zupportdesk", MODE_PRIVATE);
        String islogged = prefs.getString("islogged", "Not defined");
        String userid = prefs.getString("userid", "Not defined");
        profileToken = prefs.getString("profileToken", "Not defined");
        companyId = prefs.getString("companyId", "Not defined");
        String companyName = prefs.getString("companyName", "Not defined");
        ProfileId = prefs.getString("ProfileId", "Not defined");

        Log.d("islogged     : ", islogged);
        Log.d("userid       : ", userid);
        Log.d("profileToken : ", profileToken);
        Log.d("companyId    : ", companyId);
        Log.d("companyName  : ", companyName);
        Log.d("ProfileId    : ", ProfileId);

        getTickets(ProfileId, companyId, profileToken);

        View newTicket = findViewById(R.id.newtic);
        newTicket.setOnClickListener(onClickListener);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.pickup_pickup:
                    Log.d("Fab_clicked", "Pickup Ticket");
                    pickupTicketMessage("Are you sure you want to pickup the selected tickets?", "Confirm?");
                break;
            case R.id.pickup_close:
                    Log.d("Fab_clicked", "close tickets");
                    closeTicketMessage("Are you sure you want to close the selected tickets?", "Confirm?");
                break;
            case R.id.pickup_delete:
                    Log.d("Fab_clicked", "close tickets");
                    DeleteTicketMessage("Are you sure you want to delete the selected tickets?", "Confirm?");
                break;
            case R.id.pickup_assignto:
                    Log.d("Fab_clicked", "Assign to Agent");
                try {
                    assignTicketstMessage("Select an agent");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }



    /* Multiple Button on click event handle */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.newtic:
                    // Create a login URl, before starting anything

                    if(isNetworkAvailable()){
                        Intent intentTicket = new Intent(PickupActivity.this, NewTicket.class);
                        startActivity(intentTicket);
                    } else {showErrorMessage("Please check your internet connection.", "No Connectivity!"); }
                    break;
            }
        }
    };


    public void getTickets(String profileId, String companyId, String profileToken) {
        if (isNetworkAvailable()) {
            try {
                setFilteredDataURL(companyId);
                FilteredData = new getFilteredData().execute(profileToken).get();
                // adding the filtered data to shared preferences for further use.
                //adding user data to shared preferences.
                SharedPreferences.Editor editor = getSharedPreferences("zupportdesk", MODE_PRIVATE).edit();
                editor.putString("FilteredData", FilteredData);
                editor.commit();

                Log.d("ZF-Filtered_Data", FilteredData);
                setTicketsURL(profileId, companyId);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            new getNewTickets().execute(profileToken);
        } else {
            showErrorMessage("Please check your internet connection.", "No Connectivity!");
        }
    }

    private void setTicketsURL(String profile_id, String company_id) throws UnsupportedEncodingException {
        String query = "Ticket/GetTickets?$top=100&$skip=0&$orderby=CreateDateTime%20desc&$filter=%20Status%20eq%200%20and%20CompanyID%20eq%20" + company_id + "%20and%20Deleted%20eq%20false";
        Log.d("O data Query : ", query);
        String user_base_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = user_base_url + query;
        Log.d("Created URL :", BASEURL);
    }

    private void setPickupTicketURI(){
        String tickets_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = tickets_url +"Ticket/TicketAssignTo";
        Log.d("ZF-Base URL :", BASEURL);
    }

    private void setFilteredDataURL(String company_id) {
        String user_base_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = user_base_url + "Ticket/GetFilterationData?companyId=" + company_id;
        Log.d("Created URL :", BASEURL);
    }

    private void PickupTicket() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray TicketsArray = new JSONArray();

        List<Integer> selected_item = new ArrayList<>();

        for (PickupTicketsItemObject ticket : PickupActivityFragment.support_ticket) {
            if (ticket.isSelected()) {
                    selected_item.add(Integer.valueOf(ticket.getTicket_id()));
                    TicketsArray.put(Integer.valueOf(ticket.getTicket_id()));
            }
        }

        Log.d("pickup_ticket_size", String.valueOf(selected_item.size()));
        if(selected_item.size() < 1){
            Log.d("pickup_ticket_size", "empty");
            //Show Error Message
        }else {
            Log.d("pickup_ticket_size", "have tickets");

                jsonObject.put("TicketID", TicketsArray);
                jsonObject.put("ProfileId", ProfileId);
                jsonObject.put("CompanyID", companyId);

                setPickupTicketURI();
                Log.d("ZF-PickupTicket", String.valueOf(jsonObject));

           new TicketPickupRequest().execute(String.valueOf(jsonObject), profileToken);
        }
    }


    private void closeTicket() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray TicketsArray = new JSONArray();

        List<Integer> selected_item = new ArrayList<>();

        for (PickupTicketsItemObject ticket : PickupActivityFragment.support_ticket) {
            if (ticket.isSelected()) {
                selected_item.add(Integer.valueOf(ticket.getTicket_id()));
                TicketsArray.put(Integer.valueOf(ticket.getTicket_id()));
            }
        }

        Log.d("pickup_ticket_size", String.valueOf(selected_item.size()));
        if(selected_item.size() < 1){
            Log.d("close_ticket_size", "empty");
            //Show Error Message
        }else {
            Log.d("close_ticket_size", "have tickets");

            jsonObject.put("TicketID", TicketsArray);
            jsonObject.put("Status", 1);
            jsonObject.put("CompanyID", companyId);

            setPickupTicketURI();
            Log.d("ZF-PickupTicket", String.valueOf(jsonObject));

            new TicketPickupRequest().execute(String.valueOf(jsonObject), profileToken);
        }
    }


    private void assignTicketsto(String proflId) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray TicketsArray = new JSONArray();

        List<Integer> selected_item = new ArrayList<>();

        for (PickupTicketsItemObject ticket : PickupActivityFragment.support_ticket) {
            if (ticket.isSelected()) {
                selected_item.add(Integer.valueOf(ticket.getTicket_id()));
                TicketsArray.put(Integer.valueOf(ticket.getTicket_id()));
            }
        }

        Log.d("assign_ticket_size", String.valueOf(selected_item.size()));
        if(selected_item.size() < 1){
            Log.d("assign_ticket_size", "empty");
            //Show Error Message
        }else {
            Log.d("assign_ticket_size", "have tickets");

            jsonObject.put("TicketID", TicketsArray);
            jsonObject.put("ProfileId", proflId);
            jsonObject.put("CompanyID", companyId);

            setPickupTicketURI();
            Log.d("ZF-PickupTicket", String.valueOf(jsonObject));

            new TicketPickupRequest().execute(String.valueOf(jsonObject), profileToken);
        }
    }

    private void deleteTicket() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray TicketsArray = new JSONArray();

        List<Integer> selected_item = new ArrayList<>();

        for (PickupTicketsItemObject ticket : PickupActivityFragment.support_ticket) {
            if (ticket.isSelected()) {
                selected_item.add(Integer.valueOf(ticket.getTicket_id()));
                TicketsArray.put(Integer.valueOf(ticket.getTicket_id()));
            }
        }

        Log.d("pickup_ticket_size", String.valueOf(selected_item.size()));
        if(selected_item.size() < 1){
            Log.d("close_ticket_size", "empty");
            //Show Error Message
        }else {
            Log.d("close_ticket_size", "have tickets");

            jsonObject.put("TicketID", TicketsArray);
            jsonObject.put("Deleted", 1);
            jsonObject.put("CompanyID", companyId);

            setPickupTicketURI();
            Log.d("ZF-PickupTicket", String.valueOf(jsonObject));

            new TicketPickupRequest().execute(String.valueOf(jsonObject), profileToken);
        }
    }


    private void showSuccessMessage(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // restart Activity
                        finish();
                        startActivity(getIntent());
                    }
                })
                .setIcon(R.drawable.notification_success)
                .show();
    }


    private void assignTicketstMessage(String title) throws JSONException {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title);

        final List<String> agentList = new ArrayList<String>();
        final List<String> agentProfileIDList = new ArrayList<String>();

        if(AllAgents != null && !AllAgents.isEmpty() && !AllAgents.equals("null")) {

            JSONArray jsonarray = new JSONArray(AllAgents);
            int count = jsonarray.length();
            Log.d("Full Agents", String.valueOf(count));


            for (int k = 0; k < jsonarray.length(); k++) {
                JSONObject jsonobject5 = jsonarray.getJSONObject(k);
                Log.d("Agent object ", String.valueOf(jsonobject5));
                agentList.add(jsonobject5.getString("FirstName"));
                agentProfileIDList.add(jsonobject5.getString("ProfileId"));
            }
            String[] types = new String[agentList.size()];

            for (int j = 0; j < agentList.size(); j++) {
                types[j] = agentList.get(j);
            }

            b.setItems(types, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("Selected_no ", String.valueOf(which));
                    Log.d("Selected_agent ", String.valueOf(agentList.get(which)));
                    Log.d("Selected_profile_id ", String.valueOf(agentProfileIDList.get(which)));

                    dialog.dismiss();
                    assignTicketstMessage2(String.valueOf(agentList.get(which)), String.valueOf(agentProfileIDList.get(which)));
                }
            });

            b.show();
        }
    }


    private void assignTicketstMessage2(String agent, final String profil_id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to assign the selected tickets to agent "+ agent +"?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                try {
                    assignTicketsto(profil_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private void closeTicketMessage(String data, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(data);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                try {
                    closeTicket();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void DeleteTicketMessage(String data, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(data);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                try {
                    deleteTicket();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }



    private void pickupTicketMessage(String data, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(data);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                try {
                    PickupTicket();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(),Settings.class);
            startActivity(i);
            return true;
        }
        if(id == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.notification){
            Intent i = new Intent(getApplicationContext(), Notifications.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showErrorMessage(String data, String title) {
        new AlertDialog.Builder(this.context)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.notification_red)
                .show();
    }

    private void showErrorMessageNoInbox(String data, String title) {
        new AlertDialog.Builder(this.context)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        startActivity(i);
                    }
                })
                .setIcon(R.drawable.notification_red)
                .show();
    }


    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }




    /*************************** Get the filtered data ASync Task********************************************************/

    class getFilteredData extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                // get filtered data
                HttpClient httpclient = getNewHttpClient();
                HttpGet httpget = new HttpGet(BASEURL);
                httpget.addHeader("Authorization", "Bearer " + arg0[0]);
                // Execute HTTP get Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String responseStng = EntityUtils.toString(entity, "UTF-8");
                Log.d("Filtered Data : ", responseStng);
                return responseStng;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /****************************Gwt All Tickets Async Task********************************************/

    class getNewTickets extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PickupActivity.this);
            pDialog.setMessage("Please wait. Loading tickets...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                // get user token
                HttpClient httpclient = getNewHttpClient();
                HttpGet httpget = new HttpGet(BASEURL);
                httpget.addHeader("Authorization", "Bearer " + arg0[0]);
                // Execute HTTP get Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                pDialog.dismiss();
                Log.d("Results Ticket : ", responseString);
                return responseString;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String resultData) {
            Log.d("Results         :  ", resultData);

            JSONObject jsonObj = null;
            try {
                PickupActivityFragment.support_ticket = new ArrayList<>();
                jsonObj = new JSONObject(resultData);
                String itemsCount = jsonObj.getString("Count");
                JSONArray itemsArray = jsonObj.getJSONArray("Items");

                Log.d("No of Tickets   : ", String.valueOf(itemsCount));
                Log.d("Items Lists     : ", String.valueOf(itemsArray));

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject individualObject = itemsArray.getJSONObject(i);
                    Log.d("Items Array item: ", String.valueOf(individualObject));
                    String Subject = individualObject.getString("Subject");
                    String overdue = individualObject.getString("OverdueDateTime");
                    String TicketStatus = individualObject.getString("TicketStatus");
                    String AssignedTo = individualObject.getString("AssignedTo");
                    String CreateDate = individualObject.getString("CreateDate");
                    String TicketID = individualObject.getString("TicketID");
                    String Priority = individualObject.getString("Priority");
                    String Status = individualObject.getString("Status");
                    String From = individualObject.getString("From");
                    String AsignedToName = "Unassigned";

                    JSONObject jsonObj2 = new JSONObject(FilteredData);
                    String objectString = jsonObj2.getString("ReturnObj");
                    JSONObject AgentJsonObj = new JSONObject(objectString);
                    String agents = AgentJsonObj.getString("Agents");
                    String inboxes = AgentJsonObj.getString("Inboxes");
                    AllAgents = agents;
                    Log.d("Full_Agents", String.valueOf(agents));
                    Log.d("Full_inboxes", String.valueOf(inboxes));

                    if (AgentJsonObj.isNull("Inboxes")) {
                        showErrorMessageNoInbox("You don't have any inbox.", "ERROR");
                        Log.d("Full_inboxes", " : is null - value - 0");
                    }


                    JSONArray jsonarray = new JSONArray(agents);
                    int count = jsonarray.length();
                    Log.d("Full Agents", String.valueOf(count));
                    for (int k = 0; k < jsonarray.length(); k++) {
                        JSONObject jsonobject5 = jsonarray.getJSONObject(k);
                        Log.d("Agent object ", String.valueOf(jsonobject5));
                        String firstName = jsonobject5.getString("FirstName");
                        String profileID = jsonobject5.getString("ProfileId");
                        if (profileID.equals(AssignedTo)) {
                            AsignedToName = firstName;
                        }
                    }

                    String TicketStatusValue;
                    switch (TicketStatus) {
                        case "0":
                            TicketStatusValue = "Open";
                            break;
                        case "1":
                            TicketStatusValue = "closed";
                            break;
                        case "2":
                            TicketStatusValue = "OnHold";
                            break;
                        default:
                            TicketStatusValue = "Open";
                            break;
                    }

                    String PriorityValue;
                    int PriorityBar = 0;
                    switch (Priority) {
                        case "0":
                            PriorityValue = "Low";
                            PriorityBar = R.drawable.sm_icon_low_priority;
                            break;
                        case "1":
                            PriorityValue = "Medium";
                            PriorityBar = R.drawable.sm_icon_medium_priority;
                            break;
                        case "2":
                            PriorityValue = "High";
                            PriorityBar = R.drawable.sm_icon_high_priority;
                            break;
                        case "3":
                            PriorityValue = "Urgent";
                            PriorityBar = R.drawable.sm_icon_urgent_priority;
                            break;
                        default:
                            PriorityValue = "Low";
                            PriorityBar = R.drawable.sm_icon_low_priority;
                            break;
                    }


                    PickupTicketsItemObject ticket = new PickupTicketsItemObject();
                    ticket.setStatus_priority(PriorityBar);
                    ticket.setStatus_open(TicketStatusValue);
                    ticket.setStatus_overdue("Ticket ID : #" + TicketID);
                    ticket.setTicket_from(From);
                    ticket.setTicket_subject(Subject);
                    ticket.setTicket_assignedto("Agent : " + AsignedToName);
                    ticket.setTicket_created_date("Created on : " + CreateDate.substring(0, 10));
                    ticket.setTicket_id(TicketID);
                    PickupActivityFragment.support_ticket.add(ticket);

                    PickupTicketsAdapter adapter = new PickupTicketsAdapter(PickupActivityFragment.support_ticket);
                    PickupActivityFragment.Ticketslist.setAdapter(adapter);


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            pDialog.dismiss();
        }
    }
    /***********************************************************************************************/



    /**************************************Ticket selection option Async Task *********************/
        /*  Async Task to send response*/
    class TicketPickupRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PickupActivity.this);
            pDialog.setMessage("Please wait. Sending request...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                //Post Username and password
                HttpClient httpclient = getNewHttpClient();
                HttpPost httppost = new HttpPost(BASEURL);
                httppost.addHeader("Authorization", "Bearer "+arg0[1]);
                httppost.addHeader("Content-Type", "application/json;charset=UTF-8");
                httppost.setEntity(new ByteArrayEntity(
                        arg0[0].toString().getBytes("UTF8")));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                Log.d("ZF-TicketClose ", responseString);
                return responseString;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String resultData) {
            Log.d("ZF-TicketClose ", resultData);
            pDialog.dismiss();
            showSuccessMessage( "Your request has been completed.", "Success!");
        }
    }




}



