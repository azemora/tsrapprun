/**
 * TSR Design System — direção: cozy + lúdico/livro ilustrado
 *
 * Exporta no window:
 *  - TSR.colors          — paleta cozy
 *  - TSR.Icon            — <Icon name="book" size={24} />  (line, stroke sage)
 *  - TSR.Placeholder     — <Placeholder label="foto do bebê" w={...} h={...} />
 *  - TSR.PaperBG         — textura de papel sutil (background pattern)
 *  - TSR.Stamp           — selo decorativo de scrapbook
 *  - TSR.Stitched        — borda tracejada estilo costura
 *  - TSR.StatusBar       — barra de status mock (hora, sinal, bateria) cor configurável
 *  - TSR.HomeIndicator   — handle do iPhone
 */

const TSR_COLORS = {
  sage: "#8FA876",
  sageMist: "#E2EAD3",
  sageSoft: "#EFF3E4",
  olive: "#54683E",
  oliveDeep: "#3F4F2E",
  amber: "#CB8B45",
  amberDeep: "#A86F32",
  gold: "#D9B25C",
  tan: "#C8B998",
  cream: "#F5EFE0",
  creamDeep: "#EAE0C9",
  ink: "#3E2E1E",
  brick: "#B85450",
  white: "#FFFFFF",
  // pasteis para histórias / capas
  peach: "#F2D6D2",
  sky: "#C9DBE8",
  lilac: "#E6CFE8",
  butter: "#FCE4A7",
  mint: "#C8E0D7",
};

