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

package com.parse.loginsample.basic;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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
  private Button loginOrLogoutButton;

  private ParseUser currentUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_profile);
    titleTextView = (TextView) findViewById(R.id.profile_title);
    emailTextView = (TextView) findViewById(R.id.profile_email);
    nameTextView = (TextView) findViewById(R.id.profile_name);
    loginOrLogoutButton = (Button) findViewById(R.id.login_or_logout_button);
    titleTextView.setText(R.string.profile_title_logged_in);

    loginOrLogoutButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (currentUser != null) {
          // User clicked to log out.
          ParseUser.logOut();
          currentUser = null;
          showProfileLoggedOut();
        } else {
          // User clicked to log in.
          ParseLoginBuilder loginBuilder = new ParseLoginBuilder(
              SampleProfileActivity.this);
          startActivityForResult(loginBuilder.build(), LOGIN_REQUEST);
        }
      }
    });
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
            nameTextView.setText(row.getObjectId());
        }
    });

    loginOrLogoutButton.setVisibility(View.INVISIBLE);
  }

  /**
   * Show a message asking the user to log in, toggle login/logout button text.
   */
  private void showProfileLoggedOut() {
    titleTextView.setText(R.string.profile_title_logged_out);
    emailTextView.setText("");
    nameTextView.setText("");
    loginOrLogoutButton.setText(R.string.profile_login_button_label);
  }
}
