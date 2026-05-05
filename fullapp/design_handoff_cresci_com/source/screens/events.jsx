/**
 * Events: lista, criar (camera+naming), photo grid (event detail), photo viewer
 */

const { Icon, colors: EV, PaperBG, Stitched, Stamp, PhoneScreen, Placeholder, Blob } = window.TSR;
const { CreamScreen, SageScreen, TabBar, ScreenHeader, Tag, Polaroid, TornDivider, PrimaryButton } = window.TSR_Chrome;

// ─────────────────────── Event List ───────────────────────
function EventListA() {
  const events = [
    { title: "passeio no parque", date: "12 abr", count: 24, tone: "sage", emoji: "🌳" },
    { title: "primeiro Natal", date: "25 dez 2024", count: 47, tone: "peach", emoji: "🎄" },
    { title: "festa de 6 meses", date: "2 mar 2025", count: 38, tone: "butter", emoji: "🎉" },
    { title: "viagem à praia", date: "14 fev 2025", count: 62, tone: "sky", emoji: "🌊" },
    { title: "consulta pediatra", date: "8 fev 2025", count: 4, tone: "lilac", emoji: "🩺" },
  ];

  return (
    <PhoneScreen bg={EV.cream}>
      <CreamScreen>
        <ScreenHeader
          chapter="vol. 01 — eventos"
          title={<>seus <em style={{ fontStyle: "italic", color: EV.amberDeep, fontWeight: 300 }}>eventos</em></>}
          subtitle="momentos especiais com várias fotos"
          right={
            <button style={{ background: EV.oliveDeep, color: EV.cream, border: "none", borderRadius: 14, padding: "6px 12px", display: "flex", alignItems: "center", gap: 6, fontFamily: "'Fraunces', serif", fontSize: 13, fontWeight: 600 }}>
              <Icon name="plus" size={14} color={EV.cream} strokeWidth={2.4} />
              novo
            </button>
          }
        />

        <div style={{ flex: 1, overflowY: "auto", padding: "0 22px 18px" }}>
          {/* date sash */}
          <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "0 0 8px" }}>
            <div style={{ width: 14, height: 1, background: EV.olive, opacity: 0.4 }} />
            <Tag>2025</Tag>
            <div style={{ flex: 1, height: 1, background: EV.olive, opacity: 0.18 }} />
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
            {events.slice(0, 3).map((e, i) => (
              <button key={i} style={{
                background: EV.creamDeep,
                border: `1.4px solid ${EV.olive}1f`,
                borderRadius: 16,
                padding: 12,
                display: "flex",
                alignItems: "center",
                gap: 12,
                textAlign: "left",
              }}>
                {/* photo stack mini */}
                <div style={{ position: "relative", width: 76, height: 80, flexShrink: 0 }}>
                  <Stitched color={EV.cream} radius={4} padding={4} bg={EV.cream} style={{ position: "absolute", top: 6, left: 8, transform: "rotate(-6deg)", width: 64, height: 70 }}>
                    <Placeholder label="" tone={e.tone} h="100%" radius={2} />
                  </Stitched>
                  <Stitched color={EV.cream} radius={4} padding={4} bg={EV.cream} style={{ position: "absolute", top: 0, right: 0, transform: "rotate(6deg)", width: 60, height: 66 }}>
                    <div style={{ width: "100%", height: "100%", background: `repeating-linear-gradient(135deg, ${EV[e.tone]} 0 6px, rgba(168,111,50,0.10) 6px 7px)`, borderRadius: 2, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 22 }}>
                      {e.emoji}
                    </div>
                  </Stitched>
                </div>
                <div style={{ flex: 1 }}>
                  <Tag>{e.date}</Tag>
                  <div style={{ fontFamily: "'Fraunces', serif", fontSize: 17, color: EV.oliveDeep, fontWeight: 500, marginTop: 2, letterSpacing: -0.3 }}>
                    {e.title}
                  </div>
                  <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: EV.olive, opacity: 0.6, marginTop: 4 }}>
                    {e.count} fotos
                  </div>
                </div>
                <Icon name="chevron-right" size={18} color={EV.olive} />
              </button>
            ))}
          </div>

          <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "20px 0 8px" }}>
            <div style={{ width: 14, height: 1, background: EV.olive, opacity: 0.4 }} />
            <Tag>2024</Tag>
            <div style={{ flex: 1, height: 1, background: EV.olive, opacity: 0.18 }} />
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
            {events.slice(3).map((e, i) => (
              <button key={i} style={{
                background: EV.creamDeep,
                border: `1.4px solid ${EV.olive}1f`,
                borderRadius: 16,
                padding: 12,
                display: "flex",
                alignItems: "center",
                gap: 12,
                textAlign: "left",
              }}>
                <div style={{ position: "relative", width: 76, height: 80, flexShrink: 0 }}>
                  <Stitched color={EV.cream} radius={4} padding={4} bg={EV.cream} style={{ position: "absolute", top: 6, left: 8, transform: "rotate(-6deg)", width: 64, height: 70 }}>
                    <Placeholder label="" tone={e.tone} h="100%" radius={2} />
                  </Stitched>
                  <Stitched color={EV.cream} radius={4} padding={4} bg={EV.cream} style={{ position: "absolute", top: 0, right: 0, transform: "rotate(6deg)", width: 60, height: 66 }}>
                    <div style={{ width: "100%", height: "100%", background: `repeating-linear-gradient(135deg, ${EV[e.tone]} 0 6px, rgba(168,111,50,0.10) 6px 7px)`, borderRadius: 2, display: "flex", alignItems: "center", justifyContent: "center", fontSize: 22 }}>
                      {e.emoji}
                    </div>
                  </Stitched>
                </div>
                <div style={{ flex: 1 }}>
                  <Tag>{e.date}</Tag>
                  <div style={{ fontFamily: "'Fraunces', serif", fontSize: 17, color: EV.oliveDeep, fontWeight: 500, marginTop: 2, letterSpacing: -0.3 }}>
                    {e.title}
                  </div>
                  <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 10, color: EV.olive, opacity: 0.6, marginTop: 4 }}>
                    {e.count} fotos
                  </div>
                </div>
                <Icon name="chevron-right" size={18} color={EV.olive} />
              </button>
            ))}
          </div>
        </div>

        <TabBar active="events" />
      </CreamScreen>
    </PhoneScreen>
  );
}

