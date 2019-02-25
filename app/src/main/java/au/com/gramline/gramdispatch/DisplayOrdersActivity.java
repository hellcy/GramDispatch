package au.com.gramline.gramdispatch;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

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

    private Context context = null;
    APIInterface apiInterface;
    int size; // used to track the number of row under one order
    ObjectMapper mapper = new ObjectMapper();
    CollectedOrderList savedOrder = new CollectedOrderList();
    // Get TableLayout object in layout xml.
    TableLayout tableLayout = null;

    // Get the Intent that started this activity and extract the string
    Intent intent = null;
    String message, resume;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_orders);
        tableLayout = (TableLayout)findViewById(R.id.table_layout_table);
        Button saveOrderButton = findViewById(R.id.saveOrderButton);
        Button uploadOrderButton = findViewById(R.id.uploadOrderButton);

        size = 0;
        intent = getIntent();
        message = intent.getStringExtra(EnterOrderNumberActivity.EXTRA_MESSAGE_THREE);
        resume = intent.getStringExtra(EnterOrderNumberActivity.EXTRA_MESSAGE_TWO);
        context = getApplicationContext();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        // Capture the layout's TextView and set the string as its text
        TextView orderNumber = findViewById(R.id.OrderNumberView);
        orderNumber.setText("Order Number: " + message);

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
                        savedOrder.results.get(i).QTYCollected = Integer.parseInt(qtyCollectedValue);
                    }

                }
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

    }

    public void writeFileExternalStorage(CollectedOrderList collectedOrderList) {

        //Text of the Document
        String DirName = "orderInfo.txt";

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
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GramDispatch", "orderInfo.txt");
        try {
            collectedOrderList = mapper.readValue(file, CollectedOrderList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            tableRow.addView(description, 2, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));

            // Add a TextView in the fourth column.
            TextView qtyReqd = new TextView(context);
            qtyReqd.setText(String.valueOf(collectedOrder.ORD_QUANT));
            tableRow.addView(qtyReqd, 3, layoutParams);

            // Add a EditText in the fifth column.
            EditText qtyCollected = new EditText(context);
            if (collectedOrder.QTYCollected != null)
            {
                qtyCollected.setText(String.valueOf(collectedOrder.QTYCollected));
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
        Call<JobOrderList> call = apiInterface.doGetJobOrderList(message);
        call.enqueue(new Callback<JobOrderList>()
        {
            @Override
            public void onResponse (Call < JobOrderList > call, Response< JobOrderList > response){

                Log.d("TAG", response.code() + ""); // success code 200

                JobOrderList resource = response.body();
                List<JobOrderList.JobOrder> dataList = resource.data;

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
                    tableRow.addView(description, 2, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
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

                    size++;
                    savedOrder.results.add(orderItem);
                    tableLayout.addView(tableRow);
                }
            }
            @Override
            public void onFailure (Call < JobOrderList > call, Throwable t){
                call.cancel();
            }
        });
    }
}
