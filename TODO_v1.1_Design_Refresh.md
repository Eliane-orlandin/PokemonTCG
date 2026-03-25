# TODO — v1.1 Design Refresh "The Glass Trainer's Codex"

**Tipo:** Atualização visual (Design Refresh)  
**Pré-requisito:** v1.0 concluída e funcional  
**Regra de ouro:** Se exigir mudança em `.java` fora de `view/controller/`, está fora do escopo.

---

## Etapa 4.0 — Migração inline → styleClass

> **Objetivo:** Extrair todos os `style="..."` dos FXMLs para classes CSS nomeadas em `styles.css`. O app deve ficar **visualmente idêntico** após esta etapa.

### Arquivos alterados

| Arquivo | O que fazer |
|---|---|
| `styles.css` | Criar ~80 classes CSS nomeadas cobrindo todos os inline styles |
| `main.fxml` | Trocar 4 `style=` por `styleClass=` (logo icon, 3 header icons) |
| `home.fxml` | Trocar ~25 `style=` por `styleClass=` (todos os labels, botões, containers) |
| `search.fxml` | Trocar 5 `style=` por `styleClass=` (título, subtítulo, botão, separator, status) |
| `catalog.fxml` | Trocar ~12 `style=` por `styleClass=` (títulos, botões, header tabela, action bar) |
| `export.fxml` | Trocar ~15 `style=` por `styleClass=` (títulos, ícone containers, botões, linhas da tabela) |
### Etapa 4.0 — Migração inline → styleClass ✅
**Objetivo:** Extrair todos os estilos inline (`style="..."`) para o `styles.css`.
- [x] Extrair tokens de cores/fontes para o `.root` no `styles.css`.
- [x] Migrar `main.fxml` (Sidebar e Header).
- [x] Migrar `home.fxml` (Dashboard).
- [x] Migrar `search.fxml` (Search).
- [x] Migrar `catalog.fxml` e `catalog_row.fxml` (My Collection).
- [x] Migrar `export.fxml` (Export).
- [x] Migrar `card_item.fxml` (Cards).
- [x] **Check**: Rodar o app e garantir que o layout da v1.0 está mantido após a migração.
- **Commit:** `refactor: migrar inline styles para styleClass` em todos os FXMLs`

---

## Etapa 4.1 — Fontes e Ikonli

> **Objetivo:** Baixar e empacotar as fontes e adicionar a biblioteca de ícones Ikonli.

### Arquivos alterados

| Arquivo | O que fazer |
|---|---|
| `resources/fonts/Inter-Regular.ttf` | **NOVO** — baixar de fonts.google.com |
| `resources/fonts/Inter-Medium.ttf` | **NOVO** — baixar |
| `resources/fonts/Inter-Bold.ttf` | **NOVO** — baixar |
| `resources/fonts/PlusJakartaSans-SemiBold.ttf` | **NOVO** — baixar |
| `resources/fonts/PlusJakartaSans-Bold.ttf` | **NOVO** — baixar |
| `resources/fonts/PlusJakartaSans-ExtraBold.ttf` | **NOVO** — baixar |
| `resources/fonts/PokemonHollow.ttf` | **NOVO** — baixar (verificar licença antes) |
| `Main.java` | Adicionar `Font.loadFont()` para cada fonte |
## Etapa 4.1 — Fontes e Ikonli ✅
**Objetivo:** Adicionar tipografia premium e suporte a ícones vetoriais.
- [x] Baixar Inter e Plus Jakarta Sans para `resources/fonts/`.
- [x] Adicionar `ikonli-javafx` e `ikonli-materialdesign2-pack` ao `pom.xml`.
- [x] Carregar fontes via `@font-face` no `styles.css`.
- **Commit:** `feat: adicionar fontes premium e suporte ikonli`

---

## Etapa 4.2 — Dark Theme "Navy Glass" ✅
**Objetivo:** Virar a chave para o tema escuro v1.1.
- [x] Atualizar tokens de cores para a paleta Dark Navy no `styles.css`.
- [x] Implementar classe `.glass-pane` e variáveis de glass (reflexo/borda).
- [x] **Check**: Abrir o app e ver a transformação (tipografia + cores escuras).
- **Commit:** `style: implementar dark theme navy glass`

### Arquivos alterados

| Arquivo | O que fazer |
|---|---|
| `styles.css` | Trocar valores dos tokens (cores, fontes) para o tema escuro |
| `glass.css` | **NOVO** — criar classe `.glass-panel` reutilizável |
| `Main.java` | Adicionar `glass.css` como stylesheet da cena |

### Tokens a trocar

| Token | Valor v1.0 (claro) | Valor v1.1 (escuro) |
|---|---|---|
| `-app-bg-main` | `#F5F6FA` | `#000e3d` |
| `-app-bg-sidebar` | `#FFFFFF` | `#051749` |
| `-app-bg-card` | `#FFFFFF` | `rgba(33,56,110,0.6)` |
| `-app-primary` | `#C2185B` | `#ffcb05` |
| `-app-text-dark` | `#263238` | `#dce1ff` |
| `-app-text-muted` | `#78909C` | `#d2c5ab` |
| `-app-border-color` | `#ECEFF1` | `rgba(255,255,255,0.15)` |
| Tipografia | Segoe UI / Roboto | Inter / Plus Jakarta Sans |

