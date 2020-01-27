package com.santi.journalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
     Button makeAcctButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        usernameEditText = findViewById(R.id.username_account);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        makeAcctButton = findViewById(R.id.make_account_button);
        progressBar = findViewById(R.id.create_acct_progress);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            currentUser = firebaseAuth.getCurrentUser();
            if(currentUser != null) {

            }
            else{
                //no user yet
            }
            }
        };
           makeAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(emailEditText.getText().toString()) && !TextUtils.isEmpty(passwordEditText.getText().toString()) &&
                        !TextUtils.isEmpty(usernameEditText.getText().toString())) {
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String username = usernameEditText.getText().toString().trim();

                    createUserEmailAccount(email, password, username);
                }
            }
           });
        }

    @Override
    protected void onStart() {
        super.onStart();
        //checks if user is signed in
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void createUserEmailAccount(String email, String password, final String username) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)) { //all the fields have to be filled out hence &&
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        currentUser = firebaseAuth.getCurrentUser();
                        assert currentUser != null;
                        final String currentUserID = currentUser.getUid();

                        //Create a map so we can create a user in the User Collection
                        Map<String,String> userObj = new HashMap<>();
                        userObj.put("userID", currentUserID);
                        userObj.put("username", username);

                        //save to our FireStore database
                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(Objects.requireNonNull(Objects.requireNonNull(task).getResult()).exists()) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            String name = Objects.requireNonNull(task.getResult()).getString("username");

                                            JournalApi journalApi = JournalApi.getInstance();
                                            journalApi.setUserId(currentUserID);
                                            journalApi.setUsername(name);

                                            Intent intent = new Intent(CreateAccountActivity.this,PostJournalActivity.class);
                                            intent.putExtra("username", name);
                                            intent.putExtra("userID", currentUserID);
                                            startActivity(intent);
                                        } else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}

