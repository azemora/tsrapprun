/**
 * Moment Registration — criar registro novo
 * Conteúdo: foto, título, nota, data, tipo
 */

const { Icon, colors: MR, PaperBG, Stitched, Stamp, PhoneScreen, Placeholder, Blob } = window.TSR;
const { CreamScreen, ScreenHeader, Tag, Polaroid, PrimaryButton } = window.TSR_Chrome;

function MomentRegistrationA() {
  return (
    <PhoneScreen bg={MR.cream}>
      <CreamScreen>
        <ScreenHeader
          chapter="novo registro"
          title={<>conte um <em style={{ fontStyle: "italic", color: MR.amberDeep, fontWeight: 300 }}>momento</em>.</>}
          subtitle="o que aconteceu hoje? não precisa ser perfeito."
          onBack={() => {}}
          right={
            <Tag color={MR.olive} bg={MR.creamDeep}>rascunho</Tag>
          }
        />

        <div style={{ flex: 1, padding: "0 24px 16px", overflowY: "auto", display: "flex", flexDirection: "column", gap: 18 }}>
          {/* upload card — polaroid feel */}
          <div style={{ display: "flex", justifyContent: "center", padding: "8px 0" }}>
            <div style={{ position: "relative", transform: "rotate(-3deg)" }}>
              <Stitched color={MR.olive} radius={4} padding={10} bg={MR.cream} style={{ width: 200, height: 220 }}>
                <div style={{
                  width: "100%", height: 175,
                  background: `repeating-linear-gradient(135deg, ${MR.creamDeep} 0 8px, rgba(168,111,50,0.10) 8px 9px)`,
                  borderRadius: 2,
                  display: "flex", alignItems: "center", justifyContent: "center", flexDirection: "column", gap: 8,
                  color: MR.amberDeep,
                }}>
                  <Icon name="camera" size={32} color={MR.amberDeep} />
                  <span style={{ fontFamily: "'Fraunces', serif", fontSize: 13, fontWeight: 500 }}>tocar para adicionar foto</span>
                </div>
                <div style={{ textAlign: "center", fontFamily: "'Caveat', cursive", fontSize: 17, color: MR.oliveDeep, marginTop: 6 }}>
                  ___________
                </div>
              </Stitched>
              {/* tape */}
              <div style={{ position: "absolute", top: -10, left: "50%", transform: "translateX(-50%) rotate(-4deg)", width: 80, height: 22, background: "rgba(217,178,92,0.55)", borderRadius: 1 }} />
            </div>
          </div>

          {/* type pills */}
          <div>
            <Tag style={{ display: "inline-block", marginBottom: 8 }}>tipo de registro</Tag>
            <div style={{ display: "flex", gap: 8, flexWrap: "wrap" }}>
              {[
                { id: "daily", label: "diário", icon: "pencil", active: true },
                { id: "weekly", label: "semanal", icon: "sun" },
                { id: "mes", label: "mesversário", icon: "cake" },
                { id: "milestone", label: "marco", icon: "star" },
              ].map(t => (
                <button key={t.id} style={{
                  background: t.active ? MR.oliveDeep : MR.creamDeep,
                  color: t.active ? MR.cream : MR.olive,
                  border: t.active ? "none" : `1.4px solid ${MR.olive}22`,
                  borderRadius: 999,
                  padding: "8px 12px",
                  fontSize: 12.5,
                  fontFamily: "'Fraunces', serif",
                  fontWeight: 500,
                  display: "flex", alignItems: "center", gap: 6,
                }}>
                  <Icon name={t.icon} size={14} color={t.active ? MR.cream : MR.olive} />
                  {t.label}
                </button>
              ))}
            </div>
          </div>

          {/* title input */}
          <div>
            <Tag style={{ display: "inline-block", marginBottom: 8 }}>título</Tag>
            <div style={{
              background: MR.creamDeep,
              borderRadius: 14,
              padding: "14px 16px",
              border: `1.4px solid ${MR.olive}22`,
            }}>
              <span style={{ fontFamily: "'Fraunces', serif", fontSize: 18, color: MR.oliveDeep, fontWeight: 500, letterSpacing: -0.3 }}>
                primeira papinha
              </span>
              <span style={{ width: 1.5, height: 18, background: MR.amber, display: "inline-block", marginLeft: 2, verticalAlign: "middle" }} />
            </div>
          </div>

          {/* note (Caveat handwritten feel) */}
          <div>
            <Tag style={{ display: "inline-block", marginBottom: 8 }}>uma notinha (opcional)</Tag>
            <div style={{
              background: MR.creamDeep,
              borderRadius: 14,
              padding: "14px 16px",
              minHeight: 90,
              border: `1.4px solid ${MR.olive}22`,
              backgroundImage: `repeating-linear-gradient(transparent 0 27px, ${MR.olive}1a 27px 28px)`,
              backgroundPosition: "0 8px",
            }}>
              <span style={{ fontFamily: "'Caveat', cursive", fontSize: 20, color: MR.oliveDeep, lineHeight: "28px" }}>
                adorou batata-doce, abriu sorrisão
              </span>
            </div>
          </div>

          {/* date */}
          <div style={{ display: "flex", gap: 10 }}>
            <div style={{ flex: 1 }}>
              <Tag style={{ display: "inline-block", marginBottom: 8 }}>data</Tag>
              <div style={{ background: MR.creamDeep, borderRadius: 14, padding: "12px 14px", border: `1.4px solid ${MR.olive}22`, display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                <span style={{ fontFamily: "'Fraunces', serif", fontSize: 14, color: MR.oliveDeep }}>hoje, 4 abr</span>
                <Icon name="calendar" size={18} color={MR.olive} />
              </div>
            </div>
            <div style={{ flex: 1 }}>
              <Tag style={{ display: "inline-block", marginBottom: 8 }}>marcadores</Tag>
              <div style={{ background: MR.creamDeep, borderRadius: 14, padding: "12px 14px", border: `1.4px solid ${MR.olive}22`, display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                <span style={{ fontFamily: "'Fraunces', serif", fontSize: 14, color: MR.oliveDeep, fontStyle: "italic" }}>+ adicionar</span>
                <Icon name="plus" size={16} color={MR.olive} />
              </div>
            </div>
          </div>
        </div>

        <div style={{ padding: "10px 24px 14px", borderTop: `1px solid ${MR.olive}1a` }}>
          <PrimaryButton onColor="cream" icon="check">guardar momento</PrimaryButton>
        </div>
      </CreamScreen>
    </PhoneScreen>
  );
}

window.MomentRegistrationA = MomentRegistrationA;
