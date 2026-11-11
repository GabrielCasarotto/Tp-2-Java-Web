import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class ServicoReembolsoTest {

    private ServicoReembolso servico;
    private HistoricoConsultasFake historicoFake;
    private spyAuditoria auditoriaSpy;
    private AutorizadorReembolso autorizadorMock;

    @BeforeEach
    public void setup() {
        // Inicializa os dublês de teste antes de cada execução unitária
        historicoFake = new HistoricoConsultasFake(); // FAKE: Simula a persistência em memória
        auditoriaSpy = new spyAuditoria();             // SPY: Registra se a auditoria foi acionada
        autorizadorMock = Mockito.mock(AutorizadorReembolso.class); // MOCK: Simula a resposta do autorizador externo
        servico = new ServicoReembolso(historicoFake, auditoriaSpy, autorizadorMock);
    }

    private Paciente criarPacientePadrao() {
        return new Paciente("Gabriel");
    }

    private PlanoSaude criarPlanoPadrao(double percentual) {
        return () -> percentual; // STUB: Retorna um valor fixo simulando o percentual do plano
    }

    @Test
    public void planoSetentaPorCento() {
        // Teste Unitário: Valida o cálculo de reembolso padrão com 70% de cobertura
        Paciente p = criarPacientePadrao();
        PlanoSaude planoStub = criarPlanoPadrao(0.70);
        when(autorizadorMock.autorizar(p)).thenReturn(true);

        double res = servico.calcular(200.0, planoStub, p);
        assertEquals(140.0, res, 0.01);
        assertEquals(1, historicoFake.totalConsultas()); // Verifica o comportamento através do Fake
        assertTrue(auditoriaSpy.chamado());             // Verifica o comportamento através do Spy
    }

    @Test
    public void planoCinquentaPorCento() {
        // Teste Unitário: Valida o cálculo de reembolso com 50% de cobertura
        Paciente p = criarPacientePadrao();
        PlanoSaude planoStub = criarPlanoPadrao(0.50);
        when(autorizadorMock.autorizar(p)).thenReturn(true);

        double res = servico.calcular(200.0, planoStub, p);
        assertEquals(100.0, res, 0.01);
        assertTrue(auditoriaSpy.chamado());
    }

    @Test
    public void naoAutorizado() {
        // Teste Unitário de Exceção: Assegura que o sistema barra o reembolso se o autorizador retornar falso
        Paciente p = criarPacientePadrao();
        PlanoSaude planoStub = criarPlanoPadrao(0.70);
        when(autorizadorMock.autorizar(p)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            servico.calcular(200.0, planoStub, p);
        });
    }

    @Test
    public void respeitaTetoReembolso() {
        // Teste Unitário de Regra de Negócio: Garante que o valor retornado nunca ultrapassa o teto de R$ 150.00
        Paciente p = criarPacientePadrao();
        PlanoSaude planoStub = criarPlanoPadrao(0.90);
        when(autorizadorMock.autorizar(p)).thenReturn(true);

        double res = servico.calcular(300.0, planoStub, p);
        assertEquals(150.0, res, 0.01);
    }

    @Test
    public void cenarioCompletoIntegrado() {
        // Teste de Integração/Cenário: Valida o funcionamento conjunto de todos os componentes e dublês
        Paciente p = criarPacientePadrao();
        PlanoSaude planoStub = criarPlanoPadrao(0.70);
        when(autorizadorMock.autorizar(p)).thenReturn(true);

        double res = servico.calcular(200.0, planoStub, p);

        assertEquals(140.0, res, 0.01);
        assertEquals(1, historicoFake.totalConsultas());
        assertTrue(auditoriaSpy.chamado());
    }
}