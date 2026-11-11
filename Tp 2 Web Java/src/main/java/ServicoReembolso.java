public class ServicoReembolso {
    HistoricoConsultasFake historico;
    spyAuditoria auditoria;
    AutorizadorReembolso autorizador;

    public ServicoReembolso(HistoricoConsultasFake historico, spyAuditoria auditoria, AutorizadorReembolso autorizador) {
        this.historico = historico;
        this.auditoria = auditoria;
        this.autorizador = autorizador;
    }

    public double calcular(double valor, PlanoSaude plano, Paciente paciente) {
        // Valida se o paciente possui autorização utilizando um Mock para simular o comportamento externo
        if (!autorizador.autorizar(paciente)) {
            throw new RuntimeException("Negado");
        }

        // Executa as chamadas de efeitos colaterais utilizando Fake e Spy
        historico.salvar();
        auditoria.registrar();

        double resultado = valor * plano.getPercentual();

        // Aplica a regra de negócio do teto máximo de reembolso (R$ 150.00)
        if (resultado > 150.0) {
            return 150.0;
        }

        return resultado;
    }
}