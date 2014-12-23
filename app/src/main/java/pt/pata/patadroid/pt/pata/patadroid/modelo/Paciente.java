package pt.pata.patadroid.pt.pata.patadroid.modelo;

import java.sql.Date;

/**
 * Created by Nicolau on 23/12/14.
 */
public class Paciente {
    private int id;
    private String nome;
    private Date dataNasc;
    private String morada;
    private String cc;
    private String telefone;
    private int terapeutaID;

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

    public Date getDataNasc() {
        return dataNasc;
    }

    public void setDataNasc(Date dataNasc) {
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
