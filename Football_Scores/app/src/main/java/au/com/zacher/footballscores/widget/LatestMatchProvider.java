package au.com.zacher.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import au.com.zacher.footballscores.service.MyFetchService;

/**
 * Created by Brad on 5/10/2015.
 */
public class LatestMatchProvider extends AppWidgetProvider
{
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds)
    {
        context.startService(new Intent(context, LatestMatchIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions)
    {
        context.startService(new Intent(context, LatestMatchIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent)
    {
        super.onReceive(context, intent);
        if (MyFetchService.BROADCAST_ACTION.equals(intent.getAction()))
        {
            context.startService(new Intent(context, LatestMatchIntentService.class));
        }
    }
}
