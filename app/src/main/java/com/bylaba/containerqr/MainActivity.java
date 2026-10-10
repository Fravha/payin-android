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
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.widget.EditText;

import com.bylaba.containerqr.database.ActividadDAO;
import com.bylaba.containerqr.models.Actividad;

import java.util.ArrayList;

import android.util.Log;

import com.bylaba.containerqr.database.TipoQrDAO;
import com.bylaba.containerqr.models.TipoQr;

import com.bylaba.containerqr.database.ConfiguracionDAO;

public class MainActivity extends AppCompatActivity {

    private LinearLayout contenedorActividades;
    private ActividadDAO actividadDAO;

    private Button btnConfiguracion;
    private ConfiguracionDAO configuracionDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Conectamos variables Java con elementos XML
        contenedorActividades = findViewById(R.id.contenedorActividades);
        btnConfiguracion = findViewById(R.id.btnConfiguracion);

        // Inicializamos el DAO
        actividadDAO = new ActividadDAO(this);
        configuracionDAO = new ConfiguracionDAO(this);

        // Cargamos actividades desde SQLite
        cargarActividades();
        configurarBotonConfiguracion();

        TipoQrDAO tipoQrDAO = new TipoQrDAO(this);
        ArrayList<TipoQr> tipos = tipoQrDAO.listarTiposActivos();

        for (TipoQr tipo : tipos) {
            Log.d("ContainerQR", "Tipo QR: " + tipo.getNombre());
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*QrDAO qrDAO = new QrDAO(this);

        Qr qrPrueba = new Qr(
                1,
                1,
                "QR Librería con factura",
                "ruta_de_prueba.png",
                true
        );

        long resultado = qrDAO.insertarQr(qrPrueba);

        Log.d("ContainerQR", "QR insertado con ID: " + resultado);

        Qr qrBuscado = qrDAO.obtenerQrPorActividadYTipo(1, 1);

        if (qrBuscado != null) {
            Log.d("ContainerQR", "QR encontrado: " + qrBuscado.getNombre());
            Log.d("ContainerQR", "Ruta imagen: " + qrBuscado.getRutaImagen());
        } else {
            Log.d("ContainerQR", "No se encontró QR");
        }*/
    }

    private void cargarActividades() {
        contenedorActividades.removeAllViews();

        ArrayList<Actividad> actividades = actividadDAO.listarActividadesActivas();

        if (actividades.isEmpty()) {
            Toast.makeText(this, "No hay actividades registradas", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Actividad actividad : actividades) {
            Button boton = crearBotonActividad(actividad);
            contenedorActividades.addView(boton);
        }
    }

    private Button crearBotonActividad(Actividad actividad) {
        Button boton = new Button(this);

        boton.setText(actividad.getNombre());
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
                Intent intent = new Intent(MainActivity.this, TipoQrActivity.class);
                intent.putExtra("actividad_id", actividad.getId());
                intent.putExtra("actividad_nombre", actividad.getNombre());
                startActivity(intent);
            }
        });

        return boton;
    }

    private void configurarBotonConfiguracion() {
        btnConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoPassword();
            }
        });
    }

    private void mostrarDialogoPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acceso a configuración");
        builder.setMessage("Ingresa la contraseña para continuar");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Contraseña");

        builder.setView(input);

        builder.setPositiveButton("Ingresar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String passwordIngresado = input.getText().toString().trim();

                if (configuracionDAO.validarPasswordConfiguracion(passwordIngresado)) {
                    abrirConfiguracion();
                } else {
                    Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();
    }
    private void abrirConfiguracion() {
        Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (actividadDAO != null) {
            cargarActividades();
        }
    }
}