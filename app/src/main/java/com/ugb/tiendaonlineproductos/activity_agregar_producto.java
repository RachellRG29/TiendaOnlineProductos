package com.ugb.tiendaonlineproductos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

//AGREGAR PRODUCTO

public class activity_agregar_producto extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    CircleImageView imageCirProducto;
    private Button btnChangeImage;
    Button btnGuardar;
    TextView tempVal;
    String accion="nuevo", id="", imgproductourl="", rev="", idProducto="";
    Uri filePath;
    Bitmap bitmap;
    FloatingActionButton btnIrvista;
    Intent almacenarFotoIntent;
    utilidades utls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        //cambiar color barra estado
        cambiarColorBarraEstado(getResources().getColor(R.color.grey));

        //valores para los productos
        EditText txtcodigo= (EditText)findViewById(R.id.txtCodigo);
        EditText txtnombre= (EditText)findViewById(R.id.txtNombre);
        EditText txtprecio= (EditText)findViewById(R.id.txtPrecio);
        EditText txtmarca= (EditText)findViewById(R.id.txtMarca);
        EditText txtdescripcion= (EditText)findViewById(R.id.txtDescripcion);

        imageCirProducto = findViewById(R.id.imgProductoVista); //
        btnChangeImage = findViewById(R.id.btnCambiarImagen);
        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openGallery();
            }
        });

        //Boton para cambiar de activitys, ir a vista
        btnIrvista = findViewById(R.id.btnRegresarListaProductos);
        btnIrvista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irVista();
            }
        });

        //boton guardar producto
        btnGuardar = findViewById(R.id.btnGuardarProducto);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // almacenarImagenProducto();

            //validar campo obligatorio
                if(txtcodigo.getText().toString().isEmpty() || txtnombre.getText().toString().isEmpty() ||
                        txtmarca.getText().toString().isEmpty() || txtprecio.getText().toString().isEmpty() ||
                        txtdescripcion.getText().toString().isEmpty() ){

                    txtcodigo.setError("Campo requerido");
                    txtnombre.setError("Campo requerido");
                    txtmarca.setError("Campo requerido");
                    txtprecio.setError("Campo requerido");
                    txtdescripcion.setError("Campo requerido");
                    return;
                } else {
                    //VALIDAR AGREGAR PRODUCTO

                    try {
                        tempVal = findViewById(R.id.txtCodigo);
                        String codigo = tempVal.getText().toString();

                        tempVal = findViewById(R.id.txtNombre);
                        String nombre = tempVal.getText().toString();

                        tempVal = findViewById(R.id.txtMarca);
                        String marca = tempVal.getText().toString();

                        tempVal = findViewById(R.id.txtPrecio);
                        String precio = tempVal.getText().toString();

                        tempVal = findViewById(R.id.txtDescripcion);
                        String descripcion = tempVal.getText().toString();

                        //GUARDAR DATOS SERVIDOR
                        JSONObject datosProductos = new JSONObject();
                        if (accion.equals("modificar") && id.length() > 0 && rev.length() > 0) {
                            datosProductos.put("_id", id);
                            datosProductos.put("_rev", rev);
                        }
                        datosProductos.put("idProducto", idProducto);
                        datosProductos.put("codigo", codigo);
                        datosProductos.put("nombre", nombre);
                        datosProductos.put("marca", marca);
                        datosProductos.put("precio", precio);
                        datosProductos.put("descripcion", descripcion);
                        datosProductos.put("imgproducto", imgproductourl);
                        String respuesta = "";

                        enviarDatosServidor objGuardarDatosServidor = new enviarDatosServidor(getApplicationContext());
                        respuesta = objGuardarDatosServidor.execute(datosProductos.toString()).get();

                        JSONObject respuestaJSONObject = new JSONObject(respuesta);
                        if (respuestaJSONObject.getBoolean("ok")) {
                            id = respuestaJSONObject.getString("id");
                            rev = respuestaJSONObject.getString("rev");
                        } else {
                            respuesta = "Error al guardar en servidor: " + respuesta;
                        }

                        DB db = new DB(getApplicationContext(), "", null, 1);
                        String[] datos = new String[]{id, rev, idProducto, codigo, nombre, marca, precio, descripcion, imgproductourl};
                        respuesta = db.administrar_productos(accion, datos);
                        if (respuesta.equals("ok")) {
                            Toast.makeText(getApplicationContext(), "Producto Registrado con Exito.", Toast.LENGTH_SHORT).show();
                            irVista();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + respuesta, Toast.LENGTH_LONG).show();
                        }

                    } //cierre validar campo obligatorio
                    catch (Exception e) {
                        mostrarMsg("Error al guardar: " + e.getMessage());
                    }
                }
            }
        });

        mostrarDatosProductos(); //mostrar los datos del producto

        //
    } //ONCREATE

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            imgproductourl = filePath.toString();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageCirProducto.setImageBitmap(bitmap);

                almacenarImagenProducto();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //REVISAR POR Q NO ALMACENA LA IMAGEN

    private void almacenarImagenProducto(){
        File fotoProducto = null;
        try{
            fotoProducto = crearImagenProducto();
            if(fotoProducto!=null){
               // filePath = FileProvider.getUriForFile(activity_agregar_producto.this, "com.ugb.tiendaonlineproductos.fileprovider", fotoProducto);
                almacenarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
                startActivityForResult(almacenarFotoIntent, 1);
            }else{
                mostrarMsg("No pude almacenar la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al almacenar la foto: "+ e.getMessage());
        }
    }

    private File crearImagenProducto() throws Exception{

        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "imagen/*"+ fechaHoraMs +"_";
         File dirAlmacenamiento = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), "productos");
        //File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( !dirAlmacenamiento.exists() ){
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        imgproductourl = image.getAbsolutePath();
        return image;
    }

    private void irVista(){
        Intent abrirVentana = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(abrirVentana);
    }

    private void mostrarDatosProductos(){
        try {
            Bundle parametros = getIntent().getExtras();
            accion = parametros.getString("accion");
            if( accion.equals("modificar") ){
                JSONObject jsonObject = new JSONObject(parametros.getString("productos")).getJSONObject("value");
                id = jsonObject.getString("_id");
                rev = jsonObject.getString("_rev");
                idProducto = jsonObject.getString("idProducto");

                tempVal = findViewById(R.id.txtCodigo);
                tempVal.setText(jsonObject.getString("codigo"));

                tempVal = findViewById(R.id.txtNombre);
                tempVal.setText(jsonObject.getString("nombre"));

                tempVal = findViewById(R.id.txtMarca);
                tempVal.setText(jsonObject.getString("marca"));

                tempVal = findViewById(R.id.txtPrecio);
                tempVal.setText(jsonObject.getString("precio"));

                tempVal = findViewById(R.id.txtDescripcion);
                tempVal.setText(jsonObject.getString("descripcion"));

                imgproductourl = jsonObject.getString("imgproducto");
                Bitmap bitmap = BitmapFactory.decodeFile(imgproductourl);
                imageCirProducto.setImageBitmap(bitmap);
            }else{ //nuevo registro
                idProducto = utls.generarIdUnico();
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar los datos: "+ e.getMessage());
        }
    }

    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }


    private void cambiarColorBarraEstado(int color) {
        // Verificar si la versión del SDK es Lollipop o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    } //fin cambiar colorbarraestado
}



