package au.com.zacher.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.zacher.footballscores.DatabaseContract;
import au.com.zacher.footballscores.MainActivity;
import au.com.zacher.footballscores.R;
import au.com.zacher.footballscores.ScoresAdapter;
import au.com.zacher.footballscores.Utilities;

/**
 * Created by Brad on 5/10/2015.
 */
public class LatestMatchIntentService extends IntentService
{
    public LatestMatchIntentService()
    {
        super("LatestMatchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[]            appWidgetIds     = appWidgetManager.getAppWidgetIds(new ComponentName(this, LatestMatchProvider.class));

        // query the content resolver
        Date             date   = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Cursor cursor = getContentResolver().query(DatabaseContract.ScoresTable.buildScoreWithDate(),
                                                   null,
                                                   null,
                                                   new String[] {format.format(date)},
                                                   DatabaseContract.ScoresTable.TIME_COL + " desc");
        if (cursor == null)
        {
            return;
        }
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return;
        }

        int    awayTeamGoals, homeTeamGoals;
        String awayTeamName, homeTeamName, time, score, accessibilityStr;

        boolean found = false;
        cursor.moveToFirst();
        // try to find the latest match with a score
        do
        {
            awayTeamGoals = cursor.getInt(ScoresAdapter.COL_AWAY_GOALS);
            homeTeamGoals = cursor.getInt(ScoresAdapter.COL_HOME_GOALS);
            if (awayTeamGoals != -1 && homeTeamGoals != -1)
            {
                found = true;
                break;
            }
        } while (cursor.moveToNext());
        if (!found)
        {
            // fall back on the first record for the day
            cursor.moveToFirst();
        }


        score = Utilities.getScores(homeTeamGoals, awayTeamGoals);
        homeTeamName = cursor.getString(ScoresAdapter.COL_HOME);
        awayTeamName = cursor.getString(ScoresAdapter.COL_AWAY);
        time = cursor.getString(ScoresAdapter.COL_MATCHTIME);
        accessibilityStr = Utilities.getScoreItemAccessibilityString(homeTeamName, score, awayTeamName);

        Log.d("TAG", "WIDGET UPDATED:");
        Log.d("TAG", score);
        Log.d("TAG", homeTeamName);
        Log.d("TAG", awayTeamName);
        Log.d("TAG", time);

        cursor.close();

        // update the views
        for (int id : appWidgetIds)
        {
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_latest_match);

            views.setViewVisibility(R.id.widget_loading, View.GONE);
            views.setViewVisibility(R.id.widget_content, View.VISIBLE);

            views.setTextViewText(R.id.home_team, homeTeamName);
            views.setTextViewText(R.id.away_team, awayTeamName);
            views.setTextViewCompoundDrawables(R.id.home_team, 0, Utilities.getTeamCrestByTeamName(homeTeamName, this), 0, 0);
            views.setTextViewCompoundDrawables(R.id.away_team, 0, Utilities.getTeamCrestByTeamName(awayTeamName, this), 0, 0);
            views.setTextViewText(R.id.data_textview, time);
            views.setTextViewText(R.id.score_textview, score);

            views.setContentDescription(R.id.widget, accessibilityStr);

            Intent onClickIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, onClickIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(id, views);
        }
    }
}
