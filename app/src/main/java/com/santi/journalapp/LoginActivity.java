package com.santi.journalapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import util.JournalApi;

public class LoginActivity extends AppCompatActivity {

    private Button makeAcctButton;

    private Button loginButton;
    private AutoCompleteTextView emailAddress;
    private EditText password;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        password = findViewById(R.id.password);
        emailAddress = findViewById(R.id.email);
        makeAcctButton = findViewById(R.id.make_account_button);
        loginButton = findViewById(R.id.email_login_button);
        progressBar = findViewById(R.id.login_progress);

        progressBar.setVisibility(View.INVISIBLE);

    }

    private void loginEmailPasswordUser(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    assert user != null;
                    String currentUserId = user.getUid();
                    collectionReference.whereEqualTo("userID", currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if(e != null) {

                            }
                            assert queryDocumentSnapshots != null;

                            if(!queryDocumentSnapshots.isEmpty()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                    JournalApi journalApi = JournalApi.getInstance();
                                    journalApi.setUsername(snapshot.getString("username"));
                                    journalApi.setUserId(snapshot.getString("userID"));

                                    startActivity(new Intent(LoginActivity.this, PostJournalActivity.class));
                                }
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this,
                    "Please enter email and password",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void makeAcct(View view) {
        startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
    }

    public void login(View view) {
        Log.d("login", "click click");
        loginEmailPasswordUser(emailAddress.getText().toString(),password.getText().toString());
    }

}
