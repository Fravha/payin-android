package com.bylaba.containerqr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bylaba.containerqr.database.TipoQrDAO;
import com.bylaba.containerqr.models.TipoQr;

import java.util.ArrayList;

public class GestionTiposQrActivity extends AppCompatActivity {

    private EditText edtNombreTipoQr;
    private EditText edtDescripcionTipoQr;
    private Button btnGuardarTipoQr;
    private LinearLayout contenedorListaTiposQr;

    private TipoQrDAO tipoQrDAO;

    private boolean modoEdicion = false;
    private int tipoQrIdEditando = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_tipos_qr);

        edtNombreTipoQr = findViewById(R.id.edtNombreTipoQr);
        edtDescripcionTipoQr = findViewById(R.id.edtDescripcionTipoQr);
        btnGuardarTipoQr = findViewById(R.id.btnGuardarTipoQr);
        contenedorListaTiposQr = findViewById(R.id.contenedorListaTiposQr);

        tipoQrDAO = new TipoQrDAO(this);

        configurarEventos();
        cargarTiposQr();
    }

    private void configurarEventos() {
        btnGuardarTipoQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarTipoQr();
            }
        });
    }

    private void guardarTipoQr() {
        String nombre = edtNombreTipoQr.getText().toString().trim();
        String descripcion = edtDescripcionTipoQr.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre del tipo de QR", Toast.LENGTH_SHORT).show();
            return;
        }

        if (modoEdicion) {
            TipoQr tipoQr = tipoQrDAO.obtenerTipoQrPorId(tipoQrIdEditando);

            if (tipoQr == null) {
                Toast.makeText(this, "No se encontró el tipo de QR a editar", Toast.LENGTH_SHORT).show();
                return;
            }

            tipoQr.setNombre(nombre);
            tipoQr.setDescripcion(descripcion);

            int resultado = tipoQrDAO.actualizarTipoQr(tipoQr);

            if (resultado > 0) {
                Toast.makeText(this, "Tipo de QR actualizado", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarTiposQr();
            } else {
                Toast.makeText(this, "No se pudo actualizar el tipo de QR", Toast.LENGTH_SHORT).show();
            }

        } else {
            TipoQr tipoQr = new TipoQr(nombre, descripcion, true);

            long resultado = tipoQrDAO.insertarTipoQr(tipoQr);

            if (resultado > 0) {
                Toast.makeText(this, "Tipo de QR guardado", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarTiposQr();
            } else {
                Toast.makeText(this, "No se pudo guardar el tipo de QR", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void limpiarFormulario() {
        edtNombreTipoQr.setText("");
        edtDescripcionTipoQr.setText("");
        edtNombreTipoQr.requestFocus();

        modoEdicion = false;
        tipoQrIdEditando = -1;
        btnGuardarTipoQr.setText("Guardar tipo de QR");
    }

    private void cargarTiposQr() {
        contenedorListaTiposQr.removeAllViews();

        ArrayList<TipoQr> tipos = tipoQrDAO.listarTodosLosTipos();

        if (tipos.isEmpty()) {
            TextView txtVacio = new TextView(this);
            txtVacio.setText("No hay tipos de QR registrados.");
            txtVacio.setTextSize(15);
            txtVacio.setPadding(0, 12, 0, 12);
            contenedorListaTiposQr.addView(txtVacio);
            return;
        }

        for (TipoQr tipoQr : tipos) {
            View item = crearItemTipoQr(tipoQr);
            contenedorListaTiposQr.addView(item);
        }
    }

    private View crearItemTipoQr(TipoQr tipoQr) {
        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(16, 16, 16, 16);
        contenedor.setBackgroundColor(tipoQr.isActivo() ? 0xFFFFFFFF : 0xFFE0E0E0);

        LinearLayout.LayoutParams parametrosContenedor = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parametrosContenedor.setMargins(0, 0, 0, 12);
        contenedor.setLayoutParams(parametrosContenedor);

        TextView txtNombre = new TextView(this);
        txtNombre.setText(tipoQr.getNombre());
        txtNombre.setTextSize(17);
        txtNombre.setTextColor(0xFF222222);
        txtNombre.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView txtDescripcion = new TextView(this);
        String descripcion = tipoQr.getDescripcion();

        if (descripcion == null || descripcion.trim().isEmpty()) {
            descripcion = "Sin descripción";
        }

        txtDescripcion.setText(descripcion);
        txtDescripcion.setTextSize(14);
        txtDescripcion.setTextColor(0xFF666666);
        txtDescripcion.setPadding(0, 4, 0, 8);

        TextView txtEstado = new TextView(this);
        txtEstado.setText(tipoQr.isActivo() ? "Estado: Activo" : "Estado: Inactivo");
        txtEstado.setTextSize(13);
        txtEstado.setTextColor(tipoQr.isActivo() ? 0xFF2E7D32 : 0xFFB00020);
        txtEstado.setPadding(0, 0, 0, 8);

        Button btnEstado = new Button(this);
        btnEstado.setText(tipoQr.isActivo() ? "Desactivar" : "Activar");
        btnEstado.setAllCaps(false);

        btnEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipoQr.isActivo()) {
                    confirmarDesactivarTipoQr(tipoQr);
                } else {
                    confirmarActivarTipoQr(tipoQr);
                }
            }
        });

        Button btnEditar = new Button(this);
        btnEditar.setText("Editar");
        btnEditar.setAllCaps(false);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarTipoQrParaEditar(tipoQr);
            }
        });

        contenedor.addView(txtNombre);
        contenedor.addView(txtDescripcion);
        contenedor.addView(txtEstado);
        contenedor.addView(btnEditar);
        contenedor.addView(btnEstado);

        return contenedor;
    }

    private void confirmarDesactivarTipoQr(TipoQr tipoQr) {
        new AlertDialog.Builder(this)
                .setTitle("Desactivar tipo de QR")
                .setMessage("¿Seguro que deseas desactivar \"" + tipoQr.getNombre() + "\"?")
                .setPositiveButton("Sí, desactivar", (dialogInterface, i) -> {
                    int resultado = tipoQrDAO.desactivarTipoQr(tipoQr.getId());

                    if (resultado > 0) {
                        Toast.makeText(this, "Tipo de QR desactivado", Toast.LENGTH_SHORT).show();
                        cargarTiposQr();
                    } else {
                        Toast.makeText(this, "No se pudo desactivar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarActivarTipoQr(TipoQr tipoQr) {
        new AlertDialog.Builder(this)
                .setTitle("Activar tipo de QR")
                .setMessage("¿Seguro que deseas activar \"" + tipoQr.getNombre() + "\"?")
                .setPositiveButton("Sí, activar", (dialogInterface, i) -> {
                    int resultado = tipoQrDAO.activarTipoQr(tipoQr.getId());

                    if (resultado > 0) {
                        Toast.makeText(this, "Tipo de QR activado", Toast.LENGTH_SHORT).show();
                        cargarTiposQr();
                    } else {
                        Toast.makeText(this, "No se pudo activar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cargarTipoQrParaEditar(TipoQr tipoQr) {
        modoEdicion = true;
        tipoQrIdEditando = tipoQr.getId();

        edtNombreTipoQr.setText(tipoQr.getNombre());
        edtDescripcionTipoQr.setText(tipoQr.getDescripcion());

        btnGuardarTipoQr.setText("Actualizar tipo de QR");
        edtNombreTipoQr.requestFocus();

        Toast.makeText(this, "Editando: " + tipoQr.getNombre(), Toast.LENGTH_SHORT).show();
    }
}