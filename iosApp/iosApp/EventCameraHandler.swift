import AVFoundation
import UIKit
import shared

// ╔══════════════════════════════════════════════════════════════╗
// ║  EventCameraHandler.swift — câmera real (AVFoundation)       ║
// ║                                                              ║
// ║  Implementa o ciclo de captura em Swift porque K/N 2.1.0     ║
// ║  não expõe `AVCapturePhoto.fileDataRepresentation()`.        ║
// ║                                                              ║
// ║  O singleton IosEventCameraBridge (Kotlin) recebe:           ║
// ║   - onStart / onStop: controlam o AVCaptureSession           ║
// ║   - onCapture: dispara captura e devolve NSData via callback ║
// ║   - providePreviewView: retorna UIView com preview layer     ║
// ║   - hasCamera: false em simulador (AV não tem device)        ║
// ║                                                              ║
// ║  Notas de interop:                                           ║
// ║   - Closures Kotlin Unit-returning chegam em Swift como      ║
// ║     `() -> KotlinUnit`. Sempre retornar `KotlinUnit()`.      ║
// ║   - `NSData?` em Kotlin é exposto em Swift como `Data?`.     ║
// ╚══════════════════════════════════════════════════════════════╝

final class EventCameraHandler: NSObject, AVCapturePhotoCaptureDelegate {
    private let session = AVCaptureSession()
    private let photoOutput = AVCapturePhotoOutput()
    private let sessionQueue = DispatchQueue(label: "tsrapprun.camera.session")
    private let previewView: PreviewContainerView
    private var hasCamera = false
    private var pendingCallback: ((Data?, String?) -> KotlinUnit)?

    /// Contador para diferenciar fotos sintéticas no simulador.
    private var syntheticCounter = 0

    override init() {
        previewView = PreviewContainerView(session: session)
        super.init()
        configureSession()
        registerWithBridge()
    }

    // MARK: - Configuração

    private func configureSession() {
        session.beginConfiguration()
        session.sessionPreset = .photo

        guard
            let device = AVCaptureDevice.default(for: .video),
            let input = try? AVCaptureDeviceInput(device: device)
        else {
            session.commitConfiguration()
            hasCamera = false
            return
        }

        if session.canAddInput(input) { session.addInput(input) }
        if session.canAddOutput(photoOutput) { session.addOutput(photoOutput) }
        session.commitConfiguration()
        hasCamera = true
    }

    private func registerWithBridge() {
        let bridge = IosEventCameraBridge.shared
        bridge.hasCamera = hasCamera

        bridge.providePreviewView = { [weak self] in self?.previewView }

        bridge.onStart = { [weak self] in
            self?.start()
        }

        bridge.onStop = { [weak self] in
            self?.stop()
        }

        bridge.onCapture = { [weak self] callback in
            guard let self = self else {
                _ = callback(nil, "Câmera não inicializada")
                return
            }
            if self.hasCamera {
                self.startCapture(callback: callback)
            } else {
                // Fallback simulador: gera foto sintética para o fluxo
                // de salvar/listar/deletar continuar testável sem device.
                self.syntheticCounter += 1
                let data = SyntheticPhoto.generate(counter: self.syntheticCounter)
                _ = callback(data, nil)
            }
        }
    }

    // MARK: - Ciclo de vida

    private func start() {
        sessionQueue.async {
            if !self.session.isRunning { self.session.startRunning() }
        }
    }

    private func stop() {
        sessionQueue.async {
            if self.session.isRunning { self.session.stopRunning() }
        }
    }

    // MARK: - Captura

    private func startCapture(callback: @escaping (Data?, String?) -> KotlinUnit) {
        let status = AVCaptureDevice.authorizationStatus(for: .video)
        switch status {
        case .authorized:
            capture(callback: callback)
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
                DispatchQueue.main.async {
                    if granted {
                        self?.capture(callback: callback)
                    } else {
                        _ = callback(nil, "Permissão de câmera negada.")
                    }
                }
            }
        default:
            _ = callback(nil, "Permissão de câmera negada. Habilite em Ajustes › Privacidade › Câmera.")
        }
    }

    private func capture(callback: @escaping (Data?, String?) -> KotlinUnit) {
        guard hasCamera else {
            _ = callback(nil, "Câmera indisponível neste dispositivo.")
            return
        }
        pendingCallback = callback
        let settings = AVCapturePhotoSettings()
        photoOutput.capturePhoto(with: settings, delegate: self)
    }

    // MARK: - AVCapturePhotoCaptureDelegate

    func photoOutput(
        _ output: AVCapturePhotoOutput,
        didFinishProcessingPhoto photo: AVCapturePhoto,
        error: Error?
    ) {
        let cb = pendingCallback
        pendingCallback = nil

        DispatchQueue.main.async {
            if let error = error {
                _ = cb?(nil, error.localizedDescription)
                return
            }
            guard let data = photo.fileDataRepresentation() else {
                _ = cb?(nil, "Sem dados da foto")
                return
            }
            _ = cb?(data, nil)
        }
    }
}

