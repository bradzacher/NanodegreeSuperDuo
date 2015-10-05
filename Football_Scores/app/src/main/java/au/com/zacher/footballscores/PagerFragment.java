package au.com.zacher.footballscores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment
{
    public static final int NUM_PAGES = 5;

    public ViewPager pagerHandler;
    private MainScreenFragment[] _viewFragments = new MainScreenFragment[NUM_PAGES];

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        pagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        PageAdapter _pagerAdapter = new PageAdapter(getChildFragmentManager());

        for (int i = 0; i < NUM_PAGES; i++)
        {
            _viewFragments[i] = new MainScreenFragment();
            _viewFragments[i].setFragmentDate(calculateTime(i));
        }
        pagerHandler.setAdapter(_pagerAdapter);
        pagerHandler.setCurrentItem(MainActivity.currentFragment);

        return rootView;
    }

    private long calculateTime(int position)
    {
        long time = System.currentTimeMillis() + (position - 2) * 86400000;
        return time;
    }

    private class PageAdapter extends FragmentStatePagerAdapter
    {
        @Override
        public Fragment getItem(int i)
        {
            i = getPosition(i);
            return _viewFragments[i];
        }

        private int getPosition(int i)
        {
            // reverse the pager direction on RTL languages
            if (Utilities.isRTL(pagerHandler.getContext()))
            {
                int newI = (NUM_PAGES - i - 1) % NUM_PAGES;
                i = newI;
            }
            return i;
        }

        @Override
        public int getCount()
        {
            return NUM_PAGES;
        }

        public PageAdapter(FragmentManager fm)
        {
            super(fm);
        }

        // Returns the page title for the top indicator
        @SuppressLint("NewApi")
        @Override
        public CharSequence getPageTitle(int position)
        {
            MainScreenFragment fragment = (MainScreenFragment)this.getItem(position);
            return getDayName(getActivity(), fragment.getFragmentDate());
        }

        public String getDayName(Context context, long dateInMillis)
        {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay        = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay)
            {
                return context.getString(R.string.today);
            }
            else if (julianDay == currentJulianDay + 1)
            {
                return context.getString(R.string.tomorrow);
            }
            else if (julianDay == currentJulianDay - 1)
            {
                return context.getString(R.string.yesterday);
            }
            else
            {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
