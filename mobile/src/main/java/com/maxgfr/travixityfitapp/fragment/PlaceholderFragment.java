package com.maxgfr.travixityfitapp.fragment;

/**
 * Created by maxime on 16-May-17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.maxgfr.travixityfitapp.R;
import com.maxgfr.travixityfitapp.fit.FitLab;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static int selection;

    private ListView listeView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FitLab lab;

    private ArrayAdapter<String> adapter,adapter2;

    public PlaceholderFragment() {
        FitLab.getInstance();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listeView = (ListView) rootView.findViewById(R.id.content);

        //TheRefreshLayout : the button to reload
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById (R.id.swiperefresh);

        selection = this.getArguments().getInt(ARG_SECTION_NUMBER);

        /*
        * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
        * performs a swipe-to-refresh gesture.
        */
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("RefreshButton", "onRefresh called from SwipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        refreshContent();
                    }
                });

        lab = FitLab.getInstance();

        switch (selection) {
            case 1:
                adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, android.R.id.text1, lab.getActivityRecognition());
                listeView.setAdapter(adapter);
                break;

            case 2:
                adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, android.R.id.text1, lab.getStepActivity());
                listeView.setAdapter(adapter2);
                break;

            default:
                Log.e("TAG", "Section inconnue: " + getArguments().getInt(ARG_SECTION_NUMBER));
        }
        return rootView;
    }

    private void refreshContent () {
        selection = this.getArguments().getInt(ARG_SECTION_NUMBER);
        switch (selection) {
            case 1:
                adapter.notifyDataSetChanged();
                listeView.setAdapter(adapter);
                /*for (String s : lab.getActivityRecognition()) { //TEST
                    System.out.println(s);
                }*/
                mSwipeRefreshLayout.setRefreshing(false);
                break;

            case 2:
                adapter2.notifyDataSetChanged();
                listeView.setAdapter(adapter2);
                /*for (String s : lab.getStepActivity()) { //TEST
                    System.out.println(s);
                }*/
                mSwipeRefreshLayout.setRefreshing(false);
                break;

            default:
                Log.e("TAG", "Section inconnue: " + getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}