package pe.pucp.tel306.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import pe.pucp.tel306.Cliente.CompraProducto;
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
//                Intent intent = new Intent(context, CompraProducto.class);
//                intent.putExtra("producto", producto);
//                context.startActivity(intent);
            }
        });
        holder.aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, CompraProducto.class);
//                intent.putExtra("producto", producto);
//                context.startActivity(intent);
            }
        });
        holder.rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, CompraProducto.class);
//                intent.putExtra("producto", producto);
//                context.startActivity(intent);
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
