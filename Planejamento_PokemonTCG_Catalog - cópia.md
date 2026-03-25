# Ciclo de Desenvolvimento — Pokémon TCG Catalog
**Versão:** 1.0  
**Data:** 2026  
**Metodologia:** SDLC Linear (Waterfall simplificado)  
**Repositório:** GitHub + GitHub Actions

---

## Fase 1 — Planejamento

**Objetivo:** Definir escopo, objetivos e viabilidade do projeto.  
**Entregável:** Plano inicial do projeto ✅

### Objetivo do projeto

Aplicação desktop multiplataforma em Java para catalogar cards do Pokémon TCG, permitindo ao usuário consultar cards via API REST e gerenciar um catálogo pessoal salvo em banco de dados local.

### Escopo da versão 1.0

| Incluído | Excluído |
|---|---|
| Busca de cards via API TCGdex | Compartilhamento de coleções |
| CRUD de cards no catálogo pessoal | Sincronização em tempo real |
| Exportação em JSON e CSV | Acesso via navegador ou mobile |
| Interface desktop (JavaFX) | Sistema de login/autenticação |
| Banco de dados local (SQLite) | Notificações push |
| Visualização de imagens dos cards | Filtros avançados por tipo/raridade (v1.1) |

### Restrições e premissas

- O usuário coleciona cards em **português (PT-BR)**
- A aplicação deve rodar em **Windows, macOS e Linux**
- Não há orçamento — todas as ferramentas e serviços devem ser **gratuitos**
- O projeto é de **aprendizado em Java** — a complexidade deve ser progressiva

---

## Fase 2 — Análise

**Objetivo:** Detalhar todos os requisitos do sistema.  
**Entregável:** Documentação de requisitos completa ✅

### API selecionada

**TCGdex** — https://tcgdex.dev/pt-br

| Critério | Resultado |
|---|---|
| Cards em português | ✅ Suporte nativo a PT-BR |
| Autenticação | ✅ Não requer API Key |
| SDK Java oficial | ✅ Disponível via JitPack |
| Documentação em PT-BR | ✅ Disponível |
| Gratuita | ✅ Sem custos |

### Requisitos funcionais

#### RF-01 — Buscar cards
- RF-01.1 Buscar card por ID
- RF-01.2 Buscar cards por nome
- RF-01.3 Buscar cards por série

#### RF-02 — Gerenciar catálogo
- RF-02.1 Adicionar card ao catálogo (com quantidade)
- RF-02.2 Editar quantidade e observações de um card
- RF-02.3 Remover card do catálogo

#### RF-03 — Exportar catálogo
- RF-03.1 Exportar catálogo completo em JSON
- RF-03.2 Exportar catálogo completo em CSV

#### RF-04 — Visualização de cards
- RF-04.1 Exibir thumbnail da imagem do card nos resultados de busca
- RF-04.2 Exibir imagem do card na tela de detalhes

### Requisitos não funcionais

#### RNF-01 — Cache da API
- Respostas de busca cacheadas em memória durante a sessão
- Estratégia: HashMap com chave = parâmetros da requisição
- Objetivo: reduzir chamadas repetidas à API TCGdex

#### RNF-02 — Suporte a idiomas
- Versão 1.0: Português do Brasil (PT-BR) — interface e dados dos cards
- Estrutura preparada para internacionalização via `ResourceBundle`

#### RNF-03 — Multiplataforma
- Empacotamento via `jpackage`
- Gera instaladores: Windows (.msi), macOS (.dmg), Linux (.deb)

#### RNF-04 — Performance
- Tempo de resposta da busca na API: < 3 segundos
- Tempo de carregamento do catálogo local: < 1 segundo

#### RNF-05 — Tratamento de erros
- Exceções customizadas para cada camada (API, banco, exportação)
- Mensagens de erro amigáveis exibidas ao usuário via Alerts do JavaFX
- Log de erros técnicos no console para depuração

#### RNF-06 — Backup automático
- Cópia automática do arquivo `.db` ao iniciar a aplicação
- Mantém os últimos 3 backups em pasta dedicada

---

## Fase 3 — Projeto

