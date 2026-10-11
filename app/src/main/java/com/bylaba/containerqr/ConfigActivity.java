package com.bylaba.containerqr;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class ConfigActivity extends AppCompatActivity {

    private Button btnGestionActividades;
    private Button btnGestionTiposQr;
    private Button btnGestionQrs;

    private MaterialCardView cardGestionActividades;
    private MaterialCardView cardGestionTiposQr;
    private MaterialCardView cardGestionQrs;
    private MaterialCardView cardCambiarPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_config);

        View layoutConfig = findViewById(R.id.layoutConfig);

        final int paddingLeft = layoutConfig.getPaddingLeft();
        final int paddingTop = layoutConfig.getPaddingTop();
        final int paddingRight = layoutConfig.getPaddingRight();
        final int paddingBottom = layoutConfig.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                layoutConfig,
                (view, insets) -> {
                    Insets bars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            paddingLeft + bars.left,
                            paddingTop + bars.top,
                            paddingRight + bars.right,
                            paddingBottom + bars.bottom
                    );

                    return insets;
                }
        );

        btnGestionActividades =
                findViewById(R.id.btnGestionActividades);

        btnGestionTiposQr =
                findViewById(R.id.btnGestionTiposQr);

        btnGestionQrs =
                findViewById(R.id.btnGestionQrs);

        cardGestionActividades =
                findViewById(R.id.cardGestionActividades);

        cardGestionTiposQr =
                findViewById(R.id.cardGestionTiposQr);

        cardGestionQrs =
                findViewById(R.id.cardGestionQrs);

        cardCambiarPassword =
                findViewById(R.id.cardCambiarPassword);

        configurarEventos();
    }

    private void configurarEventos() {

        View.OnClickListener abrirActividades = view -> {
            Intent intent = new Intent(
                    ConfigActivity.this,
                    GestionActividadesActivity.class
            );
            startActivity(intent);
        };

        View.OnClickListener abrirTiposQr = view -> {
            Intent intent = new Intent(
                    ConfigActivity.this,
                    GestionTiposQrActivity.class
            );
            startActivity(intent);
        };

        View.OnClickListener abrirQrs = view -> {
            Intent intent = new Intent(
                    ConfigActivity.this,
                    GestionQrsActivity.class
            );
            startActivity(intent);
        };

        View.OnClickListener cambiarPassword = view -> {
            Intent intent = new Intent(
                    ConfigActivity.this,
                    CambiarPasswordActivity.class
            );
            startActivity(intent);
        };

        // Gestión de actividades
        cardGestionActividades.setOnClickListener(abrirActividades);
        btnGestionActividades.setOnClickListener(abrirActividades);

        // Gestión de tipos QR
        cardGestionTiposQr.setOnClickListener(abrirTiposQr);
        btnGestionTiposQr.setOnClickListener(abrirTiposQr);

        // Gestión de imágenes QR
        cardGestionQrs.setOnClickListener(abrirQrs);
        btnGestionQrs.setOnClickListener(abrirQrs);

        // Cambio de contraseña
        cardCambiarPassword.setOnClickListener(cambiarPassword);

        Button btnCambiarPassword = findViewById(R.id.btnCambiarPassword);
        btnCambiarPassword.setOnClickListener(cambiarPassword);
    }
}