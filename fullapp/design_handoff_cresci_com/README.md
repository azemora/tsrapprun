# Handoff — cresci·com (direção visual cozy + editorial)

## Visão geral
Redesign visual completo do app **cresci·com**, um diário/livro de memórias para pais. O app permite registrar momentos do dia-a-dia (registros diários, semanais, mesversários), criar eventos com várias fotos, ler histórias com o bebê, e consolidar tudo em um livro de memórias estilo álbum.

Este pacote cobre **11 telas centrais** desenhadas numa linguagem visual nova: **cozy + livro ilustrado / editorial scrapbook**, com paleta sage/cream/amber, tipografia Fraunces (serif variável) + Caveat (manuscrita) + Inter (UI), polaroids com fita adesiva, selos circulares, etiquetas monospace, e ícones desenhados em SVG line.

## Sobre os arquivos deste pacote

Os arquivos em `source/` são **referências de design criadas em HTML/React+Babel inline** — protótipos vivos mostrando o look-and-feel pretendido. **Eles não são código de produção pra copiar direto.** A tarefa é **recriar esses designs no ambiente existente do codebase do cresci·com** (provavelmente React Native pelo nome dos arquivos `.jsx` originais), seguindo os padrões, bibliotecas e arquitetura já estabelecidos no projeto.

Tudo aqui é estilizado com inline `style` e pequenos componentes JSX. No projeto real, traduza isso pra:
- React Native: `StyleSheet.create()`, `<View>`, `<Text>`, `<Image>`, `<Pressable>`
- Expo Router / React Navigation: pra estrutura de telas
- Bibliotecas existentes do projeto pra ícones (substituir o `Icon` custom), SVG (`react-native-svg`), gradient (`expo-linear-gradient`), blur, etc.

## Fidelidade

**Hi-fi.** Cores, tipografia, espaçamento, raios, sombras e ângulos de rotação foram especificados com precisão. Recriar o mais próximo possível do look pixel-perfect, mas usando as bibliotecas e patterns do codebase real.

---

## Design tokens

### Cores (paleta cozy)

```js
const colors = {
  // verdes (sage / olive)
  sage:       "#8FA876",  // verde principal — fundos saturados
  sageMist:   "#E2EAD3",  // verde claríssimo — bandas, cards
  sageSoft:   "#EFF3E4",  // ainda mais claro
  olive:      "#54683E",  // texto secundário, ícones
  oliveDeep:  "#3F4F2E",  // texto principal sobre cream, botão primário

  // âmbar / dourados
  amber:      "#CB8B45",  // accents, badges
  amberDeep:  "#A86F32",  // texto destacado (itálico), accent forte
  gold:       "#D9B25C",  // detalhes (fitas adesivas)
  tan:        "#C8B998",

  // creams (papel)
  cream:      "#F5EFE0",  // background principal
  creamDeep:  "#EAE0C9",  // cards, inputs
  ink:        "#3E2E1E",  // texto bem escuro

  // pastéis para tags/categorias
  peach:      "#F2D6D2",
  sky:        "#C9DBE8",
  lilac:      "#E6CFE8",
  butter:     "#FCE4A7",  // accent quente, mensagens de mesversário
  mint:       "#C8E0D7",

  brick:      "#B85450",  // erros, alertas
  white:      "#FFFFFF",
};
```

**Regras de uso:**
- **Tela de impacto/celebração** (Login, FrontPage, Mesversário, Câmera de Evento, Nomear Evento) → fundo `sage` saturado, texto `cream`, accents `butter`/`amberDeep`
- **Tela de leitura/listas** (Home, Calendário, Listas, Memory Book, Stories) → fundo `cream`, texto `oliveDeep`, header sage opcional
- **Botão primário** sempre `oliveDeep` sólido com texto `cream`. Em fundo sage, ele fica tom-sobre-tom; em fundo cream, contraste alto.
- **Itálico colorido**: usado pra destacar emocionalmente ("acolhedoras", "seu pequeno", "sete meses", "registros"). Em fundo sage → `butter`. Em fundo cream → `amberDeep`.

### Tipografia

