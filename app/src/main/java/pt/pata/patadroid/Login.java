package pt.pata.patadroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class Login extends ActionBarActivity {
    ProgressDialog ringProgressDialog = null;

    EditText username;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

         username = (EditText) findViewById(R.id.editText_Login_Username);
         password = (EditText) findViewById(R.id.editText_Login_Password);

        Button btn_login = (Button) findViewById(R.id.button_Login_Login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogInWeb().execute(username.getText().toString().trim(),password.getText().toString().trim());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    private class LogInWeb extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(Login.this,R.style.NewDialog);
            ringProgressDialog.setCancelable(false);

            //ringProgressDialog = ProgressDialog.show(Login.this, "Please wait ...",	"Loging in...", true);

            //  ringProgressDialog.setCancelable(false);


             ringProgressDialog.show();
        };
        @Override
        protected String doInBackground(String... params) {
            String token = "";

            try {
                token = WebServiceUtils.logIn(params[0], params[1]);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            if (token != null) {
                if (!token.isEmpty()) {

                    ringProgressDialog.dismiss();
                    token = token.replace("\"", "");
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("token", token).commit();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Erro Utilizador/Password",Toast.LENGTH_SHORT).show();
                    ringProgressDialog.dismiss();


                }
            } else {
                Toast.makeText(getApplicationContext(),"Erro Utilizador/Password",Toast.LENGTH_SHORT).show();
               ringProgressDialog.dismiss();

            }
        }
    }
}
