package baman.lankahomes.lk.zupportdeskticketsystem;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.ApplicationEnvironmentURL;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.MySSLSocketFactory;

public class NewTicket extends AppCompatActivity {

    ApplicationEnvironmentURL applicationEnvironment;
    ProgressDialog pDialog;
    Context context;
    String BASEURL;
    String userid;
    String profileToken;
    String companyId;
    String ProfileId;
    String FilteredData;
    ImageView back_arrow;

    ImageView send_ticket;
    EditText overdue;
    Button pickDate;
    EditText from_address;
    EditText cc_address;
    EditText subject;
    EditText description;
    Spinner to_adddress;
    Spinner priority;
    Spinner status;
    Spinner inbox_id;

    JSONObject jsonNewTicketObject = new JSONObject();
    JSONArray jsonAttachmentGUIDArray = new JSONArray();
    JSONArray jsonAttachmentUploadArray = new JSONArray();
    JSONArray AgentsArrayList = new JSONArray();

    Calendar c = Calendar.getInstance();
    int startYear = c.get(Calendar.YEAR);
    int startMonth = c.get(Calendar.MONTH);
    int startDay = c.get(Calendar.DAY_OF_MONTH);

    List<String> arrayInboxId = new ArrayList<String>();
    List<String> arrayAgentList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("New Ticket");
        setContentView(R.layout.activity_new_ticket);

        applicationEnvironment = new ApplicationEnvironmentURL(this.context);
        context = this.getApplicationContext();

        // add spinner for select inbox and agent, using the filtered data.
        inbox_id = (Spinner) findViewById(R.id.SPN_nt_inbox_id);
        pickDate = (Button) findViewById(R.id.BTN_nt_pick_date);
        overdue = (EditText) findViewById(R.id.ET_nt_over_due);
        from_address= (EditText) findViewById(R.id.ET_nt_from_address);
        cc_address = (EditText) findViewById(R.id.ET_nt_cc_address);
        subject= (EditText) findViewById(R.id.ET_nt_subject);
        description= (EditText) findViewById(R.id.ET_nt_description);
        to_adddress = (Spinner) findViewById(R.id.SPN_nt_to_address);
        priority = (Spinner) findViewById(R.id.SPN_nt_Priority);
        status = (Spinner) findViewById(R.id.SPN_nt_status);
        send_ticket = (ImageView) findViewById(R.id.IV_send_ticket);
        back_arrow = (ImageView) findViewById(R.id.IV_tkt_replay_back_arrow);

        pickDate.setOnClickListener(onClickListener);
        send_ticket.setOnClickListener(onClickListener);
        back_arrow.setOnClickListener(onClickListener);

        /************************ Add items to Spinner ********************************************/
        getValuesFromSharedPreferences();
        try {
            addInboxesstoSpinners();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /******************************************************************************************/
    }