| Família | Uso | Pesos | Notas |
|---|---|---|---|
| **Fraunces** (variável: `opsz`, `wght`, `SOFT`, `ital`) | Títulos, botões, labels de UI importantes | 300 (italic), 400, 500, 600 | `fontVariationSettings: "'opsz' 144"` em títulos grandes; `'opsz' 96` em médios. Itálico fino (300) usado para acentos emocionais. |
| **Inter** | UI geral (parágrafos, microcopy, botões secundários) | 400, 500, 600, 700 | font-feature-settings "ss01", "ss02" |
| **Caveat** | Notas manuscritas, legendas de polaroids, decoração | 400, 600 | Sempre em contextos "humanos" — datas, anotações |
| **JetBrains Mono** | Etiquetas (eyebrow), tags, contadores | 400, 500 | `letter-spacing: 1.4px`, `text-transform: uppercase`, `font-size: 9.5–10px`, sempre opacidade 0.6–0.8 |

**Escala (px):**
- H1 grande (FrontPage, Mesversário): 36–52, lh 0.94–1.0, letter-spacing -1.1 a -1.6
- H1 médio (telas internas): 28–32, lh 1.0, letter-spacing -0.6 a -0.9
- H2 (seções): 22, weight 500, letter-spacing -0.4
- Body: 13–14.5, lh 1.5
- Caveat (notas): 14–20, lh 1.15–1.25
- Mono (eyebrow): 9.5–11, opacidade 0.7

### Spacing / radius

- Spacing scale: 4, 6, 8, 10, 12, 14, 16, 18, 22, 24, 28, 32
- Border radius:
  - **18** botões primários
  - **14** cards, inputs
  - **10** chips menores, day cells de calendário
  - **999** chips/pills outline
  - **6** polaroids (radius pequeno, estilo papel)
  - **4** polaroid interno (foto)

### Sombras

```css
/* card elevation */
box-shadow: 0 6px 18px -10px rgba(63,79,46,0.35), 0 0 0 1px rgba(63,79,46,0.06);

/* botão primário sobre sage */
box-shadow: 0 1px 0 rgba(255,255,255,0.12) inset, 0 10px 22px -8px rgba(20,30,15,0.6);

/* botão primário sobre cream */
box-shadow: 0 1px 0 rgba(255,255,255,0.15) inset, 0 8px 18px -6px rgba(63,79,46,0.45);

/* polaroid */
box-shadow: 0 8px 18px -10px rgba(20,30,15,0.45);
```

---

## Componentes do sistema

Documentados em `source/tsr-system.jsx` (primitivos) e `source/tsr-chrome.jsx` (chrome de tela).

### Primitivos (tsr-system.jsx)
- **`Icon`** — biblioteca de ícones SVG line (book, calendar, pencil, sparkle, scrapbook, settings, arrow, chevron, camera, heart, image, download, plus, x, check, trash, leaf, sun, moon, cake, google, star, lock, bell, more, share, baby, pregnancy). Stroke 1.75, linecap/join round.
- **`Placeholder`** — placeholder striped tonal (sage, cream, amber, peach, sky, lilac, butter, mint). Substituir por componente de Image real do projeto.
- **`PaperBG`** — overlay de textura de papel sutil (SVG noise data URL). Pode ser substituído por uma imagem PNG no app real.
- **`Stitched`** — wrapper com borda tracejada estilo costura por dentro do raio.
- **`Stamp`** — selo circular tracejado com label/sub-label e rotação.
- **`Blob`** — shape orgânico SVG decorativo.
- **`PhoneScreen`** — frame de iPhone (390x844) só usado para os mockups; **descartar** no app real.

### Chrome de tela (tsr-chrome.jsx)
- **`ScreenHeader`** — header padrão com botão back, eyebrow monospace, título Fraunces grande, subtitle.
- **`TabBar`** — bottom tabs (Início, Calendário, Registros, Eventos, Livro). 5 tabs com ícones line.
- **`MomentCard`** — card de registro com polaroid interna, label colorido por tipo (DAILY/WEEKLY/MESVERSARIO/PREGNANCY_WEEK/DAY_OF_LIFE).
- **`CreamScreen`** / **`SageScreen`** — wrappers de fundo com paper texture + blobs decorativos.
- **`PrimaryButton`** — botão olive sólido com ícone arrow.
- **`GhostButton`** — botão outline.
- **`TornDivider`** — divisor SVG estilo papel rasgado entre seções.
- **`Tag`** — etiqueta monospace pequena (eyebrow).
- **`Polaroid`** — moldura cream com foto interna e legenda Caveat opcional.

---

## Telas (11)

