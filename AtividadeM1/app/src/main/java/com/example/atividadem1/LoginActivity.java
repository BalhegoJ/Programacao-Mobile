package com.example.atividadem1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_de_login);

        EditText editUsername = findViewById(R.id.editUsername);
        EditText editPassword = findViewById(R.id.editPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        // 🤫
        TextView tituloSecret = findViewById(R.id.textTituloSecret);
        tituloSecret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Release 1.0: da nota 10 pra nos professor Rafael kkkkkk", Toast.LENGTH_LONG).show();
            }
        });
        // ==========================================

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = editUsername.getText().toString();
                String pass = editPassword.getText().toString();

                //Condição de Login
                if (user.equals("JoaoBalhego") && pass.equals("1234")) {
                    Usuario usuarioObj = new Usuario("João victor Da Rosa Balhego", "8329699", "TI", "QA");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USUARIO", usuarioObj);
                    startActivity(intent);
                    finish();
                } else if (user.equals("MateusLohn") && pass.equals("0000")) {
                    Usuario usuarioObj = new Usuario("Mateus Lohn Rachadel", "654321", "TI", "Desenvolvedor");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USUARIO", usuarioObj);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciais inválidas!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}