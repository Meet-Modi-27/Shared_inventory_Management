package com.meet.shared_inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    LinearLayout register;
    ImageView Google_Btn;
    Button login_btn;

    FirebaseAuth auth;
    GoogleSignInClient gClient;

    TextInputEditText email,password;
    TextInputLayout email1,pass1;

    String EmailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z]+\\.+[a-zA-Z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.register);
        Google_Btn = findViewById(R.id.google_btn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        email1 = findViewById(R.id.email_layout);
        pass1 = findViewById(R.id.pass_layout);
        auth = FirebaseAuth.getInstance();
        login_btn = findViewById(R.id.login_btn);


        // Check if a user is already signed in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, redirect to MainActivity with user UID
            Intent intent = new Intent(login.this, MainActivity.class);
            intent.putExtra("userId", currentUser.getUid());
            startActivity(intent);
            finish(); // Close LoginActivity so the user can't go back to it
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        gClient = GoogleSignIn.getClient(this, gso);

        Google_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = gClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(login.this,registration.class);
                startActivity(register);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });


    }


    private void performLogin() {
        String Email = email.getText().toString();
        String Pass = password.getText().toString();

        if (!Email.matches(EmailPattern)){
            email.setError("Enter Correct Email Id");
        } else if(Pass.isEmpty()|| Pass.length() < 6){
            password.setError("Minimum Password length is 6 characters.");
        } else {

            auth.signInWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            //checkEmailInDatabaseAndRetrieveUserId(Email, userId);
                            SendUserToNextActivity(userId);
                        }
                    } else {
                        // Handle authentication failure
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                            // Password is incorrect
                            pass1.setError("Incorrect password.");
                        } else {
                            // Other error occurred
                            Toast.makeText(login.this, "Email is wrong", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(Login.this, "Login failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    private void checkEmailInDatabaseAndRetrieveUserId(String email, String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child("Reg_Users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Retrieve user data
                        String retrievedUserId = userSnapshot.getKey(); // Get the user ID directly
                        if (retrievedUserId != null && retrievedUserId.equals(userId)) {
                            // Email matches and user ID retrieved
                            SendUserToNextActivity(retrievedUserId);
                            return;
                        }
                    }

                    // User ID not found for the matched email
                    Toast.makeText(login.this, "User ID not found", Toast.LENGTH_SHORT).show();
                } else {
                    // Email not found in the database
                    Toast.makeText(login.this, "Email not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(login.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SendUserToNextActivity(String userid) {
        Intent intent = new Intent(login.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("userId",userid);
        startActivity(intent);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK){
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(login.this, "Signed in Successfully!!", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = task.getResult().getUser();
                                boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                                if (isNewUser) {
                                    // Redirect to Complete Registration Activity
                                    /*Intent intent = new Intent(login.this, partial_registration.class);
                                    intent.putExtra("userId", user.getUid());
                                    startActivity(intent);
                                    finish();*/
                                } else {
                                    // Existing user, redirect to main activity
                                    startActivity(new Intent(login.this, MainActivity.class));
                                    finish();
                                }
                            }
                            else {
                                Toast.makeText(login.this, "Failed to Sign in: "+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    });
}