package pt.pata.patadroid.webutils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;

import pt.pata.patadroid.pt.pata.patadroid.modelo.Paciente;


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




    public static Bitmap getImage(String id)
            throws ClientProtocolException, IOException, RestClientException, JSONException {

        Bitmap imagem=null;

        URL img_value = null;
        img_value = new URL("http://www.nicolau.info/images/"+id+".jpg");
        imagem
                = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
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



}
