package com.buseyener.harita;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, TextToSpeech.OnInitListener {

    private GoogleMap googleMap;
    private TextToSpeech textToSpeech;
    private LatLng startingPoint = new LatLng(40.821213, 29.918019); // Umuttepe, İzmit koordinatları

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Harita Fragment'ını başlat
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
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
        googleMap.addMarker(new MarkerOptions().position(startingPoint).title("Başlangıç Noktası: Umuttepe, İzmit"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingPoint, 12));

        // Gelen intent'i kontrol et
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("destination")) {
            String destination = intent.getStringExtra("destination");
            if (destination != null && !destination.isEmpty()) {
                // Yeni bir konum belirleyerek haritada göster
                showLocationOnMap(destination);
            }
        }
    }

    // Verilen adresten konum bilgisini alıp haritada gösteren metot
    private void showLocationOnMap(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && addressList.size() > 0) {
                Address location = addressList.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

                // MainActivity2'ye geçiş ve rota bilgilerini gönderme
                openMapActivity2(latLng, address);
            } else {
                textToSpeech.speak("Belirtilen konum bulunamadı", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            textToSpeech.speak("Konum bulunurken bir hata oluştu", TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void openMapActivity2(LatLng latLng, String address) {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        intent.putExtra("startLat", startingPoint.latitude);
        intent.putExtra("startLng", startingPoint.longitude);
        intent.putExtra("destLat", latLng.latitude);
        intent.putExtra("destLng", latLng.longitude);
        intent.putExtra("address", address);
        startActivity(intent);
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
}
