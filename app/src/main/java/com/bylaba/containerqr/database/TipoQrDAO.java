package com.bylaba.containerqr.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bylaba.containerqr.models.TipoQr;

import java.util.ArrayList;

public class TipoQrDAO {
    private DBHelper dbHelper;

    public TipoQrDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insertarTipoQr(TipoQr tipoQr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", tipoQr.getNombre());
        values.put("descripcion", tipoQr.getDescripcion());
        values.put("activo", tipoQr.isActivo() ? 1 : 0);

        long resultado = db.insert(DBHelper.TABLE_TIPOS_QR, null, values);

        db.close();
        return resultado;
    }

    public int actualizarTipoQr(TipoQr tipoQr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", tipoQr.getNombre());
        values.put("descripcion", tipoQr.getDescripcion());
        values.put("activo", tipoQr.isActivo() ? 1 : 0);

        int filasAfectadas = db.update(
                DBHelper.TABLE_TIPOS_QR,
                values,
                "id = ?",
                new String[]{String.valueOf(tipoQr.getId())}
        );

        db.close();
        return filasAfectadas;
    }

    public int desactivarTipoQr(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("activo", 0);

        int filasAfectadas = db.update(
                DBHelper.TABLE_TIPOS_QR,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return filasAfectadas;
    }

    public int activarTipoQr(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("activo", 1);

        int filasAfectadas = db.update(
                DBHelper.TABLE_TIPOS_QR,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return filasAfectadas;
    }

    public TipoQr obtenerTipoQrPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_TIPOS_QR,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        TipoQr tipoQr = null;

        if (cursor.moveToFirst()) {
            tipoQr = convertirCursorATipoQr(cursor);
        }

        cursor.close();
        db.close();

        return tipoQr;
    }

    public ArrayList<TipoQr> listarTiposActivos() {
        ArrayList<TipoQr> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_TIPOS_QR,
                null,
                "activo = ?",
                new String[]{"1"},
                null,
                null,
                "nombre ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                TipoQr tipoQr = convertirCursorATipoQr(cursor);
                lista.add(tipoQr);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public ArrayList<TipoQr> listarTodosLosTipos() {
        ArrayList<TipoQr> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_TIPOS_QR,
                null,
                null,
                null,
                null,
                null,
                "nombre ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                TipoQr tipoQr = convertirCursorATipoQr(cursor);
                lista.add(tipoQr);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    private TipoQr convertirCursorATipoQr(Cursor cursor) {
        TipoQr tipoQr = new TipoQr();

        tipoQr.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        tipoQr.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        tipoQr.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
        tipoQr.setActivo(cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1);

        return tipoQr;
    }
}
