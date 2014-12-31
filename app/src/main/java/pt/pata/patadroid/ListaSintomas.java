package pt.pata.patadroid;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import pt.pata.patadroid.pt.pata.patadroid.modelo.Sintoma;
import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class ListaSintomas extends ActionBarActivity {
    private String token;
    private ArrayList<Sintoma> listaSintomas;
    private ListView listaViewSintomas;
    ProgressDialog ringProgressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_sintomas);

        token = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "token", "defaultStringIfNothingFound");
        listaViewSintomas = (ListView) findViewById(R.id.listView_ListaSintomas_list);


        new GetSintomas().execute(token);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_lista_sintomas, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetSintomas extends AsyncTask<String, Void, ArrayList<Sintoma>> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(ListaSintomas.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();


        }

        ;

        @Override
        protected ArrayList<Sintoma> doInBackground(String... params) {
            ArrayList<Sintoma> lista = new ArrayList<Sintoma>();


            try {
                lista = WebServiceUtils.getAllSintomas(params[0]);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return lista;
        }

        @Override
        protected void onPostExecute(ArrayList<Sintoma> lista) {
            if (lista != null && lista.size() > 0) {
                ringProgressDialog.dismiss();

                listaSintomas = lista;
                ArrayAdapter<Sintoma> adaptador = new ArrayAdapter<Sintoma>(getApplicationContext(), R.layout.layout_lista_paciente, listaSintomas);
                adaptador.sort(new Comparator<Sintoma>() {

                    @Override
                    public int compare(Sintoma lhs, Sintoma rhs) {
                        return lhs.getNome().toLowerCase().compareTo(rhs.getNome().toLowerCase());
                    }
                });
                listaViewSintomas.setAdapter(adaptador);

            } else {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Lista Vazia", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