    /* Multiple Button on click event handle */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.IV_tkt_replay_back_arrow:
                    onBackPressed();
                    break;
                case R.id.BTN_nt_pick_date:
                    DialogFragment dialogFragment = new StartDatePicker();
                    dialogFragment.show(getFragmentManager(), "start_date_picker");
                    break;
                case R.id.IV_send_ticket:
                    if(validateParameters()) {
                        // add URL
                        setNewTicketURL();
                        //create Json object
                        Log.d("ZF-Create Ticket : ", String.valueOf(jsonNewTicketObject));
                        //send the ticket to server
                        new CreateNewTicket().execute(profileToken, String.valueOf(jsonNewTicketObject));
                    }
                    break;
            }
        }
    };

    private void getValuesFromSharedPreferences(){
        SharedPreferences prefs = getSharedPreferences("zupportdesk", MODE_PRIVATE);
        userid = prefs.getString("userid", "Not defined");
        profileToken = prefs.getString("profileToken", "Not defined");
        companyId = prefs.getString("companyId", "Not defined");
        ProfileId = prefs.getString("ProfileId", "Not defined");
        FilteredData = prefs.getString("FilteredData", "Not defined");
    }
    private void addInboxesstoSpinners() throws JSONException {
        Log.d("ZF-FilteredData :", FilteredData);

        JSONArray AgentsArray = null;
        try {
            JSONObject jsonObj = new JSONObject(FilteredData);
            String return_object = jsonObj.getString("ReturnObj");
            Log.d("ZF-return_object :", return_object);

            JSONObject rtn_object = new JSONObject(return_object);
            JSONArray itemsArray = rtn_object.getJSONArray("Inboxes");
            AgentsArray = rtn_object.getJSONArray("Agents");
            Log.d("ZF-Inboxes_array :", String.valueOf(itemsArray));
            Log.d("ZF-Inboxes_length :", String.valueOf(itemsArray.length()));

            if (itemsArray.length() > 0) {
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject individualObject = itemsArray.getJSONObject(i);
                    String Inboxid = individualObject.getString("InboxId");
                    String Name = individualObject.getString("Name");
                    String MailID = individualObject.getString("MailID");
                    String OwnerID = individualObject.getString("OwnerID");
                    String Deleted = individualObject.getString("Deleted");
                    arrayInboxId.add(MailID);
                    Log.d("ZF-Inboxes :", "id : " + Inboxid + " Name : " + Name + " MailId : " + MailID + " Owner Id : " + OwnerID + " IsDeleted" + Deleted);
                }
            } else {
                arrayInboxId.add("No inbox available");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayInboxId);
        inbox_id.setAdapter(adapter);

        //Add agents to spinner
        AgentsArrayList = AgentsArray;
        AddAgentsToSpinner(AgentsArray);
    }

    private void AddAgentsToSpinner(JSONArray agentsArray) throws JSONException {
        Log.d("ZF-Agents_array :", String.valueOf(agentsArray));
        Log.d("ZF-Agents_length :", String.valueOf(agentsArray.length()));

        if(agentsArray.length()>0){
            for (int i = 0; i < agentsArray.length(); i++) {
                JSONObject individualObject = agentsArray.getJSONObject(i);
                String ProfileId = individualObject.getString("ProfileId");
                String FirstName = individualObject.getString("FirstName");
                arrayAgentList.add(FirstName);
                Log.d("ZF-Agents :", "profile id : " + ProfileId + " FirstName : " + FirstName);
            }
        } else {
            arrayAgentList.add("No agents available");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
               android.R.layout.simple_list_item_1, arrayAgentList);
                to_adddress.setAdapter(adapter);

    }
    public boolean validateParameters(){
        String inboxid = inbox_id.getSelectedItem().toString();
        String frm_address = from_address.getText().toString();
        String to_adress = to_adddress.getSelectedItem().toString();
        String cc_adress = cc_address.getText().toString();
        String subjct = subject.getText().toString();
        String desc = description.getText().toString();
        String overdue_date = overdue.getText().toString();
        String priorty = priority.getSelectedItem().toString();
        String staus = status.getSelectedItem().toString();
        Log.d("ZF- ", "priority : "+ priorty+" - Status : "+ staus);

        if(frm_address.equals("") || !isEmailValid(frm_address) ){
            showErrorMessage("Please check your *From* address", "Error!");
            return false;
        }else if(to_adress.equals("No agents available")){
            showErrorMessage("Please add an agent.", "Error!");
            return false;
        }else if(inboxid.equals("No inbox available")){
            showErrorMessage("Please add a inbox.", "Error!");
            return false;
        }else if(subjct.equals("")){
            showErrorMessage("Please check your mail subject.", "Error!");
            return false;
        }else if(desc.equals("")){
            showErrorMessage("Please add a description.", "Error!");
            return false;
        }else if(overdue_date.equals("")){
            showErrorMessage("Please select a overdue date.", "Error!");
            return false;
        }else{
            try {
                createJsonObject(inboxid, frm_address, to_adress, cc_adress, subjct, desc, overdue_date, priorty, staus);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    private void createJsonObject(String inboxid, String frm_address, String to_adress, String cc_adress, String subjct, String desc,
                                  String overdue_date, String priorty, String staus) throws JSONException {
        int priority_id ;
        int status_id ;

        switch (staus){
            case "Open" :
                priority_id = 0;
                break;
            case "Closed":
                priority_id = 1;
                break;
            case "On Hold":
                priority_id = 2;
                break;
            default:
                priority_id = 0;
                break;
        }

        switch (priorty){
            case "Low" :
                status_id = 0;
                break;
            case "Medium":
                status_id = 1;
                break;
            case "High":
                status_id = 2;
                break;
            case "Urgent":
                status_id = 3;
                break;
            default:
                status_id = 0;
                break;
        }

        jsonNewTicketObject.put("Priority", status_id );
        jsonNewTicketObject.put("TicketStatus", priority_id);
        jsonNewTicketObject.put("OverdueDateTime", overdue_date);
        jsonNewTicketObject.put("AssignedTo", getselectedAgentId(to_adress));
        jsonNewTicketObject.put("From", frm_address);
        jsonNewTicketObject.put("Subject", subjct);
        jsonNewTicketObject.put("Message", desc);
        jsonNewTicketObject.put("AttachmentName", "");
        jsonNewTicketObject.put("ProfileID", ProfileId);
        jsonNewTicketObject.put("Token", profileToken);
        jsonNewTicketObject.put("CompanyID", companyId);
        jsonNewTicketObject.put("InboxID", inboxid);
        jsonNewTicketObject.put("AttachmentGUID", jsonAttachmentGUIDArray);
        jsonNewTicketObject.put("AttachmentUploaded", jsonAttachmentUploadArray);
    }


    private String getselectedAgentId(String toAgent) throws JSONException {
        String selectedAgentid = "";
            for (int i = 0; i < AgentsArrayList.length(); i++) {
                JSONObject individualObject = AgentsArrayList.getJSONObject(i);
                String ProfileId = individualObject.getString("ProfileId");
                String FirstName = individualObject.getString("FirstName");
                    if(toAgent.equals(FirstName)){
                        selectedAgentid = ProfileId;
                    }
                Log.d("ZF-Selected_agent : ", FirstName + " - "+ ProfileId);
            }
        return  selectedAgentid;
    }

    private void setNewTicketURL() {
        String user_base_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = user_base_url +"Ticket/AddNewTicket";
        Log.d("ZD-Created URL :", BASEURL);
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private void showSuccessMessage(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(NewTicket.this, Dashboard.class);
                        startActivity(intent);
                    }
                })
                .setIcon(R.drawable.notification_success)
                .show();
    }

    private void showErrorMessage(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.notification_red)
                .show();
    }


    @SuppressLint("ValidFragment")
    public class StartDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            // Use the current date as the default date in the picker
            DatePickerDialog dialog = new DatePickerDialog(NewTicket.this, this, startYear, startMonth, startDay);
            return dialog;
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
            // TODO Auto-generated method stub
            // Do something with the date chosen by the user
            startYear = year;
            startMonth = monthOfYear+1;
            startDay = dayOfMonth;
            overdue.setText(startYear+"-"+ startMonth+ "-" + startDay);
            Log.d("ZF-Selected date : ", startYear+"-"+ startMonth+"-"+ startDay);
        }

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


    /**********************************************************************************************/
    /*  Async Task to create ticket*/
    class CreateNewTicket extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewTicket.this);
            pDialog.setMessage("Please wait. Sending request...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                //Post Username and password
                HttpClient httpclient = getNewHttpClient();
                HttpPost httppost = new HttpPost(BASEURL);
                httppost.addHeader("Authorization", "Bearer "+arg0[0]);
                httppost.addHeader("Content-Type", "application/json;charset=UTF-8");
                httppost.setEntity(new ByteArrayEntity(
                        arg0[1].toString().getBytes("UTF8")));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                Log.d("ZF-NewTicket response :", responseString);
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
            Log.d("ZF-NewTicket response :", resultData);
            pDialog.dismiss();
            showSuccessMessage("Ticket created successfully", "Success!");
        }
    }
    /**********************************************************************************************/

}
