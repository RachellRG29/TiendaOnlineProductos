package com.ugb.tiendaonlineproductos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//VISTA O LISTA PRODUCTO

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btnIragregar;
    Bundle parametros = new Bundle();
    ListView lts;
    Cursor cProductos;
    DB dbProductos;
    productos misProductos;
    final ArrayList<productos> alProductos = new ArrayList<productos>();
    final ArrayList<productos> alProductosCopy = new ArrayList<productos>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //cambiar color barra estado
        cambiarColorBarraEstado(getResources().getColor(R.color.grey));

        //Boton para cambiar de activitys, ir a agregar
        btnIragregar = findViewById(R.id.btnAbrirNuevosProductos);
        btnIragregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parametros.putString("accion", "nuevo");
                irAgregar(parametros);
            }
        });

        obtenerProductos();
        buscarProductos();

    }

    //MENU
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        cProductos.moveToPosition(info.position);
        menu.setHeaderTitle(cProductos.getString(1));//1 es el nombre del producto
    }

    //Agregar, modificar y eliminar
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            switch (item.getItemId()){
                case R.id.mnxAgregar:
                    parametros.putString("accion", "nuevo");
                    irAgregar(parametros);
                    break;
                case R.id.mnxModificar:
                    String productoss[]={
                            cProductos.getString(0), //id
                            cProductos.getString(1), //codigo
                            cProductos.getString(2), //nombre
                            cProductos.getString(3), //marca
                            cProductos.getString(4), //precio
                            cProductos.getString(5), //descripcion
                            cProductos.getString(6)  //imgproducto
                    };
                    parametros.putString("accion", "modificar");
                    parametros.putStringArray("productos", productoss);
                    irAgregar(parametros);
                    break;

                case R.id.mnxEliminar:
                    eliminarProductos();
                    break;
            }
            return true;
        }catch (Exception e){
            mostrarMsg("Error en menu: "+ e.getMessage());
            return super.onContextItemSelected(item);
        }

    }

    private void obtenerProductos(){
        try{
            alProductos.clear();
            alProductosCopy.clear();

            dbProductos = new DB(MainActivity.this, "", null, 1);
            cProductos = dbProductos.consultar_productos();

            if ( cProductos.moveToFirst() ){
                lts = findViewById(R.id.ltsProductos);
                do{
                    misProductos = new productos(
                            cProductos.getString(0), //idProducto
                            cProductos.getString(1), //codigo
                            cProductos.getString(2), //nombre
                            cProductos.getString(3), //marca
                            cProductos.getString(4), //precio
                            cProductos.getString(5), //descripcion
                            cProductos.getString(6) //imagencircular
                    );
                    alProductos.add(misProductos);
                }while(cProductos.moveToNext());
                alProductosCopy.addAll(alProductos);

                adaptadorImagenes adImagenes = new adaptadorImagenes(getApplicationContext(), alProductos);
                lts.setAdapter(adImagenes);

                registerForContextMenu(lts);
            }else{
                mostrarMsg("No hay productos que mostrar");
            }
        }catch (Exception e){
            mostrarMsg("Error al obtener productos: "+ e.getMessage());
        }
    }


    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    //Eliminar productos
    private void eliminarProductos(){
        try {
            AlertDialog.Builder confirmacion = new AlertDialog.Builder(MainActivity.this);
            confirmacion.setTitle("¿Esta seguro de Eliminar el producto?: ");
            confirmacion.setMessage(cProductos.getString(1));
            confirmacion.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String respuesta = dbProductos.administrar_productos("eliminar", new String[]{cProductos.getString(0)});
                    if (respuesta.equals("ok")) {
                        mostrarMsg("Producto eliminado con exito.");
                        obtenerProductos();
                    } else {
                        mostrarMsg("Error al eliminar producto: " + respuesta);
                    }
                }
            });
            confirmacion.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            confirmacion.create().show();
        }catch (Exception e){
            mostrarMsg("Error al eliminar: "+ e.getMessage());
        }
    }

    //Buscar productos
    private void buscarProductos(){

        TextView tempVal;
        tempVal = findViewById(R.id.txt_busqueda);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    alProductos.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if( valor.length()<=0 ){
                        alProductos.addAll(alProductosCopy);
                    }else{
                        for( productos productos : alProductosCopy ){

                            String codigo = productos.getCodigo();
                            String nombre = productos.getNombre();
                            String marca = productos.getMarca();
                            String precio = productos.getPrecio();
                            String descripcion = productos.getDescripcion();

                            if( codigo.toLowerCase().trim().contains(valor) ||
                                    nombre.toLowerCase().trim().contains(valor) ||
                                    marca.trim().contains(valor) ||
                                    precio.trim().toLowerCase().contains(valor) ||
                                            descripcion.trim().toLowerCase().contains(valor) ){
                                alProductos.add(productos);
                            }
                        }
                        adaptadorImagenes adImagenes = new adaptadorImagenes(getApplicationContext(), alProductos);
                        lts.setAdapter(adImagenes);
                    }
                }catch (Exception e){
                    mostrarMsg("Error al buscar: "+e.getMessage() );
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
}

    //Metodo para ir al activity de agregar
    private void irAgregar(Bundle parametros){
        Intent abrirVentana = new Intent(getApplicationContext(), activity_agregar_producto.class);
        abrirVentana.putExtras(parametros); //abrir actividad y modificar
        startActivity(abrirVentana);

    }

    //Metodo para cambiar el color de la barra de estado
    private void cambiarColorBarraEstado(int color) {
        // Verificar si la versión del SDK es Lollipop o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    } //fin cambiar colorbarraestado



}

