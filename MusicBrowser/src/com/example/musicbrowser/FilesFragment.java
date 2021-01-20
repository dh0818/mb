package com.example.musicbrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

public class FilesFragment extends ListFragment implements ListView.OnScrollListener{
	
	FilesListSelectionListener mListener = null;
    File[] filesInOrder;
	File[] filesInOrderWithOffset;
	List<String> filesPathInOrder;
	List<String> filesPathInOrderWithOffset;
	ArrayList<String> filesNameAsArray;
	ArrayList<String> filesNameAsArrayWithOffset;
	int mCurrIdx = 0;
	String dir = NameHelper.ROOT_DIR;
    String prevDir;
	FragmentManager fm;
	ListView listView;
	View v, v2;
    String newDir;
    String parentFilePath;
	ListViewAdapter listViewAdapter;
	
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		listView = (ListView) v.findViewById(R.id.list);
		listView.setOnItemSelectedListener(mOnSelectedListener);
		listView.setOnScrollListener(this);
		loadDirectoryFilesName(NameHelper.ROOT_DIR);
//		setSelection(mCurrIdx);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return v = inflater.inflate(R.layout.list, container, false);
    }

    public void loadDirectoryFilesName(String dirPath) {
		filesInOrder = FileUtils.findDirectoryInFiles(FileUtils.FindFiles(dirPath, getResources()));
		filesInOrderWithOffset = FileUtils.appendFiles2ToFiles1(new File[1], filesInOrder);
		
		filesPathInOrder = FileUtils.FindFilesPath(filesInOrder);
		filesPathInOrderWithOffset = filesPathInOrder;
		filesPathInOrderWithOffset.add(0, null);
		
        filesNameAsArray = new ArrayList<String>(Arrays.asList(FileUtils.getAllFileName(filesInOrder)));
        filesNameAsArrayWithOffset = filesNameAsArray;
        filesNameAsArrayWithOffset.add(0, "BACK");
        
    	listViewAdapter = new ListViewAdapter(getActivity(), filesNameAsArrayWithOffset, screenHeight);
    	listView.setAdapter(listViewAdapter);
    	listViewAdapter.notifyDataSetChanged();
    }
    
    int screenHeight;
    
    public void getScreenHeight(int screenHeight) {
    	this.screenHeight = screenHeight;
    }
    
    String path;
    
	private AdapterView.OnItemSelectedListener mOnSelectedListener = new AdapterView.OnItemSelectedListener() {
		
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
			mCurrIdx = pos;

			if(id == 0) {
				mListener.onFilesListSelection(dir, "", "");
			}
			else {
				if(filesInOrderWithOffset[pos] != null)
					path = filesInOrderWithOffset[pos].getName();
				
				mListener.onFilesListSelection(dir + "/" + path, "", "");
			}
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    
	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {
		mCurrIdx = pos;
		listView.setItemChecked(pos, true);
		if(id == 0) {
			
			if(filesInOrderWithOffset.length < 2) {
				loadDirectoryFilesName(prevDir);
				dir = prevDir;
			}
			else {
				parentFilePath = filesInOrderWithOffset[1].getParentFile().getParent();
				
				try{
					prevDir = filesInOrderWithOffset[1].getParentFile().getParentFile().getParent();
				}
				catch(Exception e){
					prevDir = parentFilePath;
				}
				
				try{
					loadDirectoryFilesName(parentFilePath);
					dir = parentFilePath;
				}
				catch(Exception e){
					parentFilePath = dir;
				}
			}
		}
		else{
			prevDir = dir;
			dir = filesPathInOrderWithOffset.get(pos);
			loadDirectoryFilesName(dir);
		}
		
		mListener.onFilesListClick(pos, dir, FileUtils.findPlayableInFiles(FileUtils.FindFiles(dir, getResources())).length, prevDir, id, FileUtils.findPlayableInFiles(FileUtils.FindFiles(dir, getResources())));
		mListener.onFilesListSelection(dir, "", "");
	}
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setHasOptionsMenu(true);
//		setRetainInstance(true);
	}

//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.files_menu, menu);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		
//			case R.id.files_menu_item:
//				Toast.makeText(getActivity().getApplicationContext(),
//						"This action provided by the FilesFragment",
//						Toast.LENGTH_SHORT).show();
//				return true;
//			default:
//				return super.onOptionsItemSelected(item);
//		}
//	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mListener = (FilesListSelectionListener) activity;
		} 
		catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
		}
	}
	
	public interface FilesListSelectionListener {
		public void onFilesListClick(int index, String currFilePath, int pos, String prevDir, long id, File[] files);
		public void onFilesListSelection(String dir, String str1, String str2);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}