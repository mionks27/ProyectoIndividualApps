package pe.pucp.tel306.Empresa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pe.pucp.tel306.Adapters.HistorialAdapter;
import pe.pucp.tel306.Adapters.HistorialEmpresaAdapter;
import pe.pucp.tel306.Cliente.HisotrialPedidos;
import pe.pucp.tel306.Cliente.PaginaPrincipalCliente;
import pe.pucp.tel306.Cliente.PedidosPendientes;
import pe.pucp.tel306.Entity.Peticioncompra;
import pe.pucp.tel306.MainActivity;
import pe.pucp.tel306.R;

public class HistorialVentasEmpresa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_ventas_empresa);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listarHistorial();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_empresa, menu);
        return true;
    }

    ///para linkear las opciones del menú con una acción en particular de forma centralizada ///también puede realizarse desde el primer método onCreate pero de otra manera, revisar min 01:18:43 del video zoom

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuEmpresa:
                View view = findViewById(R.id.menuEmpresa);
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.getMenuInflater().inflate(R.menu.meno_popup_empresa, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.historialVentas:
//                                Intent intent = new Intent(PaginaPrincipalTI.this, SolicitudesPendientes.class);
//                                startActivity(intent);
//                                finish();
                                return true;
                            case R.id.reservasPendientes:
                                Intent intent1 = new Intent(HistorialVentasEmpresa.this, VentasPendientes.class);
                                startActivity(intent1);
                                finish();
                                return true;
                            case R.id.gestionarProductos:
                                Intent intent = new Intent(HistorialVentasEmpresa.this, PaginaPrincipalEmpresa.class);
                                startActivity(intent);
                                finish();
                                return true;
                            case R.id.cerrarSesionempresa:
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


    public void listarHistorial(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Solicitudes/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Peticioncompra> peticioncompraArrayList = new ArrayList<>();
                for(DataSnapshot children : snapshot.getChildren()){
                    Peticioncompra peticioncompra = children.getValue(Peticioncompra.class);
                    if(peticioncompra.getProducto().getUidUser().equalsIgnoreCase(firebaseUser.getUid())){
                        if(!peticioncompra.getEstado().equalsIgnoreCase("Pendiente")){
                            peticioncompraArrayList.add(peticioncompra);
                        }
                    }
                }
                if(!peticioncompraArrayList.isEmpty()){
                    TextView textView = findViewById(R.id.textViewMessageHistorialEmpresa);
                    if(textView.getVisibility()== View.VISIBLE){
                        textView.setVisibility(View.INVISIBLE);
                    }
                    HistorialEmpresaAdapter adapter = new HistorialEmpresaAdapter(peticioncompraArrayList, HistorialVentasEmpresa.this);
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewHistorialEmpresa);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HistorialVentasEmpresa.this));
                }else{
                    TextView textView = findViewById(R.id.textViewMessageHistorialEmpresa);
                    textView.setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewHistorialEmpresa);
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
                Intent intent = new Intent(HistorialVentasEmpresa.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

}