package com.proj.mobileAtm.base.service;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.proj.mobileAtm.common.CommonCallBack;
import com.proj.mobileAtm.common.CommonUtils;
import com.proj.mobileAtm.common.Constants;
import com.proj.mobileAtm.common.GsonInterfaceAdapter;
import com.proj.mobileAtm.common.SharedPreferenceUtils;
import com.proj.mobileAtm.dashboard.service.SharedPreferenceService;
import com.proj.mobileAtm.transaction.model.entity.CurrencyDispatched;
import com.proj.mobileAtm.transaction.model.entity.CurrencyTypes;
import com.proj.mobileAtm.transaction.model.entity.KeyValueEntity;
import com.proj.mobileAtm.transaction.model.entity.Notes;
import com.proj.mobileAtm.transaction.model.entity.ResponseData;
import com.proj.mobileAtm.transaction.model.entity.TransactionEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public class LocalCacheServiceImpl implements LocalCacheService {

    private SharedPreferences sharedPreferences;

    private String AVAILABLE_NOTES = "available-notes";
    private String STORED_PASS = "saved-pass";
    private String SESSION_PASS = "session-pass";

    private final double CHANGE_PERCENTAGE = 0.5;

    @Inject
    public LocalCacheServiceImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }


    @Override
    public void setDefaultValues() {
        String currencyNotesList = SharedPreferenceUtils.getString(sharedPreferences, AVAILABLE_NOTES);
        if (currencyNotesList == null || currencyNotesList.isEmpty()) {
            HashMap<String,Notes> availableNotes = new HashMap<>();
            CurrencyTypes.FiveHundredRupeeNote fiveHundredRupeeNote = new CurrencyTypes.FiveHundredRupeeNote(10);
            CurrencyTypes.TwoThousandRupeeNote twoThousandRupeeNote = new CurrencyTypes.TwoThousandRupeeNote(20);
            CurrencyTypes.HundredRupeeNote hundredRupeeNote = new CurrencyTypes.HundredRupeeNote(12);

            availableNotes.put(hundredRupeeNote.getType(),hundredRupeeNote);
            availableNotes.put(twoThousandRupeeNote.getType(),twoThousandRupeeNote);
            availableNotes.put(fiveHundredRupeeNote.getType(),fiveHundredRupeeNote);
            String storeString = new Gson().toJson(availableNotes);
            SharedPreferenceUtils.putString(sharedPreferences, AVAILABLE_NOTES, storeString);
        }

        String storedPass = SharedPreferenceUtils.getString(sharedPreferences, STORED_PASS);
        if(storedPass==null || storedPass.isEmpty()){
            SharedPreferenceUtils.putString(sharedPreferences, STORED_PASS, "1234");
        }
    }

    @Override
    public void fetchBalance(CommonCallBack<ResponseData<String>> callBack) {

        if(!validateSession(callBack)){
            return;
        }
        String currencyNotesList = SharedPreferenceUtils.getString(sharedPreferences, AVAILABLE_NOTES);
        if (currencyNotesList == null || currencyNotesList.isEmpty()) {
            callBack.data(new ResponseData<>(false,"ATM Out of order"),false);
        }

        HashMap<String,Notes> availableNotes = new Gson().fromJson(currencyNotesList,new TypeToken<HashMap<String,Notes>>(){}.getType());

        int balance = 0;

        for(HashMap.Entry<String,Notes> entrySet: availableNotes.entrySet()){
            balance += entrySet.getValue().getAvailable() * entrySet.getValue().getValue();
        }

        ResponseData<String> responseData = new ResponseData<>(true,"Success");
        responseData.setData(String.valueOf(balance));
        callBack.data(responseData, true);
    }

    @Override
    public boolean checkUserCredentials(String enteredPass) {
        String password = SharedPreferenceUtils.getString(sharedPreferences, STORED_PASS);
        if (password != null && enteredPass != null && enteredPass.equals(password)) {
            return true;
        }

        return false;
    }

    @Override
    public void setUserCredentials(String enteredPass) {
        SharedPreferenceUtils.putString(sharedPreferences, SESSION_PASS, enteredPass);
    }

    @Override
    public void processTransaction(int enteredAmount, CommonCallBack<ResponseData<CurrencyDispatched>> callBack) {
        String currencyNotesList = SharedPreferenceUtils.getString(sharedPreferences, AVAILABLE_NOTES);
        if (currencyNotesList == null || currencyNotesList.isEmpty()) {
            callBack.data(new ResponseData<>(false,"ATM Out of order"),false);
        }
        if(!validateSessionPassword(callBack)){
            return;
        }
        HashMap<String,Notes> availableNotes = new Gson().fromJson(currencyNotesList,new TypeToken<HashMap<String,Notes>>(){}.getType());

        Notes hundredRupeeNote = new CurrencyTypes.HundredRupeeNote(0);
        Notes fiveHundredRupeeNote = new CurrencyTypes.FiveHundredRupeeNote(0);
        Notes twoThousandRupeeNote = new CurrencyTypes.TwoThousandRupeeNote(0);

        for(HashMap.Entry<String,Notes> entrySet: availableNotes.entrySet()){
            if(entrySet.getValue().getValue() == hundredRupeeNote.getValue()){
                hundredRupeeNote.setAvailable(entrySet.getValue().getAvailable());
            }
            if(entrySet.getValue().getValue() == fiveHundredRupeeNote.getValue()){
                fiveHundredRupeeNote.setAvailable(entrySet.getValue().getAvailable());
            }
            if(entrySet.getValue().getValue() == twoThousandRupeeNote.getValue()){
                twoThousandRupeeNote.setAvailable(entrySet.getValue().getAvailable());
            }
        }

        if(hundredRupeeNote.getAvailable() ==0 && fiveHundredRupeeNote.getAvailable() == 0 && twoThousandRupeeNote.getAvailable() >0 ){
            callBack.data(new ResponseData<>(false,"Bank is out of cash"),false);
            return;
        }

        int amountLeft = getAmountAvailableInBank(enteredAmount, hundredRupeeNote, fiveHundredRupeeNote, twoThousandRupeeNote);

        if(amountLeft == 0){

            int bulkCurrency = (int) (enteredAmount * CHANGE_PERCENTAGE);
            if(bulkCurrency % 100 != 0 ){
                int nextRoundingValue = bulkCurrency/100;
                bulkCurrency = nextRoundingValue *100;
            }
            int smallCurrency = enteredAmount - bulkCurrency;

            int twoThousandUsed = Math.min(bulkCurrency / twoThousandRupeeNote.getValue(),twoThousandRupeeNote.getAvailable());
            bulkCurrency = bulkCurrency - twoThousandUsed* twoThousandRupeeNote.getValue();

            int totalAmountLeft = smallCurrency+bulkCurrency;
            int fiveHundRedUsed = Math.min(totalAmountLeft / fiveHundredRupeeNote.getValue(), fiveHundredRupeeNote.getAvailable());
            totalAmountLeft = totalAmountLeft - fiveHundRedUsed * fiveHundredRupeeNote.getValue();

            int hundredUsed = Math.min(totalAmountLeft / hundredRupeeNote.getValue(),hundredRupeeNote.getAvailable());
            totalAmountLeft = totalAmountLeft - hundredUsed * hundredRupeeNote.getValue();

            final CurrencyTypes.FiveHundredRupeeNote updatedFiveHundred = new CurrencyTypes.FiveHundredRupeeNote(fiveHundredRupeeNote.getAvailable() - fiveHundRedUsed);
            final CurrencyTypes.TwoThousandRupeeNote updatedTwoThousand = new CurrencyTypes.TwoThousandRupeeNote(twoThousandRupeeNote.getAvailable() - twoThousandUsed);
            final CurrencyTypes.HundredRupeeNote updatedHundred = new CurrencyTypes.HundredRupeeNote(hundredRupeeNote.getAvailable() - hundredUsed);

            availableNotes.put(updatedFiveHundred.getType(),updatedFiveHundred);
            availableNotes.put(updatedTwoThousand.getType(),updatedTwoThousand);
            availableNotes.put(updatedHundred.getType(),updatedHundred);
            String storeString = new Gson().toJson(availableNotes);
            SharedPreferenceUtils.putString(sharedPreferences, AVAILABLE_NOTES, storeString);
            saveTransactionRecord(enteredAmount);
            ResponseData<CurrencyDispatched> responseData = createResponseObject(hundredRupeeNote, fiveHundredRupeeNote, twoThousandRupeeNote, twoThousandUsed, fiveHundRedUsed, hundredUsed);
            callBack.data(responseData,true);
        } else {
            callBack.data(new ResponseData<>(false,"Bank is out of cash"),false);
        }

    }

    @Override
    public void getTransactionList(CommonCallBack<ResponseData<String>> callBack) {

        if(!validateSession(callBack)){
            return;
        }
        String transactionListString = SharedPreferenceUtils.getString(sharedPreferences, Constants.TRANSACTION_LIST);
        ResponseData<String> responseData = new ResponseData<>(true,"Success");
        responseData.setData(transactionListString);
        callBack.data(responseData, true);
    }

    private boolean validateSession(CommonCallBack<ResponseData<String>> callBack) {
        String storedPass = SharedPreferenceUtils.getString(sharedPreferences, STORED_PASS);
        String sessionPassword = SharedPreferenceUtils.getString(sharedPreferences, SESSION_PASS);

        if (CommonUtils.checkIsNullOrEmpty(storedPass) || CommonUtils.checkIsNullOrEmpty(sessionPassword)) {
            callBack.data(new ResponseData<>(false, "Invalid session"), false);
            return false;
        }
        if (!storedPass.equals(sessionPassword)) {
            callBack.data(new ResponseData<>(false, "Incorrect PIN"), false);
            return false;
        }
        return true;
    }

    private void saveTransactionRecord(int enteredAmount) {

        List<TransactionEntity> transactionEntityList = new ArrayList<>();

        String transactionListString= SharedPreferenceUtils.getString(sharedPreferences, Constants.TRANSACTION_LIST);
        if(transactionListString!=null && !transactionListString.isEmpty()){
            List<TransactionEntity> currentTransactionList = new Gson().fromJson(transactionListString, new TypeToken<List<TransactionEntity>>(){}.getType());

            if(currentTransactionList!=null && !currentTransactionList.isEmpty()){
                transactionEntityList.addAll(currentTransactionList);
            }
        }

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .transactionType(Constants.TRANSACTION_TYPE_DEBIT)
                .amount(enteredAmount)
                .debitedAt(System.currentTimeMillis())
                .build();
        transactionEntityList.add(transactionEntity);

        SharedPreferenceUtils.putString(sharedPreferences, Constants.TRANSACTION_LIST, new Gson().toJson(transactionEntityList));
    }

    private boolean validateSessionPassword(CommonCallBack<ResponseData<CurrencyDispatched>> callBack) {
        String storedPass = SharedPreferenceUtils.getString(sharedPreferences, STORED_PASS);
        String sessionPassword = SharedPreferenceUtils.getString(sharedPreferences, SESSION_PASS);

        if (CommonUtils.checkIsNullOrEmpty(storedPass) || CommonUtils.checkIsNullOrEmpty(sessionPassword)) {
            callBack.data(new ResponseData<>(false, "Invalid session"), false);
            return false;
        }
        if (!storedPass.equals(sessionPassword)) {
            callBack.data(new ResponseData<>(false, "Incorrect PIN"), false);
            return false;
        }
        return true;
    }

    @NonNull
    private ResponseData<CurrencyDispatched> createResponseObject(Notes hundredRupeeNote, Notes fiveHundredRupeeNote, Notes twoThousandRupeeNote, int twoThousandCurrency, int fiveHundRedCurrency, int hundredCurrency) {
        List<KeyValueEntity> keyValueEntityList = new ArrayList<>();
        if(hundredCurrency>0) keyValueEntityList.add(new KeyValueEntity(hundredRupeeNote.getValue(), hundredCurrency));
        if(fiveHundRedCurrency>0 ) keyValueEntityList.add(new KeyValueEntity(fiveHundredRupeeNote.getValue(), fiveHundRedCurrency));
        if(twoThousandCurrency>0 ) keyValueEntityList.add(new KeyValueEntity(twoThousandRupeeNote.getValue(), twoThousandCurrency));

        CurrencyDispatched currencyDispatched = new CurrencyDispatched(keyValueEntityList);
        ResponseData<CurrencyDispatched> responseData = new ResponseData<>(true,"Success");
        responseData.setData(currencyDispatched);
        return responseData;
    }

    private int getAmountAvailableInBank(int enteredAmount, Notes hundredRupeeNote, Notes fiveHundredRupeeNote, Notes twoThousandRupeeNote) {
        //3900
        if(twoThousandRupeeNote.getAvailable() > 0 && enteredAmount > 0){
            int twoThousandCurrency = enteredAmount / twoThousandRupeeNote.getValue();
            enteredAmount = enteredAmount - Math.min(twoThousandCurrency,twoThousandRupeeNote.getAvailable()) * twoThousandRupeeNote.getValue();
        }
        //1900
        if(fiveHundredRupeeNote.getAvailable() > 0 && enteredAmount > 0){
            int fiveHundRedCurrency = enteredAmount / fiveHundredRupeeNote.getValue();
            enteredAmount = enteredAmount - Math.min(fiveHundRedCurrency,fiveHundredRupeeNote.getAvailable()) * fiveHundredRupeeNote.getValue();
        }
        //
        if(hundredRupeeNote.getAvailable() > 0 && enteredAmount > 0 ){
            int hundredCurrency = enteredAmount / hundredRupeeNote.getValue();
            enteredAmount = enteredAmount - Math.min(hundredCurrency,hundredRupeeNote.getAvailable()) * hundredRupeeNote.getValue();
        }
        return enteredAmount;
    }

}