// ───────────── ICONES line, stroke sage, 24x24 base ─────────────
function Icon({ name, size = 24, color, strokeWidth = 1.75, style }) {
  const c = color || TSR_COLORS.olive;
  const props = {
    width: size,
    height: size,
    viewBox: "0 0 24 24",
    fill: "none",
    stroke: c,
    strokeWidth,
    strokeLinecap: "round",
    strokeLinejoin: "round",
    style,
  };
  switch (name) {
    case "book":
      return (
        <svg {...props}>
          <path d="M4 5.5a1.5 1.5 0 0 1 1.5-1.5H11v15.5H5.5A1.5 1.5 0 0 1 4 18z" />
          <path d="M20 5.5A1.5 1.5 0 0 0 18.5 4H13v15.5h5.5a1.5 1.5 0 0 0 1.5-1.5z" />
          <path d="M11 4v15.5M7 8h2M7 11h2M15 8h2M15 11h2" />
        </svg>
      );
    case "calendar":
      return (
        <svg {...props}>
          <rect x="3.5" y="5.5" width="17" height="15" rx="2.5" />
          <path d="M8 3v4M16 3v4M3.5 10h17" />
          <circle cx="8" cy="14" r="0.6" fill={c} />
          <circle cx="12" cy="14" r="0.6" fill={c} />
          <circle cx="16" cy="14" r="0.6" fill={c} />
          <circle cx="8" cy="17.5" r="0.6" fill={c} />
        </svg>
      );
    case "pencil":
      return (
        <svg {...props}>
          <path d="M14.5 4.5 19.5 9.5l-10.5 10.5H4v-5z" />
          <path d="M13 6l5 5" />
        </svg>
      );
    case "sparkle":
      return (
        <svg {...props}>
          <path d="M12 3v6M12 15v6M3 12h6M15 12h6" />
          <path d="M6 6l3 3M15 15l3 3M6 18l3-3M15 9l3-3" strokeWidth={strokeWidth * 0.7} />
        </svg>
      );
    case "scrapbook":
      return (
        <svg {...props}>
          <rect x="3.5" y="4.5" width="17" height="15" rx="2" />
          <path d="M3.5 9.5h17" />
          <path d="M7 13l2.5 2.5L14 11" />
          <circle cx="17" cy="14" r="1.2" />
        </svg>
      );
    case "settings":
      return (
        <svg {...props}>
          <circle cx="12" cy="12" r="3" />
          <path d="M12 3v2.5M12 18.5V21M21 12h-2.5M5.5 12H3M18.36 5.64l-1.77 1.77M7.41 16.59l-1.77 1.77M18.36 18.36l-1.77-1.77M7.41 7.41 5.64 5.64" />
        </svg>
      );
    case "arrow-left":
      return (
        <svg {...props}>
          <path d="M14 5l-7 7 7 7M7 12h13" />
        </svg>
      );
    case "arrow-right":
      return (
        <svg {...props}>
          <path d="M10 5l7 7-7 7M17 12H4" />
        </svg>
      );
    case "chevron-right":
      return (
        <svg {...props}>
          <path d="M9 5l7 7-7 7" />
        </svg>
      );
    case "chevron-left":
      return (
        <svg {...props}>
          <path d="M15 5l-7 7 7 7" />
        </svg>
      );
    case "camera":
      return (
        <svg {...props}>
          <path d="M3 7h3.5l1.5-2h8l1.5 2H21v11.5a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 18.5z" />
          <circle cx="12" cy="13" r="3.5" />
        </svg>
      );
    case "heart":
      return (
        <svg {...props}>
          <path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z" />
        </svg>
      );
    case "image":
      return (
        <svg {...props}>
          <rect x="3.5" y="4.5" width="17" height="15" rx="2.5" />
          <circle cx="9" cy="10" r="1.5" />
          <path d="m4 17 5-5 5 5 3-3 3 3" />
        </svg>
      );
    case "download":
      return (
        <svg {...props}>
          <path d="M12 4v11M7 11l5 5 5-5M5 19h14" />
        </svg>
      );
    case "plus":
      return (
        <svg {...props}>
          <path d="M12 5v14M5 12h14" />
        </svg>
      );
    case "x":
      return (
        <svg {...props}>
          <path d="M5 5l14 14M19 5L5 19" />
        </svg>
      );
    case "check":
      return (
        <svg {...props}>
          <path d="M5 12.5 9.5 17 19 7" />
        </svg>
      );
    case "trash":
      return (
        <svg {...props}>
          <path d="M5 7h14M9 7V5a1.5 1.5 0 0 1 1.5-1.5h3A1.5 1.5 0 0 1 15 5v2M6.5 7l1 12.5A1.5 1.5 0 0 0 9 21h6a1.5 1.5 0 0 0 1.5-1.5L17.5 7M10 11v6M14 11v6" />
        </svg>
      );
    case "leaf":
      return (
        <svg {...props}>
          <path d="M5 19c0-9 6-15 15-15 0 9-6 15-15 15z" />
          <path d="M5 19l8-8" />
        </svg>
      );
    case "sun":
      return (
        <svg {...props}>
          <circle cx="12" cy="12" r="4" />
          <path d="M12 2v2M12 20v2M2 12h2M20 12h2M5 5l1.5 1.5M17.5 17.5 19 19M5 19l1.5-1.5M17.5 6.5 19 5" />
        </svg>
      );
    case "moon":
      return (
        <svg {...props}>
          <path d="M20 14a8 8 0 0 1-10-10 8 8 0 1 0 10 10z" />
        </svg>
      );
    case "cake":
      return (
        <svg {...props}>
          <path d="M4 20h16M5 20v-7h14v7M7 13v-3h10v3M9 10V7M12 10V6M15 10V7" />
          <circle cx="9" cy="6" r="0.7" fill={c} />
          <circle cx="12" cy="5" r="0.7" fill={c} />
          <circle cx="15" cy="6" r="0.7" fill={c} />
        </svg>
      );
    case "google":
      return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none">
          <path d="M21.6 12.2c0-.7-.06-1.4-.18-2.05H12v3.88h5.4a4.6 4.6 0 0 1-2 3.04v2.5h3.24c1.9-1.74 2.96-4.3 2.96-7.37z" fill="#4285F4" />
          <path d="M12 22c2.7 0 4.96-.9 6.62-2.43l-3.24-2.5c-.9.6-2.05.96-3.38.96-2.6 0-4.8-1.75-5.6-4.1H3.07v2.58A10 10 0 0 0 12 22z" fill="#34A853" />
          <path d="M6.4 13.93a6 6 0 0 1 0-3.86V7.49H3.07a10 10 0 0 0 0 9.02L6.4 13.93z" fill="#FBBC05" />
          <path d="M12 5.95a5.4 5.4 0 0 1 3.83 1.5l2.87-2.87C16.95 2.99 14.7 2 12 2A10 10 0 0 0 3.07 7.49L6.4 10.07c.8-2.35 3-4.1 5.6-4.1z" fill="#EA4335" />
        </svg>
      );
    case "star":
      return (
        <svg {...props}>
          <path d="M12 3l2.6 5.6L20 9.5l-4 4 1 5.7L12 16.5 7 19.2l1-5.7-4-4 5.4-.9z" />
        </svg>
      );
    case "lock":
      return (
        <svg {...props}>
          <rect x="4.5" y="10.5" width="15" height="10" rx="2" />
          <path d="M8 10V7a4 4 0 0 1 8 0v3" />
        </svg>
      );
    case "bell":
      return (
        <svg {...props}>
          <path d="M6 16V11a6 6 0 0 1 12 0v5l1.5 2h-15z" />
          <path d="M10 21h4" />
        </svg>
      );
    case "more":
      return (
        <svg {...props}>
          <circle cx="6" cy="12" r="1.3" fill={c} />
          <circle cx="12" cy="12" r="1.3" fill={c} />
          <circle cx="18" cy="12" r="1.3" fill={c} />
        </svg>
      );
    case "share":
      return (
        <svg {...props}>
          <path d="M12 4v12M7 9l5-5 5 5M5 14v5h14v-5" />
        </svg>
      );
    case "baby":
      return (
        <svg {...props}>
          <circle cx="12" cy="8" r="4" />
          <path d="M9 8h.01M15 8h.01M10.5 10.5c.5.6 2.5.6 3 0" />
          <path d="M5 20c1-4 4-6 7-6s6 2 7 6" />
        </svg>
      );
    case "pregnancy":
      return (
        <svg {...props}>
          <circle cx="11" cy="6" r="2.5" />
          <path d="M11 9c-3 0-5 1.5-5 4.5 0 2 1 3 1 5h2v-3" />
          <path d="M12 13c2 0 4 1.5 4 4 0 1.5-1 2.5-2.5 2.5h-1.5" />
        </svg>
      );
    default:
      return null;
  }
}

