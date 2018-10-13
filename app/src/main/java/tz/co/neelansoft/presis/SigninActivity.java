package tz.co.neelansoft.presis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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

public class SigninActivity extends AppCompatActivity {

    private CheckBox mCheckboxPassword;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private TextView mTextSignin;
    private TextView mTextCancel;
    private TextView mTextError;
    private ProgressBar mProgressBar;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mCheckboxPassword = findViewById(R.id.cbPassword);
        mEditEmail = findViewById(R.id.etEmail);
        mEditPassword = findViewById(R.id.etPassword);

        mTextCancel = findViewById(R.id.tvCancel);
        mTextSignin = findViewById(R.id.tvSignin);
        mTextError = findViewById(R.id.tvTextError);

        mProgressBar = findViewById(R.id.progressBar);

        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this,SignupActivity.class));
                finish();
            }
        });

        mTextSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithEmailAndPassword();
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

    private void signInWithEmailAndPassword(){
        mProgressBar.setVisibility(View.VISIBLE);
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final FirebaseUser user = task.getResult().getUser();
                            mProgressBar.setVisibility(View.GONE);
                            updateUI(user);
                        }
                        else{
                            mProgressBar.setVisibility(View.GONE);
                            mTextError.setText(R.string.signin_error);
                        }
                    }
                });

    }
    private void updateUI(FirebaseUser user){
        if(user != null){
            startActivity(new Intent(SigninActivity.this,MainActivity.class));
            DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference(AppConstants.FIREBASE_ROOT).child("users");
            dbRoot.child(user.getUid()).child("online").setValue(true);
            dbRoot.child(user.getUid()).child("lastaccess").setValue(new Date());
            finish();
        }
    }
}
