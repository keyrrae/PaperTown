/*
 *  Copyright (c) 2017 - present, Zhenyu Yang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 */

package edu.ucsb.cs.cs190i.papertown.town.towndetail;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.ucsb.cs.cs190i.papertown.ImageAdapter;
import edu.ucsb.cs.cs190i.papertown.R;
import edu.ucsb.cs.cs190i.papertown.models.Town;
import edu.ucsb.cs.cs190i.papertown.models.TownBuilder;
import edu.ucsb.cs.cs190i.papertown.models.UserSingleton;
import edu.ucsb.cs.cs190i.papertown.town.newtown.myMapFragment;



public class TownDetailActivity extends AppCompatActivity {

  private GridView imageGrid;
  private ArrayList<Uri> uriList;

    private String mode = "detail";

  String title = "";
  String address = "";
  String category = "";
  String description = "";
  String information = "";
  ArrayList<String> uriStringArrayList;

  float lat = 34.415320f;
  float lng = -119.84023f;

  private TownBuilder townBuilder = new TownBuilder();
  private List<String> remoteImageUrls = new ArrayList<>();
  private FirebaseStorage storage;
  private FirebaseDatabase database;
  private DatabaseReference townRef;

  private Integer[] mImageIds = {
          R.drawable.door, R.drawable.light, R.drawable.corner,
          R.drawable.mc, R.drawable.light, R.drawable.door,
          R.drawable.light, R.drawable.corner};


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_town_detail);



    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail); // Attaching the layout to the toolbar object
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(null);
    toolbar.setTitle("");
    toolbar.setSubtitle("");
    toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        Log.i("dataToD", "setNavigationOnClickListener");
        finish();
      }
    });



    this.imageGrid = (GridView) findViewById(R.id.detail_image_grid);
    this.uriList = new ArrayList<Uri>();
//    this.uriList.add(Uri.parse("https://s-media-cache-ak0.pinimg.com/564x/8f/af/c0/8fafc02753b860c3213ffe1748d8143d.jpg"));
//    this.uriList.add(Uri.parse("https://s-media-cache-ak0.pinimg.com/564x/8f/af/c0/8fafc02753b860c3213ffe1748d8143d.jpg"));


