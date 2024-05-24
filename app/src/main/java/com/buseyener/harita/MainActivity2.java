package com.buseyener.harita;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements OnMapReadyCallback, TextToSpeech.OnInitListener {

    private GoogleMap googleMap;
    private TextToSpeech textToSpeech;
    private LatLng startingLatLng;
    private LatLng destinationLatLng;
    private String destinationAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Intent'ten verileri al
        double startLat = getIntent().getDoubleExtra("startLat", 0);
        double startLng = getIntent().getDoubleExtra("startLng", 0);
        double destLat = getIntent().getDoubleExtra("destLat", 0);
        double destLng = getIntent().getDoubleExtra("destLng", 0);
        destinationAddress = getIntent().getStringExtra("address");

        startingLatLng = new LatLng(startLat, startLng);
        destinationLatLng = new LatLng(destLat, destLng);

        // Harita Fragment'ını başlat
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map2);
        mapFragment.getMapAsync(this);

        // TextToSpeech'i başlat
        textToSpeech = new TextToSpeech(this, this);
    }

    @Override
    protected void onDestroy() {
        // TextToSpeech'i kapat
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Başlangıç ve hedef konumlarını haritada göster
        googleMap.addMarker(new MarkerOptions().position(startingLatLng).title("Başlangıç Noktası: Umuttepe, İzmit"));
        googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title(destinationAddress));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingLatLng, 12));

        // Hedef konuma rota ve sesli yönlendirme
        getRouteToDestination();
    }

    private void getRouteToDestination() {
        String directionsText = "Başlangıç noktanız Umuttepe, İzmit. Hedefiniz " + destinationAddress + ". Rotayı takip edin.";
        textToSpeech.speak(directionsText, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("tr", "TR"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Türkçe dil desteği bulunamadı", Toast.LENGTH_SHORT).show();
            } else {
                getRouteToDestination();
            }
        } else {
            Toast.makeText(this, "TextToSpeech başlatılamadı", Toast.LENGTH_SHORT).show();
        }
    }
}
