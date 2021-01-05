package pe.pucp.tel306.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pe.pucp.tel306.Adapters.ProductosAdapterEmpresa;
import pe.pucp.tel306.Cliente.PaginaPrincipalCliente;
import pe.pucp.tel306.Entity.Producto;
import pe.pucp.tel306.MainActivity;
import pe.pucp.tel306.R;

public class PaginaPrincipalEmpresa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal_empresa);
        listarProductos();
    }

    public void agregarProducto(View view){
        Intent intent = new Intent(PaginaPrincipalEmpresa.this, AgregarProducto.class);
        startActivity(intent);
    }

    public void listarProductos(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Productos/"+firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Producto> productoArrayList = new ArrayList<>();
                for(DataSnapshot children : snapshot.getChildren()){
                    Producto producto = children.getValue(Producto.class);
                    productoArrayList.add(producto);
                }
                if(!productoArrayList.isEmpty()){
                    TextView textView = findViewById(R.id.textViewMessage);
                    if(textView.getVisibility()==View.VISIBLE){
                        textView.setVisibility(View.INVISIBLE);
                    }
                    ProductosAdapterEmpresa adapter = new ProductosAdapterEmpresa(productoArrayList,PaginaPrincipalEmpresa.this);
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewProductos);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(PaginaPrincipalEmpresa.this));
                }else{
                    TextView textView = findViewById(R.id.textViewMessage);
                    textView.setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewProductos);
                    recyclerView.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logOut(){
        AuthUI instance = AuthUI.getInstance();
        instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Lógica de cerrao de sesión lo pongo aquí porque luego lo ecesitaremos cuando acabemos el menú de cliente y TI
                Intent intent = new Intent(PaginaPrincipalEmpresa.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}