package pt.pata.patadroid.pt.pata.patadroid.modelo;

/**
 * Created by Nicolau on 26/12/14.
 */
public class Sintoma {
    private String nome;
    private boolean selected;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return  nome ;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
