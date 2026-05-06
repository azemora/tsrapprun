import AVFoundation
import Speech
import shared

// ╔══════════════════════════════════════════════════════════════╗
// ║  AudioRecorderHandler.swift — gravação + transcrição (real)  ║
// ║                                                              ║
// ║  AVAudioRecorder grava num arquivo .m4a temporário.          ║
// ║  Ao parar, SFSpeechRecognizer roda transcrição offline       ║
// ║  (pt-BR, com fallback para reconhecedor padrão).             ║
// ║                                                              ║
// ║  Permissões precisam estar no Info.plist:                    ║
// ║   - NSMicrophoneUsageDescription                             ║
// ║   - NSSpeechRecognitionUsageDescription                      ║
// ║                                                              ║
// ║  No simulador o microfone capta áudio do mac (se permitido)  ║
// ║  ou silêncio — em ambos os casos transcrição pode vir vazia. ║
// ╚══════════════════════════════════════════════════════════════╝

final class AudioRecorderHandler: NSObject, AVAudioRecorderDelegate {
    private var recorder: AVAudioRecorder?
    private var currentURL: URL?
    private let recognizer: SFSpeechRecognizer? = {
        // Prefere pt-BR, mas cai para o padrão do device se não disponível.
        SFSpeechRecognizer(locale: Locale(identifier: "pt-BR"))
            ?? SFSpeechRecognizer()
    }()

    override init() {
        super.init()
        registerWithBridge()
    }

    private func registerWithBridge() {
        let bridge = IosAudioBridge.shared
        bridge.isAvailable = true

        bridge.onRequestPermission = { [weak self] callback in
            self?.requestPermissions(callback: callback)
        }

        bridge.onStart = { [weak self] callback in
            self?.startRecording(callback: callback)
        }

        bridge.onStopAndTranscribe = { [weak self] callback in
            self?.stopAndTranscribe(callback: callback)
        }

        bridge.onCancel = { [weak self] in
            self?.cancel()
        }
    }

    // MARK: - Permissões

    private func requestPermissions(callback: @escaping (KotlinBoolean, String?) -> KotlinUnit) {
        SFSpeechRecognizer.requestAuthorization { speechStatus in
            DispatchQueue.main.async {
                guard speechStatus == .authorized else {
                    _ = callback(KotlinBoolean(value: false), "Permissão de transcrição negada. Habilite em Ajustes › Privacidade › Reconhecimento de Fala.")
                    return
                }
                let session = AVAudioSession.sharedInstance()
                let micRequest: (@escaping (Bool) -> Void) -> Void = { handler in
                    if #available(iOS 17.0, *) {
                        AVAudioApplication.requestRecordPermission(completionHandler: handler)
                    } else {
                        session.requestRecordPermission(handler)
                    }
                }
                micRequest { granted in
                    DispatchQueue.main.async {
                        if granted {
                            _ = callback(KotlinBoolean(value: true), nil)
                        } else {
                            _ = callback(KotlinBoolean(value: false), "Permissão de microfone negada. Habilite em Ajustes › Privacidade › Microfone.")
                        }
                    }
                }
            }
        }
    }

    // MARK: - Gravação

    private func startRecording(callback: @escaping (KotlinBoolean, String?) -> KotlinUnit) {
        // Limpa qualquer gravação anterior pendente.
        recorder?.stop()
        recorder = nil

        do {
            let session = AVAudioSession.sharedInstance()
            try session.setCategory(.playAndRecord, mode: .default, options: [.defaultToSpeaker])
            try session.setActive(true, options: [])
        } catch {
            _ = callback(KotlinBoolean(value: false), "Falha ao preparar áudio: \(error.localizedDescription)")
            return
        }

        let url = FileManager.default.temporaryDirectory
            .appendingPathComponent("registro-\(Int(Date().timeIntervalSince1970)).m4a")
        currentURL = url

        let settings: [String: Any] = [
            AVFormatIDKey: Int(kAudioFormatMPEG4AAC),
            AVSampleRateKey: 16_000,
            AVNumberOfChannelsKey: 1,
            AVEncoderAudioQualityKey: AVAudioQuality.medium.rawValue
        ]

        do {
            let r = try AVAudioRecorder(url: url, settings: settings)
            r.delegate = self
            r.isMeteringEnabled = true
            guard r.prepareToRecord(), r.record() else {
                _ = callback(KotlinBoolean(value: false), "Não foi possível iniciar gravação.")
                return
            }
            recorder = r
            _ = callback(KotlinBoolean(value: true), nil)
        } catch {
            _ = callback(KotlinBoolean(value: false), "Erro de gravação: \(error.localizedDescription)")
        }
    }

    private func stopAndTranscribe(callback: @escaping (String?, String?) -> KotlinUnit) {
        guard let r = recorder, let url = currentURL else {
            _ = callback(nil, "Nenhuma gravação em andamento.")
            return
        }
        r.stop()
        recorder = nil

        // Desativa sessão pra liberar áudio do simulador/sistema.
        try? AVAudioSession.sharedInstance().setActive(false, options: [.notifyOthersOnDeactivation])

        guard let recognizer = recognizer, recognizer.isAvailable else {
            cleanup(url: url)
            _ = callback(nil, "Reconhecedor de fala indisponível agora.")
            return
        }

        let request = SFSpeechURLRecognitionRequest(url: url)
        request.shouldReportPartialResults = false
        if #available(iOS 13.0, *) {
            request.requiresOnDeviceRecognition = false
        }

        recognizer.recognitionTask(with: request) { [weak self] result, error in
            guard let self = self else { return }
            // Aguarda final result (não parcial) ou erro.
            if let error = error {
                DispatchQueue.main.async {
                    self.cleanup(url: url)
                    _ = callback(nil, "Erro de transcrição: \(error.localizedDescription)")
                }
                return
            }
            guard let result = result, result.isFinal else { return }
            let text = result.bestTranscription.formattedString
            DispatchQueue.main.async {
                self.cleanup(url: url)
                _ = callback(text, nil)
            }
        }
    }

    private func cancel() {
        recorder?.stop()
        recorder = nil
        if let url = currentURL { cleanup(url: url) }
        try? AVAudioSession.sharedInstance().setActive(false, options: [.notifyOthersOnDeactivation])
    }

    private func cleanup(url: URL) {
        try? FileManager.default.removeItem(at: url)
        currentURL = nil
    }
}
