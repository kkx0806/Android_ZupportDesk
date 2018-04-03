package baman.lankahomes.lk.zupportdeskticketsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.ApplicationEnvironmentURL;
import baman.lankahomes.lk.zupportdeskticketsystem.Data.MySSLSocketFactory;

public class ForgotPassword extends ActionBarActivity {

    EditText FP_email;
    Button FP_submit;
    Context context;
    ApplicationEnvironmentURL applicationEnvironment;
    String BASEURL;
    String forget_email;
    ProgressDialog pDialog;
    ImageView appbar_image;
    TextView app_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        app_title = (TextView) findViewById(R.id.appbar_title);
        app_title.setText("Forgot Password");
        appbar_image = (ImageView) findViewById(R.id.appbar_image);
        appbar_image.setImageResource(R.drawable.icon_forgotpassword);


        applicationEnvironment = new ApplicationEnvironmentURL(this.context);
        context = this.getApplicationContext();

        FP_email = (EditText) findViewById(R.id.et_FP_email);
        FP_submit = (Button) findViewById(R.id.btn_FP_submit);
        FP_submit.setOnClickListener(onClickListener);
    }

    /* Multiple Button on click event handle */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.btn_FP_submit:
                    setForgetPasswordURL();

                    if(isNetworkAvailable()){
                        forget_email = FP_email.getText().toString();
                        if(forget_email.length() > 5 && !(forget_email.equals("")) && isEmailValid(forget_email)){
                            new ResetPassword().execute(forget_email);
                        }else{showErrorMessage("Please check your email address.", "Email not valid!");}
                    }else{showErrorMessage("Please check your internet connection.", "No Connectivity!");}
                break;
            }
        }
    };

    private void setForgetPasswordURL(){
        String user_base_url = applicationEnvironment.get_user_api_url(this.context);
        String secondParameter = applicationEnvironment.getForgetPasswordSecondParameter(this.context);
        BASEURL = user_base_url +"Account/ForgotPassword"+secondParameter;
        Log.d("Created URL :", BASEURL);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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



     /*  Async Task to forget password - Reset */

    class ResetPassword extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPassword.this);
            pDialog.setMessage("Please wait. Sending request...");
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
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                Log.d("Results ", responseString);
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

            switch (resultData){
                case "1013":
                    Toast.makeText(ForgotPassword.this, "Password reset Successful, Please check your email.", Toast.LENGTH_LONG).show();
                    break;
                case "2007":
                    showErrorMessage("Please enter correct Email Address.", "Email Not Found!");
                    break;
                default:
                    showErrorMessage("Please try again.", "Unknown Error!");
                    break;
            }
            pDialog.dismiss();
        }
    }


}
