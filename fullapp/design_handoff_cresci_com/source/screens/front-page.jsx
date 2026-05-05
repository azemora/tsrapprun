/**
 * FrontPage — 3 variações
 *
 * Conteúdo real:
 *  H1: "memórias acolhedoras, seguras e suas"
 *  Sub: "do diário do dia-a-dia ao livro de memórias do seu filho"
 *  CTA: "vamos começar"
 *  Footer: "ficam só no seu aparelho"
 */

const { Icon, Placeholder, PaperBG, Stitched, Stamp, Blob, PhoneScreen, colors: C } = window.TSR;

// ─────────────────────────────────────────────
// Variação A — refinamento direto do atual
// (cozy, illustration central, type Fraunces variável)
// ─────────────────────────────────────────────
function FrontPageA() {
  return (
    <PhoneScreen bg={C.cream}>
      <PaperBG color={C.cream} style={{ flex: 1, display: "flex", flexDirection: "column", padding: "8px 28px 32px" }}>
        {/* logotipo / wordmark */}
        <div style={{ display: "flex", alignItems: "center", gap: 8, marginTop: 10 }}>
          <div style={{ width: 26, height: 26, borderRadius: 8, background: C.sage, display: "flex", alignItems: "center", justifyContent: "center" }}>
            <Icon name="leaf" size={16} color="#fff" />
          </div>
          <div style={{ fontFamily: "'Fraunces', serif", fontWeight: 600, fontSize: 16, color: C.olive, letterSpacing: -0.2, fontVariationSettings: "'opsz' 14" }}>
            cresci<span style={{ color: C.amber }}>·</span>com
          </div>
        </div>

        {/* hero illustration central — placeholder estilo cartão postal */}
        <div style={{ flex: 1, display: "flex", flexDirection: "column", justifyContent: "center", alignItems: "center", gap: 24, position: "relative" }}>
          <div style={{ position: "relative", width: 220, height: 240 }}>
            <Stitched color={C.olive} radius={28} padding={10} bg={C.sageMist} style={{ width: 220, height: 240, transform: "rotate(-3deg)" }}>
              <Placeholder label="ilustração: mãe + bebê em folhas" w="100%" h="100%" tone="sage" radius={20} />
            </Stitched>
            <div style={{ position: "absolute", top: -12, right: -8 }}>
              <Stamp size={62} color={C.amber} sub="desde" label="2025" rotate={10} />
            </div>
            <div style={{ position: "absolute", bottom: -12, left: -16, transform: "rotate(-12deg)" }}>
              <Icon name="leaf" size={36} color={C.sage} />
            </div>
          </div>

          <div style={{ textAlign: "center", maxWidth: 320 }}>
            <h1 style={{
              fontFamily: "'Fraunces', serif",
              fontWeight: 500,
              fontSize: 34,
              lineHeight: 1.05,
              color: C.oliveDeep,
              margin: 0,
              letterSpacing: -0.8,
              textWrap: "pretty",
              fontVariationSettings: "'opsz' 96, 'SOFT' 80",
            }}>
              memórias <em style={{ fontStyle: "italic", color: C.amber, fontWeight: 400 }}>acolhedoras</em>, seguras e suas
            </h1>
            <p style={{ fontSize: 14.5, color: C.olive, marginTop: 14, opacity: 0.85, lineHeight: 1.5, textWrap: "pretty" }}>
              do diário do dia-a-dia ao livro de memórias do seu filho
            </p>
          </div>
        </div>

        {/* CTA */}
        <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
          <button style={{
            background: C.olive,
            color: C.cream,
            border: "none",
            borderRadius: 18,
            padding: "18px 24px",
            fontSize: 16,
            fontWeight: 600,
            fontFamily: "'Fraunces', serif",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            gap: 10,
            boxShadow: "0 1px 0 rgba(255,255,255,0.15) inset, 0 8px 18px -6px rgba(63,79,46,0.5)",
          }}>
            vamos começar
            <Icon name="arrow-right" size={18} color={C.cream} strokeWidth={2} />
          </button>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 6, color: C.olive, opacity: 0.6, fontSize: 12 }}>
            <Icon name="lock" size={12} color={C.olive} />
            <span>ficam só no seu aparelho</span>
          </div>
        </div>
      </PaperBG>
    </PhoneScreen>
  );
}

