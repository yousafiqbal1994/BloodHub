package com.giveblood.bloodhub.searchfeature;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.giveblood.bloodhub.R;
import com.giveblood.bloodhub.messagingfeature.ChatsFragment;

/**
 * Created by YouCaf Iqbal on 12/23/2016.
 */

public class EmptyPeopleList extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emptypeoplelistview);
        textView = (TextView) findViewById(R.id.textViewempty);
        String text = "Sorry, No user with bloodgroup "+getIntent().getStringExtra("bg")+" found in "+getIntent().getStringExtra("km")+" distance." ;
        textView.setText(text);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        SearchFragment.seek_bar.setProgress(0);
    }
}
