package pe.pucp.tel306.Empresa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pe.pucp.tel306.Cliente.PaginaPrincipalCliente;
import pe.pucp.tel306.MainActivity;
import pe.pucp.tel306.R;

public class PaginaPrincipalEmpresa extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal_empresa);
    }

    public void agregarProducto(View view){
        Intent intent = new Intent(PaginaPrincipalEmpresa.this, AgregarProducto.class);
        startActivity(intent);
    }

}