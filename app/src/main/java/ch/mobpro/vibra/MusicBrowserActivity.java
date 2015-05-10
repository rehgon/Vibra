package ch.mobpro.vibra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import java.util.ArrayList;

/**
 * Created by Matthias on 04.05.2015.
 */
public class MusicBrowserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_browser);
        TabHost tabHost = (TabHost) findViewById(R.id.TabHost);
        tabHost.setup();
        // tab1
        TabHost.TabSpec spec1 = tabHost.newTabSpec("tab_creation");
        spec1.setIndicator("Interpreten", getResources().getDrawable(android.R.drawable.ic_menu_add));
        spec1.setContent(R.id.onglet1);
        tabHost.addTab(spec1);
        //tab2
        TabHost.TabSpec spec2 = tabHost.newTabSpec("tab_creation");
        spec2.setIndicator("Alben", getResources().getDrawable(android.R.drawable.ic_menu_add));
        spec2.setContent(R.id.onglet2);
        tabHost.addTab(spec2);
        //tab3
        TabHost.TabSpec spec3 = tabHost.newTabSpec("tab_creation");
        spec3.setIndicator("Titel", getResources().getDrawable(android.R.drawable.ic_menu_add));
        spec3.setContent(R.id.onglet3);
        tabHost.addTab(spec3);
        //ListView
        ArrayList<String> songs = MainActivity.getSongs();
        ListView lv = (ListView) findViewById(R.id.listViewTitel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, songs);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("songIndex", position);
                startActivity(intent);
            }
        });
    }
}
