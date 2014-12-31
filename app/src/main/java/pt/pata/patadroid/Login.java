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
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class Login extends ActionBarActivity {
    ProgressDialog ringProgressDialog = null;

    EditText username;
    EditText password;
    private String tokenSessao;

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
                new LogInWeb().execute(username.getText().toString().trim(), password.getText().toString().trim());
            }
        });

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString("token", "defaultStringIfNothingFound");
        if (!token.equals("defaultStringIfNothingFound"))
            new IsLoggedIN().execute(token);

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
            ringProgressDialog = new ProgressDialog(Login.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            PreferenceManager
                    .getDefaultSharedPreferences(
                            getApplicationContext())
                    .edit().clear().commit();

            //ringProgressDialog = ProgressDialog.show(Login.this, "Please wait ...",	"Loging in...", true);

            //  ringProgressDialog.setCancelable(false);


            ringProgressDialog.show();
        }

        ;

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
                    tokenSessao = token;
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("token", token).commit();
                    new IsAdmin().execute(token);
                } else {
                    Toast.makeText(getApplicationContext(), "Erro Utilizador/Password", Toast.LENGTH_SHORT).show();
                    ringProgressDialog.dismiss();


                }
            } else {
                Toast.makeText(getApplicationContext(), "Erro Utilizador/Password", Toast.LENGTH_SHORT).show();
                ringProgressDialog.dismiss();

            }
        }
    }

    private class IsLoggedIN extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(Login.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();

        }

        ;

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean resultado = false;

            try {
                resultado = WebServiceUtils.isLoggedIn(params[0]);
            } catch (IOException | RestClientException e) {
                e.printStackTrace();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if (resultado) {


                // new Notifications(getApplicationContext(),
                // "Connexão Efetuada com Sucesso!");

                ringProgressDialog.dismiss();
                Intent equipa = new Intent(getBaseContext(),
                        MainActivity.class);
                startActivity(equipa);
            } else {
                Toast.makeText(getApplicationContext(), "Sessão expirada!", Toast.LENGTH_SHORT).show();
                PreferenceManager
                        .getDefaultSharedPreferences(
                                getApplicationContext())
                        .edit().clear().commit();
                ringProgressDialog.dismiss();


            }
        }
    }

    private class IsAdmin extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(Login.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();

        }

        ;

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean resultado = false;

            try {
                resultado = WebServiceUtils.isAdmin(params[0]);
            } catch (IOException | RestClientException e) {
                e.printStackTrace();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if (!resultado) {
                ringProgressDialog.dismiss();
                username.setText("");
                password.setText("");
                username.requestFocus();
                username.setHint("Username... ");
                password.setHint("Password... ");

                new GetTerapeutaNome().execute(tokenSessao);

            } else {
                Toast.makeText(getApplicationContext(), "Apenas Disponivel para Terapeutas", Toast.LENGTH_SHORT).show();
                PreferenceManager
                        .getDefaultSharedPreferences(
                                getApplicationContext())
                        .edit().clear().commit();
                ringProgressDialog.dismiss();


            }
        }
    }
    private class GetTerapeutaNome extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(Login.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();

        }

        ;

        @Override
        protected String doInBackground(String... params) {
            String resultado = "";

            try {
                resultado = WebServiceUtils.getNomeTerapeuta(params[0]);
            } catch (IOException | RestClientException e) {
                e.printStackTrace();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(String resultado) {
            if (!resultado.trim().equals("")) {
                ringProgressDialog.dismiss();
                resultado = resultado.replace("\"", "");
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("nomeTerapeuta", resultado).commit();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Erro Get Nome Terapeut", Toast.LENGTH_SHORT).show();
                PreferenceManager
                        .getDefaultSharedPreferences(
                                getApplicationContext())
                        .edit().clear().commit();
                ringProgressDialog.dismiss();


            }
        }
    }
}
