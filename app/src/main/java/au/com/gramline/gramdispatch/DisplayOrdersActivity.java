package au.com.gramline.gramdispatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class DisplayOrdersActivity extends AppCompatActivity {

    private Context context = null;
    TextView responseText;
    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_orders);
        // Get TableLayout object in layout xml.
        final TableLayout tableLayout = (TableLayout)findViewById(R.id.table_layout_table);

        context = getApplicationContext();

        responseText = (TextView) findViewById(R.id.responseText);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView orderNumber = findViewById(R.id.OrderNumberView);
        orderNumber.setText("Order Number: " + message);

        /**
         * GET List Resources
         **/
        Call<JobOrderList> call = apiInterface.doGetJobOrderList(message);
        call.enqueue(new Callback<JobOrderList>()
        {
            @Override
            public void onResponse (Call < JobOrderList > call, Response< JobOrderList > response){

                Log.d("TAG", response.code() + "");

                String displayResponse = "";

                JobOrderList resource = response.body();
                List<JobOrderList.JobOrder> dataList = resource.data;

                for (JobOrderList.JobOrder joborder : dataList) {
                    Toast.makeText(getApplicationContext(), joborder.SEQNO + " " + joborder.DESCRIPTION + " " + joborder.QTYREQD + "\n", Toast.LENGTH_SHORT).show();
                }

                for (JobOrderList.JobOrder joborder : dataList) {
                    // Create a new table row.
                    TableRow tableRow = new TableRow(context);
                    // Set new table row layout parameters.
                    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);

                    displayResponse += joborder.SEQNO + " " + joborder.DESCRIPTION + " " + joborder.QTYREQD + "\n";

                    // Add a TextView in the first column.
                    TextView seqNo = new TextView(context);
                    seqNo.setText(String.valueOf(joborder.SEQNO));
                    tableRow.addView(seqNo, 0,layoutParams);

                    // Add a TextView in the second column.
                    TextView description = new TextView(context);
                    description.setText(joborder.DESCRIPTION);
                    tableRow.addView(description, 1, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4));

                    // Add a TextView in the third column.
                    TextView qtyReqd = new TextView(context);
                    qtyReqd.setText(String.valueOf(joborder.QTYREQD));
                    tableRow.addView(qtyReqd, 2, layoutParams);

                    // Add a EditText in the fourth column.
                    EditText qtyCollected = new EditText(context);
                    tableRow.addView(qtyCollected, 3, layoutParams);

                    // Add a EditText in the fifth column.
                    EditText bundle = new EditText(context);
                    tableRow.addView(bundle, 4, layoutParams);

                    tableLayout.addView(tableRow);
                }
            }

            @Override
            public void onFailure (Call < JobOrderList > call, Throwable t){
                call.cancel();
            }
        });

//        // Get add table row button.
//        Button addRowButton = (Button)findViewById(R.id.table_layout_add_row_button);
//        addRowButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Create a new table row.
//                TableRow tableRow = new TableRow(context);
//
//                // Set new table row layout parameters.
//                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
//                tableRow.setLayoutParams(layoutParams);
//
//                // Add a TextView in the first column.
//                TextView textView = new TextView(context);
//                textView.setText("This is the ");
//                tableRow.addView(textView, 0);
//
//                // Add a button in the second column
//                Button button = new Button(context);
//                button.setText("New Row");
//                tableRow.addView(button, 1);
//
//                // Add a checkbox in the third column.
//                CheckBox checkBox = new CheckBox(context);
//                checkBox.setText("Check it");
//                tableRow.addView(checkBox, 2);
//
//                tableLayout.addView(tableRow);
//            }
//        });
//
//
//        // Get delete table row button.
//        Button deleteRowButton = (Button)findViewById(R.id.table_layout_delete_row_button);
//        deleteRowButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Get table row count.
//                int rowCount = tableLayout.getChildCount();
//
//                // Save delete row number list.
//                List<Integer> deleteRowNumberList = new ArrayList<Integer>();
//
//                // Loop each table rows.
//                for(int i =0 ;i < rowCount;i++)
//                {
//                    // Get table row.
//                    View rowView = tableLayout.getChildAt(i);
//
//                    if(rowView instanceof TableRow)
//                    {
//                        TableRow tableRow = (TableRow)rowView;
//
//                        // Get row column count.
//                        int columnCount = tableRow.getChildCount();
//
//                        // Loop all columns in row.
//                        for(int j = 0;j<columnCount;j++)
//                        {
//                            View columnView = tableRow.getChildAt(j);
//                            if(columnView instanceof CheckBox)
//                            {
//                                // If columns is a checkbox and checked then save the row number in list.
//                                CheckBox checkboxView = (CheckBox)columnView;
//                                if(checkboxView.isChecked())
//                                {
//                                    deleteRowNumberList.add(i);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // Remove all rows by the selected row number.
//                for(int rowNumber :deleteRowNumberList)
//                {
//                    tableLayout.removeViewAt(rowNumber);
//                }
//            }
//        });
    }
}
