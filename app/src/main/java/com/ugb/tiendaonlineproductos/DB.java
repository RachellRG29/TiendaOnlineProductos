package com.ugb.tiendaonlineproductos;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    private  static  final String dbname = "db_productos";
    private  static final int v=1;

    private static  final String SQldb = "CREATE TABLE productos(idProducto integer primary key autoincrement, codigo text, nombre text, marca text, precio text, descripcion text, imgproducto text)";

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, v);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQldb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //ADMINISTRAR LOS PRODUCTOS
   public String administrar_productos(String accion, String[] datos){
        try {
            SQLiteDatabase db= getWritableDatabase();
            if (accion.equals("nuevo")){
                db.execSQL("INSERT INTO productos(codigo,nombre,marca,precio,descripcion,imgproducto) VALUES('"+datos[1]+"','"+datos[2]+"','"+datos[3]+"','"+datos[4]+"','"+datos[5]+"','"+datos[6]+"' )");
            } else if (accion.equals("modificar")) {
                db.execSQL("UPDATE productos SET codigo='"+ datos[1] +"', nombre='"+ datos[2] +"', marca='"+ datos[3] +"', precio='"+ datos[4] +"',descripcion='"+ datos[5] +"',imgproducto='"+ datos[6] +"' WHERE idProducto='"+ datos[0] +"' ");
            } else if (accion.equals("eliminar")) {
                db.execSQL("DELETE FROM productos WHERE idProducto='"+datos[0]+"' ");
            }
            return "ok";

        } catch (Exception e){
            return  "Error: "+ e.getMessage();

        }
   }

   //CURSOS CONSULTAR PRODUCTOS
   public Cursor consultar_productos(){
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos ORDER BY codigo", null);
        return cursor;
   }



}
