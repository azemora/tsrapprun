/**
 * Mesversário — celebração mensal do bebê
 * Mensagens por mês (1-12), foto, idade, marco do mês
 */

const { Icon, colors: MV, PaperBG, Stitched, Stamp, PhoneScreen, Placeholder, Blob } = window.TSR;
const { SageScreen, ScreenHeader, Tag, Polaroid, TornDivider, PrimaryButton } = window.TSR_Chrome;

function MesversarioA() {
  // mensagem do mês 7
  const monthMessages = {
    7: "sete meses do mais doce dos sorrisos.\nvocê descobriu o mundo dos sabores e nada vai ser igual.",
  };
  const month = 7;

  return (
    <PhoneScreen bg={MV.sage} statusColor={MV.cream} homeColor={MV.cream}>
      <SageScreen>
        {/* eyebrow header */}
        <div style={{ padding: "10px 24px 6px", display: "flex", alignItems: "center", justifyContent: "space-between" }}>
          <button style={{ background: "transparent", border: "none", padding: 4 }}>
            <Icon name="chevron-left" size={22} color={MV.cream} />
          </button>
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <Icon name="cake" size={14} color={MV.cream} strokeWidth={1.8} />
            <Tag color={MV.cream} style={{ opacity: 0.85 }}>mesversário</Tag>
          </div>
          <button style={{ background: "transparent", border: "none", padding: 4 }}>
            <Icon name="share" size={20} color={MV.cream} />
          </button>
        </div>

        {/* HERO — number + photo */}
        <div style={{ flex: 1, padding: "8px 24px 16px", display: "flex", flexDirection: "column", overflowY: "auto" }}>
          <div style={{ position: "relative", marginTop: 4, marginBottom: 22 }}>
            {/* huge number */}
            <div style={{
              fontFamily: "'Fraunces', serif",
              fontWeight: 300,
              fontSize: 200,
              lineHeight: 0.85,
              color: MV.cream,
              letterSpacing: -8,
              fontStyle: "italic",
              fontVariationSettings: "'opsz' 144",
              position: "relative",
              zIndex: 0,
            }}>
              7
            </div>
            {/* word + meses */}
            <div style={{ position: "absolute", right: 4, top: 30, textAlign: "right" }}>
              <Tag color={MV.cream} style={{ opacity: 0.7 }}>meses</Tag>
              <div style={{ fontFamily: "'Caveat', cursive", fontSize: 26, color: MV.butter, marginTop: 2, lineHeight: 1 }}>
                sete!
              </div>
            </div>
            {/* polaroid centered overlap */}
            <div style={{ position: "absolute", right: 8, bottom: -10, transform: "rotate(8deg)", zIndex: 2 }}>
              <Polaroid tone="peach" caption="2 abr 2025" w={130} h={150} />
            </div>
            {/* stamp left */}
            <div style={{ position: "absolute", left: 4, bottom: 14, zIndex: 2 }}>
              <Stamp size={64} color={MV.butter} sub="capítulo" label="07" rotate={-8} />
            </div>
          </div>

          <div style={{ height: 80 }} />

          {/* mensagem do mês */}
          <div style={{
            background: "rgba(245,239,224,0.12)",
            border: `1.4px dashed ${MV.cream}66`,
            borderRadius: 18,
            padding: "18px 18px",
            position: "relative",
          }}>
            <div style={{ position: "absolute", top: -10, left: 18, background: MV.sage, padding: "0 8px" }}>
              <Tag color={MV.butter}>mensagem do mês</Tag>
            </div>
            <p style={{
              fontFamily: "'Fraunces', serif",
              fontWeight: 400,
              fontSize: 20,
              lineHeight: 1.25,
              color: MV.cream,
              margin: 0,
              letterSpacing: -0.4,
              fontVariationSettings: "'opsz' 96",
              fontStyle: "italic",
              whiteSpace: "pre-line",
              textWrap: "pretty",
            }}>
              {monthMessages[month]}
            </p>
          </div>

          {/* mini stats */}
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10, marginTop: 16 }}>
            <div style={{ background: "rgba(245,239,224,0.10)", border: `1.4px solid ${MV.cream}33`, borderRadius: 14, padding: "12px 14px" }}>
              <Tag color={MV.cream} style={{ opacity: 0.7 }}>idade</Tag>
              <div style={{ fontFamily: "'Fraunces', serif", fontSize: 22, color: MV.cream, fontWeight: 500, marginTop: 2, letterSpacing: -0.4 }}>
                7m, 2d
              </div>
            </div>
            <div style={{ background: "rgba(245,239,224,0.10)", border: `1.4px solid ${MV.cream}33`, borderRadius: 14, padding: "12px 14px" }}>
              <Tag color={MV.cream} style={{ opacity: 0.7 }}>dia da vida</Tag>
              <div style={{ fontFamily: "'Fraunces', serif", fontSize: 22, color: MV.cream, fontWeight: 500, marginTop: 2, letterSpacing: -0.4 }}>
                #218
              </div>
            </div>
          </div>
        </div>

        <div style={{ padding: "10px 24px 16px" }}>
          <button style={{
            background: MV.cream,
            color: MV.oliveDeep,
            border: "none",
            borderRadius: 18,
            padding: "16px 22px",
            fontSize: 15.5,
            fontWeight: 600,
            fontFamily: "'Fraunces', serif",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            gap: 10,
            width: "100%",
            letterSpacing: -0.2,
            boxShadow: "0 1px 0 rgba(255,255,255,0.4) inset, 0 8px 18px -8px rgba(20,30,15,0.4)",
          }}>
            registrar este mesversário
            <Icon name="arrow-right" size={16} color={MV.oliveDeep} strokeWidth={2} />
          </button>
        </div>
      </SageScreen>
    </PhoneScreen>
  );
}

window.MesversarioA = MesversarioA;