// ─────────────────────────────────────────────
// Variação B — Editorial / Capa de livro
// (split layout, big editorial type, montagem scrapbook em cima)
// ─────────────────────────────────────────────
function FrontPageB() {
  return (
    <PhoneScreen bg={C.creamDeep}>
      {/* Top half: scrapbook collage */}
      <div style={{
        height: 380,
        background: C.sageMist,
        position: "relative",
        overflow: "hidden",
        borderBottom: `1.5px solid ${C.olive}33`,
      }}>
        <PaperBG color={C.sageMist} style={{ position: "absolute", inset: 0 }} />
        {/* sun blob */}
        <div style={{ position: "absolute", top: -40, right: -40, opacity: 0.6 }}>
          <Blob size={180} color={C.butter} />
        </div>
        {/* picture 1 */}
        <div style={{ position: "absolute", top: 38, left: 28, transform: "rotate(-7deg)" }}>
          <Stitched color={C.olive} radius={6} padding={8} bg="#fff" style={{ width: 140, height: 160 }}>
            <Placeholder label="primeira foto" w="100%" h="100%" tone="peach" radius={2} />
            <div style={{ textAlign: "center", fontFamily: "'Caveat', 'Fraunces', cursive", fontSize: 16, color: C.olive, marginTop: 4 }}>
              dia 1
            </div>
          </Stitched>
        </div>
        {/* picture 2 */}
        <div style={{ position: "absolute", top: 70, right: 22, transform: "rotate(6deg)" }}>
          <Stitched color={C.amber} radius={6} padding={6} bg="#fff" style={{ width: 120, height: 140 }}>
            <Placeholder label="primeiro sorriso" w="100%" h="100%" tone="butter" radius={2} />
          </Stitched>
        </div>
        {/* sticker / leaf */}
        <div style={{ position: "absolute", bottom: 20, left: 100, transform: "rotate(-20deg)" }}>
          <Icon name="leaf" size={48} color={C.sage} />
        </div>
        {/* tape */}
        <div style={{ position: "absolute", top: 28, left: 80, width: 50, height: 18, background: "rgba(217,178,92,0.55)", transform: "rotate(-10deg)", borderRadius: 1 }} />
        <div style={{ position: "absolute", top: 56, right: 50, width: 50, height: 18, background: "rgba(143,168,118,0.5)", transform: "rotate(8deg)", borderRadius: 1 }} />
        {/* stamp */}
        <div style={{ position: "absolute", bottom: 28, right: 30 }}>
          <Stamp size={70} color={C.amberDeep} sub="capítulo" label="01" rotate={6} />
        </div>
      </div>

      {/* Bottom half: editorial */}
      <PaperBG color={C.creamDeep} style={{ flex: 1, padding: "28px 28px 24px", display: "flex", flexDirection: "column" }}>
        <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 16 }}>
          <div style={{ width: 24, height: 1, background: C.olive }} />
          <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: C.olive, letterSpacing: 1.5, textTransform: "uppercase" }}>
            cresci·com — vol. 01
          </span>
        </div>

        <h1 style={{
          fontFamily: "'Fraunces', serif",
          fontWeight: 400,
          fontSize: 38,
          lineHeight: 0.98,
          color: C.oliveDeep,
          margin: 0,
          letterSpacing: -1.2,
          textWrap: "balance",
          fontVariationSettings: "'opsz' 144",
        }}>
          memórias<br/>
          <em style={{ fontStyle: "italic", fontWeight: 300, color: C.amberDeep }}>acolhedoras</em>,<br/>
          seguras<br/>
          <span style={{ color: C.sage }}>&amp;</span> suas.
        </h1>

        <p style={{ fontSize: 13, color: C.olive, marginTop: 18, opacity: 0.78, lineHeight: 1.55, maxWidth: 280 }}>
          do diário do dia-a-dia ao livro de memórias do seu filho.
        </p>

        <div style={{ flex: 1 }} />

        <button style={{
          marginTop: 14,
          background: "transparent",
          color: C.oliveDeep,
          border: `1.6px solid ${C.oliveDeep}`,
          borderRadius: 999,
          padding: "16px 22px",
          fontSize: 15,
          fontWeight: 600,
          fontFamily: "'Fraunces', serif",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
        }}>
          <span>vamos começar</span>
          <Icon name="arrow-right" size={18} color={C.oliveDeep} strokeWidth={2} />
        </button>
        <div style={{ textAlign: "center", color: C.olive, opacity: 0.55, fontSize: 11, marginTop: 10, fontFamily: "'JetBrains Mono', monospace", letterSpacing: 0.5 }}>
          ✦ ficam só no seu aparelho ✦
        </div>
      </PaperBG>
    </PhoneScreen>
  );
}

