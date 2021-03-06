package pe.pucp.tel306.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import pe.pucp.tel306.Entity.Producto;
import pe.pucp.tel306.R;

public class EditarProducto extends AppCompatActivity {
    Producto producto = new Producto();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent =  getIntent();
        producto = (Producto) intent.getSerializableExtra("producto");
        EditText stock = findViewById(R.id.editTextNumberStockEditar);
        stock.setText( String.valueOf(producto.getStock()));
        EditText precio = findViewById(R.id.editTextNumberDecimalPrecioEditar);
        precio.setText(String.valueOf(producto.getPrecio()));
        EditText descripcion = findViewById(R.id.editTextTextMultiLinedescripcionEditar);
        descripcion.setText(producto.getDescricion());
        TextView nombre = findViewById(R.id.textViewNombreProductoEditar);
        nombre.setText(producto.getNombre());
        ImageView imagen = findViewById(R.id.imageViewImagen);
        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child(producto.getPk()+"/"+producto.getNombreFoto());
        Glide.with(EditarProducto.this).load(reference).into(imagen);
    }

    public void edtarProducto(View view){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        EditText stock = findViewById(R.id.editTextNumberStockEditar);
        EditText precio = findViewById(R.id.editTextNumberDecimalPrecioEditar);
        EditText descripcion = findViewById(R.id.editTextTextMultiLinedescripcionEditar);
        if(stock.getText().toString().trim().isEmpty() || stock.getText().toString().length()>6){
            stock.setError("de 1 a 6 dígitos");
        }else{
            if(precio.getText().toString().trim().isEmpty() || precio.getText().toString().length()>7){
                precio.setError("de 1 a 7 dígitos");
            }else{
                if(descripcion.getText().toString().trim().isEmpty() || descripcion.getText().toString().length()>50){
                    descripcion.setError("de 1 a 50 caracteres");
                }else{
                    int stockk = Integer.parseInt(stock.getText().toString());
                    if(stockk < 0){
                        stock.setError("Este campo tiene que ser mayor a 0");
                    }else{
                        producto.setStock(Integer.parseInt(stock.getText().toString().trim()));
                        producto.setPrecio(Double.parseDouble(precio.getText().toString().trim()));
                        producto.setDescricion(descripcion.getText().toString().trim());

                        databaseReference.child("Productos/"+producto.getPk()).setValue(producto)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("JULIO","GUARDADO EXITOSO EN TU DATABASE");
                                        Intent intent = new Intent(EditarProducto.this, PaginaPrincipalEmpresa.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(EditarProducto.this, "Producto editado exitósamente", Toast.LENGTH_SHORT).show();
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