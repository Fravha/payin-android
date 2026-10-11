package com.bylaba.containerqr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bylaba.containerqr.database.ConfiguracionDAO;

public class CambiarPasswordActivity extends AppCompatActivity {

    private EditText edtPasswordActual;
    private EditText edtPasswordNueva;
    private EditText edtPasswordConfirmacion;
    private Button btnGuardarPassword;
    private Button btnCancelarPassword;
    private ImageButton btnVolverPassword;
    private ConfiguracionDAO configuracionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cambiar_password);

        View root = findViewById(R.id.scrollCambiarPassword);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(view.getPaddingLeft(), bars.top + (8), view.getPaddingRight(), bars.bottom + (8));
            return windowInsets;
        });

        configurarInsets();

        edtPasswordActual = findViewById(R.id.edtPasswordActual);
        edtPasswordNueva = findViewById(R.id.edtPasswordNueva);
        edtPasswordConfirmacion = findViewById(R.id.edtPasswordConfirmacion);
        btnGuardarPassword = findViewById(R.id.btnGuardarPassword);
        btnCancelarPassword = findViewById(R.id.btnCancelarPassword);
        btnVolverPassword = findViewById(R.id.btnVolverPassword);

        configuracionDAO = new ConfiguracionDAO(this);

        btnGuardarPassword.setOnClickListener(v -> cambiarPassword());
        btnCancelarPassword.setOnClickListener(v -> finish());
        btnVolverPassword.setOnClickListener(v -> finish());
    }

    private void configurarInsets() {
        final android.view.View root = findViewById(R.id.scrollCambiarPassword);
        final int left = root.getPaddingLeft();
        final int top = root.getPaddingTop();
        final int right = root.getPaddingRight();
        final int bottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(left + bars.left, top + bars.top,
                    right + bars.right, bottom + bars.bottom);
            return insets;
        });
    }

    private void cambiarPassword() {
        String actual = edtPasswordActual.getText().toString();
        String nueva = edtPasswordNueva.getText().toString();
        String confirmacion = edtPasswordConfirmacion.getText().toString();

        if (actual.trim().isEmpty()) {
            edtPasswordActual.setError("Ingresa tu contraseña actual");
            edtPasswordActual.requestFocus();
            return;
        }

        if (nueva.trim().isEmpty()) {
            edtPasswordNueva.setError("Ingresa una nueva contraseña");
            edtPasswordNueva.requestFocus();
            return;
        }

        if (confirmacion.trim().isEmpty()) {
            edtPasswordConfirmacion.setError("Confirma la nueva contraseña");
            edtPasswordConfirmacion.requestFocus();
            return;
        }

        if (!configuracionDAO.validarPasswordConfiguracion(actual)) {
            edtPasswordActual.setError("La contraseña actual es incorrecta");
            edtPasswordActual.requestFocus();
            return;
        }

        if (nueva.length() < 8) {
            edtPasswordNueva.setError("Usa al menos 8 caracteres");
            edtPasswordNueva.requestFocus();
            return;
        }

        if (!nueva.equals(confirmacion)) {
            edtPasswordConfirmacion.setError("Las contraseñas no coinciden");
            edtPasswordConfirmacion.requestFocus();
            return;
        }

        int filas = configuracionDAO.actualizarValor("password_configuracion", nueva);

        if (filas > 0) {
            Mensaje.exito(this, "Contraseña actualizada correctamente");
            finish();
        } else {
            Mensaje.error(this, "No se pudo actualizar la contraseña");
        }
    }
}
