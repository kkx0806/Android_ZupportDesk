package baman.lankahomes.lk.zupportdeskticketsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
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
import java.util.concurrent.ExecutionException;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.ApplicationEnvironmentURL;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.MySSLSocketFactory;

public class Login extends AppCompatActivity {

    Button btn_login;
    Context context;
    String global_profileId;

    EditText username;
    EditText password;
    ProgressDialog pDialog;
    public String user_email;
    public String user_password;
    public String BASEURL;
    public ApplicationEnvironmentURL applicationEnvironment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        applicationEnvironment = new ApplicationEnvironmentURL(this.context);

        username = (EditText) findViewById(R.id.et_login_email);
        password = (EditText) findViewById(R.id.et_login_password);
        //get context
        context = this.getApplicationContext();
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(onClickListener);
    }

    private void setupPofileidURL(String profileToken) throws UnsupportedEncodingException {
        String encoded_profileToken = URLEncoder.encode(profileToken, "utf-8");
        String user_login_url = applicationEnvironment.get_user_api_url(this.context);
        BASEURL = user_login_url +"Account/GetProfileDetails?profileToken="+encoded_profileToken;
        Log.d("Base URL :", BASEURL);
    }

    private void setLoginURL(){
        String user_login_url = applicationEnvironment.get_user_api_url(this.context);
        BASEURL = user_login_url +"Account/SignIn";
        Log.d("Base URL :", BASEURL);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void goToSignUP(View v) throws ExecutionException, InterruptedException {
        //startActivity(new Intent(Login.this, Signup.class));

        //Redirecting the user to Browser
        Uri uri = Uri.parse("https://www.zupportdesk.com/#/home/signup");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void passwordForgot(View v){
        startActivity(new Intent(Login.this, ForgotPassword.class));
    }

    /* Multiple Button on click event handle */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.btn_login:
                    // Create a login URl, before starting anything
                    setLoginURL();
                    // check for network connectivity
                    if(isNetworkAvailable()){
                            user_email = username.getText().toString();
                            user_password = password.getText().toString();
                            if(user_email.equals("zupport#934#")){
                                selectEnvironment();
                            }else {
                                if(user_email.length() < 4 || user_email.equals("") || user_password.length() > 15 || user_password.equals("") || user_password.length() < 4){
                                    showErrorMessage("Please check your login credentials and try again.", "Sign in Failed!");
                                }else {
                                    new getToken().execute(user_email, user_password);
                                }
                            }
                    } else {showErrorMessage("Please check your internet connection.", "No Connectivity!"); }
                    break;
            }
        }
    };


    public void selectEnvironment(){
        final CharSequence environment[] = new CharSequence[] {"Testing", "Development", "Production"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change environment");
        builder.setIcon(R.drawable.icon_company_profile);
        builder.setItems(environment, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String new_environment = (String) environment[which];
                Toast.makeText(Login.this, "Your environment changed to " + new_environment, Toast.LENGTH_LONG).show();
                Log.d("Selected Environment : ", new_environment);
                applicationEnvironment.changeEnvironment(new_environment);
            }
        });
        builder.show();
    }

    private void checkResultValue(String result) throws JSONException, UnsupportedEncodingException, ExecutionException, InterruptedException {
        JSONObject jsonObj = new JSONObject(result);
        String status = jsonObj.getString("IsSuccess");
        String message = jsonObj.getString("Message");
        String messageCode = jsonObj.getString("Message_Code");
        Log.d("status : ", status);
        Log.d("message : ", message);
        Log.d("messageCode : ", messageCode);

        switch (status){
            case "true":
                String  userId = jsonObj.getJSONObject("ReturnObj").getString("UserId");
                String profileToken = jsonObj.getJSONObject("ReturnObj").getString("profileToken");
                String companyData = jsonObj.getJSONObject("ReturnObj").getString("CompanyList");
                JSONArray jsonArray = new JSONArray(companyData );
                int companyCount = jsonArray.length();
                Log.d("User Id : ", String.valueOf(userId));
                Log.d("Company Count : ", String.valueOf(companyCount));
                Log.d("profile Token : ", profileToken);

                        if (companyCount == 1) {
                            /****************** Single company ****************************/
                            // remove square brackets from json
                            companyData = companyData.substring(1, companyData.length() - 1);
                            Log.d("companyData : ", companyData);

                            JSONObject jsoncompany = new JSONObject(companyData);
                            String companyName = jsoncompany.getString("CompanyName");
                            String companyID = jsoncompany.getString("_CompanyID");

                            Log.d("companyId : ", companyID);
                            Log.d("company name : ", companyName);

                            //adding user data to shared preferences.
                            SharedPreferences.Editor editor = getSharedPreferences("zupportdesk", MODE_PRIVATE).edit();
                            editor.putString("islogged", "true");
                            editor.putString("userid", userId);
                            editor.putString("profileToken", profileToken);
                            editor.putString("companyId", companyID);
                            editor.putString("companyName", companyName);
                            editor.commit();

                            //Async task to get the Profile id
                            setupPofileidURL(profileToken);
                            String resultProfileData = new getProfileId().execute(profileToken).get();

                            JSONObject jsonObj3 = null;
                            try {
                                jsonObj3 = new JSONObject(resultProfileData);
                                String profileId = jsonObj3.getString("ProfileId");
                                String FirstName = jsonObj3.getString("FirstName");
                                String DisplayName = jsonObj3.getString("DisplayName");
                                String usrEmail = jsonObj3.getString("Email");
                                String userRole = jsonObj3.getString("UserRole");
                                String TimeZone = jsonObj3.getString("TimeZone");
                                String IsDeleted = jsonObj3.getString("IsDeleted");
                                Log.d("ProfileId : ", profileId);
                                Log.d("FirstName : ", FirstName);
                                Log.d("DisplayName : ", DisplayName);
                                Log.d("Email : ", usrEmail);
                                Log.d("userRole : ", userRole);
                                Log.d("TimeZone : ", TimeZone);
                                Log.d("IsDeleted : ", IsDeleted);

                                SharedPreferences.Editor editor2 = getSharedPreferences("zupportdesk", MODE_PRIVATE).edit();
                                editor2.putString("ProfileId", profileId);
                                editor2.putString("FirstName", FirstName);
                                editor2.putString("DisplayName", DisplayName);
                                editor2.putString("Email", usrEmail);
                                editor2.putString("userRole", userRole);
                                editor2.putString("TimeZone", TimeZone);
                                editor2.putString("IsDeleted", IsDeleted);
                                editor2.commit();

                                global_profileId = profileId;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Redirect to dashboard
                            //Intent intent = new Intent(Login.this, Dashboard.class);
                            Intent intent = new Intent(Login.this, PickupActivity.class);
                            startActivity(intent);
                        }else {
                            /****************** Multiple company ****************************/
                            Log.d("No token : ", "Multiple company no profile token provided");

                            //adding user data to shared preferences.
                            SharedPreferences.Editor editor = getSharedPreferences("zupportdesk", MODE_PRIVATE).edit();
                            editor.putString("islogged", "true");
                            editor.putString("userid", userId);
                            editor.commit();

                            //Async task to get the Profile id
                            setupPofileidURL(profileToken);
                            String resultProfileData = new getProfileId().execute(profileToken).get();

                            JSONObject jsonObj3 = null;
                            try {
                                jsonObj3 = new JSONObject(resultProfileData);
                                String profileId = jsonObj3.getString("ProfileId");
                                String FirstName = jsonObj3.getString("FirstName");
                                String DisplayName = jsonObj3.getString("DisplayName");
                                String usrEmail = jsonObj3.getString("Email");
                                String userRole = jsonObj3.getString("UserRole");
                                String TimeZone = jsonObj3.getString("TimeZone");
                                String IsDeleted = jsonObj3.getString("IsDeleted");
                                Log.d("ProfileId : ", profileId);
                                Log.d("FirstName : ", FirstName);
                                Log.d("DisplayName : ", DisplayName);
                                Log.d("Email : ", usrEmail);
                                Log.d("userRole : ", userRole);
                                Log.d("TimeZone : ", TimeZone);
                                Log.d("IsDeleted : ", IsDeleted);
                                SharedPreferences.Editor editor2 = getSharedPreferences("zupportdesk", MODE_PRIVATE).edit();
                                editor2.putString("ProfileId", profileId);
                                editor2.putString("FirstName", FirstName);
                                editor2.putString("DisplayName", DisplayName);
                                editor2.putString("Email", usrEmail);
                                editor2.putString("userRole", userRole);
                                editor2.putString("TimeZone", TimeZone);
                                editor2.putString("IsDeleted", IsDeleted);
                                editor2.commit();

                                global_profileId = profileId;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // redirect to company profile list.
                            Intent intent = new Intent(Login.this, CompanyProfile.class);
                            intent.putExtra("companyData", companyData);
                            startActivity(intent);
                        }
                break;
            case "false":
                this.showErrorMessage("Please check your login credentials and try again.", "Sign in Failed!");
                break;
            default:
                this.showErrorMessage("Unknown error has occurred. Please try again.", "Sign in Failed!");
                break;
        }
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
    
    /**************************************************************************************************************************************/
    /*  Async Task to Login - Async Task to UserToken */

    class getToken extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Please wait. Logging in...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                //Post Username and password
                HttpClient httpclient = getNewHttpClient();
                HttpPost httppost = new HttpPost(BASEURL);
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair("Email", arg0[0]));
                nameValuePairs.add(new BasicNameValuePair("Password", arg0[1]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                Log.d("Results ", "tryLogin: "+responseString);
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

            // Check the results data
            Log.d("Results data : ", resultData);
            if(resultData == null || resultData.equals("null") || resultData.equals("")|| resultData.equals(" ")){
                Toast.makeText(Login.this, "Unable to login. Please try again!", Toast.LENGTH_SHORT).show();
            }else {
                try {
                    checkResultValue(resultData);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
               // Toast.makeText(Login.this, "Login Success!", Toast.LENGTH_SHORT).show();
            }
            pDialog.dismiss();
        }
    }

    /**************************************************************************************************************************************/

    //Async tak to get Profile id
    class getProfileId extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                Log.d("ZF-Results_Profile : ", "Trying");
                // get user token
                HttpClient httpclient = getNewHttpClient();
                HttpGet httpget = new HttpGet(BASEURL);
                httpget.addHeader("Authorization", "Bearer "+arg0[0]);
                Log.d("ZF-Profile_url : ", BASEURL);
                // Execute HTTP get Request
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String responseprofileString = EntityUtils.toString(entity, "UTF-8");
                Log.d("ZF-Results_Profile : ", responseprofileString);
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
   }
    /**************************************************************************************************************************************/

}
