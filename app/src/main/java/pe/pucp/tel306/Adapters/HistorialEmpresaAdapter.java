package pe.pucp.tel306.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import pe.pucp.tel306.Entity.Peticioncompra;
import pe.pucp.tel306.R;

public class HistorialEmpresaAdapter extends RecyclerView.Adapter<HistorialEmpresaAdapter.HistorialVentasEmpresaViewHolder> {

    private ArrayList<Peticioncompra> listaDePeticiones;
    private Context context;

    public HistorialEmpresaAdapter(ArrayList<Peticioncompra> listaDePeticiones, Context context) {
        this.listaDePeticiones = listaDePeticiones;
        this.context = context;
    }

    @NonNull
    @Override
    public HistorialEmpresaAdapter.HistorialVentasEmpresaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.historial_empresa_rv,parent,false);
        HistorialEmpresaAdapter.HistorialVentasEmpresaViewHolder historialVentasEmpresaViewHolder = new HistorialEmpresaAdapter.HistorialVentasEmpresaViewHolder(itemView);
        return historialVentasEmpresaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialEmpresaAdapter.HistorialVentasEmpresaViewHolder holder, int position) {
        Peticioncompra peticioncompra = listaDePeticiones.get(position);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child(peticioncompra.getProducto().getPk()+"/"+peticioncompra.getProducto().getNombreFoto());
        Glide.with(context).load(reference).into(holder.imagenPro);
        holder.nombre.setText("Producto: "+peticioncompra.getProducto().getNombre());
        holder.comprador.setText("Cliente: "+ peticioncompra.getNombreComprador());
        holder.cantidad.setText("Cantidad: "+ String.valueOf(peticioncompra.getCantidad()));
        double precioTotal = peticioncompra.getProducto().getPrecio() * Double.valueOf(peticioncompra.getCantidad());
        holder.precio.setText("Precio total(S/.): "+ precioTotal);
        holder.estado.setText("Estado: "+ peticioncompra.getEstado());

    }

    @Override
    public int getItemCount() {
        return listaDePeticiones.size();
    }

    public static  class HistorialVentasEmpresaViewHolder extends RecyclerView.ViewHolder{
        TextView nombre;
        TextView cantidad;
        TextView comprador;
        TextView precio;
        TextView estado;
        ImageView imagenPro;
        public HistorialVentasEmpresaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textViewNombreHistorialEmpresa);
            comprador = itemView.findViewById(R.id.textViewNombreClienteHistorialEmpresa);
            cantidad = itemView.findViewById(R.id.textViewCantidadHistorialEmpresa);
            precio = itemView.findViewById(R.id.textViewPrecioHistorialempresa);
            estado = itemView.findViewById(R.id.textViewEstado);
            imagenPro = itemView.findViewById(R.id.imageViewHistorialEmpresa);
        }
    }
}


