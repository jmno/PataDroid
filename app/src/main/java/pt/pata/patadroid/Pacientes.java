package pt.pata.patadroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import pt.pata.patadroid.pt.pata.patadroid.modelo.Paciente;
import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class Pacientes extends ActionBarActivity {

    private String token;
    private ArrayList<Paciente> listaPacientes;
    private ListView listaViewPacientes;
    private EditText pacientesText;
    ProgressDialog ringProgressDialog = null;
    private ArrayAdapter<Paciente> adaptadorPacientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);

        token = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "token", "defaultStringIfNothingFound");
        listaViewPacientes = (ListView) findViewById(R.id.listView_Pacientes_list);
        listaViewPacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Paciente p = (Paciente) listaViewPacientes.getAdapter().getItem(position);
                Gson g = new Gson();
                Intent profile = new Intent(getApplicationContext(), Profile.class);
                profile.putExtra("paciente", g.toJson(p, Paciente.class));
                startActivity(profile);
            }
        });

        pacientesText = (EditText) findViewById(R.id.editText_Pacientes_search);
        pacientesText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                adaptadorPacientes.getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });


        new GetPacientes().execute(token);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_pacientes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {

        new GetPacientes().execute(token);
        super.onRestart();
    }

    private class GetPacientes extends AsyncTask<String, Void, ArrayList<Paciente>> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(Pacientes.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();


        }

        ;

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
            if (lista != null && lista.size() > 0) {
                ringProgressDialog.dismiss();

                listaPacientes = lista;
                ArrayAdapter<Paciente> adaptador = new ArrayAdapter<Paciente>(getApplicationContext(), android.R.layout.simple_list_item_1, listaPacientes)
                {
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                        text1.setTextColor(Color.parseColor("#000000"));
                        return view;

                    };
                };
                adaptador.sort(new Comparator<Paciente>() {

                    @Override
                    public int compare(Paciente lhs, Paciente rhs) {
                        return lhs.getNome().toLowerCase().compareTo(rhs.getNome().toLowerCase());
                    }
                });
                adaptadorPacientes = adaptador;
                listaViewPacientes.setAdapter(adaptador);

            } else {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Lista Vazia", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


    }
}
