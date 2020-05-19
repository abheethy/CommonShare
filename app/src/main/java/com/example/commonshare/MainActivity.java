package com.example.commonshare;


import android.accounts.Account;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Activity;

import android.content.Intent;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.commonshare.Member;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.util.JsonMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amount);
        userLocalStore = new UserLocalStore(this);
        // Intent intent = new Intent(this, LoginActivity.class);
        // startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authenticate() == true){
            UserDetails userDetails = userLocalStore.getLoggdInUser();
            Intent intent = new Intent(this, Amount.class);
            intent.putExtra("userId", userDetails.getName());
            intent.putExtra("group", userDetails.getGroup());
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
    private boolean authenticate(){
        return userLocalStore.isUserLoggedIn();
    }
}