// ───────────── Placeholder striped (foto do bebê) ─────────────
function Placeholder({ label = "foto", w = "100%", h = 120, tone = "sage", radius = 16, mono = true, style }) {
  const palettes = {
    sage: { bg: "#E2EAD3", stripe: "rgba(84,104,62,0.10)", text: "#54683E" },
    cream: { bg: "#F5EFE0", stripe: "rgba(62,46,30,0.08)", text: "#3E2E1E" },
    amber: { bg: "#F1DCBE", stripe: "rgba(168,111,50,0.12)", text: "#A86F32" },
    peach: { bg: "#F2D6D2", stripe: "rgba(184,84,80,0.10)", text: "#8C3F3D" },
    sky:   { bg: "#C9DBE8", stripe: "rgba(40,80,110,0.10)", text: "#2F5575" },
    lilac: { bg: "#E6CFE8", stripe: "rgba(100,60,110,0.10)", text: "#5A3D6B" },
    butter:{ bg: "#FCE4A7", stripe: "rgba(140,100,30,0.10)", text: "#7A5C1F" },
    mint:  { bg: "#C8E0D7", stripe: "rgba(40,90,75,0.10)", text: "#2D5C4D" },
  };
  const p = palettes[tone] || palettes.sage;
  const stripeBG = `repeating-linear-gradient(135deg, ${p.bg} 0 8px, ${p.stripe} 8px 9px)`;
  return (
    <div
      style={{
        width: w,
        height: h,
        background: stripeBG,
        borderRadius: radius,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        color: p.text,
        fontFamily: mono ? "'JetBrains Mono', ui-monospace, monospace" : "'Inter', sans-serif",
        fontSize: 11,
        fontWeight: 500,
        letterSpacing: 0.4,
        textAlign: "center",
        padding: 12,
        position: "relative",
        overflow: "hidden",
        ...style,
      }}
    >
      <span style={{ opacity: 0.7 }}>{label}</span>
    </div>
  );
}

