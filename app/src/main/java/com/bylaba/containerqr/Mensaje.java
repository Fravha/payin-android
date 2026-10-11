
package com.bylaba.containerqr;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public final class Mensaje {

    private Mensaje() {
        // Evita crear instancias de esta clase.
    }

    public static void exito(Context context, String mensaje) {
        mostrar(context, mensaje,
                R.drawable.ic_check_circle, R.color.primary);
    }

    public static void advertencia(Context context, String mensaje) {
        mostrar(context, mensaje,
                R.drawable.ic_warning, Color.rgb(183, 121, 31));
    }

    public static void error(Context context, String mensaje) {
        mostrar(context, mensaje,
                R.drawable.ic_error, Color.rgb(220, 38, 38));
    }

    public static void informacion(Context context, String mensaje) {
        mostrar(context, mensaje,
                R.drawable.ic_info, Color.rgb(37, 99, 235));
    }

    private static void mostrar(
            Context context,
            String mensaje,
            int recursoIcono,
            int color
    ) {
        View vista = LayoutInflater.from(context)
                .inflate(R.layout.view_mensaje_toast, null);

        ImageView imgIcono = vista.findViewById(R.id.imgIconoToast);
        TextView txtMensaje = vista.findViewById(R.id.txtMensajeToast);

        imgIcono.setImageResource(recursoIcono);
        imgIcono.setColorFilter(
                ContextCompat.getColor(context, color)
        );
        txtMensaje.setText(mensaje);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                0,
                100
        );
        toast.setView(vista);
        toast.show();
    }
}