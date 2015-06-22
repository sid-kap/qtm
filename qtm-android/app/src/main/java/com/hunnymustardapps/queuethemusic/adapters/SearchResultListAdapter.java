package com.hunnymustardapps.queuethemusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hunnymustardapps.queuethemusic.R;

import java.util.List;

public class SearchResultListAdapter extends ArrayAdapter<String> {

    private Context _context;
    private List<String> _searchResults;

    public SearchResultListAdapter(Context context, List<String> searchResults) {
        super(context, R.layout.list_search_results, searchResults);
        _context = context;
        _searchResults = searchResults;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View resultView = inflater.inflate(R.layout.list_search_results, parent, false);

        TextView songName = (TextView) resultView.findViewById(R.id.list_search_result_song_name);
        songName.setText(_searchResults.get(position));
        return resultView;
    }
}
