package au.com.zacher.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.zacher.alexandria.data.AlexandriaContract;
import au.com.zacher.alexandria.services.BookService;
import au.com.zacher.alexandria.services.DownloadImage;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{

    public static final String EAN_KEY   = "EAN";
    private final       int    LOADER_ID = 10;
    private View                _rootView;
    private String              _ean;
    private String              _bookTitle;
    private ShareActionProvider _shareActionProvider;

    private Intent _shareActionIntent;

    public BookDetail()
    {
    }

    private void setShareAction()
    {
        if (_shareActionProvider != null && _shareActionIntent != null)
        {
            _shareActionProvider.setShareIntent(_shareActionIntent);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            _ean = arguments.getString(BookDetail.EAN_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }

        _rootView = inflater.inflate(R.layout.fragment_full_book, container, false);
        _rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, _ean);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return _rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        _shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setShareAction();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(_ean)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data)
    {
        if (!data.moveToFirst())
        {
            return;
        }

        _bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) _rootView.findViewById(R.id.fullBookTitle)).setText(_bookTitle);

        _shareActionIntent = new Intent(Intent.ACTION_SEND);
        _shareActionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        _shareActionIntent.setType("text/plain");
        _shareActionIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text, _bookTitle));
        setShareAction();

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        ((TextView) _rootView.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);

        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        ((TextView) _rootView.findViewById(R.id.fullBookDesc)).setText(desc);

        String   authors    = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = new String[0];
        String authorsStr = "";
        if (authors != null) {
            authorsArr = authors.split(",");
            authorsStr = authors.replace(",", "\n");
        }
        ((TextView) _rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
        ((TextView) _rootView.findViewById(R.id.authors)).setText(authorsStr);
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches())
        {
            new DownloadImage((ImageView) _rootView.findViewById(R.id.fullBookCover)).execute(imgUrl);
            _rootView.findViewById(R.id.fullBookCover).setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) _rootView.findViewById(R.id.categories)).setText(categories);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader)
    {

    }

    @Override
    public void onPause()
    {
        super.onDestroyView();
        if (MainActivity.IS_TABLET && _rootView.findViewById(R.id.right_container) == null)
        {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}