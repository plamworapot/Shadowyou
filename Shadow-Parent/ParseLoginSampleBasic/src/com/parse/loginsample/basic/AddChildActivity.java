package com.parse.loginsample.basic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddChildActivity extends FragmentActivity {
    ArrayList<ChildList> array_child_list = new ArrayList<ChildList>();
    ListView childlist ;
    ArrayList<String> child_id = new ArrayList<String>();
    ArrayList<String> child_name = new ArrayList<String>();
    Gson g = new Gson();
    SimpleAdapter adapter;
    List<Map<String, String>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child);
        childlist = (ListView)findViewById(R.id.child_list);
        childlist.setVisibility(View.INVISIBLE);
        loadchild();

    }
    public void updatelist(){
        data = new ArrayList<Map<String, String>>();
        for (ChildList item : array_child_list ) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("name", item.name);
            datum.put("id", item.deviceId);
            data.add(datum);
        }
         adapter = new SimpleAdapter(AddChildActivity.this, data,
                android.R.layout.activity_list_item,
                new String[] {"name", "id"},
                new int[] {android.R.id.text1,
                        android.R.id.text2});


        // Assign adapter to ListView
        childlist.setAdapter(adapter);

        // ListView Item Click Listener
        childlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder adb=new AlertDialog.Builder(AddChildActivity.this);
                adb.setTitle("Delete?");
                Log.i("",""+array_child_list.get(position)+"><position:"+position);
                adb.setMessage("Are you sure you want to delete " + array_child_list.get(position).name);
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Parent_relation_child");
                        query.getInBackground(array_child_list.get(positionToRemove).object_id,new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject row, ParseException e) {
                                row.deleteInBackground(new DeleteCallback(){
                                    @Override
                                    public void done(ParseException e) {
                                        loadchild();
                                        childlist.invalidateViews();
                                    }
                                });
                            }
                        });


                    }});
                adb.show();
            }

        });

    }

    public void loadchild(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Parent_relation_child");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> rows, ParseException e) {
                if (e == null && rows.size()>0) {
                    childlist.setVisibility(View.VISIBLE);
                    array_child_list.clear();

                    for(ParseObject row:rows){
                        ChildList temp = new ChildList(row.getString("name"),row.getString("device"),row.getObjectId());
                        array_child_list.add(temp);
                        updatelist();

                    }
                }else{
                    childlist.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    public void savetoparse(){
        for(final ChildList item:array_child_list){
            if(item.object_id == null){
                Log.i("","Save "+item.deviceId);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Device");
                query.getInBackground(item.deviceId,new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject device, ParseException e) {
                        if(device != null){
                            ParseObject object = new ParseObject("Parent_relation_child");
                            object.put("device", device);
                            object.put("user",  ParseUser.getCurrentUser());
                            object.put("name", item.name);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e != null && e.getMessage().equals("duplicate") ){
                                        Toast.makeText(getApplicationContext(), "Duplicate deviceId in account",
                                                Toast.LENGTH_LONG).show();
                                        Log.i("","Duplicate");
                                    }else{
                                        loadchild();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(), "No deviceId has",
                                    Toast.LENGTH_LONG).show();
                            Log.i("","No deviceId has");
                        }
                    }
                });
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_child, menu);
        MenuItem item = menu.findItem(R.id.action_add_child);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_child) {
            Log.i("","Hello im in");
            AlertDialog.Builder builder = new AlertDialog.Builder(AddChildActivity.this);
            Context context = AddChildActivity.this;

            builder.setTitle("Add Child");
            builder.setMessage("Insert Child Name and Child Device ID");


            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText textName = new EditText(context);
            textName.setHint("Insert Child Name");
            layout.addView(textName);

            final EditText textID = new EditText(context);
            textID.setHint("Child Device ID Must have 10 characters");
            layout.addView(textID);

            builder.setView(layout);
            builder.setPositiveButton("Save",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
//                            childlist.setVisibility(View.VISIBLE);
                            String child_id = textID.getText().toString();
                            String child_ch_name = textName.getText().toString();
                            Log.i("", "child_deviceId :" + child_id);
//                                child_id.add(child_deviceid);
//                                child_name.add(child_ch_name);
                            array_child_list.add(new ChildList(child_ch_name,child_id));
                            savetoparse();

                        }
                    });
            builder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // DO TASK

                        }
                    });


            final AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

            textID.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Check if edit text is empty
                    if (s.length()!=10) {
                        // Disable ok button
                        (dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    } else {
                        // Something into edit text. Enable the button.
                        (dialog).getButton(
                                AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }

                }
            });
        }

        return super.onOptionsItemSelected(item);
    }
}
