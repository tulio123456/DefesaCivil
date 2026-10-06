package br.com.defesacivil.alerta;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class AlertMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL = "alertas_criticos";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // O app usa o tópico "defesa_civil_testes", portanto não é necessário
        // copiar o token manualmente para o computador.
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        String title = message.getData().get("title");
        String body = message.getData().get("body");
        String level = message.getData().get("level");
        String kind = message.getData().get("kind");

        if (title == null || title.trim().isEmpty()) title = "ALERTA";
        if (body == null || body.trim().isEmpty()) body = "Nova mensagem recebida.";
        if (level == null) level = "ALTO";
        if (kind == null) kind = "TESTE";

        createChannel();

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, CHANNEL)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("[" + kind + "] " + title)
                        .setContentText(body)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(
                                "Nível: " + level + "\\n\\n" + body +
                                "\\n\\nMensagem enviada pelo painel independente de testes."))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{0, 500, 250, 500, 250, 800});

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify((int) System.currentTimeMillis(), notification.build());
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager.getNotificationChannel(CHANNEL) != null) return;

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        NotificationChannel channel = new NotificationChannel(
                CHANNEL,
                "Alertas de alta prioridade",
                NotificationManager.IMPORTANCE_HIGH);

        channel.setDescription("Alertas enviados pelo painel independente.");
        channel.setSound(sound, attributes);
        channel.enableVibration(true);
        manager.createNotificationChannel(channel);
    }
}
