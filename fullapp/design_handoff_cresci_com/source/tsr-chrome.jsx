/**
 * TSR Chrome — componentes compartilhados de chrome de tela
 * Linguagem D + B mesclados.
 */

const { Icon, colors: TC, PaperBG, Stitched, Stamp, Blob } = window.TSR;

// Header padrão de tela interna — fundo cream, com etiqueta monospace e título Fraunces
function ScreenHeader({ chapter, title, subtitle, onBack, right, accent = TC.amberDeep, color = TC.oliveDeep }) {
  return (
    <div style={{ padding: "8px 24px 18px", flexShrink: 0 }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 14, minHeight: 32 }}>
        {onBack ? (
          <button onClick={onBack} style={{ background: "transparent", border: "none", padding: 4, display: "flex", alignItems: "center", color }}>
            <Icon name="chevron-left" size={24} color={color} />
          </button>
        ) : <div style={{ width: 32 }} />}
        {chapter && (
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <div style={{ width: 18, height: 1, background: color, opacity: 0.4 }} />
            <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 9.5, color, opacity: 0.7, letterSpacing: 1.6, textTransform: "uppercase" }}>
              {chapter}
            </span>
            <div style={{ width: 18, height: 1, background: color, opacity: 0.4 }} />
          </div>
        )}
        {right || <div style={{ width: 32 }} />}
      </div>
      {title && (
        <h1 style={{
          fontFamily: "'Fraunces', serif",
          fontWeight: 400,
          fontSize: 32,
          lineHeight: 1.0,
          color,
          margin: 0,
          letterSpacing: -0.9,
          fontVariationSettings: "'opsz' 144",
          textWrap: "pretty",
        }}>
          {title}
        </h1>
      )}
      {subtitle && (
        <p style={{ fontSize: 13, color, opacity: 0.65, marginTop: 8, lineHeight: 1.5 }}>
          {subtitle}
        </p>
      )}
    </div>
  );
}

// Tab bar inferior padrão (Home, Calendário, Registros, Eventos, Livro)
function TabBar({ active = "home", onChange, color = TC.oliveDeep, bg = TC.cream }) {
  const items = [
    { id: "home", label: "início", icon: "leaf" },
    { id: "calendar", label: "calendário", icon: "calendar" },
    { id: "moments", label: "registros", icon: "pencil" },
    { id: "events", label: "eventos", icon: "camera" },
    { id: "book", label: "livro", icon: "book" },
  ];
  return (
    <div style={{
      background: bg,
      borderTop: `1px solid ${color}1a`,
      padding: "8px 8px 6px",
      display: "flex",
      justifyContent: "space-around",
      flexShrink: 0,
    }}>
      {items.map(it => {
        const isActive = it.id === active;
        return (
          <button key={it.id} onClick={() => onChange?.(it.id)} style={{
            background: "transparent",
            border: "none",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            gap: 3,
            padding: "6px 6px",
            color: isActive ? color : `${color}80`,
            position: "relative",
          }}>
            <Icon name={it.icon} size={22} color={isActive ? color : `${color}99`} strokeWidth={isActive ? 1.9 : 1.5} />
            <span style={{ fontSize: 9.5, fontWeight: isActive ? 600 : 500, fontFamily: "'Inter', sans-serif", letterSpacing: 0.2 }}>
              {it.label}
            </span>
            {isActive && (
              <div style={{ position: "absolute", bottom: -6, left: "50%", transform: "translateX(-50%)", width: 4, height: 4, borderRadius: 2, background: color }} />
            )}
          </button>
        );
      })}
    </div>
  );
}

