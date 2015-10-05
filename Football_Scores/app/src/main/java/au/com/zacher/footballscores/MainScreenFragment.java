package au.com.zacher.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.zacher.footballscores.service.MyFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final int SCORES_LOADER = 0;

    public ScoresAdapter adapter;
    private String[] _fragmentDate     = new String[1];
    private int      _lastSelectedItem = -1;
    private long _fragmentDateLong;

    public MainScreenFragment()
    {
    }

    private void updateScores()
    {
        Intent service_start = new Intent(getActivity(), MyFetchService.class);
        getActivity().startService(service_start);
    }

    public void setFragmentDate(long date)
    {
        _fragmentDateLong = date;
        Date fragmentDate = new Date(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        _fragmentDate[0] = format.format(fragmentDate);
    }

    public long getFragmentDate()
    {
        return _fragmentDateLong;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState)
    {
        updateScores();
        View           rootView   = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView score_list = (ListView) rootView.findViewById(R.id.scores_list);
        adapter = new ScoresAdapter(getActivity(), null, 0);
        score_list.setAdapter(adapter);
        getLoaderManager().initLoader(SCORES_LOADER, null, this);
        adapter.detailMatchId = MainActivity.selectedMatchId;
        score_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ViewHolder selected = (ViewHolder) view.getTag();
                adapter.detailMatchId = selected.matchId;
                MainActivity.selectedMatchId = (int) selected.matchId;
                adapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(), DatabaseContract.ScoresTable.buildScoreWithDate(), null, null, _fragmentDate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");
        //cursor.moveToFirst();
        /*
        while (!cursor.isAfterLast())
        {
            Log.v(FetchScoreTask.LOG_TAG,cursor.getString(1));
            cursor.moveToNext();
        }
        */

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            i++;
            cursor.moveToNext();
        }
        //Log.v(FetchScoreTask.LOG_TAG,"Loader query: " + String.valueOf(i));
        adapter.swapCursor(cursor);
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        adapter.swapCursor(null);
    }


}
