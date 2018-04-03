package baman.lankahomes.lk.zupportdeskticketsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.ApplicationEnvironmentURL;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.MySSLSocketFactory;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.SimpleDividerItemDecoration;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.TicketDetailsItemObject;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.TicketDetailsRecyclerViewAdapter;

public class TicketDetails extends AppCompatActivity implements View.OnClickListener{
    ImageView priority_bar;
    TextView tv_ticket_id;
    ImageView back_arrow;
    TextView ticket_status;
    TextView ticket_overdue;
    TextView ticket_subject;
    ImageView edit_button;

    public String sender_email;
    public String mail_subject;
    public String  InboxID;
    public String  Ticket_CreatedBy;
    public String  Ticket_CompanyID;
    public String  Ticket_ProfileID;
    public String  Ticket_TicketID;
    public String  FilteredData;
    public String profileToken;

    Context context;
    ProgressDialog pDialog;
    ProgressDialog pDialog2;
    public String BASEURL;
    public ApplicationEnvironmentURL applicationEnvironment;

    private com.github.clans.fab.FloatingActionButton fab1,fab2,fab3;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TicketDetailsRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_details_floating);

        applicationEnvironment = new ApplicationEnvironmentURL(this.context);
        context = this.getApplicationContext();

        Intent intent = getIntent();
        String ticket_id = intent.getStringExtra("ticket_id");

        edit_button = (ImageView) findViewById(R.id.IV_td_edit_ticket);
        ticket_status = (TextView) findViewById(R.id.tv_td_open_status);
        ticket_overdue = (TextView) findViewById(R.id.tv_td_overdue);
        ticket_subject= (TextView) findViewById(R.id.tv_td_subject);

        priority_bar = (ImageView) findViewById(R.id.IV_td_priority_bar);
        tv_ticket_id = (TextView) findViewById(R.id.tv_TD_ticket_id);
        tv_ticket_id.setText("  # "+ticket_id+" # ");

        back_arrow = (ImageView) findViewById(R.id.IV_td_leftarrow_bar);
        back_arrow.setOnClickListener(onClickListener);
        edit_button.setOnClickListener(onClickListener);
        /*********************** Floating Icon *****************************************************/

        fab1 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab2);
        fab3 = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab3);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        /*******************************************************************************************/

        /*********************** Recycler View*****************************************************/
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_tickets_details);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        layoutManager = new LinearLayoutManager(TicketDetails.this);
        recyclerView.setLayoutManager(layoutManager);
        /*******************************************************************************************/

        SharedPreferences prefs = getSharedPreferences("zupportdesk", MODE_PRIVATE);
        profileToken = prefs.getString("profileToken", "Not defined");
        String profileid = prefs.getString("ProfileId", "Not defined");
        FilteredData = prefs.getString("FilteredData", "Not defined");

        Log.d("ZD-profileToken : ", profileToken);
        setTicketsDetailsURL(ticket_id);
        new ticketDetails().execute(profileToken);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTicketsHistoryURL(ticket_id, profileid);
        new ticketHistory().execute(profileToken);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(TicketDetails.this, TicketResponse.class);
        int id = v.getId();
        switch (id){
            case R.id.fab1:
                //activity Replay
                intent.putExtra("response_title", "Reply");
                intent.putExtra("sender_email", sender_email);
                intent.putExtra("Ticket_CreatedBy", Ticket_CreatedBy);
                intent.putExtra("Ticket_ProfileID", Ticket_ProfileID);
                intent.putExtra("Ticket_CompanyID", Ticket_CompanyID);
                intent.putExtra("Ticket_TicketID", Ticket_TicketID);
                intent.putExtra("InboxID", InboxID);
                intent.putExtra("mail_subject", mail_subject);
                startActivity(intent);
                break;
            case R.id.fab2:
                //activity note
                Intent intent2 = new Intent(TicketDetails.this, AddNote.class);
                intent2.putExtra("response_title", "Add Note");
                intent2.putExtra("sender_email", sender_email);
                intent2.putExtra("Ticket_CreatedBy", Ticket_CreatedBy);
                intent2.putExtra("Ticket_ProfileID", Ticket_ProfileID);
                intent2.putExtra("Ticket_CompanyID", Ticket_CompanyID);
                intent2.putExtra("Ticket_TicketID", Ticket_TicketID);
                intent2.putExtra("InboxID", InboxID);
                intent2.putExtra("mail_subject", mail_subject);
                startActivity(intent2);
             break;
            case R.id.fab3:
                //activity forward
                intent.putExtra("response_title", "Forward");
                intent.putExtra("sender_email", sender_email);
                intent.putExtra("Ticket_CreatedBy", Ticket_CreatedBy);
                intent.putExtra("Ticket_ProfileID", Ticket_ProfileID);
                intent.putExtra("Ticket_CompanyID", Ticket_CompanyID);
                intent.putExtra("Ticket_TicketID", Ticket_TicketID);
                intent.putExtra("InboxID", InboxID);
                intent.putExtra("mail_subject", mail_subject);
                startActivity(intent);
                break;
        }
    }

    private void setTicketsDetailsURL(String Ticketsid){
        String tickets_url = applicationEnvironment.get_ticket_api_url(this.context);
        String query ="GetTicket?TicketID=%20"+Ticketsid+"";
        BASEURL = tickets_url +"Ticket/"+query;
        Log.d("ZF-Base URL :", BASEURL);
    }

    private void setTicketsHistoryURL(String Ticketsid, String profileid){
        String tickets_url = applicationEnvironment.get_ticket_api_url(this.context);
        String query ="GetTicketHistory?TicketID=%20"+Ticketsid+"&profileId="+profileid;
        BASEURL = tickets_url +"Ticket/"+query;
        Log.d("ZF-Base URL :", BASEURL);
    }

    private void setCloseTicketURI(){
        String tickets_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = tickets_url +"Ticket/TicketAssignTo";
        Log.d("ZF-Base URL :", BASEURL);
    }

    /* Multiple Button on click event handle */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.IV_td_leftarrow_bar:
                    onBackPressed();
                    Log.d("ticket details", "left arrow clicked");
                    break;

                case R.id.IV_td_edit_ticket:
                    PopupMenu popup = new PopupMenu(getApplicationContext(), edit_button);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_ticket_details, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                          //  Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_SHORT).show();
                            if(item.getTitle().equals("Close Ticket")){
                                closeTicketMessage("Are you sure you want to close this ticket?", "Confirm?");
                            }
                            return true;
                        }
                    });
                    popup.show();//showing popup menu
                    break;

            }
        }
    };

    private void closeTicketMessage(String data, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(data);

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
                    try {
                        closingTicket();
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

    private void closingTicket() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray TicketsArray = new JSONArray();

        TicketsArray.put(Integer.valueOf(Ticket_TicketID));
        jsonObject.put("TicketID", TicketsArray);
        jsonObject.put("Status", 1);
        jsonObject.put("CompanyID", Ticket_CompanyID);

        setCloseTicketURI();
        Log.d("ZF-closeTicket", String.valueOf(jsonObject));
        new SendCloseRequest().execute(String.valueOf(jsonObject), profileToken);
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

    /***********************************************************************************************/

    //Async tak to get Ticket Details
    class ticketDetails extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(TicketDetails.this);
            pDialog2.setMessage("Please wait. Fetching Ticket Details...");
            pDialog2.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                HttpClient httpclient = getNewHttpClient();
                HttpGet httpget = new HttpGet(BASEURL);
                httpget.addHeader("Authorization", "Bearer "+arg0[0]);
                Log.d("ZF-TicketDetails_url : ", BASEURL);
                // Execute HTTP get Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String responseprofileString = EntityUtils.toString(entity, "UTF-8");
                Log.d("ZF-TicketDetails : ", responseprofileString);
                return responseprofileString;
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

            // Check the results data
            Log.d("ZF-TickResults data : ", resultData);
            try {
                JSONObject jsonObj = new JSONObject(resultData);
                String  Priority = jsonObj.getJSONObject("ReturnObj").getString("Priority");
                String  OverdueDateTimeAsString = jsonObj.getJSONObject("ReturnObj").getString("OverdueDateTimeAsString");
                String  TicketStatus = jsonObj.getJSONObject("ReturnObj").getString("TicketStatus");
                String  Subject = jsonObj.getJSONObject("ReturnObj").getString("Subject");
                InboxID = jsonObj.getJSONObject("ReturnObj").getString("InboxID");
                Ticket_CreatedBy = jsonObj.getJSONObject("ReturnObj").getString("From");
                Ticket_CompanyID = jsonObj.getJSONObject("ReturnObj").getString("CompanyID");
                Ticket_ProfileID = jsonObj.getJSONObject("ReturnObj").getString("ProfileID");
                Ticket_TicketID = jsonObj.getJSONObject("ReturnObj").getString("TicketID");

                String TicketStatusValue;
                switch (TicketStatus){
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
                        PriorityBar = R.drawable.big_icon_low_priority;
                        break;
                    case "1":
                        PriorityValue = "Medium";
                        PriorityBar = R.drawable.big_icon_medium_priority;
                        break;
                    case "2":
                        PriorityValue = "High";
                        PriorityBar = R.drawable.big_icon_high_priority;
                        break;
                    case "3":
                        PriorityValue = "Urgent";
                        PriorityBar = R.drawable.big_icon_urgent_priority;
                        break;
                    default:
                        PriorityValue = "Low";
                        PriorityBar = R.drawable.big_icon_low_priority;
                        break;
                }

                priority_bar.setImageResource(PriorityBar);
                ticket_status.setText(TicketStatusValue);
                ticket_overdue.setText("Overdue : "+OverdueDateTimeAsString);
                ticket_subject.setText(Html.fromHtml(Subject));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            pDialog2.dismiss();
        }
    }

    private void showSuccessMessage(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(TicketDetails.this, Dashboard.class);
                        startActivity(intent);
                    }
                })
                .setIcon(R.drawable.notification_success)
                .show();
    }

    //Async tak to get Ticket History
    class ticketHistory extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TicketDetails.this);
            pDialog.setMessage("Please wait. Fetching Ticket History...");
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... arg0) {
            try {
                HttpClient httpclient = getNewHttpClient();
                HttpGet httpget = new HttpGet(BASEURL);
                httpget.addHeader("Authorization", "Bearer "+arg0[0]);
                Log.d("ZF-TicketHistory_url : ", BASEURL);
                // Execute HTTP get Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String responseprofileString = EntityUtils.toString(entity, "UTF-8");
                Log.d("ZF-TicketHistory : ", responseprofileString);
                return responseprofileString;
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

            // Check the results data
            Log.d("ZF-TickHistory data : ", resultData);
            List<TicketDetailsItemObject> items = new ArrayList<TicketDetailsItemObject>();

            try {
                JSONObject FilteredObject = new JSONObject(FilteredData);
                String  agentsList = FilteredObject.getJSONObject("ReturnObj").getString("Agents");
                JSONArray agentsArray = new JSONArray(agentsList);

                JSONArray ticketHistoryArray = new JSONArray(resultData);
                for (int i = 0; i < ticketHistoryArray.length(); i++) {

                    if(i==0){
                        mail_subject = ticketHistoryArray.getJSONObject(i).getString("MailSubject");
                        sender_email = ticketHistoryArray.getJSONObject(i).getString("CreatedUserEmail");
                    }

                    int avatar = R.drawable.user_avatar;
                    String name = null;
                    String email = ticketHistoryArray.getJSONObject(i).getString("CreatedUserEmail");

                    // Get Agent name from email
                    for (int k = 0; k < agentsArray.length(); k++) {
                        String UserEmail = agentsArray.getJSONObject(k).getString("Email");
                        if(email.equals(UserEmail)){
                            name = agentsArray.getJSONObject(k).getString("FirstName");
                        }
                    }

                    String reported_date = ticketHistoryArray.getJSONObject(i).getString("QueueDate");
                    String mail_subject = ticketHistoryArray.getJSONObject(i).getString("MailSubject");
                    String hide_show = "hide details";
                    String cc_address = ticketHistoryArray.getJSONObject(i).getString("CreatedUserEmail");
                    String msg_date = ticketHistoryArray.getJSONObject(i).getString("SentDate");
                    String message = ticketHistoryArray.getJSONObject(i).getString("Body");
                    String has_Attachment = ticketHistoryArray.getJSONObject(i).getString("hasAttachment");
                    String atachment_list = ticketHistoryArray.getJSONObject(i).getString("AttachmentList");
                    String visiblity = ticketHistoryArray.getJSONObject(i).getString("Visible");
                    String AttachmentUri = ticketHistoryArray.getJSONObject(i).getString("AttachmentUri");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date ReportedDate = sdf.parse(reported_date);
                    Date ReplieddDate = sdf.parse(msg_date);

                    // "Replied on : " +msg_date.substring(0,10)
                    items.add(new TicketDetailsItemObject(avatar, name, "Created on : " +String.valueOf(ReportedDate),
                                hide_show, cc_address, "Replied on : " +String.valueOf(ReplieddDate), message));

                    adapter = new TicketDetailsRecyclerViewAdapter(TicketDetails.this, items);
                    recyclerView.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            pDialog.dismiss();
        }
    }
    /**************************************************************************************************************************************/


      /*  Async Task to send response*/
    class SendCloseRequest extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TicketDetails.this);
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
            showSuccessMessage( "Ticket closed successfully..", "Ticket Closed!");
        }
    }


}