// Card de moment (registro): polaroid-ish, com data e nota
function MomentCard({ kind = "DAILY", title, date, note, photo = "peach", rotate = 0, mini = false, accent }) {
  const tones = {
    DAILY: { bg: TC.cream, label: "diário", color: TC.amberDeep, photo: "peach" },
    WEEKLY: { bg: TC.cream, label: "semanal", color: TC.olive, photo: "sage" },
    MESVERSARIO: { bg: TC.butter, label: "mesversário", color: TC.amberDeep, photo: "butter" },
    PREGNANCY_WEEK: { bg: TC.peach, label: "semana", color: TC.amberDeep, photo: "peach" },
    DAY_OF_LIFE: { bg: TC.cream, label: "dia da vida", color: TC.olive, photo: "sage" },
  };
  const t = tones[kind] || tones.DAILY;
  return (
    <div style={{
      background: t.bg,
      borderRadius: mini ? 10 : 14,
      padding: mini ? 8 : 10,
      transform: `rotate(${rotate}deg)`,
      boxShadow: "0 1px 0 rgba(255,255,255,0.4) inset, 0 6px 18px -10px rgba(63,79,46,0.35), 0 0 0 1px rgba(63,79,46,0.06)",
    }}>
      <window.TSR.Placeholder label={photo === "peach" ? "foto" : photo} tone={t.photo} h={mini ? 90 : 130} radius={mini ? 6 : 10} />
      <div style={{ padding: "8px 4px 2px" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline", marginBottom: 4 }}>
          <span style={{ fontFamily: "'JetBrains Mono', monospace", fontSize: 8.5, color: t.color, opacity: 0.75, letterSpacing: 1.2, textTransform: "uppercase" }}>
            {t.label}
          </span>
          <span style={{ fontSize: 9.5, color: TC.olive, opacity: 0.6, fontFamily: "'JetBrains Mono', monospace" }}>{date}</span>
        </div>
        <div style={{
          fontFamily: "'Fraunces', serif",
          fontSize: mini ? 13 : 15,
          fontWeight: 500,
          color: TC.oliveDeep,
          lineHeight: 1.2,
          letterSpacing: -0.2,
        }}>
          {title}
        </div>
        {note && (
          <div style={{ fontFamily: "'Caveat', cursive", fontSize: mini ? 13 : 15, color: TC.olive, opacity: 0.85, marginTop: 4, lineHeight: 1.15 }}>
            {note}
          </div>
        )}
      </div>
    </div>
  );
}

// Wrapper de tela cream com paper texture
function CreamScreen({ children, padding = 0, style }) {
  return (
    <PaperBG color={TC.cream} style={{ flex: 1, display: "flex", flexDirection: "column", padding, ...style }}>
      {children}
    </PaperBG>
  );
}

// Wrapper de tela sage saturado com blobs sutis
function SageScreen({ children, style }) {
  return (
    <div style={{ flex: 1, position: "relative", overflow: "hidden", display: "flex", flexDirection: "column", background: TC.sage, ...style }}>
      <PaperBG color="transparent" style={{ position: "absolute", inset: 0, pointerEvents: "none", opacity: 0.55 }} />
      <Blob size={300} color={TC.oliveDeep} style={{ position: "absolute", top: -130, right: -110, opacity: 0.16, pointerEvents: "none" }} />
      <Blob
        size={240}
        color={TC.butter}
        opacity={0.16}
        style={{ position: "absolute", bottom: -90, left: -90, pointerEvents: "none" }}
        d="M40.6,-46.7C53.4,-37.7,65.2,-26.7,68.5,-13.4C71.8,-0.1,66.6,15.5,57.4,28.2C48.2,40.9,35,50.7,20.5,55.7C6,60.7,-9.8,60.9,-23.5,55.7C-37.2,50.5,-48.8,39.9,-55.6,26.8C-62.4,13.7,-64.4,-1.9,-59.5,-15.1C-54.6,-28.3,-42.9,-39.1,-30.2,-48.4C-17.5,-57.7,-3.8,-65.5,7.7,-65.6C19.1,-65.6,38.3,-58,40.6,-46.7Z"
      />
      <div style={{ position: "relative", zIndex: 1, display: "flex", flexDirection: "column", flex: 1 }}>{children}</div>
    </div>
  );
}

// Botão primário — olive sólido, Fraunces
function PrimaryButton({ children, icon = "arrow-right", style, onColor = "sage", onClick }) {
  // onColor: "sage" -> button olive on sage bg ; "cream" -> button olive on cream bg
  return (
    <button onClick={onClick} style={{
      background: TC.oliveDeep,
      color: TC.cream,
      border: "none",
      borderRadius: 18,
      padding: "16px 22px",
      fontSize: 16,
      fontWeight: 600,
      fontFamily: "'Fraunces', serif",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      gap: 10,
      width: "100%",
      letterSpacing: -0.2,
      boxShadow: onColor === "sage"
        ? "0 1px 0 rgba(255,255,255,0.12) inset, 0 10px 22px -8px rgba(20,30,15,0.6)"
        : "0 1px 0 rgba(255,255,255,0.15) inset, 0 8px 18px -6px rgba(63,79,46,0.45)",
      ...style,
    }}>
      {children}
      {icon && <Icon name={icon} size={18} color={TC.cream} strokeWidth={2} />}
    </button>
  );
}

// Botão secundário — outline em creme/olive
function GhostButton({ children, icon, color = TC.oliveDeep, style, onClick }) {
  return (
    <button onClick={onClick} style={{
      background: "transparent",
      color,
      border: `1.6px solid ${color}`,
      borderRadius: 999,
      padding: "14px 20px",
      fontSize: 14.5,
      fontWeight: 600,
      fontFamily: "'Fraunces', serif",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      gap: 8,
      ...style,
    }}>
      {icon && <Icon name={icon} size={16} color={color} strokeWidth={2} />}
      {children}
    </button>
  );
}

// Torn paper divider — útil pra separar seções
function TornDivider({ color = TC.cream, flip = false, height = 12 }) {
  return (
    <svg style={{ width: "100%", height, display: "block", transform: flip ? "scaleY(-1)" : undefined }} viewBox="0 0 390 12" preserveAspectRatio="none">
      <path d="M0,7 Q30,1 60,6 T120,7 T180,5 T240,8 T300,6 T390,7 L390,12 L0,12 Z" fill={color} />
    </svg>
  );
}

// Tag/etiqueta monospace
function Tag({ children, color = TC.oliveDeep, bg = "transparent", style }) {
  return (
    <span style={{
      fontFamily: "'JetBrains Mono', monospace",
      fontSize: 9.5,
      letterSpacing: 1.4,
      textTransform: "uppercase",
      color,
      background: bg,
      padding: bg === "transparent" ? 0 : "4px 8px",
      borderRadius: 999,
      opacity: bg === "transparent" ? 0.7 : 1,
      ...style,
    }}>
      {children}
    </span>
  );
}

// Polaroid com legenda manuscrita (Caveat)
function Polaroid({ tone = "peach", caption, w = 130, h = 150, rotate = 0, frame = "cream", style }) {
  return (
    <div style={{
      background: frame === "cream" ? TC.cream : TC.white,
      borderRadius: 4,
      padding: 8,
      paddingBottom: caption ? 4 : 8,
      transform: `rotate(${rotate}deg)`,
      boxShadow: "0 8px 18px -10px rgba(20,30,15,0.45)",
      width: w,
      ...style,
    }}>
      <window.TSR.Placeholder label="" tone={tone} h={h} radius={2} />
      {caption && (
        <div style={{ textAlign: "center", fontFamily: "'Caveat', cursive", fontSize: 16, color: TC.oliveDeep, marginTop: 4, lineHeight: 1.05 }}>
          {caption}
        </div>
      )}
    </div>
  );
}

window.TSR_Chrome = {
  ScreenHeader,
  TabBar,
  MomentCard,
  CreamScreen,
  SageScreen,
  PrimaryButton,
  GhostButton,
  TornDivider,
  Tag,
  Polaroid,
};
