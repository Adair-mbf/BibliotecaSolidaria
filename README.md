# Biblioteca Solidária - Backend MVC (Sem Frontend)

Sistema de gestão de biblioteca solidária em Java sem interface gráfica, estruturado de acordo com o diagrama `biblioteca_solidaria.drawio`.

## Padrões Arquiteturais e GoF Implementados:
- **MVC (Backend Puro)**: Model (Entidades e Repositórios), Service/Facade e Controllers REST (sem Views).
- **Factory Method**: Criação polimórfica de `Doador`, `UsuarioSite` e `Funcionario` com `FabricaRegistry`.
- **Strategy**: 
  - Cálculo de Multa (`MultaFixaDiaria`, `MultaProgressiva`).
  - Política de Renovação (`LimiteFixoRenovacao`, `SemRenovacao`).
  - Bonificação de Doações (`BonificacaoPorQuantidade`, `BonificacaoPorValor`, `BonificacaoEmpresarial`).
- **Observer**: `PublicadorEventos` notificando observadores via `CanalEmail` e `CanalPainelInterno`.
- **Adapter**: Desacoplamento para `GatewayPagamento` e `AcervoGlobalProvider`.
- **Singleton**: `ConexaoBanco` e `ConfiguracaoSistema` thread-safe.
- **Facade**: `EmprestimoFacade` e `DoacaoFacade`.

## Como executar os testes JUnit 5:
```bash
mvn clean test
```