**Objetivo:** Definir a arquitetura técnica completa do sistema.  
**Entregável:** Documento de Projeto de Software (SDD) ✅

### Stack tecnológica

| Camada | Tecnologia | Versão |
|---|---|---|
| Linguagem | Java | 21 (LTS) |
| Interface Gráfica | JavaFX | 21 |
| Gerenciador de Dependências | Maven | 3.9+ |
| Banco de Dados | SQLite via sqlite-jdbc | 3.45+ |
| SDK da API | TCGdex Java SDK (JitPack) | 2.0.3 |
| Serialização JSON | Jackson | 2.17+ |
| Exportação CSV | OpenCSV | 5.9 |
| Testes unitários | JUnit 5 + Mockito | 5.x / 5.x |
| CI/CD | GitHub Actions | — |
| Distribuição | GitHub Releases | — |

> **Nota:** O SDK TCGdex é distribuído via **JitPack**, não pelo Maven Central. O repositório JitPack deve ser adicionado ao `pom.xml`.

### Arquitetura em camadas (MVC)

```
┌─────────────────────────────────────────────┐
│                  VIEW (JavaFX)               │
│            Telas FXML (.fxml)                │
└─────────────────┬───────────────────────────┘
                  │ eventos
┌─────────────────▼───────────────────────────┐
│             CONTROLLER (JavaFX)              │
│     Classes controller para cada tela        │
└─────────────────┬───────────────────────────┘
                  │ chama
┌─────────────────▼───────────────────────────┐
│              SERVICE (Lógica de Negócio)     │
│      Regras da aplicação e orquestração      │
└──────────┬──────────────┬───────────────────┘
           │ chama        │ chama
┌──────────▼──────────┐ ┌─▼───────────────────┐
│  REPOSITORY (BD)    │ │   API CLIENT        │
│  SQLite via JDBC    │ │   TCGdex SDK Java   │
└──────────┬──────────┘ └──────────┬──────────┘
           │                       │
┌──────────▼──────────┐ ┌──────────▼──────────┐
│  Banco de Dados     │ │   API TCGdex        │
│  SQLite local       │ │   (PT-BR)           │
└─────────────────────┘ └─────────────────────┘
```

### Estrutura de pacotes

```
pokemon-tcg-catalog/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/pokemontcg/
    │   │       ├── Main.java
    │   │       ├── model/
    │   │       │   ├── Card.java
    │   │       │   └── CatalogEntry.java
    │   │       ├── controller/
    │   │       │   ├── MainController.java
    │   │       │   ├── SearchController.java
    │   │       │   ├── CatalogController.java
    │   │       │   └── ExportController.java
    │   │       ├── service/
    │   │       │   ├── CardService.java
    │   │       │   ├── CatalogService.java
    │   │       │   └── ExportService.java
    │   │       ├── repository/
    │   │       │   ├── CatalogRepository.java
    │   │       │   └── DatabaseManager.java
    │   │       ├── api/
    │   │       │   ├── TcgDexClient.java
    │   │       │   └── CacheManager.java
    │   │       ├── export/
    │   │       │   ├── JsonExporter.java
    │   │       │   └── CsvExporter.java
    │   │       └── exception/
    │   │           ├── ApiException.java
    │   │           ├── DatabaseException.java
    │   │           └── ExportException.java
    │   └── resources/
    │       ├── fxml/
    │       │   ├── main.fxml
    │       │   ├── search.fxml
    │       │   ├── catalog.fxml
    │       │   └── export.fxml
    │       ├── css/
    │       │   └── styles.css
    │       └── db/
    │           └── schema.sql
    └── test/
        └── java/
            └── com/pokemontcg/
                ├── service/
                │   ├── CardServiceTest.java
                │   ├── CatalogServiceTest.java
                │   └── ExportServiceTest.java
                ├── repository/
                │   └── CatalogRepositoryTest.java
                ├── api/
                │   ├── TcgDexClientTest.java
                │   └── CacheManagerTest.java
                └── export/
                    ├── JsonExporterTest.java
                    └── CsvExporterTest.java
```

### Modelo de dados — `Card.java`

