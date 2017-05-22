/*
 *  Copyright (c) 2017 - present, Zhenyu Yang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 */

package edu.ucsb.cs.cs190i.papertown.town.newtown;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.ucsb.cs.cs190i.papertown.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import edu.ucsb.cs.cs190i.papertown.R;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SelectImageActivity extends AppCompatActivity
{
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 112;
    final int PICK_PHOTO_REQUEST = 5;
    Uri[] imageUris;
    final int NEW_PHOTO_REQUEST = 5;
    GridView grid;
    String[] web = {
            "Google"
    } ;

    //TODO
    //Need to fix the gridView

    int[] imageId = {
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test,
            R.drawable.test

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_newTown_selectimg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbar.setNavigationIcon(R.drawable.ic_check_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                Log.i("dataToD", "setNavigationOnClickListener");
                Intent returnIntent = new Intent();

                ArrayList<Uri> uriList = new ArrayList<Uri>(Arrays.asList(imageUris)); //new ArrayList is only needed if you absolutely need an ArrayList
                Log.i("mSwitcher", "imageUris[0] = "+imageUris[0].toString());
                returnIntent.putParcelableArrayListExtra    ("multipleImage", uriList);
                //returnIntent.putExtra("result", imageUris[0].toString());
                setResult(Activity.RESULT_FIRST_USER, returnIntent);
                finish();

                //if don't want to return data:
//                Intent returnIntent = new Intent();
//                setResult(Activity.RESULT_CANCELED, returnIntent);
//                finish();
            }
        });



        String s = getIntent().getStringExtra(EXTRA_MESSAGE);
        Log.i("onActivityResult", "getStringExtra = " + s);

//         imageUris = { Uri.parse(s),
//                Uri.parse(s),
//                Uri.parse(s)};

        Uri uri = Uri.parse(s);
        File f = new File(uri.toString());
        // Log.i("original_image","f.getName(); = "+f.getName());
        f = new File(resizeAndCompressImageBeforeSend(getApplicationContext(), uri, "/" + f.getName() + UUID.randomUUID().toString() + System.currentTimeMillis() + ".jpg"));
        uri = (Uri.fromFile(f));
        imageUris = addUri(imageUris,uri);


        //imageUris = addUri(imageUris,Uri.parse(s));

        SelelctImageGrid adapter = new SelelctImageGrid(SelectImageActivity.this, web, imageUris);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("addOnItemTouchListener", "onItemClick position =" + position);


                //respond to the add more image event
                if(position == imageUris.length){

                    Log.i("addOnItemTouchListener", "last one!");

//                    //stat camera roll
//                    Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT,
//                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) , NEW_PHOTO_REQUEST);//one can be replaced with any action code

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {


                            // Should we show an explanation?
                            if (shouldShowRequestPermissionRationale(
                                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                // Explain to the user why we need to read the contacts
                                Log.i("my", "permission.READ_EXTERNAL_STORAGE");

                            }

                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant

//                        return;
                        }
                        else{
                            Log.i("my", "permission.READ_EXTERNAL_STORAGE3");
                            //normal request goes here
                            Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), PICK_PHOTO_REQUEST);//one can be replaced with any action code
                        }
                    }



                }
                //Toast.makeText(MainActivity.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();

            }
        });







//        //add button
//        Button button = (Button) findViewById(R.id.button_new_image_done);
//        button.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//
//
//            }
//
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to


        if (requestCode == NEW_PHOTO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri selectedImageURI = data.getData();
                Log.i("onActivityResult", "result = " + selectedImageURI.toString());








                Uri uri = selectedImageURI;
                File f = new File(uri.toString());
                // Log.i("original_image","f.getName(); = "+f.getName());
                f = new File(resizeAndCompressImageBeforeSend(getApplicationContext(), uri, "/" + f.getName() + UUID.randomUUID().toString() + System.currentTimeMillis() + ".jpg"));
                uri = (Uri.fromFile(f));
                imageUris = addUri(imageUris,uri);
                //imageUris = addUri(imageUris,selectedImageURI);

                SelelctImageGrid adapter = new SelelctImageGrid(SelectImageActivity.this, web, imageUris);
                Log.i("onActivityResult", "imageUris.length = " + imageUris.length);
                grid=(GridView)findViewById(R.id.grid);
                grid.setAdapter(adapter);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("onActivityResult", "NEW_TITLE_REQUEST RESULT_CANCELED");
                //Write your code if there's no result
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.i("my", "permission requestCode = "+requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("my", "permission.READ_EXTERNAL_STORAGE2");

                    //to start activity after the first time asking for permission
                    Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), PICK_PHOTO_REQUEST);//one can be replaced with any action code
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.i("my", "permission.READ_EXTERNAL_STORAGE denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
//                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    Uri[] addUri(Uri[] urilist, Uri in) {
        if(urilist!=null) {
            Uri[] output = new Uri[urilist.length+1];

            for (int i = 0; i < urilist.length; i++) {
                output[i] = urilist[i];
            }
            output[urilist.length] = in;
            return output;
        }
        else{
            Uri[] output = {in};
            return output;
        }



    }

    public static String resizeAndCompressImageBeforeSend(Context context, Uri inputUri, String fileName) {
        String filePath = getRealPathFromURI_API19(context, inputUri);
        final int MAX_IMAGE_SIZE = 600 * 800; // max final file size in kilobytes   700 * 1024;

        // First decode with inJustDecodeBounds=true to check dimensions of image
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize(First we are going to resize the image to 800x800 image, in order to not have a big but very low quality image.
        //resizing the image will already reduce the file size, but after resizing we will check the file size and start to compress image
        options.inSampleSize = calculateInSampleSize(options, 800, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath, options);


        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            Log.d("compressBitmap", "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
            Log.d("compressBitmap", "Size: " + streamLength / 1024 + " kb");
        } while (streamLength >= MAX_IMAGE_SIZE);

        try {
            //save the resized and compressed file to disk cache
            Log.d("compressBitmap", "cacheDir: " + context.getCacheDir());
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir() + fileName);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e("compressBitmap", "Error on saving file");
        }
        //return the path of resized and compressed file
        return context.getCacheDir() + fileName;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(debugTag, "image height: " + height + "---image width: " + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.d(debugTag, "inSampleSize: " + inSampleSize);
        return inSampleSize;
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

}
