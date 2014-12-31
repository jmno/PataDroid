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
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class MainActivity extends ActionBarActivity {
    private String token;
    TextView listaPacientes;
    TextView novoPaciente;
    TextView listaSintomas;
    TextView novoEpisodioClinico;
    TextView logout;
    TextView nomeTerapeuta;
    String nomeTerapeutaString;
    ProgressDialog ringProgressDialog = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("");

        token = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "token", "defaultStringIfNothingFound");
        nomeTerapeutaString = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "nomeTerapeuta", "Unknown");
        nomeTerapeuta = (TextView) findViewById(R.id.textView_Main_NomeTerapeuta);
        nomeTerapeuta.setText("Bem Vindo: " + nomeTerapeutaString);
        listaPacientes = (TextView) findViewById(R.id.textView_Main_Lista_Pacientes);
        listaPacientes.setClickable(true);
        listaPacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Pacientes.class));

            }
        });
        novoPaciente = (TextView) findViewById(R.id.textView_Main_Novo_Paciente);
        novoPaciente.setClickable(true);
        novoPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),EditProfile.class));
            }
        });
        listaSintomas = (TextView) findViewById(R.id.textView_Main_ListaSintomas);
        listaSintomas.setClickable(true);
        listaSintomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ListaSintomas.class));
            }
        });
        novoEpisodioClinico = (TextView) findViewById(R.id.textView_Main_NovoEpisodioClinico);
        novoEpisodioClinico.setClickable(true);
        novoEpisodioClinico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NovoEpisodioClinico.class));
            }});
        logout = (TextView) findViewById(R.id.textView_Main_Logout);
        logout.setClickable(true);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Logout().execute();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_edit:
             //   newGame();
               // return true;
           // case R.id.help:
            //    showHelp();
             //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        // super.onBackPressed(); // optional depending on your needs
    }

    private class Logout extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(MainActivity.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            String resultado ="";

            try {

                 WebServiceUtils.logout(token);
                resultado = "s";
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(String s) {
            PreferenceManager
                    .getDefaultSharedPreferences(
                            getApplicationContext())
                    .edit().clear().commit();
            ringProgressDialog.dismiss();

            finish();

        }


    }



}
