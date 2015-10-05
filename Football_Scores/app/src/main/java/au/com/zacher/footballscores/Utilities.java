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
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int BUNDESLIGA1      = 394;
    public static final int BUNDESLIGA2      = 395;
    public static final int BUNDESLIGA3      = 396;
    public static final int PREMIER_LEAGUE   = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A          = 401;
    public static final int PRIMERA_LIGA     = 402;
    public static final int EREDIVISIE       = 404;
    public static final int[] LEAGUE_LIST = new int[] {
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

    public static String getLeague(int leagueNum)
    {
        switch (leagueNum)
        {
            case CHAMPIONS_LEAGUE:
                return "UEFA Champions League";
            case BUNDESLIGA1:
            case BUNDESLIGA2:
            case BUNDESLIGA3:
                return "Bundesliga";
            case PREMIER_LEAGUE:
                return "Premier League";
            case PRIMERA_DIVISION:
                return "Primera Division";
            case SEGUNDA_DIVISION:
                return "Segunda Division";
            case SERIE_A:
                return "Seria A";
            case PRIMERA_LIGA:
                return "Primera Liga";
            case EREDIVISIE:
                return "Eredivisie";
            default:
                return "Unknown league. Please report";
        }
    }

    public static String getMatchDay(int matchDay, int leagueNum)
    {
        if (leagueNum == CHAMPIONS_LEAGUE)
        {
            if (matchDay <= 6)
            {
                return "Group Stages, Matchday : 6";
            }
            else if (matchDay == 7 || matchDay == 8)
            {
                return "First Knockout round";
            }
            else if (matchDay == 9 || matchDay == 10)
            {
                return "QuarterFinal";
            }
            else if (matchDay == 11 || matchDay == 12)
            {
                return "SemiFinal";
            }
            else
            {
                return "Final";
            }
        }
        else
        {
            return "Matchday : " + String.valueOf(matchDay);
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

    public static String getScoreItemAccessibilityString(String homeTeamName, String score, String awayTeamName)
    {
        return "Home: " + homeTeamName + ", Away: " + awayTeamName + ", Score: " + score + ".";
    }
}
