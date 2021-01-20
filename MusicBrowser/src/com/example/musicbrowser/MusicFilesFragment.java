package com.example.musicbrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class MusicFilesFragment extends ListFragment {
	
	MusicListSelectionListener mListener = null;
	int mCurrIdx = 0;
	String currFilePath;
	ArrayList<String> Target2;
    File[] filesInOrder;
	File[] filesInOrderWithOffset;
	List<String> filesPathInOrder;
	List<String> filesPathInOrderWithOffset;
	ArrayList<String> filesNameAsArray;
	ArrayList<String> filesNameAsArrayWithOffset;
	ListView listView;
	View v;
	ListViewAdapter listViewAdapter;
	
	public interface MusicListSelectionListener {
		public void onMusicListClick(int pos, List<String> musicsFilePath, ArrayList<String> filesName, boolean playNext);
		public void onMusicListSelection(String str1, String str2);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mListener = (MusicListSelectionListener) activity;
		} 
		catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setHasOptionsMenu(true);
//		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		listView = (ListView) v.findViewById(R.id.list);
		listView.setOnItemSelectedListener(mOnSelectedListener);
//		setSelection(mCurrIdx);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return v = inflater.inflate(R.layout.list, container, false);
    }
	
	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		mCurrIdx = pos;
		listView.setItemChecked(pos, true);
		
		mListener.onMusicListClick(pos, filesPathInOrder, filesNameAsArray, true);
	}
	
	private AdapterView.OnItemSelectedListener mOnSelectedListener = new AdapterView.OnItemSelectedListener() {
		
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
			mCurrIdx = pos;
			mListener.onMusicListSelection("str3", "str4");
        }

        public void onNothingSelected(AdapterView<?> parent) {
     
        }
    };

	public void loadMusicFilesName(String currFilePath) {
		
		this.currFilePath = currFilePath;
		filesInOrder = FileUtils.findPlayableInFiles(FileUtils.FindFiles(currFilePath, getResources()));
		filesPathInOrder = FileUtils.FindFilesPath(filesInOrder);
	    filesNameAsArray = new ArrayList<String>(Arrays.asList(FileUtils.getAllFileName(filesInOrder)));
    	listViewAdapter = new ListViewAdapter(getActivity(), filesNameAsArray, screenHeight);
    	listView.setAdapter(listViewAdapter);
    	listViewAdapter.notifyDataSetChanged();
	}
	
	int screenHeight;
	
    public void getScreenHeight(int screenHeight) {
    	this.screenHeight = screenHeight;
    }
	
//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.music_files_menu, menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		
//			case R.id.music_files_menu_item:
//				Toast.makeText(getActivity().getApplicationContext(),
//						"This action provided by the MusicFilesFragment",
//						Toast.LENGTH_SHORT).show();
//				return true;
//			default:
//				return super.onOptionsItemSelected(item);
//		}
//	}
}