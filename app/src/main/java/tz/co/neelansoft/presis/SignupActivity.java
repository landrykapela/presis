package tz.co.neelansoft.presis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import tz.co.neelansoft.presis.helpers.AppConstants;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private CheckBox mCheckboxPassword;
    private EditText mEditEmail;
    private EditText mEditFullname;
    private EditText mEditPassword;
    private TextView mTextSignup;
    private TextView mTextCancel;
    private TextView mTextError;
    private ProgressBar mProgressBar;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        mCheckboxPassword = findViewById(R.id.cbPassword);
        mEditEmail = findViewById(R.id.etEmail);
        mEditFullname = findViewById(R.id.etFullName);
        mEditPassword = findViewById(R.id.etPassword);

        mTextCancel = findViewById(R.id.tvCancel);
        mTextSignup = findViewById(R.id.tvSignin);
        mTextError = findViewById(R.id.tvTextError);

        mProgressBar = findViewById(R.id.progressBar);

        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,SigninActivity.class));
                finish();
            }
        });

        mTextSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupWithEmailAndPassword();
            }
        });

        mCheckboxPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mEditPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else{
                    mEditPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_CLASS_TEXT);
                }
                mEditPassword.setSelection(mEditPassword.getText().toString().length());
            }
        });

    }
    private void signupWithEmailAndPassword(){
        mProgressBar.setVisibility(View.VISIBLE);
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();
        final String fullname = mEditFullname.getText().toString();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final FirebaseUser user = task.getResult().getUser();
                            UserProfileChangeRequest upcr = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullname)
                                    .build();
                            user.updateProfile(upcr)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProgressBar.setVisibility(View.GONE);
                                            updateUI(user);
                                        }
                                    });
                        }
                        else{
                            Log.e(TAG,task.getException().getMessage(),task.getException());
                            mProgressBar.setVisibility(View.GONE);
                            mTextError.setText(R.string.signup_error);
                        }
                    }
                });

    }

    private void updateUI(FirebaseUser user){
        if(user != null){
            startActivity(new Intent(SignupActivity.this,MainActivity.class));
            DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference(AppConstants.FIREBASE_ROOT).child("users");
            dbRoot.child(user.getUid()).child("online").setValue(true);
            dbRoot.child(user.getUid()).child("lastaccess").setValue(new Date());
            finish();
        }
    }
}
