package com.meet.shared_inventory;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meet.shared_inventory.Scanner.BarcodeScannerActivity;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    Button _logout;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    TextView _name,_role,_companyName,_companyId;
    FirebaseDatabase db;
    DatabaseReference dbref;
    ImageButton scan,item,inventory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        _logout = findViewById(R.id.logout_btn);
        _name = findViewById(R.id.user_name);
        _role = findViewById(R.id.user_role);
        _companyName = findViewById(R.id.company_name);
        _companyId = findViewById(R.id.company_id);
        auth = FirebaseAuth.getInstance();
        user  = auth.getCurrentUser();
        userId = user.getUid().toString();
        scan = findViewById(R.id.scan_qr);
        item = findViewById(R.id.itemDetails);
        db = FirebaseDatabase.getInstance();
        inventory = findViewById(R.id.inventory);

        Intent intent = getIntent();
        if (intent.hasExtra("Scan_result")) {
            String scannedData = intent.getStringExtra("Scan_result");
            Log.d("MainActivity", "Received Scan Result: " + scannedData);
            Toast.makeText(this, "Received: " + scannedData, Toast.LENGTH_SHORT).show();
        }

        fetchDetails(userId);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        _logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignOut();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BarcodeScannerActivity.class);
                startActivity(intent);

            }
        });
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),itemDetails.class);
                startActivity(intent);
            }
        });
        inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, myItems.class);
                startActivity(intent);
            }
        });

    }
    private void SignOut(){
        auth.signOut();

        Intent intent = new Intent(MainActivity.this, login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void fetchDetails(String userId){
        dbref = db.getReference().child("users").child("Reg_Users").child(userId);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    _name.setText(snapshot.child("name").getValue(String.class));
                    _role.setText(snapshot.child("role").getValue(String.class));
                    _companyName.setText(snapshot.child("company_Name").getValue(String.class));
                    _companyId.setText(snapshot.child("company_id").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Database_Error", error.getMessage());
            }
        });
    }
}