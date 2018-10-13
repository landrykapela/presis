package tz.co.neelansoft.presis;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tz.co.neelansoft.presis.helpers.AppConstants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonSales;
    private Button mButtonPurchases;
    private Button mButtonExpenses;
    private ProgressBar mProgressBar;

    FirebaseAuth mAuth;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(AppConstants.FIREBASE_ROOT);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mButtonExpenses = findViewById(R.id.btnExpenses);
        mButtonPurchases = findViewById(R.id.btnPurchases);
        mButtonSales     = findViewById(R.id.btnSales);
        mProgressBar = findViewById(R.id.progressBar);

        mButtonSales.setOnClickListener(this);
        mButtonPurchases.setOnClickListener(this);
        mButtonExpenses.setOnClickListener(this);

    }
    @Override
    public void onClick(View v){
        int button_id = v.getId();
        switch (button_id){
            case R.id.btnExpenses:
                startActivity(new Intent(MainActivity.this,RecordExpenseActivity.class));
                break;
            case R.id.btnPurchases:
                startActivity(new Intent(MainActivity.this,PurchaseTransactionsActivity.class));
                break;
            case R.id.btnSales:
                startActivity(new Intent(MainActivity.this,SalesTransactionsActivity.class));
                break;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this,SigninActivity.class));
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        goOffline();
    }
    private void goOffline(){
        if(mAuth.getCurrentUser() != null) dbRef.child("users").child(mAuth.getCurrentUser().getUid()).child("online").setValue(false);

    }
    private void signout(){
        mProgressBar.setVisibility(View.VISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.confirm_signout)
                .setMessage(R.string.confirm_signout_message)
                .setCancelable(true)
                .setPositiveButton(R.string.signout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userid = mAuth.getCurrentUser().getUid();
                        dbRef.child("users").child(userid).child("online").setValue(false)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mProgressBar.setVisibility(View.GONE);
                                        if(task.isSuccessful()){
                                            mAuth.signOut();
                                            startActivity(new Intent(MainActivity.this,SigninActivity.class));
                                            finish();
                                        }
                                    }
                                });

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_activity,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_signout){
            signout();
        }
        return super.onOptionsItemSelected(item);
    }
}
