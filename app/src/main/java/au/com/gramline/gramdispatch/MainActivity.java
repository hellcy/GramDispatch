package au.com.gramline.gramdispatch;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the log in button */
    public void sendMessage(View view) {
        EditText userNameText = (EditText) findViewById(R.id.userName);
        String userName = userNameText.getText().toString();
        EditText passwordText = (EditText) findViewById(R.id.password);
        String password = passwordText.getText().toString();
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("username", userName);
        editor.putString("password", password);
        editor.apply();
        if (userName.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please enter user name. \n", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent = new Intent(this, EnterOrderNumberActivity.class);
            startActivity(intent);
        }
    }
}
