package com.meet.shared_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.meet.shared_inventory.Models.Registration_Model;

public class registration extends AppCompatActivity {

    String[] roles = {"Role","Admin", "Owner", "Worker"};
    AutoCompleteTextView roleDropdown;
    ImageView back;
    Button reg;
    TextInputEditText _name,_email,_pass,_confirmPass,_companyName,_companyId;
    TextInputLayout _nameLayout,_emailLayout,_passLayout,_confirmPassLayout,_companyNameLayout,_companyIdLayout,_roleLayout;
    String EmailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference db;
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        mAuth= FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("users");

        roleDropdown = findViewById(R.id.Role);
        back = findViewById(R.id.back_btn);
        reg = findViewById(R.id.Reg_btn);
        _name = findViewById(R.id.Reg_Name);
        _email = findViewById(R.id.Reg_email);
        _pass = findViewById(R.id.Reg_pass);
        _confirmPass = findViewById(R.id.Reg_confirm_pass);
        _companyName = findViewById(R.id.company_name);
        _companyId = findViewById(R.id.company_id);
        _nameLayout = findViewById(R.id.name);
        _emailLayout = findViewById(R.id.email_layout);
        _passLayout = findViewById(R.id.pass_layout);
        _confirmPassLayout =findViewById(R.id.confirm_pass_layout);
        _companyNameLayout = findViewById(R.id.company_name_layout);
        _companyIdLayout = findViewById(R.id.company_id_layout);
        _roleLayout = findViewById(R.id.roleLayout);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        roleDropdown.setAdapter(adapter);
        roleDropdown.post(() -> roleDropdown.setDropDownWidth(400));

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });

    }
    private void performRegistration() {
        String Name = _name.getText().toString();
        String Email = _email.getText().toString();
        String Pass = _pass.getText().toString();
        String Confirm_pass = _confirmPass.getText().toString();
        String Company_Name = _companyName.getText().toString();
        String Company_Id = _companyId.getText().toString();
        String Role = roleDropdown.getText().toString();

        if (Name.isEmpty()){
            _nameLayout.setError("Enter the Name.");
        } else if (!Email.matches(EmailPattern)){
            _emailLayout.setError("Enter Correct Email Id");
        } else if(Pass.isEmpty()|| Pass.length() < 6){
            _passLayout.setError("Minimum Password length is 6 characters.");
        } else if (!Confirm_pass.equals(Pass)) {
            _confirmPassLayout.setError("Enter Same Password as above");
        } else if (Company_Name.isEmpty()) {
            _companyNameLayout.setError("Enter the Company Name.");
        } else if (Company_Id.isEmpty()) {
            _companyIdLayout.setError("Enter the Phone Number");
        } else if (Role.equals("Role")) {
            _roleLayout.setError("Select a role");
        } else {

            mAuth.createUserWithEmailAndPassword(Email, Pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                UpdateDataToFirebase(Name,Email,Company_Name,Company_Id,Role);
                                SendUserToNextActivity();
                                Toast.makeText(registration.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("Error",task.getException().toString());
                                Toast.makeText(registration.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void UpdateDataToFirebase(String NAME, String EMAIL, String COMPANY_NAME, String COMPANY_ID, String ROLE) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userId = user.getUid();
            DatabaseReference userRef = db.child("Reg_Users").child(userId);
            Registration_Model registrationModel = new Registration_Model(NAME, EMAIL, COMPANY_NAME,COMPANY_ID,ROLE);
            userRef.setValue(registrationModel);
        }
    }

    private void SendUserToNextActivity() {
        Intent intent = new Intent(registration.this, MainActivity.class);
        intent.putExtra("userId",userId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}