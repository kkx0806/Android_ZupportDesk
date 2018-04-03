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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.ApplicationEnvironmentURL;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.CompanyProfileAdapter;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.CompanyProfileData;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.MySSLSocketFactory;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.SimpleDividerItemDecoration;

public class CompanyProfile extends ActionBarActivity {

    private List<CompanyProfileData> CompanyProfileList = new ArrayList<>();
    private RecyclerView recyclerView;
    private CompanyProfileAdapter mAdapter;
    ApplicationEnvironmentURL applicationEnvironment;
    ProgressDialog pDialog;
    Context context;
    String BASEURL;
    Button CP_submit;

    ImageView appbar_image;
    TextView app_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        applicationEnvironment = new ApplicationEnvironmentURL(this.context);
        context = this.getApplicationContext();

        app_title = (TextView) findViewById(R.id.appbar_title);
        app_title.setText("Company Profile");
        appbar_image = (ImageView) findViewById(R.id.appbar_image);
        appbar_image.setImageResource(R.drawable.icon_company_profile);

        Intent intent = getIntent();
        String company_data = intent.getStringExtra("companyData");
        Log.d("Company data", company_data);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mAdapter = new CompanyProfileAdapter(CompanyProfileList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        try {
            prepareCompanyProfileData(company_data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CP_submit = (Button) findViewById(R.id.btn_CP_submit);
        CP_submit.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.btn_CP_submit:
                    if(isNetworkAvailable()){
                        String company_id = mAdapter.companyID;
                        String help_desk_name = mAdapter.helpdeskName;
                        String company_name = mAdapter.companyName;

                        //adding user data to shared preferences.
                        SharedPreferences.Editor editor = getSharedPreferences("zupportdesk", MODE_PRIVATE).edit();
                        editor.putString("companyId", company_id);
                        editor.putString("companyName", company_name);
                        editor.commit();

                        //get the userid from sharedPreferences
                        SharedPreferences prefs = getSharedPreferences("zupportdesk", MODE_PRIVATE);
                        String userid = prefs.getString("userid", "No id provided");
                        try {
                            setProfileTokenURL(userid,company_id);
                            new getProfileToken().execute();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }else{showErrorMessage("Please check your internet connection.", "No Connectivity!");}
                    break;
            }
        }
    };

    private void setProfileTokenURL(String user_id, String company_id) throws UnsupportedEncodingException {
        String encoded_user = URLEncoder.encode(user_id, "utf-8");
        Log.d("Normal User id : ", user_id);
        Log.d("encoded User id : ", encoded_user);
        String user_base_url = applicationEnvironment.get_user_api_url(this.context);
        BASEURL = user_base_url +"Account/CreateLoginToken?userToken="+ encoded_user +"&UserCompanyID="+company_id;
        Log.d("Created URL :", BASEURL);
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

    private void prepareCompanyProfileData(String company_data) throws JSONException {
        CompanyProfileData CompanyData ;
        JSONArray jsonArray = new JSONArray(company_data);
        int count = jsonArray.length();
        for(int i = 0; i< jsonArray.length();i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            CompanyData = new CompanyProfileData(jsonObject.getString("CompanyName"), jsonObject.getString("HelpdeskDomainName"), jsonObject.getString("_CompanyID"));
            CompanyProfileList.add(CompanyData);
        }
        mAdapter.notifyDataSetChanged();
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

    public void check_results(String result_string) throws JSONException {
        JSONObject jsonObj = new JSONObject(result_string);
        Log.d("json object : ", String.valueOf(jsonObj));
        String status = jsonObj.getString("IsSuccess");
        String message = jsonObj.getString("Message");
        String messagecode = jsonObj.getString("Message_Code");

        if(message.equals("Success") && messagecode.equals("2010")) {
            String ProfileToken = jsonObj.getString("ReturnObj");

            SharedPreferences.Editor editor = getSharedPreferences("zupportdesk", MODE_PRIVATE).edit();
            editor.putString("profileToken", ProfileToken);
            editor.commit();

            // Redirect to dashboard
            Intent intent = new Intent(CompanyProfile.this, Dashboard.class);
            startActivity(intent);
        }else {showErrorMessage("Please try again later.", "Unknown Error!"); }
    }


    //Async tak to get Profile data
    class getProfileToken extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CompanyProfile.this);
            pDialog.setMessage("Please wait. Loading data...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {

            try {
                // get user token
                HttpClient httpclient = getNewHttpClient();
                HttpGet httpget = new HttpGet(BASEURL);

                // Execute HTTP get Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

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
            Log.d("Results ", resultData);
            try {
                check_results(resultData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog.dismiss();
        }
    }
}
