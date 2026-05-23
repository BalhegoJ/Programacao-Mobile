package com.example.atividadem1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textNome, textMatricula, textLotacao, textFuncao, textContador;
    private LinearLayout layoutHoras;
    private Button btnPartilhar;
    private Usuario usuario;
    private List<EditText> listaCampos = new ArrayList<>();
    private String dataAtualStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_principal);

        // Mapeamento dos IDs no XML
        textNome = findViewById(R.id.textNome);
        textMatricula = findViewById(R.id.textMatricula);
        textLotacao = findViewById(R.id.textLotacao);
        textFuncao = findViewById(R.id.textFuncao);
        textContador = findViewById(R.id.textContador);
        layoutHoras = findViewById(R.id.layoutHoras);
        btnPartilhar = findViewById(R.id.btnPartilhar);

        // Recupera os dados do Intent e preenche os 4 TextViews separados
        if (getIntent().hasExtra("USUARIO")) {
            usuario = (Usuario) getIntent().getSerializableExtra("USUARIO");
            textNome.setText("Nome: " + usuario.getNomeCompleto());
            textMatricula.setText("Matrícula: " + usuario.getMatricula());
            textLotacao.setText("Lotação: " + usuario.getLotacao());
            textFuncao.setText("Função: " + usuario.getFuncao());
        }

        btnPartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compartilharDados();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        verificarMudancaDeDia();
        gerarCamposDinamicos();
        restaurarDadosSalvos();
        atualizarContador();
    }

    @Override
    protected void onPause() {
        super.onPause();
        salvarDados();
    }

    private void verificarMudancaDeDia() {
        String hoje = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String diaSalvo = prefs.getString("DATA_SALVA", "");

        if (!hoje.equals(diaSalvo)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.putString("DATA_SALVA", hoje);
            editor.apply();
        }
        dataAtualStr = hoje;
    }

    private void gerarCamposDinamicos() {
        layoutHoras.removeAllViews();
        listaCampos.clear();

        Calendar calendar = Calendar.getInstance();
        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);

        //inclui pausa para almoço entre as 12 e as 13h
        int[] horasUteis = {8, 9, 10, 11, 13, 14, 15, 16};


        for (int horaDaAtividade : horasUteis) {
            // Apenas adiciona o campo se a hora atual for MAIOR que a hora da atividade
            if (horaAtual > horaDaAtividade) {
                adicionarCampoDeHora(horaDaAtividade);
            }
        }
    }

    private void adicionarCampoDeHora(int horaInicio) {
        int horaFim = horaInicio + 1;

        TextView label = new TextView(this);
        label.setText("Atividade " + horaInicio + ":00h - " + horaFim + ":00h:");
        label.setPadding(0, 32, 0, 8);
        label.setTextColor(android.graphics.Color.parseColor("#0F5132"));
        label.setTypeface(null, android.graphics.Typeface.BOLD);

        EditText editText = new EditText(this);
        editText.setHint("Descreva o trabalho...");
        editText.setId(horaInicio);

        editText.setTextColor(android.graphics.Color.parseColor("#000000"));
        editText.setHintTextColor(android.graphics.Color.parseColor("#888888"));

        editText.setBackgroundColor(android.graphics.Color.parseColor("#F1F5F3"));
        editText.setPadding(24, 24, 24, 24);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarContador();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        listaCampos.add(editText);
        layoutHoras.addView(label);
        layoutHoras.addView(editText);
    }

    private void atualizarContador() {
        int preenchidos = 0;
        int total = listaCampos.size();

        for (EditText et : listaCampos) {
            if (!et.getText().toString().trim().isEmpty()) {
                preenchidos++;
            }
        }

        int faltantes = total - preenchidos;
        String mensagem = preenchidos + " horas de trabalho já estão preenchidas.\n" +
                faltantes + " horas de trabalho ainda não foram registradas.";
        textContador.setText(mensagem);
    }

    private void salvarDados() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for (EditText et : listaCampos) {
            String chaveUnica = usuario.getMatricula() + "_HORA_" + et.getId();
            editor.putString(chaveUnica, et.getText().toString());
        }
        editor.apply();
    }

    private void restaurarDadosSalvos() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        for (EditText et : listaCampos) {
            String chaveUnica = usuario.getMatricula() + "_HORA_" + et.getId();
            String textoSalvo = prefs.getString(chaveUnica, "");
            et.setText(textoSalvo);
        }
    }

    private void compartilharDados() {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatório de Atividades - ").append(dataAtualStr).append("\n");
        if (usuario != null) {
            sb.append("Usuário: ").append(usuario.getNomeCompleto()).append("\n\n");
        }

        for (EditText et : listaCampos) {
            int horaInicio = et.getId();
            String descricao = et.getText().toString().trim();
            if (descricao.isEmpty()) {
                descricao = "Não preenchido";
            }
            sb.append(horaInicio).append("h - ").append(horaInicio + 1).append("h: ").append(descricao).append("\n");
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Partilhar via");
        startActivity(shareIntent);
    }
}