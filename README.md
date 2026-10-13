# Biblioteca Solidária - DAOs

Camada de persistência Java 17 usando JDBC e PostgreSQL, estruturada a partir das entidades do diagrama `biblioteca_solidaria.drawio`.

## Configuração

Defina as variáveis de ambiente `DB_URL`, `DB_USER` e `DB_PASSWORD`. Exemplo de URL:

```text
jdbc:postgresql://localhost:5432/biblioteca_solidaria
```

Execute [`src/main/resources/db/schema.sql`](src/main/resources/db/schema.sql) no banco antes de usar os DAOs. As conexões são abertas por operação; não compartilhe uma mesma `Connection` entre threads.

## DAOs disponíveis

`UsuarioDao`, `DoacaoDao`, `LivroDao`, `ItemAcervoDao`, `EmprestimoDao`, `MultaDao`, `ReservaDao`, `BonificacaoDao` e `NotificacaoDao` implementam `Dao<T>` com `findById`, `findAll`, `save`, `update` e `deleteById`. Os DAOs aceitam um `SqlConnectionProvider`, o que permite substituir a fábrica por um provedor de conexões de teste.

O modelo usa `Domain` para agrupar os registros e enums do diagrama. A hierarquia de usuário, doação e item de acervo é persistida em tabelas únicas com campos de subtipo opcionais e uma coluna discriminadora (`tipo_usuario`, `tipo`).

Para obter a fábrica configurada pelo ambiente:

```java
var connections = ConnectionFactory.fromEnvironment();
var usuarios = new UsuarioDao(connections);
```

Os DAOs lançam `SQLException` ao ocorrer falha de persistência; o tratamento é responsabilidade da camada chamadora.
