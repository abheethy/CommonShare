package com.example.commonshare;


import android.accounts.Account;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Activity;

import android.content.Intent;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class Amount extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    EditText amounttext;
    EditText remarksText;
    TextView balanceLabel;
    TextView transactionLabel;
    TextView transactionLabelTwo;
    Button withdrawbtn;
    Button depositbtn;
    // DatabaseReference ref;
    AmountEntity amountEntity;
    AmountEntityList amountEntityList;
    Double total =0.0;
    private DatabaseReference ref;
    private DatabaseReference reference;
    String userId;
    String groupName;
    UserLocalStore userLocalStore;
    ProgressDialog nDialog;
    public final Object self = this;

    //This is our tablayout
    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;







    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_icon:
                userLocalStore.setUserLoggedIn(false);
                userLocalStore.clearUserData();
                Intent intent = new Intent(Amount.this, LoginActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Transaction"));
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views
        tabLayout.setOnTabSelectedListener(this);

        userLocalStore = new UserLocalStore(this);
        userId = getIntent().getStringExtra("userId");
        groupName = getIntent().getStringExtra("group");


        ref = FirebaseDatabase.getInstance().getReference().child("AmountEntity");
        reference = FirebaseDatabase.getInstance().getReference().child("AmountEntityList");
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tabone, null);
        amounttext=(EditText)findViewById(R.id.amounttext);
        remarksText=(EditText)findViewById(R.id.remarkstext);
        withdrawbtn=(Button)findViewById(R.id.withdrawbtn);
        depositbtn=(Button)findViewById(R.id.depositbtn);
        balanceLabel=(TextView) findViewById(R.id.balancelabel);
        transactionLabel=(TextView)findViewById(R.id.recenttransactiondata);
        transactionLabelTwo=(TextView) findViewById(R.id.recenttransactiondatatwo);


        nDialog = new ProgressDialog(Amount.this);
        nDialog.setMessage("Loading..");
        nDialog.setTitle("Fetching data");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();

        //  Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim);
        // transactionLabel.startAnimation(animation);
        ref.orderByChild("group").
                equalTo(groupName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot
                                             dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // String value= dataSnapshot.getValue(String.class);
                balanceLabel=(TextView) findViewById(R.id.balancelabel);
                transactionLabel=(TextView)findViewById(R.id.recenttransactiondata);
                transactionLabelTwo=(TextView)findViewById(R.id.recenttransactiondatatwo);
                GenericTypeIndicator<List<Member>> t = new GenericTypeIndicator<List<Member>>() {};
                Object data = dataSnapshot.getValue(Object.class);
                Map<String, Object> dataMap = new HashMap<>();
                Map<String, Object> map = new HashMap<>();
                String daataValue = "";
                String userName = "";
                String transactionType = "";
                String remarks = "";
                String lasttransaction = "";
                if(data != null){
                    daataValue= data.toString();
                }
                try {
                    map=JsonMapper.parseJson(daataValue);
                }catch (IOException e){

                }
                String group = "";
                if(map.get(groupName) != null){
                    dataMap = (Map<String, Object>) map.get(groupName);
                }
                if(dataMap.get("group") != null){
                    group = dataMap.get("group").toString();
                }
                if(dataMap.get("balance") != null && group != "" && group.equals(groupName)){
                    total = Double.valueOf(dataMap.get("balance").toString());
                    if(dataMap.get("userid") !=null){
                        userName = dataMap.get("userid").toString();
                    }
                    transactionType = dataMap.get("transactiontype").toString();
                    remarks = dataMap.get("remarks").toString();
                    lasttransaction = dataMap.get("lasttransaction").toString();
                    balanceLabel.setText("Balance : ₹"+dataMap.get("balance"));
                    nDialog.dismiss();
                    transactionLabel.setText(userName +" has " + transactionType + " ₹"+
                            lasttransaction+" for " + remarks);
                    final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    animator.setRepeatCount(ValueAnimator.INFINITE);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.setDuration(9000L);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            final float progress = (float) animation.getAnimatedValue();
                            final float width = transactionLabel.getWidth();
                            final float translationX = width * progress;
                            transactionLabel.setTranslationX(translationX);
                            transactionLabelTwo.setTranslationX(translationX - width);
                        }
                    });
                    animator.start();

                }else{
                    nDialog.dismiss();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(),
                        "Error"+error,
                        Toast.LENGTH_LONG).show();
                nDialog.dismiss();
            }
        });

        reference.child(groupName).orderByChild("group").
                equalTo(groupName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot
                                             dataSnapshot){

                final ListView list = findViewById(R.id.list);
                ArrayList<String> arrayList = new ArrayList<>();

                arrayAdapter.clear();
                Object data = dataSnapshot.getValue(Object.class);
                Map<String, Object> dataMap = new HashMap<>();
                Map<String, Object> map = new HashMap<>();
                Map<String, Object> valueMap = new HashMap<>();
                map = (Map<String, Object> )data;
                String dataValue = "";
                if(map == null) {
                return;
                }

                String group = "";

                String dataString = "";
                for ( Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    valueMap = (Map<String, Object>)entry.getValue();
                    dataString = "";
                    dataString+= valueMap.get("userid") + " has "+valueMap.get("transactiontype") + " "+
                            valueMap.get("lasttransaction")+" for "+valueMap.get("remarks")+" on "+valueMap.get("date");
                    arrayList.add(dataString);
                }
                arrayAdapter.addAll(arrayList);
                list.setAdapter(arrayAdapter);

            }


            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getApplicationContext(),
                        "Error"+error,
                        Toast.LENGTH_LONG).show();
            }
        });

    }


    public void depositbutton(View v) {
        Double depAmount = 0.0;
        amounttext=(EditText)findViewById(R.id.amounttext);
        balanceLabel=(TextView) findViewById(R.id.balancelabel);
        remarksText=(EditText)findViewById(R.id.remarkstext);
        String remarks = remarksText.getText().toString();
        String dep = amounttext.getText().toString();
        if(dep == "" || !(dep instanceof  String) || dep.length() == 0){
            Toast.makeText(getApplicationContext(),
                    "Amount Empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(remarks == "" || !(remarks instanceof  String) || remarks.length() == 0){
            Toast.makeText(getApplicationContext(),
                    "Remarks Empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);

        if (dep != "") {
            depAmount = Double.valueOf(dep) + total;
            balanceLabel.setText("Balance : ₹" + depAmount.toString());
            amountEntity = new AmountEntity();
            amountEntity.setBalance(depAmount.toString());
            amountEntity.setRemarks(remarksText.getText().toString());
            amountEntity.setUserid(userId);
            amountEntity.setTransactiontype("deposited");
            amountEntity.setGroup(groupName);
            amountEntity.setLasttransaction(amounttext.getText().toString());
            ref.child(groupName).setValue(amountEntity);

            amountEntityList = new AmountEntityList();
            amountEntityList.setBalance(depAmount.toString());
            amountEntityList.setRemarks(remarksText.getText().toString());
            amountEntityList.setUserid(userId);
            amountEntityList.setTransactiontype("deposited");
            amountEntityList.setGroup(groupName);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            formatter.format(date);
            amountEntityList.setDate(String.valueOf(date));
            amountEntityList.setLasttransaction(amounttext.getText().toString());
            reference.child(groupName).child(UUID.randomUUID().toString()).setValue(amountEntityList);


            amounttext.setText("");
            remarksText.setText("");
            Toast.makeText(getApplicationContext(),
                    "Amount Credited",
                    Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),
                    "Amount Empty",
                    Toast.LENGTH_SHORT).show();
        }

    }
    public void withdrawbutton(View v) {
        Double withdrawAmount = 0.0;
        balanceLabel=(TextView) findViewById(R.id.balancelabel);
        amounttext=(EditText)findViewById(R.id.amounttext);
        remarksText=(EditText)findViewById(R.id.remarkstext);
        String with = amounttext.getText().toString();
        String remarks = remarksText.getText().toString();
        if(with == "" || !(with instanceof  String) || with.length() == 0){
            Toast.makeText(getApplicationContext(),
                    "Amount Empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(remarks == "" || !(remarks instanceof  String) || remarks.length() == 0){
            Toast.makeText(getApplicationContext(),
                    "Remarks Empty",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (with != "") {
            withdrawAmount = total - Double.valueOf(with);
            if(withdrawAmount < 0){
                Toast.makeText(getApplicationContext(),
                        "No Sufficiant Amount",
                        Toast.LENGTH_SHORT).show();
                amounttext.setText("");
                return;
            }
            balanceLabel.setText("Balance : " + withdrawAmount.toString());
            amountEntity = new AmountEntity();
            amountEntity.setBalance(withdrawAmount.toString());
            amountEntity.setRemarks(remarksText.getText().toString());
            amountEntity.setUserid(userId);
            amountEntity.setTransactiontype("withdrawn");
            amountEntity.setGroup(groupName);
            amountEntity.setLasttransaction(amounttext.getText().toString());
            ref.child(groupName).setValue(amountEntity);


            amountEntityList = new AmountEntityList();
            amountEntityList.setBalance(withdrawAmount.toString());
            amountEntityList.setRemarks(remarksText.getText().toString());
            amountEntityList.setUserid(userId);
            amountEntityList.setTransactiontype("withdrawn");
            amountEntityList.setGroup(groupName);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            formatter.format(date);
            amountEntityList.setDate(String.valueOf(date));
            amountEntityList.setLasttransaction(amounttext.getText().toString());
            reference.child(groupName).child(UUID.randomUUID().toString()).setValue(amountEntityList);

            amounttext.setText("");
            remarksText.setText("");
            Toast.makeText(getApplicationContext(),
                    "Amount Debited",
                    Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getApplicationContext(),
                    "Amount Empty",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
