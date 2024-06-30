package com.example.ichat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailInput, passwordInput;
    private EditText signupEmailInput, signupPasswordInput;
    private Button btnEmailSignIn, btnRegister;
    private ProgressBar loginProgressBar, signupProgressBar;
    private LinearLayout loginSection, signupSection, progressLayout;
    private TextView toggleLoginSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.btn_google_sign_in);
        signInButton.setOnClickListener(v -> signInWithGoogle());

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        signupEmailInput = findViewById(R.id.signup_email_input);
        signupPasswordInput = findViewById(R.id.signup_password_input);
        btnEmailSignIn = findViewById(R.id.btn_email_sign_in);
        btnRegister = findViewById(R.id.btn_register);
        loginProgressBar = findViewById(R.id.login_progress_bar);
        signupProgressBar = findViewById(R.id.signup_progress_bar);
        loginSection = findViewById(R.id.login_section);
        signupSection = findViewById(R.id.signup_section);
        progressLayout = findViewById(R.id.progress_layout);
        toggleLoginSignup = findViewById(R.id.toggle_login_signup);

        btnEmailSignIn.setOnClickListener(v -> signInWithEmail());
        btnRegister.setOnClickListener(v -> registerWithEmail());

        toggleLoginSignup.setOnClickListener(v -> toggleLoginSignup());

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void toggleLoginSignup() {
        if (loginSection.getVisibility() == View.VISIBLE) {
            loginSection.setVisibility(View.GONE);
            signupSection.setVisibility(View.VISIBLE);
            toggleLoginSignup.setText("Already have an account? Sign in.");
        } else {
            loginSection.setVisibility(View.VISIBLE);
            signupSection.setVisibility(View.GONE);
            toggleLoginSignup.setText("Don't have an account? Sign up.");
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // Handle sign in failure
            Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user);
                        }
                    } else {
                        // Handle sign in failure
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithEmail() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoginProgress(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                    showLoginProgress(false);
                });
    }

    private void registerWithEmail() {
        String email = signupEmailInput.getText().toString().trim();
        String password = signupPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        showSignupProgress(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                    showSignupProgress(false);
                });
    }

    private void saveUserToFirestore(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getDisplayName());
        userMap.put("email", user.getEmail());
        userMap.put("photo", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");

        db.collection("users").document(user.getUid()).set(userMap)
                .addOnSuccessListener(aVoid -> {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Failed to save user information.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoginProgress(boolean show) {
        if (show) {
            loginProgressBar.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
            loginSection.setVisibility(View.GONE);
            signupSection.setVisibility(View.GONE);
        } else {
            loginProgressBar.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            loginSection.setVisibility(View.VISIBLE);
            signupSection.setVisibility(View.GONE);
        }
    }

    private void showSignupProgress(boolean show) {
        if (show) {
            signupProgressBar.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
            loginSection.setVisibility(View.GONE);
            signupSection.setVisibility(View.GONE);
        } else {
            signupProgressBar.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            loginSection.setVisibility(View.GONE);
            signupSection.setVisibility(View.VISIBLE);
        }
    }
}
