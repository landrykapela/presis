package tz.co.neelansoft.presis;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tz.co.neelansoft.presis.helpers.AppConstants;
import tz.co.neelansoft.presis.helpers.Transaction;

public class RecordSaleActivity extends AppCompatActivity {

    private static final String TAG = "RecordSaleActivity";
    private TextView mTextCancel;
    private TextView mTextSave;
    private EditText mTextDate;
    private EditText mTextAmount;
    private EditText mTextDescription;
    private EditText mTextBeneficiary;
    private CheckBox mToday;
    String myDate = "";
    DatabaseReference dbRoot = FirebaseDatabase.getInstance().getReference(AppConstants.FIREBASE_ROOT);
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record_sale);
        mAuth = FirebaseAuth.getInstance();

        mTextCancel      = findViewById(R.id.tvCancel);
        mTextSave = findViewById(R.id.tvSave);

        mTextAmount = findViewById(R.id.etAmount);
        mTextDescription = findViewById(R.id.etDescription);
        mTextBeneficiary = findViewById(R.id.etSoldTo);

        mTextDate = findViewById(R.id.etDate);
        mToday   = findViewById(R.id.cbDate);

        mToday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Date date = new Date();
                    myDate = String.valueOf(date.getTime());
                    String today = AppConstants.simpleDateFormat.format(date);

                    mTextDate.setText(today);
                }
                else{
                    mTextDate.setText("");
                }
            }
        });



        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                myDate = String.valueOf(calendar.getTimeInMillis());
                String newDate = AppConstants.simpleDateFormat.format(new Date(calendar.getTimeInMillis()));
                mTextDate.setText(newDate);
            }
        };

        mTextDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showDatePicker(datePickerListener,calendar);
            }
        });

        mTextSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record_transaction();
            }
        });

    }
    private void record_transaction(){
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference dbRef = dbRoot.child("transactions").child(user_id).push();

        String description = mTextDescription.getText().toString();
        String beneficiary = "Unknwon";
        if(!mTextBeneficiary.getText().toString().isEmpty() && !mTextBeneficiary.getText().toString().equals(" ")) beneficiary = mTextBeneficiary.getText().toString();
        String amount = mTextAmount.getText().toString();
        float mAmount = Float.parseFloat(amount);
        String time = "";

        Transaction transaction = new Transaction(dbRef.getKey(),mAmount,description,myDate,AppConstants.SALE);
        transaction.setBeneficiary(beneficiary);
        dbRef.setValue(transaction)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RecordSaleActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.e(TAG,"failed: "+task.getException().getMessage());
                            }
                        }
                });

        finish();
    }
    private void showDatePicker(DatePickerDialog.OnDateSetListener listener, Calendar cal){
        new DatePickerDialog(RecordSaleActivity.this,listener,
                cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();
    }
}
