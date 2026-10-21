package com.bylaba.containerqr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

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

        btnCambiarPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoCambiarPassword();
            }
        });
    }

    private void mostrarDialogoCambiarPassword() {

        // Crear los campos de contraseña
        EditText etPasswordActual = new EditText(this);
        etPasswordActual.setHint("Contraseña actual");
        etPasswordActual.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        EditText etPasswordNueva = new EditText(this);
        etPasswordNueva.setHint("Nueva contraseña");
        etPasswordNueva.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        EditText etConfirmarPassword = new EditText(this);
        etConfirmarPassword.setHint("Confirmar nueva contraseña");
        etConfirmarPassword.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        // Contenedor vertical
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (20 * getResources()
                .getDisplayMetrics().density);

        layout.setPadding(padding, 0, padding, 0);

        layout.addView(etPasswordActual);
        layout.addView(etPasswordNueva);
        layout.addView(etConfirmarPassword);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cambiar contraseña")
                .setMessage("Introduce tu contraseña actual y la nueva.")
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", null)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        String actual = etPasswordActual.getText()
                                .toString();

                        String nueva = etPasswordNueva.getText()
                                .toString();

                        String confirmar = etConfirmarPassword.getText()
                                .toString();

                        // Validar campos vacíos
                        if (actual.isEmpty() ||
                                nueva.isEmpty() ||
                                confirmar.isEmpty()) {

                            Toast.makeText(
                                    this,
                                    "Completa todos los campos",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        // Validar contraseña actual
                        ConfiguracionDAO dao =
                                new ConfiguracionDAO(this);

                        if (!dao.validarPasswordConfiguracion(actual)) {

                            etPasswordActual.setError(
                                    "Contraseña incorrecta"
                            );

                            etPasswordActual.requestFocus();
                            return;
                        }

                        // Evitar una contraseña demasiado corta
                        if (nueva.length() < 6) {

                            etPasswordNueva.setError(
                                    "Usa al menos 6 caracteres"
                            );

                            etPasswordNueva.requestFocus();
                            return;
                        }

                        // Confirmar que ambas coincidan
                        if (!nueva.equals(confirmar)) {

                            etConfirmarPassword.setError(
                                    "Las contraseñas no coinciden"
                            );

                            etConfirmarPassword.requestFocus();
                            return;
                        }

                        // Guardar en SQLite
                        int filas = dao.actualizarValor(
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
}