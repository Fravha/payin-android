package com.bylaba.containerqr;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bylaba.containerqr.database.ActividadDAO;
import com.bylaba.containerqr.models.Actividad;

import java.util.ArrayList;

import android.widget.ScrollView;
import android.widget.ImageView;

public class GestionActividadesActivity extends AppCompatActivity {
    private EditText edtNombreActividad;
    private EditText edtDescripcionActividad;
    private Button btnGuardarActividad;
    private Button btnCancelarEdicion;
    private TextView txtTituloFormularioActividad;
    private LinearLayout contenedorListaActividades;
    private ActividadDAO actividadDAO;

    private boolean modoEdicion = false;
    private int actividadIdEditando = -1;

    private final int colorPrimary = 0xFF168447;
    private final int colorPrimaryDark = 0xFF106B38;
    private final int colorSurface = 0xFFF4F7F5;
    private final int colorCard = 0xFFFFFFFF;
    private final int colorText = 0xFF202136;
    private final int colorSecondary = 0xFF68738A;
    private final int colorBorder = 0xFFE8E8F1;
    private final int colorDanger = 0xFFB42318;
    private final int colorDangerBg = 0xFFFFF0EE;
    private final int colorSuccessBg = 0xFFE8F5EC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_actividades);

        View root = findViewById(R.id.scrollGestionActividades);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(view.getPaddingLeft(), bars.top + dp(8), view.getPaddingRight(), bars.bottom + dp(8));
            return windowInsets;
        });

        edtNombreActividad = findViewById(R.id.edtNombreActividad);
        edtDescripcionActividad = findViewById(R.id.edtDescripcionActividad);
        btnGuardarActividad = findViewById(R.id.btnGuardarActividad);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicion);
        txtTituloFormularioActividad = findViewById(R.id.txtTituloFormularioActividad);
        contenedorListaActividades = findViewById(R.id.contenedorListaActividades);
        actividadDAO = new ActividadDAO(this);

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
        btnGuardarActividad.setOnClickListener(v -> guardarActividad());
        btnCancelarEdicion.setOnClickListener(v -> limpiarFormulario());

        cargarActividades();
    }

    private void guardarActividad() {
        String nombre = edtNombreActividad.getText().toString().trim();
        String descripcion = edtDescripcionActividad.getText().toString().trim();

        if (nombre.isEmpty()) {
            edtNombreActividad.setError("El nombre es obligatorio");
            edtNombreActividad.requestFocus();
            return;
        }

        if (modoEdicion) {
            Actividad actividad = actividadDAO.obtenerActividadPorId(actividadIdEditando);
            if (actividad == null) {
                Toast.makeText(this, "No se encontró la actividad a editar", Toast.LENGTH_SHORT).show();
                limpiarFormulario();
                cargarActividades();
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
        edtNombreActividad.setError(null);
        modoEdicion = false;
        actividadIdEditando = -1;
        btnGuardarActividad.setText("Guardar actividad");
        btnCancelarEdicion.setVisibility(View.GONE);
        txtTituloFormularioActividad.setText("Nueva actividad");
        edtNombreActividad.requestFocus();
    }

    private void cargarActividades() {
        contenedorListaActividades.removeAllViews();
        ArrayList<Actividad> actividades = actividadDAO.listarTodasLasActividades();

        if (actividades.isEmpty()) {
            LinearLayout vacio = new LinearLayout(this);
            vacio.setOrientation(LinearLayout.VERTICAL);
            vacio.setGravity(android.view.Gravity.CENTER);
            vacio.setPadding(dp(24), dp(28), dp(24), dp(28));
            vacio.setBackground(background(colorCard, colorBorder, 18));

            TextView icono = new TextView(this);
            icono.setText("＋");
            icono.setTextSize(30);
            icono.setTextColor(colorPrimary);
            icono.setGravity(android.view.Gravity.CENTER);
            vacio.addView(icono, new LinearLayout.LayoutParams(-1, -2));

            TextView mensaje = new TextView(this);
            mensaje.setText("Todavía no hay actividades");
            mensaje.setTextColor(colorText);
            mensaje.setTextSize(15);
            mensaje.setTypeface(null, Typeface.BOLD);
            mensaje.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(-1, -2);
            mp.topMargin = dp(8);
            vacio.addView(mensaje, mp);

            TextView detalle = new TextView(this);
            detalle.setText("Registra la primera actividad usando el formulario.");
            detalle.setTextColor(colorSecondary);
            detalle.setTextSize(13);
            detalle.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(-1, -2);
            dp.topMargin = dp(4);
            vacio.addView(detalle, dp);
            contenedorListaActividades.addView(vacio);
            return;
        }

        for (Actividad actividad : actividades) {
            contenedorListaActividades.addView(crearItemActividad(actividad));
        }
    }

    private View crearItemActividad(Actividad actividad) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(14));
        card.setBackground(background(colorCard, colorBorder, 16));
        card.setElevation(dp(1));
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(-1, -2);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        LinearLayout encabezado = new LinearLayout(this);
        encabezado.setOrientation(LinearLayout.HORIZONTAL);
        encabezado.setGravity(android.view.Gravity.CENTER_VERTICAL);

        ImageView icono = new ImageView(this);
        icono.setImageResource(R.drawable.ic_hero_squares_2x2);
        icono.setColorFilter(colorPrimary);
        icono.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icono.setBackground(background(0xFFE8F5EC, 0xFFE8F5EC, 12));

        encabezado.addView(
                icono,
                new LinearLayout.LayoutParams(dp(46), dp(46))
        );

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setPadding(dp(12), 0, 0, 0);
        LinearLayout.LayoutParams textosParams = new LinearLayout.LayoutParams(0, -2, 1f);
        encabezado.addView(textos, textosParams);

        TextView nombre = new TextView(this);
        nombre.setText(actividad.getNombre());
        nombre.setTextSize(16);
        nombre.setTextColor(colorText);
        nombre.setTypeface(null, Typeface.BOLD);
        textos.addView(nombre);

        TextView estado = new TextView(this);
        estado.setText(actividad.isActivo() ? "●  Activa" : "●  Inactiva");
        estado.setTextSize(12);
        estado.setTypeface(null, Typeface.BOLD);
        estado.setTextColor(actividad.isActivo() ? colorPrimaryDark : colorDanger);
        estado.setPadding(dp(9), dp(5), dp(9), dp(5));
        estado.setBackground(background(actividad.isActivo() ? colorSuccessBg : colorDangerBg,
                actividad.isActivo() ? colorSuccessBg : colorDangerBg, 20));
        LinearLayout.LayoutParams estadoParams = new LinearLayout.LayoutParams(-2, -2);
        estadoParams.topMargin = dp(6);
        textos.addView(estado, estadoParams);

        card.addView(encabezado);

        String descripcion = actividad.getDescripcion();
        if (descripcion == null || descripcion.trim().isEmpty()) descripcion = "Sin descripción";
        TextView detalle = new TextView(this);
        detalle.setText(descripcion);
        detalle.setTextSize(13);
        detalle.setTextColor(colorSecondary);
        detalle.setLineSpacing(dp(2), 1f);
        LinearLayout.LayoutParams detalleParams = new LinearLayout.LayoutParams(-1, -2);
        detalleParams.topMargin = dp(12);
        detalleParams.bottomMargin = dp(14);
        card.addView(detalle, detalleParams);

        View separador = new View(this);
        separador.setBackgroundColor(colorBorder);
        card.addView(separador, new LinearLayout.LayoutParams(-1, dp(1)));

        LinearLayout acciones = new LinearLayout(this);
        acciones.setOrientation(LinearLayout.HORIZONTAL);
        acciones.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams accionesParams = new LinearLayout.LayoutParams(-1, -2);
        accionesParams.topMargin = dp(12);
        card.addView(acciones, accionesParams);

        Button btnEditar = new Button(this);
        btnEditar.setText("Editar");
        btnEditar.setAllCaps(false);
        btnEditar.setTextSize(13);
        btnEditar.setTextColor(colorPrimaryDark);
        btnEditar.setBackgroundTintList(null);
        btnEditar.setBackground(background(0xFFE8F5EC, 0xFFE8F5EC, 10));
        btnEditar.setMinHeight(0);
        btnEditar.setMinimumHeight(dp(42));
        btnEditar.setPadding(dp(12), 0, dp(12), 0);
        acciones.addView(btnEditar, new LinearLayout.LayoutParams(0, dp(42), 1f));
        btnEditar.setOnClickListener(v -> cargarActividadParaEditar(actividad));

        View espacio = new View(this);
        acciones.addView(espacio, new LinearLayout.LayoutParams(dp(10), 1));

        Button btnEstado = new Button(this);
        btnEstado.setText(actividad.isActivo() ? "Desactivar" : "Activar");
        btnEstado.setAllCaps(false);
        btnEstado.setTextSize(13);
        btnEstado.setTextColor(actividad.isActivo() ? colorDanger : colorPrimaryDark);
        btnEstado.setBackgroundTintList(null);
        btnEstado.setBackground(background(actividad.isActivo() ? 0xFFFFF0EE : 0xFFE8F5EC,
                actividad.isActivo() ? 0xFFFFF0EE : 0xFFE8F5EC, 10));
        btnEstado.setMinHeight(0);
        btnEstado.setMinimumHeight(dp(42));
        btnEstado.setPadding(dp(10), 0, dp(10), 0);
        acciones.addView(btnEstado, new LinearLayout.LayoutParams(0, dp(42), 1f));
        btnEstado.setOnClickListener(v -> {
            if (actividad.isActivo()) confirmarDesactivarActividad(actividad);
            else confirmarActivarActividad(actividad);
        });

        return card;
    }

    private void confirmarDesactivarActividad(Actividad actividad) {
        new AlertDialog.Builder(this)
                .setTitle("Desactivar actividad")
                .setMessage("¿Seguro que deseas desactivar \"" + actividad.getNombre() + "\"?")
                .setPositiveButton("Sí, desactivar", (dialog, which) -> {
                    int resultado = actividadDAO.desactivarActividad(actividad.getId());
                    if (resultado > 0) {
                        Mensaje.exito(this, "Actividad desactivada");
                        if (modoEdicion && actividadIdEditando == actividad.getId()) limpiarFormulario();
                        cargarActividades();
                    } else
                        Mensaje.error(this, "No se pudo desactivar");
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarActivarActividad(Actividad actividad) {
        new AlertDialog.Builder(this)
                .setTitle("Activar actividad")
                .setMessage("¿Seguro que deseas activar \"" + actividad.getNombre() + "\"?")
                .setPositiveButton("Sí, activar", (dialog, which) -> {
                    int resultado = actividadDAO.activarActividad(actividad.getId());
                    if (resultado > 0) {
                        Mensaje.exito(this, "Actividad activada");
                        cargarActividades();
                    } else
                        Mensaje.error(this, "No se pudo activar");
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cargarActividadParaEditar(Actividad actividad) {
        modoEdicion = true;
        actividadIdEditando = actividad.getId();
        edtNombreActividad.setText(actividad.getNombre());
        edtDescripcionActividad.setText(actividad.getDescripcion());
        edtNombreActividad.setError(null);
        btnGuardarActividad.setText("Actualizar actividad");
        btnCancelarEdicion.setVisibility(View.VISIBLE);
        txtTituloFormularioActividad.setText("Editar actividad");
        edtNombreActividad.requestFocus();
        ScrollView scrollView = findViewById(R.id.scrollGestionActividades);
        scrollView.smoothScrollTo(0, 0);
        Mensaje.informacion(this, "Editando: "+ actividad.getNombre());
    }

    private GradientDrawable background(int color, int strokeColor, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        drawable.setStroke(dp(1), strokeColor);
        return drawable;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}