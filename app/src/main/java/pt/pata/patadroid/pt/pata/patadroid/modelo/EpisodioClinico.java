package pt.pata.patadroid.pt.pata.patadroid.modelo;

/**
 * Created by Nicolau on 26/12/14.
 */
public class EpisodioClinico {
    private String data;
    private String diagnostico;
    private int id;
    private int idPaciente;

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


    @Override
    public String toString() {
        return diagnostico + " - "+data ;
    }
}
