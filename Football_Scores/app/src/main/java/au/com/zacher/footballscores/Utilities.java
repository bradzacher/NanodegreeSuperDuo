package au.com.zacher.footballscores;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities
{
    public static final int   CHAMPIONS_LEAGUE = 362;
    public static final int   BUNDESLIGA1      = 394;
    public static final int   BUNDESLIGA2      = 395;
    public static final int   BUNDESLIGA3      = 396;
    public static final int   PREMIER_LEAGUE   = 398;
    public static final int   PRIMERA_DIVISION = 399;
    public static final int   SEGUNDA_DIVISION = 400;
    public static final int   SERIE_A          = 401;
    public static final int   PRIMERA_LIGA     = 402;
    public static final int   EREDIVISIE       = 404;
    public static final int[] LEAGUE_LIST      = new int[] {
            CHAMPIONS_LEAGUE,
            BUNDESLIGA1,
            BUNDESLIGA2,
            BUNDESLIGA3,
            PREMIER_LEAGUE,
            PRIMERA_DIVISION,
            SEGUNDA_DIVISION,
            SERIE_A,
            PRIMERA_LIGA,
            EREDIVISIE
    };

    public static String getLeague(Context context, int leagueNum)
    {
        switch (leagueNum)
        {
            case CHAMPIONS_LEAGUE:
                return context.getString(R.string.league_champions_league);
            case BUNDESLIGA1:
            case BUNDESLIGA2:
            case BUNDESLIGA3:
                return context.getString(R.string.league_bundesliga);
            case PREMIER_LEAGUE:
                return context.getString(R.string.league_premier_league);
            case PRIMERA_DIVISION:
                return context.getString(R.string.league_primera_division);
            case SEGUNDA_DIVISION:
                return context.getString(R.string.league_segunda_division);
            case SERIE_A:
                return context.getString(R.string.league_serie_a);
            case PRIMERA_LIGA:
                return context.getString(R.string.league_primera_liga);
            case EREDIVISIE:
                return context.getString(R.string.league_eredivisie);
            default:
                return context.getString(R.string.league_unknown);
        }
    }

    public static String getMatchDay(Context context, int matchDay, int leagueNum)
    {
        if (leagueNum == CHAMPIONS_LEAGUE)
        {
            if (matchDay <= 6)
            {
                return context.getString(R.string.matchday_group_stages);
            }
            else if (matchDay == 7 || matchDay == 8)
            {
                return context.getString(R.string.matchday_first_knockout);
            }
            else if (matchDay == 9 || matchDay == 10)
            {
                return context.getString(R.string.matchday_quarterfinal);
            }
            else if (matchDay == 11 || matchDay == 12)
            {
                return context.getString(R.string.matchday_semifinal);
            }
            else
            {
                return context.getString(R.string.matchday_final);
            }
        }
        else
        {
            return context.getString(R.string.matchday_unknown, matchDay);
        }
    }

    public static String getScores(int homeGoals, int awayGoals)
    {
        if (homeGoals < 0 || awayGoals < 0)
        {
            return " - ";
        }
        else
        {
            String home = String.valueOf(homeGoals);
            String away = String.valueOf(awayGoals);
            return home + " - " + away;
        }
    }

    public static int getTeamCrestByTeamName(String teamName, Context context)
    {
        if (teamName == null)
        {
            return R.drawable.no_icon;
        }
        // automatically pull the drawable based on the team name
        teamName = teamName.replace(" FC", ""); // football club
        teamName = teamName.replace("FC ", ""); // *
        teamName = teamName.replace(" CF", ""); // club de futbol
        teamName = teamName.replace(" AFC", ""); // association football club
        teamName = teamName.replace(" ", "_"); // replace spaces
        teamName = teamName.toLowerCase();
        int resourceId = context.getResources().getIdentifier(teamName, "drawable", context.getPackageName());
        if (resourceId == 0)
        {
            resourceId = R.drawable.no_icon;
        }
        return resourceId;
    }

    public static Drawable getTeamCrestDrawableByTeamName(String teamName, Context context)
    {
        int drawableId = Utilities.getTeamCrestByTeamName(teamName, context);
        return ContextCompat.getDrawable(context, drawableId);
    }

    public static boolean isRTL(Context context)
    {
        Configuration config = context.getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public static String getScoreItemAccessibilityString(Context context, String homeTeamName, String score, String awayTeamName)
    {
        return context.getString(R.string.score_item_accessibility_string, homeTeamName, awayTeamName, score);
    }
}
