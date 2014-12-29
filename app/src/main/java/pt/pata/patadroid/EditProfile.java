package pt.pata.patadroid;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import pt.pata.patadroid.pt.pata.patadroid.modelo.Paciente;
import pt.pata.patadroid.webutils.RestClientException;
import pt.pata.patadroid.webutils.WebServiceUtils;


public class EditProfile extends ActionBarActivity {
    private Gson g;
    private Paciente paciente;
    private EditText data;
    private EditText cc;
    private EditText nome;
    private EditText morada;
    private EditText telefone;
    private Spinner spinner_sexo;
    private String token;
    private int mYear, mMonth, mDay;

    Bundle extras;
    ProgressDialog ringProgressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        token = PreferenceManager.getDefaultSharedPreferences(this).getString(
                "token", "defaultStringIfNothingFound");

        telefone = (EditText) findViewById(R.id.editText_EditProfile_Telefone);
        morada = (EditText) findViewById(R.id.editText_EditProfile_morada);
        nome = (EditText) findViewById(R.id.editText_EditProfile_nome);
        cc = (EditText) findViewById(R.id.editText_EditProfile_CC);
        data = (EditText) findViewById(R.id.editText_EditProfile_Data);
        spinner_sexo = (Spinner) findViewById(R.id.spinner_EditProfile_Sexo);
        ArrayList<String> listaSexos = new ArrayList<>();
        listaSexos.add("H");
        listaSexos.add("M");
        ArrayAdapter<String> adaptadorSexo = new ArrayAdapter<String>(getApplicationContext(),R.layout.layout_lista_paciente,listaSexos);
        spinner_sexo.setAdapter(adaptadorSexo);
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(EditProfile.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                String day = "";
                                if(dayOfMonth <10)
                                    day = "0"+dayOfMonth;
                                else
                                    day = dayOfMonth+"";

                                String month;
                                if((monthOfYear+1) <10)
                                    month = "0"+(monthOfYear+1);
                                else
                                    month = (monthOfYear+1)+"";

                                data.setText(day +"/"+ month
                                        + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        g = new Gson();
        extras = getIntent().getExtras();
        if (extras != null) {
            this.setTitle("Editar Profile");
            paciente = new Paciente();
            paciente =  g.fromJson(extras.getString("paciente"),Paciente.class);
            preencherAtividade(paciente);
        } else {
            this.setTitle("Novo Profile");
            paciente = new Paciente();

        }

    }

    public void preencherAtividade(Paciente p) {
        nome.setText(p.getNome());
        data.setText(p.getDataNasc());
        cc.setText(p.getCc());
        morada.setText(p.getMorada());
        telefone.setText(p.getTelefone());

        if(p.getSexo().toLowerCase().equals("h"))
        spinner_sexo.setSelection(0);
        else
            spinner_sexo.setSelection(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_save:
                if (extras == null) {
                    if (!telefone.getText().toString().trim().equals("") && !cc.getText().toString().trim().equals("") && !data.getText().toString().trim().equals("") && !nome.getText().toString().trim().equals("") && !morada.getText().toString().trim().equals("")) {
                        paciente = new Paciente();
                        paciente.setTelefone(telefone.getText().toString());
                        paciente.setCc(cc.getText().toString());
                        paciente.setDataNasc(data.getText().toString());
                        paciente.setNome(nome.getText().toString());
                        paciente.setMorada(morada.getText().toString());
                        paciente.setSexo((String)spinner_sexo.getSelectedItem());
                        new AddPaciente().execute(token);
                    } else {
                        Toast.makeText(getApplicationContext(), "Preencha Todos os Campos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!telefone.getText().toString().trim().equals("") && !cc.getText().toString().trim().equals("") && !data.getText().toString().trim().equals("") && !nome.getText().toString().trim().equals("") && !morada.getText().toString().trim().equals("")) {

                        paciente.setTelefone(telefone.getText().toString());
                        paciente.setCc(cc.getText().toString());
                        paciente.setDataNasc(data.getText().toString());
                        paciente.setNome(nome.getText().toString());
                        paciente.setMorada(morada.getText().toString());
                        paciente.setSexo((String) spinner_sexo.getSelectedItem());
                        new EditPaciente().execute(token);
                    } else {
                        Toast.makeText(getApplicationContext(), "Preencha Todos os Campos", Toast.LENGTH_SHORT).show();
                    }

                }


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class AddPaciente extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(EditProfile.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();


        }

        ;

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean resultado = false;


            try {
                resultado = WebServiceUtils.addPaciente(params[0], paciente);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if (resultado) {
                ringProgressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Paciente Adicionado com Sucesso", Toast.LENGTH_SHORT).show();

                finish();

            } else {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Paciente não Adicionado", Toast.LENGTH_SHORT).show();
                paciente = new Paciente();

            }
        }
    }

    private class EditPaciente extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            ringProgressDialog = new ProgressDialog(EditProfile.this, R.style.NewDialog);
            ringProgressDialog.setCancelable(false);
            ringProgressDialog.show();


        }

        ;

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean resultado = false;


            try {
                resultado = WebServiceUtils.editPaciente(params[0], paciente);
            } catch (IOException | RestClientException
                    | JSONException e) {
                e.printStackTrace();
            }

            return resultado;
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            if (resultado) {
                ringProgressDialog.dismiss();

                Toast.makeText(getApplicationContext(), "Paciente Alterado com Sucesso", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("paciente",g.toJson(paciente,Paciente.class));
                setResult(RESULT_OK,resultIntent);
                finish();


            } else {
                ringProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Paciente não Alterado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
