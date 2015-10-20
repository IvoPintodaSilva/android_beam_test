package com.example.ivosilva.sendnfctest;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class MainActivity extends Activity {
    NfcAdapter mNfcAdapter;

    // List of URIs to provide to Android Beam
    private Uri[] mFileUris = new Uri[1];

    // A File object containing the path to the transferred files
    private File mParentPath;
    // Incoming Intent
    private Intent mIntent;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //should always return the most recent
        setIntent(intent);
        Log.d("SWAG","SWAG");
        handleViewIntent();
    }


    private void handleViewIntent() {

        // Get the Intent action
        mIntent = getIntent();
        String action = mIntent.getAction();
        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        Log.d("handleViewIntent", "entrou");

        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // Get the URI from the Intent
            Uri beamUri = mIntent.getData();
            /*
             * Test for the type of URI, by getting its scheme value
             */

            Log.e("handleViewIntent", beamUri.toString());

            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                mParentPath = new File(handleFileUri(beamUri));
            } else if (TextUtils.equals(
                    beamUri.getScheme(), "content")) {
                mParentPath = handleContentUri(beamUri);
            }
        }
    }

    public File handleContentUri(Uri beamUri) {
        Log.d("handleContentUri", "entrou");

        // Position of the filename in the query Cursor
        int filenameIndex;
        // File object for the filename
        File copiedFile;
        // The filename stored in MediaStore
        String fileName;
        // Test the authority of the URI
        if (!TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            /*
             * Handle content URIs for other content providers
             */
            // For a MediaStore content URI
        } else {
            // Get the column that contains the file name
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor pathCursor =
                    getContentResolver().query(beamUri, projection,
                            null, null, null);
            // Check for a valid cursor
            if (pathCursor != null &&
                    pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA);
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
                // Create a File object for the filename
                copiedFile = new File(fileName);
                // Return the parent directory of the file
                return new File(copiedFile.getParent());
            } else {
                // The query didn't work; return null
                return null;
            }
        }
        return null;
    }

    public String handleFileUri(Uri beamUri) {
        Log.d("handleFileUri", "lol");
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        // Create a File object for this filename
        File copiedFile = new File(fileName);
        // Get a string containing the file's parent directory
        return copiedFile.getParent();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri fileUri;

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        String transferFile = "swag.txt";
        File extDir = getExternalFilesDir(null);
        File requestFile = new File(extDir, transferFile);

        if(!requestFile.exists()) {
            try{
                FileOutputStream fOut = new FileOutputStream(requestFile);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write("lindoooo binooooo");
                osw.flush();
                osw.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        if(!requestFile.exists()) {
            Log.d("TURBINADO","NON EXISTINGZ MANS");
        }
        else{
            try{
                BufferedReader r = new BufferedReader(new FileReader(requestFile));
                Log.d("TURBINADO", r.readLine() );

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        requestFile.setReadable(true, false);
        // Get a URI for the File and add it to the list of URIs
        fileUri = Uri.fromFile(requestFile);
        mFileUris[0] = fileUri;

        Log.d("OnCreate", mFileUris[0].toString());
        mNfcAdapter.setBeamPushUris(mFileUris, this);
    }

}
