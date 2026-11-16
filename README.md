# Território Alerta

Projeto da Global Solution 2026.2 de Object Oriented Programming. O sistema acompanha medições simuladas de chuva, nível do rio e saturação do solo, consulta o histórico e gera previsões e alertas.

O público beneficiado são gestores locais e equipes comunitárias que precisam observar pontos vulneráveis a alagamentos e priorizar verificações. O programa apoia essa decisão, sem acionar serviços ou tomar decisões sobre moradores.

## Integrantes

| Nome | RM |
| --- | --- |
| Lucas Kenzo Nishiwaki | 561325 |
| Felipe Hui Hattori | 565169 |
| Kauany Ribeiro de Moura | 564576 |

## Tecnologias

Java 17, Maven, JDBC, H2 em arquivo, JUnit 5 e Mockito. A interface é um menu no console; não há necessidade de servidor de banco ou API REST.

## Como executar

Com JDK 17 ou superior e Maven 3.9 ou superior instalados, executar na pasta do projeto:

```sh
mvn clean package
java -jar target/territorio-alerta-1.0.0.jar
```

A primeira compilação precisa de internet para baixar as dependências. O pacote ZIP também inclui `territorio-alerta.jar` já compilado, que pode ser iniciado com `java -jar territorio-alerta.jar`.

Para executar apenas os testes:

```sh
mvn test
```

O banco é criado automaticamente em `dados/territorio.mv.db`. A URL JDBC é `jdbc:h2:file:./dados/territorio`, com usuário local `sa` e senha vazia. Executar novamente a partir da mesma pasta permite reencontrar as medições e os alertas. Esse banco é usado somente na demonstração local.

## Funcionalidades

- Verificar os sensores simulados e gravar medições válidas.
- Rodar previsões por média móvel ou comparação de dois períodos.
- Consultar o histórico por sensor e os alertas persistidos.
- Alterar população e limiares do território, com validação.
- Exibir o estado completo da estação.
- Exportar o histórico consultado em CSV.

A opção `8` coleta dez rodadas demonstrativas para cada sensor. Depois, a opção `2` permite escolher uma estratégia de previsão. As opções `3` e `6` mostram medições e alertas; `7` exporta o CSV para `dados/historico.csv`; `0` encerra.

O código demonstrativo para alterar limiares é `2468`. Valores negativos, fora da faixa ou limiares fora de ordem são rejeitados com uma mensagem no menu. Configurações de população e limiares duram a sessão; medições e alertas ficam no banco.

## Organização do código

| Pacote | Responsabilidade |
| --- | --- |
| `dominio` | Componentes, sensores, medições, alertas, dados do território e estratégias |
| `excecao` | Erros de dados inválidos e fonte indisponível |
| `persistencia` | Inicialização do banco e DAOs de medições e alertas |
| `servico` | Monitoramento, emissão de alertas e criação das estratégias |

`ComponenteTerritorial` é a classe abstrata de `EstacaoTerritorial`. A interface `Sensor` é implementada pelos sensores de chuva, nível do rio e saturação do solo. `DadosTerritorio` protege os dados e os limiares por atributos privados e métodos de validação.

`EstrategiaPrevisao` define o contrato de cálculo. `PrevisaoMediaMovel` usa a média das últimas cinco leituras. `PrevisaoComparacao` usa dois blocos de cinco: média atual + (média atual - média anterior), respeitando os limites do sensor. As subclasses usam `super()` para informar nome e mínimo de leituras.

## Decisões técnicas

Foi escolhido JDBC com H2 em arquivo para mostrar as consultas ao banco e manter a execução simples. Os DAOs usam `PreparedStatement`, com valores separados do SQL, e fecham os recursos por `try-with-resources`. O histórico é consultado em ordem cronológica.

O padrão criacional escolhido foi Factory. `FabricaPrevisao` concentra a criação das estratégias; o menu recebe uma `EstrategiaPrevisao` e chama `prever()` sem depender da implementação concreta. Isso evita espalhar a escolha da estratégia pela aplicação.

`ServicoMonitoramento` compara o valor previsto com os limiares de atenção, alerta e crítico. Cada alerta informa sensor, valor previsto, unidade, limite e motivo, e é salvo no banco. Rodar novamente a previsão pode registrar outro alerta: o histórico guarda as execuções realizadas.

## Testes

A base contém 21 testes: nove de domínio, onze de serviço e limiares com Mockito e um de persistência em arquivo. Há testes de cálculos conhecidos, histórico mínimo, valores inválidos, código de acesso e níveis de alerta nos limites exatos. O teste de persistência fecha e reabre o banco para consultar medições e alertas.

Os testes seguem Arrange, Act e Assert. `assertThrows` verifica as exceções customizadas. Os mocks dos DAOs permitem testar a lógica de alerta sem abrir um banco.

## Reflexão crítica

O sistema deve se calar quando não dispõe de histórico suficiente. A média móvel precisa de cinco leituras; a comparação precisa de dez, formando dois blocos de cinco. Antes disso, a estratégia lança uma exceção e o menu explica que nenhuma previsão foi emitida. Esses números são uma regra didática, não prova de confiabilidade estatística. Mesmo com dez registros, leituras coletadas em poucos segundos não representam dias de observação. As rodadas demonstrativas mostram o funcionamento do software, mas não validam uma previsão ambiental.

Os dados deixam de representar vários aspectos do território. Um ponto de medição não descreve toda a região; relevo, impermeabilização, drenagem e manutenção dos sensores podem mudar a interpretação. O nível do rio é relativo a uma régua simulada, e não a uma cota oficial de inundação. Os limiares iniciais são exemplos acadêmicos e precisariam de calibração com profissionais e séries históricas locais. O programa não verifica automaticamente a idade do histórico, a regularidade dos intervalos ou falhas de calibração. Por isso, a previsão não tem horizonte em horas ou dias validado e não deve ser apresentada como aviso meteorológico real.

Uma ausência de alerta significa apenas que o cálculo não atingiu o limite configurado; não significa que o bairro está seguro. Um alerta precisa mostrar o motivo e permitir que uma pessoa confira as leituras e compare a situação com fontes oficiais e observação local. A equipe responsável conhece fatores sociais e ambientais que não estão no programa. O sistema organiza evidências e ajuda a priorizar investigação, enquanto decisões com consequências importantes continuam humanas. Não há acionamento automático de serviços, restrição de acesso ou identificação de moradores.

## Uso de IA

Foi utilizada IA para gerar a versão inicial do código, dos testes e da documentação. A validação técnica incluiu compilação com Maven, execução dos testes automatizados e conferência de persistência após reabrir o programa. Essa validação técnica foi executada com auxílio do assistente.