// ─────────────────────── Event Camera ───────────────────────
function EventCameraA() {
  return (
    <PhoneScreen bg="#000" statusColor={EV.cream} homeColor={EV.cream}>
      <div style={{ flex: 1, position: "relative", background: "#1a1a1a", display: "flex", flexDirection: "column" }}>
        {/* viewfinder */}
        <div style={{ flex: 1, position: "relative", overflow: "hidden", background: `repeating-linear-gradient(45deg, #2a2a2a 0 12px, #252525 12px 13px)` }}>
          {/* corner brackets */}
          {[
            { top: 80, left: 30, rot: 0 },
            { top: 80, right: 30, rot: 90 },
            { bottom: 100, left: 30, rot: -90 },
            { bottom: 100, right: 30, rot: 180 },
          ].map((c, i) => (
            <svg key={i} width="32" height="32" viewBox="0 0 32 32" style={{ position: "absolute", ...c, transform: `rotate(${c.rot}deg)` }}>
              <path d="M2 12 L2 2 L12 2" stroke={EV.cream} strokeWidth="2" fill="none" />
            </svg>
          ))}
          {/* event title overlay */}
          <div style={{ position: "absolute", top: 24, left: 0, right: 0, display: "flex", justifyContent: "center" }}>
            <div style={{ background: "rgba(0,0,0,0.55)", border: `1.4px dashed ${EV.cream}66`, borderRadius: 999, padding: "8px 14px", display: "flex", alignItems: "center", gap: 8 }}>
              <Tag color={EV.cream} style={{ opacity: 0.7 }}>evento</Tag>
              <span style={{ fontFamily: "'Fraunces', serif", fontSize: 14, color: EV.cream, fontWeight: 500, fontStyle: "italic" }}>
                passeio no parque
              </span>
            </div>
          </div>
          {/* center placeholder text */}
          <div style={{ position: "absolute", top: "50%", left: "50%", transform: "translate(-50%, -50%)", textAlign: "center" }}>
            <div style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: EV.cream, opacity: 0.4, letterSpacing: 1.4, textTransform: "uppercase" }}>
              visor da câmera
            </div>
          </div>
          {/* photo count */}
          <div style={{ position: "absolute", bottom: 24, left: 24, background: "rgba(0,0,0,0.55)", borderRadius: 999, padding: "6px 10px" }}>
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 11, color: EV.cream, letterSpacing: 0.5 }}>
              ✦ 12 capturadas
            </span>
          </div>
        </div>
        {/* shutter strip */}
        <div style={{ background: "#0d0d0d", padding: "16px 24px 28px", display: "flex", alignItems: "center", justifyContent: "space-between", borderTop: `1px solid #ffffff15` }}>
          <button style={{ background: "#222", border: "none", borderRadius: 12, padding: 10, display: "flex", alignItems: "center", justifyContent: "center" }}>
            <Icon name="image" size={22} color={EV.cream} />
          </button>
          {/* shutter */}
          <button style={{
            width: 76, height: 76, borderRadius: "50%",
            background: EV.cream,
            border: `4px solid ${EV.butter}`,
            boxShadow: "0 0 0 3px #0d0d0d, 0 0 0 4px rgba(252,228,167,0.5)",
            display: "flex", alignItems: "center", justifyContent: "center",
          }}>
            <div style={{ width: 60, height: 60, borderRadius: "50%", background: EV.butter, display: "flex", alignItems: "center", justifyContent: "center" }}>
              <Icon name="camera" size={28} color={EV.amberDeep} strokeWidth={1.8} />
            </div>
          </button>
          <button style={{ background: "#222", border: "none", borderRadius: 12, padding: 10, display: "flex", alignItems: "center", justifyContent: "center" }}>
            <Icon name="check" size={22} color={EV.butter} strokeWidth={2.4} />
          </button>
        </div>
      </div>
    </PhoneScreen>
  );
}

