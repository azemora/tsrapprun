/**
 * Calendar — calendário com feriados BR + datas marcadas (mesversários, eventos)
 */

const { Icon, colors: CL, PaperBG, Stitched, Stamp, PhoneScreen, Placeholder } = window.TSR;
const { CreamScreen, TabBar, ScreenHeader, Tag, Polaroid } = window.TSR_Chrome;

function CalendarA() {
  // April 2025: começa numa terça
  const days = Array.from({ length: 30 }, (_, i) => i + 1);
  const firstDayOffset = 2; // April 2025 starts on Tuesday (Mon=0)
  const cells = Array.from({ length: firstDayOffset }, () => null).concat(days);

  const events = {
    2: { type: "mesversario", label: "7 meses" },
    8: { type: "feriado", label: "tiradentes" },
    12: { type: "evento", label: "passeio" },
    21: { type: "feriado", label: "tiradentes" },
  };

  const today = 4;

  const upcoming = [
    { date: "abr 12", title: "passeio no parque", kind: "evento", color: CL.sage },
    { date: "abr 21", title: "Tiradentes", kind: "feriado", color: CL.amber },
    { date: "mai 02", title: "8 meses ✦", kind: "mesversário", color: CL.amberDeep },
  ];

  return (
    <PhoneScreen bg={CL.cream}>
      <CreamScreen>
        <ScreenHeader
          chapter="vol. 01 — calendário"
          title={<>abril <em style={{ fontStyle: "italic", color: CL.amberDeep, fontWeight: 300 }}>2025</em></>}
          right={
            <div style={{ display: "flex", gap: 4 }}>
              <button style={{ background: "transparent", border: "none", padding: 4 }}>
                <Icon name="chevron-left" size={18} color={CL.oliveDeep} />
              </button>
              <button style={{ background: "transparent", border: "none", padding: 4 }}>
                <Icon name="chevron-right" size={18} color={CL.oliveDeep} />
              </button>
            </div>
          }
        />

        {/* calendar grid */}
        <div style={{ padding: "0 22px 12px", flexShrink: 0 }}>
          {/* weekdays */}
          <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 2, marginBottom: 6 }}>
            {["s", "t", "q", "q", "s", "s", "d"].map((d, i) => (
              <div key={i} style={{ textAlign: "center", fontFamily: "'JetBrains Mono', monospace", fontSize: 9.5, color: CL.olive, opacity: 0.6, letterSpacing: 1.4, textTransform: "uppercase" }}>
                {d}
              </div>
            ))}
          </div>

          {/* days */}
          <div style={{ display: "grid", gridTemplateColumns: "repeat(7, 1fr)", gap: 4 }}>
            {cells.map((day, i) => {
              if (day === null) return <div key={i} style={{ aspectRatio: "1" }} />;
              const ev = events[day];
              const isToday = day === today;
              const bg = isToday ? CL.oliveDeep
                : ev?.type === "mesversario" ? CL.butter
                : ev?.type === "feriado" ? CL.peach
                : ev?.type === "evento" ? CL.sageMist
                : "transparent";
              const color = isToday ? CL.cream : CL.oliveDeep;
              return (
                <div key={i} style={{
                  aspectRatio: "1",
                  background: bg,
                  borderRadius: 10,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  flexDirection: "column",
                  position: "relative",
                  border: ev && !isToday ? `1.2px dashed ${CL.olive}66` : "none",
                }}>
                  <span style={{ fontFamily: "'Fraunces', serif", fontSize: 15, color, fontWeight: isToday ? 600 : 500 }}>
                    {day}
                  </span>
                  {ev && !isToday && (
                    <div style={{ width: 4, height: 4, borderRadius: 2, background: CL.amberDeep, marginTop: 1 }} />
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* legend */}
        <div style={{ padding: "0 24px 8px", display: "flex", gap: 14, flexWrap: "wrap" }}>
          {[
            { color: CL.butter, label: "mesversário" },
            { color: CL.peach, label: "feriado" },
            { color: CL.sageMist, label: "evento" },
          ].map(l => (
            <div key={l.label} style={{ display: "flex", alignItems: "center", gap: 6 }}>
              <div style={{ width: 10, height: 10, borderRadius: 3, background: l.color, border: `1px solid ${CL.olive}33` }} />
              <span style={{ fontFamily: "'Inter', sans-serif", fontSize: 11, color: CL.olive, opacity: 0.8 }}>{l.label}</span>
            </div>
          ))}
        </div>

        {/* upcoming list */}
        <div style={{ flex: 1, padding: "12px 24px 16px", overflowY: "auto" }}>
          <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 10 }}>
            <div style={{ width: 14, height: 1, background: CL.olive, opacity: 0.4 }} />
            <Tag>próximos</Tag>
            <div style={{ flex: 1, height: 1, background: CL.olive, opacity: 0.18 }} />
          </div>
          <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
            {upcoming.map((u, i) => (
              <div key={i} style={{
                background: CL.creamDeep,
                borderRadius: 14,
                padding: "12px 14px",
                display: "flex", alignItems: "center", gap: 12,
                border: `1.4px solid ${CL.olive}1a`,
              }}>
                <div style={{
                  width: 44, minWidth: 44, height: 44,
                  background: u.color + "33",
                  borderRadius: 10,
                  border: `1.4px dashed ${u.color}`,
                  display: "flex", alignItems: "center", justifyContent: "center",
                  fontFamily: "'JetBrains Mono', monospace", fontSize: 9.5, color: u.color, letterSpacing: 0.5,
                  textAlign: "center", lineHeight: 1.1,
                }}>
                  {u.date}
                </div>
                <div style={{ flex: 1 }}>
                  <Tag color={u.color}>{u.kind}</Tag>
                  <div style={{ fontFamily: "'Fraunces', serif", fontSize: 16, color: CL.oliveDeep, fontWeight: 500, marginTop: 2, letterSpacing: -0.3 }}>
                    {u.title}
                  </div>
                </div>
                <Icon name="chevron-right" size={18} color={CL.olive} />
              </div>
            ))}
          </div>
        </div>

        <TabBar active="calendar" />
      </CreamScreen>
    </PhoneScreen>
  );
}

window.CalendarA = CalendarA;
