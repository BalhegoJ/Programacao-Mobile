package com.example.atividadem2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private CardapioDAO dao;
    private ArrayList<ItemCardapio> dadosCardapio;
    private final String URL_JSON = "https://gist.githubusercontent.com/Mateusrlohnn/79252ec285898e25e7ad005f3adc9ab2/raw/ca2bd7766511c8891010045b74bdca7021d73cc7/gistfile1.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewCardapio);
        dao = new CardapioDAO(this);

        if (estaOnline()) {
            Toast.makeText(this, "Modo Online: Sincronizando...", Toast.LENGTH_SHORT).show();
            new TaskBaixarCardapio().execute(URL_JSON);
        } else {
            Toast.makeText(this, "Modo Offline: Usando SQLite...", Toast.LENGTH_LONG).show();
            carregarDadosLocais();
        }
    }

    private boolean estaOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void carregarDadosLocais() {
        dadosCardapio = dao.listarTudo();
        CardapioAdapter adapter = new CardapioAdapter(this, dadosCardapio, true);
        listView.setAdapter(adapter);
    }

    private class TaskBaixarCardapio extends AsyncTask<String, Void, ArrayList<ItemCardapio>> {

        @Override
        protected ArrayList<ItemCardapio> doInBackground(String... urls) {
            ArrayList<ItemCardapio> listaBaixada = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String linha;
                while ((linha = reader.readLine()) != null) {
                    buffer.append(linha).append("\n");
                }

                JSONArray jsonArray = new JSONArray(buffer.toString());

                dao.limparTabela();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    ItemCardapio item = new ItemCardapio();
                    item.setNome(obj.getString("nome"));
                    item.setPreco(String.valueOf(obj.getDouble("preco")));

                    item.setCategoria(obj.has("categoria") ? obj.getString("categoria") : "Outros");
                    String urlImagemObtida = "";
                    if (obj.has("imagem")) {
                        urlImagemObtida = obj.getString("imagem");
                    } else if (obj.has("urlImagem")) {
                        urlImagemObtida = obj.getString("urlImagem");
                    }
                    item.setUrlImagem(urlImagemObtida);

                    String nomeArquivoFoto = "prato_" + i + ".png";

                    String caminhoLocal = salvarImagemSistemaArquivos(item.getUrlImagem(), nomeArquivoFoto);
                    item.setCaminhoFotoLocal(caminhoLocal);

                    dao.inserir(item);
                    listaBaixada.add(item);
                }

            } catch (Exception e) {
                Log.e("MainActivity", "Erro na busca remota", e);
                return null;
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
            return listaBaixada;
        }

        @Override
        protected void onPostExecute(ArrayList<ItemCardapio> resultado) {
            if (resultado != null && resultado.size() > 0) {
                CardapioAdapter adapter = new CardapioAdapter(MainActivity.this, resultado, false);
                listView.setAdapter(adapter);
            } else {
                carregarDadosLocais();
            }
        }
    }
    private String salvarImagemSistemaArquivos(String urlUrl, String nomeArquivo) {
        if (urlUrl == null || urlUrl.isEmpty()) return null;
        try {
            InputStream in = new java.net.URL(urlUrl).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            File diretorioInterno = getFilesDir();
            File arquivoFoto = new File(diretorioInterno, nomeArquivo);

            FileOutputStream out = new FileOutputStream(arquivoFoto);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return arquivoFoto.getAbsolutePath();

        } catch (Exception e) {
            Log.e("MainActivity", "Erro ao salvar imagem localmente", e);
            return null;
        }
    }
}