// ─────────────────────── Event Naming ───────────────────────
function EventNamingA() {
  return (
    <PhoneScreen bg={EV.sage} statusColor={EV.cream} homeColor={EV.cream}>
      <SageScreen>
        <ScreenHeader
          chapter="novo evento — passo 2"
          title={<>como vamos chamar<br/><em style={{ fontStyle: "italic", color: EV.butter, fontWeight: 300 }}>esse momento</em>?</>}
          subtitle="dê um nome carinhoso. você pode editar depois."
          color={EV.cream}
          accent={EV.butter}
          onBack={() => {}}
        />

        <div style={{ flex: 1, padding: "0 24px 16px", display: "flex", flexDirection: "column", gap: 18 }}>
          {/* photo strip preview */}
          <div style={{ display: "flex", gap: 8, justifyContent: "center", paddingTop: 6 }}>
            {[
              { tone: "sage", rot: -6 },
              { tone: "butter", rot: 4 },
              { tone: "peach", rot: -3 },
            ].map((p, i) => (
              <Polaroid key={i} tone={p.tone} w={88} h={100} rotate={p.rot} caption={i === 1 ? "+ 9" : ""} />
            ))}
          </div>

          <div>
            <Tag color={EV.cream} style={{ opacity: 0.8 }}>nome do evento</Tag>
            <div style={{
              background: "rgba(245,239,224,0.12)",
              borderRadius: 14,
              padding: "16px 18px",
              border: `1.4px solid ${EV.cream}66`,
              marginTop: 8,
              display: "flex", alignItems: "center", gap: 10,
            }}>
              <span style={{ fontFamily: "'Fraunces', serif", fontSize: 20, color: EV.cream, fontWeight: 500, letterSpacing: -0.4 }}>
                passeio no parque
              </span>
              <span style={{ width: 1.5, height: 22, background: EV.butter, animation: "blink 1s infinite" }} />
            </div>
            <div style={{ fontFamily: "'Caveat', cursive", fontSize: 16, color: EV.butter, marginTop: 6, opacity: 0.95 }}>
              ✿ que dia bonito ✿
            </div>
          </div>

          <div>
            <Tag color={EV.cream} style={{ opacity: 0.8 }}>quando aconteceu</Tag>
            <div style={{
              background: "rgba(245,239,224,0.12)",
              borderRadius: 14,
              padding: "14px 18px",
              border: `1.4px solid ${EV.cream}55`,
              marginTop: 8,
              display: "flex", alignItems: "center", justifyContent: "space-between",
            }}>
              <span style={{ fontFamily: "'Fraunces', serif", fontSize: 16, color: EV.cream }}>
                hoje, 12 de abril
              </span>
              <Icon name="calendar" size={20} color={EV.cream} />
            </div>
          </div>

          <div style={{ flex: 1 }} />

          <button style={{
            background: EV.cream,
            color: EV.oliveDeep,
            border: "none",
            borderRadius: 18,
            padding: "16px 22px",
            fontSize: 15.5,
            fontWeight: 600,
            fontFamily: "'Fraunces', serif",
            display: "flex", alignItems: "center", justifyContent: "center", gap: 10,
            boxShadow: "0 1px 0 rgba(255,255,255,0.4) inset, 0 8px 18px -8px rgba(20,30,15,0.4)",
          }}>
            guardar evento
            <Icon name="check" size={18} color={EV.oliveDeep} strokeWidth={2.2} />
          </button>
        </div>
      </SageScreen>
    </PhoneScreen>
  );
}