// ─────────────────────────────────────────────
// Variação C — Bold / Playful (saturada, blobs)
// ─────────────────────────────────────────────
function FrontPageC() {
  return (
    <PhoneScreen bg={C.sage}>
      <div style={{ flex: 1, position: "relative", overflow: "hidden", display: "flex", flexDirection: "column" }}>
        {/* organic shapes background */}
        <Blob size={420} color={C.sageMist} style={{ position: "absolute", top: -180, left: -150, opacity: 0.9 }} />
        <Blob size={280} color={C.amber} style={{ position: "absolute", bottom: -80, right: -100, opacity: 0.85 }} d="M40.6,-46.7C53.4,-37.7,65.2,-26.7,68.5,-13.4C71.8,-0.1,66.6,15.5,57.4,28.2C48.2,40.9,35,50.7,20.5,55.7C6,60.7,-9.8,60.9,-23.5,55.7C-37.2,50.5,-48.8,39.9,-55.6,26.8C-62.4,13.7,-64.4,-1.9,-59.5,-15.1C-54.6,-28.3,-42.9,-39.1,-30.2,-48.4C-17.5,-57.7,-3.8,-65.5,7.7,-65.6C19.1,-65.6,38.3,-58,40.6,-46.7Z" />

        {/* content */}
        <div style={{ flex: 1, padding: "24px 28px 28px", display: "flex", flexDirection: "column", position: "relative", zIndex: 1 }}>
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <div style={{ width: 30, height: 30, borderRadius: 10, background: C.cream, display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Icon name="leaf" size={18} color={C.sage} />
            </div>
            <span style={{ fontFamily: "'Fraunces', serif", fontWeight: 600, fontSize: 17, color: C.cream }}>cresci<span style={{ color: C.butter }}>·</span>com</span>
          </div>

          <div style={{ flex: 1, display: "flex", flexDirection: "column", justifyContent: "center", marginTop: 24 }}>
            {/* big stamp */}
            <div style={{ display: "flex", alignItems: "center", gap: 14, marginBottom: 18 }}>
              <Stamp size={56} color={C.cream} sub="cap." label="1" rotate={-6} />
              <div style={{ height: 1, flex: 1, background: C.cream, opacity: 0.4 }} />
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: C.cream, opacity: 0.7, letterSpacing: 1.4, textTransform: "uppercase" }}>
                bem-vindo
              </span>
            </div>

            <h1 style={{
              fontFamily: "'Fraunces', serif",
              fontWeight: 400,
              fontSize: 52,
              lineHeight: 0.94,
              color: C.cream,
              margin: 0,
              letterSpacing: -1.6,
              fontVariationSettings: "'opsz' 144",
            }}>
              memórias<br/>
              <em style={{ fontStyle: "italic", fontWeight: 300 }}>acolhedoras</em>
            </h1>
            <div style={{ marginTop: 14, padding: "8px 14px", background: C.butter, borderRadius: 999, alignSelf: "flex-start", border: `1.4px dashed ${C.amberDeep}` }}>
              <span style={{ fontFamily: "'Fraunces', serif", fontStyle: "italic", color: C.amberDeep, fontSize: 15 }}>seguras &amp; suas</span>
            </div>

            <p style={{ fontSize: 14.5, color: C.cream, marginTop: 22, opacity: 0.85, lineHeight: 1.5, maxWidth: 280 }}>
              do diário do dia-a-dia ao livro de memórias do seu filho
            </p>
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: 10, position: "relative", zIndex: 2 }}>
            <button style={{
              background: C.cream,
              color: C.oliveDeep,
              border: "none",
              borderRadius: 22,
              padding: "20px 24px",
              fontSize: 17,
              fontWeight: 600,
              fontFamily: "'Fraunces', serif",
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              boxShadow: "0 8px 0 rgba(63,79,46,0.35)",
            }}>
              <span>vamos começar</span>
              <div style={{ width: 32, height: 32, borderRadius: 16, background: C.amber, display: "flex", alignItems: "center", justifyContent: "center" }}>
                <Icon name="arrow-right" size={16} color={C.cream} strokeWidth={2.2} />
              </div>
            </button>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 6, color: C.cream, opacity: 0.7, fontSize: 11.5 }}>
              <Icon name="lock" size={12} color={C.cream} />
              <span style={{ fontFamily: "'JetBrains Mono', monospace", letterSpacing: 0.5 }}>ficam só no seu aparelho</span>
            </div>
          </div>
        </div>
      </div>
    </PhoneScreen>
  );
}

