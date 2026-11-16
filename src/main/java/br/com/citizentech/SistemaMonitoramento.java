package br.com.citizentech;


import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import br.com.citizentech.dominio.*;
import br.com.citizentech.excecao.*;
import br.com.citizentech.persistencia.*;
import br.com.citizentech.servico.*;

public class SistemaMonitoramento {
    public static void main(String[] args) {
        try { executar(); }
        catch (Exception e) { System.err.println("Nao foi possivel iniciar: " + e.getMessage()); System.exit(1); }
    }
    private static void executar() throws Exception {
        Files.createDirectories(Path.of("dados"));
        Banco banco = new Banco("jdbc:h2:file:./dados/territorio");
        banco.inicializar();
        MedicaoDAO medicoes = new MedicaoDAO(banco);
        AlertaDAO alertas = new AlertaDAO(banco);
        DadosTerritorio dados = new DadosTerritorio("Bairro demonstrativo", 1200, "2468");
        EstacaoTerritorial estacao = new EstacaoTerritorial(dados.getRegiao(), List.of(new SensorChuva(), new SensorNivelRio(), new SensorSaturacaoSolo()));
        ServicoMonitoramento servico = new ServicoMonitoramento(medicoes, alertas, dados);
        FabricaPrevisao fabrica = new FabricaPrevisao();
        System.out.println("TERRITORIO ALERTA | Dados SIMULADOS | Apoio a decisao humana");
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n1 Verificar sensores e coletar | 2 Rodar previsoes | 3 Historico | 4 Gerenciar territorio | 5 Status completo | 6 Alertas | 7 Exportar CSV | 8 Coletar 10 rodadas demonstrativas | 0 Sair");
                if (!sc.hasNextLine()) return;
                String opcao = sc.nextLine().trim();
                try {
                    switch (opcao) {
                        case "1" -> coletar(estacao, servico, 1);
                        case "8" -> { coletar(estacao, servico, 10); System.out.println("Rodadas sinteticas, sem representar passagem real de horas."); }
                        case "2" -> {
                            System.out.println("1 Media movel (5 leituras) | 2 Comparacao (10 leituras)");
                            EstrategiaPrevisao estrategia = fabrica.criar(sc.nextLine().trim());
                            for (TipoSensor sensor : TipoSensor.values()) {
                                try {
                                    var resultado = servico.prever(sensor, estrategia);
                                    System.out.printf(Locale.ROOT, "%s: proximo bloco estimado %.2f %s. %s%n", sensor.getDescricao(), resultado.valor(), sensor.getUnidade(),
                                        resultado.alerta().map(a -> a.nivel() + " - " + a.justificativa()).orElse("Abaixo dos limiares; nao significa ausencia de risco."));
                                } catch (FonteIndisponivelException e) { System.out.println(sensor.getDescricao() + ": " + e.getMessage()); }
                            }
                        }
                        case "3" -> { TipoSensor tipo = escolherSensor(sc); List<Medicao> historico = medicoes.buscarHistoricoPorSensor(tipo); if (historico.isEmpty()) System.out.println("Sem medicoes."); historico.forEach(System.out::println); }
                        case "4" -> {
                            System.out.println("1 Alterar populacao | 2 Alterar limiares (codigo demonstrativo: 2468)");
                            String acao = sc.nextLine().trim();
                            if (acao.equals("1")) { System.out.println("Populacao agregada:"); dados.setPopulacao(Integer.parseInt(sc.nextLine().trim())); }
                            else if (acao.equals("2")) {
                                TipoSensor tipo = escolherSensor(sc);
                                System.out.println("Codigo de acesso:"); String codigo = sc.nextLine();
                                System.out.println("Atencao:"); double a = Double.parseDouble(sc.nextLine());
                                System.out.println("Alerta:"); double b = Double.parseDouble(sc.nextLine());
                                System.out.println("Critico:"); double c = Double.parseDouble(sc.nextLine());
                                dados.alterarLimiares(tipo, new Limiares(a,b,c), codigo);
                            } else throw new DadosInvalidosException("Opcao de gerenciamento invalida.");
                            System.out.println("Alteracao aplicada nesta sessao. Medicoes e alertas continuam persistidos.");
                        }
                        case "5" -> {
                            estacao.verificarStatus();
                            System.out.println(estacao.getId() + " | " + estacao.getNome() + " | " + estacao.getRegiao() + " | " + estacao.getStatus());
                            System.out.println("Populacao agregada: " + dados.getPopulacao());
                            for (TipoSensor tipo : TipoSensor.values()) System.out.println(tipo + " | " + dados.getLimiares(tipo) + " | Leituras: " + medicoes.buscarHistoricoPorSensor(tipo).size());
                            System.out.println("Alertas persistidos: " + alertas.listar().size() + ". Previsoes didaticas, sem validacao meteorologica.");
                        }
                        case "6" -> { List<Alerta> lista = alertas.listar(); if (lista.isEmpty()) System.out.println("Sem alertas emitidos."); lista.forEach(System.out::println); }
                        case "7" -> exportar(medicoes.buscarHistoricoPorSensor(escolherSensor(sc)));
                        case "0" -> { System.out.println("Encerrado. Historico salvo no banco local."); return; }
                        default -> System.out.println("Opcao invalida. Use 0 a 8.");
                    }
                } catch (DadosInvalidosException | FonteIndisponivelException e) { System.out.println("Atencao: " + e.getMessage()); }
                catch (NumberFormatException e) { System.out.println("Digite um numero valido; use ponto para decimais."); }
                catch (IllegalStateException | IOException e) { System.out.println("Operacao nao concluida: " + e.getMessage()); }
                catch (NoSuchElementException e) { System.out.println("Entrada encerrada. Historico preservado no banco local."); return; }
            }
        }
    }
    private static TipoSensor escolherSensor(Scanner sc) {
        System.out.println("1 Chuva | 2 Nivel do rio | 3 Saturacao do solo");
        int indice = Integer.parseInt(sc.nextLine().trim()) - 1;
        if (indice < 0 || indice >= TipoSensor.values().length) throw new DadosInvalidosException("Sensor invalido.");
        return TipoSensor.values()[indice];
    }
    private static void coletar(EstacaoTerritorial estacao, ServicoMonitoramento servico, int rodadas) {
        estacao.verificarStatus();
        for (int i = 0; i < rodadas; i++) for (Sensor sensor : estacao.getSensores()) {
            try { System.out.println(servico.coletar(sensor)); }
            catch (FonteIndisponivelException | DadosInvalidosException e) { System.out.println("Leitura rejeitada: " + e.getMessage()); }
        }
    }
    private static void exportar(List<Medicao> historico) throws IOException {
        List<String> linhas = new ArrayList<>(); linhas.add("sensor,valor,instante");
        for (Medicao m : historico) linhas.add(m.sensor().name() + "," + m.valor() + "," + m.instante());
        Path arquivo = Path.of("dados/historico.csv");
        Files.write(arquivo, linhas, java.nio.charset.StandardCharsets.UTF_8);
        System.out.println("CSV gerado: " + arquivo.toAbsolutePath());
    }
}