//    Intent intent = getIntent();
//    double lat = intent.getDoubleExtra("LAT", 0.0);
//    double lng = intent.getDoubleExtra("LNG", 0.0);
//    townBuilder.setAddress("testAddress");
//    townBuilder.setCategory("testCate");
//    townBuilder.setDescription("test descriptions");
//    townBuilder.setTitle("test title");
//    townBuilder.setLatLng(lat, lng);
    townBuilder.setUserId(UserSingleton.getInstance().getUid());


    FirebaseApp.initializeApp(this);
    storage = FirebaseStorage.getInstance();
    database = FirebaseDatabase.getInstance();
    townRef = database.getReference("towns");

      Button button_test_detail = (Button) findViewById(R.id.button_test_detail);
      button_test_detail.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Log.i("dataToD", "button_test_detail OnClickListener");




              if(mode.equals("preview")){
                  Log.i("dataToD", "SUBMIT!");


                if(storage != null) {
                  StorageReference storageRef = storage.getReference();

                  for(Uri uri: uriList) {



                      Log.i("original_image","newUri = "+uri.toString());
                      File f = new File(uri.toString());
                      Log.i("original_image","f.getName(); = "+f.getName());
                      f = new File(resizeAndCompressImageBeforeSend(getApplicationContext(), uri, "/"+f.getName()+".jpg"));

                      uri = (Uri.fromFile(f));

                      Log.i("resize_image","newUri_resized = "+uri.toString());


                    StorageReference riversRef = storageRef.child("images/" + uri.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(uri);

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                      }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        @SuppressWarnings("VisibleForTests")
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if(downloadUrl != null) {
                          remoteImageUrls.add(downloadUrl.toString());
                          if(remoteImageUrls.size() == uriList.size()){
                            townBuilder.setImages(remoteImageUrls);
                            DatabaseReference newTown = townRef.child(townBuilder.getId());
                            Town town = townBuilder.build();
                            newTown.setValue(townBuilder.build(), new DatabaseReference.CompletionListener() {
                              @Override
                              public void onComplete(DatabaseError databaseError,
                                                     DatabaseReference databaseReference) {
                                Toast.makeText(
                                        TownDetailActivity.this,
                                        "Successfully submitted network",
                                        Toast.LENGTH_SHORT
                                ).show();
                                finish();
                              }
                            });
                          }
                        }
                      }
                    });
                  }
                }
              }
              else{
                  finish();
              }
              Log.i("dataToD", "button_test_detail OnClickListener");
                      //.setInformation   ???
          }
      });




    String s = getIntent().getStringExtra("dataToD");
    Log.i("dataToD", "data = "+s);

    title = getIntent().getStringExtra("title");
    address = getIntent().getStringExtra("address");
    description = getIntent().getStringExtra("description");
    category = getIntent().getStringExtra("category");
    information = getIntent().getStringExtra("information");
    uriStringArrayList = getIntent().getStringArrayListExtra("uriStringArrayList");
      mode = getIntent().getStringExtra("mode");
      if(mode==null){
          mode = "detail";
      }

    Town passedInTown = (Town)getIntent().getSerializableExtra("town");
    if(passedInTown!=null) {
      Log.i("dataToD", "passedInTown getDescription = "+passedInTown.getDescription().toString());
    }


      //change button color
      if(mode!=null&&mode.equals("preview")) {
          //change color of submission button
          button_test_detail.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
          button_test_detail.setText("SUBMIT !");



      }
      else{
          //change color of submission button
//          button_test_detail.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
//          button_test_detail.setText("DONE");
        button_test_detail.setVisibility(Button.INVISIBLE);
      }


    //process uriStringArrayList, put data into uriList
    if(uriStringArrayList!=null&&uriStringArrayList.size()>0) {
      for (int i = 0; i < uriStringArrayList.size(); i++) {
        uriList.add(Uri.parse(uriStringArrayList.get(i)));
        Log.i("manu", "uriList["+i+"] = "+uriList.get(i));
      }
    }
    else{
      Toast.makeText(getApplicationContext(), "Cannot get images, default images used!", Toast.LENGTH_SHORT).show();
      this.uriList.add(Uri.parse("https://s-media-cache-ak0.pinimg.com/564x/8f/af/c0/8fafc02753b860c3213ffe1748d8143d.jpg"));
      this.uriList.add(Uri.parse("https://s-media-cache-ak0.pinimg.com/564x/8f/af/c0/8fafc02753b860c3213ffe1748d8143d.jpg"));
        this.uriList.add(Uri.parse("https://s-media-cache-ak0.pinimg.com/564x/8f/af/c0/8fafc02753b860c3213ffe1748d8143d.jpg"));
        this.uriList.add(Uri.parse("https://s-media-cache-ak0.pinimg.com/564x/8f/af/c0/8fafc02753b860c3213ffe1748d8143d.jpg"));
        this.uriList.add(Uri.parse("https://s-media-cache-ak0.pinimg.com/564x/8f/af/c0/8fafc02753b860c3213ffe1748d8143d.jpg"));
        this.uriList.add(Uri.parse("https://s-media-cache-ak0.pinimg.com/564x/8f/af/c0/8fafc02753b860c3213ffe1748d8143d.jpg"));

    }

    //load title
    if(title!=null) {
      TextView detail_town_description = (TextView) findViewById(R.id.detail_town_title);
      detail_town_description.setText(title);
        townBuilder.setTitle(title);
    }

    //load address
    if(address!=null) {
      TextView detail_town_description = (TextView) findViewById(R.id.detail_address);
      detail_town_description.setText(address);
        townBuilder.setAddress(address);

        //processing address to latlng
        String[] separated = address.split(",");
        lat = Float.parseFloat(separated[0]);
        lng = Float.parseFloat(separated[1]);
        townBuilder.setLatLng(lat, lng);
    }



    //load description
    if(description!=null) {
      TextView detail_town_description = (TextView) findViewById(R.id.detail_town_description);
      detail_town_description.setText(description);
        townBuilder.setDescription(description);
    }



    //load category
    if(category!=null) {
      TextView detail_town_description = (TextView) findViewById(R.id.detail_town_category);
      detail_town_description.setText(category);
        townBuilder.setCategory(category);
    }

    //load information
    if(information!=null) {
      TextView detail_town_description = (TextView) findViewById(R.id.detail_town_information);
      detail_town_description.setText(information);
        townBuilder.setUserAlias(information);
    }


    //load uriStringArrayList
    if(uriList!=null) {
      if(uriList.size()>0) {
        ImageView detail_town_image = (ImageView) findViewById(R.id.detail_town_image);
        Picasso.with(this).load(uriList.get(0))
                .fit()
                .into(detail_town_image);
        this.imageGrid.setAdapter(new ImageAdapter(this, uriList));
      }
    }



    //adjust the gridView hright





    //handle the google Maps

    myMapFragment mapFragment = ((myMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map));

    if (mapFragment != null) {
      mapFragment.getMapAsync(new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap map) {
          //enable myLocationButton
          if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            //map.getUiSettings().setMyLocationButtonEnabled(true);
          }
          else {
            Log.i("manu", "Error - checkSelfPermission!!");
          }
          //end of enabling myLocationButton

          BitmapDescriptor icon1 = BitmapDescriptorFactory.fromResource(R.drawable.test_marker);
          BitmapDescriptor icon2 = BitmapDescriptorFactory.fromResource(R.drawable.test_marker3);
          BitmapDescriptor icon3 = BitmapDescriptorFactory.fromResource(R.drawable.test_marker2);


          if(category!=null&&!category.isEmpty()) {
            if (category.equals("place")) {
              //add markers
              map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                      .title("Ohh!")
                      .snippet("TsadADSadsA")
                      .icon(icon1));
            } else if (category.equals("creature")) {

              map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                      .title("Underclass beauty")
                      .snippet("Get sunburn in my head")
                      .icon(icon2));
            } else if (category.equals("event")) {
              map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                      .title("Big thing!")
                      .snippet("Meat carnival")
                      .icon(icon3));

              //end of adding markers
            }
          }

          //camera animation
          //map.moveCamera(CameraUpdateFactory.newLatLngZoom(/*some location*/, 10));

          if (map != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));  //add animation
          }


          //end of camera animation


        }
      });

      mapFragment.setListener(new myMapFragment.OnTouchListener() {
        @Override
        public void onTouch() {
          ScrollView mScrollView = (ScrollView)findViewById(R.id.scrollView_detail);
          mScrollView.requestDisallowInterceptTouchEvent(true);
        }
      });


    } else {

      Log.i("manu", "Error - Map Fragment was null!!");
    }






  }




    public static String resizeAndCompressImageBeforeSend(Context context, Uri inputUri, String fileName){
        String filePath = getRealPathFromURI_API19(context,inputUri);
        final int MAX_IMAGE_SIZE = 600*800; // max final file size in kilobytes   700 * 1024;

        // First decode with inJustDecodeBounds=true to check dimensions of image
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath,options);

        // Calculate inSampleSize(First we are going to resize the image to 800x800 image, in order to not have a big but very low quality image.
        //resizing the image will already reduce the file size, but after resizing we will check the file size and start to compress image
        options.inSampleSize = calculateInSampleSize(options, 800, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inPreferredConfig= Bitmap.Config.ARGB_8888;

        Bitmap bmpPic = BitmapFactory.decodeFile(filePath,options);


        int compressQuality = 100; // quality decreasing by 5 every loop.
        int streamLength;
        do{
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            Log.d("compressBitmap", "Quality: " + compressQuality);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
            compressQuality -= 5;
            Log.d("compressBitmap", "Size: " + streamLength/1024+" kb");
        }while (streamLength >= MAX_IMAGE_SIZE);

        try {
            //save the resized and compressed file to disk cache
            Log.d("compressBitmap","cacheDir: "+context.getCacheDir());
            FileOutputStream bmpFile = new FileOutputStream(context.getCacheDir()+fileName);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
            Log.e("compressBitmap", "Error on saving file");
        }
        //return the path of resized and compressed file
        return  context.getCacheDir()+fileName;
    }



    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        String debugTag = "MemoryInformation";
        // Image nin islenmeden onceki genislik ve yuksekligi
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(debugTag,"image height: "+height+ "---image width: "+ width);
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
        Log.d(debugTag,"inSampleSize: "+inSampleSize);
        return inSampleSize;
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }



}