// MARK: - Foto sintética (fallback simulador)

/// Gera um JPEG colorido com timestamp e contador desenhados em cima,
/// para que cada captura no simulador produza bytes distintos e visíveis.
enum SyntheticPhoto {
    private static let palette: [UIColor] = [
        UIColor(red: 0.86, green: 0.34, blue: 0.34, alpha: 1.0),
        UIColor(red: 0.32, green: 0.55, blue: 0.86, alpha: 1.0),
        UIColor(red: 0.39, green: 0.71, blue: 0.51, alpha: 1.0),
        UIColor(red: 0.95, green: 0.71, blue: 0.32, alpha: 1.0),
        UIColor(red: 0.62, green: 0.43, blue: 0.78, alpha: 1.0),
    ]

    static func generate(counter: Int) -> Data {
        let size = CGSize(width: 1080, height: 1440)
        let renderer = UIGraphicsImageRenderer(size: size)
        let image = renderer.image { ctx in
            // Fundo
            let color = palette[(counter - 1) % palette.count]
            color.setFill()
            ctx.fill(CGRect(origin: .zero, size: size))

            // Anéis decorativos
            let center = CGPoint(x: size.width / 2, y: size.height / 2)
            UIColor.white.withAlphaComponent(0.12).setStroke()
            for i in 1...6 {
                let radius = CGFloat(i) * 120
                let rect = CGRect(
                    x: center.x - radius, y: center.y - radius,
                    width: radius * 2, height: radius * 2
                )
                let path = UIBezierPath(ovalIn: rect)
                path.lineWidth = 6
                path.stroke()
            }

            // Texto: contador grande
            let counterStr = "#\(counter)"
            let counterAttrs: [NSAttributedString.Key: Any] = [
                .font: UIFont.boldSystemFont(ofSize: 220),
                .foregroundColor: UIColor.white,
            ]
            let counterSize = counterStr.size(withAttributes: counterAttrs)
            counterStr.draw(
                at: CGPoint(
                    x: (size.width - counterSize.width) / 2,
                    y: (size.height - counterSize.height) / 2 - 100
                ),
                withAttributes: counterAttrs
            )

            // Texto: timestamp
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
            let stamp = formatter.string(from: Date())
            let stampAttrs: [NSAttributedString.Key: Any] = [
                .font: UIFont.monospacedSystemFont(ofSize: 36, weight: .medium),
                .foregroundColor: UIColor.white.withAlphaComponent(0.9),
            ]
            let stampSize = stamp.size(withAttributes: stampAttrs)
            stamp.draw(
                at: CGPoint(
                    x: (size.width - stampSize.width) / 2,
                    y: size.height - stampSize.height - 80
                ),
                withAttributes: stampAttrs
            )

            // Texto: marca "MODO SIMULADOR"
            let badge = "MODO SIMULADOR"
            let badgeAttrs: [NSAttributedString.Key: Any] = [
                .font: UIFont.boldSystemFont(ofSize: 28),
                .foregroundColor: UIColor.white.withAlphaComponent(0.7),
                .kern: 4.0,
            ]
            let badgeSize = badge.size(withAttributes: badgeAttrs)
            badge.draw(
                at: CGPoint(x: (size.width - badgeSize.width) / 2, y: 80),
                withAttributes: badgeAttrs
            )
        }
        return image.jpegData(compressionQuality: 0.85) ?? Data()
    }
}

// MARK: - Preview UIView

final class PreviewContainerView: UIView {
    private let previewLayer: AVCaptureVideoPreviewLayer

    init(session: AVCaptureSession) {
        previewLayer = AVCaptureVideoPreviewLayer(session: session)
        previewLayer.videoGravity = .resizeAspectFill
        super.init(frame: .zero)
        backgroundColor = .black
        layer.addSublayer(previewLayer)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) not supported")
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        previewLayer.frame = bounds
        CATransaction.commit()
    }
}