// ───────────── Paper texture (subtle noise) ─────────────
const PAPER_BG_DATA_URL =
  "data:image/svg+xml;utf8," +
  encodeURIComponent(`
<svg xmlns='http://www.w3.org/2000/svg' width='220' height='220'>
  <filter id='n'>
    <feTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='2' seed='3'/>
    <feColorMatrix values='0 0 0 0 0.32  0 0 0 0 0.40  0 0 0 0 0.24  0 0 0 0.05 0'/>
  </filter>
  <rect width='220' height='220' filter='url(#n)'/>
</svg>`);

function PaperBG({ children, color = TSR_COLORS.cream, style }) {
  return (
    <div
      style={{
        background: color,
        backgroundImage: `url("${PAPER_BG_DATA_URL}")`,
        backgroundBlendMode: "multiply",
        ...style,
      }}
    >
      {children}
    </div>
  );
}

// ───────────── Stitched border (costura tracejada) ─────────────
function Stitched({ color = TSR_COLORS.olive, style, children, radius = 20, padding = 12, bg = "transparent", offset = 5 }) {
  return (
    <div style={{ position: "relative", borderRadius: radius, background: bg, padding, ...style }}>
      <div
        style={{
          position: "absolute",
          inset: offset,
          borderRadius: Math.max(radius - offset, 4),
          border: `1.4px dashed ${color}`,
          opacity: 0.5,
          pointerEvents: "none",
        }}
      />
      {children}
    </div>
  );
}

// ───────────── Stamp (selo redondo) ─────────────
function Stamp({ size = 64, color = TSR_COLORS.amber, label, sub, rotate = -8 }) {
  return (
    <div
      style={{
        width: size,
        height: size,
        borderRadius: "50%",
        border: `1.6px dashed ${color}`,
        color,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        textAlign: "center",
        fontFamily: "'Fraunces', serif",
        fontWeight: 600,
        transform: `rotate(${rotate}deg)`,
        background: "rgba(255,255,255,0.55)",
        position: "relative",
      }}
    >
      <div style={{ fontSize: size * 0.18, letterSpacing: 1.6, textTransform: "uppercase", opacity: 0.8, fontFamily: "'Inter', sans-serif", fontWeight: 700 }}>
        {sub}
      </div>
      <div style={{ fontSize: size * 0.36, lineHeight: 1, marginTop: 2 }}>{label}</div>
    </div>
  );
}

