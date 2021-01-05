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

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.HistorialVentasViewHolder> {

    private ArrayList<Peticioncompra> listaDePeticiones;
    private Context context;

    public HistorialAdapter(ArrayList<Peticioncompra> listaDePeticiones, Context context) {
        this.listaDePeticiones = listaDePeticiones;
        this.context = context;
    }

    @NonNull
    @Override
    public HistorialAdapter.HistorialVentasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.historial_rv,parent,false);
        HistorialAdapter.HistorialVentasViewHolder historialVentasViewHolder = new HistorialAdapter.HistorialVentasViewHolder(itemView);
        return historialVentasViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialAdapter.HistorialVentasViewHolder holder, int position) {
        Peticioncompra peticioncompra = listaDePeticiones.get(position);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child(peticioncompra.getProducto().getPk()+"/"+peticioncompra.getProducto().getNombreFoto());
        Glide.with(context).load(reference).into(holder.imagenPro);
        holder.nombre.setText("Producto: "+peticioncompra.getProducto().getNombre());
        holder.empresa.setText("Cliente: "+ peticioncompra.getProducto().getNombreEmpresa());
        holder.cantidad.setText("Cantidad: "+ String.valueOf(peticioncompra.getCantidad()));
        double precioTotal = peticioncompra.getProducto().getPrecio() * Double.valueOf(peticioncompra.getCantidad());
        holder.precio.setText("Precio total(S/.): "+ precioTotal);
        holder.estado.setText("Estado: "+ peticioncompra.getEstado());
        if(peticioncompra.getEstado().equalsIgnoreCase("Rechazado")){
            holder.motivo.setVisibility(View.VISIBLE);
            holder.motivo.setText("Motivo : "+ peticioncompra.getMotivoRechazo());
        }


    }

    @Override
    public int getItemCount() {
        return listaDePeticiones.size();
    }

    public static  class HistorialVentasViewHolder extends RecyclerView.ViewHolder{
        TextView nombre;
        TextView cantidad;
        TextView empresa;
        TextView precio;
        TextView estado;
        TextView motivo;
        ImageView imagenPro;
        public HistorialVentasViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textViewNombreHistorial);
            empresa = itemView.findViewById(R.id.textViewNombreEmpresaHistorial);
            cantidad = itemView.findViewById(R.id.textViewCantidadHistorial);
            precio = itemView.findViewById(R.id.textViewPrecioTotalHistorial);
            estado = itemView.findViewById(R.id.textViewEstadoHistorial);
            motivo = itemView.findViewById(R.id.textViewRazonHistorial);
            imagenPro = itemView.findViewById(R.id.imageViewHistorial);
        }
    }
}

