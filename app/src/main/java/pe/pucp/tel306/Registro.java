package pe.pucp.tel306;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pe.pucp.tel306.Empresa.AgregarProducto;
import pe.pucp.tel306.Empresa.PaginaPrincipalEmpresa;
import pe.pucp.tel306.Entity.User;

public class Registro extends AppCompatActivity {
    TextView name;
    EditText nombreEmpresa,distrito,phone;
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        name = findViewById(R.id.textView7);
        nombreEmpresa = findViewById(R.id.editTextNombreEmpresa);
        distrito = findViewById(R.id.editTextDistrito);
        phone = findViewById(R.id.editTextPhone);
        String [] lista = {"Cliente","Empresa"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,lista);
        Spinner spinner = findViewById(R.id.spinnerRolUsuario);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("JULIO","SELECCIONASTE ESTO : " + parent.getItemAtPosition(position).toString());
                if(position == 0){
                  user.setRol("Cliente");
                  if(nombreEmpresa.getVisibility() == View.VISIBLE){
                      nombreEmpresa.setVisibility(View.INVISIBLE);
                      name.setVisibility(View.INVISIBLE);
                  }
                }else if(position == 1){
                    user.setRol("Empresa");
                    nombreEmpresa.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    public void guardarUsuario(View view){

        if(distrito.getText().toString().trim().isEmpty()){
            distrito.setError("Este campo no puede ser vacío");
        }else{
            if(phone.getText().toString().trim().isEmpty()||phone.getText().toString().trim().length()!=9){
                phone.setError("Tiene que colocar 9 dígitos");
            }else{
                if(nombreEmpresa.getText().toString().trim().isEmpty() && nombreEmpresa.getVisibility() == View.VISIBLE){
                    nombreEmpresa.setError("Este campo no puede ser vacío");
                }else{
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                    databaseReference.child("users/").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<User> userArrayList = new ArrayList<>();
                            for(DataSnapshot children : snapshot.getChildren()){
                                User user = children.getValue(User.class);
                                if(user.getRol().equalsIgnoreCase("Empresa") && user.getNombreEmpresa().equalsIgnoreCase(nombreEmpresa.getText().toString())){
                                    userArrayList.add(user);
                                }
                            }
                            if(userArrayList.isEmpty()){
                                user.setDistrito(distrito.getText().toString().trim());
                                user.setNombreEmpresa(nombreEmpresa.getText().toString().trim());
                                user.setTelefono(phone.getText().toString().trim());
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("users/"+firebaseUser.getUid()).setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("JULIO","GUARDADO EXITOSO EN TU DATABASE");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                e.printStackTrace();
                                            }
                                        });
                            }else{
                                nombreEmpresa.setError("Este nombre ya ha sido usado");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }
        }

    }

}