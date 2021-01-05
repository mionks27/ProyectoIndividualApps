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

public class ProductosAdapterEmpresa extends RecyclerView.Adapter<ProductosAdapterEmpresa.ProductoViewHolder> {

    private ArrayList<Producto> listaProductos;
    private Context context;

    public ProductosAdapterEmpresa(ArrayList<Producto> listadeDispositivos, Context context) {
        this.listaProductos = listadeDispositivos;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.producto_rv,parent,false);
        ProductoViewHolder productoViewHolder = new ProductoViewHolder(itemView);
        return productoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        final Producto producto = listaProductos.get(position);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference reference =
                FirebaseStorage.getInstance().getReference().child(producto.getPk()+"/"+producto.getNombreFoto());
        Glide.with(context).load(reference).into(holder.imagenPro);
        holder.nombre.setText("Nombre: "+producto.getNombre());
        holder.precio.setText("Precio: "+ producto.getPrecio() + " Nuevos Soles");
        holder.descripcion.setText("Descripción: "+ producto.getDescricion());
        holder.stock.setText("Stock: "+ String.valueOf(producto.getStock()));
        holder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditarProducto.class);
                intent.putExtra("producto", producto);
                context.startActivity(intent);
            }
        });
        holder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Productos/"+producto.getPk()).setValue(null)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("JULIO","BORRADO EXITOSO EN TU DATABASE");
                                Toast.makeText(context, "Producto borrado exitósamente", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static  class ProductoViewHolder extends RecyclerView.ViewHolder{
        TextView nombre;
        TextView precio;
        TextView stock;
        TextView descripcion;
        ImageView imagenPro;
        Button borrar;
        Button editar;
        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.textViewNombre);
            stock = itemView.findViewById(R.id.textViewStock);
            precio = itemView.findViewById(R.id.textViewPrecio);
            descripcion = itemView.findViewById(R.id.TextViewDescripción);
            imagenPro = itemView.findViewById(R.id.imageViewFotoProductoLista);
            borrar = itemView.findViewById(R.id.buttonBorrar);
            editar = itemView.findViewById(R.id.buttonEditar);
        }
    }
}
