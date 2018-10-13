package tz.co.neelansoft.presis.helpers;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tz.co.neelansoft.presis.R;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder>{

    private OnTransactionSelectedListener mTransactionSelectedListener;
    private Context mContext;
    private List<Transaction> mTransactionList = new ArrayList<>();
    public TransactionAdapter(Context context,OnTransactionSelectedListener listener){
        this.mContext = context;
        this.mTransactionSelectedListener = listener;
    }

    public void setTransactionList(List<Transaction> transactions){
        this.mTransactionList = transactions;
    }
    public List<Transaction> getTransactionList(){
        return mTransactionList;
    }
    @Override
    public int getItemCount(){
        return mTransactionList.size();
    }
    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position){
        vh.bind(position);
    }
    public interface OnTransactionSelectedListener{
        void onTransactionSelected(String transactionId);
    }

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvTextAmount;
        TextView tvTextDate;
        TextView tvTextDescription;

        public ViewHolder(View itemView){
            super(itemView);

            tvTextDescription = itemView.findViewById(R.id.tvItemDescription);
            tvTextAmount = itemView.findViewById(R.id.tvItemAmount);
            tvTextDate = itemView.findViewById(R.id.tvItemDate);
        }

        public void bind(int position){

            Transaction txn = mTransactionList.get(position);
            tvTextDate.setText(AppConstants.simpleDateFormat.format(new Date(Long.parseLong(txn.getDate()))));
            tvTextAmount.setText(String.valueOf(txn.getAmount()).concat("TZS"));
            tvTextDescription.setText(txn.getDescription());
        }

        @Override
        public void onClick(View v){
            String trans_id = mTransactionList.get(getAdapterPosition()).getId();
            mTransactionSelectedListener.onTransactionSelected(trans_id);
        }

}
}
