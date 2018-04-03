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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.ApplicationEnvironmentURL;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.MySSLSocketFactory;

public class TicketResponse extends AppCompatActivity {
    ApplicationEnvironmentURL applicationEnvironment;
    ProgressDialog pDialog;
    Context context;
    ImageView back_arrow;
    ImageView send_button;
    Spinner Spnr_replay_from;
    MultiAutoCompleteTextView replay_ccaddress;
    MultiAutoCompleteTextView replay_bccaddress;
    TextView replay_message;
    TextView header_title;
    TextView tv_toaddress;
    EditText et_toaddress;
    View v_toaddress;
    String BASEURL;

    String  Activity_title;
    public String sender_email;
    public String mail_subject;
    public String Ticket_CreatedBy;
    public String Ticket_ProfileID;
    public String Ticket_CompanyID;
    public String Ticket_TicketID;
    public String InboxID;
    public String profileToken;
    public String FilteredData;
    public String visiblity="Public";

    List<String> InbixArraySpinner = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_response);

        /******************************************************************************************/
        Intent intent = getIntent();
        Activity_title = intent.getStringExtra("response_title");
        mail_subject  = intent.getStringExtra("mail_subject");
        sender_email  = intent.getStringExtra("sender_email");
        Ticket_CreatedBy  = intent.getStringExtra("Ticket_CreatedBy");
        Ticket_ProfileID  = intent.getStringExtra("Ticket_ProfileID");
        Ticket_CompanyID  = intent.getStringExtra("Ticket_CompanyID");
        Ticket_TicketID  = intent.getStringExtra("Ticket_TicketID");
        InboxID  = intent.getStringExtra("InboxID");

        header_title = (TextView) findViewById(R.id.TV_tkt_replay_header_title);
        header_title.setText(Activity_title);
        applicationEnvironment = new ApplicationEnvironmentURL(this.context);
        context = this.getApplicationContext();

        SharedPreferences prefs = getSharedPreferences("zupportdesk", MODE_PRIVATE);
        profileToken = prefs.getString("profileToken", "Not defined");
        FilteredData = prefs.getString("FilteredData", "Not defined");

        Log.d("ZD-profileToken : ", profileToken);
        /******************************************************************************************/
        replay_message = (TextView) findViewById(R.id.ET_tkt_replay_message);
        tv_toaddress = (TextView) findViewById(R.id.TV_txt_replay_to);
        et_toaddress = (EditText) findViewById(R.id.ET_tkt_replay_toaddress);
        v_toaddress = findViewById(R.id.txt_replay_to_view);
        /*********************** Forward Ticket ***************************************************/
        if(Activity_title.equals("Forward")){
            tv_toaddress.setVisibility(View.VISIBLE);
            et_toaddress.setVisibility(View.VISIBLE);
            v_toaddress.setVisibility(View.VISIBLE);
        }
        /******************************************************************************************/
        try {
            setInboxestoSpinners();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /******************************************************************************************/
        back_arrow = (ImageView) findViewById(R.id.IV_tkt_replay_back_arrow);
        send_button = (ImageView) findViewById(R.id.IV_send_response);
        back_arrow.setOnClickListener(onClickListener);
        send_button.setOnClickListener(onClickListener);


        setCompanyProfilesURL(Ticket_CompanyID);
        new getCompanyProfileEmails().execute(profileToken);
    }



    // Setting auto complete email editText view, with a help of Asunc task.
    public void setMultipleEmailSelection(String jsonvalue) throws JSONException {
        List<String> emails = new ArrayList<String>();

        JSONObject jsonObj = null;
        jsonObj = new JSONObject(jsonvalue);
        JSONArray itemsArray= jsonObj.getJSONArray("ReturnObj");
        Log.d("ZF-profile details : ", String.valueOf(itemsArray));

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject individualObject =  itemsArray.getJSONObject(i);
            String email = individualObject.getString("CompanyEmail");
            Log.d("profile email : ", String.valueOf(email));
            emails.add(email);
        }

        replay_ccaddress = (MultiAutoCompleteTextView) this.findViewById(R.id.ET_tkt_replay_ccaddress);
        ArrayAdapter<String> aaStr = new ArrayAdapter<String>(this, R.layout.auto_complete_email_text, R.id.autoCompleteText,emails);
        replay_ccaddress.setAdapter(aaStr);
        replay_ccaddress.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer() );

        replay_bccaddress = (MultiAutoCompleteTextView) this.findViewById(R.id.ET_tkt_replay_bccaddress);
        ArrayAdapter<String> email_string = new ArrayAdapter<String>(this, R.layout.auto_complete_email_text, R.id.autoCompleteText,emails);
        replay_bccaddress.setAdapter(email_string);
        replay_bccaddress.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer() );
    }


    /* Multiple Button on click event handle */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.IV_tkt_replay_back_arrow:
                    onBackPressed();
                    break;
                case R.id.IV_send_response:
                        if (Activity_title.equals("Forward")){
                            //for forward ticket
                                try {
                                    setTicketsForwardURL();
                                    setForwardJson();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                        }else {
                            //for replay ticket
                                try {
                                    setTicketsReplayURL();
                                    setReplayJson();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                    break;
            }
        }
    };

    private void setCompanyProfilesURL(String Company_id){
        String tickets_url = applicationEnvironment.get_user_api_url(this.context);
        String query = "account/GetOperatorsByCompanyToken?companyID="+Company_id;
        BASEURL = tickets_url + query;
        Log.d("ZF-Base URL :", BASEURL);
    }

    private void setInboxestoSpinners() throws JSONException {
        JSONObject jsonObj =  new JSONObject(FilteredData);
        String return_object = jsonObj.getString("ReturnObj");
        Log.d("ZF-return_object :", return_object);

        JSONObject rtn_object = new JSONObject(return_object);
        JSONArray itemsArray = rtn_object.getJSONArray("Inboxes");

        Log.d("ZF-Inboxes_array :", String.valueOf(itemsArray));
        Log.d("ZF-Inboxes_length :", String.valueOf(itemsArray.length()));

        if (itemsArray.length() > 0) {
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject individualObject = itemsArray.getJSONObject(i);
                String Inboxid = individualObject.getString("InboxId");
                String Name = individualObject.getString("Name");
                String MailID = individualObject.getString("MailID");
                String OwnerID = individualObject.getString("OwnerID");
                InbixArraySpinner.add(MailID);
            }
        }

            Spnr_replay_from = (Spinner) findViewById(R.id.SPN_tkt_replay_from);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, InbixArraySpinner);
            Spnr_replay_from.setAdapter(adapter);
    }
    private void setTicketsReplayURL(){
        String tickets_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = tickets_url +"Ticket/AddNote";
        Log.d("ZF-Base URL :", BASEURL);
    }

    private void setTicketsForwardURL(){
        String tickets_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = tickets_url +"Ticket/ForwardTicketForMobile";
        Log.d("ZF-Base URL :", BASEURL);
    }

    public void setForwardJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray NotifyAgentArray = new JSONArray();

        String from_address = Spnr_replay_from.getSelectedItem().toString();
        String cc_address = replay_ccaddress.getText().toString();
        String to_address = et_toaddress.getText().toString();
        String bcc_address = replay_bccaddress.getText().toString();
        String reply_msg = replay_message.getText().toString();


        if(from_address.equals("")){
            showErrorMessage("Please Select a *From* address", "Error");
        }else if(to_address.equals("") || !isEmailValid(to_address)){
            showErrorMessage("Please enter a valid email address for *TO* address.", "Error");
        }else if(reply_msg.equals("")){
            showErrorMessage("Please enter a message.", "Error");
        }else {

            String[] notifyList = cc_address.split(",");
            Log.d("NotifyListLength", String.valueOf(notifyList.length));
            if (notifyList.length>1){
                for(int i=0; i < notifyList.length; i++){
                    Log.d("NotifyList", notifyList[i]);
                    NotifyAgentArray.put(notifyList[i]);
                }
            }else{
                NotifyAgentArray.put(cc_address);
            }


            jsonObject.put("Subject", mail_subject);
            jsonObject.put("SendTo", to_address);
            jsonObject.put("From", from_address);
            jsonObject.put("Message", reply_msg);
            jsonObject.put("Token", profileToken);
            jsonObject.put("TicketID", Ticket_TicketID);
            jsonObject.put("AttachmentUploaded", jsonArray);
            jsonObject.put("Visible", "Replay");


            Log.d("ZF-ForwardJson : ", String.valueOf(jsonObject));
            new SendResponse().execute(String.valueOf(jsonObject), profileToken);
        }
    }

    public void setReplayJson() throws JSONException {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONArray NotifyAgentArray = new JSONArray();

                String from_address = Spnr_replay_from.getSelectedItem().toString();
                String cc_address = replay_ccaddress.getText().toString();
                String bcc_address = replay_bccaddress.getText().toString();
                String reply_msg = replay_message.getText().toString();

                if(from_address.equals("")){
                    showErrorMessage("Please Select a *From* address", "Error");
                }else if(reply_msg.equals("")){
                    showErrorMessage("Please enter a message.", "Error");
                }else {

                    String[] notifyList = cc_address.split(",");
                    Log.d("NotifyListLength", String.valueOf(notifyList.length));

                    if (notifyList.length>1){
                        for(int i=0; i < notifyList.length; i++){
                            Log.d("NotifyList", notifyList[i]);
                            NotifyAgentArray.put(notifyList[i]);
                        }
                    }else{
                        NotifyAgentArray.put(cc_address);
                    }

                    //jsonArray.put("lv2");

                    jsonObject.put("Token", profileToken);
                    jsonObject.put("TicketID", Ticket_TicketID);
                    jsonObject.put("CompanyID", Ticket_CompanyID);
                    jsonObject.put("SendTo", cc_address);
                    jsonObject.put("From", from_address);
                    jsonObject.put("Subject", mail_subject);
                    jsonObject.put("Message", reply_msg);
                    jsonObject.put("AttachmentGUID", 0);
                    jsonObject.put("Visible", visiblity);
                    jsonObject.put("MessageType", 2);
                    jsonObject.put("AttachmentUploaded", jsonArray);
                    jsonObject.put("VisibleAgent", NotifyAgentArray);

                    Log.d("ZF-ReplayJson : ", String.valueOf(jsonObject));
                    new SendResponse().execute(String.valueOf(jsonObject), profileToken);
                }
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
                        Intent intent = new Intent(TicketResponse.this, Dashboard.class);
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


    
     /*  Async Task to send response*/

    class SendResponse extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TicketResponse.this);
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
                Log.d("ZF-TicketResponse ", responseString);
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
            Log.d("ZF-TicketResponse ", resultData);
            pDialog.dismiss();

                    if (Activity_title.equals("Forward")){
                        showSuccessMessage("Ticket has been forwarded successfully.", "Forward request Sent!");
                    } else {
                        showSuccessMessage("Reply has been sent successfully.", "Reply Sent!");
                    }
        }
    }

    /**********************************************************************************************/
          /*  Async Task to get Company Profiles email*/
    class getCompanyProfileEmails extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                // get user token
                HttpClient httpclient = getNewHttpClient();
                HttpGet httpget = new HttpGet(BASEURL);
                Log.d("ZF-Company Profiles : ", arg0[0]);
                httpget.addHeader("Authorization", "Bearer "+arg0[0]);
                httpget.addHeader("Content-Type", "application/json");
                // Execute HTTP get Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                Log.d("ZF-Company Profiles : ", responseString);
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
            Log.d("ZF-Company Profiles : ", resultData);
            try {
                setMultipleEmailSelection(resultData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
