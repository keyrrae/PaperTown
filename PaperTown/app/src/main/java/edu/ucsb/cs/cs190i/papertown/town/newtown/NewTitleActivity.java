

/*
 *  Copyright (c) 2017 - present, Zhenyu Yang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 */

package edu.ucsb.cs.cs190i.papertown.town.newtown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import edu.ucsb.cs.cs190i.papertown.R;
import edu.ucsb.cs.cs190i.papertown.models.Town;
import edu.ucsb.cs.cs190i.papertown.models.TownManager;

public class NewTitleActivity extends AppCompatActivity {
    private Town passedInTown;
    final int NEW_TITLE_REQUEST = 0;
    final int NEW_ADDRESS_REQUEST = 1;
    final int NEW_CATEGORY_REQUEST = 2;
    final int NEW_DESCRIPTION_REQUEST = 3;
    final int NEW_INFORMATION_REQUEST = 4;
    final int NEW_PHOTO_REQUEST = 5;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_to_next, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_title);

        final EditText ed = (EditText) findViewById(R.id.editText_newtitle);
        //passedInTown = (Town) getIntent().getSerializableExtra("townPassIn");
        passedInTown = TownManager.getInstance().getNewTown();


        String dataPassIn = passedInTown.getTitle();
        Log.i("ed","dataPassIn = "+dataPassIn);
        if(!dataPassIn.isEmpty()&&dataPassIn!=null){
            Log.i("ed","dataPassIn2 = "+dataPassIn);
            ((EditText)findViewById(R.id.editText_newtitle)).setText(dataPassIn);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_newTown_new_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.save_and_exit:
//                        Intent returnIntent = new Intent();
//                        passedInTown.setTitle(ed.getText().toString());
//                        returnIntent.putExtra("result",passedInTown);
//                        Log.i("ed","text = "+ed.getText().toString());
//                        setResult(Activity.RESULT_OK,returnIntent);
//                        finish();

                        passedInTown.setTitle(ed.getText().toString());
//                        Intent intent = new Intent(getApplicationContext(), NewTownActivity.class);
//                        //intent.putExtra("townPassIn", outputTown);
//                        startActivity(intent);
                        finish();

                        break;
                }
                return true;
            }
        });


        findViewById(R.id.button_new_title_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ed","onClick");
                passedInTown.setTitle(ed.getText().toString());
                Intent intent = new Intent(getApplicationContext(), NewAddressActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // Check which request we're responding to
//        Log.i("onActivityResult", "requestCode = " + requestCode);
//        Log.i("onActivityResult", "resultCode = " + resultCode);
//        if (requestCode == NEW_TITLE_REQUEST) {
//            // Make sure the request was successful
//            if (resultCode == RESULT_OK) {
//                passedInTown = (Town) data.getSerializableExtra("result");
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result",passedInTown);
//                setResult(Activity.RESULT_OK,returnIntent);
//                finish();
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//                Log.i("onActivityResult", "NEW_TITLE_REQUEST RESULT_CANCELED");
//                //Write your code if there's no result
//            }
//        }
//    }



    }