```java
public class Card {
    private String id;          // ID único na API (ex: "swsh1-1")
    private String localId;     // ID local do card no set
    private String name;        // Nome do Pokémon/card
    private String image;       // URL da imagem do card
    private String category;    // Pokémon, Treinador, Energia
    private String rarity;      // Raridade (Comum, Incomum, Raro, etc.)
    private String setId;       // ID do set/expansion
    private String setName;     // Nome do set (ex: "Espada e Escudo")
    private String seriesId;    // ID da série
    private String seriesName;  // Nome da série
    private Integer hp;         // Pontos de vida (se Pokémon)
    private List<String> types; // Tipos (Fogo, Água, etc.)
}
```

### Modelo de dados — `CatalogEntry.java`

```java
public class CatalogEntry {
    private int id;              // PK auto-incremento
    private String cardId;       // ID do card na API
    private String cardName;     // Nome para exibição
    private String seriesId;     // Referência à série
    private String seriesName;   // Nome da série
    private String imageUrl;     // URL da imagem do card
    private int quantity;        // Quantidade na coleção
    private String language;     // Idioma (default: "pt")
    private String notes;        // Observações do usuário
    private LocalDateTime addedAt;    // Data de adição
    private LocalDateTime updatedAt;  // Data de atualização
}
```

### Modelo do banco de dados

```sql
CREATE TABLE IF NOT EXISTS catalog (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    card_id       TEXT    NOT NULL,
    card_name     TEXT    NOT NULL,
    series_id     TEXT    NOT NULL,
    series_name   TEXT    NOT NULL,
    image_url     TEXT,
    quantity      INTEGER NOT NULL DEFAULT 1,
    language      TEXT    NOT NULL DEFAULT 'pt',
    notes         TEXT,
    added_at      TEXT    NOT NULL,
    updated_at    TEXT    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_card_id   ON catalog(card_id);
CREATE INDEX IF NOT EXISTS idx_series_id ON catalog(series_id);
```

### Classes de exceção customizadas

```java
// Exceção para erros de comunicação com a API TCGdex
public class ApiException extends RuntimeException {
    public ApiException(String message) { super(message); }
    public ApiException(String message, Throwable cause) { super(message, cause); }
}

// Exceção para erros de operação no banco SQLite
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) { super(message); }
    public DatabaseException(String message, Throwable cause) { super(message, cause); }
}

// Exceção para erros na exportação de arquivos
public class ExportException extends RuntimeException {
    public ExportException(String message) { super(message); }
    public ExportException(String message, Throwable cause) { super(message, cause); }
}
```

---

## Fase 4 — Codificação

**Objetivo:** Implementar o código do sistema em ordem progressiva.  
**Entregável:** Protótipo funcional

### Ordem de implementação recomendada

A ordem abaixo foi escolhida para que cada etapa já seja testável ao final dela, evitando acúmulo de bugs.

#### Etapa 4.1 — Setup do projeto
- [ ] Criar projeto Maven no VS Code ou IntelliJ IDEA
- [ ] Configurar `pom.xml` com todas as dependências (incluindo repositório JitPack)
- [ ] Criar estrutura de pacotes (model, controller, service, repository, api, export, exception)
- [ ] Configurar JavaFX no Maven (`javafx-maven-plugin`)
- [ ] Criar `Main.java` que abre uma janela vazia
- [ ] Criar as classes de exceção customizadas (`ApiException`, `DatabaseException`, `ExportException`)

#### Etapa 4.2 — Banco de dados
- [ ] Criar `schema.sql` com a tabela `catalog` (incluindo coluna `image_url`)
- [ ] Criar classe `DatabaseManager.java` que inicializa o banco SQLite e gerencia backup automático
- [ ] Implementar lógica de backup automático (copia `.db` ao iniciar, mantém 3 últimos backups)
- [ ] Implementar `CatalogRepository.java` com os métodos:
  - `save(CatalogEntry entry)` — lança `DatabaseException` em caso de falha
  - `update(CatalogEntry entry)` — lança `DatabaseException` em caso de falha
  - `delete(String cardId)` — lança `DatabaseException` em caso de falha
  - `findAll()` — retorna `List<CatalogEntry>`
  - `findById(String cardId)` — retorna `Optional<CatalogEntry>`
  - `existsById(String cardId)` — retorna `boolean`