### 1. FrontPage (`screens/front-page.jsx` — `FrontPageD`)
**Propósito:** primeira impressão antes do login.
**Layout:** split em duas metades. Topo (~360px): collage scrapbook em fundo sage com 2 polaroids, fitas adesivas, folha decorativa, selo "capítulo 01". Borda inferior em "torn paper" creme transitando pra continuação sage. Embaixo: eyebrow monospace, H1 gigante editorial em 4 linhas (`memórias / acolhedoras, / seguras / & suas.` — itálico butter em "acolhedoras"), parágrafo subtítulo, botão CTA `oliveDeep` "vamos começar", microtexto com cadeado "ficam só no seu aparelho".
**Copy:** "memórias acolhedoras, seguras e suas" / "do diário do dia-a-dia ao livro de memórias do seu filho" / "vamos começar" / "ficam só no seu aparelho"

### 2. Login (`screens/login.jsx`)
**Propósito:** autenticação.
**Layout:** fundo sage. Mini scrapbook acent no topo (2 polaroids menores rotacionadas, folha, selo). H1 editorial igual à FrontPage. Stack vertical: botão branco-creme com Google logo "continuar com Google", botão outline creme "continuar como convidado", footer com cadeado.

### 3. Cadastro do bebê (`screens/child-registration.jsx`)
**Propósito:** onboarding do dado do bebê.
**Layout:** fundo cream. ScreenHeader com chapter "passo 1 de 2", título "vamos conhecer / seu pequeno." Avatar circular com upload (polaroid circular tracejada com câmera no centro + badge "+ foto" amber rotacionado). Segmented control "já nasceu / gestação". Inputs (nome, data nascimento) em cards `creamDeep`. Footer fixed com botão primário "continuar".

### 4. Home / Início (`screens/home.jsx`)
**Propósito:** dashboard principal.
**Layout:** banda sage no topo com eyebrow + ícone settings, avatar da criança + idade ("Manu, 7 meses" — itálico butter), microtexto "~218 dias de vida" em Caveat. Torn divider creme. Body scrollable: card destaque butter "próximo mesversário em 12 dias" com selo amber, seção "últimos registros" com 2 MomentCards mini, grid 2x2 de atalhos coloridos. TabBar fixa.

### 5. Calendário (`screens/calendar.jsx`)
**Propósito:** calendário com feriados BR + datas marcadas.
**Layout:** ScreenHeader "abril 2025" com chevrons. Grid 7-col com weekday labels mono (s t q q s s d). Day cells: hoje = oliveDeep cheio, mesversário = butter, feriado = peach, evento = sageMist (com borda tracejada). Legenda em chips. Lista "próximos" de 3 cards com badge de data colorido. TabBar.

### 6. Lista de Registros (`screens/moments-list.jsx`)
**Propósito:** ver todos os registros.
**Layout:** ScreenHeader com botão "novo" oliveDeep no canto. Filter chips horizontais (todos/diário/semanal/mesversário) com contadores. Sashes "esta semana" / "mês passado" entre seções. Grid 2-col de MomentCards mini.

### 7. Novo Registro (`screens/moment-registration.jsx`)
**Propósito:** criar registro.
**Layout:** fundo cream. ScreenHeader "conte um momento". Polaroid grande rotacionada -3deg com fita adesiva no topo, área de upload striped com câmera. Type pills (diário, semanal, mesversário, marco) — selecionado fica oliveDeep. Input "título" em Fraunces. Textarea "uma notinha" estilo caderno pautado em Caveat. Row com inputs de data e marcadores. Footer com botão "guardar momento" + check icon.

### 8. Mesversário (`screens/mesversario.jsx`)
**Propósito:** celebração mensal.
**Layout:** fundo sage. Header simples (back + cake icon + share). Hero com número GIGANTE (200px Fraunces italic 300, letter-spacing -8) "7" em creme ocupando o lado esquerdo, label "meses" + Caveat "sete!" no canto direito, polaroid rotacionada 8deg sobreposta no canto inferior direito, selo "capítulo 07" rotacionado -8deg no canto inferior esquerdo. Card de "mensagem do mês" com borda tracejada creme — texto Fraunces italic grande. Mini-stats grid 2-col (idade, dia da vida). Botão CTA cream "registrar este mesversário".
**Mensagem do mês 7:** "sete meses do mais doce dos sorrisos. você descobriu o mundo dos sabores e nada vai ser igual."

