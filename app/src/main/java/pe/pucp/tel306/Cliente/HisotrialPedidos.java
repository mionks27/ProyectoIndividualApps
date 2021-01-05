package pe.pucp.tel306.Cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pe.pucp.tel306.Adapters.HistorialAdapter;
import pe.pucp.tel306.Adapters.VentasPendientesAdapter;
import pe.pucp.tel306.Empresa.VentasPendientes;
import pe.pucp.tel306.Entity.Peticioncompra;
import pe.pucp.tel306.R;

public class HisotrialPedidos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hisotrial_pedidos);
        listarHistorial();
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
                    if(peticioncompra.getUiComprador().equalsIgnoreCase(firebaseUser.getUid())){
                        if(!peticioncompra.getEstado().equalsIgnoreCase("Pendiente")){
                            peticioncompraArrayList.add(peticioncompra);
                        }
                    }
                }
                if(!peticioncompraArrayList.isEmpty()){
                    TextView textView = findViewById(R.id.textViewMessageHistorial);
                    if(textView.getVisibility()== View.VISIBLE){
                        textView.setVisibility(View.INVISIBLE);
                    }
                    HistorialAdapter adapter = new HistorialAdapter(peticioncompraArrayList, HisotrialPedidos.this);
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewHistorial);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HisotrialPedidos.this));
                }else{
                    TextView textView = findViewById(R.id.textViewMessageHistorial);
                    textView.setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewHistorial);
                    recyclerView.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}