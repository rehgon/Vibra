package ch.mobpro.vibra;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

/**
 * Created by Matthias on 04.05.2015.
 */
public class MusicBrowserActivity extends Activity {

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_browser);
        tabHost = (TabHost) findViewById(R.id.TabHost);
        tabHost.setup();
        // tab1
        TabHost.TabSpec spec1 = tabHost.newTabSpec("tab_creation");
        spec1.setIndicator("Interpreten", getResources().getDrawable(android.R.drawable.ic_menu_add));
        spec1.setContent(R.id.onglet1);
        tabHost.addTab(spec1);
        //tab2
        TabHost.TabSpec spec2 = tabHost.newTabSpec("tab_creation");
        spec2.setIndicator("Alben", getResources().getDrawable(android.R.drawable.ic_menu_add));
        spec2.setContent(R.id.onglet1);
        tabHost.addTab(spec2);
        //tab3
        TabHost.TabSpec spec3 = tabHost.newTabSpec("tab_creation");
        spec3.setIndicator("Titel",getResources().getDrawable(android.R.drawable.ic_menu_add));
        spec3.setContent(R.id.onglet1);
        tabHost.addTab(spec3);
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
}
