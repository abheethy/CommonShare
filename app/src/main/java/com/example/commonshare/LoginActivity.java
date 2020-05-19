package com.example.commonshare;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    Button b1,b2;
    EditText ed1,ed2, ed3;

    TextView tx1;
    DatabaseReference reference;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        reference = FirebaseDatabase.getInstance().getReference("userDetails");
        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);
        ed3 = (EditText)findViewById(R.id.editText3);
        userLocalStore = new UserLocalStore(this);
        TextView register = (TextView)findViewById(R.id.link_signup);
        register.setMovementMethod(LinkMovementMethod.getInstance());
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ed1.getText().toString();
                String pass = ed2.getText().toString();
                String grp = ed3.getText().toString();

                if(name == "" || pass == "" || grp ==  ""){
                    Toast.makeText(getApplicationContext(), "Credentials Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                reference.child(grp).orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue() == null){
                            Toast.makeText(getApplicationContext(), "User Does not Exists",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        boolean userFound = false;
                        for(DataSnapshot datas: dataSnapshot.getChildren()){
                            String userName = datas.child("name").getValue().toString();
                            String password = datas.child("password").getValue().toString();
                            String group = datas.child("group").getValue().toString();
                            if(password.equals(ed2.getText().toString()) &&
                            group.equals(ed3.getText().toString())){
                                Toast.makeText(getApplicationContext(),
                                        "Logging in ..."+userName,Toast.LENGTH_SHORT).show();
                                userFound = true;
                                UserDetails userDetails = new UserDetails();
                                userDetails.setName(userName);
                                userDetails.setPassword(password);
                                userDetails.setGroup(group);
                                userLocalStore.storeUserData(userDetails);
                                userLocalStore.setUserLoggedIn(true);
                                Intent intent = new Intent(LoginActivity.this, Amount.class);
                                intent.putExtra("userId", userName);
                                intent.putExtra("group", group);
                                startActivity(intent);
                            }
                        }
                        if(!userFound){
                            Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }
}