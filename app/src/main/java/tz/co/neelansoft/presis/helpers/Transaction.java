package tz.co.neelansoft.presis.helpers;

import android.util.Log;

import java.util.Date;

public class Transaction {
    private static final String TAG="Transaction";
    private String id;
    private Float amount;
    private String description;
    private String date;
    private int type;

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    private String beneficiary;

    public Transaction() {
    }

    public Transaction(String id, Float amount, String description, String date,int type) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;

    }

    public void setDate(String date) {
        this.date = date;
    }
    public int getType(){
        return type;
    }
    public void setType(int type){
        this.type = type;
    }
}
