package pe.pucp.tel306;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pe.pucp.tel306.Entity.User;

public class Registro extends AppCompatActivity {
    TextView name;
    EditText nombreEmpresa,distrito,phone;
    User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
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
            if(phone.getText().toString().trim().isEmpty()){
                phone.setError("Este campo no puede ser vacío");
            }else{
                if(nombreEmpresa.getText().toString().trim().isEmpty() && nombreEmpresa.getVisibility() == View.VISIBLE){
                    nombreEmpresa.setError("Este campo no puede ser vacío");
                }else{
                    user.setDistrito(distrito.getText().toString());
                    user.setNombreEmpresa(nombreEmpresa.getText().toString());
                    user.setTelefono(phone.getText().toString());
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
                }
            }
        }

    }

}