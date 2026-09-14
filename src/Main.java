import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

void main() {
    IO.println("==================================================");
    IO.println("       ⏰       JAVA ALARM CLI                    ");
    IO.println("==================================================");

    LocalTime targetTime;
    try {
        targetTime = ScheduleFile.readTime(ScheduleFile.DEFAULT_PATH);
    } catch (IOException e) {
        IO.println("❌ " + e.getMessage());
        return;
    }

    AlarmPlayer player = new AlarmPlayer();
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime nextRun = now.with(targetTime);

    if (!now.isBefore(nextRun)) {
        nextRun = nextRun.plusDays(1);
    }

    long delaySeconds = Duration.between(now, nextRun).toSeconds();
    DateTimeFormatter clock = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter stamp = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    IO.println("📄 Horario: " + ScheduleFile.DEFAULT_PATH.toAbsolutePath().normalize());
    IO.println("⏰ Hora:    " + targetTime.format(clock));
    IO.println(String.format("✅ Disparo: %s  (%d segundos)", nextRun.format(stamp), delaySeconds));

    scheduler.schedule(player::startAlarm, delaySeconds, TimeUnit.SECONDS);

    IO.readln("\nEsperando... (Presiona ENTER para apagar o cancelar)\n");

    if (player.isRinging()) {
        player.stopAlarm();
    } else {
        IO.println("🚫 Programación cancelada por el usuario.");
        player.stopAlarm();
    }

    scheduler.shutdownNow();
}
