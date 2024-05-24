package com.buseyener.harita;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity0 extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;
    private final ActivityResultLauncher<Intent> voiceCommandLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    ArrayList<String> voiceResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (voiceResults != null && !voiceResults.isEmpty()) {
                        String voiceCommand = voiceResults.get(0);
                        // Alınan sesli komutları işleme
                        Toast.makeText(MainActivity0.this, "Sesli Komut: " + voiceCommand, Toast.LENGTH_SHORT).show();
                        // Harita ekranına geçiş
                        openMapActivity(voiceCommand);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main0);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, this);

        // Butonun tıklama olayını ayarlama
        Button voiceButton = findViewById(R.id.voice_button);
        voiceButton.setOnClickListener(view -> startVoiceRecognition());

        // Uygulama ilk açıldığında sesli komut ile konum talimatını okuma
        speakLocationInstructions();
    }

    @Override
    protected void onDestroy() {
        // Shutdown TextToSpeech
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void startVoiceRecognition() {
        Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR"); // Türkçe dil ayarı
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "tr-TR"); // Türkçe dil tercihi
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Bir komut söyleyin...");

        try {
            voiceCommandLauncher.launch(voiceIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Cihazınız ses tanıma özelliğini desteklemiyor", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakLocationInstructions() {
        String instructions = "Bir komut söyleyerek istediğiniz yere gitmek için ekranın ortasına dokunun";
        textToSpeech.speak(instructions, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("tr", "TR"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Türkçe dil desteği bulunamadı", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "TextToSpeech başlatılamadı", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMapActivity(String destination) {
        // Intent'i hazırla ve konumu ekleyerek navigasyonu başlat
        Intent intent = new Intent(MainActivity0.this, MainActivity.class);
        intent.putExtra("destination", destination);
        startActivity(intent);
    }
}
