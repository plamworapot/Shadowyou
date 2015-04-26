package com.shadowu.parent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by plamworapot on 27/4/2558.
 */

public class QRReader extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        String deviceId = rawResult.getText();

        AlertDialog.Builder builder = new AlertDialog.Builder(QRReader.this);
        Context context = QRReader.this;
        builder.setTitle("Add Child");
        builder.setMessage("Insert Child Name and Child Device ID");


        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText textName = new EditText(context);
        textName.setHint("Insert Child Name");
        layout.addView(textName);

        final EditText textID = new EditText(context);
        textID.setText(deviceId);
        textID.setEnabled(false);
        textID.setHint("Child Device ID Must have 10 characters");
        layout.addView(textID);

        builder.setView(layout);
        builder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        String child_id = textID.getText().toString();
                        final String child_ch_name = textName.getText().toString();
                        Log.i("deviceId", child_id); // Prints scan results
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Device");
                        query.getInBackground(child_id, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject device, ParseException e) {
                                if (device != null) {
                                    ParseObject object = new ParseObject("Parent_relation_child");
                                    object.put("device", device);
                                    object.put("user", ParseUser.getCurrentUser());
                                    object.put("name", child_ch_name);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null && e.getMessage().equals("duplicate")) {
                                                Toast.makeText(getApplicationContext(), "Duplicate deviceId in account",
                                                        Toast.LENGTH_LONG).show();
                                                Log.i("", "Duplicate");
                                            }
                                            finish();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "No deviceId has",
                                            Toast.LENGTH_LONG).show();
                                    Log.i("", "No deviceId has");
                                    finish();
                                }

                            }
                        });

                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // DO TASK
                        finish();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        textName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if edit text is empty
                if (s.length() > 1) {
                    // Disable ok button
                    (dialog).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    // Something into edit text. Enable the button.
                    (dialog).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }

            }
        });


    }

    public static Bitmap toBitmap(BitMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

}
