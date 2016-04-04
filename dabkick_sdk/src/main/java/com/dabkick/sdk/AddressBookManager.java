package com.dabkick.sdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class AddressBookManager extends Fragment implements
        AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    int count = 0;
    private static final String TAG = "ContactsListFragment";
    ListView contacts_list;
    RelativeLayout secondSearchLayout;
    EditText searchEmailorPhone, searchName;
    private static final String STATE_PREVIOUSLY_SELECTED_KEY = "com.example.android.contactslist.ui.SELECTED_ITEM";
    private boolean isSearchResultView = false;
    private ContactsAdapter mAdapter;
    private String mSearchTerm;
    // OnTextChangedListener onTextChangedListener;
    /*ClearableEditText clearableEdittext;
    NewClearableCapsEditText secondClearableEditText;*/
    // Contact selected listener that allows the activity holding this fragment
    // to be notified of
    // a contact being selected
    private OnContactsInteractionListener mOnContactSelectedListener;

    // Stores the previously selected search item so that on a configuration
    // change the same item
    // can be reselected again
    private int mPreviouslySelectedSearchItem = 0;

    // Whether or not the search query has changed since the last time the
    // loader was refreshed
    private boolean mSearchQueryChanged;

    // Whether or not this fragment is showing in a two-pane layout
    private boolean mIsTwoPaneLayout;

    // Whether or not this is a search result view of this fragment, only used
    // on pre-honeycomb
    // OS versions as search results are shown in-line via Action Bar search
    // from honeycomb onward
    private boolean mIsSearchResultView = false;

    /**
     * Fragments require an empty constructor.
     */
    // public ContactsListFragment() {}

    /**
     * In platform versions prior to Android 3.0, the ActionBar and SearchView
     * are not supported, and the UI gets the search string from an EditText.
     * However, the fragment doesn't allow another search when search results
     * are already showing. This would confuse the user, because the resulting
     * search would re-query the Contacts Provider instead of searching the
     * listed results. This method sets the search query and also a boolean that
     * tracks if this Fragment should be displayed as a search result view or
     * not.
     *
     * @param query The contacts search query.
     */
    public void setSearchQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            mIsSearchResultView = false;
        } else {
            mSearchTerm = query;
            mIsSearchResultView = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if this fragment is part of a two-pane set up or a single pane
        // by reading a
        // boolean from the application resource directories. This lets allows
        // us to easily specify
        // which screen sizes should use a two-pane layout by setting this
        // boolean in the
        // corresponding resource size-qualified directory.
        mIsTwoPaneLayout = getResources().getBoolean(R.bool.has_two_panes);

        // Let this fragment contribute menu items
        setHasOptionsMenu(true);

        // Create the main contacts adapter
        mAdapter = new ContactsAdapter(getActivity());


        if (savedInstanceState != null) {
            // If we're restoring state after this fragment was recreated then
            // retrieve previous search term and previously selected search
            // result.
            mSearchTerm = savedInstanceState.getString(SearchManager.QUERY);
            mPreviouslySelectedSearchItem = savedInstanceState.getInt(
                    STATE_PREVIOUSLY_SELECTED_KEY, 0);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contact_list_fragment, container,
                false);
        contacts_list = (ListView) view.findViewById(R.id.contacts);
       /* clearableEdittext = (ClearableEditText) view
                .findViewById(R.id.search_text);
        clearableEdittext.setHint("Enter email/phone or Search");*/
        contacts_list.setAdapter(mAdapter);
        contacts_list.setOnItemClickListener(this);
        secondSearchLayout = (RelativeLayout) view.findViewById(R.id.layout_no_contacts);
       /* secondClearableEditText = (NewClearableCapsEditText) view
                .findViewById(R.id.editText2);
        secondClearableEditText.setHint("Type friend's name");
        clearableEdittext.doneIMEoption();
        secondClearableEditText.doneIMEoption();


        secondClearableEditText.setOnTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String searchText) {

            }

            @Override
            public void onClickKeyboardSearch(String searchText) {
                Log.e("Shwetha", "Done clicked");

                if (!searchText.isEmpty()) {

                    String firstText = clearableEdittext.getText().toString();
                    String email = "", num = "";

                    if (!firstText.matches("^[+]?[0-9]{10,13}$")) {
                        //Email
                        email = firstText;
                        num = "";
                    } else {
                        //Phone
                        num = firstText;
                        email = "";
                    }

                    newContactInteractLive(searchText, email, num);
                } else {
                    Toast.makeText(BaseActivity.mCurrentActivity, "friend name cannot be empty. please enter a name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // this.onTextChangedListener=(OnTextChangedListener) getActivity();
        clearableEdittext.setOnTextChangedListener(this);
        Log.d(Utils.TAG, "contacts number " + mAdapter.getCount());*/
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {


        final Cursor cursor = mAdapter.getCursor();
        Log.e("Dabkick_lib",
                "DISPLAY_NAME " + cursor.getString(ContactsQuery.DISPLAY_NAME));

        DabKick_Agent.checkInStatusMsg(
                "DISPLAY_NAME: "+cursor.getString(ContactsQuery.DISPLAY_NAME));

        DabKick_Agent.goToWaitingScreen(cursor.getString(ContactsQuery.DISPLAY_NAME));
        getActivity().finish();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // search.setOnTextChangedListener(onTextChangedListener);
        // Set up ListView, assign adapter and set some listeners. The adapter
        // was previously
        // created in onCreate().

		/*
         * contacts_list.setOnScrollListener(new AbsListView.OnScrollListener()
		 * {
		 *
		 * @Override public void onScrollStateChanged(AbsListView absListView,
		 * int scrollState) { // Pause image loader to ensure smoother scrolling
		 * when flinging if (scrollState ==
		 * AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
		 * mImageLoader.setPauseWork(true); } else {
		 * mImageLoader.setPauseWork(false); } }
		 *
		 * @Override public void onScroll(AbsListView absListView, int i, int
		 * i1, int i2) {} });
		 */

        if (mIsTwoPaneLayout) {
            // In a two-pane layout, set choice mode to single as there will be
            // two panes
            // when an item in the ListView is selected it should remain
            // highlighted while
            // the content shows in the second pane.
            contacts_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        // If there's a previously selected search item from a saved state then
        // don't bother
        // initializing the loader as it will be restarted later when the query
        // is populated into
        // the action bar search view (see onQueryTextChange() in
        // onCreateOptionsMenu()).
        if (mPreviouslySelectedSearchItem == 0) {
            // Initialize the loader, and create a loader identified by
            // ContactsQuery.QUERY_ID
            if (ContextCompat.checkSelfPermission(DabKick_Agent.mActivity,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        0);


            }
            else
                getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);



        try {
            // this.onTextChangedListener=(OnTextChangedListener) getActivity();
            // Assign callback listener which the holding activity must
            // implement. This is used
            // so that when a contact item is interacted with (selected by the
            // user) the holding
            // activity will be notified and can take further action such as
            // populating the contact
            // detail pane (if in multi-pane layout) or starting a new activity
            // with the contact
            // details (single pane layout).
            // mOnContactSelectedListener = (OnContactsInteractionListener)
            // activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnContactsInteractionListener");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // In the case onPause() is called during a fling the image loader is
        // un-paused to let any remaining background work complete.
        // mImageLoader.setPauseWork(false);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Called when ListView selection is cleared, for example when search mode
     * is finished and the currently selected contact should no longer be
     * selected.
     */
    private void onSelectionCleared() {
        // Uses callback to notify activity this contains this fragment
        mOnContactSelectedListener.onSelectionCleared();

        // Clears currently checked item
        contacts_list.clearChoices();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!TextUtils.isEmpty(mSearchTerm)) {
            // Saves the current search string
            outState.putString(SearchManager.QUERY, mSearchTerm);

            // Saves the currently selected contact
            outState.putInt(STATE_PREVIOUSLY_SELECTED_KEY,
                    contacts_list.getCheckedItemPosition());
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // If this is the loader for finding contacts in the Contacts Provider
        // (the only one supported)
        if (id == ContactsQuery.QUERY_ID) {
            Uri contentUri;

            // There are two types of searches, one which displays all contacts
            // and
            // one which filters contacts by a search query. If mSearchTerm is
            // set
            // then a search query has been entered and the latter should be
            // used.

            if (mSearchTerm == null) {
                // Since there's no search string, use the content URI that
                // searches the entire
                // Contacts table
                contentUri = ContactsQuery.CONTENT_URI;

                new CursorLoader(getActivity(), PhoneContactsQuery.CONTENT_URI,
                        PhoneContactsQuery.PROJECTION, PhoneContactsQuery.SELECTION, null,
                        PhoneContactsQuery.SORT_ORDER);

                return new CursorLoader(getActivity(), contentUri,
                        ContactsQuery.PROJECTION, ContactsQuery.SELECTION, null,
                        ContactsQuery.SORT_ORDER);
            } else {
                // Since there's a search string, use the special content Uri
                // that searches the
                // Contacts table. The URI consists of a base Uri and the search
                // string.

                if (!mSearchTerm.matches("^[+]?[0-9]{10,13}$")) {
                    contentUri = Uri.withAppendedPath(ContactsQuery.FILTER_URI,
                            Uri.encode(mSearchTerm));

                    return new CursorLoader(getActivity(), contentUri,
                            ContactsQuery.PROJECTION, ContactsQuery.SELECTION, null,
                            ContactsContract.Contacts.DISPLAY_NAME);
                } else {
                    contentUri = Uri.withAppendedPath(PhoneContactsQuery.FILTER_URI,
                            Uri.encode(mSearchTerm));

                    return new CursorLoader(getActivity(), contentUri,
                            PhoneContactsQuery.PROJECTION, PhoneContactsQuery.SELECTION, null,
                            ContactsContract.Contacts.DISPLAY_NAME);

                }
            }

            // Returns a new CursorLoader for querying the Contacts table. No
            // arguments are used
            // for the selection clause. The search string is either encoded
            // onto the content URI,
            // or no contacts search string is used. The other search criteria
            // are constants. See
            // the ContactsQuery interface.
            /*return new CursorLoader(getActivity(), contentUri,
                    ContactsQuery.PROJECTION, ContactsQuery.SELECTION, null,
                    ContactsQuery.SORT_ORDER);*/
        }

        Log.e(TAG, "onCreateLoader - incorrect ID provided (" + id + ")");
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // This swaps the new cursor into the adapter.
        if (loader.getId() == ContactsQuery.QUERY_ID) {
           /* if(!TextUtils.isEmpty(mSearchTerm)) {
                if (!mSearchTerm.matches("[a-zA-Z ]*"))
                    data = null;
            }
*/
            mAdapter.swapCursor(data);

            if (data.getCount() > 0) {
                //clearableEdittext.requestFocus();
                secondSearchLayout.setVisibility(View.INVISIBLE);
            }

            // If this is a two-pane layout and there is a search query then
            // there is some additional work to do around default selected
            // search item.
            if (mIsTwoPaneLayout && !TextUtils.isEmpty(mSearchTerm)
                    && mSearchQueryChanged) {
                // Selects the first item in results, unless this fragment has
                // been restored from a saved state (like orientation change)
                // in which case it selects the previously selected search item.
                if (data != null
                        && data.moveToPosition(mPreviouslySelectedSearchItem)) {
                    // Creates the content Uri for the previously selected
                    // contact by appending the
                    // contact's ID to the Contacts table content Uri
                    final Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
                            String.valueOf(data.getLong(ContactsQuery.ID)));
                    mOnContactSelectedListener.onContactSelected(uri);
                    contacts_list.setItemChecked(mPreviouslySelectedSearchItem,
                            true);
                } else {
                    // No results, clear selection.
                    onSelectionCleared();
                }
                // Only restore from saved state one time. Next time fall back
                // to selecting first item. If the fragment state is saved again
                // then the currently selected item will once again be saved.
                mPreviouslySelectedSearchItem = 0;
                mSearchQueryChanged = false;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            // When the loader is being reset, clear the cursor from the
            // adapter. This allows the
            // cursor resources to be freed.
            mAdapter.swapCursor(null);
        }
    }

    /**
     * Gets the preferred height for each item in the ListView, in pixels, after
     * accounting for screen density. ImageLoader uses this value to resize
     * thumbnail images to match the ListView item height.
     *
     * @return The preferred height in pixels, based on the current theme.
     */
    private int getListPreferredItemHeight() {
        final TypedValue typedValue = new TypedValue();

        // Resolve list item preferred height theme attribute into typedValue
        getActivity().getTheme().resolveAttribute(
                android.R.attr.listPreferredItemHeight, typedValue, true);

        // Create a new DisplayMetrics object
        final DisplayMetrics metrics = new DisplayMetrics();

        // Populate the DisplayMetrics
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);

        // Return theme value based on DisplayMetrics
        return (int) typedValue.getDimension(metrics);
    }

    /**
     * Decodes and scales a contact's image from a file pointed to by a Uri in
     * the contact's data, and returns the result as a Bitmap. The column that
     * contains the Uri varies according to the platform version.
     *
     * @return A Bitmap containing the contact's image, resized to fit the
     * provided image size. If no thumbnail exists, returns null.
     */

    private boolean onTextChange(String newText) {

        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

        if (mSearchTerm == null && newFilter == null) {
            return true;
        }

        if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
            return true;
        }
        mSearchTerm = newFilter;
        mSearchQueryChanged = true;
        getLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null,
                this);
        return true;
    }

    private class ContactsAdapter extends CursorAdapter implements
            SectionIndexer {
        private LayoutInflater mInflater; // Stores the layout inflater
        private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer
        // instance
        private TextAppearanceSpan highlightTextSpan; // Stores the highlight
        // text appearance style

        /**
         * Instantiates a new Contacts Adapter.
         *
         * @param context A context that has access to the app's layout.
         */
        public ContactsAdapter(Context context) {
            super(context, null, 0);

            // Stores inflater for use later
            mInflater = LayoutInflater.from(context);

            // Loads a string containing the English alphabet. To fully localize
            // the app, provide a
            // strings.xml file in res/values-<x> directories, where <x> is a
            // locale. In the file,
            // define a string with android:name="alphabet" and contents set to
            // all of the
            // alphabetic characters in the language in their proper sort order,
            // in upper case if
            // applicable.
            final String alphabet = context.getString(R.string.alphabet);

            // Instantiates a new AlphabetIndexer bound to the column used to
            // sort contact names.
            // The cursor is left null, because it has not yet been retrieved.
            mAlphabetIndexer = new AlphabetIndexer(null,
                    ContactsQuery.SORT_KEY, alphabet);

            // Defines a span for highlighting the part of a display name that
            // matches the search
            // string
            // highlightTextSpan = new TextAppearanceSpan(getActivity(),
            // R.style.searchTextHiglight);
        }

        /**
         * Identifies the start of the search string in the display name column
         * of a Cursor row. E.g. If displayName was "Adam" and search query
         * (mSearchTerm) was "da" this would return 1.
         *
         * @param displayName The contact display name.
         * @return The starting position of the search string in the display
         * name, 0-based. The method returns -1 if the string is not
         * found in the display name, or if the search string is empty
         * or null.
         */
        private int indexOfSearchQuery(String displayName) {
            if (!TextUtils.isEmpty(mSearchTerm)) {
                String input = mSearchTerm;
                input = input.replace(" ", "");
                if (!input.matches("^[+]?[0-9]{10,13}$")) {
                    return displayName.toLowerCase(Locale.getDefault()).indexOf(
                            mSearchTerm.toLowerCase(Locale.getDefault()));
                } else {
                    return 1;
                }
            }
            return -1;
        }

        /**
         * Overrides newView() to inflate the list item views.
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            // Inflates the list item layout.
            final View itemLayout = mInflater.inflate(
                    R.layout.contact_list_item, viewGroup, false);

            // Creates a new ViewHolder in which to store handles to each view
            // resource. This
            // allows bindView() to retrieve stored references instead of
            // calling findViewById for
            // each instance of the layout.
            final ViewHolder holder = new ViewHolder();
            holder.text1 = (TextView) itemLayout.findViewById(R.id.text1);
            holder.text2 = (TextView) itemLayout.findViewById(R.id.text2);

            // Stores the resourceHolder instance in itemLayout. This makes
            // resourceHolder
            // available to bindView and other methods that receive a handle to
            // the item view.
            itemLayout.setTag(holder);

            // Returns the item layout view
            return itemLayout;
        }

        /**
         * Binds data from the Cursor to the provided view.
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // Gets handles to individual view resources
            final ViewHolder holder = (ViewHolder) view.getTag();

            // For Android 3.0 and later, gets the thumbnail image Uri from the
            // current Cursor row.
            // For platforms earlier than 3.0, this isn't necessary, because the
            // thumbnail is
            // generated from the other fields in the row.
            final String photoUri = cursor
                    .getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);

            final String displayName = cursor
                    .getString(ContactsQuery.DISPLAY_NAME);

            final int startIndex = indexOfSearchQuery(displayName);

            /*if (startIndex == -1) {
                    if (TextUtils.isEmpty(mSearchTerm)) {
                        // If the search search is empty, hide the second line of
                        // text
                        holder.text1.setText(displayName);
                        count++;
                        Log.d(Utils.TAG, count + "name " + displayName);
                        holder.text2.setVisibility(View.GONE);
                    }else{
                        //if(!displayName.matches("[a-zA-Z]*")) {

                            String s1 = Normalizer.normalize(displayName, Normalizer.Form.NFKD);
                            String regex = Pattern.quote("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}" +
                                    "\\p{IsCc}\\p{IsCf}\\p{IsLl}\\p{IsLo}\\p{IsLt}\\p{IsLu}\\p{IsMc}\\p{IsPf}" +
                                    "\\p{IsMe}\\p{IsMn}\\p{IsNd}\\p{IsNi}\\p{IsNo}\\p{IsPc}\\p{IsPd}\\p{IsPe}" +
                                    "\\p{IsPi}\\p{IsPo}\\p{IsPs}\\p{IsSc}\\p{IsSm}\\p{IsSo}\\p{IsZi}\\p{IsZp}" +
                                    "\\p{IsZs}]\\+");

                            try {
                                String s2 = new String(s1.replaceAll(regex, "").getBytes("ascii"), "ascii");
                                s2 = s2.replaceAll("\\?", "");
                                if (s2.toLowerCase(Locale.getDefault()).contains(mSearchTerm.toLowerCase(Locale.getDefault()))) {
                                    holder.text1.setText(displayName);
                                }
                            }catch (UnsupportedEncodingException e){

                            }

                        //}else {
                            if (displayName.contains(mSearchTerm)) {
                                holder.text1.setText(displayName);
                            }
                        //}
                    }
            } else {*/
            // If the search string matched the display name, applies a
            // SpannableString to
            // highlight the search string with the displayed display name

            // Wraps the display name in the SpannableString
            final SpannableString highlightedName = new SpannableString(
                    displayName);

               /* if(!mSearchTerm.matches("^[+]?[0-9]{10,13}$")) {
                    // Sets the span to start at the starting point of the match and
                    // end at "length"
                    // characters beyond the starting point
                    highlightedName.setSpan(highlightTextSpan, startIndex,
                            startIndex + mSearchTerm.length(), 0);
                }*/

            // Binds the SpannableString to the display name View object
            holder.text1.setText(highlightedName);
            // Since the search string matched the name, this hides the
            // secondary message
            holder.text2.setVisibility(View.GONE);
            //}

            // Processes the QuickContactBadge. A QuickContactBadge first
            // appears as a contact's
            // thumbnail image with styling that indicates it can be touched for
            // additional
            // information. When the user clicks the image, the badge expands
            // into a dialog box
            // containing the contact's details and icons for the built-in apps
            // that can handle
            // each detail type.

            // Generates the contact lookup Uri
            final Uri contactUri = ContactsContract.Contacts.getLookupUri(
                    cursor.getLong(ContactsQuery.ID),
                    cursor.getString(ContactsQuery.LOOKUP_KEY));

            // Binds the contact's lookup Uri to the QuickContactBadge
            // holder.icon.assignContactUri(contactUri);

            // Loads the thumbnail image pointed to by photoUri into the
            // QuickContactBadge in a
            // background worker thread
            // mImageLoader.loadImage(photoUri, holder.icon);
        }

        /**
         * Overrides swapCursor to move the new Cursor into the AlphabetIndex as
         * well as the CursorAdapter.
         */
        @Override
        public Cursor swapCursor(Cursor newCursor) {
            // Update the AlphabetIndexer with new cursor as well
            mAlphabetIndexer.setCursor(newCursor);
            return super.swapCursor(newCursor);
        }

        /**
         * An override of getCount that simplifies accessing the Cursor. If the
         * Cursor is null, getCount returns zero. As a result, no test for
         * Cursor == null is needed.
         */
        @Override
        public int getCount() {
            if (getCursor() == null) {
                return 0;
            }
            return super.getCount();
        }

        /**
         * Defines the SectionIndexer.getSections() interface.
         */
        @Override
        public Object[] getSections() {
            return mAlphabetIndexer.getSections();
        }

        /**
         * Defines the SectionIndexer.getPositionForSection() interface.
         */
        @Override
        public int getPositionForSection(int i) {
            if (getCursor() == null) {
                return 0;
            }
            return mAlphabetIndexer.getPositionForSection(i);
        }

        /**
         * Defines the SectionIndexer.getSectionForPosition() interface.
         */
        @Override
        public int getSectionForPosition(int i) {
            if (getCursor() == null) {
                return 0;
            }
            return mAlphabetIndexer.getSectionForPosition(i);
        }

        /**
         * A class that defines fields for each resource ID in the list item
         * layout. This allows ContactsAdapter.newView() to store the IDs once,
         * when it inflates the layout, instead of calling findViewById in each
         * iteration of bindView.
         */
        private class ViewHolder {
            TextView text1;
            TextView text2;

        }
    }

    /**
     * This interface must be implemented by any activity that loads this
     * fragment. When an interaction occurs, such as touching an item from the
     * ListView, these callbacks will be invoked to communicate the event back
     * to the activity.
     */
    public interface OnContactsInteractionListener {
        /**
         * Called when a contact is selected from the ListView.
         *
         * @param contactUri The contact Uri.
         */
        public void onContactSelected(Uri contactUri);

        /**
         * Called when the ListView selection is cleared like when a contact
         * search is taking place or is finishing.
         */
        public void onSelectionCleared();
    }

    /**
     * This interface defines constants for the Cursor and CursorLoader, based
     * on constants defined in the
     * {@link android.provider.ContactsContract.Contacts} class.
     */
    public interface ContactsQuery {

        // An identifier for the loader
        final static int QUERY_ID = 1;

        // A content URI for the Contacts table
        final static Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;

        // The search/filter query Uri
        final static Uri FILTER_URI = ContactsContract.Contacts.CONTENT_FILTER_URI;

        // The selection clause for the CursorLoader query. The search criteria
        // defined here
        // restrict results to contacts that have a display name and are linked
        // to visible groups.
        // Notice that the search on the string provided by the user is
        // implemented by appending
        // the search string to CONTENT_FILTER_URI.
        @SuppressLint("InlinedApi")
        final static String SELECTION = (hasHoneycomb() ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                : ContactsContract.Contacts.DISPLAY_NAME)
                + "<>''" + " AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1";

        // The desired sort order for the returned Cursor. In Android 3.0 and
        // later, the primary
        // sort key allows for localization. In earlier versions. use the
        // display name as the sort
        // key.
        @SuppressLint("InlinedApi")
        final static String SORT_ORDER = hasHoneycomb() ? ContactsContract.Contacts.SORT_KEY_PRIMARY
                : ContactsContract.Contacts.DISPLAY_NAME;

        // The projection for the CursorLoader query. This is a list of columns
        // that the Contacts
        // Provider should return in the Cursor.
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {

                // The contact's row id
                ContactsContract.Contacts._ID,

                // A pointer to the contact that is guaranteed to be more
                // permanent than _ID. Given
                // a contact's current _ID value and LOOKUP_KEY, the Contacts
                // Provider can generate
                // a "permanent" contact URI.
                ContactsContract.Contacts.LOOKUP_KEY,

                // In platform version 3.0 and later, the Contacts table
                // contains
                // DISPLAY_NAME_PRIMARY, which either contains the contact's
                // displayable name or
                // some other useful identifier such as an email address. This
                // column isn't
                // available in earlier versions of Android, so you must use
                // Contacts.DISPLAY_NAME
                // instead.
                hasHoneycomb() ? ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
                        : ContactsContract.Contacts.DISPLAY_NAME,

                // In Android 3.0 and later, the thumbnail image is pointed to
                // by
                // PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct
                // pointer; instead,
                // you generate the pointer from the contact's ID value and
                // constants defined in
                // android.provider.ContactsContract.Contacts.
                hasHoneycomb() ? ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
                        : ContactsContract.Contacts._ID,

                // The sort order column for the returned Cursor, used by the
                // AlphabetIndexer
                SORT_ORDER,};

        // The query column numbers which map to each value in the projection
        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int PHOTO_THUMBNAIL_DATA = 3;
        final static int SORT_KEY = 4;
    }

    public interface PhoneContactsQuery {

        // An identifier for the loader
        final static int QUERY_ID = 1;

        // A content URI for the Contacts table
        final static Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // The search/filter query Uri
        final static Uri FILTER_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI;

        // The selection clause for the CursorLoader query. The search criteria
        // defined here
        // restrict results to contacts that have a display name and are linked
        // to visible groups.
        // Notice that the search on the string provided by the user is
        // implemented by appending
        // the search string to CONTENT_FILTER_URI.
        @SuppressLint("InlinedApi")
        final static String SELECTION = (hasHoneycomb() ? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY
                : ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                + "<>''" + " AND " + ContactsContract.CommonDataKinds.Phone.IN_VISIBLE_GROUP + "=1";

        // The desired sort order for the returned Cursor. In Android 3.0 and
        // later, the primary
        // sort key allows for localization. In earlier versions. use the
        // display name as the sort
        // key.
        @SuppressLint("InlinedApi")
        final static String SORT_ORDER = hasHoneycomb() ? ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY
                : ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        // The projection for the CursorLoader query. This is a list of columns
        // that the Contacts
        // Provider should return in the Cursor.
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {

                // The contact's row id
                ContactsContract.CommonDataKinds.Phone._ID,

                // A pointer to the contact that is guaranteed to be more
                // permanent than _ID. Given
                // a contact's current _ID value and LOOKUP_KEY, the Contacts
                // Provider can generate
                // a "permanent" contact URI.
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,

                // In platform version 3.0 and later, the Contacts table
                // contains
                // DISPLAY_NAME_PRIMARY, which either contains the contact's
                // displayable name or
                // some other useful identifier such as an email address. This
                // column isn't
                // available in earlier versions of Android, so you must use
                // Contacts.DISPLAY_NAME
                // instead.
                hasHoneycomb() ? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY
                        : ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,

                // In Android 3.0 and later, the thumbnail image is pointed to
                // by
                // PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct
                // pointer; instead,
                // you generate the pointer from the contact's ID value and
                // constants defined in
                // android.provider.ContactsContract.Contacts.
                ContactsContract.CommonDataKinds.Phone.NUMBER,

                // The sort order column for the returned Cursor, used by the
                // AlphabetIndexer
                SORT_ORDER,};

        // The query column numbers which map to each value in the projection
        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int PHOTO_THUMBNAIL_DATA = 3;
        final static int SORT_KEY = 4;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
                    getLoaderManager().restartLoader(ContactsQuery.QUERY_ID, null, this);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

}
