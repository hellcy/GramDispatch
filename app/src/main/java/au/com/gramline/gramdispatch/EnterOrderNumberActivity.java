package au.com.gramline.gramdispatch;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.gramline.gramdispatch.pojo.CollectedOrderList;

public class EnterOrderNumberActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE_TWO = "com.example.GramDispatch.MESSAGE2";
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_order_number);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.usernameView);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String username = prefs.getString("username", null);
        textView.setText("You are logged in as: ");
        TextView username2 = findViewById(R.id.usernameView2);
        username2.setText(username);
    }

    /** Called when the user taps the get job order button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayOrdersActivity.class);
        EditText editText = (EditText) findViewById(R.id.enterOrderNumberText);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("orderNumber", editText.getText().toString());
        editor.apply();

        startActivity(intent);
    }

    /** Called when the user taps the continue order button */
    public void continueOrder(View view) {
        EditText editText = (EditText) findViewById(R.id.enterOrderNumberText);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("orderNumber", editText.getText().toString());
        editor.apply();

        String resume = "resume";

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GramDispatch", "order " + editText.getText().toString() + ".txt");
        if (!file.exists())
        {
            Toast.makeText(getApplicationContext(), "File doesn't exist! \n", Toast.LENGTH_SHORT).show();
        }
        else
        {
            CollectedOrderList collectedOrderList = new CollectedOrderList();
            try {
                collectedOrderList = mapper.readValue(file, CollectedOrderList.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (collectedOrderList.stage == 1)
            {
                Intent intent = new Intent(this, DisplayOrdersActivity.class);
                intent.putExtra(EXTRA_MESSAGE_TWO, resume);
                startActivity(intent);
            }
            else if (collectedOrderList.stage == 2)
            {
                Intent intent = new Intent(this, PackActivity.class);
                intent.putExtra(EXTRA_MESSAGE_TWO, resume);
                startActivity(intent);
            }
            else if (collectedOrderList.stage == 3)
            {
                Intent intent = new Intent(this, WeightMeasuringActivity.class);
                intent.putExtra(EXTRA_MESSAGE_TWO, resume);
                startActivity(intent);
            }
            else if (collectedOrderList.stage == 4)
            {
                Intent intent = new Intent(this, LoadActivity.class);
                intent.putExtra(EXTRA_MESSAGE_TWO, resume);
                startActivity(intent);
            }
        }

    }
}
