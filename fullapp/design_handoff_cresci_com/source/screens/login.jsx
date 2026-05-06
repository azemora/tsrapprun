/**
 * Login — direção D + B
 * Conteúdo real do app:
 *   "memórias acolhedoras, seguras e suas"
 *   "para começar, escolha como entrar"
 *   "continuar com Google" / "continuar como convidado"
 *   "ficam só no seu aparelho"
 */

const { Icon, colors: LC, PaperBG, Stitched, Stamp, Blob, PhoneScreen } = window.TSR;
const { SageScreen, PrimaryButton, GhostButton, Tag, Polaroid } = window.TSR_Chrome;

// Variação A — direção escolhida (D+B)
function LoginA() {
  return (
    <PhoneScreen bg={LC.sage} statusColor={LC.cream} homeColor={LC.cream}>
      <SageScreen>
        <div style={{ flex: 1, display: "flex", flexDirection: "column", padding: "20px 28px 32px" }}>
          {/* eyebrow */}
          <div style={{ display: "flex", alignItems: "center", gap: 10, marginTop: 8 }}>
            <div style={{ width: 22, height: 1, background: LC.cream, opacity: 0.5 }} />
            <Tag color={LC.cream} style={{ opacity: 0.78 }}>cresci·com — entrar</Tag>
            <div style={{ flex: 1, height: 1, background: LC.cream, opacity: 0.18 }} />
          </div>

          {/* hero */}
          <div style={{ flex: 1, display: "flex", flexDirection: "column", justifyContent: "center", gap: 22, position: "relative" }}>
            {/* mini scrapbook accent */}
            <div style={{ position: "relative", height: 170 }}>
              <div style={{ position: "absolute", left: -4, top: 4, transform: "rotate(-8deg)" }}>
                <Polaroid tone="peach" caption="dia 1" w={120} h={130} />
              </div>
              <div style={{ position: "absolute", right: -8, top: 18, transform: "rotate(7deg)" }}>
                <Polaroid tone="butter" caption="primeira vez" w={108} h={120} />
              </div>
              <div style={{ position: "absolute", left: 86, top: 116, transform: "rotate(-18deg)" }}>
                <Icon name="leaf" size={36} color={LC.cream} strokeWidth={1.5} />
              </div>
              <div style={{ position: "absolute", right: 28, bottom: 0 }}>
                <Stamp size={56} color={LC.butter} sub="capítulo" label="01" rotate={6} />
              </div>
            </div>

            <div>
              <h1 style={{
                fontFamily: "'Fraunces', serif",
                fontWeight: 400,
                fontSize: 36,
                lineHeight: 0.98,
                color: LC.cream,
                margin: 0,
                letterSpacing: -1.1,
                fontVariationSettings: "'opsz' 144",
              }}>
                memórias<br/>
                <em style={{ fontStyle: "italic", fontWeight: 300, color: LC.butter }}>acolhedoras</em>,<br/>
                seguras &amp; suas.
              </h1>
              <p style={{ fontSize: 13.5, color: LC.cream, marginTop: 14, opacity: 0.78, lineHeight: 1.55, maxWidth: 290 }}>
                para começar, escolha como entrar.
              </p>
            </div>
          </div>

          {/* CTA stack */}
          <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
            <button style={{
              background: LC.cream,
              color: LC.oliveDeep,
              border: "none",
              borderRadius: 18,
              padding: "16px 20px",
              fontSize: 15.5,
              fontWeight: 600,
              fontFamily: "'Fraunces', serif",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: 10,
              boxShadow: "0 1px 0 rgba(255,255,255,0.4) inset, 0 8px 18px -8px rgba(20,30,15,0.4)",
            }}>
              <Icon name="google" size={20} />
              <span>continuar com Google</span>
            </button>

            <button style={{
              background: "transparent",
              color: LC.cream,
              border: `1.4px solid ${LC.cream}`,
              borderRadius: 18,
              padding: "14px 20px",
              fontSize: 14.5,
              fontWeight: 500,
              fontFamily: "'Fraunces', serif",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              gap: 8,
              opacity: 0.95,
            }}>
              continuar como convidado
            </button>

            <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 6, color: LC.cream, opacity: 0.6, fontSize: 11, marginTop: 4 }}>
              <Icon name="lock" size={11} color={LC.cream} />
              <span style={{ fontFamily: "'JetBrains Mono', monospace", letterSpacing: 0.4 }}>ficam só no seu aparelho</span>
            </div>
          </div>
        </div>
      </SageScreen>
    </PhoneScreen>
  );
}

window.LoginA = LoginA;
