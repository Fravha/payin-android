
package com.bylaba.containerqr;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    private ZoomImageView imgQr;

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

        // Conservamos el padding del XML y sumamos los insets.
        final int paddingLeft = raiz.getPaddingLeft();
        final int paddingTop = raiz.getPaddingTop();
        final int paddingRight = raiz.getPaddingRight();
        final int paddingBottom = raiz.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(raiz, (view, insets) -> {
            Insets barras = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    paddingLeft + barras.left,
                    paddingTop + barras.top,
                    paddingRight + barras.right,
                    paddingBottom + barras.bottom
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

        actividadNombre =
                getIntent().getStringExtra("actividad_nombre");

        tipoQrNombre =
                getIntent().getStringExtra("tipo_qr_nombre");

        if (actividadNombre == null) {
            actividadNombre = "Actividad no identificada";
        }

        if (tipoQrNombre == null) {
            tipoQrNombre = "Tipo no identificado";
        }

        txtDetalleQr.setText(
                actividadNombre + " · " + tipoQrNombre
        );
    }

    private void buscarQr() {
        Qr qr = qrDAO.obtenerQrPorActividadYTipo(
                actividadId,
                tipoQrId
        );

        if (qr == null) {
            mostrarError(
                    "No hay QR configurado para esta actividad y tipo."
            );
            return;
        }

        if (qr.getRutaImagen() == null
                || qr.getRutaImagen().trim().isEmpty()) {

            mostrarError(
                    "El QR existe, pero todavía no tiene una imagen configurada."
            );
            return;
        }

        try {
            imgQr.setImageURI(Uri.parse(qr.getRutaImagen()));

            if (imgQr.getDrawable() == null) {
                mostrarError(
                        "No se pudo cargar la imagen del QR. "
                                + "Vuelve a configurarla desde Gestión de QR."
                );
                return;
            }

            imgQr.setVisibility(View.VISIBLE);

            // Reiniciamos el zoom cuando cargamos el QR.
            imgQr.resetZoom();

            txtMensajeQr.setText(
                    qr.getNombre() == null
                            || qr.getNombre().trim().isEmpty()
                            ? "Código QR disponible"
                            : qr.getNombre()
            );

        } catch (Exception e) {
            mostrarError(
                    "No se pudo cargar la imagen del QR. "
                            + "Vuelve a configurarla desde Gestión de QR."
            );
        }
    }

    private void mostrarError(String mensaje) {
        imgQr.setImageDrawable(null);
        imgQr.setVisibility(View.GONE);
        txtMensajeQr.setText(mensaje);
    }
}