// ───────────── iPhone status bar / home indicator ─────────────
function StatusBar({ color = "#000", time = "9:41" }) {
  return (
    <div
      style={{
        height: 44,
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        padding: "0 28px",
        fontFamily: "'Inter', system-ui, sans-serif",
        fontWeight: 600,
        fontSize: 15,
        color,
        flexShrink: 0,
      }}
    >
      <span style={{ letterSpacing: -0.3 }}>{time}</span>
      <div style={{ display: "flex", alignItems: "center", gap: 5 }}>
        {/* signal */}
        <svg width="17" height="11" viewBox="0 0 17 11" fill={color}>
          <rect x="0" y="7" width="3" height="4" rx="0.5" />
          <rect x="4.5" y="5" width="3" height="6" rx="0.5" />
          <rect x="9" y="2.5" width="3" height="8.5" rx="0.5" />
          <rect x="13.5" y="0" width="3" height="11" rx="0.5" />
        </svg>
        {/* wifi */}
        <svg width="15" height="11" viewBox="0 0 15 11" fill={color}>
          <path d="M7.5 2C4.6 2 2 3.2 0 5l1.4 1.4C3 5 5.2 4 7.5 4s4.5 1 6.1 2.4L15 5C13 3.2 10.4 2 7.5 2zm0 3C5.6 5 3.9 5.7 2.6 7l1.4 1.4c1-.9 2.2-1.4 3.5-1.4s2.5.5 3.5 1.4L12.4 7C11.1 5.7 9.4 5 7.5 5zm0 3c-1 0-1.8.4-2.5 1l2.5 2.5L10 8.4C9.3 7.7 8.5 8 7.5 8z" />
        </svg>
        {/* battery */}
        <svg width="27" height="12" viewBox="0 0 27 12" fill="none">
          <rect x="0.5" y="0.5" width="22" height="11" rx="3" stroke={color} opacity="0.4" />
          <rect x="2" y="2" width="19" height="8" rx="1.6" fill={color} />
          <rect x="23.5" y="4" width="2" height="4" rx="1" fill={color} opacity="0.4" />
        </svg>
      </div>
    </div>
  );
}

function HomeIndicator({ color = "#000", bg = "transparent" }) {
  return (
    <div style={{ height: 34, display: "flex", alignItems: "flex-end", justifyContent: "center", paddingBottom: 8, background: bg, flexShrink: 0 }}>
      <div style={{ width: 134, height: 5, borderRadius: 3, background: color, opacity: 0.85 }} />
    </div>
  );
}

// ───────────── Phone frame simples (390 x 844 — iPhone 15) ─────────────
function PhoneScreen({ children, statusColor = "#000", homeColor = "#000", bg = TSR_COLORS.sageMist, w = 390, h = 844, statusBar = true, homeIndicator = true, time = "9:41", style }) {
  return (
    <div
      style={{
        width: w,
        height: h,
        borderRadius: 44,
        background: bg,
        overflow: "hidden",
        position: "relative",
        display: "flex",
        flexDirection: "column",
        boxShadow: "0 30px 60px -20px rgba(62,46,30,0.25), 0 0 0 1px rgba(62,46,30,0.08)",
        fontFamily: "'Inter', system-ui, sans-serif",
        color: TSR_COLORS.ink,
        ...style,
      }}
    >
      {statusBar && <StatusBar color={statusColor} time={time} />}
      <div style={{ flex: 1, position: "relative", overflow: "hidden", display: "flex", flexDirection: "column" }}>
        {children}
      </div>
      {homeIndicator && <HomeIndicator color={homeColor} />}
    </div>
  );
}

// ───────────── Blob shape (decorativa, lúdica) ─────────────
function Blob({ d, color, size = 100, style, opacity = 1 }) {
  // d default = blob orgânico
  const path = d || "M52,-58.4C66.7,-46.5,77.4,-29.4,79.5,-11.4C81.6,6.6,75,25.5,62.6,38.7C50.2,51.9,32,59.4,13.4,63.4C-5.2,67.4,-24.2,67.9,-38.5,59.6C-52.8,51.3,-62.4,34.2,-66.6,16.1C-70.8,-2,-69.6,-21,-60.5,-34.2C-51.4,-47.4,-34.4,-54.7,-17.6,-58.7C-0.7,-62.8,16,-63.5,32.3,-63.4Z";
  return (
    <svg width={size} height={size} viewBox="-100 -100 200 200" style={{ ...style }}>
      <path d={path} fill={color} opacity={opacity} />
    </svg>
  );
}

window.TSR = {
  colors: TSR_COLORS,
  Icon,
  Placeholder,
  PaperBG,
  Stitched,
  Stamp,
  Blob,
  StatusBar,
  HomeIndicator,
  PhoneScreen,
};
