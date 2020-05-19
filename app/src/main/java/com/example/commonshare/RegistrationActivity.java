package com.example.commonshare;

import android.content.Intent;
import android.os.Bundle;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.commonshare.Amount;
import com.example.commonshare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by tutlane on 08-01-2018.
 */

public class RegistrationActivity extends AppCompatActivity {
    private DatabaseReference ref;
    UserDetails userDetails;
    EditText name;
    EditText pass;
    EditText cnfrmpass;
    EditText group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        ref = FirebaseDatabase.getInstance().getReference();
        TextView login = (TextView)findViewById(R.id.lnkLogin);
        Button regButton = (Button) findViewById(R.id.regbtn);

        login.setMovementMethod(LinkMovementMethod.getInstance());
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        regButton.setMovementMethod(LinkMovementMethod.getInstance());
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name =(EditText) findViewById(R.id.name);
                pass =(EditText) findViewById(R.id.passsword);
                cnfrmpass =(EditText) findViewById(R.id.confirmpasssword);
                group =(EditText) findViewById(R.id.group);

                String userPassword = pass.getText().toString();
                String confirmPassword = cnfrmpass.getText().toString();
                if(name.getText().toString().equals("") || pass.getText().toString().equals("")
                || cnfrmpass.getText().toString().equals("") || group.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Credentials Cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!userPassword.equals(confirmPassword)){
                    Toast.makeText(getApplicationContext(), "Password Mismatch",Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase.getInstance().getReference("userDetails").child(group.getText().toString()).orderByChild("name").
                        equalTo(name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userName = name.getText().toString();
                        String password = pass.getText().toString();
                        String groupName = group.getText().toString();


                        userDetails = new UserDetails();
                        userDetails.setName(userName);
                        userDetails.setGroup(groupName);
                        userDetails.setPassword(password);

                        if(dataSnapshot == null || dataSnapshot.getValue() == null){
                            if (userName != null && userName != "" &&
                                    password != null && password != "" &&
                                    groupName != null && groupName != ""){
                                ref.child("userDetails").child(groupName).child(userName).setValue(userDetails);
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(getApplicationContext(), "User Not Inserted",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "User Already Exists",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}