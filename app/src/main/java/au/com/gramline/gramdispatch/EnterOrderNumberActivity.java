package au.com.gramline.gramdispatch;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class EnterOrderNumberActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE_THREE = "com.example.GramDispatch.MESSAGE";
    public static final String EXTRA_MESSAGE_TWO = "com.example.GramDispatch.MESSAGE2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_order_number);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.usernameView);
        textView.setText("You are logged in as: " + message);
    }

    /** Called when the user taps the get job order button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayOrdersActivity.class);
        EditText editText = (EditText) findViewById(R.id.enterOrderNumberText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE_THREE, message);
        startActivity(intent);
    }

    /** Called when the user taps the continue order button */
    public void continueOrder(View view) {
        Intent intent = new Intent(this, DisplayOrdersActivity.class);
        EditText editText = (EditText) findViewById(R.id.enterOrderNumberText);
        String message = editText.getText().toString();
        String resume = "resume";
        intent.putExtra(EXTRA_MESSAGE_THREE, message);
        intent.putExtra(EXTRA_MESSAGE_TWO, resume);
        startActivity(intent);
    }
}
