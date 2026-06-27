package com.example.atividadem3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;
    File photoFile = null;

    //Variáveis para armazenar latitude e longitude globais
    Double latPoint = 0.0;
    Double lngPoint = 0.0;

    ImageView imageView;
    TextView txtCoordinates;
    TextView txtJsonPayload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageViewHolderFullSize);
        txtCoordinates = findViewById(R.id.txtCoordinates);
        txtJsonPayload = findViewById(R.id.txtJsonPayload);
        Button btnCapture = findViewById(R.id.btnCapture);

        //Solicita permissões de GPS e Câmera na inicialização
        askForPermission();

        btnCapture.setOnClickListener(v -> dispatchTakePictureIntentFullSize());
    }

    //Solicita permissão para acessar localização do GPS
    private void askForPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            configService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                configService();
            } else {
                Toast.makeText(this, "Permissão negada para acessar o GPS", Toast.LENGTH_LONG).show();
            }
        }
    }

    //Configura o serviço de localização
    public void configService() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Requisito atendido: Recuperar a latitude e longitude que o usuário estava no momento
                    latPoint = location.getLatitude();
                    lngPoint = location.getLongitude();
                }
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Captura uma imagem pela câmera em tempo real
    private void dispatchTakePictureIntentFullSize() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = null;
        try {
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        getApplicationContext().getPackageName() + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                //Dispara a intent da câmera
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        } catch (Exception ex) {
            //Se algo der errado (ex: aparelho sem câmera)
            ex.printStackTrace();
            Toast.makeText(this, "Não foi possível abrir a câmera do dispositivo.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Método disparado quando o usuário confirma a foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (photoFile != null) {
                //Exibe a imagem capturada em tela.
                Bitmap imageBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                imageView.setImageBitmap(imageBitmap);

                //Exibe as coordenadas em tela.
                String coordsText = "Latitude: " + latPoint + "\nLongitude: " + lngPoint;
                txtCoordinates.setText(coordsText);

                //Converte Bitmap para array de bytes
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                //Converte bytes para Base64
                String base64Image = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);

                //Exibe em tela a String JSON
                String jsonPayload = "{\n" +
                        "  \"latitude\": " + latPoint + ",\n" +
                        "  \"longitude\": " + lngPoint + ",\n" +
                        "  \"imagem\": \"" + base64Image + "\"\n" +
                        "}";

                txtJsonPayload.setText(jsonPayload);

                //Preparar uma requisição HTTP pelo método POST.
                sendMultimedia(jsonPayload);
            }
        }
    }

    //Função responsável pela Requisição HTTP POST baseada na classe HttpURLConnection
    public void sendMultimedia(final String jsonPayload) {
        Thread t = new Thread() {
            public void run() {
                // Endpoint inócuo (fake) como descrito na atividade
                String resourceURI = "http://192.168.0.15:3333/multimedia";
                try {
                    URL url = new URL(resourceURI);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    // Define o envio de JSON no cabeçalho
                    conn.setRequestProperty("Content-Type", "application/json; utf-8");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);

                    // Escreve a string JSON no corpo da requisição POST
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonPayload.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Apenas lê a resposta (simulação)
                    int responseCode = conn.getResponseCode();
                    System.out.println("Código da Resposta HTTP: " + responseCode);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}