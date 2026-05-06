/**
 * Child Registration — formulário de cadastro do bebê
 * Conteúdo real:
 *   nome, data de nascimento, foto
 *   gestação: data prevista de parto
 *   "vamos conhecer seu pequeno"
 */

const { Icon, colors: CC, PaperBG, Stitched, Stamp, PhoneScreen, Placeholder } = window.TSR;
const { CreamScreen, PrimaryButton, ScreenHeader, Tag, Polaroid } = window.TSR_Chrome;

function ChildRegistrationA() {
  return (
    <PhoneScreen bg={CC.cream}>
      <CreamScreen>
        <ScreenHeader
          chapter="cadastro — passo 1 de 2"
          title={<>vamos conhecer<br/><em style={{ fontStyle: "italic", color: CC.amberDeep, fontWeight: 300 }}>seu pequeno</em>.</>}
          subtitle="esses detalhes ficam só no seu aparelho. você pode editar depois."
          onBack={() => {}}
        />

        <div style={{ flex: 1, padding: "0 24px 20px", overflowY: "auto", display: "flex", flexDirection: "column", gap: 18 }}>
          {/* foto upload */}
          <div style={{ display: "flex", justifyContent: "center", marginTop: 4, marginBottom: 6 }}>
            <div style={{ position: "relative" }}>
              <Stitched color={CC.olive} radius={100} padding={8} bg={CC.cream} style={{ width: 130, height: 130, transform: "rotate(-3deg)" }}>
                <div style={{
                  width: "100%", height: "100%",
                  borderRadius: "50%",
                  background: `repeating-linear-gradient(135deg, ${CC.sageMist} 0 8px, rgba(84,104,62,0.10) 8px 9px)`,
                  display: "flex", alignItems: "center", justifyContent: "center",
                  color: CC.olive,
                }}>
                  <Icon name="camera" size={32} color={CC.olive} />
                </div>
              </Stitched>
              <div style={{ position: "absolute", bottom: -4, right: -4, background: CC.amber, color: CC.cream, borderRadius: 999, padding: "6px 10px", fontFamily: "'Fraunces', serif", fontSize: 12, fontWeight: 600, transform: "rotate(8deg)", boxShadow: "0 4px 10px -4px rgba(168,111,50,0.5)" }}>
                + foto
              </div>
            </div>
          </div>

          {/* segmented: já nasceu / ainda na barriga */}
          <div>
            <Tag style={{ marginBottom: 8, display: "inline-block" }}>momento</Tag>
            <div style={{ display: "flex", background: CC.creamDeep, borderRadius: 14, padding: 4, gap: 4 }}>
              <button style={{
                flex: 1, padding: "12px 8px", border: "none", background: CC.oliveDeep, color: CC.cream,
                borderRadius: 11, fontFamily: "'Fraunces', serif", fontWeight: 600, fontSize: 14,
                display: "flex", alignItems: "center", justifyContent: "center", gap: 6,
              }}>
                <Icon name="baby" size={16} color={CC.cream} />
                já nasceu
              </button>
              <button style={{
                flex: 1, padding: "12px 8px", border: "none", background: "transparent", color: CC.olive,
                borderRadius: 11, fontFamily: "'Fraunces', serif", fontWeight: 500, fontSize: 14,
                display: "flex", alignItems: "center", justifyContent: "center", gap: 6, opacity: 0.7,
              }}>
                <Icon name="pregnancy" size={16} color={CC.olive} />
                gestação
              </button>
            </div>
          </div>

          {/* input: nome */}
          <div>
            <Tag style={{ marginBottom: 8, display: "inline-block" }}>nome ou apelido</Tag>
            <div style={{
              background: CC.creamDeep,
              borderRadius: 14,
              padding: "14px 16px",
              border: `1.4px solid ${CC.olive}33`,
              display: "flex", alignItems: "center", gap: 10,
            }}>
              <span style={{ fontFamily: "'Fraunces', serif", fontSize: 18, color: CC.oliveDeep, fontWeight: 500 }}>
                Manu
              </span>
              <span style={{ width: 1.5, height: 20, background: CC.amber, animation: "blink 1s infinite" }} />
            </div>
          </div>

          {/* input: data de nascimento */}
          <div>
            <Tag style={{ marginBottom: 8, display: "inline-block" }}>nasceu em</Tag>
            <div style={{
              background: CC.creamDeep,
              borderRadius: 14,
              padding: "14px 16px",
              border: `1.4px solid ${CC.olive}1f`,
              display: "flex", alignItems: "center", justifyContent: "space-between",
            }}>
              <span style={{ fontFamily: "'Fraunces', serif", fontSize: 16, color: CC.oliveDeep }}>
                12 de março de 2025
              </span>
              <Icon name="calendar" size={20} color={CC.olive} />
            </div>
            <div style={{ fontFamily: "'Caveat', cursive", fontSize: 16, color: CC.amberDeep, marginTop: 6, textAlign: "right" }}>
              ~ 7 meses ✿
            </div>
          </div>
        </div>

        {/* footer cta */}
        <div style={{ padding: "10px 24px 14px", borderTop: `1px solid ${CC.olive}1a`, background: CC.cream }}>
          <PrimaryButton onColor="cream">continuar</PrimaryButton>
        </div>
      </CreamScreen>
    </PhoneScreen>
  );
}

window.ChildRegistrationA = ChildRegistrationA;
