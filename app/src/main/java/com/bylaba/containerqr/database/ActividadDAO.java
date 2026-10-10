package com.bylaba.containerqr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bylaba.containerqr.models.Actividad;

import java.util.ArrayList;

public class ActividadDAO {
    private DBHelper dbHelper;

    public ActividadDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insertarActividad(Actividad actividad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", actividad.getNombre());
        values.put("descripcion", actividad.getDescripcion());
        values.put("activo", actividad.isActivo() ? 1 : 0);

        long resultado = db.insert(DBHelper.TABLE_ACTIVIDADES, null, values);
        db.close();

        return resultado;
    }

    public int actualizarActividad(Actividad actividad) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", actividad.getNombre());
        values.put("descripcion", actividad.getDescripcion());
        values.put("activo", actividad.isActivo() ? 1 : 0);

        int filasAfectadas = db.update(
                DBHelper.TABLE_ACTIVIDADES,
                values,
                "id = ?",
                new String[]{String.valueOf(actividad.getId())}
        );

        db.close();
        return filasAfectadas;
    }

    public int desactivarActividad(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("activo", 0);

        int filasAfectadas = db.update(
                DBHelper.TABLE_ACTIVIDADES,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return filasAfectadas;
    }

    public int activarActividad(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("activo", 1);

        int filasAfectadas = db.update(
                DBHelper.TABLE_ACTIVIDADES,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return filasAfectadas;
    }

    public Actividad obtenerActividadPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_ACTIVIDADES,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Actividad actividad = null;

        if (cursor.moveToFirst()) {
            actividad = convertirCursorAActividad(cursor);
        }

        cursor.close();
        db.close();

        return actividad;
    }

    public ArrayList<Actividad> listarActividadesActivas() {
        ArrayList<Actividad> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_ACTIVIDADES,
                null,
                "activo = ?",
                new String[]{"1"},
                null,
                null,
                "nombre ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Actividad actividad = convertirCursorAActividad(cursor);
                lista.add(actividad);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public ArrayList<Actividad> listarTodasLasActividades() {
        ArrayList<Actividad> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_ACTIVIDADES,
                null,
                null,
                null,
                null,
                null,
                "nombre ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                Actividad actividad = convertirCursorAActividad(cursor);
                lista.add(actividad);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    private Actividad convertirCursorAActividad(Cursor cursor) {
        Actividad actividad = new Actividad();

        actividad.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        actividad.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        actividad.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
        actividad.setActivo(cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1);

        return actividad;
    }
}
