package au.com.zacher.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity
{
    public static final  String LOG_TAG  = "MainActivity";
    private static final String SAVE_TAG = "Save Test";

    public static int selectedMatchId;
    public static int currentFragment = 2;
    private PagerFragment _myMain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG, "Reached MainActivity onCreate");
        if (savedInstanceState == null)
        {
            _myMain = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                                       .add(R.id.container, _myMain)
                                       .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about)
        {
            Intent start_about = new Intent(this, AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.v(SAVE_TAG, "will save");
        Log.v(SAVE_TAG, "fragment: " + String.valueOf(_myMain.pagerHandler.getCurrentItem()));
        Log.v(SAVE_TAG, "selected id: " + selectedMatchId);
        outState.putInt("Pager_Current", _myMain.pagerHandler.getCurrentItem());
        outState.putInt("Selected_match", selectedMatchId);
        getSupportFragmentManager().putFragment(outState, "_myMain", _myMain);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        Log.v(SAVE_TAG, "will retrive");
        Log.v(SAVE_TAG, "fragment: " + String.valueOf(savedInstanceState.getInt("Pager_Current")));
        Log.v(SAVE_TAG, "selected id: " + savedInstanceState.getInt("Selected_match"));
        currentFragment = savedInstanceState.getInt("Pager_Current");
        selectedMatchId = savedInstanceState.getInt("Selected_match");
        _myMain = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState, "_myMain");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
