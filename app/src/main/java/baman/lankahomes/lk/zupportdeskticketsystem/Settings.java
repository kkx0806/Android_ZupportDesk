package baman.lankahomes.lk.zupportdeskticketsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import baman.lankahomes.lk.zupportdeskticketsystem.Data.navigationDrawerFragment;

public class Settings extends AppCompatActivity {

    private Toolbar toolbar;
    private navigationDrawerFragment drawerFragment;

    public TextView displayname;
    public TextView displayEmail;
    public Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Settings");
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.app_bar_dashboard);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (navigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setup(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        logout = (Button) findViewById(R.id.st_btn_logout);
        displayname = (TextView) findViewById(R.id.st_txt_displayname);
        displayEmail = (TextView) findViewById(R.id.st_txt_displayemaill);

        SharedPreferences prefs = getSharedPreferences("zupportdesk", MODE_PRIVATE);
        String DisplayName = prefs.getString("DisplayName", "Not defined");
        String Email = prefs.getString("Email", "Not defined");

        displayname.setText(DisplayName);
        displayEmail.setText(Email);


        logout.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }


    private void logout(){
        SharedPreferences.Editor editor = getSharedPreferences("zupportdesk", MODE_PRIVATE).edit();
        editor.putString("islogged", "false");
        editor.commit();


        //Clear all previous activities and go to login
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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

}
