package au.com.zacher.footballscores;

import android.view.View;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ViewHolder
{
    public final TextView homeName;
    public final TextView awayName;
    public final TextView score;
    public final TextView date;
    public       double   matchId;

    public ViewHolder(View view)
    {
        homeName = (TextView) view.findViewById(R.id.home_team);
        awayName = (TextView) view.findViewById(R.id.away_team);

        score = (TextView) view.findViewById(R.id.score_textview);
        date = (TextView) view.findViewById(R.id.data_textview);
    }
}
