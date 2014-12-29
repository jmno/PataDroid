package pt.pata.patadroid.webutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;

import pt.pata.patadroid.pt.pata.patadroid.modelo.EpisodioClinico;
import pt.pata.patadroid.pt.pata.patadroid.modelo.Paciente;
import pt.pata.patadroid.pt.pata.patadroid.modelo.Sintoma;


public class WebServiceUtils {

    public static String URL = "https://pata.apphb.com/Service1.svc/REST/";
    public static HttpClient client = new DefaultHttpClient();

    public static String logIn(String username, String password)
            throws ClientProtocolException, IOException, RestClientException,
            JSONException {
        String token = "";

        HttpPost httpPost = new HttpPost(URL + "login?username=" + username
                + "&password=" + password);

        BasicHttpResponse httpResponse = (BasicHttpResponse) client
                .execute(httpPost);

        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = httpResponse.getEntity();
            token = EntityUtils.toString(entity);

        } else {
            throw new RestClientException(
                    "HTTP Response with invalid status code "
                            + httpResponse.getStatusLine().getStatusCode()
                            + ".");
        }

        return token;

    }

    public static Boolean isLoggedIn(String token)
            throws ClientProtocolException, IOException, RestClientException {
        Boolean resultado = false;

        HttpGet request = new HttpGet(URL + "isLoggedIn?token=" + token);
        // request.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
        // "application/json"));
        request.setHeader("Accept", "Application/JSON");

        BasicHttpResponse basicHttpResponse = (BasicHttpResponse) client
                .execute(request);

        if (isOk(basicHttpResponse.getStatusLine().getStatusCode())) {

            HttpEntity entity = basicHttpResponse.getEntity();
            resultado = Boolean.valueOf(EntityUtils.toString(entity));

        } else {
            throw new RestClientException(
                    "HTTP Response with invalid status code "
                            + basicHttpResponse.getStatusLine().getStatusCode()
                            + ".");
        }

        return resultado;
    }


    public static Bitmap getImage(String id)
            throws ClientProtocolException, IOException, RestClientException, JSONException {

        Bitmap imagem = null;

        URL img_value = null;
        img_value = new URL("http://www.nicolau.info/images/" + id + ".jpg");
        try{imagem
                = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());}
        catch (Exception e)
        {
            Log.i("error", e.getMessage().toString());
        }
        return imagem;

    }

    public static ArrayList<Paciente> getAllPacientes(String token)
            throws ClientProtocolException, IOException,
            JSONException, RestClientException {
        ArrayList<Paciente> listaPacientes = null;

        HttpGet request = new HttpGet(URL + "getAllPacientesByTerapeuta?token=" + token);
        // request.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
        // "application/json"));
        request.setHeader("Accept", "Application/JSON");
        BasicHttpResponse basicHttpResponse = (BasicHttpResponse) client
                .execute(request);


        Gson g = new Gson();
        if (basicHttpResponse.getStatusLine().getStatusCode() == 200) {
            listaPacientes = new ArrayList<Paciente>();
            Type collectionType = new TypeToken<ArrayList<Paciente>>() {
            }.getType();
            listaPacientes = g.fromJson(
                    EntityUtils.toString(basicHttpResponse.getEntity()),
                    collectionType);

        } else {
            throw new RestClientException(
                    "HTTP Response with invalid status code "
                            + basicHttpResponse.getStatusLine().getStatusCode()
                            + ".");
        }

        return listaPacientes;
    }

    public static ArrayList<EpisodioClinico> getEpisodiosClinicosByIDPaciente(String token, String idPaciente)
            throws ClientProtocolException, IOException,
            JSONException, RestClientException {
        ArrayList<EpisodioClinico> lista = null;

        HttpGet request = new HttpGet(URL + "getAllEpisodiosByIDPaciente?token=" + token + "&idPaciente=" + idPaciente);
        // request.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
        // "application/json"));
        request.setHeader("Accept", "Application/JSON");

        BasicHttpResponse basicHttpResponse = (BasicHttpResponse) client
                .execute(request);


        Gson g = new Gson();
        if (basicHttpResponse.getStatusLine().getStatusCode() == 200) {
            lista = new ArrayList<EpisodioClinico>();
            Type collectionType = new TypeToken<ArrayList<EpisodioClinico>>() {
            }.getType();
            lista = g.fromJson(
                    EntityUtils.toString(basicHttpResponse.getEntity()),
                    collectionType);

        } else {
            throw new RestClientException(
                    "HTTP Response with invalid status code "
                            + basicHttpResponse.getStatusLine().getStatusCode()
                            + ".");
        }

        return lista;
    }

    public static Boolean addPaciente(String token, Paciente p)
            throws ClientProtocolException, IOException, RestClientException,
            JSONException {
        Boolean resultado = false;

        HttpPost httpPost = new HttpPost(URL + "addPaciente?token=" + token);
        Gson g = new Gson();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cc", p.getCc());
        jsonObject.put("dataNasc", p.getDataNasc());
        jsonObject.put("morada", p.getMorada());
        jsonObject.put("nome", p.getNome());
        jsonObject.put("telefone", p.getTelefone());
        jsonObject.put("dataNasc", p.getDataNasc());


        StringEntity se = new StringEntity(g.toJson(p, Paciente.class), "UTF-8");
        se.setContentType("text/json");
        se.setContentType("application/json;charset=UTF-8");

        httpPost.setEntity(se);
        HttpResponse httpResponse = client
                .execute(httpPost);

        HttpEntity entity = httpResponse.getEntity();
        String result = EntityUtils.toString(entity);
        Log.i("error",result);
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            //  HttpEntity entity = httpResponse.getEntity();
            // String string = EntityUtils.toString(entity);
            resultado = Boolean.valueOf(result);

        } else {
            throw new RestClientException(
                    "HTTP Response with invalid status code "
                            + httpResponse.getStatusLine().getStatusCode()
                           );
        }

        return resultado;

    }

    public static Boolean editPaciente(String token, Paciente p)
            throws ClientProtocolException, IOException, RestClientException,
            JSONException {
        Boolean resultado = false;

        HttpPost httpPost = new HttpPost(URL + "editPaciente?token=" + token);
        Gson g = new Gson();
        StringEntity se = new StringEntity(g.toJson(p, Paciente.class), "UTF-8");
        se.setContentType("text/json");
        se.setContentType("application/json;charset=UTF-8");

        httpPost.setEntity(se);
        BasicHttpResponse httpResponse = (BasicHttpResponse) client
                .execute(httpPost);
        HttpEntity entity = httpResponse.getEntity();
        String result = EntityUtils.toString(entity);
        Log.i("error",result);

        if (httpResponse.getStatusLine().getStatusCode() == 200) {

            resultado = Boolean.valueOf(result);

        } else {
            throw new RestClientException(
                    "HTTP Response with invalid status code "
                            + httpResponse.getStatusLine().getStatusCode()
                            + ".");
        }

        return resultado;

    }

    public static ArrayList<Sintoma> getAllSintomas(String token)
            throws ClientProtocolException, IOException,
            JSONException, RestClientException {
        ArrayList<Sintoma> listaSintomas = null;

        HttpGet request = new HttpGet(URL + "lerSintomasXML?token=" + token);
        // request.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
        // "application/json"));
        request.setHeader("Accept", "Application/JSON");
        BasicHttpResponse basicHttpResponse = (BasicHttpResponse) client
                .execute(request);


        Gson g = new Gson();
        if (basicHttpResponse.getStatusLine().getStatusCode() == 200) {
            listaSintomas = new ArrayList<Sintoma>();
            Type collectionType = new TypeToken<ArrayList<Sintoma>>() {
            }.getType();
            listaSintomas = g.fromJson(
                    EntityUtils.toString(basicHttpResponse.getEntity()),
                    collectionType);

        } else {
            throw new RestClientException(
                    "HTTP Response with invalid status code "
                            + basicHttpResponse.getStatusLine().getStatusCode()
                            + ".");
        }

        return listaSintomas;
    }
    public static Boolean isOk(int statusCode) {
        Boolean resultado = false;

        switch (statusCode) {
            case 200:
                resultado = true;
                break;
            case 201:
                resultado = true;
                break;
            case 202:
                resultado = true;
                break;
            case 203:
                resultado = true;
                break;
            default:
                break;
        }

        return resultado;
    }

}
