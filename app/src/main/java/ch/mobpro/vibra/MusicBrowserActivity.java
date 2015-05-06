package ch.mobpro.vibra;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matthias on 04.05.2015.
 */
public class MusicBrowserActivity extends Activity {

    private static ArrayList<String> songs;
    private ListView lv;

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
        songs = getStringList(MainActivity.getMusicFiles());
        lv = (ListView) findViewById(R.id.listViewTitel);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, songs);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("songIndex", position);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<String> getStringList(ArrayList<File> list) {
        ArrayList<String> songs = new ArrayList<>();
        for (File file : list) {
            songs.add(file.getName().substring(0, file.getName().length() - 4));
        }
        return songs;
    }
}
