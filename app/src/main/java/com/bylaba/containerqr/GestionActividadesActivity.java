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

import com.bylaba.containerqr.database.ActividadDAO;
import com.bylaba.containerqr.models.Actividad;

import java.util.ArrayList;

public class GestionActividadesActivity extends AppCompatActivity {
    private EditText edtNombreActividad;
    private EditText edtDescripcionActividad;
    private Button btnGuardarActividad;
    private LinearLayout contenedorListaActividades;

    private ActividadDAO actividadDAO;

    private boolean modoEdicion = false;
    private int actividadIdEditando = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_actividades);

        edtNombreActividad = findViewById(R.id.edtNombreActividad);
        edtDescripcionActividad = findViewById(R.id.edtDescripcionActividad);
        btnGuardarActividad = findViewById(R.id.btnGuardarActividad);
        contenedorListaActividades = findViewById(R.id.contenedorListaActividades);

        actividadDAO = new ActividadDAO(this);

        configurarEventos();
        cargarActividades();
    }
    private void configurarEventos() {
        btnGuardarActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarActividad();
            }
        });
    }

    private void guardarActividad() {
        String nombre = edtNombreActividad.getText().toString().trim();
        String descripcion = edtDescripcionActividad.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingresa el nombre de la actividad", Toast.LENGTH_SHORT).show();
            return;
        }

        if (modoEdicion) {
            Actividad actividad = actividadDAO.obtenerActividadPorId(actividadIdEditando);

            if (actividad == null) {
                Toast.makeText(this, "No se encontró la actividad a editar", Toast.LENGTH_SHORT).show();
                return;
            }

            actividad.setNombre(nombre);
            actividad.setDescripcion(descripcion);

            int resultado = actividadDAO.actualizarActividad(actividad);

            if (resultado > 0) {
                Toast.makeText(this, "Actividad actualizada", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarActividades();
            } else {
                Toast.makeText(this, "No se pudo actualizar la actividad", Toast.LENGTH_SHORT).show();
            }

        } else {
            Actividad actividad = new Actividad(nombre, descripcion, true);

            long resultado = actividadDAO.insertarActividad(actividad);

            if (resultado > 0) {
                Toast.makeText(this, "Actividad guardada", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarActividades();
            } else {
                Toast.makeText(this, "No se pudo guardar la actividad", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void limpiarFormulario() {
        edtNombreActividad.setText("");
        edtDescripcionActividad.setText("");
        edtNombreActividad.requestFocus();

        modoEdicion = false;
        actividadIdEditando = -1;
        btnGuardarActividad.setText("Guardar actividad");
    }

    private void cargarActividades() {
        contenedorListaActividades.removeAllViews();

        ArrayList<Actividad> actividades = actividadDAO.listarTodasLasActividades();

        if (actividades.isEmpty()) {
            TextView txtVacio = new TextView(this);
            txtVacio.setText("No hay actividades registradas.");
            txtVacio.setTextSize(15);
            txtVacio.setPadding(0, 12, 0, 12);
            contenedorListaActividades.addView(txtVacio);
            return;
        }

        for (Actividad actividad : actividades) {
            View item = crearItemActividad(actividad);
            contenedorListaActividades.addView(item);
        }
    }

    private View crearItemActividad(Actividad actividad) {
        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(16, 16, 16, 16);
        contenedor.setBackgroundColor(actividad.isActivo() ? 0xFFFFFFFF : 0xFFE0E0E0);

        LinearLayout.LayoutParams parametrosContenedor = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parametrosContenedor.setMargins(0, 0, 0, 12);
        contenedor.setLayoutParams(parametrosContenedor);

        TextView txtNombre = new TextView(this);
        txtNombre.setText(actividad.getNombre());
        txtNombre.setTextSize(17);
        txtNombre.setTextColor(0xFF222222);
        txtNombre.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView txtDescripcion = new TextView(this);
        String descripcion = actividad.getDescripcion();

        if (descripcion == null || descripcion.trim().isEmpty()) {
            descripcion = "Sin descripción";
        }

        txtDescripcion.setText(descripcion);
        txtDescripcion.setTextSize(14);
        txtDescripcion.setTextColor(0xFF666666);
        txtDescripcion.setPadding(0, 4, 0, 8);

        TextView txtEstado = new TextView(this);
        txtEstado.setText(actividad.isActivo() ? "Estado: Activa" : "Estado: Inactiva");
        txtEstado.setTextSize(13);
        txtEstado.setTextColor(actividad.isActivo() ? 0xFF2E7D32 : 0xFFB00020);
        txtEstado.setPadding(0, 0, 0, 8);

        Button btnEstado = new Button(this);
        btnEstado.setText(actividad.isActivo() ? "Desactivar" : "Activar");
        btnEstado.setAllCaps(false);

        btnEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actividad.isActivo()) {
                    confirmarDesactivarActividad(actividad);
                } else {
                    confirmarActivarActividad(actividad);
                }
            }
        });

        Button btnEditar = new Button(this);
        btnEditar.setText("Editar");
        btnEditar.setAllCaps(false);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarActividadParaEditar(actividad);
            }
        });

        contenedor.addView(txtNombre);
        contenedor.addView(txtDescripcion);
        contenedor.addView(txtEstado);
        contenedor.addView(btnEditar);
        contenedor.addView(btnEstado);

        return contenedor;
    }

    private void confirmarDesactivarActividad(Actividad actividad) {
        new AlertDialog.Builder(this)
                .setTitle("Desactivar actividad")
                .setMessage("¿Seguro que deseas desactivar \"" + actividad.getNombre() + "\"?")
                .setPositiveButton("Sí, desactivar", (dialogInterface, i) -> {
                    int resultado = actividadDAO.desactivarActividad(actividad.getId());

                    if (resultado > 0) {
                        Toast.makeText(this, "Actividad desactivada", Toast.LENGTH_SHORT).show();
                        cargarActividades();
                    } else {
                        Toast.makeText(this, "No se pudo desactivar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarActivarActividad(Actividad actividad) {
        new AlertDialog.Builder(this)
                .setTitle("Activar actividad")
                .setMessage("¿Seguro que deseas activar \"" + actividad.getNombre() + "\"?")
                .setPositiveButton("Sí, activar", (dialogInterface, i) -> {
                    int resultado = actividadDAO.activarActividad(actividad.getId());

                    if (resultado > 0) {
                        Toast.makeText(this, "Actividad activada", Toast.LENGTH_SHORT).show();
                        cargarActividades();
                    } else {
                        Toast.makeText(this, "No se pudo activar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cargarActividadParaEditar(Actividad actividad) {
        modoEdicion = true;
        actividadIdEditando = actividad.getId();

        edtNombreActividad.setText(actividad.getNombre());
        edtDescripcionActividad.setText(actividad.getDescripcion());

        btnGuardarActividad.setText("Actualizar actividad");
        edtNombreActividad.requestFocus();

        Toast.makeText(this, "Editando: " + actividad.getNombre(), Toast.LENGTH_SHORT).show();
    }
}