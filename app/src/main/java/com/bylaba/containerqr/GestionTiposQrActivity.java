package com.bylaba.containerqr;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bylaba.containerqr.database.TipoQrDAO;
import com.bylaba.containerqr.models.TipoQr;

import java.util.ArrayList;
import android.widget.ImageView;

public class GestionTiposQrActivity extends AppCompatActivity {

    private EditText edtNombreTipoQr;
    private EditText edtDescripcionTipoQr;
    private Button btnGuardarTipoQr;
    private Button btnCancelarEdicion;
    private TextView txtTituloFormularioTipoQr;
    private TextView txtResumenTiposQr;
    private LinearLayout contenedorListaTiposQr;
    private TipoQrDAO tipoQrDAO;

    private boolean modoEdicion = false;
    private int tipoQrIdEditando = -1;

    private final int colorPrimary = Color.rgb(22, 132, 71);
    private final int colorTextPrimary = Color.rgb(32, 33, 54);
    private final int colorTextSecondary = Color.rgb(104, 115, 138);
    private final int colorBorder = Color.rgb(232, 232, 241);
    private final int colorSuccessBg = Color.rgb(232, 245, 236);
    private final int colorDanger = Color.rgb(176, 0, 32);
    private final int colorDangerBg = Color.rgb(255, 235, 238);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_tipos_qr);

        View root = findViewById(R.id.scrollGestionTiposQr);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(view.getPaddingLeft(), bars.top + dp(8), view.getPaddingRight(), bars.bottom + dp(8));
            return windowInsets;
        });

        edtNombreTipoQr = findViewById(R.id.edtNombreTipoQr);
        edtDescripcionTipoQr = findViewById(R.id.edtDescripcionTipoQr);
        btnGuardarTipoQr = findViewById(R.id.btnGuardarTipoQr);
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicion);
        txtTituloFormularioTipoQr = findViewById(R.id.txtTituloFormularioTipoQr);
        txtResumenTiposQr = findViewById(R.id.txtResumenTiposQr);
        contenedorListaTiposQr = findViewById(R.id.contenedorListaTiposQr);

        tipoQrDAO = new TipoQrDAO(this);

        configurarEventos();
        cargarTiposQr();
    }

    private void configurarEventos() {
        btnGuardarTipoQr.setOnClickListener(view -> guardarTipoQr());
        btnCancelarEdicion.setOnClickListener(view -> limpiarFormulario());

        ImageButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void guardarTipoQr() {
        String nombre = edtNombreTipoQr.getText().toString().trim();
        String descripcion = edtDescripcionTipoQr.getText().toString().trim();

        if (nombre.isEmpty()) {
            edtNombreTipoQr.setError("Ingresa un nombre");
            edtNombreTipoQr.requestFocus();
            return;
        }

        if (modoEdicion) {
            TipoQr tipoQr = tipoQrDAO.obtenerTipoQrPorId(tipoQrIdEditando);
            if (tipoQr == null) {
                Mensaje.advertencia(this, "No se encontró el tipo de QR a editar");
                return;
            }

            tipoQr.setNombre(nombre);
            tipoQr.setDescripcion(descripcion);
            int resultado = tipoQrDAO.actualizarTipoQr(tipoQr);

            if (resultado > 0) {
                Mensaje.exito(this, "Tipo de QR actualizado");
                limpiarFormulario();
                cargarTiposQr();
            } else {
                Mensaje.error(this, "No se pudo actualizar el tipo de QR");
            }
        } else {
            TipoQr tipoQr = new TipoQr(nombre, descripcion, true);
            long resultado = tipoQrDAO.insertarTipoQr(tipoQr);

            if (resultado > 0) {
                Mensaje.exito(this, "Tipo de QR guardado");
                limpiarFormulario();
                cargarTiposQr();
            } else {
                Mensaje.error(this, "No se pudo guardad el tipo de QR");
            }
        }
    }

    private void limpiarFormulario() {
        edtNombreTipoQr.setText("");
        edtDescripcionTipoQr.setText("");
        modoEdicion = false;
        tipoQrIdEditando = -1;
        btnGuardarTipoQr.setText("Guardar tipo de QR");
        txtTituloFormularioTipoQr.setText("Nuevo tipo de QR");
        btnCancelarEdicion.setVisibility(View.GONE);
        edtNombreTipoQr.setError(null);

        ScrollView scrollView = findViewById(R.id.scrollGestionTiposQr);
        scrollView.post(() -> scrollView.smoothScrollTo(0, 0));
    }

    private void cargarTiposQr() {
        contenedorListaTiposQr.removeAllViews();
        ArrayList<TipoQr> tipos = tipoQrDAO.listarTodosLosTipos();

        int activos = 0;
        for (TipoQr tipo : tipos) {
            if (tipo.isActivo()) activos++;
        }
        txtResumenTiposQr.setText(tipos.size() + (tipos.size() == 1 ? " tipo registrado · " : " tipos registrados · ")
                + activos + (activos == 1 ? " activo" : " activos"));

        if (tipos.isEmpty()) {
            TextView txtVacio = new TextView(this);
            txtVacio.setText("Todavía no hay tipos de QR. Registra el primero usando el formulario.");
            txtVacio.setTextSize(14);
            txtVacio.setTextColor(colorTextSecondary);
            txtVacio.setGravity(Gravity.CENTER);
            txtVacio.setPadding(dp(20), dp(24), dp(20), dp(24));
            txtVacio.setBackground(fondo(Color.WHITE, colorBorder, 16));
            contenedorListaTiposQr.addView(txtVacio);
            return;
        }

        for (TipoQr tipoQr : tipos) {
            contenedorListaTiposQr.addView(crearItemTipoQr(tipoQr));
        }
    }

    private View crearItemTipoQr(TipoQr tipoQr) {
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(dp(16), dp(16), dp(16), dp(14));
        tarjeta.setBackground(fondo(Color.WHITE, colorBorder, 16));
        tarjeta.setElevation(dp(1));

        LinearLayout.LayoutParams paramsTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTarjeta.bottomMargin = dp(12);
        tarjeta.setLayoutParams(paramsTarjeta);

        LinearLayout filaTitulo = new LinearLayout(this);
        filaTitulo.setOrientation(LinearLayout.HORIZONTAL);
        filaTitulo.setGravity(Gravity.CENTER_VERTICAL);

        ImageView icono = new ImageView(this);
        icono.setImageResource(R.drawable.qr);
        icono.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icono.setColorFilter(colorPrimary);
        icono.setPadding(dp(9), dp(9), dp(9), dp(9));
        icono.setBackground(fondo(colorSuccessBg, colorSuccessBg, 10));

        filaTitulo.addView(
                icono,
                new LinearLayout.LayoutParams(dp(42), dp(42))
        );

        LinearLayout bloqueTexto = new LinearLayout(this);
        bloqueTexto.setOrientation(LinearLayout.VERTICAL);
        bloqueTexto.setPadding(dp(12), 0, 0, 0);

        TextView txtNombre = new TextView(this);
        txtNombre.setText(tipoQr.getNombre());
        txtNombre.setTextSize(16);
        txtNombre.setTypeface(null, Typeface.BOLD);
        txtNombre.setTextColor(colorTextPrimary);

        TextView txtEstado = new TextView(this);
        txtEstado.setText(tipoQr.isActivo() ? "Activo" : "Inactivo");
        txtEstado.setTextSize(12);
        txtEstado.setTypeface(null, Typeface.BOLD);
        txtEstado.setTextColor(tipoQr.isActivo() ? colorPrimary : colorDanger);
        txtEstado.setBackground(fondo(tipoQr.isActivo() ? colorSuccessBg : colorDangerBg,
                tipoQr.isActivo() ? colorSuccessBg : colorDangerBg, 20));
        txtEstado.setPadding(dp(9), dp(4), dp(9), dp(4));

        bloqueTexto.addView(txtNombre);
        LinearLayout.LayoutParams estadoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        estadoParams.topMargin = dp(5);
        bloqueTexto.addView(txtEstado, estadoParams);

        filaTitulo.addView(bloqueTexto, new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        tarjeta.addView(filaTitulo);

        String descripcion = tipoQr.getDescripcion();
        if (descripcion == null || descripcion.trim().isEmpty()) {
            descripcion = "Sin descripción";
        }

        TextView txtDescripcion = new TextView(this);
        txtDescripcion.setText(descripcion);
        txtDescripcion.setTextSize(13);
        txtDescripcion.setTextColor(colorTextSecondary);
        txtDescripcion.setPadding(0, dp(12), 0, dp(14));
        tarjeta.addView(txtDescripcion);

        LinearLayout filaBotones = new LinearLayout(this);
        filaBotones.setOrientation(LinearLayout.HORIZONTAL);

        Button btnEditar = crearBoton("Editar", colorSuccessBg, colorPrimary);
        Button btnEstado = crearBoton(tipoQr.isActivo() ? "Desactivar" : "Activar",
                tipoQr.isActivo() ? colorDangerBg : colorSuccessBg,
                tipoQr.isActivo() ? colorDanger : colorPrimary);

        LinearLayout.LayoutParams botonParams = new LinearLayout.LayoutParams(
                0, dp(42), 1);
        botonParams.rightMargin = dp(6);
        filaBotones.addView(btnEditar, botonParams);

        LinearLayout.LayoutParams estadoButtonParams = new LinearLayout.LayoutParams(
                0, dp(42), 1);
        estadoButtonParams.leftMargin = dp(6);
        filaBotones.addView(btnEstado, estadoButtonParams);

        btnEditar.setOnClickListener(view -> cargarTipoQrParaEditar(tipoQr));
        btnEstado.setOnClickListener(view -> {
            if (tipoQr.isActivo()) confirmarDesactivarTipoQr(tipoQr);
            else confirmarActivarTipoQr(tipoQr);
        });

        tarjeta.addView(filaBotones);
        return tarjeta;
    }

    private Button crearBoton(String texto, int colorFondo, int colorTexto) {
        Button boton = new Button(this);
        boton.setText(texto);
        boton.setAllCaps(false);
        boton.setTextSize(13);
        boton.setTextColor(colorTexto);
        boton.setBackground(fondo(colorFondo, colorFondo, 10));
        boton.setStateListAnimator(null);
        boton.setPadding(dp(8), 0, dp(8), 0);
        return boton;
    }

    private GradientDrawable fondo(int color, int borde, int radioDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radioDp));
        drawable.setStroke(dp(1), borde);
        return drawable;
    }

    private int dp(float valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void confirmarDesactivarTipoQr(TipoQr tipoQr) {
        new AlertDialog.Builder(this)
                .setTitle("Desactivar tipo de QR")
                .setMessage("¿Seguro que deseas desactivar \"" + tipoQr.getNombre() + "\"?")
                .setPositiveButton("Sí, desactivar", (dialog, which) -> {
                    int resultado = tipoQrDAO.desactivarTipoQr(tipoQr.getId());
                    if (resultado > 0) {
                        Mensaje.exito(this, "Tipo de QR desactivado");
                        if (modoEdicion && tipoQrIdEditando == tipoQr.getId()) limpiarFormulario();
                        cargarTiposQr();
                    } else {
                        Mensaje.error(this, "No se pudo desactivar");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarActivarTipoQr(TipoQr tipoQr) {
        new AlertDialog.Builder(this)
                .setTitle("Activar tipo de QR")
                .setMessage("¿Seguro que deseas activar \"" + tipoQr.getNombre() + "\"?")
                .setPositiveButton("Sí, activar", (dialog, which) -> {
                    int resultado = tipoQrDAO.activarTipoQr(tipoQr.getId());
                    if (resultado > 0) {
                        Mensaje.exito(this, "Tipo de QR activado");
                        cargarTiposQr();
                    } else {
                        Mensaje.error(this, "No se pudo activar");
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
        txtTituloFormularioTipoQr.setText("Editar tipo de QR");
        btnCancelarEdicion.setVisibility(View.VISIBLE);
        edtNombreTipoQr.setError(null);

        ScrollView scrollView = findViewById(R.id.scrollGestionTiposQr);
        scrollView.post(() -> scrollView.smoothScrollTo(0, 0));
        edtNombreTipoQr.requestFocus();
        Toast.makeText(this, "Editando: " + tipoQr.getNombre(), Toast.LENGTH_SHORT).show();
    }
}