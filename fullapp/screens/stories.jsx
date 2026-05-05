/**
 * Stories — histórias para ler com o bebê
 * Conteúdo: "O Patinho Amigo", "A Lua Gentil", etc.
 */

const { Icon, colors: ST, PaperBG, Stitched, Stamp, PhoneScreen, Placeholder, Blob } = window.TSR;
const { CreamScreen, ScreenHeader, Tag, Polaroid, TornDivider } = window.TSR_Chrome;

function StoriesA() {
  const stories = [
    { title: "O Patinho Amigo", duration: "4 min", tone: "butter", emoji: "🦆", tag: "amizade" },
    { title: "A Lua Gentil", duration: "3 min", tone: "sky", emoji: "🌙", tag: "soninho" },
    { title: "A Festa das Folhas", duration: "5 min", tone: "sage", emoji: "🍃", tag: "natureza" },
    { title: "O Sol que Sorri", duration: "3 min", tone: "peach", emoji: "☀️", tag: "alegria" },
    { title: "Pequenas Estrelas", duration: "4 min", tone: "lilac", emoji: "✦", tag: "soninho" },
  ];

  return (
    <PhoneScreen bg={ST.cream}>
      <CreamScreen>
        {/* HERO featured story */}
        <div style={{ position: "relative", flexShrink: 0 }}>
          <div style={{ background: ST.sage, padding: "10px 24px 24px", position: "relative", overflow: "hidden" }}>
            <PaperBG color="transparent" style={{ position: "absolute", inset: 0, opacity: 0.5 }} />
            <Blob size={220} color={ST.oliveDeep} style={{ position: "absolute", top: -100, right: -100, opacity: 0.18 }} />

            <div style={{ position: "relative", display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 14 }}>
              <Tag color={ST.cream} style={{ opacity: 0.8 }}>histórias para o soninho</Tag>
              <button style={{ background: "transparent", border: "none", padding: 4 }}>
                <Icon name="more" size={20} color={ST.cream} />
              </button>
            </div>

            <div style={{ position: "relative", display: "flex", gap: 14, alignItems: "center" }}>
              <div style={{ position: "relative" }}>
                <Stitched color={ST.cream} radius={6} padding={6} bg={ST.cream} style={{ width: 110, height: 130, transform: "rotate(-4deg)" }}>
                  <div style={{ width: "100%", height: "100%", background: `repeating-linear-gradient(135deg, ${ST.butter} 0 8px, rgba(168,111,50,0.12) 8px 9px)`, borderRadius: 2, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 36 }}>
                    🦆
                  </div>
                </Stitched>
              </div>
              <div style={{ flex: 1 }}>
                <Tag color={ST.cream} style={{ opacity: 0.7 }}>história · 4 min</Tag>
                <h1 style={{
                  fontFamily: "'Fraunces', serif",
                  fontWeight: 400,
                  fontSize: 26,
                  lineHeight: 1.0,
                  color: ST.cream,
                  margin: "4px 0 0",
                  letterSpacing: -0.7,
                  fontVariationSettings: "'opsz' 144",
                }}>
                  O Patinho<br/>
                  <em style={{ fontStyle: "italic", fontWeight: 300, color: ST.butter }}>Amigo</em>
                </h1>
                <button style={{
                  marginTop: 10,
                  background: ST.cream,
                  color: ST.oliveDeep,
                  border: "none",
                  borderRadius: 999,
                  padding: "8px 14px",
                  fontFamily: "'Fraunces', serif",
                  fontWeight: 600,
                  fontSize: 13,
                  display: "flex", alignItems: "center", gap: 6,
                }}>
                  ler agora
                  <Icon name="arrow-right" size={14} color={ST.oliveDeep} strokeWidth={2.2} />
                </button>
              </div>
            </div>
          </div>
          <TornDivider color={ST.cream} />
        </div>

        {/* LIBRARY scroll */}
        <div style={{ flex: 1, overflowY: "auto", padding: "16px 24px 16px" }}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline", marginBottom: 14 }}>
            <h2 style={{
              fontFamily: "'Fraunces', serif", fontWeight: 500, fontSize: 22, color: ST.oliveDeep,
              margin: 0, letterSpacing: -0.4,
            }}>
              biblioteca <em style={{ fontStyle: "italic", color: ST.amberDeep, fontWeight: 400 }}>de histórias</em>
            </h2>
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
            {stories.map((s, i) => (
              <div key={i} style={{
                background: ST.creamDeep,
                borderRadius: 14,
                padding: 12,
                display: "flex", alignItems: "center", gap: 12,
                border: `1.4px solid ${ST.olive}1a`,
              }}>
                <div style={{
                  width: 56, height: 64,
                  background: `repeating-linear-gradient(135deg, ${ST[s.tone] || ST.butter} 0 8px, rgba(168,111,50,0.10) 8px 9px)`,
                  borderRadius: 6,
                  display: "flex", alignItems: "center", justifyContent: "center",
                  fontSize: 26,
                  border: `1.4px solid ${ST.cream}`,
                  boxShadow: "0 4px 10px -6px rgba(63,79,46,0.4)",
                  transform: `rotate(${i % 2 === 0 ? -2 : 2}deg)`,
                  flexShrink: 0,
                }}>
                  {s.emoji}
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ display: "flex", alignItems: "center", gap: 6 }}>
                    <Tag>{s.tag}</Tag>
                    <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 9.5, color: ST.olive, opacity: 0.55 }}>· {s.duration}</span>
                  </div>
                  <div style={{ fontFamily: "'Fraunces', serif", fontSize: 17, color: ST.oliveDeep, fontWeight: 500, marginTop: 2, letterSpacing: -0.3 }}>
                    {s.title}
                  </div>
                </div>
                <button style={{
                  background: ST.cream, border: `1.4px solid ${ST.olive}33`, borderRadius: 999,
                  padding: 8, display: "flex", alignItems: "center", justifyContent: "center",
                }}>
                  <Icon name="arrow-right" size={14} color={ST.oliveDeep} strokeWidth={2.2} />
                </button>
              </div>
            ))}
          </div>
        </div>
      </CreamScreen>
    </PhoneScreen>
  );
}

window.StoriesA = StoriesA;
