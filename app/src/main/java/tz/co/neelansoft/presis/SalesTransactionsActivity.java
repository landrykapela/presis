package tz.co.neelansoft.presis;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import tz.co.neelansoft.presis.helpers.AppConstants;
import tz.co.neelansoft.presis.helpers.Transaction;
import tz.co.neelansoft.presis.helpers.TransactionAdapter;

public class SalesTransactionsActivity extends AppCompatActivity implements TransactionAdapter.OnTransactionSelectedListener {
    private static final String TAG = "SalesTransaction";
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private TextView mTextNoData;
    private ProgressBar mProgressBar;
    private TransactionAdapter mTransactionAdapter;
    DatabaseReference databaseRoot = FirebaseDatabase.getInstance().getReference(AppConstants.FIREBASE_ROOT);
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transaction_list);
        mRecyclerView = findViewById(R.id.recyclerview);
        mFloatingActionButton = findViewById(R.id.fab);
        mTextNoData = findViewById(R.id.tvNoData);
        mProgressBar = findViewById(R.id.progressBar);
        mTransactionAdapter = new TransactionAdapter(this,this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        loadTransactions();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                viewHolder.itemView.setBackgroundColor(getResources().getColor(R.color.color_error));
                AlertDialog.Builder builder = new AlertDialog.Builder(SalesTransactionsActivity.this)
                        .setTitle(R.string.dialog_title_delete)
                        .setMessage(R.string.dialog_confirm_delete)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTransaction(viewHolder);
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadTransactions();
                            }
                        });
                if(!builder.create().isShowing()) builder.create().show();
            }
        }).attachToRecyclerView(mRecyclerView);


        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalesTransactionsActivity.this,RecordSaleActivity.class));
            }
        });
    }
    private void deleteTransaction(RecyclerView.ViewHolder vh){
        String user_id = mAuth.getCurrentUser().getUid();
        int index = vh.getAdapterPosition();
        String trans_id = mTransactionAdapter.getTransactionList().get(index).getId();
        databaseRoot.child("transactions").child(user_id).child(trans_id).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SalesTransactionsActivity.this,"Transaction deleted",Toast.LENGTH_SHORT).show();
                        loadTransactions();
                    }
                });

    }

    private void loadTransactions(){
        String user_id = mAuth.getCurrentUser().getUid();
        mProgressBar.setVisibility(View.VISIBLE);
        databaseRoot.child("transactions").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Transaction> salesTransactions = new ArrayList<>();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Transaction tx = ds.getValue(Transaction.class);
                    if(tx.getType() == AppConstants.SALE){
                        salesTransactions.add(tx);
                    }
                }
                mProgressBar.setVisibility(View.GONE);

                mRecyclerView.setAdapter(mTransactionAdapter);
                mTransactionAdapter.setTransactionList(salesTransactions);
                mTransactionAdapter.notifyDataSetChanged();
                if(mTransactionAdapter.getItemCount() == 0){
                    mTextNoData.setVisibility(View.VISIBLE);
                }
                else{
                    mTextNoData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onTransactionSelected(String transaction_id){
        Log.d(TAG,"selected id: "+transaction_id);
    }

}
