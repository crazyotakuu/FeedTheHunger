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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String[] types={"Donee","Donor"};
    private TextView tv;
    private Button login;
    private EditText mail,pass;
    private FirebaseAuth mAuth;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Spinner spin = findViewById(R.id.edittext_loginspin);
        spin.setOnItemSelectedListener(this);
        tv=findViewById(R.id.gotosignup);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv==view){
                    Intent i=new Intent(MainActivity.this,SignUp.class);
                    startActivity(i);
                }
            }
        });
        login=findViewById(R.id.buttonlogin);
        mail=findViewById(R.id.edittext_loginmail);
        pass=findViewById(R.id.edittext_loginpass);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mail.getText().toString().equals("")||pass.getText().toString().equals("")||type==-1){
                    Toast.makeText(getApplicationContext(),"Enter all the feilds",Toast.LENGTH_LONG).show();
                    return;
                }
                authenticate a=new authenticate();
                a.execute();
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
        type=position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        type=-1;
    }

    private class authenticate extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;

        @Override
        protected void onPreExecute() //Starts the progress dailog
        {
            progress = ProgressDialog.show(MainActivity.this, "Loging in",
                    "Loading! Please Wait...", true);
        }

        @Override
        protected Void doInBackground(Void... voids)  // Connect to the database, write query and add items to array list
        {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(mail.getText().toString(), pass.getText().toString())
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                                DatabaseReference mDbRef = mDatabase.getReference("users");
                                Toast.makeText(MainActivity.this,user.getUid(),
                                        Toast.LENGTH_SHORT).show();
                                mDbRef.child(user.getUid()).child("type").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String t=dataSnapshot.getValue().toString();


                                        if((t=="3")||(type==Integer.parseInt(t))){
                                            Toast.makeText(MainActivity.this,"Login Success",Toast.LENGTH_LONG).show();
                                            Intent i=new Intent(MainActivity.this,Donor.class);
                                            startActivity(i);
                                        }
                                        else{
                                            return;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
// Failed to read value

                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) // disimissing progress dialoge, showing error and setting up my ListView
        {
            progress.dismiss();

        }
    }

    public class User {

        public String username;
        public String email;
        public String password;
        public String mobile;
        public int type;

        public User() {
        }

        public User(String username, String email,String password,String mobile,int type) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.mobile = mobile;
            this.type=type;

        }

    }



}