#### Etapa 4.3 — Integração com a API
- [ ] Implementar `TcgDexClient.java` usando o SDK TCGdex (v2.0.3 via JitPack):
  - `searchByName(String name)` — lança `ApiException` em caso de falha
  - `searchBySeries(String seriesId)` — lança `ApiException` em caso de falha
  - `findById(String cardId)` — lança `ApiException` em caso de falha
- [ ] Implementar `CacheManager.java` com HashMap em memória
- [ ] Testar chamadas à API manualmente

#### Etapa 4.4 — Camada de serviços
- [ ] Implementar `CardService.java`:
  - `search(String query, SearchType type)` — usa cache + API
- [ ] Implementar `CatalogService.java`:
  - `addCard(Card card, int quantity)` — valida duplicatas e quantidade
  - `updateCard(String cardId, int quantity, String notes)` — valida existência
  - `removeCard(String cardId)` — valida existência
  - `getAllCards()` — retorna lista do repositório
- [ ] Implementar `ExportService.java`:
  - `exportToJson(List<CatalogEntry> entries, Path destination)` — orquestra `JsonExporter`
  - `exportToCsv(List<CatalogEntry> entries, Path destination)` — orquestra `CsvExporter`

#### Etapa 4.5 — Interface gráfica
- [ ] Criar layout `main.fxml` com sidebar de navegação
- [ ] Criar `search.fxml` — grid de resultados com thumbnails dos cards e botão "Adicionar"
- [ ] Criar `catalog.fxml` — tabela de cards com ações de editar/remover
- [ ] Criar `export.fxml` — opções de formato (JSON/CSV) e seletor de pasta
- [ ] Implementar `MainController.java` — navegação entre telas
- [ ] Implementar `SearchController.java` — busca e exibição de resultados com imagens
- [ ] Implementar `CatalogController.java` — CRUD do catálogo
- [ ] Implementar `ExportController.java` — seleção de formato e destino
- [ ] Implementar tratamento de erros na UI (Alerts do JavaFX para exceções customizadas)
- [ ] Aplicar estilos CSS (`styles.css`)

#### Etapa 4.6 — Exportação
- [ ] Implementar `JsonExporter.java` com Jackson — lança `ExportException` em caso de falha
- [ ] Implementar `CsvExporter.java` com OpenCSV — lança `ExportException` em caso de falha

---

## Fase 5 — Testes

**Objetivo:** Garantir qualidade e confiabilidade do código.  
**Entregável:** Software refinado, relatório de cobertura de testes

### Estratégia de testes

```
                  [ Testes de Sistema ]
                  Fluxo completo manual
                          ▲
              [ Testes de Integração ]
              Repository + SQLite real
              API TCGdex real (@Tag)
                          ▲
              [  Testes Unitários   ]   ← Foco principal
              Service + API + Export
```

### Dependências de teste (pom.xml)

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>

<!-- Mockito para simular dependências -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

### Testes unitários por classe

#### CacheManagerTest.java
```java
// Cenários a cobrir:
- deveRetornarNullQuandoCacheVazio()
- deveArmazenarERecuperarValor()
- deveSubstituirValorExistenteParaMesmaChave()
- deveLimparCacheCorretamente()
```

#### TcgDexClientTest.java
```java
// Cenários a cobrir (API mockada com Mockito):
- deveBuscarCardPorNomeComSucesso()
- deveRetornarListaVaziaQuandoNomeSemResultados()
- deveBuscarCardPorIdComSucesso()
- deveLancarApiExceptionQuandoApiIndisponivel()
- deveBuscarCardsPorSerieComSucesso()
```

#### CardServiceTest.java
```java
// Cenários a cobrir (TcgDexClient e CacheManager mockados):
- deveBuscarNaApiQuandoCacheMiss()
- deveRetornarDoCacheQuandoCacheHit()
- deveSalvarResultadoNoCacheAposBuscarNaApi()
- deveRetornarListaVaziaQuandoBuscaSemResultado()
- deveLancarExcecaoQuandoQueryNula()
```

