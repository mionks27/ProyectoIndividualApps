package pe.pucp.tel306.Cliente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pe.pucp.tel306.Adapters.ProductosAdapterEmpresa;
import pe.pucp.tel306.Empresa.PaginaPrincipalEmpresa;
import pe.pucp.tel306.Entity.Producto;
import pe.pucp.tel306.MainActivity;
import pe.pucp.tel306.R;

public class PaginaPrincipalCliente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal_cliente);
        listarProductos();
    }

    ////para relacionar el layout de menú con esta vista

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cliente, menu);
        return true;
    }

    ///para linkear las opciones del menú con una acción en particular de forma centralizada ///también puede realizarse desde el primer método onCreate pero de otra manera, revisar min 01:18:43 del video zoom

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCliente:
                View view = findViewById(R.id.menuCliente);
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_cliente, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.historialCompras:
//                                Intent intent = new Intent(PaginaPrincipalTI.this, SolicitudesPendientes.class);
//                                startActivity(intent);
//                                finish();
                                return true;
                            case R.id.comprasRecientes:
//                                Intent intent1 = new Intent(PaginaPrincipalTI.this, SolicitudesPendientes.class);
//                                startActivity(intent1);
//                                finish();
                                return true;
                            case R.id.verProductos:
//                                Intent intent = new Intent(PaginaPrincipalTI.this, SolicitudesPendientes.class);
//                                startActivity(intent);
//                                finish();
                                return true;
                            case R.id.cerrarsesion:
                                logOut();
                                return true;
                            default:
                                return false;

                        }
                    }
                });
                popupMenu.show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void logOut(){
        AuthUI instance = AuthUI.getInstance();
        instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Lógica de cerrao de sesión lo pongo aquí porque luego lo ecesitaremos cuando acabemos el menú de cliente y TI
                Intent intent = new Intent(PaginaPrincipalCliente.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void listarProductos(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Productos/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Producto> productoArrayList = new ArrayList<>();
                for(DataSnapshot children : snapshot.getChildren()){
                    Producto producto = children.getValue(Producto.class);
                        productoArrayList.add(producto);
                }
                if(!productoArrayList.isEmpty()){
                    TextView textView = findViewById(R.id.textViewMessagecliente);
                    if(textView.getVisibility()==View.VISIBLE){
                        textView.setVisibility(View.INVISIBLE);
                    }
                    ProductosAdapterEmpresa adapter = new ProductosAdapterEmpresa(productoArrayList,PaginaPrincipalCliente.this);
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewProdusctosCliente);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(PaginaPrincipalCliente.this));
                }else{
                    TextView textView = findViewById(R.id.textViewMessagecliente);
                    textView.setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewProdusctosCliente);
                    recyclerView.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}