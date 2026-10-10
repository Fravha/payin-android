package com.bylaba.containerqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.bylaba.containerqr.database.TipoQrDAO;
import com.bylaba.containerqr.models.TipoQr;

import java.util.ArrayList;

public class TipoQrActivity extends AppCompatActivity {

    private TextView txtActividadSeleccionada;
    private LinearLayout contenedorTiposQr;
    private TipoQrDAO tipoQrDAO;
    private int actividadId;
    private String actividadNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tipo_qr);

        View root = findViewById(R.id.layoutTipoQr);
        final int paddingStart = root.getPaddingStart();
        final int paddingTop = root.getPaddingTop();
        final int paddingEnd = root.getPaddingEnd();
        final int paddingBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );
            view.setPadding(
                    paddingStart + systemBars.left,
                    paddingTop + systemBars.top,
                    paddingEnd + systemBars.right,
                    paddingBottom + systemBars.bottom
            );
            return windowInsets;
        });

        txtActividadSeleccionada = findViewById(R.id.txtActividadSeleccionada);
        contenedorTiposQr = findViewById(R.id.contenedorTiposQr);
        tipoQrDAO = new TipoQrDAO(this);

        recibirDatosActividad();
        cargarTiposQr();
    }

    private void recibirDatosActividad() {
        actividadId = getIntent().getIntExtra("actividad_id", -1);
        actividadNombre = getIntent().getStringExtra("actividad_nombre");

        if (actividadNombre == null || actividadNombre.trim().isEmpty()) {
            actividadNombre = "Actividad no identificada";
        }

        txtActividadSeleccionada.setText(actividadNombre);
    }

    private void cargarTiposQr() {
        contenedorTiposQr.removeAllViews();
        ArrayList<TipoQr> tipos = tipoQrDAO.listarTiposActivos();

        if (tipos.isEmpty()) {
            TextView vacio = new TextView(this);
            vacio.setText("Todavía no hay tipos de QR disponibles.");
            vacio.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            vacio.setTextSize(14);
            vacio.setPadding(dp(4), dp(12), dp(4), dp(12));
            contenedorTiposQr.addView(vacio);
            return;
        }

        for (TipoQr tipoQr : tipos) {
            contenedorTiposQr.addView(crearTarjetaTipoQr(tipoQr));
        }
    }

    private MaterialCardView crearTarjetaTipoQr(TipoQr tipoQr) {
        MaterialCardView tarjeta = new MaterialCardView(this);
        tarjeta.setClickable(true);
        tarjeta.setFocusable(true);
        tarjeta.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface_card));
        tarjeta.setStrokeColor(ContextCompat.getColor(this, R.color.border));
        tarjeta.setStrokeWidth(dp(1));
        tarjeta.setRadius(dp(18));
        tarjeta.setCardElevation(0);

        LinearLayout contenido = new LinearLayout(this);
        contenido.setOrientation(LinearLayout.HORIZONTAL);
        contenido.setGravity(Gravity.CENTER_VERTICAL);
        contenido.setPadding(dp(15), dp(15), dp(15), dp(15));

        ImageView icono = new ImageView(this);
        icono.setImageResource(R.drawable.qr);
        icono.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icono.setPadding(dp(11), dp(11), dp(11), dp(11));
        icono.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_nav_brand_icon));
        icono.setColorFilter(ContextCompat.getColor(this, R.color.primary));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        iconParams.setMarginEnd(dp(14));
        icono.setLayoutParams(iconParams);
        contenido.addView(icono);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams textosParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textos.setLayoutParams(textosParams);

        TextView nombre = new TextView(this);
        nombre.setText(tipoQr.getNombre());
        nombre.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        nombre.setTextSize(16);
        nombre.setTypeface(null, android.graphics.Typeface.BOLD);
        textos.addView(nombre);

        TextView detalle = new TextView(this);
        detalle.setText("Consultar código QR");
        detalle.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        detalle.setTextSize(13);
        LinearLayout.LayoutParams detalleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        detalleParams.topMargin = dp(4);
        detalle.setLayoutParams(detalleParams);
        textos.addView(detalle);
        contenido.addView(textos);

        TextView flecha = new TextView(this);
        flecha.setText("›");
        flecha.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        flecha.setTextSize(28);
        flecha.setGravity(Gravity.CENTER);
        contenido.addView(flecha);

        tarjeta.addView(contenido);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        tarjeta.setLayoutParams(cardParams);

        tarjeta.setOnClickListener(view -> {
            Intent intent = new Intent(TipoQrActivity.this, MostrarQrActivity.class);
            intent.putExtra("actividad_id", actividadId);
            intent.putExtra("actividad_nombre", actividadNombre);
            intent.putExtra("tipo_qr_id", tipoQr.getId());
            intent.putExtra("tipo_qr_nombre", tipoQr.getNombre());
            startActivity(intent);
        });

        return tarjeta;
    }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tipoQrDAO != null) {
            cargarTiposQr();
        }
    }
}