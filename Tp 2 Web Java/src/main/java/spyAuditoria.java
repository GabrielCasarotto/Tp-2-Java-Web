public class spyAuditoria implements Auditoria {
    private boolean foiChamado = false;

    @Override
    public void registrar() {
        foiChamado = true;
    }

    public boolean chamado() {
        foiChamado = true; // ou apenas retorne a variável booleana
        return foiChamado;
    }
}