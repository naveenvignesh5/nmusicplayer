package com.theace56.naveenvignesh.nplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class credits extends AppCompatActivity {

    //activity to show developer credits

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

    }

    //method to open github
    public void github(View v)
    {
        String url="http://www.github.com/";
        Intent git = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
        startActivity(git);
    }

    public void onBackPressed()
    {
       Intent i = new Intent(this,record.class);
        startActivity(i);
        finish();
    }

}
