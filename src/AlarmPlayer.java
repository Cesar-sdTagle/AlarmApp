import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlarmPlayer {

    private static final String MAC_SOUND_PATH = "/System/Library/Sounds/Ping.aiff";
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private volatile Clip activeClip;

    /**
     * Inicia la alarma en un Virtual Thread liviano.
     */
    public void startAlarm() {
        executor.submit(() -> {
            File soundFile = new File(MAC_SOUND_PATH);
            if (!soundFile.exists()) {
                IO.println("⚠️ No se encontró el sonido nativo de macOS.");
                return;
            }

            try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile)) {
                activeClip = AudioSystem.getClip();
                activeClip.open(audioStream);
                activeClip.loop(Clip.LOOP_CONTINUOUSLY);

                IO.println("\n🔔 ¡ALARMA SONANDO! Presiona ENTER para apagar la alarma...");
            } catch (Exception e) {
                IO.println("❌ Error reproduciendo audio: " + e.getMessage());
            }
        });
    }

    /**
     * Detiene la alarma y destruye el hilo virtual asociado.
     */
    public void stopAlarm() {
        if (activeClip != null && activeClip.isRunning()) {
            activeClip.stop();
            activeClip.close();
            IO.println("🛑 Alarma apagada con éxito.");
        }
        executor.close(); // Cierra limpiamente el ExecutorService de Virtual Threads
    }

    public boolean isRinging() {
        return activeClip != null && activeClip.isRunning();
    }
}