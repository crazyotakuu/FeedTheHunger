package com.example.feedthehunger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executor;

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] types={"Donee","Donor","Both"};
    int type;

    private TextView tv;
    Button signup;
    EditText uname,pass,conpass,mail,mobile;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        uname=findViewById(R.id.uname);
        mail=findViewById(R.id.mail);
        pass=findViewById(R.id.pass);
        conpass=findViewById(R.id.conpass);
        mobile=findViewById(R.id.mobile);
        Spinner spin = findViewById(R.id.edittext_login5);
        spin.setOnItemSelectedListener(this);
        signup=findViewById(R.id.button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uname.getText().toString().equals("")||mail.getText().toString().equals("")||pass.getText().toString().equals("")||
                        conpass.getText().toString().equals("")||mobile.getText().toString().equals("")||type==-1||!pass.getText().toString().equals(conpass.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Enter all the fields",Toast.LENGTH_LONG).show();
                    return;
                }

                insert obj=new insert();
                obj.execute();



            }
        });
        tv=findViewById(R.id.gotologin);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv==view){
                    Intent i=new Intent(SignUp.this,MainActivity.class);
                    startActivity(i);
                }
            }
        });
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        this.type=position;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    private class insert extends AsyncTask<Void, Void, Void>
    {
        String s="";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(mail.getText().toString(), pass.getText().toString())
                    .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUp.this,"Authentication failed. " + task.getException(),Toast.LENGTH_LONG).show();
                            } else {

                                Intent i=new Intent(SignUp.this,MainActivity.class);
                                startActivity(i);
                            }
                            // ...
                        }
                    });

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase.child("users").child(user.getUid()).setValue(new User(uname.getText().toString(),mail.getText().toString()
            ,pass.getText().toString(),mobile.getText().toString(),type));

            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public class User {

        public String username;
        public String email;
        public String password;
        public String mobile;
        public int type;


        public User(String username, String email,String password,String mobile,int type) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.mobile = mobile;
            this.type=type;

        }

    }

}

