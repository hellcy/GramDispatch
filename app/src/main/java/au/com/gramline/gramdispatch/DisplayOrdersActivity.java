package au.com.gramline.gramdispatch;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import au.com.gramline.gramdispatch.pojo.CollectedOrderList;
import au.com.gramline.gramdispatch.pojo.JobOrderList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class DisplayOrdersActivity extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    private Context context = null;
    APIInterface apiInterface;
    int size; // used to track the number of row under one order
    ObjectMapper mapper = new ObjectMapper();
    CollectedOrderList savedOrder = new CollectedOrderList();
    // Get TableLayout object in layout xml.
    TableLayout tableLayout = null;

    // Get the Intent that started this activity and extract the string
    Intent intent = null;
    String resume, orderNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_orders);
        tableLayout = (TableLayout)findViewById(R.id.table_layout_table);
        Button saveOrderButton = findViewById(R.id.saveOrderButton);
        Button uploadOrderButton = findViewById(R.id.uploadOrderButton);
        Button nextButton = findViewById(R.id.nextButton);

        size = 0;
        intent = getIntent();
        resume = intent.getStringExtra(EnterOrderNumberActivity.EXTRA_MESSAGE_TWO);

        context = getApplicationContext();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        // Capture the layout's TextView and set the string as its text
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        orderNumber = prefs.getString("orderNumber", null);

        if (orderNumber.isEmpty())
        {
            backToEnterPurchaseOrderNumberActivity();
            finish();
            return;
        }
        TextView orderNumberView = findViewById(R.id.OrderNumberView);
        orderNumberView.setText(getString(R.string.order_number_label, orderNumber));

        if (resume != null)
        {
            savedOrder = getOrderFromFile(savedOrder);
        }
        else
        {
            getOrderFromDatabase();
        }

        /* When save order button clicked. */
        saveOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < size; i++)
                {
                    EditText qtyCollected = tableLayout.findViewWithTag("qtyCollected_" + i);
                    if (qtyCollected.length() != 0)
                    {
                        String qtyCollectedValue = qtyCollected.getText().toString();
                        savedOrder.results.get(i).QtyCollected = Integer.parseInt(qtyCollectedValue);
                    }

                }
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String username = prefs.getString("username", null);
                savedOrder.USERNAME = username;
                Toast.makeText(getApplicationContext(), "Data Saved \n", Toast.LENGTH_SHORT).show();
                writeFileExternalStorage(savedOrder);
            }

        });
        /* When upload order button is clicked. */
        uploadOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<CollectedOrderList> call1 = apiInterface.createCollectedOrderList(savedOrder);
                // call and response type have to be the same to get a successful callback
                call1.enqueue(new Callback<CollectedOrderList>() {
                    @Override
                    public void onResponse(Call<CollectedOrderList> call, Response<CollectedOrderList> response) {
                        Toast.makeText(getApplicationContext(), "File Uploaded \n", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<CollectedOrderList> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "File failed to upload \n", Toast.LENGTH_SHORT).show();
                        call.cancel();
                    }
                });
            }
        });
        /* When NEXT button is clicked. */
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < size; i++)
                {
                    EditText qtyCollected = tableLayout.findViewWithTag("qtyCollected_" + i);
                    if (qtyCollected.length() != 0)
                    {
                        String qtyCollectedValue = qtyCollected.getText().toString();
                        savedOrder.results.get(i).QtyCollected = Integer.parseInt(qtyCollectedValue);
                    }
                }
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String username = prefs.getString("username", null);
                savedOrder.USERNAME = username;
                savedOrder.stage = 2;
                Toast.makeText(getApplicationContext(), "Data Saved \n", Toast.LENGTH_SHORT).show();
                writeFileExternalStorage(savedOrder);
                Intent intent = new Intent(context, PackActivity.class);
                startActivity(intent);

            }
        });
    }

    public void writeFileExternalStorage(CollectedOrderList collectedOrderList) {

        //Text of the Document
        String DirName = "order " + orderNumber + ".txt";

        //Checking the availability state of the External Storage.
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {

            //If it isn't mounted - we can't write into it.
            return;
        }

        //Create a new file that points to the root directory, with the given name:
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GramDispatch");
        if (!path.mkdirs())
        {
            path.mkdirs();
        }
        File file = new File(path, DirName);
        //File file = mapper.writeValue(new File("result.json"), collectedOrder);//Plain JSON
        /**
         * Write object to file
         */
        try {
            //mapper.writeValue(file, collectedOrderList);//Plain JSON
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, collectedOrderList);//Prettified JSON
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CollectedOrderList getOrderFromFile(CollectedOrderList collectedOrderList)
    {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GramDispatch", "order " + orderNumber + ".txt");
        try {
            collectedOrderList = mapper.readValue(file, CollectedOrderList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView accNo = findViewById(R.id.AccNoView);
        accNo.setText(getString(R.string.account_number_label, String.valueOf(collectedOrderList.results.get(0).ACCNO)));
        TextView orderDate = findViewById(R.id.OrderDateView);
        orderDate.setText(getString(R.string.order_date_label, collectedOrderList.results.get(0).ORDERDATE));

        for (CollectedOrderList.CollectedOrder collectedOrder : collectedOrderList.results) {
            // Create a new table row.
            TableRow tableRow = new TableRow(context);
            // Set new table row layout parameters.
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);

            // Add a TextView in the first column.
            TextView seqNo = new TextView(context);
            seqNo.setText(String.valueOf(collectedOrder.SEQNO));
            tableRow.addView(seqNo, 0,layoutParams);

            // Add a TextView in the second column.
            TextView stockCode = new TextView(context);
            stockCode.setText(String.valueOf(collectedOrder.STOCKCODE));
            tableRow.addView(stockCode, 1,new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

            // Add a TextView in the third column.
            TextView description = new TextView(context);
            description.setText(collectedOrder.DESCRIPTION);
            tableRow.addView(description, 2, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

            // Add a TextView in the fourth column.
            TextView qtyReqd = new TextView(context);
            qtyReqd.setText(String.valueOf(collectedOrder.ORD_QUANT));
            tableRow.addView(qtyReqd, 3, layoutParams);

            // Add a EditText in the fifth column.
            EditText qtyCollected = new EditText(context);
            if (collectedOrder.QtyCollected != null)
            {
                qtyCollected.setText(String.valueOf(collectedOrder.QtyCollected));
            }
            tableRow.addView(qtyCollected, 4, layoutParams);
            qtyCollected.setTag("qtyCollected_" + size);
            qtyCollected.setInputType(InputType.TYPE_CLASS_NUMBER);

            size++;
            tableLayout.addView(tableRow);
        }
        return collectedOrderList;
    }

    public void getOrderFromDatabase()
    {
        /**
         * GET List Resources
         **/
        Call<JobOrderList> call = apiInterface.doGetJobOrderList(orderNumber);
        call.enqueue(new Callback<JobOrderList>()
        {
            @Override
            public void onResponse (Call < JobOrderList > call, Response< JobOrderList > response){

                Log.d("TAG", response.code() + ""); // success code 200

                JobOrderList resource = response.body();
                List<JobOrderList.JobOrder> dataList = resource.results;

                if (dataList.size() == 0)
                {
                    backToEnterPurchaseOrderNumberActivity();
                }
                else
                {
                    for (JobOrderList.JobOrder joborder : dataList) {
                        // Create a new table row.
                        TableRow tableRow = new TableRow(context);
                        CollectedOrderList.CollectedOrder orderItem = new CollectedOrderList.CollectedOrder();
                        // Set new table row layout parameters.
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);

                        // Add a TextView in the first column.
                        TextView seqNo = new TextView(context);
                        seqNo.setText(String.valueOf(joborder.SEQNO));
                        tableRow.addView(seqNo, 0,layoutParams);
                        orderItem.SEQNO = joborder.SEQNO;

                        // Add a TextView in the second column.
                        TextView stockCode = new TextView(context);
                        stockCode.setText(String.valueOf(joborder.STOCKCODE));
                        tableRow.addView(stockCode, 1,new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
                        orderItem.STOCKCODE = joborder.STOCKCODE;

                        // Add a TextView in the third column.
                        TextView description = new TextView(context);
                        description.setText(joborder.DESCRIPTION);
                        tableRow.addView(description, 2, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
                        orderItem.DESCRIPTION = joborder.DESCRIPTION;

                        // Add a TextView in the fourth column.
                        TextView qtyReqd = new TextView(context);
                        qtyReqd.setText(String.valueOf(joborder.ORD_QUANT));
                        tableRow.addView(qtyReqd, 3, layoutParams);
                        orderItem.ORD_QUANT = joborder.ORD_QUANT.intValue();

                        // Add a EditText in the fifth column.
                        EditText qtyCollected = new EditText(context);
                        tableRow.addView(qtyCollected, 4, layoutParams);
                        qtyCollected.setTag("qtyCollected_" + size);
                        qtyCollected.setInputType(InputType.TYPE_CLASS_NUMBER);

                        orderItem.ACCNO = joborder.ACCNO;
                        orderItem.ORDERDATE = joborder.ORDERDATE;
                        orderItem.HDR_SEQNO = joborder.HDR_SEQNO;

                        size++;
                        savedOrder.results.add(orderItem);
                        savedOrder.stage = 1;
                        tableLayout.addView(tableRow);
                    }
                    TextView accNo = findViewById(R.id.AccNoView);
                    accNo.setText(getString(R.string.account_number_label, String.valueOf(savedOrder.results.get(0).ACCNO)));
                    TextView orderDate = findViewById(R.id.OrderDateView);
                    orderDate.setText(getString(R.string.order_date_label, savedOrder.results.get(0).ORDERDATE));
                }
            }
            @Override
            public void onFailure (Call < JobOrderList > call, Throwable t){
                call.cancel();
            }
        });
    }

    public void backToEnterPurchaseOrderNumberActivity ()
    {
        Toast toast = Toast.makeText(getApplicationContext(), "Job Order doesn't exist! \n", Toast.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(25);
        toast.show();
        Intent intent = new Intent(context, EnterOrderNumberActivity.class);
        startActivity(intent);
    }
}