### 9. Lista de Eventos (`screens/events.jsx` — `EventListA`)
**Propósito:** ver todos os eventos (capturas em lote).
**Layout:** ScreenHeader com botão "novo". Sashes "2025" / "2024". Cards horizontais: stack de 2 polaroids mini sobrepostas (uma rotacionada -6deg, outra +6deg) à esquerda, info à direita (eyebrow data, título Fraunces, contador "X fotos" mono), chevron.

### 10. Câmera de Evento (`screens/events.jsx` — `EventCameraA`)
**Propósito:** capturar várias fotos para um evento.
**Layout:** fullscreen escuro. Visor com brackets de canto creme. Pill flutuante no topo: eyebrow "evento" + nome do evento ("passeio no parque") em Fraunces italic. Pill no canto inferior esquerdo "✦ 12 capturadas". Strip de shutter: galeria à esquerda, botão obturador grande creme com anel butter, check butter à direita.

### 11. Nomear Evento (`screens/events.jsx` — `EventNamingA`)
**Propósito:** dar nome após capturar.
**Layout:** fundo sage. ScreenHeader em cores creme/butter. Strip de 3 polaroids (a do meio mostra "+9" indicando mais fotos). Input grande Fraunces "nome do evento". Caveat butter "que dia bonito". Input "quando aconteceu". Botão CTA cream com check.

### 12. Grid de Fotos (`screens/events.jsx` — `PhotoGridA`)
**Propósito:** ver todas as fotos de um evento.
**Layout:** ScreenHeader "passeio / no parque". Mosaico grid 3-col com primeira célula em span 2x2 + badge "capa". Card creamDeep com nota Caveat "nota do dia". Footer com 2 botões: "adicionar fotos" outline + "ao livro" oliveDeep.

### 13. Livro de Memórias (`screens/memory-book.jsx`)
**Propósito:** álbum estilo livro com spreads.
**Layout:** fundo creamDeep. ScreenHeader "livro da Manu" com download icon. Spread em card cream com linha vertical fina central simulando dobra do livro. Heading "capítulo 07" com selo. Grid 2-col (left page / right page) com polaroids rotacionadas, notas Caveat, sticker leaf. Page numbers mono no rodapé. Navegação entre capítulos: chevron prev / dots indicator / chevron next.

### 14. Histórias (`screens/stories.jsx`)
**Propósito:** biblioteca de histórias para ler com o bebê.
**Layout:** hero sage com história destacada — polaroid rotacionada com emoji grande, título Fraunces, botão "ler agora". Torn divider. Lista de cards creamDeep — thumbnail striped com emoji + sticker rotation, eyebrow tag + duração, título Fraunces, botão arrow circular.
**Histórias placeholder:** "O Patinho Amigo" (4min, amizade), "A Lua Gentil" (3min, soninho), "A Festa das Folhas" (5min, natureza), "O Sol que Sorri" (3min, alegria), "Pequenas Estrelas" (4min, soninho).

---

## Interações & comportamento

- **Navegação:** os 5 destinos da TabBar — Início (Home), Calendário, Registros (lista), Eventos (lista), Livro (memory book).
- **Stories** abre como destino opcional (não está na tabbar; entra via card no Home ou shortcut).
- **Login → Cadastro do bebê → Home** é o fluxo de onboarding.
- **Home → "novo registro" shortcut → Novo Registro** cria moment.
- **Home → "novo evento" shortcut → Câmera → Nomear → Photo Grid** cria evento com fotos.
- **Momento → após salvar** volta pra Home com toast de sucesso (não desenhado ainda).
- **Mesversário** é gerado automaticamente quando bate a data; aparece como destaque na Home e como entrada no calendário.

### Animações sugeridas
- Botões CTA: `transform: scale(0.97)` no press, `transition: 80ms ease-out`
- Polaroids no scrapbook: subtle parallax em scroll (3-5deg max)
- Torn divider entre seções: estático
- Caveat (notas manuscritas): considerar fade-in linha-por-linha quando entra no viewport (delight opcional)
- Itálico Fraunces: poderia animar `fontVariationSettings opsz` em hover/press para efeito sutil

