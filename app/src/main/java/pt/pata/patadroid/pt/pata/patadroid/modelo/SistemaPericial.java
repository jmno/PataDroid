package pt.pata.patadroid.pt.pata.patadroid.modelo;

/**
 * Created by Nicolau on 29/12/14.
 */
public class SistemaPericial {
    private String diagnostico;
    private String tratamento;
    private float score;

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamento() {
        return tratamento;
    }

    public void setTratamento(String tratamento) {
        this.tratamento = tratamento;
    }

    @Override
    public String toString() {
        return  score +" - " +diagnostico ;
    }
}
