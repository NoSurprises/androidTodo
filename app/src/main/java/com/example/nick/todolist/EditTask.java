package com.example.nick.todolist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditTask extends AppCompatActivity {

    private static final String TAG = "daywint";

    EditText editText;
    Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        editText = ((EditText) findViewById(R.id.editNameTask));
        done = (Button) findViewById(R.id.editingDone);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Log.d(TAG, " " + editText.getText());
                intent.putExtra("name", editText.getText()); // TODO check user input
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
