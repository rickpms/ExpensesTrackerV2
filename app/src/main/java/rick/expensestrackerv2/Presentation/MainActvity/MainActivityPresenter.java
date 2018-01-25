package rick.expensestrackerv2.Presentation.MainActvity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

import rick.expensestrackerv2.Domain.Model.BillModel;
import rick.expensestrackerv2.R;
import rick.expensestrackerv2.Utils.Injection;
import rick.expensestrackerv2.Utils.RecyclerAdapter;

/**
 * Created by Rick on 1/21/2018.
 */

public class MainActivityPresenter implements NotificationCallback {

    private static final String TAG = "MainActivityPresenter";

    static RecyclerAdapter recyclerAdapter;

    SharedPreferences.Editor editor;

    ArrayList<BillModel> bills = new ArrayList<>();

    public MainActivityPresenter(RecyclerView recyclerView, Context context) {

        Injection.getFirebaseDatabaseClassInstance().getDatabaseInstance();
        Injection.getFirebaseDatabaseClassInstance().getDatabaseReferenceInstance();

        Injection.getFirebaseDatabaseClassInstance().initializeCallback(this);

        checkFirstTime(context);

        initData(context, recyclerView);
    }

    public void initData(Context context, RecyclerView recyclerView) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("savedBills", Context.MODE_PRIVATE);

        Gson gson = new Gson();

        String json = sharedPreferences.getString("bills", "");

        Type type = new TypeToken<ArrayList<BillModel>>(){}.getType();
        ArrayList<BillModel> savedBills = gson.fromJson(json, type);

        if (savedBills != null) {
            bills.addAll(savedBills);
        }

        recyclerAdapter = new RecyclerAdapter(bills);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void notifyWhendone() {

        recyclerAdapter.notifyDataSetChanged();
    }

    public void addNewBill(final Context context) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View customDialogView = inflater.inflate(R.layout.dialog_add_new_bill, null);

        dialogBuilder.setView(customDialogView);

        final EditText userInput = customDialogView.findViewById(R.id.editTextDialogUserInput);

        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        BillModel bill = new BillModel(userInput.getText().toString(), false);

                        bills.add(bill);

                        saveBills(context);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void saveBills(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("savedBills", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(bills);
        editor.putString("bills", json);
        editor.apply();
    }

    public void checkFirstTime(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("FirstTime", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        boolean firstRun = sharedPreferences.getBoolean("firstRun", true);

        if (firstRun) {

            bills.add(new BillModel("testingFirstRun", true));
            Log.d(TAG, "checkFirstTime: testing this bitch 1");

            editor.putBoolean("firstRun", false).apply();
        }
    }
}