// ─────────────────────── Photo Grid (event detail) ───────────────────────
function PhotoGridA() {
  const photos = ["peach", "sage", "butter", "sky", "lilac", "mint", "cream", "peach", "sage", "butter", "sky", "lilac"];
  return (
    <PhoneScreen bg={EV.cream}>
      <CreamScreen>
        <ScreenHeader
          chapter="evento · 12 abr — 24 fotos"
          title={<>passeio<br/><em style={{ fontStyle: "italic", color: EV.amberDeep, fontWeight: 300 }}>no parque</em></>}
          onBack={() => {}}
          right={
            <div style={{ display: "flex", gap: 6 }}>
              <button style={{ background: "transparent", border: "none", padding: 4 }}>
                <Icon name="share" size={20} color={EV.oliveDeep} />
              </button>
              <button style={{ background: "transparent", border: "none", padding: 4 }}>
                <Icon name="more" size={20} color={EV.oliveDeep} />
              </button>
            </div>
          }
        />

        {/* mosaic grid */}
        <div style={{ flex: 1, padding: "0 16px 12px", overflowY: "auto" }}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 6 }}>
            {photos.map((tone, i) => (
              <div key={i} style={{
                aspectRatio: i === 0 ? "1 / 1.4" : "1",
                gridRow: i === 0 ? "span 2" : "span 1",
                background: `repeating-linear-gradient(135deg, ${EV[tone]} 0 8px, rgba(168,111,50,0.10) 8px 9px)`,
                borderRadius: 10,
                position: "relative",
              }}>
                {i === 0 && (
                  <div style={{ position: "absolute", top: 6, left: 6, background: "rgba(255,255,255,0.85)", borderRadius: 999, padding: "3px 8px" }}>
                    <Tag>capa</Tag>
                  </div>
                )}
              </div>
            ))}
          </div>

          {/* event note (Caveat) */}
          <div style={{
            marginTop: 16,
            background: EV.creamDeep,
            border: `1.4px dashed ${EV.olive}55`,
            borderRadius: 14,
            padding: "14px 16px",
          }}>
            <Tag style={{ marginBottom: 6, display: "inline-block" }}>nota do dia</Tag>
            <div style={{ fontFamily: "'Caveat', cursive", fontSize: 19, color: EV.oliveDeep, lineHeight: 1.2 }}>
              dia mais lindo do ano. Manu adorou as folhas, ficou rindo do vento.
              precisamos voltar logo.
            </div>
          </div>
        </div>

        {/* footer action */}
        <div style={{ padding: "10px 24px 14px", borderTop: `1px solid ${EV.olive}1a`, background: EV.cream, display: "flex", gap: 10 }}>
          <button style={{
            flex: 1,
            background: EV.creamDeep, border: `1.4px solid ${EV.olive}33`, borderRadius: 14, padding: "12px",
            fontFamily: "'Fraunces', serif", fontSize: 14, fontWeight: 500, color: EV.oliveDeep,
            display: "flex", alignItems: "center", justifyContent: "center", gap: 8,
          }}>
            <Icon name="plus" size={16} color={EV.oliveDeep} strokeWidth={2.2} />
            adicionar fotos
          </button>
          <button style={{
            flex: 1,
            background: EV.oliveDeep, border: "none", borderRadius: 14, padding: "12px",
            fontFamily: "'Fraunces', serif", fontSize: 14, fontWeight: 600, color: EV.cream,
            display: "flex", alignItems: "center", justifyContent: "center", gap: 8,
          }}>
            <Icon name="book" size={16} color={EV.cream} strokeWidth={1.9} />
            ao livro
          </button>
        </div>
      </CreamScreen>
    </PhoneScreen>
  );
}

window.EventListA = EventListA;
window.EventCameraA = EventCameraA;
window.EventNamingA = EventNamingA;
window.PhotoGridA = PhotoGridA;
