package au.com.gramline.gramdispatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;

import au.com.gramline.gramdispatch.pojo.CollectedOrderList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeightMeasuringActivity extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    private Context context = null;
    APIInterface apiInterface;
    ObjectMapper mapper = new ObjectMapper();
    CollectedOrderList savedOrder = new CollectedOrderList();
    // Get TableLayout object in layout xml.
    TableLayout tableLayout = null;

    // Get the Intent that started this activity and extract the string
    Intent intent = null;
    String orderNumber;
    Character bundleName = 'A';
    Integer bundleCount = 0;
    int size; // used to track the number of row under one order

    ArrayList<TableRow> rows = new ArrayList<>(); // all table rows

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_measuring);

        tableLayout = (TableLayout)findViewById(R.id.weight_table_layout_table);
        Button saveOrderButton = findViewById(R.id.saveOrderButton);
        Button uploadOrderButton = findViewById(R.id.uploadOrderButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button addBundleButton = findViewById(R.id.addBundleButton);
        final Button deleteBundleButton = findViewById(R.id.deleteBundleButton);

        intent = getIntent();

        context = getApplicationContext();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        // Capture the layout's TextView and set the string as its text
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        orderNumber = prefs.getString("orderNumber", null);
        TextView orderNumberView = findViewById(R.id.OrderNumberView);
        orderNumberView.setText(getString(R.string.order_number_label, orderNumber));

        savedOrder = getOrderFromFile(savedOrder);

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
                    EditText qtyPacked = tableLayout.findViewWithTag("qtyPacked_" + i);
                    if (qtyPacked.length() != 0)
                    {
                        String qtyPackedValue = qtyPacked.getText().toString();
                        savedOrder.results.get(i).QtyPacked = Integer.parseInt(qtyPackedValue);
                    }
                    EditText bundle = tableLayout.findViewWithTag("bundle_" + i);
                    if (bundle.length() != 0)
                    {
                        String bundleValue = bundle.getText().toString();
                        savedOrder.results.get(i).Bundle = bundleValue;
                    }
                }
                for (Integer i = 0; i < bundleCount; i++)
                {
                    EditText bundle = tableLayout.findViewWithTag("bundleWeight_" + i);
                    if (bundle.length() != 0)
                    {
                        String bundleValue = bundle.getText().toString();
                        savedOrder.bundles.get(i).weight = Integer.parseInt(bundleValue);
                        savedOrder.bundles.get(i).bundle_name = (--bundleName).toString();
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
                    EditText qtyPacked = tableLayout.findViewWithTag("qtyPacked_" + i);
                    if (qtyPacked.length() != 0)
                    {
                        String qtyPackedValue = qtyPacked.getText().toString();
                        savedOrder.results.get(i).QtyPacked = Integer.parseInt(qtyPackedValue);
                    }
                    EditText bundle = tableLayout.findViewWithTag("bundle_" + i);
                    if (bundle.length() != 0)
                    {
                        String bundleValue = bundle.getText().toString();
                        savedOrder.results.get(i).Bundle = bundleValue;
                    }
                }
                for (Integer i = 0; i < bundleCount; i++)
                {
                    EditText bundle = tableLayout.findViewWithTag("bundleWeight_" + i);
                    if (bundle.length() != 0)
                    {
                        String bundleValue = bundle.getText().toString();
                        savedOrder.bundles.get(i).weight = Integer.parseInt(bundleValue);
                        savedOrder.bundles.get(i).bundle_name = (--bundleName).toString();
                    }
                }
                savedOrder.stage = 4;
                Toast.makeText(getApplicationContext(), "Data Saved \n", Toast.LENGTH_SHORT).show();
                writeFileExternalStorage(savedOrder);
                Intent intent = new Intent(context, LoadActivity.class);
                startActivity(intent);

            }
        });

        /* When Add Bundle button is clicked. */
        addBundleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CollectedOrderList.Bundle bundle = new CollectedOrderList.Bundle();
                savedOrder.bundles.add(bundle);
                TableRow tableRow = new TableRow(context);
                rows.add(tableRow);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
                TextView tableName = new TextView(context);
                tableName.setTag("bundle_" + bundleName);
                tableName.setText("Bundle " + bundleName);
                bundleName++;

                EditText bundleWeight = new EditText(context);
                bundleWeight.setTag("bundleWeight_" + bundleCount);
                bundleWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
                bundleWeight.setHint("Weight");
                bundleCount++;

                tableRow.addView(tableName,layoutParams);
                tableRow.addView(bundleWeight,layoutParams);
                tableLayout.addView(tableRow);
                if (bundleCount > 0) deleteBundleButton.setEnabled(true);
            }
        });

        /* When DELETE Bundle button is clicked. */
        deleteBundleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedOrder.bundles.remove(bundleCount - 1);
                tableLayout.removeView(rows.get(savedOrder.results.size() + bundleCount - 1));
                rows.remove(savedOrder.results.size() + bundleCount - 1);
                bundleCount--;
                bundleName--;
                if (bundleCount == 0) deleteBundleButton.setEnabled(false);
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
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String orderNumber = prefs.getString("orderNumber", null);
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
            rows.add(tableRow);
            // Set new table row layout parameters.
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);

            // Add a TextView in the first column.
            TextView seqNo = new TextView(context);
            seqNo.setText(String.valueOf(collectedOrder.SEQNO));
            tableRow.addView(seqNo, 0, layoutParams);

            // Add a TextView in the second column.
            TextView stockCode = new TextView(context);
            stockCode.setText(String.valueOf(collectedOrder.STOCKCODE));
            tableRow.addView(stockCode, 1, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));

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
            if (collectedOrder.QtyCollected != null) {
                qtyCollected.setText(String.valueOf(collectedOrder.QtyCollected));
            }
            tableRow.addView(qtyCollected, 4, layoutParams);
            qtyCollected.setTag("qtyCollected_" + size);
            qtyCollected.setInputType(InputType.TYPE_CLASS_NUMBER);

            // Add a TextView in the sixth column.
            EditText qtyPacked = new EditText(context);
            if (collectedOrder.QtyPacked != null) {
                qtyPacked.setText(String.valueOf(collectedOrder.QtyPacked));
            }
            tableRow.addView(qtyPacked, 5, layoutParams);
            qtyPacked.setTag("qtyPacked_" + size);
            qtyPacked.setInputType(InputType.TYPE_CLASS_NUMBER);

            // Add a TextView in the seventh column.
            EditText bundle = new EditText(context);
            if (collectedOrder.Bundle != null) {
                bundle.setText(String.valueOf(collectedOrder.Bundle));
            }
            tableRow.addView(bundle, 6, layoutParams);
            bundle.setTag("bundle_" + size);
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(1);
            bundle.setFilters(FilterArray);
            bundle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            size++;
            tableLayout.addView(tableRow);
        }

        for (CollectedOrderList.Bundle bundle : collectedOrderList.bundles)
        {
            TableRow tableRow = new TableRow(context);
            rows.add(tableRow);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
            TextView tableName = new TextView(context);
            tableName.setTag("bundle_" + bundle.bundle_name);
            tableName.setText("Bundle " + bundle.bundle_name);
            bundleName++;

            EditText bundleWeight = new EditText(context);
            bundleWeight.setTag("bundleWeight_" + bundleCount);
            bundleWeight.setText(bundle.weight.toString());
            bundleWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
            bundleCount++;

            tableRow.addView(tableName,layoutParams);
            tableRow.addView(bundleWeight,layoutParams);
            tableLayout.addView(tableRow);
        }
        return collectedOrderList;
    }
}