### Checklist
- [ ] Trocar todos os tokens de cor em `.root`
- [ ] Atualizar família de fonte para Inter
- [ ] Atualizar classes de título para Plus Jakarta Sans
- [ ] Criar `glass.css` com `.glass-panel`
- [ ] Adicionar `glass.css` no `Main.java`
- [ ] Verificar visualmente
- [ ] Commit: `feat: criar design system dark The Glass Trainer's Codex`

---

### Etapa 4.3 — Sidebar, Home & Search (Gold & Ikonli) ✅
**Objetivo:** Substituir emojis e aplicar a paleta final das fotos.
- [x] Substituir emojis por `FontIcon` (Ikonli) em `main.fxml`, `home.fxml` e `search.fxml`.
- [x] Aplicar branding em **Pokemon Hollow** no Logo lateral.
- [x] Garantir que o Dashboard use os cards translúcidos (`glass-pane`).
- **Commit:** `style: refinar interface com gold theme e ikonli`

### Arquivos alterados

| Arquivo | O que fazer |
|---|---|
| `main.fxml` | Trocar texto do logo para fonte Pokemon Hollow, trocar emojis por ícones Ikonli, ajustar item ativo (amarelo) |
| `home.fxml` | Aplicar `.glass-panel` nos pulse cards, ajustar cores dos badges e ícones, tema escuro no banner |

### Checklist
- [ ] `main.fxml` — logo com fonte Pokemon Hollow
- [ ] `main.fxml` — trocar 🏠🔍🗂📑 por ícones Ikonli FontAwesome
- [ ] `main.fxml` — item ativo com destaque amarelo (`-fx-primary`)
- [ ] `home.fxml` — aplicar `.glass-panel` nos pulse cards
- [ ] `home.fxml` — atualizar cores e ícones da galeria
- [ ] `home.fxml` — atualizar banner de exportação
- [ ] Verificar visualmente sidebar + home
- [ ] Commit: `feat: aplicar tema escuro na sidebar e dashboard`

---

## Etapa 4.4 — Busca

> **Objetivo:** Aplicar tema escuro na tela de busca e nos cards de resultado.

### Arquivos alterados

| Arquivo | O que fazer |
|---|---|
| `search.fxml` | Campo de busca estilo underline, botão com gradiente amarelo, fundo escuro |
| `card_item.fxml` | Aplicar `.glass-panel`, ajustar cores do nome/ID/botão + |
| `CardItemController.java` | Atualizar cores no `updateTypeBadge()` para funcionar em fundo escuro |

### Checklist
- [ ] `search.fxml` — campo de busca com estilo underline (borda inferior)
- [ ] `search.fxml` — botão "PROCURAR" com gradiente amarelo
- [ ] `card_item.fxml` — aplicar `.glass-panel` no container
- [ ] `card_item.fxml` — ajustar text-fill para tema escuro
- [ ] `CardItemController.java` — ajustar cores dos 10 tipos para contraste em fundo escuro
- [ ] Verificar visualmente busca + cards
- [ ] Commit: `feat: aplicar tema escuro na tela de busca`

---

## Etapa 4.5 — Catálogo

> **Objetivo:** Aplicar tema escuro na tabela do catálogo.

### Arquivos alterados

| Arquivo | O que fazer |
|---|---|
| `catalog.fxml` | Fundo escuro, header com texto `-fx-secondary`, zebra-striping escuro, botões |
| `catalog_row.fxml` | Ajustar cores do nome, set, type badge, rarity, botão delete |

