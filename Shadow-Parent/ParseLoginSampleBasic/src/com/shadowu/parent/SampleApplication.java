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

package com.shadowu.parent;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.BatteryManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ui.ParseLoginConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import com.parse.ParseTwitterUtils;

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
    super.onCreate();
    // Required - Initialize the Parse SDK
    Parse.initialize(this);
    ParseInstallation.getCurrentInstallation().saveInBackground();
    Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
    ParseFacebookUtils.initialize(this);
    // Optional - If you don't want to allow Twitter login, you can
    // remove this line (and other related ParseTwitterUtils calls)
//    ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
//        getString(R.string.twitter_consumer_secret));

//      try {
//          PackageInfo info = getPackageManager().getPackageInfo("com.parse.loginsample.basic", PackageManager.GET_SIGNATURES);
//          for (Signature signature : info.signatures) {
//              MessageDigest md = MessageDigest.getInstance("SHA");
//              md.update(signature.toByteArray());
//              Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//          }
//      } catch (PackageManager.NameNotFoundException e) {
//          e.printStackTrace();
//      } catch (NoSuchAlgorithmException e) {
//          e.printStackTrace();
//      }
  }

}
