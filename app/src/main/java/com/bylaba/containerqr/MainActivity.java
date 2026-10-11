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
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import androidx.core.content.ContextCompat;
import com.google.android.material.card.MaterialCardView;
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

import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private LinearLayout contenedorActividades;
    private ActividadDAO actividadDAO;

    private Button btnConfiguracion;
    private ConfiguracionDAO configuracionDAO;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private View drawerPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Conectamos variables Java con elementos XML
        contenedorActividades = findViewById(R.id.contenedorActividades);
        btnConfiguracion = findViewById(R.id.btnConfiguracion);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        drawerPanel = findViewById(R.id.drawerPanel);

        // Inicializamos el DAO
        actividadDAO = new ActividadDAO(this);
        configuracionDAO = new ConfiguracionDAO(this);

        // Cargamos actividades desde SQLite
        cargarActividades();
        configurarMenuLateral();

        TipoQrDAO tipoQrDAO = new TipoQrDAO(this);
        ArrayList<TipoQr> tipos = tipoQrDAO.listarTiposActivos();

        for (TipoQr tipo : tipos) {
            Log.d("ContainerQR", "Tipo QR: " + tipo.getNombre());
        }

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.drawerPanel),
                (view, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            view.getPaddingLeft(),
                            systemBars.top,
                            view.getPaddingRight(),
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    private void cargarActividades() {
        contenedorActividades.removeAllViews();

        ArrayList<Actividad> actividades = actividadDAO.listarActividadesActivas();

        if (actividades.isEmpty()) {
            Toast.makeText(this, "No hay actividades registradas", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Actividad actividad : actividades) {
            MaterialCardView tarjeta = crearBotonActividad(actividad);
            contenedorActividades.addView(tarjeta);
        }
    }

    private MaterialCardView crearBotonActividad(Actividad actividad) {
        MaterialCardView tarjeta = new MaterialCardView(this);
        tarjeta.setClickable(true);
        tarjeta.setFocusable(true);
        tarjeta.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface_card));
        tarjeta.setStrokeColor(ContextCompat.getColor(this, R.color.border));
        tarjeta.setStrokeWidth(dp(1));
        tarjeta.setRadius(dp(20));
        tarjeta.setCardElevation(dp(1));

        LinearLayout contenido = new LinearLayout(this);
        contenido.setOrientation(LinearLayout.HORIZONTAL);
        contenido.setGravity(android.view.Gravity.CENTER_VERTICAL);
        contenido.setPadding(dp(16), dp(18), dp(16), dp(18));

        ImageView icono = new ImageView(this);
        icono.setImageResource(R.drawable.ic_hero_squares_2x2);
        icono.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icono.setPadding(dp(14), dp(14), dp(14), dp(14));
        icono.setBackground(fondoIcono());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(56), dp(56));
        iconParams.setMarginEnd(dp(14));
        icono.setLayoutParams(iconParams);
        contenido.addView(icono);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams textosParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textos.setLayoutParams(textosParams);

        TextView nombre = new TextView(this);
        nombre.setText(actividad.getNombre());
        nombre.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        nombre.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        nombre.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textos.addView(nombre);

        TextView descripcion = new TextView(this);
        String detalle = actividad.getDescripcion();
        descripcion.setText(detalle == null || detalle.trim().isEmpty() ? "Consultar códigos QR disponibles" : detalle);
        descripcion.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        descripcion.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descParams.topMargin = dp(5);
        descripcion.setLayoutParams(descParams);
        textos.addView(descripcion);
        contenido.addView(textos);

        TextView flecha = new TextView(this);
        flecha.setText("›");
        flecha.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        flecha.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        flecha.setGravity(android.view.Gravity.CENTER);
        contenido.addView(flecha);

        tarjeta.addView(contenido);
        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parametros.bottomMargin = dp(12);
        tarjeta.setLayoutParams(parametros);
        tarjeta.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TipoQrActivity.class);
            intent.putExtra("actividad_id", actividad.getId());
            intent.putExtra("actividad_nombre", actividad.getNombre());
            startActivity(intent);
        });
        return tarjeta;
    }

    private android.graphics.drawable.Drawable fondoIcono() {
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(android.graphics.Color.parseColor("#E4F5E9"));
        fondo.setCornerRadius(dp(16));
        return fondo;
    }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    private void configurarMenuLateral() {

        btnConfiguracion.setOnClickListener(view ->
                drawerLayout.openDrawer(drawerPanel)
        );

        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                drawerLayout.closeDrawer(drawerPanel);

            } else if (id == R.id.nav_configuracion) {
                drawerLayout.closeDrawer(drawerPanel);
                mostrarDialogoPassword();

            } else if (id == R.id.nav_acerca) {
                drawerLayout.closeDrawer(drawerPanel);
                mostrarAcercaDe();
            }

            return true;
        });
    }


    private void mostrarAcercaDe() {
        View contenido = getLayoutInflater()
                .inflate(R.layout.dialogo_acerca_de, null);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setView(contenido)
                .setPositiveButton("Entendido", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();

        dialogo.setOnShowListener(dialog -> {
            if (dialogo.getWindow() != null) {
                dialogo.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                dialogo.getWindow().setDimAmount(0.45f);

                dialogo.getWindow().setLayout(
                        (int) (getResources().getDisplayMetrics().widthPixels * 0.88f),
                        android.view.WindowManager.LayoutParams.WRAP_CONTENT
                );
            }
        });

        dialogo.show();

        if (dialogo.getWindow() != null) {
            dialogo.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            dialogo.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.88f),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void mostrarDialogoPassword() {

        View vista = getLayoutInflater().inflate(
                R.layout.dialogo_password,
                null
        );

        EditText inputPassword = vista.findViewById(R.id.inputPassword);
        TextView txtError = vista.findViewById(R.id.txtErrorPassword);
        TextView btnMostrar = vista.findViewById(R.id.btnMostrarPassword);
        TextView btnCancelar = vista.findViewById(R.id.btnCancelarPassword);
        TextView btnIngresar = vista.findViewById(R.id.btnIngresarPassword);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setView(vista)
                .create();

        // Mostrar u ocultar la contraseña
        btnMostrar.setOnClickListener(v -> {
            int posicion = inputPassword.getSelectionStart();

            if (inputPassword.getTransformationMethod()
                    instanceof android.text.method.PasswordTransformationMethod) {

                inputPassword.setTransformationMethod(
                        android.text.method.HideReturnsTransformationMethod
                                .getInstance()
                );

                btnMostrar.setText("Ocultar contraseña");

            } else {
                inputPassword.setTransformationMethod(
                        android.text.method.PasswordTransformationMethod
                                .getInstance()
                );

                btnMostrar.setText("Mostrar contraseña");
            }

            inputPassword.setSelection(
                    Math.max(0, inputPassword.getText().length())
            );
        });

        // Cancelar el acceso
        btnCancelar.setOnClickListener(v -> dialogo.dismiss());

        // Validar la contraseña e ingresar
        btnIngresar.setOnClickListener(v -> {

            String passwordIngresado = inputPassword.getText()
                    .toString()
                    .trim();

            if (passwordIngresado.isEmpty()) {
                txtError.setText("Ingresa tu contraseña para continuar.");
                txtError.setVisibility(View.VISIBLE);
                inputPassword.requestFocus();
                return;
            }

            if (configuracionDAO.validarPasswordConfiguracion(
                    passwordIngresado
            )) {
                dialogo.dismiss();
                abrirConfiguracion();

            } else {
                txtError.setText("La contraseña es incorrecta.");
                txtError.setVisibility(View.VISIBLE);
                inputPassword.setText("");
                inputPassword.requestFocus();
            }
        });

        dialogo.show();

        if (dialogo.getWindow() != null) {
            dialogo.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }
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