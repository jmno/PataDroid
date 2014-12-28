package pt.pata.patadroid.pt.pata.patadroid.modelo;

import java.util.List;

/**
 * Created by Nicolau on 26/12/14.
 */
public class EpisodioClinico {
    private String data;
    private String diagnostico;
    private int id;
    private int idPaciente;
    private List<Sintoma> listaSintomas;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public List<Sintoma> getListaSintomas() {
        return listaSintomas;
    }

    public void setListaSintomas(List<Sintoma> listaSintomas) {
        this.listaSintomas = listaSintomas;
    }

    @Override
    public String toString() {
        return "EpisodioClinico{" +
                "diagnostico='" + diagnostico + '\'' +
                ", idPaciente=" + idPaciente +
                '}';
    }
}