### Checklist
- [ ] `catalog.fxml` — fundo `-fx-surface`, barra de ações escura
- [ ] `catalog.fxml` — header da tabela com texto secondary, caixa alta
- [ ] `catalog_row.fxml` — zebra-striping com alternância de cores escuras
- [ ] `catalog_row.fxml` — cores de texto ajustadas para legibilidade
- [ ] Verificar visualmente catálogo
- [ ] Commit: `feat: aplicar tema escuro na tela de catálogo`

---

## Etapa 4.6 — Exportar

> **Objetivo:** Aplicar tema escuro na tela de exportação.

### Arquivos alterados

| Arquivo | O que fazer |
|---|---|
| `export.fxml` | Cards com `.glass-panel`, ícones Ikonli, botões com gradiente amarelo |

### Checklist
- [ ] `export.fxml` — cards de formato com `.glass-panel`
- [ ] `export.fxml` — ícones Ikonli nos cards JSON e CSV
- [ ] `export.fxml` — botões com gradiente primário amarelo
- [ ] `export.fxml` — tabela de exportações com tema escuro
- [ ] Verificar visualmente exportação
- [ ] Commit: `feat: aplicar tema escuro na tela de exportação`

---

## Etapa 4.7 — Revisão Visual + Testes

> **Objetivo:** Validar tudo e garantir que nenhuma funcionalidade foi quebrada.

### Checklist de testes unitários
- [ ] `mvn test` — todos os 8 testes passam com 0 falhas
- [ ] BUILD SUCCESS confirmado

### Checklist visual — Sidebar
- [ ] Item ativo destacado corretamente
- [ ] Fontes carregadas (Pokemon Hollow no logo, Inter no corpo)
- [ ] Ícones Ikonli renderizados

### Checklist visual — Busca
- [ ] Fundo escuro correto
- [ ] Campo de busca com foco amarelo
- [ ] Cards com efeito glass
- [ ] Badges de tipo com cores corretas sobre fundo escuro

### Checklist visual — Catálogo
- [ ] Zebra-striping escuro
- [ ] Texto legível em todas as colunas
- [ ] Botões de ação estilizados

### Checklist visual — Exportar
- [ ] Cards glass centralizados
- [ ] Botões com gradiente amarelo

### Checklist final
- [ ] Nenhuma funcionalidade da v1.0 quebrada
- [ ] App inicia sem erros de CSS/fonte
- [ ] Commit final e tag `v1.1.0`

---

## Resumo de impacto por arquivo

| Arquivo | 4.0 | 4.1 | 4.2 | 4.3 | 4.4 | 4.5 | 4.6 |
|---|---|---|---|---|---|---|---|
| `styles.css` | ✏️ | — | ✏️ | — | — | — | — |
| `glass.css` | — | — | 🆕 | — | — | — | — |
| `Main.java` | — | ✏️ | ✏️ | — | — | — | — |
| `pom.xml` | — | ✏️ | — | — | — | — | — |
| `main.fxml` | ✏️ | — | — | ✏️ | — | — | — |
| `home.fxml` | ✏️ | — | — | ✏️ | — | — | — |
| `search.fxml` | ✏️ | — | — | — | ✏️ | — | — |
| `card_item.fxml` | ✏️ | — | — | — | ✏️ | — | — |
| `catalog.fxml` | ✏️ | — | — | — | — | ✏️ | — |
| `catalog_row.fxml` | ✏️ | — | — | — | — | ✏️ | — |
| `export.fxml` | ✏️ | — | — | — | — | — | ✏️ |
| `CardItemController.java` | — | — | — | — | ✏️ | — | — |
| `resources/fonts/*` | — | 🆕 | — | — | — | — | — |

**Legenda:** ✏️ = modificado | 🆕 = criado

---

## Cronograma

| Etapa | Sessões | Commits |
|---|---|---|
| 4.0 — Migração inline→styleClass | 2 | 1 |
| 4.1 — Fontes e Ikonli | 1 | 1 |
| 4.2 — CSS tema escuro | 2 | 1 |
| 4.3 — Sidebar + Home | 2 | 1 |
| 4.4 — Busca | 2 | 1 |
| 4.5 — Catálogo | 2 | 1 |
| 4.6 — Exportar | 1 | 1 |
| 4.7 — Revisão + Testes | 1 | 1 |
| **Total** | **~14** | **8** |
