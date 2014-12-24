package pt.pata.patadroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import pt.pata.patadroid.pt.pata.patadroid.modelo.Paciente;
import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class MainActivity extends ActionBarActivity {
    private String token;
    private ArrayList<Paciente> listaPacientes;
    private ListView listaViewPacientes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        token = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "token", "defaultStringIfNothingFound");
        listaViewPacientes = (ListView) findViewById(R.id.listView_Main);
        listaViewPacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Paciente p = (Paciente) listaViewPacientes.getAdapter().getItem(position);
                Gson g = new Gson();
                Intent profile = new Intent(getApplicationContext(),Profile.class);
                profile.putExtra("paciente",g.toJson(p,Paciente.class));
                startActivity(profile);
            }
        });
        new GetPacientes().execute(token);
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

        return super.onOptionsItemSelected(item);
    }

    private class GetPacientes extends AsyncTask<String, Void, ArrayList<Paciente>> {


        @Override
        protected void onPreExecute() {

        };
        @Override
        protected ArrayList<Paciente> doInBackground(String... params) {
            ArrayList<Paciente> lista = new ArrayList<Paciente>();


            try {
                lista = WebServiceUtils.getAllPacientes(params[0]);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return lista;
        }

        @Override
        protected void onPostExecute(ArrayList<Paciente> lista) {
            if (lista != null) {

                listaPacientes = lista;
                ArrayAdapter<Paciente> adaptador = new ArrayAdapter<Paciente>(getApplicationContext(),android.R.layout.simple_list_item_1,listaPacientes);
                listaViewPacientes.setAdapter(adaptador);
                    //startActivity(new Intent(getApplicationContext(),Profile.class));
                    Toast.makeText(getApplicationContext(), "Adaptador", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(), "Erro GetAllPacientes", Toast.LENGTH_SHORT).show();
               }
        }
    }
}
