public class HistoricoConsultasFake {
    private int contador = 0;

    public void salvar() {
        contador++;
    }

    public int totalConsultas() {
        return contador;
    }
}