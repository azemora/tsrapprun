/**
 * Memory Book — livro de memórias estilo álbum
 * Pretensão: layout de spread (duas páginas) com fotos, datas, notas Caveat
 */

const { Icon, colors: MB, PaperBG, Stitched, Stamp, PhoneScreen, Placeholder, Blob } = window.TSR;
const { CreamScreen, TabBar, ScreenHeader, Tag, Polaroid, TornDivider } = window.TSR_Chrome;

function MemoryBookA() {
  return (
    <PhoneScreen bg={MB.creamDeep}>
      <CreamScreen style={{ background: MB.creamDeep }}>
        <ScreenHeader
          chapter="livro de memórias"
          title={<>livro <em style={{ fontStyle: "italic", color: MB.amberDeep, fontWeight: 300 }}>da Manu</em></>}
          subtitle="cada página um capítulo. tocar para abrir."
          right={
            <button style={{ background: "transparent", border: "none", color: MB.olive }}>
              <Icon name="download" size={20} color={MB.olive} />
            </button>
          }
        />

        {/* book spread mockup */}
        <div style={{ flex: 1, padding: "0 18px 18px", overflowY: "auto" }}>
          <div style={{
            background: MB.cream,
            borderRadius: 14,
            padding: "16px 16px 20px",
            position: "relative",
            boxShadow: "0 1px 0 rgba(255,255,255,0.5) inset, 0 12px 30px -16px rgba(63,79,46,0.45), 0 0 0 1px rgba(63,79,46,0.08)",
            backgroundImage: `linear-gradient(90deg, transparent 49.5%, ${MB.olive}1f 49.5%, ${MB.olive}1f 50.5%, transparent 50.5%)`,
          }}>
            {/* chapter heading */}
            <div style={{ display: "flex", alignItems: "center", gap: 10, marginBottom: 16 }}>
              <Stamp size={48} color={MB.amberDeep} sub="capítulo" label="07" rotate={-6} />
              <div style={{ flex: 1 }}>
                <Tag>mês 07</Tag>
                <div style={{ fontFamily: "'Fraunces', serif", fontSize: 22, color: MB.oliveDeep, fontWeight: 500, lineHeight: 1, marginTop: 2, letterSpacing: -0.4 }}>
                  <em style={{ fontStyle: "italic", fontWeight: 300, color: MB.amberDeep }}>sete</em> meses
                </div>
              </div>
            </div>

            {/* spread grid: left page + right page */}
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 14 }}>
              {/* LEFT page */}
              <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                <Polaroid tone="peach" caption="22 mar" w="100%" h={120} rotate={-2} />
                <div style={{ fontFamily: "'Caveat', cursive", fontSize: 14, color: MB.oliveDeep, lineHeight: 1.15, padding: "0 4px" }}>
                  começou a engatinhar de costas, fica brava quando não chega.
                </div>
                <Polaroid tone="butter" caption="26 mar" w="100%" h={100} rotate={2.2} />
              </div>

              {/* RIGHT page */}
              <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
                <div style={{ fontFamily: "'Caveat', cursive", fontSize: 14, color: MB.oliveDeep, lineHeight: 1.15, padding: "0 4px" }}>
                  primeira papinha! batata-doce. abriu sorrisão.
                </div>
                <Polaroid tone="sage" caption="2 abr ✿" w="100%" h={110} rotate={-1.5} />
                {/* sticker */}
                <div style={{ alignSelf: "center", transform: "rotate(-12deg)" }}>
                  <Icon name="leaf" size={26} color={MB.sage} />
                </div>
                <div style={{ fontFamily: "'Caveat', cursive", fontSize: 14, color: MB.amberDeep, lineHeight: 1.15, padding: "0 4px", textAlign: "right" }}>
                  ✦ 7 meses redondos ✦
                </div>
              </div>
            </div>

            {/* page number */}
            <div style={{ display: "flex", justifyContent: "space-between", marginTop: 18, padding: "0 4px" }}>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 9, color: MB.olive, opacity: 0.55, letterSpacing: 1 }}>14</span>
              <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 9, color: MB.olive, opacity: 0.55, letterSpacing: 1 }}>15</span>
            </div>
          </div>

          {/* navigation between chapters */}
          <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginTop: 14, padding: "0 4px" }}>
            <button style={{ background: MB.cream, border: `1.4px solid ${MB.olive}22`, borderRadius: 999, padding: "10px 14px", display: "flex", alignItems: "center", gap: 6, fontFamily: "'Fraunces', serif", fontSize: 13, color: MB.oliveDeep, fontWeight: 500 }}>
              <Icon name="chevron-left" size={14} color={MB.oliveDeep} />
              <span>cap. 06</span>
            </button>
            <div style={{ display: "flex", gap: 4, alignItems: "center" }}>
              {[1,2,3,4,5,6,7].map(n => (
                <div key={n} style={{ width: n === 7 ? 18 : 4, height: 4, borderRadius: 2, background: n === 7 ? MB.amberDeep : MB.olive + "33" }} />
              ))}
            </div>
            <button style={{ background: MB.oliveDeep, border: "none", borderRadius: 999, padding: "10px 14px", display: "flex", alignItems: "center", gap: 6, fontFamily: "'Fraunces', serif", fontSize: 13, color: MB.cream, fontWeight: 500 }}>
              <span>cap. 08</span>
              <Icon name="chevron-right" size={14} color={MB.cream} />
            </button>
          </div>
        </div>

        <TabBar active="book" bg={MB.creamDeep} />
      </CreamScreen>
    </PhoneScreen>
  );
}

window.MemoryBookA = MemoryBookA;
