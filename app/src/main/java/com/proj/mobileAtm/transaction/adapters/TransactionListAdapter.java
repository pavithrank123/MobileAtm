package com.proj.mobileAtm.transaction.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.proj.mobileAtm.R;
import com.proj.mobileAtm.common.Constants;
import com.proj.mobileAtm.databinding.AdapterItemBinding;
import com.proj.mobileAtm.transaction.model.entity.TransactionAdapterEntity;
import com.proj.mobileAtm.transaction.model.entity.TransactionEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder> {

    List<TransactionEntity> transactionEntityList;

    @Inject
    public TransactionListAdapter(List<TransactionEntity> transactionEntities) {
        this.transactionEntityList = transactionEntities;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        AdapterItemBinding adapterDateBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.adapter_item, viewGroup, false);
        return new TransactionViewHolder(adapterDateBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionEntity entity = transactionEntityList.get(position);
        holder.bind(entity);
    }

    @Override
    public int getItemCount() {
        if (transactionEntityList != null) {
            return transactionEntityList.size();
        }
        return 0;
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {

        private AdapterItemBinding adapterItemBinding;

        public TransactionViewHolder(@NonNull AdapterItemBinding adapterItemBinding) {
            super(adapterItemBinding.getRoot());
            this.adapterItemBinding = adapterItemBinding;
        }

        public void bind(TransactionEntity transactionEntity) {
            TransactionAdapterEntity adapterEntity = constructTransactionAdapterEntity(transactionEntity);
            adapterItemBinding.setTransactionEntity(adapterEntity);
            adapterItemBinding.executePendingBindings();
        }
    }

    private TransactionAdapterEntity constructTransactionAdapterEntity(TransactionEntity entity) {
        TransactionAdapterEntity transactionAdapterEntity = new TransactionAdapterEntity();

        Date updatedate = new Date(entity.getDebitedAt());
        SimpleDateFormat format = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String date = format.format(updatedate);
        transactionAdapterEntity.setDebitedAt(date);

        transactionAdapterEntity.setAmount(Constants.INR_CURRENCY_SYMBOL+ entity.getAmount());
        transactionAdapterEntity.setTransactionType(Constants.TRANSACTION_TYPE_DEBIT);

        return transactionAdapterEntity;

    }

    public List<TransactionEntity> getTransactionEntityList() {
        return transactionEntityList;
    }

    public void setTransactionEntityList(List<TransactionEntity> transactionEntityList) {
        this.transactionEntityList = transactionEntityList;
        TransactionListAdapter.this.notifyDataSetChanged();
    }
}
