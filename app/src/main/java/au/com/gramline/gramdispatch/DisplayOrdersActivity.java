package au.com.gramline.gramdispatch;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DisplayOrdersActivity extends AppCompatActivity {

    private Context context = null;
    TextView responseText;
    APIInterface apiInterface;
    int size = 0; // used to track the number of row under one order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_orders);

        // Get TableLayout object in layout xml.
        final TableLayout tableLayout = (TableLayout)findViewById(R.id.table_layout_table);
        Button saveOrderButton = findViewById(R.id.saveOrderButton);
        Button uploadOrderButton = findViewById(R.id.uploadOrderButton);

        context = getApplicationContext();

        responseText = (TextView) findViewById(R.id.responseText);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView orderNumber = findViewById(R.id.OrderNumberView);
        orderNumber.setText("Order Number: " + message);

        final CollectedOrderList savedOrder = new CollectedOrderList();

        /**
         * GET List Resources
         **/
        Call<JobOrderList> call = apiInterface.doGetJobOrderList(message);
        call.enqueue(new Callback<JobOrderList>()
        {
            @Override
            public void onResponse (Call < JobOrderList > call, Response< JobOrderList > response){

                Log.d("TAG", response.code() + "");

                JobOrderList resource = response.body();
                List<JobOrderList.JobOrder> dataList = resource.data;

                for (JobOrderList.JobOrder joborder : dataList) {
                    Toast.makeText(getApplicationContext(), joborder.SEQNO + " " + joborder.DESCRIPTION + " " + joborder.QTYREQD + "\n", Toast.LENGTH_SHORT).show();
                }

                for (JobOrderList.JobOrder joborder : dataList) {
                    // Create a new table row.
                    TableRow tableRow = new TableRow(context);
                    CollectedOrderList.CollectedOrder orderItem = new CollectedOrderList.CollectedOrder(0,0,"",0,0,"");
                    // Set new table row layout parameters.
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);

                    // Add a TextView in the first column.
                    TextView seqNo = new TextView(context);
                    seqNo.setText(String.valueOf(joborder.SEQNO));
                    tableRow.addView(seqNo, 0,layoutParams);
                    orderItem.SEQNO = joborder.SEQNO;

                    // Add a TextView in the second column.
                    TextView description = new TextView(context);
                    description.setText(joborder.DESCRIPTION);
                    tableRow.addView(description, 1, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));
                    orderItem.DESCRIPTION = joborder.DESCRIPTION;

                    // Add a TextView in the third column.
                    TextView qtyReqd = new TextView(context);
                    qtyReqd.setText(String.valueOf(joborder.QTYREQD));
                    tableRow.addView(qtyReqd, 2, layoutParams);
                    orderItem.QTYREQD = joborder.QTYREQD;

                    // Add a EditText in the fourth column.
                    EditText qtyCollected = new EditText(context);
                    tableRow.addView(qtyCollected, 3, layoutParams);
                    qtyCollected.setTag("qtyCollected_" + size);

                    // Add a EditText in the fifth column.
                    EditText bundle = new EditText(context);
                    tableRow.addView(bundle, 4, layoutParams);
                    bundle.setTag("bundle_" + size);

                    size++;
                    savedOrder.data.add(orderItem);
                    tableLayout.addView(tableRow);
                }
            }
            @Override
            public void onFailure (Call < JobOrderList > call, Throwable t){
                call.cancel();
            }
        });


        /* When save order button clicked. */
        saveOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText qtyCollected = tableLayout.findViewWithTag("qtyCollected_" + 0);
                String qtyCollectedValue = qtyCollected.getText().toString();
                savedOrder.data.get(0).QTYCollected = Integer.parseInt(qtyCollectedValue);
                EditText bundle = tableLayout.findViewWithTag("bundle_" + 0);
                String bundleValue = bundle.getText().toString();
                savedOrder.data.get(0).Bundle = bundleValue;
                TableRow tableRow = new TableRow(context);
                TextView test = new TextView(context);
                test.setText(String.valueOf("Quantity collected: " + savedOrder.data.get(0).QTYCollected));
                tableRow.addView(test, 0);
                tableLayout.addView(tableRow);

            }

        });
        /* When upload order button is clicked. */
        uploadOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableRow tableRow = new TableRow(context);
                TextView test = new TextView(context);
                test.setText(String.valueOf("Sequence Number:" + savedOrder.data.get(0).SEQNO +
                                "Description: " + savedOrder.data.get(0).DESCRIPTION +
                                "Quantity required: " + savedOrder.data.get(0).QTYREQD +
                                "Quantity collected: " + savedOrder.data.get(0).QTYCollected +
                                "Bundle: " + savedOrder.data.get(0).Bundle
                        ));
                tableRow.addView(test, 0);
                tableLayout.addView(tableRow);

                /**
                 Create new user
                 **/
//                try {
//                    JSONObject paramObject = new JSONObject();
//                    paramObject.put("email", "sample@gmail.com");
//                    paramObject.put("pass", "4384984938943");
//
//                    Call<CollectedOrderList> userCall = apiInterface.createCollectedOrderList(paramObject.toString());
//                    userCall.enqueue(new Callback<CollectedOrderList>() {
//                        @Override
//                        public void onResponse(Call<CollectedOrderList> call, Response<CollectedOrderList> response) {
//                            String returnString = "Failed!";
//                            if (response.body() != null)
//                            {
//                                returnString = response.body().toString();
//                            }
//
//                            Toast.makeText(getApplicationContext(), returnString, Toast.LENGTH_SHORT).show();
//                        }
//                        @Override
//                        public void onFailure(Call<CollectedOrderList> call, Throwable t) {
//                            call.cancel();
//                        }
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                Call<CollectedOrderList> call1 = apiInterface.createCollectedOrderList(savedOrder);
                call1.enqueue(new Callback<CollectedOrderList>() {
                    @Override
                    public void onResponse(Call<CollectedOrderList> call, Response<CollectedOrderList> response) {
                        String returnString = "Failed!";
                        if (response.body() != null)
                        {
                            returnString = response.body().toString();
                        }

                        Toast.makeText(getApplicationContext(), returnString, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(Call<CollectedOrderList> call, Throwable t) {
                        call.cancel();
                    }
                });
            }
        });
    }
}
