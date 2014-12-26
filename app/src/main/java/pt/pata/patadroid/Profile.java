package pt.pata.patadroid;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.pata.patadroid.pt.pata.patadroid.modelo.Paciente;
import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class Profile extends ActionBarActivity {

    private TextView textViewNome;
    private TextView textViewData;
    private TextView textViewCC;
    private TextView textViewTelefone;
    private TextView textViewMorada;
    private ListView listViewEpisodios;
    private CircleImageView imagemProfile;
    private Paciente paciente;
    private String token;
    private Bitmap imagemPaciente;
    private ArrayList<String> listaEpisodios;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        token = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "token", "defaultStringIfNothingFound");

        Gson g = new Gson();
        Bundle extras = getIntent().getExtras();
        paciente = new Paciente();
        paciente = g.fromJson(extras.getString("paciente"),Paciente.class);
        textViewNome = (TextView) findViewById(R.id.textView_Profile_nome);
        textViewData = (TextView) findViewById(R.id.textView_Profile_data);
        textViewCC = (TextView) findViewById(R.id.textView_Profile_cc);
        textViewMorada = (TextView) findViewById(R.id.textView_Profile_morada);
        textViewTelefone = (TextView) findViewById(R.id.textView_Profile_telefone);
        listViewEpisodios = (ListView) findViewById(R.id.listView_Profile_listaEpisodiosClinicos);
        imagemProfile = (CircleImageView) findViewById(R.id.profile_image);

        new GetImage().execute(paciente.getId()+"");





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    public void preencherAtividade(Paciente p)
    {
        textViewNome.setText(p.getNome());
        textViewData.setText(p.getDataNasc());
        textViewCC.setText("CC:"+p.getCc());
        textViewMorada.setText(p.getMorada());
        textViewTelefone.setText("Tlf:" +p.getTelefone());
        imagemProfile.setImageBitmap(imagemPaciente);
      //  listViewEpisodios.setAdapter(new ArrayAdapter<String>(listaEpisodios));
    }


    private class GetImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected void onPreExecute() {

        };
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap imagem = null;

            try {
                imagem = WebServiceUtils.getImage(params[0]);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return imagem;
        }

        @Override
        protected void onPostExecute(Bitmap imagem) {
            if (imagem != null) {
                imagemPaciente = imagem;
                preencherAtividade(paciente);
            } else {

            }
        }
    }
}
