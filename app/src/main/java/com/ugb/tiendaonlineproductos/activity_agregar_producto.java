package com.ugb.tiendaonlineproductos;

import static android.provider.MediaStore.EXTRA_MEDIA_ALBUM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.View;
import android.graphics.Bitmap;
import android.net.Uri;
import android.content.Intent;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.provider.MediaStore;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

//AGREGAR PRODUCTO

public class activity_agregar_producto extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    CircleImageView imageCirProducto;
    private Button btnChangeImage;
    Button btnGuardar;
    TextView tempVal;
    String accion="nuevo", id="", imgproductourl="";
    private Uri filePath;
    FloatingActionButton btnIrvista;
    Intent guardarFotoIntent;

    @SuppressLint("CutPasteId")
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
                guardarImagen();
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

                    imageCirProducto= findViewById(R.id.imgProductoVista);
                    imgproductourl = tempVal.getText().toString();



                    DB db = new DB(getApplicationContext(),"", null, 1);
                    String[] datos = new String[]{id,codigo,nombre,marca,precio,descripcion, imgproductourl};
                    String respuesta = db.administrar_productos(accion, datos);
                    if( respuesta.equals("ok") ){
                        Toast.makeText(getApplicationContext(), "Producto Registrado con Exito.", Toast.LENGTH_SHORT).show();
                        irVista();
                    }else{
                        Toast.makeText(getApplicationContext(), "Error: "+ respuesta, Toast.LENGTH_LONG).show();
                    }

                } //cierre validar campo obligatorio

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

    private File crearImagenProducto() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "imagen_"+ fechaHoraMs +"_";

        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( !dirAlmacenamiento.exists() ){
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        File fotoProducto = null;
        imgproductourl = image.getAbsolutePath();

        return image;
    }

    private void guardarImagen(){
        guardarFotoIntent = new Intent(MediaStore.ACTION_PICK_IMAGES);

        if (guardarFotoIntent.resolveActivity(getPackageManager())!=null ){
            File fotoProducto = null;
            try {
                fotoProducto = crearImagenProducto();
                if( fotoProducto!=null ){
                    Uri uriFotoProducto = FileProvider.getUriForFile(activity_agregar_producto.this, "com.ugb.tiendaonlineproductos.fileprovider", fotoProducto);
                    guardarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoProducto);
                    startActivityForResult(guardarFotoIntent, 1);
                }else{
                    mostrarMsg("NO pude guardar la foto");
                }
            }catch (Exception e){
                mostrarMsg("ERROR al guardar la foto");
            }
        }


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


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void irVista(){
        Intent abrirVentana = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(abrirVentana);
    }

  private void mostrarDatosProductos(){
        try{
            Bundle parametros= getIntent().getExtras();
            accion = parametros.getString("accion");
            if (accion.equals("modificar")){
                String[] datos= parametros.getStringArray("productos");
                id = datos[0];

                tempVal = findViewById(R.id.txtCodigo);
                tempVal.setText(datos[1]);

                tempVal = findViewById(R.id.txtNombre);
                tempVal.setText(datos[2]);

                tempVal = findViewById(R.id.txtMarca);
                tempVal.setText(datos[3]);

                tempVal = findViewById(R.id.txtPrecio);
                tempVal.setText(datos[4]);

                tempVal = findViewById(R.id.txtDescripcion);
                tempVal.setText(datos[5]);


                imageCirProducto= findViewById(R.id.imgProductoVista);
                imgproductourl = tempVal.getText().toString();
                imgproductourl = datos[6];

            }
        } catch (Exception e){
            mostrarMsg("Error al mostrar los datos: "+e.getMessage());
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



