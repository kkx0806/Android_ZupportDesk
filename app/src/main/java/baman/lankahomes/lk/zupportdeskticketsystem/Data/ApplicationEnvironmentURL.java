package baman.lankahomes.lk.zupportdeskticketsystem.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Baman on 7/12/2016.
 */
public class ApplicationEnvironmentURL {

    /*** Avaliable Environments -- "Testing", "Development", "Production" ***/

    public Context context;
    public String MY_PREFS_NAME = "zupport_environment";

    public ApplicationEnvironmentURL(Context context) {
        this.context=context;
    }

    public void changeEnvironment(String environment){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE).edit();
        editor.putString("environment", environment);
        editor.commit();
    }

    //get the current Environment or use default as production
    public String getEnvironment(){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE);
        String environment = prefs.getString("environment", "Production");
        return environment;
    }

    public String get_user_api_url(Context context){
        this.context=context;
        String selectedURL = null;

            String environment = getEnvironment();
            switch (environment) {
                case "Production":
                    selectedURL = "https://user-api.zupportdesk.com/api/";
                    break;
                case "Development":
                    selectedURL = "https://user-api-dev.zupportdesk.com/api/";
                    break;
                case "Testing":
                    selectedURL = "https://user-api-test.zupportdesk.com/api/";
                    break;
                default:
                    selectedURL = "https://user-api.zupportdesk.com/api/";
                    break;
            }
        Log.d("Current Environment : ", environment);
        Log.d("Generated URL : ", selectedURL);
        return selectedURL;
    }

    public String get_ticket_api_url(Context context){
        this.context=context;
        String selectedURL = null;

        String environment = getEnvironment();
        switch (environment) {
            case "Production":
                selectedURL = "https://tickets-api.zupportdesk.com:8083/api/";
                break;
            case "Development":
                selectedURL = "https://tickets-api-dev.zupportdesk.com:8083/api/";
                break;
            case "Testing":
                selectedURL = "https://ticket-api-test.zupportdesk.com/api/";
                break;
            default:
                selectedURL = "https://tickets-api.zupportdesk.com:8083/api/";
                break;
        }
        Log.d("Current Environment : ", environment);
        Log.d("Generated URL : ", selectedURL);
        return selectedURL;
    }

    public String get_chat_api_url(Context context){
        this.context=context;
        String selectedURL = null;

        String environment = getEnvironment();
        switch (environment) {
            case "Production":
                selectedURL = "https://chat-api.zupportdesk.com/api/";
                break;
            case "Development":
                selectedURL = "https://chat-api-dev.zupportdesk.com/api/";
                break;
            case "Testing":
                selectedURL = "https://chat-api-test.zupportdesk.com/api/";
                break;
            default:
                selectedURL = "https://chat-api.zupportdesk.com/api/";
                break;
        }
        Log.d("Current Environment : ", environment);
        Log.d("Generated URL : ", selectedURL);
        return selectedURL;
    }

    public String getForgetPasswordSecondParameter(Context context){
        this.context=context;
        String selectedURL = null;

        String environment = getEnvironment();
        switch (environment) {
            case "Production":
                selectedURL = "?url=www.zupportdesk.com";
                break;
            case "Development":
                selectedURL = "?url=www-dev.zupportdesk.com";
                break;
            case "Testing":
                selectedURL = "?url=www-test.zupportdesk.com";
                break;
            default:
                selectedURL = "?url=www.zupportdesk.com";
                break;
        }
        Log.d("Current Environment : ", environment);
        Log.d("Generated URL : ", selectedURL);
        return selectedURL;
    }

}
