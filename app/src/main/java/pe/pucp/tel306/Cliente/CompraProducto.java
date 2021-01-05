package pe.pucp.tel306.Cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pe.pucp.tel306.Entity.Peticioncompra;
import pe.pucp.tel306.Entity.Producto;
import pe.pucp.tel306.R;

public class CompraProducto extends AppCompatActivity {
    Peticioncompra peticioncompra = new Peticioncompra();
    Producto producto = new Producto();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra_producto);
        Intent intent =  getIntent();
        producto = (Producto) intent.getSerializableExtra("producto");
        System.out.println("AHHHHHHHHHHHHHHHHHHHHHHHHH" + producto.getNombre());
        ImageView imagen = findViewById(R.id.imageViewComprar);
        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child(producto.getPk()+"/"+producto.getNombreFoto());
        Glide.with(CompraProducto.this).load(reference).into(imagen);
        TextView nombre = findViewById(R.id.textViewNombreProductoCompra);
        nombre.setText("Nombre: "+producto.getNombre());
        TextView stock = findViewById(R.id.textViewstockCompra);
        stock.setText("Stock: "+String.valueOf(producto.getStock()));
        TextView descripcion = findViewById(R.id.textViewDescripcionCompra);
        descripcion.setText("Descripción: "+producto.getDescricion());
        TextView precio = findViewById(R.id.textViewPreciocompra);
        precio.setText("Precio(S/.): "+  String.valueOf(producto.getPrecio()));
    }

    private boolean gpsActivo() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return providerEnabled;
    }

    public void mostrarInfoDeUbicacion(View view) {
        if (gpsActivo()) {
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                FusedLocationProviderClient location = LocationServices.getFusedLocationProviderClient(this);
                location.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("infoApp", "ALt" + location.getAltitude());
                        Log.d("infoApp", "Lat" + location.getLatitude());
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {
                            List<Address> direccion = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Log.d("infoApp", "la direccion es:" + direccion.get(0).getAddressLine(0));
                            TextView textViewGps = findViewById(R.id.textViewUbicacionCompra);
                            textViewGps.setText(direccion.get(0).getAddressLine(0));
                            textViewGps.setVisibility(View.VISIBLE);
                            peticioncompra.setLatitud(location.getLatitude());
                            peticioncompra.setLongitud(location.getLongitude());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });
                location.getLastLocation().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("infoApp", "Fallo aquí GA");
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            Toast.makeText(CompraProducto.this, "Por favor active su GPS", Toast.LENGTH_SHORT).show(); //FORMATO DE UN TOAST QUE ES COMO UN POP UP

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("infoApp", "Permisos concedidos");
            } else {
                Log.d("infoApp", "Persmisos denegados");
            }

        }
    }


    public void confirmarReserva(View view) {
        TextView textviewGPSaValidar = findViewById(R.id.textViewUbicacionCompra);
        if (textviewGPSaValidar.getVisibility() == View.VISIBLE) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            EditText cantidadAComprar = findViewById(R.id.editTextNumberCantidadAComprar);
            int cantidad = Integer.parseInt(cantidadAComprar.getText().toString());
            if(cantidadAComprar.getText().toString().trim().isEmpty() || cantidad>producto.getStock()){
                cantidadAComprar.setError("Ingrese una cantidad válida.");
            }else{
                peticioncompra.setCantidad(cantidad);
                TextView textViewGps = findViewById(R.id.textViewUbicacionCompra);
                peticioncompra.setProducto(producto);
                peticioncompra.setEstado("Pendiente");
                peticioncompra.setUbicacionGps(textViewGps.getText().toString());
                peticioncompra.setNombreComprador(firebaseUser.getDisplayName());
                peticioncompra.setUiComprador(firebaseUser.getUid());
                peticioncompra.setCorreoUser(firebaseUser.getEmail());
                String mypk = databaseReference.push().getKey();
                peticioncompra.setPk(mypk);

                databaseReference.child("Solicitudes/" + mypk).setValue(peticioncompra)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("infoApp", "GUARDADO EXITOSO de reserva EN TU DATABASE");
                                reducirStock(producto,peticioncompra);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("IMPORTANTE");
            alertDialog.setMessage("Por favor sigua los siguientes pasos:\n \n 1° Active su GPS \n  2° Presione el botón 'OBTENER UBICACION'\n 3°Haga click en 'PEDIDO DE COMPRA' nuevamente");
            alertDialog.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialog.show();
            // Toast.makeText(SolicitudReserva.this, "Para reservar debe activar el GPS y presionar el 'BOTÓN DE OBTENER UBICACION'", Toast.LENGTH_SHORT).show();
        }


    }

    public void reducirStock(Producto producto, Peticioncompra peticioncompra) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        int stockNuevo = producto.getStock() - peticioncompra.getCantidad();
        producto.setStock(stockNuevo);

        databaseReference.child("Productos/" + producto.getPk()).setValue(producto).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CompraProducto.this);
                alertDialog.setTitle("¡Pedido Exitoso!");
                alertDialog.setMessage("Se le enviará un correo para informar si su petición de compra fue aprobada o no.");
                alertDialog.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(CompraProducto.this, PaginaPrincipalCliente.class);
                        startActivity(intent);
                        finish();
                    }
                });
                alertDialog.show();
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