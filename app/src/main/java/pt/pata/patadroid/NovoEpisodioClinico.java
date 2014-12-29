package pt.pata.patadroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import pt.pata.patadroid.pt.pata.patadroid.modelo.EpisodioClinico;
import pt.pata.patadroid.pt.pata.patadroid.modelo.Paciente;
import pt.pata.patadroid.pt.pata.patadroid.modelo.Sintoma;
import pt.pata.patadroid.pt.pata.patadroid.modelo.SistemaPericial;
import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class NovoEpisodioClinico extends ActionBarActivity {
    private String token;
    private ArrayAdapter<Paciente> adaptadorPacientes;
    private Dialog dialogPacientes;
    public static ListView listaViewPacientes;
    public static EditText texto_Paciente;
    private Paciente paciente;
    ProgressDialog ringProgressDialog = null;
    private Bitmap imagemPaciente;
    private TextView textViewNome;
    private TextView textViewTelefone;
    private CircleImageView imagemProfile;
    private Button btn_gerarDiagnosticos;
    private TextView textView_NovoEpisodio_tratamento;
    private ListView listaDiagnosticos;
    private SistemaPericial diagnostico;
    private String diagnosticoString = "";
    private EpisodioClinico episodioClinico;

    private ImageView novoSintoma;

    //Dialog Escolher Sintomas
    private Dialog dialogSintomas;
    private ListView listaSintomas;
    private ArrayList<Sintoma> listaSintomaDialog;
    private SintomaAdapter adaptadorDialogSintoma;
    private Button btn_okSintomas;
    private ArrayList<Sintoma> listaSintomasEpisodioTemp;


    //Sintomas Escolhidos
    private ArrayList<Sintoma> listaSintomasEpisodio;
    private ListView listViewSintomasNovoEpisodioClinico;
    private ArrayAdapter<Sintoma> adaptadorSintomasEpisodio;
    private ArrayList<Integer> selectedItemsIndexList;
    private boolean[] isSelectedArray;
    private boolean[] isSelectedArrayTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_episodio_clinico);
        token = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "token", "defaultStringIfNothingFound");
        textViewNome = (TextView) findViewById(R.id.textView_NovoEpisodio_nome);
        textViewTelefone = (TextView) findViewById(R.id.textView_NovoEpisodio_telefone);
        imagemProfile = (CircleImageView) findViewById(R.id.profile_image_NovoEpisodio);
        listViewSintomasNovoEpisodioClinico = (ListView) findViewById(R.id.listView_NovoEpisodio_listaSintomas);

        textView_NovoEpisodio_tratamento = (TextView) findViewById(R.id.textView_NovoEpisodio_tratamentoString);

        novoSintoma = (ImageView) findViewById(R.id.imageView_NovoEpisodio_novoSintoma);
        btn_gerarDiagnosticos = (Button) findViewById(R.id.button_NovoEpisodio_btnGerarResultados);
        btn_gerarDiagnosticos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView_NovoEpisodio_tratamento.setText("");
                new GetSistemaPericial().execute();
            }
        });
        listaSintomasEpisodio = new ArrayList<Sintoma>();
        novoSintoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                procuraSintomas();
            }
        });
        listaDiagnosticos = (ListView) findViewById(R.id.listView_NovoEpisodio_listaDiagnosticos);
        listaDiagnosticos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                diagnostico = (SistemaPericial) listaDiagnosticos.getAdapter().getItem(position);
                textView_NovoEpisodio_tratamento.setText(diagnostico.getTratamento().toString());
                diagnosticoString = diagnostico.getDiagnostico()+"|"+diagnostico.getTratamento();
            }
        });

        paciente = new Paciente();

        procuraPaciente();
        new GetPacientes().execute();

        selectedItemsIndexList = new ArrayList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_save:
                if(!diagnosticoString.equals(""))
                {
                    episodioClinico = new EpisodioClinico();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();

                    episodioClinico.setData(dateFormat.format(date));
                    episodioClinico.setDiagnostico(diagnosticoString);
                    episodioClinico.setIdPaciente(paciente.getId());
                    episodioClinico.setListaSintomas(listaSintomasEpisodio);
                    new AddEpisodioClinico().execute();
                }
                else
                Toast.makeText(getApplicationContext(),"Tem de selecionar um Diagnóstico", Toast.LENGTH_SHORT).show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface multiChoiceListDialogListener {
        public void onOkay(ArrayList<Integer> arrayList);

        public void onCancel();
    }

    public void preencherAtividade(Paciente p) {
        textViewNome.setText(p.getNome());
        textViewTelefone.setText("Tlf:" + p.getTelefone());
        imagemProfile.setImageBitmap(imagemPaciente);
    }

    public void procuraPaciente() {
        dialogPacientes = new Dialog(NovoEpisodioClinico.this);
        dialogPacientes.setContentView(R.layout.dialog_procura_paciente);
        dialogPacientes.setTitle("Escolha o Paciente:");
        dialogPacientes.getWindow()
                .setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final EditText nomeEditText = (EditText) dialogPacientes
                .findViewById(R.id.editText_escolhaPaciente);
        nomeEditText.addTextChangedListener(new TextWatcher() {

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
        listaViewPacientes = (ListView) dialogPacientes.findViewById(R.id.listView_ProcuraPacientes);

        listaViewPacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                paciente = (Paciente) listaViewPacientes.getAdapter().getItem(position);
                new GetImage().execute(paciente.getId() + "");
            }
        });


        dialogPacientes.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (paciente.getId() == 0)
                    finish();
            }
        });
    }

    public void procuraSintomas() {
        AlertDialog dialog;
        final CharSequence[] items = new CharSequence[listaSintomaDialog.size()];

        for (int i = 0; i < listaSintomaDialog.size(); i++) {
            items[i] = listaSintomaDialog.get(i).toString();
        }
        listaSintomasEpisodioTemp = new ArrayList<Sintoma>();
        for (int i = 0; i < listaSintomasEpisodio.size(); i++)
            listaSintomasEpisodioTemp.add(listaSintomasEpisodio.get(i));
        for (int i = 0; i < listaSintomaDialog.size(); i++)
            isSelectedArrayTemp[i] = isSelectedArray[i];

        // arraylist to keep the selected items
        final ArrayList seletedItems = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escolha o(s) Sintomas");
        builder.setMultiChoiceItems(items, isSelectedArray,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        int conta = 0;
                        for (int i = 0; i < listaSintomaDialog.size(); i++)
                            if (isSelectedArray[i] == true)
                                conta += 1;

                        if (isChecked) {
                            if (conta > 11) {
                                ((AlertDialog) dialog).getListView().setItemChecked(indexSelected, false);
                                isSelectedArray[indexSelected] = false;
                                Toast.makeText(getApplicationContext(), "Selecione apenas 11 Elementos", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Ncheck", Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        listaSintomasEpisodio = new ArrayList<Sintoma>();
                        for (int i = 0; i < listaSintomaDialog.size(); i++) {
                            if (isSelectedArray[i] == true)
                                listaSintomasEpisodio.add(listaSintomaDialog.get(i));
                        }


                        adaptadorSintomasEpisodio = new ArrayAdapter<Sintoma>(getApplicationContext(), R.layout.layout_lista_paciente, listaSintomasEpisodio);
                        listViewSintomasNovoEpisodioClinico.setAdapter(adaptadorSintomasEpisodio);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for (int i = 0; i < listaSintomaDialog.size(); i++) {
                            isSelectedArray[i] = isSelectedArrayTemp[i];
                        }
                    }
                });

        dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();
    }

    public void preencheLista() {
        adaptadorDialogSintoma = new SintomaAdapter(this, listaSintomaDialog);
        listaSintomas.setAdapter(adaptadorDialogSintoma);
        Toast.makeText(this, listaSintomaDialog.toString(), Toast.LENGTH_SHORT).show();
        dialogSintomas.show();
    }


    private class GetPacientes extends AsyncTask<String, Void, ArrayList<Paciente>> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(NovoEpisodioClinico.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();


        }

        ;

        @Override
        protected ArrayList<Paciente> doInBackground(String... params) {
            ArrayList<Paciente> lista = new ArrayList<Paciente>();


            try {
                lista = WebServiceUtils.getAllPacientes(token);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return lista;
        }

        @Override
        protected void onPostExecute(ArrayList<Paciente> lista) {
            if (lista != null && lista.size() > 1) {
                ringProgressDialog.dismiss();

                adaptadorPacientes = new ArrayAdapter<Paciente>(getApplicationContext(), R.layout.layout_lista_paciente, lista);
                adaptadorPacientes.sort(new Comparator<Paciente>() {

                    @Override
                    public int compare(Paciente lhs, Paciente rhs) {
                        return lhs.getNome().toLowerCase().compareTo(rhs.getNome().toLowerCase());
                    }
                });
                listaViewPacientes.setAdapter(adaptadorPacientes);
                dialogPacientes.show();
            } else {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Lista Vazia", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private class GetImage extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(NovoEpisodioClinico.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);

            ringProgressDialog.show();
        }

        ;

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap imagem = null;

            try {
                imagem = WebServiceUtils.getImage(paciente.getSexo(),paciente.getId()+"");
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
                ringProgressDialog.dismiss();
                dialogPacientes.dismiss();

                preencherAtividade(paciente);
                new GetAllSintomas().execute(token);
            } else {
                ringProgressDialog.dismiss();
                dialogPacientes.dismiss();

                preencherAtividade(paciente);
                new GetAllSintomas().execute(token);
            }
        }
    }

    private class GetAllSintomas extends AsyncTask<String, Void, ArrayList<Sintoma>> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(NovoEpisodioClinico.this, R.style.NewDialog);
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
            if (lista != null && lista.size() > 1) {
                ringProgressDialog.dismiss();

                listaSintomaDialog = lista;
                //preencheLista();
                isSelectedArray = new boolean[listaSintomaDialog.size()];
                isSelectedArrayTemp = new boolean[listaSintomaDialog.size()];
                for (int i = 0; i < listaSintomaDialog.size(); i++) {
                    isSelectedArray[i] = false;
                    isSelectedArrayTemp[i] = false;
                }

                /*adaptadorDialogSintoma = new ArrayAdapter<Sintoma>(getApplicationContext(), R.layout.layout_lista_sintomas, listaSintomaDialog);
                adaptadorDialogSintoma.sort(new Comparator<Sintoma>() {

                    @Override
                    public int compare(Sintoma lhs, Sintoma rhs) {
                        return lhs.getNome().toLowerCase().compareTo(rhs.getNome().toLowerCase());
                    }
                });
                listaSintomas.setAdapter(adaptadorDialogSintoma);*/


            } else {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Lista Vazia", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private class GetSistemaPericial extends AsyncTask<String, Void, ArrayList<SistemaPericial>> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(NovoEpisodioClinico.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();
        }

        @Override
        protected ArrayList<SistemaPericial> doInBackground(String... params) {
            ArrayList<SistemaPericial> lista = new ArrayList<SistemaPericial>();


            try {
                lista = WebServiceUtils.getSistemaPericial(token, listaSintomasEpisodio);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return lista;
        }

        @Override
        protected void onPostExecute(ArrayList<SistemaPericial> lista) {
            if (lista != null && lista.size() > 1) {
                ArrayAdapter<SistemaPericial> adaptador = new ArrayAdapter<SistemaPericial>(getApplicationContext(), R.layout.layout_lista_paciente, lista);
                listaDiagnosticos.setAdapter(adaptador);
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),lista.toString(),Toast.LENGTH_SHORT).show();

            } else {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Lista Vazia", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private class AddEpisodioClinico extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(NovoEpisodioClinico.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean resultado = false;

            try {
                resultado = WebServiceUtils.addEpisodioClinico(token, episodioClinico);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if (resultado) {
                Toast.makeText(getApplicationContext(),"Episódio Adicionado com Sucesso", Toast.LENGTH_SHORT).show();
                ringProgressDialog.dismiss();
                finish();

            } else {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Erro Adicionar Episódio - Verifique Token", Toast.LENGTH_SHORT).show();
            }
        }


    }

}
