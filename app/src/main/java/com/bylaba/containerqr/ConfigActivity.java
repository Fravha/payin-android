package com.bylaba.containerqr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfigActivity extends AppCompatActivity {

    private Button btnGestionActividades;
    private Button btnGestionTiposQr;
    private Button btnGestionQrs;
    private Button btnCambiarPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config);

        btnGestionActividades = findViewById(R.id.btnGestionActividades);
        btnGestionTiposQr = findViewById(R.id.btnGestionTiposQr);
        btnGestionQrs = findViewById(R.id.btnGestionQrs);
        btnCambiarPassword = findViewById(R.id.btnCambiarPassword);

        configurarEventos();
    }
    private void configurarEventos() {
        btnGestionActividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigActivity.this, GestionActividadesActivity.class);
                startActivity(intent);
            }
        });

        btnGestionTiposQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigActivity.this, GestionTiposQrActivity.class);
                startActivity(intent);
            }
        });

        btnGestionQrs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfigActivity.this, GestionQrsActivity.class);
                startActivity(intent);
            }
        });

        btnCambiarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ConfigActivity.this, "Cambiar contraseña", Toast.LENGTH_SHORT).show();
            }
        });
    }
}