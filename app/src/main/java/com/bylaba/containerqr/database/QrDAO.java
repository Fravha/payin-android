package com.bylaba.containerqr.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bylaba.containerqr.models.Qr;

import java.util.ArrayList;

public class QrDAO {

    private DBHelper dbHelper;

    public QrDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public long insertarQr(Qr qr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("actividad_id", qr.getActividadId());
        values.put("tipo_qr_id", qr.getTipoQrId());
        values.put("nombre", qr.getNombre());
        values.put("ruta_imagen", qr.getRutaImagen());
        values.put("activo", qr.isActivo() ? 1 : 0);

        long resultado = db.insert(DBHelper.TABLE_QRS, null, values);

        db.close();
        return resultado;
    }

    public int actualizarQr(Qr qr) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("actividad_id", qr.getActividadId());
        values.put("tipo_qr_id", qr.getTipoQrId());
        values.put("nombre", qr.getNombre());
        values.put("ruta_imagen", qr.getRutaImagen());
        values.put("activo", qr.isActivo() ? 1 : 0);

        int filasAfectadas = db.update(
                DBHelper.TABLE_QRS,
                values,
                "id = ?",
                new String[]{String.valueOf(qr.getId())}
        );

        db.close();
        return filasAfectadas;
    }

    public int desactivarQr(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("activo", 0);

        int filasAfectadas = db.update(
                DBHelper.TABLE_QRS,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
        return filasAfectadas;
    }

    public Qr obtenerQrPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_QRS,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Qr qr = null;

        if (cursor.moveToFirst()) {
            qr = convertirCursorAQr(cursor);
        }

        cursor.close();
        db.close();

        return qr;
    }

    public Qr obtenerQrPorActividadYTipo(int actividadId, int tipoQrId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_QRS,
                null,
                "actividad_id = ? AND tipo_qr_id = ? AND activo = ?",
                new String[]{
                        String.valueOf(actividadId),
                        String.valueOf(tipoQrId),
                        "1"
                },
                null,
                null,
                null
        );

        Qr qr = null;

        if (cursor.moveToFirst()) {
            qr = convertirCursorAQr(cursor);
        }

        cursor.close();
        db.close();

        return qr;
    }

    public ArrayList<Qr> listarQrs() {
        ArrayList<Qr> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_QRS,
                null,
                null,
                null,
                null,
                null,
                "id DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                Qr qr = convertirCursorAQr(cursor);
                lista.add(qr);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public ArrayList<Qr> listarQrsPorActividad(int actividadId) {
        ArrayList<Qr> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DBHelper.TABLE_QRS,
                null,
                "actividad_id = ? AND activo = ?",
                new String[]{String.valueOf(actividadId), "1"},
                null,
                null,
                "id DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                Qr qr = convertirCursorAQr(cursor);
                lista.add(qr);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    private Qr convertirCursorAQr(Cursor cursor) {
        Qr qr = new Qr();

        qr.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        qr.setActividadId(cursor.getInt(cursor.getColumnIndexOrThrow("actividad_id")));
        qr.setTipoQrId(cursor.getInt(cursor.getColumnIndexOrThrow("tipo_qr_id")));
        qr.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        qr.setRutaImagen(cursor.getString(cursor.getColumnIndexOrThrow("ruta_imagen")));
        qr.setActivo(cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1);

        return qr;
    }

    public long guardarOActualizarQr(Qr qr) {
        Qr qrExistente = obtenerQrPorActividadYTipo(
                qr.getActividadId(),
                qr.getTipoQrId()
        );

        if (qrExistente != null) {
            qr.setId(qrExistente.getId());
            return actualizarQr(qr);
        } else {
            return insertarQr(qr);
        }
    }
}