### Acessibilidade
- Mín 44pt em todos os hit targets (botões, day cells do calendário, chips)
- Texto sobre sage saturado: usar `cream` (#F5EFE0) — contraste 8.4:1 ✓
- Texto `oliveDeep` (#3F4F2E) sobre `cream` (#F5EFE0): contraste 9.2:1 ✓
- Itálico decorativo (butter sobre sage, amberDeep sobre cream) é sempre apoiado por texto principal — nunca sozinho transmitindo informação crítica.
- Caveat só em conteúdo decorativo/redundante — nunca em rótulos de UI primários (legibilidade).

---

## State management (sugerido — adapte ao que já existe no app)

| State | Tipo | Onde |
|---|---|---|
| `child` | `{ name, dob, photo, status: 'born' \| 'pregnancy' }` | global |
| `moments` | `MomentCard[]` agrupados por data | global, persistido local |
| `events` | `Event[]` com array de photos | global, persistido local |
| `currentMonthMessage` | `string` | calculado a partir de `child.dob` + `today` |
| `holidays` | `Holiday[]` (BR) | seed estático |
| `bookChapters` | `Chapter[]` | derivado de moments + events agrupados por mês |

---

## Assets / imagens

Os designs usam **placeholders striped** (linhas diagonais coloridas com label) onde fotos reais entrariam. Substituir por:
- `Image` real do React Native com `source={{ uri }}`
- Picker de foto (expo-image-picker) na primeira interação
- Default avatar / hero illustration para estado vazio (sugestão: ilustração contratada de mãe+bebê em folhas, estilo line art, na cor `oliveDeep`)

**Ícones:** os SVGs em `Icon` foram desenhados à mão pra esse projeto. No app real, recomendo:
- Manter como SVGs em `react-native-svg` (mais fiel à direção)
- Ou substituir por `lucide-react-native` (linha similar — substituir só os que existem; `book`, `calendar`, `camera`, `leaf`, `cake`, `lock`, `share`, `chevron-*`, `arrow-*`, `plus`, `x`, `check`, `trash`, `bell`, `settings`, `star`, `heart`, `sun`, `moon`, `image`, `download` todos existem)
- Os customizados que não existem no Lucide: `baby`, `pregnancy`, `scrapbook`, `sparkle` — manter os SVGs do `Icon`.

**Fontes:** importar pelo Expo (`expo-font`) ou pacote nativo:
- Fraunces (variável)
- Inter
- Caveat
- JetBrains Mono

---

## Arquivos neste pacote

```
design_handoff_cresci_com/
├── README.md                       # este arquivo
└── source/
    ├── cresci-com.html             # canvas com todas as 13 telas (abrir num browser pra ver)
    ├── tsr-system.jsx              # primitivos (Icon, Placeholder, Stamp, Stitched, Blob...)
    ├── tsr-chrome.jsx              # chrome (ScreenHeader, TabBar, MomentCard, buttons...)
    └── screens/
        ├── front-page.jsx          # FrontPageA, FrontPageB, FrontPageC, FrontPageD (default)
        ├── login.jsx               # LoginA
        ├── child-registration.jsx  # ChildRegistrationA
        ├── home.jsx                # HomeA
        ├── moments-list.jsx        # MomentsListA
        ├── moment-registration.jsx # MomentRegistrationA
        ├── memory-book.jsx         # MemoryBookA
        ├── calendar.jsx            # CalendarA
        ├── stories.jsx             # StoriesA
        ├── mesversario.jsx         # MesversarioA
        └── events.jsx              # EventListA, EventCameraA, EventNamingA, PhotoGridA
```

---

## Como usar este pacote no Claude Code

1. Descompacte o zip dentro do repositório do app (numa pasta tipo `docs/design_handoff_cresci_com/`).
2. Abra `source/cresci-com.html` num browser local pra ver os designs vivos (todas as 13 telas num canvas explorável — você pode arrastar os artboards, abrir em fullscreen com clique).
3. No Claude Code, peça:
   > "Implemente o redesign descrito em `docs/design_handoff_cresci_com/README.md`. Comece traduzindo os tokens (cores, tipografia, spacing) pra `theme.ts` ou onde já estiverem definidos. Depois recrie cada tela seguindo os layouts descritos, lendo os JSX de referência em `source/screens/*.jsx` pra detalhes específicos. Mantenha a arquitetura/navegação atual do app."

4. Itere tela por tela. Comece pela `FrontPage` ou pelo `Home` (dependendo do que tem maior valor mostrar primeiro).

---

**Direção visual:** D+B — sage saturado (impacto) + editorial scrapbook (calor) + tipo creme + botão olive.
**Princípios:** acolhedor, manuscrito, livro de família, sem AI-slop. Cada elemento tem peso; placeholders são feitos pra se tornarem fotos reais; texturas e rotações sutis dão vida sem virar distração.
