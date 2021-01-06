package pe.pucp.tel306.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import pe.pucp.tel306.Adapters.ProductosAdapterEmpresa;
import pe.pucp.tel306.Cliente.CompraProducto;
import pe.pucp.tel306.Cliente.PaginaPrincipalCliente;
import pe.pucp.tel306.Entity.JavaMailAPI;
import pe.pucp.tel306.Entity.Peticioncompra;
import pe.pucp.tel306.Entity.Producto;
import pe.pucp.tel306.R;

public class RechazarCompra extends AppCompatActivity {
    Peticioncompra peticioncompra = new Peticioncompra();
    Producto producto = new Producto();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rechazar_compra);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent =  getIntent();
        peticioncompra = (Peticioncompra) intent.getSerializableExtra("peticioncompra");
        producto = (Producto) intent.getSerializableExtra("producto");
        ImageView imagen = findViewById(R.id.imageViewProductoRechazo);
        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child(peticioncompra.getProducto().getPk()+"/"+peticioncompra.getProducto().getNombreFoto());
        Glide.with(RechazarCompra.this).load(reference).into(imagen);
        TextView nombre = findViewById(R.id.textViewNombreproductorechjazo);
        nombre.setText(peticioncompra.getProducto().getNombre());
        TextView cliente = findViewById(R.id.textViewClienteRechazo);
        cliente.setText("Cliente: "+peticioncompra.getNombreComprador());
        TextView cantidad = findViewById(R.id.textViewCantidadRechazo);
         cantidad.setText("Cantidad: "+peticioncompra.getCantidad()+" unidades");
    }

    public void rechazarVenta(View view){
        EditText motivo = findViewById(R.id.editTextTextMultiLineRechazoMotivo);
        if(motivo.getText().toString().trim().isEmpty()){
            motivo.setError("Este campo noo puede ser vacío");
        }else{
            peticioncompra.setMotivoRechazo(motivo.getText().toString().trim());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            peticioncompra.setEstado("Rechazado");
            databaseReference.child("Solicitudes/"+peticioncompra.getPk()).setValue(peticioncompra)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("JULIO","GUARDADO EXITOSO EN TU DATABASE");
                            String mail = peticioncompra.getCorreoUser();
                            String subject = "Venta de "+" "+peticioncompra.getCantidad()+" "+peticioncompra.getProducto().getNombre();
                            String message = "Su venta fue rechazada por la empresa "+ peticioncompra.getProducto().getNombreEmpresa() + " por el motivo de : "
                                    +peticioncompra.getMotivoRechazo();
                            JavaMailAPI javaMailAPI = new JavaMailAPI(RechazarCompra.this,mail,subject,message);
                            javaMailAPI.execute();
                            reponerStock(peticioncompra);
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

    public void reponerStock(Peticioncompra peticioncompra) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        int stockNuevo = producto.getStock() + peticioncompra.getCantidad();
        producto.setStock(stockNuevo);

        databaseReference.child("Productos/" + producto.getPk()).setValue(producto).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(RechazarCompra.this, VentasPendientes.class);
                startActivity(intent);
                finish();
                Toast.makeText(RechazarCompra.this, "Producto rechazado exitósamente", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
        return;



    }




}