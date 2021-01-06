package pe.pucp.tel306.Empresa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import pe.pucp.tel306.Entity.Producto;
import pe.pucp.tel306.Entity.User;
import pe.pucp.tel306.R;

public class AgregarProducto extends AppCompatActivity {
    Uri uri = null;
    Producto producto = new Producto();
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent =  getIntent();
        user = (User) intent.getSerializableExtra("user");
    }

    public void pickFile(View view) {
        ImageView foto = findViewById(R.id.imageViewFotoProductoCrear);
        if(foto.getVisibility()==View.VISIBLE){
            foto.setVisibility(View.GONE);
        }
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione Foto para subir"), 10);

    }

    public  void  tomarFoto(View view){
        TextView textViewFoto = findViewById(R.id.textViewRutafoto);
        if(textViewFoto.getVisibility()==View.VISIBLE){
            textViewFoto.setVisibility(View.GONE);
        }
        Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    String fileName = getFileName(uri);
                    TextView textView = findViewById(R.id.textViewRutafoto);
                    textView.setText(fileName);
                    producto.setNombreFoto(fileName);
                    textView.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap =  (Bitmap) bundle.get("data");
                    ImageView foto = findViewById(R.id.imageViewFotoProductoCrear);
                    foto.setVisibility(View.VISIBLE);
                    foto.setImageBitmap(bitmap);
                    guardarFotoTomada(bitmap);
                }
                break;
        }
    }

    public void guardarFotoTomada(Bitmap bitmap){
        producto.setNombreFoto("prueba.jpg");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, producto.getNombreFoto());
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            uri  = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            try(OutputStream outputStream = getContentResolver().openOutputStream(uri)){
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void guardarDispositivo(View view){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        EditText editTextNombre = findViewById(R.id.editTextTextNombreProductoCrear);
        EditText editTextTextDescripcion = findViewById(R.id.editTextDescripcion);
        EditText editTextTextPrecio = findViewById(R.id.editTextPrecio);
        EditText editTextNumberStock = findViewById(R.id.editTextStock);
        ImageView foto = findViewById(R.id.imageViewFotoProductoCrear);
        TextView textViewFoto21 = findViewById(R.id.textViewRutafoto);
        if(foto.getVisibility() == View.INVISIBLE && textViewFoto21.getVisibility() == View.INVISIBLE){
            Toast.makeText(AgregarProducto.this, "Debe escoger o tomar una foto", Toast.LENGTH_SHORT).show();
        }else{
            if(editTextNombre.getText().toString().trim().isEmpty()){
                editTextNombre.setError("Este campo no puede ser vacío");
            }else{
                if(editTextTextDescripcion.getText().toString().trim().isEmpty() || editTextTextDescripcion.getText().toString().length()>50){
                    editTextTextDescripcion.setError("de 1 a 50 caracteres");
                }else{
                    if(editTextTextPrecio.getText().toString().trim().isEmpty() || editTextTextPrecio.getText().toString().length()>7){
                        editTextTextPrecio.setError("de 1 a 6 digítos");
                    }else{
                        if(editTextNumberStock.getText().toString().trim().isEmpty() || editTextNumberStock.getText().toString().length()>6){
                            editTextNumberStock.setError("de 1 a 6 caracteres");
                        }else{
                            int stock = Integer.parseInt(editTextNumberStock.getText().toString());
                            if(stock < 0){
                                editTextNumberStock.setError("Este campo tiene que ser mayor a 0");
                            }else{
                                producto.setStock(Integer.parseInt(editTextNumberStock.getText().toString()));
                                producto.setNombre(editTextNombre.getText().toString());
                                producto.setDescricion(editTextTextDescripcion.getText().toString());
                                producto.setPrecio(Double.parseDouble(editTextTextPrecio.getText().toString()));
                                final TextView textViewFoto = findViewById(R.id.textViewRutafoto);

                                String mypk = databaseReference.push().getKey();
                                producto.setPk(mypk);
                                producto.setUidUser(firebaseUser.getUid());
                                producto.setNombreEmpresa(user.getNombreEmpresa());

                                databaseReference.child("Productos/"+producto.getPk()).setValue(producto)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("JULIO","GUARDADO EXITOSO EN TU DATABASE");

                                                if(textViewFoto.getVisibility()==View.VISIBLE){
                                                    subirArchivoConPutFile(textViewFoto.getText().toString());
                                                }else{
                                                    subirArchivoConPutFile(producto.getNombreFoto());
                                                }

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });

                            }
                        }
                    }
                }
            }
        }
    }



    public void subirArchivoConPutFile( String fileName) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            //subir archivo a firebase storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            StorageMetadata storageMetadata = new StorageMetadata.Builder()
                    .setCustomMetadata("autor", firebaseUser.getDisplayName())
                    .build();

            UploadTask task = storageReference.child(producto.getPk()   +"/"+producto.getNombreFoto()).putFile(uri, storageMetadata);


            task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("JULIO", "subida exitosa");
                    Intent intent = new Intent(AgregarProducto.this, PaginaPrincipalEmpresa.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(AgregarProducto.this, "Producto agregado exitósamente", Toast.LENGTH_SHORT).show();
                }
            });
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("JULIO", "error en la subida");
                    e.printStackTrace();
                }
            });
            task.addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                }
            });
            task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    long bytesTransferred = snapshot.getBytesTransferred();
                    long totalByteCount = snapshot.getTotalByteCount();
                    double progreso = (100.0 * bytesTransferred) / totalByteCount;
                    Log.d("JULIO", String.valueOf(progreso));
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            Log.d("JULIO", "SIN PERMISOOOOOOOOOOOOOOOOOO");
        }
    }
}