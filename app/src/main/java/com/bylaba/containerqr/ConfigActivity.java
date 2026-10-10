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

import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.bylaba.containerqr.database.ConfiguracionDAO;

import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.bylaba.containerqr.database.ConfiguracionDAO;

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

        btnCambiarPassword.setOnClickListener(view -> {
            mostrarDialogoCambiarPassword();
        });
    }


    private void mostrarDialogoCambiarPassword() {
        ConfiguracionDAO configuracionDAO = new ConfiguracionDAO(this);

        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);

        int padding = Math.round(
                24 * getResources().getDisplayMetrics().density
        );
        contenedor.setPadding(padding, padding / 2, padding, 0);

        EditText inputActual = crearCampoPassword(
                "Contraseña actual"
        );
        EditText inputNueva = crearCampoPassword(
                "Nueva contraseña"
        );
        EditText inputConfirmacion = crearCampoPassword(
                "Confirmar nueva contraseña"
        );

        contenedor.addView(inputActual);
        contenedor.addView(inputNueva);

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) inputNueva.getLayoutParams();
        params.bottomMargin = padding / 3;
        inputNueva.setLayoutParams(params);

        contenedor.addView(inputConfirmacion);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cambiar contraseña")
                .setMessage("Ingresa tus datos para actualizar la contraseña.")
                .setView(contenedor)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(ignored -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {
                        String actual = inputActual.getText().toString();
                        String nueva = inputNueva.getText().toString();
                        String confirmacion =
                                inputConfirmacion.getText().toString();

                        if (actual.trim().isEmpty()
                                || nueva.trim().isEmpty()
                                || confirmacion.trim().isEmpty()) {
                            inputActual.setError(
                                    "Completa todos los campos"
                            );
                            return;
                        }

                        if (!configuracionDAO
                                .validarPasswordConfiguracion(actual)) {
                            inputActual.setError(
                                    "La contraseña actual es incorrecta"
                            );
                            return;
                        }

                        if (nueva.length() < 8) {
                            inputNueva.setError(
                                    "Usa al menos 8 caracteres"
                            );
                            return;
                        }

                        if (!nueva.equals(confirmacion)) {
                            inputConfirmacion.setError(
                                    "Las contraseñas no coinciden"
                            );
                            return;
                        }

                        int filas = configuracionDAO.actualizarValor(
                                "password_configuracion",
                                nueva
                        );

                        if (filas > 0) {
                            Toast.makeText(
                                    this,
                                    "Contraseña actualizada correctamente",
                                    Toast.LENGTH_LONG
                            ).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(
                                    this,
                                    "No se pudo actualizar la contraseña",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        dialog.show();
    }

    private EditText crearCampoPassword(String hint) {
        EditText campo = new EditText(this);
        campo.setHint(hint);
        campo.setSingleLine(true);
        campo.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        return campo;
    }
}