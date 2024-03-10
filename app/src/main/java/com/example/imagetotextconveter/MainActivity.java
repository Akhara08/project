package com.example.imagetotextconveter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.github.dhaval2404.imagepicker.ImagePickerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView clear,getImage,copy;
    EditText recgText;
    android.net.Uri imageUri;
    TextRecognizer textRecognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clear=findViewById(R.id.clear);
        getImage=findViewById(R.id.getimage);
        copy=findViewById(R.id.copy);
        recgText=findViewById(R.id.recgText);
        textRecognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080,1080)
                        .start();

            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=recgText.getText().toString();
                if (text.isEmpty()){
                    Toast.makeText(MainActivity.this,"There is no text to copy",Toast.LENGTH_SHORT).show();
                }else {

                    ClipboardManager clipboardManager=(ClipboardManager) getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("Data ",recgText.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "Text copy to Clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=recgText.getText().toString();
                if(text.isEmpty()){
                    Toast.makeText(MainActivity.this, "There is no text to clear", Toast.LENGTH_SHORT).show();
                }
                else{
                    recgText.setText("");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
                recognizetext();
            }
        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizetext() {
        if (imageUri!=null){
            try {
                InputImage inputImage=InputImage.fromFilePath(MainActivity.this,imageUri);
                com.google.android.gms.tasks.Task<com.google.mlkit.vision.text.Text> result=textRecognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text text) {
                                String recognizeText= text.getText();
                                recgText.setText(recognizeText);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}