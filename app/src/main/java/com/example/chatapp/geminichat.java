package com.example.chatapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
public class geminichat extends AppCompatActivity {


    private TextView textView;
    private EditText editText;
    //given id is editTextText

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geminichat);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        textView = findViewById(R.id.textViewg);
        editText = findViewById(R.id.editTextText);
    }

    public void buttonCallGeminiAPI(View view){

        String userPrompt = editText.getText().toString();
        // For text-only input, use the gemini-pro model
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-pro",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ "AIzaSyAgnJrSQ_X97FyEg40c6S9ccHkrzL9fLZo");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(userPrompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                textView.setText(resultText);
            }
            @Override
            public void onFailure(Throwable t) {
                textView.setText(t.toString());
            }


        }, ContextCompat.getMainExecutor(this));
    }
}