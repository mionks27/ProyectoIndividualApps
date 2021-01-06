package pe.pucp.tel306.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.ArrayList;

import pe.pucp.tel306.Cliente.CompraProducto;
import pe.pucp.tel306.Empresa.Mapa;
import pe.pucp.tel306.Empresa.RechazarCompra;
import pe.pucp.tel306.Entity.JavaMailAPI;
import pe.pucp.tel306.Entity.Peticioncompra;
import pe.pucp.tel306.Entity.Producto;
import pe.pucp.tel306.R;

public class VentasPendientesAdapter extends RecyclerView.Adapter<VentasPendientesAdapter.VentasPendientesViewHolder> {

    private ArrayList<Peticioncompra> listaDePeticiones;
    private Context context;

    public VentasPendientesAdapter(ArrayList<Peticioncompra> listaDePeticiones, Context context) {
        this.listaDePeticiones = listaDePeticiones;
        this.context = context;
    }

    @NonNull
    @Override
    public VentasPendientesAdapter.VentasPendientesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.ventas_pendientes_rv,parent,false);
        VentasPendientesAdapter.VentasPendientesViewHolder ventasPendientesViewHolder = new VentasPendientesAdapter.VentasPendientesViewHolder(itemView);
        return ventasPendientesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VentasPendientesAdapter.VentasPendientesViewHolder holder, int position) {
        Peticioncompra peticioncompra = listaDePeticiones.get(position);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child(peticioncompra.getProducto().getPk()+"/"+peticioncompra.getProducto().getNombreFoto());
        Glide.with(context).load(reference).into(holder.imagenPro);
        holder.nombre.setText("Producto: "+peticioncompra.getProducto().getNombre());
        holder.comprador.setText("Cliente: "+ peticioncompra.getNombreComprador());
        holder.cantidad.setText("Cantidad: "+ String.valueOf(peticioncompra.getCantidad()));
        holder.direccion.setText("Stock: "+ peticioncompra.getUbicacionGps());
        holder.gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Mapa.class);
                intent.putExtra("latitud",peticioncompra.getLatitud());
                intent.putExtra("longitud",peticioncompra.getLongitud());
                intent.putExtra("nombreUsuario",peticioncompra.getNombreComprador());
                context.startActivity(intent);
            }
        });
        holder.aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                peticioncompra.setEstado("Aceptado");
                databaseReference.child("Solicitudes/"+peticioncompra.getPk()).setValue(peticioncompra)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("JULIO","GUARDADO EXITOSO EN TU DATABASE");
                                    String mail = peticioncompra.getCorreoUser();
                                    String subject = "Venta de "+" "+peticioncompra.getCantidad()+" "+peticioncompra.getProducto().getNombre();
                                    String message = "Su venta fue Aceptada por la empresa "+ peticioncompra.getProducto().getNombreEmpresa();
                                    JavaMailAPI javaMailAPI = new JavaMailAPI(context,mail,subject,message);
                                    javaMailAPI.execute();
                                    Toast.makeText(context, "Producto vendido exitósamente", Toast.LENGTH_SHORT).show();
                                }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
            }
        });
        holder.rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Productos/").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Producto> productoArrayList = new ArrayList<>();
                        for(DataSnapshot children : snapshot.getChildren()){
                            Producto producto = children.getValue(Producto.class);
                            if(producto.getPk().equalsIgnoreCase(peticioncompra.getProducto().getPk())){
                                productoArrayList.add(producto);
                            }
                        }

                        if(!productoArrayList.isEmpty()){
                            Intent intent = new Intent(context, RechazarCompra.class);
                            intent.putExtra("peticioncompra", peticioncompra);
                            intent.putExtra("producto",productoArrayList.get(0));
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return listaDePeticiones.size();
    }

    public static  class VentasPendientesViewHolder extends RecyclerView.ViewHolder{
        TextView nombre;
        TextView cantidad;
        TextView comprador;
        TextView direccion;
        ImageView imagenPro;
        Button rechazar;
        Button aceptar;
        ImageButton gps;
        public VentasPendientesViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textViewNombreProdVentasPendientes);
            comprador = itemView.findViewById(R.id.textViewCompradorPendiente);
            cantidad = itemView.findViewById(R.id.textViewCantidadListapendientes);
            direccion = itemView.findViewById(R.id.textViewUbicacionGpsPendientes);
            imagenPro = itemView.findViewById(R.id.imageViewVentasPendientes);
            rechazar = itemView.findViewById(R.id.buttonrechazarPeticion);
            aceptar = itemView.findViewById(R.id.buttonVendido);
            gps = itemView.findViewById(R.id.imageButtonUbicacionGps);
        }
    }
}
