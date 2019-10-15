package com.example.manpreet.dragit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout userNameTextInputLayout, phoneTextInputLayout;
    EditText userName, phone;
    Button play;

    String joshPlayerInfo = "Josh Players Info";
    String joshPlayerInfoFileString = "players_info.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        userName = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        userNameTextInputLayout = (TextInputLayout) findViewById(R.id.username_text_input_layout);
        phoneTextInputLayout = (TextInputLayout) findViewById(R.id.phone_text_input_layout);
        play = (Button) findViewById(R.id.start);
        play.setOnClickListener(this);
        isStoragePermissionGranted();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            isStoragePermissionGranted();
        }
    }

    private boolean checkIfPhoneNoExist(String phone) {
        File file = createDirectoryAndReturnFileIfDoesNotExist();
        final Scanner scanner;
        try {
            scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                final String lineFromFile = scanner.nextLine();
                if(lineFromFile.contains(phone)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private File createDirectoryAndReturnFileIfDoesNotExist(){
        File internalStorageFile = Environment.getExternalStorageDirectory();
        File josh = new File(internalStorageFile, joshPlayerInfo);
        if (!josh.exists()) {
            josh.mkdir();
        }

        File joshPlayerInfoFile = new File(josh, joshPlayerInfoFileString);
        if (!joshPlayerInfoFile.exists()) {
            try {
                joshPlayerInfoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return joshPlayerInfoFile;
    }

    private void saveData() {
        String nameString = userName.getText().toString();
        String phoneString = phone.getText().toString();
        File joshPlayerInfoFile = createDirectoryAndReturnFileIfDoesNotExist();
        if(!checkIfPhoneNoExist(phoneString)){
            try {
                FileOutputStream fileOutput = new FileOutputStream(joshPlayerInfoFile, true);
                fileOutput.write(("Name - " + nameString + " Phone - " + phoneString).getBytes("UTF-8"));
                fileOutput.write(("\n").getBytes("UTF-8"));
                fileOutput.flush();
                fileOutput.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateDetails() {
        boolean isAllValid = true;
        userNameTextInputLayout.setErrorEnabled(false);
        phoneTextInputLayout.setErrorEnabled(false);

        String nameString = userName.getText().toString().trim();
        String phoneString = phone.getText().toString().trim();

        if (nameString != null && !nameString.isEmpty()) {
            boolean valid = nameString.matches("^[a-zA-Z\\s]*$");
            if (!valid) {
                isAllValid = false;
                userNameTextInputLayout.setError("Please enter valid username");
            }
        } else {
            isAllValid = false;
            userNameTextInputLayout.setError("Please add your username");
        }
        if (phoneString != null && !phoneString.isEmpty()) {
            boolean valid = phoneString.matches("[0-9]+") && phoneString.length() == 10 && phoneString.charAt(0) >= '6';
            if (!valid) {
                isAllValid = false;
                phoneTextInputLayout.setError("Please enter valid phone no.");
            }
        } else {
            isAllValid = false;
            phoneTextInputLayout.setError("Please add your phone no.");
        }
        return isAllValid;
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (validateDetails()) {
            saveData();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
