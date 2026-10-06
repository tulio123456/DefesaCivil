package br.com.defesacivil.alerta;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private int dp(int v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView label(String s, float size) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(ContextCompat.getColor(this, R.color.white));
        t.setPadding(dp(18), dp(12), dp(18), dp(12));
        return t;
    }

    private Button btn(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setAllCaps(false);
        b.setTextSize(15);
        return b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);
        }

        createChannel();
        subscribe();

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackgroundColor(ContextCompat.getColor(this, R.color.bg));

        TextView header = label("🚨  SISTEMA DE ALERTAS", 25);
        header.setGravity(Gravity.CENTER_VERTICAL);
        page.addView(header);

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        content.addView(label(
                "PROJETO INDEPENDENTE\\n\\n" +
                "Este aplicativo recebe mensagens enviadas pelo seu próprio painel de computador. " +
                "Ele NÃO é o aplicativo oficial do Governo e não substitui o Cell Broadcast da Defesa Civil.",
                16));

        content.addView(label("STATUS DA CONEXÃO", 19));

        TextView status = label("● Registrando este aparelho no canal de testes...", 16);
        status.setTextColor(ContextCompat.getColor(this, R.color.green));
        content.addView(status);

        FirebaseMessaging.getInstance().subscribeToTopic("defesa_civil_testes")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        status.setText("● CONECTADO AO CANAL DE TESTES\\nEste aparelho está pronto para receber mensagens do painel.");
                    } else {
                        status.setText("● ERRO AO REGISTRAR\\nVerifique a conexão com a internet e o Firebase.");
                    }
                });

        Button test = btn("🔔 Testar notificação local");
        test.setOnClickListener(v ->
                Toast.makeText(this,
                        "Use o painel do computador para testar o envio remoto.",
                        Toast.LENGTH_LONG).show());
        content.addView(test);

        Button emergency = btn("📡 Abrir Alertas de Emergência do Android");
        emergency.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_EMERGENCY_BROADCAST_SETTINGS));
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });
        content.addView(emergency);

        Button idap = btn("🌐 Consultar IDAP oficial");
        idap.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://idap.mdr.gov.br/"))));
        content.addView(idap);

        content.addView(label(
                "FLUXO\\n" +
                "1. Você escreve o alerta no computador.\\n" +
                "2. O painel envia a mensagem para o Firebase.\\n" +
                "3. O Firebase entrega ao Android.\\n" +
                "4. O Android mostra a notificação, vibra e toca o som configurado.\\n\\n" +
                "Use apenas mensagens de teste/simulação neste projeto.",
                15));

        scroll.addView(content);
        page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(page);
    }

    private void subscribe() {
        FirebaseMessaging.getInstance().subscribeToTopic("defesa_civil_testes");
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < 26) return;

        NotificationChannel channel = new NotificationChannel(
                "alertas_criticos",
                "Alertas de alta prioridade",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Canal usado pelo painel independente.");
        channel.enableVibration(true);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }
}
