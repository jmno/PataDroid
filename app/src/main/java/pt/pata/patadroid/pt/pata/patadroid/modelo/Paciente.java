package pt.pata.patadroid.pt.pata.patadroid.modelo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Nicolau on 23/12/14.
 */
public class Paciente implements Serializable{

    private static final long serialVersionUID = -7101605979273721440L;

    private int id;
    private String nome;
    private String dataNasc;
    private String morada;
    private String cc;
    private String telefone;
    private int terapeutaID;
    private String sexo;
    private ArrayList<EpisodioClinico> listaEpisodios;

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public ArrayList<EpisodioClinico> getListaEpisodios() {
        return listaEpisodios;
    }

    public void setListaEpisodios(ArrayList<EpisodioClinico> listaEpisodios) {
        this.listaEpisodios = listaEpisodios;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNasc() {

        String[] dataPartida = dataNasc.split("/");
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

        return (day+"/"+month+"/"+year);

    }

    public void setDataNasc(String dataNasc) {
        this.dataNasc = dataNasc;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getTerapeutaID() {
        return terapeutaID;
    }

    public void setTerapeutaID(int terapeutaID) {
        this.terapeutaID = terapeutaID;
    }



    @Override
    public String toString() {
        return nome ;
    }
}