// ─────────────────────────────────────────────
// Variação D — combo escolhido
// fundo sage (C) + layout editorial (B) + tipo creme + botão olive sólido (A)
// ─────────────────────────────────────────────
function FrontPageD() {
  return (
    <PhoneScreen bg={C.sage} statusColor={C.cream} homeColor={C.cream}>
      <div style={{ flex: 1, position: "relative", overflow: "hidden", display: "flex", flexDirection: "column" }}>
        {/* sage paper texture overlay */}
        <PaperBG color="transparent" style={{ position: "absolute", inset: 0, pointerEvents: "none", opacity: 0.6 }} />

        {/* organic blobs — sutis, dão warmth sem competir */}
        <Blob size={360} color={C.oliveDeep} style={{ position: "absolute", top: -160, right: -130, opacity: 0.18 }} />
        <Blob
          size={260}
          color={C.butter}
          opacity={0.18}
          style={{ position: "absolute", bottom: -90, left: -110 }}
          d="M40.6,-46.7C53.4,-37.7,65.2,-26.7,68.5,-13.4C71.8,-0.1,66.6,15.5,57.4,28.2C48.2,40.9,35,50.7,20.5,55.7C6,60.7,-9.8,60.9,-23.5,55.7C-37.2,50.5,-48.8,39.9,-55.6,26.8C-62.4,13.7,-64.4,-1.9,-59.5,-15.1C-54.6,-28.3,-42.9,-39.1,-30.2,-48.4C-17.5,-57.7,-3.8,-65.5,7.7,-65.6C19.1,-65.6,38.3,-58,40.6,-46.7Z"
        />

        {/* TOP — scrapbook collage (sage tinted) */}
        <div style={{ height: 360, position: "relative", overflow: "hidden", flexShrink: 0 }}>
          {/* picture 1 */}
          <div style={{ position: "absolute", top: 50, left: 28, transform: "rotate(-7deg)" }}>
            <Stitched color={C.cream} radius={6} padding={8} bg={C.cream} style={{ width: 140, height: 168 }}>
              <Placeholder label="primeira foto" w="100%" h="100%" tone="peach" radius={2} />
              <div style={{ textAlign: "center", fontFamily: "'Caveat', cursive", fontSize: 18, color: C.oliveDeep, marginTop: 4, lineHeight: 1 }}>
                dia 1 ✿
              </div>
            </Stitched>
          </div>
          {/* picture 2 */}
          <div style={{ position: "absolute", top: 82, right: 22, transform: "rotate(6deg)" }}>
            <Stitched color={C.cream} radius={6} padding={6} bg={C.cream} style={{ width: 124, height: 144 }}>
              <Placeholder label="primeiro sorriso" w="100%" h="100%" tone="butter" radius={2} />
            </Stitched>
          </div>
          {/* tape pieces */}
          <div style={{ position: "absolute", top: 38, left: 84, width: 56, height: 18, background: "rgba(252,228,167,0.55)", transform: "rotate(-10deg)", borderRadius: 1 }} />
          <div style={{ position: "absolute", top: 70, right: 56, width: 52, height: 18, background: "rgba(245,239,224,0.45)", transform: "rotate(8deg)", borderRadius: 1 }} />
          {/* leaf sticker */}
          <div style={{ position: "absolute", bottom: 18, left: 110, transform: "rotate(-22deg)" }}>
            <Icon name="leaf" size={44} color={C.cream} strokeWidth={1.6} />
          </div>
          {/* stamp */}
          <div style={{ position: "absolute", bottom: 28, right: 24 }}>
            <Stamp size={70} color={C.butter} sub="capítulo" label="01" rotate={6} />
          </div>
          {/* divider — torn paper effect */}
          <svg style={{ position: "absolute", bottom: -1, left: 0, width: "100%", height: 14 }} viewBox="0 0 390 14" preserveAspectRatio="none">
            <path d="M0,8 Q30,2 60,7 T120,8 T180,6 T240,9 T300,7 T390,8 L390,14 L0,14 Z" fill={C.sage} />
          </svg>
        </div>

        {/* BOTTOM — editorial */}
        <div style={{ flex: 1, padding: "20px 28px 26px", display: "flex", flexDirection: "column", position: "relative", zIndex: 1 }}>
          <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 14 }}>
            <div style={{ width: 22, height: 1, background: C.cream, opacity: 0.5 }} />
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: C.cream, opacity: 0.75, letterSpacing: 1.6, textTransform: "uppercase" }}>
              cresci·com — vol. 01
            </span>
          </div>

          <h1 style={{
            fontFamily: "'Fraunces', serif",
            fontWeight: 400,
            fontSize: 42,
            lineHeight: 0.96,
            color: C.cream,
            margin: 0,
            letterSpacing: -1.3,
            textWrap: "balance",
            fontVariationSettings: "'opsz' 144, 'SOFT' 50",
          }}>
            memórias<br/>
            <em style={{ fontStyle: "italic", fontWeight: 300, color: C.butter }}>acolhedoras</em>,<br/>
            seguras<br/>
            <span style={{ color: C.cream, opacity: 0.55 }}>&amp;</span> suas.
          </h1>

          <p style={{ fontSize: 13.5, color: C.cream, marginTop: 16, opacity: 0.78, lineHeight: 1.55, maxWidth: 280 }}>
            do diário do dia-a-dia ao livro de memórias do seu filho.
          </p>

          <div style={{ flex: 1 }} />

          {/* CTA — botão olive sólido (da var A), agora tom-sobre-tom no sage */}
          <button style={{
            background: C.oliveDeep,
            color: C.cream,
            border: "none",
            borderRadius: 18,
            padding: "18px 24px",
            fontSize: 16,
            fontWeight: 600,
            fontFamily: "'Fraunces', serif",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            gap: 10,
            boxShadow: "0 1px 0 rgba(255,255,255,0.12) inset, 0 10px 22px -8px rgba(20,30,15,0.6)",
            letterSpacing: -0.2,
          }}>
            vamos começar
            <Icon name="arrow-right" size={18} color={C.cream} strokeWidth={2} />
          </button>
          <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 6, color: C.cream, opacity: 0.65, fontSize: 11.5, marginTop: 10 }}>
            <Icon name="lock" size={12} color={C.cream} />
            <span style={{ fontFamily: "'JetBrains Mono', monospace", letterSpacing: 0.4 }}>ficam só no seu aparelho</span>
          </div>
        </div>
      </div>
    </PhoneScreen>
  );
}

window.FrontPageA = FrontPageA;
window.FrontPageB = FrontPageB;
window.FrontPageC = FrontPageC;
window.FrontPageD = FrontPageD;
