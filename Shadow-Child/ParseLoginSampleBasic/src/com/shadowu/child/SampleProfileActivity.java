/*
 *  Copyright (c) 2014, Parse, LLC. All rights reserved.
 *
 *  You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 *  copy, modify, and distribute this software in source code or binary form for use
 *  in connection with the web services and APIs provided by Parse.
 *
 *  As with any software that integrates with the Parse platform, your use of
 *  this software is subject to the Parse Terms of Service
 *  [https://www.parse.com/about/terms]. This copyright notice shall be
 *  included in all copies or substantial portions of the software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.shadowu.child;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ui.ParseLoginBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Shows the user profile. This simple activity can function regardless of whether the user
 * is currently logged in.
 */
public class SampleProfileActivity extends Activity {
  private static final int LOGIN_REQUEST = 0;

  private TextView titleTextView;
  private TextView emailTextView;
  private TextView nameTextView;
  private ImageView qrView;
  private ParseUser currentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_profile);
    titleTextView = (TextView) findViewById(R.id.profile_title);
    emailTextView = (TextView) findViewById(R.id.profile_email);
    nameTextView = (TextView) findViewById(R.id.profile_name);
    titleTextView.setText(R.string.profile_title_logged_in);
    qrView = (ImageView)findViewById(R.id.imageViewQR);

  }

  @Override
  protected void onStart() {
    super.onStart();
    currentUser = ParseUser.getCurrentUser();
//    if (currentUser != null) {
//        String android_id = Settings.Secure.getString(this.getContentResolver(),
//                Settings.Secure.ANDROID_ID);
//
//        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//        installation.put("user",ParseUser.getCurrentUser());
//        installation.saveInBackground();
//        Intent intent = new Intent(this, MapsActivity.class);
//        startActivity(intent);
//        finish();
        showProfileLoggedIn();
//    } else {
//        showProfileLoggedOut();
//    }
  }


  /**
   * Shows the profile of the given user.
   */
  private void showProfileLoggedIn() {
    titleTextView.setText("Your deviceId");
    emailTextView.setText("");
    nameTextView.setText("Loading...");
    ParseQuery<ParseObject> query = ParseQuery.getQuery("Device");
    query.whereEqualTo("installation", ParseInstallation.getCurrentInstallation());

    query.getFirstInBackground(new GetCallback<ParseObject>() {
        public void done(ParseObject row, ParseException e) {
            if(row != null){
                nameTextView.setText(row.getObjectId());
                com.google.zxing.Writer writer = new QRCodeWriter();
                try{
                    BitMatrix bm = writer.encode(row.getObjectId()+"", BarcodeFormat.QR_CODE, 300, 300);
                    Bitmap bitmap = toBitmap(bm);
                    if(bitmap != null) {
                        qrView.setImageBitmap(bitmap);
                    }
                }catch (WriterException error){
                }
            }else{
                nameTextView.setText("Plase restart app");
            }
        }
    });

  }

  /**
   * Show a message asking the user to log in, toggle login/logout button text.
   */
  private void showProfileLoggedOut() {
    titleTextView.setText(R.string.profile_title_logged_out);
    emailTextView.setText("");
    nameTextView.setText("");
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
