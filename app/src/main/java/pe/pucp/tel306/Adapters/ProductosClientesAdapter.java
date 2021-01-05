package pe.pucp.tel306.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import pe.pucp.tel306.Empresa.EditarProducto;
import pe.pucp.tel306.Entity.Producto;
import pe.pucp.tel306.R;

public class ProductosClientesAdapter extends RecyclerView.Adapter<ProductosClientesAdapter.ProductoClienteViewHolder> {

    private ArrayList<Producto> listaProductos;
    private Context context;

    public ProductosClientesAdapter(ArrayList<Producto> listadeDispositivos, Context context) {
        this.listaProductos = listadeDispositivos;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductosClientesAdapter.ProductoClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.productos_cliente_rv,parent,false);
        ProductosClientesAdapter.ProductoClienteViewHolder productoClienteViewHolder = new ProductosClientesAdapter.ProductoClienteViewHolder(itemView);
        return productoClienteViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosClientesAdapter.ProductoClienteViewHolder holder, int position) {
        final Producto producto = listaProductos.get(position);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child(producto.getPk()+"/"+producto.getNombreFoto());
        Glide.with(context).load(reference).into(holder.imagenPro);
        holder.nombre.setText("Nombre: "+producto.getNombre());
        holder.precio.setText("Precio: "+ producto.getPrecio() + " Nuevos Soles");
        holder.empresa.setText("Empresa: "+ producto.getNombreEmpresa());
        holder.stock.setText("Stock: "+ String.valueOf(producto.getStock()));
        holder.comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, EditarProducto.class);
//                intent.putExtra("producto", producto);
//                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static  class ProductoClienteViewHolder extends RecyclerView.ViewHolder{
        TextView nombre;
        TextView precio;
        TextView stock;
        TextView empresa;
        ImageView imagenPro;
        Button comprar;
        public ProductoClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textViewNombreClientelista);
            stock = itemView.findViewById(R.id.textViewStockListaClientes);
            precio = itemView.findViewById(R.id.textViewPrecioListaClientes);
            empresa = itemView.findViewById(R.id.textViewNombreEmpresaListaClientes);
            imagenPro = itemView.findViewById(R.id.imageViewProductoListaClientes);
            comprar = itemView.findViewById(R.id.buttonComprar);
        }
    }
}
