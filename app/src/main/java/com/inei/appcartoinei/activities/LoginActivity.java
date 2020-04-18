package com.inei.appcartoinei.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inei.appcartoinei.R;

public class LoginActivity extends AppCompatActivity {
    Button btn_ingresar;
    EditText usuario;
    EditText clave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario      = (EditText)findViewById(R.id.login_edtUsuario);
        clave        = (EditText)findViewById(R.id.login_edtClave);
        btn_ingresar = (Button) findViewById(R.id.login_btnIngresar);

        usuario.setText("marco");
        clave.setText("1234");
        btn_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuario.getText().toString().equals("marco") && clave.getText().toString().equals("1234")){
                    Intent intent = new Intent(LoginActivity.this,CargarMarcoActivity.class);
                    startActivity(intent);
                }
                else {
                    if(usuario.getText().toString().equals("user") && clave.getText().toString().equals("1234")){
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                    Toast.makeText(LoginActivity.this, "USUARIO O CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();}
                }
            }
        });
    }
}
