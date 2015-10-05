package au.com.zacher.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter
{
    public static final  int    COL_DATE                = 1;
    public static final  int    COL_MATCHTIME           = 2;
    public static final  int    COL_HOME                = 3;
    public static final  int    COL_AWAY                = 4;
    public static final  int    COL_LEAGUE              = 5;
    public static final  int    COL_HOME_GOALS          = 6;
    public static final  int    COL_AWAY_GOALS          = 7;
    public static final  int    COL_ID                  = 8;
    public static final  int    COL_MATCHDAY            = 9;
    private static final String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    public double detailMatchId = 0;

    public ScoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View       mItem   = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.homeName.setText(cursor.getString(COL_HOME));
        holder.awayName.setText(cursor.getString(COL_AWAY));
        holder.date.setText(cursor.getString(COL_MATCHTIME));
        holder.score.setText(Utilities.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        holder.matchId = cursor.getDouble(COL_ID);
        holder.homeCrest.setImageResource(Utilities.getTeamCrestByTeamName(cursor.getString(COL_HOME)));
        holder.awayCrest.setImageResource(Utilities.getTeamCrestByTeamName(cursor.getString(COL_AWAY)));
        //Log.v(FetchScoreTask.LOG_TAG,holder.homeName.getText() + " Vs. " + holder.awayName.getText() +" id " + String.valueOf(holder.matchId));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detailMatchId));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View      v         = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if (holder.matchId == detailMatchId)
        {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilities.getMatchDay(cursor.getInt(COL_MATCHDAY), cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(cursor.getInt(COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //add Share Action
                    String shareString = holder.homeName.getText() + " " + holder.score.getText() + " " + holder.awayName.getText() + " " + FOOTBALL_SCORES_HASHTAG;
                    Intent i           = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, shareString);
                    // create a share chooser
                    context.startActivity(Intent.createChooser(i, context.getString(R.string.share_match)));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }

    }
}
