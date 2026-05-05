/**
 * Moments List — lista de registros (diário, semanal, mesversário, etc)
 * Conteúdo real:
 *   tipos: DAILY, WEEKLY, MESVERSARIO, PREGNANCY_WEEK, DAY_OF_LIFE
 *   filtro por tipo, ordenação por data
 */

const { Icon, colors: ML, PaperBG, Stitched, Stamp, PhoneScreen, Placeholder, Blob } = window.TSR;
const { CreamScreen, TabBar, MomentCard, ScreenHeader, Tag, Polaroid, TornDivider, PrimaryButton } = window.TSR_Chrome;

function MomentsListA() {
  const moments = [
    { kind: "DAILY", title: "primeira papinha", date: "hoje", note: "adorou batata-doce!", photo: "peach", rotate: -1.4 },
    { kind: "MESVERSARIO", title: "7 meses", date: "ter, 2 abr", note: "já senta sozinha", photo: "butter", rotate: 1.0 },
    { kind: "WEEKLY", title: "semana 31", date: "26 mar", note: "novos sons!", photo: "sage", rotate: -0.8 },
    { kind: "DAILY", title: "passeio no parque", date: "20 mar", note: "primeiras folhas", photo: "mint", rotate: 1.2 },
    { kind: "DAY_OF_LIFE", title: "dia 200", date: "18 mar", note: "marco redondo ✦", photo: "lilac", rotate: -0.6 },
    { kind: "DAILY", title: "soninho da tarde", date: "15 mar", note: "", photo: "sky", rotate: 1.4 },
  ];

  const filters = [
    { id: "all", label: "todos", count: 24 },
    { id: "daily", label: "diário", count: 12 },
    { id: "weekly", label: "semanal", count: 6 },
    { id: "mes", label: "mesversário", count: 6 },
  ];

  return (
    <PhoneScreen bg={ML.cream}>
      <CreamScreen>
        <ScreenHeader
          chapter="vol. 01 — registros"
          title={<>seus <em style={{ fontStyle: "italic", color: ML.amberDeep, fontWeight: 300 }}>registros</em></>}
          right={
            <button style={{ background: ML.oliveDeep, color: ML.cream, border: "none", borderRadius: 14, padding: "6px 12px", display: "flex", alignItems: "center", gap: 6, fontFamily: "'Fraunces', serif", fontSize: 13, fontWeight: 600 }}>
              <Icon name="plus" size={14} color={ML.cream} strokeWidth={2.4} />
              novo
            </button>
          }
        />

        {/* filter chips */}
        <div style={{ padding: "0 24px 14px", flexShrink: 0 }}>
          <div style={{ display: "flex", gap: 8, overflowX: "auto", paddingBottom: 4 }}>
            {filters.map((f, i) => (
              <button key={f.id} style={{
                flexShrink: 0,
                background: i === 0 ? ML.oliveDeep : ML.creamDeep,
                color: i === 0 ? ML.cream : ML.olive,
                border: i === 0 ? "none" : `1.4px solid ${ML.olive}22`,
                borderRadius: 999,
                padding: "8px 14px",
                fontSize: 12.5,
                fontFamily: "'Fraunces', serif",
                fontWeight: 500,
                display: "flex",
                alignItems: "center",
                gap: 6,
                letterSpacing: -0.1,
              }}>
                {f.label}
                <span style={{ opacity: 0.55, fontSize: 10, fontFamily: "'JetBrains Mono', monospace" }}>{f.count}</span>
              </button>
            ))}
          </div>
        </div>

        {/* date sash */}
        <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "0 24px 6px" }}>
          <div style={{ width: 14, height: 1, background: ML.olive, opacity: 0.4 }} />
          <Tag>esta semana</Tag>
          <div style={{ flex: 1, height: 1, background: ML.olive, opacity: 0.18 }} />
        </div>

        {/* grid (scrollable) */}
        <div style={{ flex: 1, overflowY: "auto", padding: "10px 20px 16px" }}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
            {moments.slice(0, 2).map((m, i) => (
              <MomentCard key={i} {...m} mini />
            ))}
          </div>

          <div style={{ display: "flex", alignItems: "center", gap: 10, padding: "20px 4px 6px" }}>
            <div style={{ width: 14, height: 1, background: ML.olive, opacity: 0.4 }} />
            <Tag>mês passado</Tag>
            <div style={{ flex: 1, height: 1, background: ML.olive, opacity: 0.18 }} />
          </div>

          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14, marginTop: 4 }}>
            {moments.slice(2).map((m, i) => (
              <MomentCard key={i} {...m} mini />
            ))}
          </div>
        </div>

        <TabBar active="moments" />
      </CreamScreen>
    </PhoneScreen>
  );
}

window.MomentsListA = MomentsListA;