#### CatalogServiceTest.java
```java
// Cenários a cobrir (CatalogRepository mockado):
- deveAdicionarCardComSucesso()
- deveLancarExcecaoAoAdicionarCardJaExistente()
- deveAtualizarQuantidadeComSucesso()
- deveLancarExcecaoAoAtualizarCardInexistente()
- deveRemoverCardComSucesso()
- deveLancarExcecaoAoRemoverCardInexistente()
- deveListarTodosOsCardsDoRepositorio()
- deveLancarExcecaoSeQuantidadeForZeroOuNegativa()
```

#### ExportServiceTest.java
```java
// Cenários a cobrir (JsonExporter e CsvExporter mockados):
- deveExportarParaJsonComSucesso()
- deveExportarParaCsvComSucesso()
- deveLancarExportExceptionQuandoDestinoInvalido()
- devePassarListaCorretaParaOExportador()
```

#### CatalogRepositoryTest.java
```java
// Cenários a cobrir (banco SQLite em memória para testes):
// Usar SQLite em memória: "jdbc:sqlite::memory:"
- deveSalvarCardERecuperarPorId()
- deveAtualizarQuantidadeDeCardExistente()
- deveRemoverCardExistente()
- deveRetornarFalseParaCardInexistente()
- deveListarTodosOsCardsCorretamente()
- deveRetornarListaVaziaQuandoCatalogoVazio()
- deveGerarIdAutomaticamenteAoSalvar()
- deveSalvarImageUrlCorretamente()
```

#### JsonExporterTest.java
```java
// Cenários a cobrir:
- deveExportarListaDeCardsParaJsonValido()
- deveGerarJsonComTodosOsCampos()
- deveExportarListaVaziaComoArrayVazio()
- deveLancarExportExceptionSeCaminhoInvalido()
```

#### CsvExporterTest.java
```java
// Cenários a cobrir:
- deveExportarListaDeCardsParaCsvComCabecalho()
- deveUsarVirgulaComoSeparador()
- deveExportarListaVaziaApenasComCabecalho()
- deveEscaparCamposComVirgulaNoConteudo()
- deveLancarExportExceptionSeCaminhoInvalido()
```

### Testes de integração (opcionais — `@Tag("integration")`)

```java
// Executar manualmente ou com: mvn test -Dgroups="integration"
// NÃO rodam no CI para não depender de rede

@Tag("integration")
class TcgDexClientIntegrationTest {
    - deveBuscarPikachuNaApiReal()
    - deveRetornarCardComImagemUrl()
}
```

### Cobertura mínima esperada

| Pacote | Meta de cobertura |
|---|---|
| `service/` | ≥ 90% |
| `repository/` | ≥ 85% |
| `api/` | ≥ 80% |
| `export/` | ≥ 85% |
| `model/` | ≥ 70% |
| `exception/` | ≥ 50% |
| **Total** | **≥ 80%** |

### Configuração do plugin de cobertura (pom.xml)

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals><goal>report</goal></goals>
        </execution>
    </executions>
</plugin>
```

Gera o relatório em: `target/site/jacoco/index.html`

### Testes manuais (sistema)

Fluxos a validar manualmente antes de cada release:

- [ ] Buscar card por nome → resultado aparece em grid com thumbnails
- [ ] Clicar em um card → exibe imagem ampliada e detalhes
- [ ] Adicionar card ao catálogo → aparece na tela de catálogo
- [ ] Adicionar card duplicado → exibe mensagem de erro amigável
- [ ] Editar quantidade de um card → valor atualizado persiste ao reabrir app
- [ ] Remover card → some da lista e do banco
- [ ] Exportar JSON → arquivo gerado é válido
- [ ] Exportar CSV → arquivo abre corretamente no Excel/LibreOffice
- [ ] Buscar o mesmo termo duas vezes → segunda busca usa cache (sem nova chamada à API)
- [ ] Abrir app sem conexão com internet → app abre normalmente, busca exibe mensagem de erro amigável
- [ ] Verificar que backup do `.db` foi criado ao iniciar o app

---

## Fase 6 — CI/CD com GitHub Actions

**Objetivo:** Automatizar build, testes e distribuição.  
**Entregável:** Pipeline funcional no GitHub

### Workflow: build e testes (`.github/workflows/build.yml`)

```yaml
name: Build & Test

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout do código
        uses: actions/checkout@v4

      - name: Configurar Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Cache do Maven
        uses: actions/cache@v4
        with:
          path: ~/.m2
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}

      - name: Build e testes (exclui testes de integração)
        run: mvn clean verify -DexcludedGroups="integration"

      - name: Publicar relatório de cobertura
        uses: actions/upload-artifact@v4
        with:
          name: jacoco-report
          path: target/site/jacoco/
