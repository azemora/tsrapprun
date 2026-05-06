/**
 * Home (início) — dashboard principal
 * Mostra: bebê, idade, próximos eventos, registros recentes, atalhos
 */

const { Icon, colors: HC, PaperBG, Stitched, Stamp, PhoneScreen, Placeholder, Blob } = window.TSR;
const { CreamScreen, TabBar, MomentCard, ScreenHeader, Tag, Polaroid, TornDivider } = window.TSR_Chrome;

function HomeA() {
  return (
    <PhoneScreen bg={HC.cream}>
      <CreamScreen>
        {/* TOP — sage band com bebê */}
        <div style={{ background: HC.sage, position: "relative", overflow: "hidden", flexShrink: 0 }}>
          <PaperBG color="transparent" style={{ position: "absolute", inset: 0, opacity: 0.5 }} />
          <Blob size={220} color={HC.oliveDeep} style={{ position: "absolute", top: -90, right: -80, opacity: 0.15 }} />

          <div style={{ position: "relative", padding: "10px 24px 22px" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 14 }}>
              <Tag color={HC.cream} style={{ opacity: 0.8 }}>cresci·com — início</Tag>
              <button style={{ background: "transparent", border: "none", padding: 4, color: HC.cream }}>
                <Icon name="settings" size={22} color={HC.cream} />
              </button>
            </div>

            <div style={{ display: "flex", alignItems: "center", gap: 14 }}>
              <div style={{ position: "relative" }}>
                <Stitched color={HC.cream} radius={100} padding={5} bg={HC.cream} style={{ width: 78, height: 78, transform: "rotate(-4deg)" }}>
                  <Placeholder label="" tone="peach" w="100%" h="100%" radius={50} />
                </Stitched>
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 9.5, color: HC.cream, opacity: 0.7, letterSpacing: 1.4, textTransform: "uppercase" }}>
                  bom dia ✦
                </div>
                <h1 style={{
                  fontFamily: "'Fraunces', serif",
                  fontWeight: 400,
                  fontSize: 28,
                  lineHeight: 1.0,
                  color: HC.cream,
                  margin: "2px 0 0",
                  letterSpacing: -0.6,
                }}>
                  Manu, <em style={{ fontStyle: "italic", color: HC.butter, fontWeight: 300 }}>7 meses</em>
                </h1>
                <div style={{ fontFamily: "'Caveat', cursive", fontSize: 16, color: HC.cream, opacity: 0.85, marginTop: 2 }}>
                  ~ 218 dias de vida
                </div>
              </div>
            </div>
          </div>
          <TornDivider color={HC.cream} />
        </div>

        {/* BODY scrollable */}
        <div style={{ flex: 1, overflowY: "auto", padding: "20px 24px 16px", display: "flex", flexDirection: "column", gap: 22 }}>
          {/* destaque: próximo mesversário */}
          <div style={{
            background: HC.butter,
            borderRadius: 18,
            padding: "16px 18px",
            display: "flex",
            alignItems: "center",
            gap: 14,
            position: "relative",
            overflow: "hidden",
            border: `1.4px dashed ${HC.amberDeep}55`,
          }}>
            <div style={{ position: "absolute", right: -12, top: -12 }}>
              <Stamp size={56} color={HC.amberDeep} sub="próx." label="8M" rotate={10} />
            </div>
            <Icon name="cake" size={32} color={HC.amberDeep} strokeWidth={1.7} />
            <div style={{ flex: 1, paddingRight: 56 }}>
              <Tag color={HC.amberDeep}>em 12 dias</Tag>
              <div style={{ fontFamily: "'Fraunces', serif", fontSize: 18, color: HC.amberDeep, fontWeight: 500, marginTop: 2, letterSpacing: -0.3 }}>
                mesversário de 8 meses
              </div>
            </div>
          </div>

          {/* registros recentes */}
          <div>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline", marginBottom: 12 }}>
              <h2 style={{
                fontFamily: "'Fraunces', serif", fontWeight: 500, fontSize: 22, color: HC.oliveDeep,
                margin: 0, letterSpacing: -0.4,
              }}>
                últimos <em style={{ fontStyle: "italic", color: HC.amberDeep, fontWeight: 400 }}>registros</em>
              </h2>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: HC.olive, opacity: 0.7 }}>
                ver todos →
              </span>
            </div>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
              <MomentCard kind="DAILY" title="primeira papinha" date="hoje" note="adorou batata-doce!" rotate={-1.5} mini />
              <MomentCard kind="WEEKLY" title="semana 31" date="2d" note="já se vira sozinha" rotate={1.2} mini />
            </div>
          </div>

          {/* atalhos */}
          <div>
            <Tag style={{ display: "inline-block", marginBottom: 10 }}>atalhos</Tag>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
              {[
                { icon: "pencil", label: "novo registro", bg: HC.sageMist, color: HC.olive },
                { icon: "camera", label: "novo evento", bg: HC.peach, color: HC.amberDeep },
                { icon: "book", label: "livro de memórias", bg: HC.creamDeep, color: HC.oliveDeep },
                { icon: "calendar", label: "calendário", bg: HC.butter, color: HC.amberDeep },
              ].map((s, i) => (
                <button key={i} style={{
                  background: s.bg, border: "none", borderRadius: 14, padding: "14px 14px",
                  display: "flex", alignItems: "center", gap: 10,
                  fontFamily: "'Fraunces', serif", fontWeight: 500, fontSize: 14, color: s.color,
                  textAlign: "left", letterSpacing: -0.2,
                }}>
                  <Icon name={s.icon} size={20} color={s.color} />
                  <span>{s.label}</span>
                </button>
              ))}
            </div>
          </div>
        </div>

        <TabBar active="home" />
      </CreamScreen>
    </PhoneScreen>
  );
}

window.HomeA = HomeA;
