package com.bylaba.containerqr;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bylaba.containerqr.database.TipoQrDAO;
import com.bylaba.containerqr.models.TipoQr;

import java.util.ArrayList;

import android.content.Intent;

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

        txtActividadSeleccionada = findViewById(R.id.txtActividadSeleccionada);
        contenedorTiposQr = findViewById(R.id.contenedorTiposQr);

        tipoQrDAO = new TipoQrDAO(this);

        recibirDatosActividad();
        cargarTiposQr();
    }

    private void recibirDatosActividad() {
        actividadId = getIntent().getIntExtra("actividad_id", -1);
        actividadNombre = getIntent().getStringExtra("actividad_nombre");

        if (actividadNombre == null) {
            actividadNombre = "Actividad no identificada";
        }

        txtActividadSeleccionada.setText("Actividad: " + actividadNombre);
    }

    private void cargarTiposQr() {
        contenedorTiposQr.removeAllViews();

        ArrayList<TipoQr> tipos = tipoQrDAO.listarTiposActivos();

        if (tipos.isEmpty()) {
            Toast.makeText(this, "No hay tipos de QR registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        for (TipoQr tipoQr : tipos) {
            Button boton = crearBotonTipoQr(tipoQr);
            contenedorTiposQr.addView(boton);
        }
    }

    private Button crearBotonTipoQr(TipoQr tipoQr) {
        Button boton = new Button(this);

        boton.setText(tipoQr.getNombre());
        boton.setAllCaps(false);
        boton.setTextSize(18);
        boton.setPadding(16, 16, 16, 16);

        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        parametros.setMargins(0, 0, 0, 16);
        boton.setLayoutParams(parametros);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TipoQrActivity.this, MostrarQrActivity.class);

                intent.putExtra("actividad_id", actividadId);
                intent.putExtra("actividad_nombre", actividadNombre);
                intent.putExtra("tipo_qr_id", tipoQr.getId());
                intent.putExtra("tipo_qr_nombre", tipoQr.getNombre());

                startActivity(intent);
            }
        });

        return boton;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (tipoQrDAO != null) {
            cargarTiposQr();
        }
    }
}