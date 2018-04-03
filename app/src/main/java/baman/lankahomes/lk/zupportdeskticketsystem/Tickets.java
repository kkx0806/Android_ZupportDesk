package baman.lankahomes.lk.zupportdeskticketsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.ApplicationEnvironmentURL;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.MySSLSocketFactory;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.SimpleDividerItemDecoration;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.TicketsItemObject;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.TicketsRecyclerViewAdapter;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.navigationDrawerFragment;

public class Tickets extends AppCompatActivity {

    private Toolbar toolbar;
    private navigationDrawerFragment drawerFragment;
    ApplicationEnvironmentURL applicationEnvironment;
    ProgressDialog pDialog;
    Context context;
    String BASEURL;
    String FilteredData;
    String  Activity_title;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TicketsRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Activity_title = intent.getStringExtra("activity_title");
        setTitle(Activity_title);
        setContentView(R.layout.activity_tickets);

        applicationEnvironment = new ApplicationEnvironmentURL(this.context);
        context = this.getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.app_bar_dashboard);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (navigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setup(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        SharedPreferences prefs = getSharedPreferences("zupportdesk", MODE_PRIVATE);
        String islogged = prefs.getString("islogged", "Not defined");
        String userid = prefs.getString("userid", "Not defined");
        String profileToken = prefs.getString("profileToken", "Not defined");
        String companyId = prefs.getString("companyId", "Not defined");
        String companyName = prefs.getString("companyName", "Not defined");
        String ProfileId = prefs.getString("ProfileId", "Not defined");

        Log.d("ZD-islogged     : ", islogged);
        Log.d("ZD-userid       : ", userid);
        Log.d("ZD-profileToken : ", profileToken);
        Log.d("ZD-companyId    : ", companyId);
        Log.d("ZD-companyName  : ", companyName);
        Log.d("ZD-ProfileId    : ", ProfileId);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_tickets);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        layoutManager = new LinearLayoutManager(Tickets.this);
        recyclerView.setLayoutManager(layoutManager);

        getMyTickets(ProfileId, companyId, profileToken);
        View newTicket = findViewById(R.id.newtic);
        newTicket.setOnClickListener(onClickListener);
    }

    /* Multiple Button on click event handle */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.newtic:
                    // Create a login URl, before starting anything

                    if(isNetworkAvailable()){
                        Intent intentTicket = new Intent(Tickets.this, NewTicket.class);
                        startActivity(intentTicket);
                    } else {showErrorMessage("Please check your internet connection.", "No Connectivity!"); }
                    break;
            }
        }
    };

    private void getMyTickets(String profileId, String companyId, String profileToken){
        if(isNetworkAvailable()) {
            try {
                setFilteredDataURL(companyId);
                FilteredData = new getFilteredData().execute(profileToken).get();
                Log.d("ZF-Filtration_data", FilteredData);
                setTicketsURL(profileId, companyId);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            new getNewTickets().execute(profileToken);
        }else{ showErrorMessage("Please check your internet connection.", "No Connectivity!"); }
    }

    private void setTicketsURL(String profile_id, String company_id) throws UnsupportedEncodingException {
        String query = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String TodaysDate = dateFormat.format(new Date());
        switch (Activity_title){
            case "All Tickets":
                //All Tickets section is removed from this and Redirected to pickupActivity
                Log.d("ZD-Selected Option : ", "Tickets");
                query ="Ticket/GetTickets?$top=50&$skip=0&$orderby=CreateDateTime%20desc&$filter=CompanyID%20eq%20"+company_id+"%20and%20Deleted%20eq%20false%20and%20AssignedTo%20eq%20%27"+profile_id+"%27";
                break;
            case "Overdue Tickets":
                Log.d("ZD-Selected Option : ", "Overdue Tickets");
                query ="Ticket/GetTickets?$top=50&$skip=0&$orderby=CreateDateTime%20desc&$filter=%20OverdueDateTime%20lt%20"+TodaysDate+"%20and%20CompanyID%20eq%20"+company_id+"%20and%20Deleted%20eq%20false";
                break;
            case "Open Tickets":
                Log.d("ZD-Selected Option : ", "Open Tickets");
                query ="Ticket/GetTickets?$top=50&$skip=0&$orderby=CreateDateTime%20desc&$filter=CompanyID%20eq%20"+company_id+"%20and%20Deleted%20eq%20false%20and%20Status%20eq%20"+0+"%20and%20AssignedTo%20eq%20%27"+profile_id+"%27";
                break;
            case "On Hold":
                Log.d("ZD-Selected Option : ", "On Hold");
                query ="Ticket/GetTickets?$top=50&$skip=0&$orderby=CreateDateTime%20desc&$filter=CompanyID%20eq%20"+company_id+"%20and%20Deleted%20eq%20false%20and%20Status%20eq%20"+2+"%20and%20AssignedTo%20eq%20%27"+profile_id+"%27";
                break;
            case "Due Today":
                Log.d("ZD-Selected Option : ", "Due Today");
                query ="Ticket/GetTickets?$top=50&$skip=0&$orderby=CreateDateTime%20desc&$filter=%20TodayDate%20eq%20"+TodaysDate+"%20and%20CompanyID%20eq%20"+company_id+"%20and%20Deleted%20eq%20false";
                break;
            case "Unassigned":
                Log.d("ZD-Selected Option : ", "Unassigned");
                //query ="Ticket/GetTickets?$top=15&$skip=0&$orderby=CreateDateTime%20desc&$filter=CompanyID%20eq%20"+company_id+"%20and%20Deleted%20eq%20false%20and%20Status%20eq%200%20and%20AssignedTo%20eq%20%27"+profile_id+"%27";
                query ="Ticket/GetTickets?$top=50&$skip=0&$orderby=CreateDateTime%20desc&$filter=CompanyID%20eq%20"+company_id+"%20and%20Deleted%20eq%20false%20and%20Status%20eq%200%20and%20AssignedTo%20eq%20%27-1%27";
                break;
            default:
                Log.d("ZD-Selected Option : ", "Default");
                query ="Ticket/GetTickets?$top=50&$skip=0&$orderby=CreateDateTime%20desc&$filter=CompanyID%20eq%20"+company_id+"%20and%20Deleted%20eq%20false%20and%20Status%20eq%200%20and%20AssignedTo%20eq%20%27"+profile_id+"%27";
                break;
        }

        Log.d("ZD-O data Query : ", query);
        String user_base_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = user_base_url +query;
        Log.d("ZD-Created URL :", BASEURL);
    }

    private void setFilteredDataURL(String company_id) {
        String user_base_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = user_base_url +"Ticket/GetFilterationData?companyId="+company_id;
        Log.d("ZD-Created URL :", BASEURL);
    }

      private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showErrorMessage(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
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

    /**************************************************************************************************************************************/
    //Async tak to get Profile id
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
                httpget.addHeader("Authorization", "Bearer "+arg0[0]);
                // Execute HTTP get Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String responseStng = EntityUtils.toString(entity, "UTF-8");
                Log.d("ZD-Filtered Data : ", responseStng);
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

    /**************************************************************************************************************************************/
    //Async tak to get Profile data
    class getNewTickets extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Tickets.this);
            pDialog.setMessage("Please wait. Loading tickets...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                // get user token
                HttpClient httpclient = getNewHttpClient();
                HttpGet httpget = new HttpGet(BASEURL);
                httpget.addHeader("Authorization", "Bearer "+arg0[0]);
                // Execute HTTP get Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                pDialog.dismiss();
                Log.d("ZD-Results Ticket : ", responseString);
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
            Log.d("ZD-Results         :  ", resultData);

            List<TicketsItemObject> items = new ArrayList<TicketsItemObject>();
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject(resultData);
                String itemsCount = jsonObj.getString("Count");
                JSONArray itemsArray= jsonObj.getJSONArray("Items");

                Log.d("ZD-No of Tickets   : ", String.valueOf(itemsCount));
                Log.d("ZD-Items Lists     : ", String.valueOf(itemsArray));

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject individualObject =  itemsArray.getJSONObject(i);
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
                    String AsignedToName="Unassigned";

                    JSONObject jsonObj2 = new JSONObject(FilteredData);
                    String objectString = jsonObj2.getString("ReturnObj");
                    JSONObject AgentJsonObj = new JSONObject(objectString);
                    String agents = AgentJsonObj.getString("Agents");

                    JSONArray jsonarray = new JSONArray(agents);
                    int count = jsonarray.length();
                   // Log.d("Full Agents", String.valueOf(count));
                    for (int k = 0; k < jsonarray.length(); k++) {
                        JSONObject jsonobject5 = jsonarray.getJSONObject(k);
                        //Log.d("Agent object ", String.valueOf(jsonobject5));
                        String firstName = jsonobject5.getString("FirstName");
                        String profileID = jsonobject5.getString("ProfileId");
                        if(profileID.equals(AssignedTo)){AsignedToName=firstName;}
                    }

                    String TicketStatusValue;
                    switch (Status){
                        case "0" :
                            TicketStatusValue = "Open";
                            break;
                        case "1":
                            TicketStatusValue = "Closed";
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
                    switch (Priority){
                        case "0" :
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
                    items.add(new TicketsItemObject(PriorityBar, TicketStatusValue, "Overdue on : "+overdue.substring(0,10), From, Subject, "Assigned to : "+AsignedToName, "Created on : "+CreateDate.substring(0,10), TicketID));
                    adapter = new TicketsRecyclerViewAdapter(Tickets.this, items);
                    recyclerView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
        }
    }
    /**************************************************************************************************************************************/


}