```

### Workflow: geração de release (`.github/workflows/release.yml`)

```yaml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build-jar:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Gerar JAR executável
        run: mvn clean package -DskipTests
      - name: Upload do JAR no Release
        uses: softprops/action-gh-release@v2
        with:
          files: target/pokemon-tcg-catalog-*.jar
```

### Estratégia de branches

```
main          → código estável, releases oficiais
develop       → integração de features prontas
feature/xxx   → desenvolvimento de cada funcionalidade
```

### Versionamento

Seguir **Semantic Versioning** (semver):

- `v1.0.0` — release inicial com todos os requisitos da v1.0
- `v1.1.0` — nova funcionalidade (ex: filtros avançados por tipo/raridade)
- `v1.0.1` — correção de bug

---

## Cronograma estimado

| Fase | Duração estimada | Dependência |
|---|---|---|
| 1 — Planejamento | ✅ Concluído | — |
| 2 — Análise | ✅ Concluído | Fase 1 |
| 3 — Projeto (SDD) | ✅ Concluído | Fase 2 |
| 4.1 — Setup + exceções | 1 sessão de estudo | Fase 3 |
| 4.2 — Banco de dados + backup | 2 sessões | Etapa 4.1 |
| 4.3 — Integração API | 2 sessões | Etapa 4.1 |
| 4.4 — Serviços (incluindo ExportService) | 2 sessões | Etapas 4.2 e 4.3 |
| 4.5 — Interface (com imagens dos cards) | 4 sessões | Etapa 4.4 |
| 4.6 — Exportação | 1 sessão | Etapa 4.4 |
| 5 — Testes (unitários + integração) | 3 sessões | Fase 4 completa |
| 6 — CI/CD | 1 sessão | Fase 5 |

> "Sessão de estudo" = aproximadamente 1 a 2 horas de desenvolvimento focado.

---

## Riscos identificados

| Risco | Probabilidade | Impacto | Mitigação |
|---|---|---|---|
| API TCGdex fora do ar | Baixa | Médio | Cache em memória + `ApiException` exibida como mensagem amigável |
| Card sem versão em PT-BR | Média | Baixo | Exibir aviso ao usuário |
| Corrupção do banco SQLite | Baixa | Alto | Backup automático ao iniciar o app (mantém 3 últimos) |
| Dificuldade com JavaFX | Média | Médio | Começar com layouts simples e evoluir gradualmente |
| SDK TCGdex incompatível | Baixa | Alto | Usar versão estável (2.0.3) via JitPack; testar integração cedo |
| Imagens dos cards indisponíveis | Baixa | Baixo | Exibir placeholder quando URL da imagem não carregar |

---

## Referência rápida — Configuração do `pom.xml`

### Repositório JitPack (necessário para TCGdex SDK)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Dependências principais

```xml
<!-- JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>21</version>
</dependency>

<!-- SQLite JDBC -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.3.0</version>
</dependency>

<!-- TCGdex Java SDK (via JitPack) -->
<dependency>
    <groupId>com.github.tcgdex</groupId>
    <artifactId>java-sdk</artifactId>
    <version>2.0.3</version>
</dependency>

<!-- Jackson (JSON) -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.0</version>
</dependency>

<!-- OpenCSV -->
<dependency>
    <groupId>com.opencsv</groupId>
    <artifactId>opencsv</artifactId>
    <version>5.9</version>
</dependency>
```

---

*Documento revisado incorporando ajustes de arquitetura, tratamento de erros, backup automático e correção de dependências — versão 1.0 revisada*
