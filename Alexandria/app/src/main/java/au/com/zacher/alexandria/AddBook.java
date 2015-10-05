package au.com.zacher.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import au.com.zacher.alexandria.data.AlexandriaContract;
import au.com.zacher.alexandria.services.BookService;
import au.com.zacher.alexandria.services.DownloadImage;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final String TAG           = "INTENT_TO_SCAN_ACTIVITY";
    private final        String EAN_CONTENT   = "eanContent";
    private static final String SCAN_FORMAT   = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";
    private final        int    LOADER_ID     = 1;
    private EditText _ean;
    private View     _rootView;

    private String _scanFormat   = "Format:";
    private String _scanContents = "Contents:";


    public AddBook()
    {
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (_ean != null)
        {
            outState.putString(EAN_CONTENT, _ean.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        _rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        _ean = (EditText) _rootView.findViewById(R.id.ean);

        _ean.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //no need
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String ean = s.toString();
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978"))
                {
                    ean = "978" + ean;
                }
                if (ean.length() < 13)
                {
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.FETCH_BOOK);
                getActivity().startService(bookIntent);
                AddBook.this.restartLoader();
            }
        });

        _rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(AddBook.this);
                integrator.setCaptureActivity(BarcodeScan.class);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        _rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                _ean.setText("");
            }
        });

        _rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, _ean.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                _ean.setText("");
            }
        });

        if (savedInstanceState != null)
        {
            _ean.setText(savedInstanceState.getString(EAN_CONTENT));
            _ean.setHint("");
        }

        return _rootView;
    }

    private void restartLoader()
    {
        LoaderManager manager = getLoaderManager();
        // make sure we have a loader before trying to restart it
        if (manager.getLoader(LOADER_ID) != null)
        {
            manager.restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if (_ean.getText().length() == 0)
        {
            return null;
        }
        String eanStr = _ean.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith("978"))
        {
            eanStr = "978" + eanStr;
        }

        // make sure that the barcode is in the correct format
        long barcodeNumber;
        try
        {
            barcodeNumber = Long.parseLong(eanStr);
        }
        catch (NumberFormatException ignored)
        {
            Toast.makeText(getActivity(), getText(R.string.scan_error_invalid_format), Toast.LENGTH_LONG).show();
            _ean.setText("");
            return null;
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(barcodeNumber),
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

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) _rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        ((TextView) _rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

        String   authors    = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");
        ((TextView) _rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
        ((TextView) _rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches())
        {
            new DownloadImage((ImageView) _rootView.findViewById(R.id.bookCover)).execute(imgUrl);
            _rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) _rootView.findViewById(R.id.categories)).setText(categories);

        _rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        _rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader)
    {

    }

    private void clearFields()
    {
        ((TextView) _rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) _rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) _rootView.findViewById(R.id.authors)).setText("");
        ((TextView) _rootView.findViewById(R.id.categories)).setText("");
        _rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        _rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        _rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == BarcodeScan.REQUEST_SCAN_START && resultCode == Activity.RESULT_OK)
        {
            String barcodeValue = data.getStringExtra(BarcodeScan.SCAN_RESULT_VALUE_KEY);
            _ean.setText(barcodeValue);
        }*/
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                Log.d("MainActivity", "Cancelled scan");
            }
            else
            {
                Log.d("MainActivity", "Scanned");
                String barcode = result.getContents();
                _ean.setText(barcode);
            }
        }
        else
        {
            Log.d("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
