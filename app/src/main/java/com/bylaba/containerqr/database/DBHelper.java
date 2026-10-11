package com.bylaba.containerqr.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "container_qr.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ACTIVIDADES = "actividades";
    public static final String TABLE_TIPOS_QR = "tipos_qr";
    public static final String TABLE_QRS = "qrs";
    public static final String TABLE_CONFIGURACIONES = "configuraciones";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createActividades = "CREATE TABLE " + TABLE_ACTIVIDADES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "activo INTEGER DEFAULT 1" +
                ");";

        String createTiposQr = "CREATE TABLE " + TABLE_TIPOS_QR + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "activo INTEGER DEFAULT 1" +
                ");";

        String createQrs = "CREATE TABLE " + TABLE_QRS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "actividad_id INTEGER NOT NULL, " +
                "tipo_qr_id INTEGER NOT NULL, " +
                "nombre TEXT, " +
                "ruta_imagen TEXT, " +
                "activo INTEGER DEFAULT 1, " +
                "FOREIGN KEY (actividad_id) REFERENCES " + TABLE_ACTIVIDADES + "(id), " +
                "FOREIGN KEY (tipo_qr_id) REFERENCES " + TABLE_TIPOS_QR + "(id), " +
                "UNIQUE(actividad_id, tipo_qr_id)" +
                ");";

        String createConfiguraciones = "CREATE TABLE " + TABLE_CONFIGURACIONES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "clave TEXT NOT NULL UNIQUE, " +
                "valor TEXT NOT NULL" +
                ");";

        db.execSQL(createActividades);
        db.execSQL(createTiposQr);
        db.execSQL(createQrs);
        db.execSQL(createConfiguraciones);

        insertarDatosIniciales(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {

        db.execSQL("INSERT INTO " + TABLE_ACTIVIDADES + " (nombre, descripcion, activo) VALUES " +
                "('Librería', 'Pagos correspondientes a la librería', 1), " +
                "('Cafetería', 'Pagos correspondientes a la cafetería', 1);");

        db.execSQL("INSERT INTO " + TABLE_TIPOS_QR + " (nombre, descripcion, activo) VALUES " +
                "('Electronica', 'Pago con emisión de documento electrónico', 1), " +
                "('Manual', 'Pago con emisión de documento manual', 1);");

        db.execSQL("INSERT INTO " + TABLE_CONFIGURACIONES + " (clave, valor) VALUES " +
                "('password_configuracion', 'admin321');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QRS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONFIGURACIONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPOS_QR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVIDADES);

        onCreate(db);
    }
}