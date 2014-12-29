package pt.pata.patadroid;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.pata.patadroid.pt.pata.patadroid.modelo.EpisodioClinico;
import pt.pata.patadroid.pt.pata.patadroid.modelo.Paciente;
import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class Profile extends ActionBarActivity  {

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
    private Gson g;
    ProgressDialog ringProgressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        token = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "token", "defaultStringIfNothingFound");

        g = new Gson();
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
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent editPro = new Intent(getApplicationContext(),EditProfile.class);
                editPro.putExtra("paciente",paciente);
                startActivityForResult(editPro,1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle b = data.getExtras();
                if (b != null) {
                    paciente = (Paciente) b.getSerializable("paciente");
                    new GetImage().execute(paciente.getId()+"");
                }
            } else if (resultCode == 0) {
                System.out.println("RESULT CANCELLED");
            }
        }
    }

    public void preencherAtividade(Paciente p)
    {
        textViewNome.setText(p.getNome());
        String data = p.getDataNasc();
        String[] dataPartida = data.split("/");
        int dayOfMonth = Integer.parseInt(dataPartida[0]);
        int monthInt = Integer.parseInt(dataPartida[1]);
        int year = Integer.parseInt(dataPartida[2]);
        String day = "";
        if(dayOfMonth <10)
            day = "0"+dayOfMonth;
        else
            day = dayOfMonth+"";

        String month;
        if(monthInt <10)
            month = "0"+monthInt;
        else
            month = monthInt+"";

        textViewData.setText(day+"/"+month+"/"+year);
        textViewCC.setText("CC:"+p.getCc());
        textViewMorada.setText(p.getMorada());
        textViewTelefone.setText("Tlf:" +p.getTelefone());
        imagemProfile.setImageBitmap(imagemPaciente);
      //  listViewEpisodios.setAdapter(new ArrayAdapter<String>(listaEpisodios));
    }


    private class GetImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(Profile.this,R.style.NewDialog);
            ringProgressDialog.setCancelable(false);

            ringProgressDialog.show();
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
                new GetEpisodiosClinicos().execute(token, paciente.getId() + "");
            } else {
                new GetEpisodiosClinicos().execute(token, paciente.getId() + "");
            }
        }
    }

    private class GetEpisodiosClinicos extends AsyncTask<String, Void, ArrayList<EpisodioClinico>> {


        @Override
        protected void onPreExecute() {

        };
        @Override
        protected ArrayList<EpisodioClinico> doInBackground(String... params) {
            ArrayList<EpisodioClinico> lista = null;

            try {
                lista = WebServiceUtils.getEpisodiosClinicosByIDPaciente(params[0], params[1]);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return lista;
        }

        @Override
        protected void onPostExecute(ArrayList<EpisodioClinico> lista) {
            if (lista != null && lista.size()>1) {
                paciente.setListaEpisodios(lista);

                ArrayAdapter<EpisodioClinico> adaptador = new ArrayAdapter<EpisodioClinico>(getApplicationContext(),R.layout.layout_lista_paciente,lista);
                listViewEpisodios.setAdapter(adaptador);
                preencherAtividade(paciente);
                ringProgressDialog.dismiss();

            } else {
                Toast.makeText(getApplicationContext(),"Não Foram encontrados Episodios Clínicos para o Paciente" + paciente.getId(),Toast.LENGTH_LONG).show();
                preencherAtividade(paciente);
                ringProgressDialog.dismiss();

            }
        }
    }
}
