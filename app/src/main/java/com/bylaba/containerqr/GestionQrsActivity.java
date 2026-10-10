package com.bylaba.containerqr;

import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bylaba.containerqr.database.ActividadDAO;
import com.bylaba.containerqr.database.TipoQrDAO;
import com.bylaba.containerqr.database.QrDAO;
import com.bylaba.containerqr.models.Actividad;
import com.bylaba.containerqr.models.TipoQr;
import com.bylaba.containerqr.models.Qr;

import java.util.ArrayList;

public class GestionQrsActivity extends AppCompatActivity {

    private Spinner spinnerActividades;
    private Spinner spinnerTiposQr;
    private ImageView imgPreviewQr;
    private Button btnSeleccionarImagen;
    private Button btnGuardarQr;
    private TextView txtEstadoQr;

    private ActividadDAO actividadDAO;
    private TipoQrDAO tipoQrDAO;
    private QrDAO qrDAO;

    private ArrayList<Actividad> listaActividades;
    private ArrayList<TipoQr> listaTiposQr;

    private Uri imagenSeleccionadaUri;

    private ActivityResultLauncher<String[]> seleccionarImagenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestion_qrs);

        spinnerActividades = findViewById(R.id.spinnerActividades);
        spinnerTiposQr = findViewById(R.id.spinnerTiposQr);
        imgPreviewQr = findViewById(R.id.imgPreviewQr);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnGuardarQr = findViewById(R.id.btnGuardarQr);
        txtEstadoQr = findViewById(R.id.txtEstadoQr);

        actividadDAO = new ActividadDAO(this);
        tipoQrDAO = new TipoQrDAO(this);
        qrDAO = new QrDAO(this);

        configurarSelectorImagen();
        cargarSpinners();

        btnSeleccionarImagen.setOnClickListener(v -> {
            seleccionarImagenLauncher.launch(new String[]{"image/*"});
        });

        btnGuardarQr.setOnClickListener(v -> {
            guardarQr();
        });

        spinnerActividades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                cargarQrExistente();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerTiposQr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                cargarQrExistente();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void configurarSelectorImagen() {
        seleccionarImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;

                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                        imagenSeleccionadaUri = uri;
                        imgPreviewQr.setImageURI(uri);
                        txtEstadoQr.setText("Imagen seleccionada. Presione Guardar / Actualizar.");
                    }
                }
        );
    }

    private void cargarSpinners() {
        listaActividades = actividadDAO.listarActividadesActivas();
        listaTiposQr = tipoQrDAO.listarTiposActivos();

        ArrayList<String> nombresActividades = new ArrayList<>();
        for (Actividad actividad : listaActividades) {
            nombresActividades.add(actividad.getNombre());
        }

        ArrayList<String> nombresTiposQr = new ArrayList<>();
        for (TipoQr tipoQr : listaTiposQr) {
            nombresTiposQr.add(tipoQr.getNombre());
        }

        ArrayAdapter<String> adapterActividades = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombresActividades
        );
        adapterActividades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterTiposQr = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombresTiposQr
        );
        adapterTiposQr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerActividades.setAdapter(adapterActividades);
        spinnerTiposQr.setAdapter(adapterTiposQr);
    }

    private void cargarQrExistente() {
        if (listaActividades == null || listaActividades.isEmpty()) {
            return;
        }

        if (listaTiposQr == null || listaTiposQr.isEmpty()) {
            return;
        }

        int posicionActividad = spinnerActividades.getSelectedItemPosition();
        int posicionTipoQr = spinnerTiposQr.getSelectedItemPosition();

        if (posicionActividad < 0 || posicionTipoQr < 0) {
            return;
        }

        Actividad actividadSeleccionada = listaActividades.get(posicionActividad);
        TipoQr tipoQrSeleccionado = listaTiposQr.get(posicionTipoQr);

        Qr qr = qrDAO.obtenerQrPorActividadYTipo(
                actividadSeleccionada.getId(),
                tipoQrSeleccionado.getId()
        );

        if (qr != null && qr.getRutaImagen() != null && !qr.getRutaImagen().isEmpty()) {
            imagenSeleccionadaUri = Uri.parse(qr.getRutaImagen());

            try {
                imgPreviewQr.setImageURI(imagenSeleccionadaUri);
                txtEstadoQr.setText("QR existente: " + qr.getNombre());
            } catch (SecurityException e) {
                imagenSeleccionadaUri = null;
                imgPreviewQr.setImageDrawable(null);
                txtEstadoQr.setText("El QR existe, pero la app ya no tiene permiso para leer la imagen. Vuelve a cargarla.");
            } catch (Exception e) {
                imagenSeleccionadaUri = null;
                imgPreviewQr.setImageDrawable(null);
                txtEstadoQr.setText("No se pudo cargar la imagen existente. Vuelve a cargarla.");
            }

        } else {
            imagenSeleccionadaUri = null;
            imgPreviewQr.setImageDrawable(null);
            txtEstadoQr.setText("No existe QR para esta combinación.");
        }
    }

    private void guardarQr() {
        if (listaActividades == null || listaActividades.isEmpty()) {
            Toast.makeText(this, "No hay actividades activas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listaTiposQr == null || listaTiposQr.isEmpty()) {
            Toast.makeText(this, "No hay tipos QR activos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenSeleccionadaUri == null) {
            Toast.makeText(this, "Selecciona una imagen QR", Toast.LENGTH_SHORT).show();
            return;
        }

        int posicionActividad = spinnerActividades.getSelectedItemPosition();
        int posicionTipoQr = spinnerTiposQr.getSelectedItemPosition();

        Actividad actividadSeleccionada = listaActividades.get(posicionActividad);
        TipoQr tipoQrSeleccionado = listaTiposQr.get(posicionTipoQr);

        String nombreQr = actividadSeleccionada.getNombre() + " - " + tipoQrSeleccionado.getNombre();

        Qr qr = new Qr(
                actividadSeleccionada.getId(),
                tipoQrSeleccionado.getId(),
                nombreQr,
                imagenSeleccionadaUri.toString(),
                true
        );

        long resultado = qrDAO.guardarOActualizarQr(qr);

        if (resultado > 0) {
            Toast.makeText(this, "QR guardado correctamente", Toast.LENGTH_SHORT).show();
            txtEstadoQr.setText("QR guardado: " + nombreQr);
        } else {
            Toast.makeText(this, "No se pudo guardar el QR", Toast.LENGTH_SHORT).show();
        }
    }
}