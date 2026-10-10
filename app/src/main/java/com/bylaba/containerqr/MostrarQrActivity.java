package com.bylaba.containerqr;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bylaba.containerqr.database.QrDAO;
import com.bylaba.containerqr.models.Qr;

public class MostrarQrActivity extends AppCompatActivity {

    private TextView txtDetalleQr;
    private TextView txtMensajeQr;
    private ImageView imgQr;

    private QrDAO qrDAO;

    private int actividadId;
    private int tipoQrId;
    private String actividadNombre;
    private String tipoQrNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mostrar_qr);

        View raiz = findViewById(R.id.layoutMostrarQr);

        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            v.setPadding(
                    barras.left,
                    barras.top,
                    barras.right,
                    barras.bottom
            );

            return insets;
        });

        txtDetalleQr = findViewById(R.id.txtDetalleQr);
        txtMensajeQr = findViewById(R.id.txtMensajeQr);
        imgQr = findViewById(R.id.imgQr);

        qrDAO = new QrDAO(this);

        recibirDatos();
        buscarQr();
    }
    private void recibirDatos() {
        actividadId = getIntent().getIntExtra("actividad_id", -1);
        tipoQrId = getIntent().getIntExtra("tipo_qr_id", -1);

        actividadNombre = getIntent().getStringExtra("actividad_nombre");
        tipoQrNombre = getIntent().getStringExtra("tipo_qr_nombre");

        if (actividadNombre == null) {
            actividadNombre = "Actividad no identificada";
        }

        if (tipoQrNombre == null) {
            tipoQrNombre = "Tipo no identificado";
        }

        txtDetalleQr.setText(actividadNombre + " - " + tipoQrNombre);
    }

    private void buscarQr() {
        Qr qr = qrDAO.obtenerQrPorActividadYTipo(actividadId, tipoQrId);

        if (qr == null) {
            imgQr.setVisibility(View.GONE);
            txtMensajeQr.setText("No hay QR configurado para esta actividad y tipo de pago.");
            return;
        }

        if (qr.getRutaImagen() == null || qr.getRutaImagen().isEmpty()) {
            imgQr.setVisibility(View.GONE);
            txtMensajeQr.setText("El QR existe, pero no tiene imagen configurada.");
            return;
        }

        try {
            imgQr.setVisibility(View.VISIBLE);
            imgQr.setImageURI(Uri.parse(qr.getRutaImagen()));
            txtMensajeQr.setText(qr.getNombre());
        } catch (Exception e) {
            imgQr.setVisibility(View.GONE);
            txtMensajeQr.setText("No se pudo cargar la imagen del QR. Vuelve a configurarla desde Gestión de QR.");
        }
    }
}