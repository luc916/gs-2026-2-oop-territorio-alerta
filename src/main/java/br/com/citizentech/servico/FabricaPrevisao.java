package br.com.citizentech.servico;


import java.util.Map;
import java.util.function.Supplier;
import br.com.citizentech.dominio.*;
import br.com.citizentech.excecao.DadosInvalidosException;

public class FabricaPrevisao {
    private final Map<String, Supplier<EstrategiaPrevisao>> criadores = Map.of(
        "1", PrevisaoMediaMovel::new, "2", PrevisaoComparacao::new);
    public EstrategiaPrevisao criar(String opcao) {
        Supplier<EstrategiaPrevisao> criador = criadores.get(opcao);
        if (criador == null) throw new DadosInvalidosException("Estrategia invalida. Use 1 ou 2.");
        return criador.get();
    }
}
