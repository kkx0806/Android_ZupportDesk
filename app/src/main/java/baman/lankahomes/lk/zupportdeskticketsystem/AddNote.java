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
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
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
import java.util.Arrays;
import java.util.List;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.ApplicationEnvironmentURL;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.MySSLSocketFactory;

public class AddNote extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    ApplicationEnvironmentURL applicationEnvironment;
    ProgressDialog pDialog;
    Context context;
    String BASEURL;
    String  Activity_title;
    ImageView back_arrow;
    SwitchCompat note_visiblity;
    EditText replay_message;
    MultiAutoCompleteTextView NotifyToAddress;
    ImageView send_button;
    TextView header_title;
    TextView notify_text;
    View bottomline;

    public String sender_email;
    public String mail_subject;
    public String Ticket_CreatedBy;
    public String Ticket_ProfileID;
    public String Ticket_CompanyID;
    public String Ticket_TicketID;
    public String InboxID;
    public String visiblity="Private";
    public String profileToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        Intent intent = getIntent();
        Activity_title = intent.getStringExtra("response_title");
        mail_subject  = intent.getStringExtra("mail_subject");
        sender_email  = intent.getStringExtra("sender_email");
        Ticket_CreatedBy  = intent.getStringExtra("Ticket_CreatedBy");
        Ticket_ProfileID  = intent.getStringExtra("Ticket_ProfileID");
        Ticket_CompanyID  = intent.getStringExtra("Ticket_CompanyID");
        Ticket_TicketID  = intent.getStringExtra("Ticket_TicketID");
        InboxID  = intent.getStringExtra("InboxID");

        NotifyToAddress = (MultiAutoCompleteTextView) this.findViewById(R.id.ET_tkt_replay_ccaddress);
        notify_text = (TextView) findViewById(R.id.tv_note_notify);
        bottomline = findViewById(R.id.view4);
        notify_text.setVisibility(View.GONE);
        NotifyToAddress.setVisibility(View.GONE);

        header_title = (TextView) findViewById(R.id.TV_tkt_replay_header_title);
        header_title.setText(Activity_title);

        back_arrow = (ImageView) findViewById(R.id.IV_tkt_replay_back_arrow);
        back_arrow.setOnClickListener(onClickListener);
        note_visiblity= (SwitchCompat) findViewById(R.id.SW_note_visiblity);
        note_visiblity.setOnCheckedChangeListener(this);

        replay_message = (EditText) findViewById(R.id.ET_tkt_replay_message);

        applicationEnvironment = new ApplicationEnvironmentURL(this.context);
        context = this.getApplicationContext();

        SharedPreferences prefs = getSharedPreferences("zupportdesk", MODE_PRIVATE);
        profileToken = prefs.getString("profileToken", "Not defined");
        Log.d("ZD-profileToken : ", profileToken);

        // get profile email address and set to multiple email selection
        setCompanyProfilesURL(Ticket_CompanyID);
        new getCompanyProfileEmails().execute(profileToken);


        send_button = (ImageView) findViewById(R.id.IV_send_response2);
        send_button.setOnClickListener(onClickListener);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            this.visiblity= "Public";
            notify_text.setVisibility(View.VISIBLE);
            NotifyToAddress.setVisibility(View.VISIBLE);
            replay_message.setHint("Enter your public message here.");
            Log.d("ZF-Visibility :", visiblity);
        } else {
            this.visiblity= "Private";
            notify_text.setVisibility(View.GONE);
            NotifyToAddress.setVisibility(View.GONE);
            replay_message.setHint("Enter your private message here.");
            Log.d("ZF-Visibility :", visiblity);
        }
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

        ArrayAdapter<String> aaStr = new ArrayAdapter<String>(this, R.layout.auto_complete_email_text, R.id.autoCompleteText,emails);
        NotifyToAddress.setAdapter(aaStr);
        NotifyToAddress.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer() );
    }

    /* Multiple Button on click event handle */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.IV_tkt_replay_back_arrow:
                    onBackPressed();
                    break;
                case R.id.IV_send_response2:
                        //for replay ticket
                        try {
                            setNotesURL();
                            setNoteReplayJson();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    break;
            }
        }
    };

    private void setNotesURL(){
        String tickets_url = applicationEnvironment.get_ticket_api_url(this.context);
        BASEURL = tickets_url +"Ticket/AddNote";
        Log.d("ZF-Base URL :", BASEURL);
    }

    private void setCompanyProfilesURL(String Company_id){
        String tickets_url = applicationEnvironment.get_user_api_url(this.context);
        String query = "account/GetOperatorsByCompanyToken?companyID="+Company_id;
        BASEURL = tickets_url + query;
        Log.d("ZF-Base URL :", BASEURL);
    }

    public void setNoteReplayJson() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray NotifyAgentArray = new JSONArray();

        String to_address = NotifyToAddress.getText().toString();
        String reply_msg = replay_message.getText().toString();

        String[] notifyList = to_address.split(",");
        Log.d("NotifyListLength", String.valueOf(notifyList.length));

        if (notifyList.length>1){
                for(int i=0; i < notifyList.length; i++){
                   Log.d("NotifyList", notifyList[i]);
                    NotifyAgentArray.put(notifyList[i]);
                }
        }else{
            NotifyAgentArray.put(to_address);
        }


        if(to_address.equals("") && visiblity.equals("Public")){
            showErrorMessage("Pleases add some people to notify.", "Error!");
        } else if(reply_msg.equals("")){
            showErrorMessage("Pleases add a message.", "Error!");
        } else {



            //jsonArray.put("lv2");

            jsonObject.put("Token", profileToken);
            jsonObject.put("TicketID", Ticket_TicketID);
            jsonObject.put("CompanyID", Ticket_CompanyID);
            jsonObject.put("SendTo", to_address);
            jsonObject.put("From", sender_email);
            jsonObject.put("Subject", mail_subject);
            jsonObject.put("Message", reply_msg);
            jsonObject.put("AttachmentGUID", 0);
            jsonObject.put("Visible", visiblity);
            jsonObject.put("MessageType", 3);
            jsonObject.put("AttachmentUploaded", jsonArray);
            jsonObject.put("VisibleAgent", NotifyAgentArray);

            Log.d("ZF-ReplayJson : ", String.valueOf(jsonObject));
            new SendResponse().execute(String.valueOf(jsonObject), profileToken);
        }
    }

    private void showSuccessMessage(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(AddNote.this, Dashboard.class);
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

    /**********************************************************************************************/
      /*  Async Task to send response*/
    class SendResponse extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddNote.this);
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
            showSuccessMessage("Reply has been sent successfully.", "Reply Sent!");
        }
    }
    
}
