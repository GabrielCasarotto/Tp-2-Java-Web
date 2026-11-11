import java.time.LocalDate;

public interface HistoricoConsultas {
    void salvar(Paciente paciente, double valor, LocalDate data);
    int totalConsultas();
}