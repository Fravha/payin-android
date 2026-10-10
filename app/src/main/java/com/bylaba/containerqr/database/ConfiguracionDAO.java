package com.bylaba.containerqr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ConfiguracionDAO {
    private DBHelper dbHelper;

    public ConfiguracionDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public String obtenerValor(String clave) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_CONFIGURACIONES,
                new String[]{"valor"},
                "clave = ?",
                new String[]{clave},
                null,
                null,
                null
        );

        String valor = null;

        if (cursor.moveToFirst()) {
            valor = cursor.getString(cursor.getColumnIndexOrThrow("valor"));
        }

        cursor.close();
        db.close();

        return valor;
    }

    public boolean validarPasswordConfiguracion(String passwordIngresado) {
        String passwordGuardado = obtenerValor("password_configuracion");

        if (passwordGuardado == null) {
            return false;
        }

        return passwordGuardado.equals(passwordIngresado);
    }

    public int actualizarValor(String clave, String nuevoValor) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("valor", nuevoValor);

        int filasAfectadas = db.update(
                DBHelper.TABLE_CONFIGURACIONES,
                values,
                "clave = ?",
                new String[]{clave}
        );

        db.close();

        return filasAfectadas;
    }
}
