package com.example.musicbrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.musicbrowser.FilesFragment.FilesListSelectionListener;
import com.example.musicbrowser.MusicFilesFragment.MusicListSelectionListener;

public class MusicBrowserActivity extends Activity implements FilesListSelectionListener, MusicListSelectionListener, OnClickListener {

	MediaPlayer mediaPlayer;
	FragmentManager fm;
	FilesFragment mFilesFragment = new FilesFragment();
	MusicFilesFragment mMusicFilesFragment = new MusicFilesFragment();
	MediaPlayerFragment mMediaPlayerFragment = new MediaPlayerFragment();
	TextView file_path_title, file_path;
	TextView number_of_songs_title, numberOfSongs, play_list_title;
	FragmentTransaction ftInOnActivityCreated, fragmentTransaction2;
	String musicFilePath;
	View hori_seperator_in_second_panel, hori_seperator_in_first_panel, vert_seperator;
	LinearLayout first_panel, second_panel, third_panel, file_path_info;
	Button add_to_play_list_button, button1, button2, button3;
	ListView list_in_activity;
	ArrayList<String> filesName = new ArrayList<String>();
	ArrayList<String> filesPath = new ArrayList<String>();
    String file1_path, file1_name, file2_path, file2_name, file3_path, file3_name;
    String filePath1 = null, filePath2 = null, filePath3 = null;
    SharedPreferences prefs;
    private static String FILE1_PATH = "file1_path";
    private static String FILE2_PATH = "file2_path";
    private static String FILE3_PATH = "file3_path";
    private static String FILE1_NAME = "file1_name";
    private static String FILE2_NAME = "file2_name";
    private static String FILE3_NAME = "file3_name";
	ArrayList<String> filesNameAsArrayWithOffset = new ArrayList<String>();
	ListViewAdapter listViewAdapter;
    private IntentFilter usbFilter;
    String currFilePath = NameHelper.ROOT_DIR;
    ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_music_browser);
        findListView();
        findTextView();
        findView();
        findLinearLayout();
        findFragmentManager();
        findMediaPlayer();
        findButton();
        findProgressDialog();
	}
	
    public void init() {
    }
    
    public void findListView() {
    	list_in_activity = (ListView)this.findViewById(R.id.list_in_activity);
    	list_in_activity.setOnItemSelectedListener(mOnSelectedListener);
    	list_in_activity.setOnItemClickListener(onItemClickListener);
    }
    
    public void findButton() {
    	String empty = getResources().getString(R.string.empty);
    	String remove = getResources().getString(R.string.remove);
    	
        add_to_play_list_button = (Button)this.findViewById(R.id.add_to_play_list_button);
        add_to_play_list_button.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScreenHeight()*0.020f);
        add_to_play_list_button.setOnClickListener(this);
        
        button1 = (Button)this.findViewById(R.id.button1);
        button1.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScreenHeight()*0.020f);
        button1.setOnClickListener(this);
        
        if(filesPath.get(0)!= null)
        	button1.setText(remove + "\n" + filesName.get(0).toUpperCase(Locale.getDefault()));
        else
        	button1.setText(empty);
        
        button2 = (Button)this.findViewById(R.id.button2);
        button2.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScreenHeight()*0.020f);
        button2.setOnClickListener(this);
        
        if(filesPath.get(1)!= null)
        	button2.setText(remove + "\n" + filesName.get(1).toUpperCase(Locale.getDefault()));
        else
        	button2.setText(empty);
        
        button3 = (Button)this.findViewById(R.id.button3);
        button3.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScreenHeight()*0.020f);
        button3.setOnClickListener(this);
        
        if(filesPath.get(2)!= null)
        	button3.setText(remove + "\n" + filesName.get(2).toUpperCase(Locale.getDefault()));
        else
        	button3.setText(empty);
    }
    
    public void findTextView() {
    	file_path_title = (TextView)this.findViewById(R.id.file_path_title);
    	file_path_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScreenHeight()*0.035f);
    	
        file_path = (TextView)this.findViewById(R.id.file_path);
		file_path.setText(NameHelper.ROOT_DIR);
		file_path.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScreenHeight()*0.030f);
		
		number_of_songs_title = (TextView)this.findViewById(R.id.number_of_songs_title);
		number_of_songs_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScreenHeight()*0.035f);
		
		numberOfSongs = (TextView)this.findViewById(R.id.number_of_songs);
		numberOfSongs.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScreenHeight()*0.030f);
		
		play_list_title = (TextView)this.findViewById(R.id.play_list_title);
		play_list_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, getScreenHeight()*0.035f);
		
		prefs = getPreferences(MODE_PRIVATE);
		filesPath.add(prefs.getString(FILE1_PATH, null));
		filesPath.add(prefs.getString(FILE2_PATH, null));
		filesPath.add(prefs.getString(FILE3_PATH, null));
		filesName.add(prefs.getString(FILE1_NAME, ""));
		filesName.add(prefs.getString(FILE2_NAME, ""));
		filesName.add(prefs.getString(FILE3_NAME, ""));
		
		loadFilesName(filesName);
//		checkResult();
    }
    
    public void findView() {
    	hori_seperator_in_second_panel = (View)this.findViewById(R.id.hori_seperator_in_second_panel);
    	hori_seperator_in_first_panel = (View)this.findViewById(R.id.hori_seperator_in_first_panel);
    	vert_seperator = (View)this.findViewById(R.id.vert_seperator);
    }
    
    public void findFragmentManager() {
    	fm = getFragmentManager();
		ftInOnActivityCreated = fm.beginTransaction();
		ftInOnActivityCreated.add(R.id.files_fragment, mFilesFragment);
		ftInOnActivityCreated.add(R.id.musics_fragment, mMusicFilesFragment);
		ftInOnActivityCreated.hide(mMusicFilesFragment);
		ftInOnActivityCreated.add(R.id.media_palyer_fragment, mMediaPlayerFragment);
		ftInOnActivityCreated.hide(mMediaPlayerFragment);
		ftInOnActivityCreated.commit();
		
		mMediaPlayerFragment.getLayoutForExtend(first_panel, second_panel, mMusicFilesFragment, mFilesFragment, vert_seperator);

		mFilesFragment.getScreenHeight(getScreenHeight());
		mMusicFilesFragment.getScreenHeight(getScreenHeight());
		mMediaPlayerFragment.getScreenHeight(getScreenHeight());
    }
    
    public void findMediaPlayer() {
    	mMediaPlayerFragment.getContext(this);
    }
    
    public void findProgressDialog() {
    	progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Waiting for the renew of menu...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setIndeterminate(true);
		
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

//            @Override
//            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
//                    return true;
//                }
//                else if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//                	return true;
//                }
        	
//                return false;
//            }
            
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                }
                else if(keyCode == KeyEvent.KEYCODE_BACK) {
                	return true;
                }
                
                return false;
            }
        });
    }
    
    public void findLinearLayout() {
    	first_panel = (LinearLayout)this.findViewById(R.id.first_panel);
    	second_panel = (LinearLayout)this.findViewById(R.id.second_panel);
    	third_panel = (LinearLayout)this.findViewById(R.id.third_panel);
    	file_path_info = (LinearLayout)this.findViewById(R.id.file_path_info);
    }
  
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		usbFilter = new IntentFilter("android.hardware.usb.action.USB_STATE");
		usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(USBReceiver, usbFilter);

		super.onResume();
    }
	
	boolean usb_connect;
	
    private BroadcastReceiver USBReceiver = new BroadcastReceiver() {
    	
        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	String action = intent.getAction();
        	
	        if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
	            usb_connect = true;
	            renewMenuAfterSomeTime(900);
	        }
	        else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
	        	usb_connect = false;
    			if(mMediaPlayerFragment.mediaPlayer != null) {
		    		mMediaPlayerFragment.myHandler.removeCallbacks(mMediaPlayerFragment.UpdateSongTimeRunnable);
		    		mMediaPlayerFragment.myHandler.removeCallbacks(mMediaPlayerFragment.UpdateSongInfoRunnable);
    				mMediaPlayerFragment.mediaPlayer.release();
    			}
    			renewMenuAfterSomeTime(900);
	        }
        }
    };
	
	public void renewMenuAfterSomeTime(final int time){

		progressDialog.show();
		
		final Thread t = new Thread(){
	
			@Override
			public void run(){
	 
				int jumpTime = 0;
				
				while(jumpTime < time){
					
					try {
						sleep(500);
						jumpTime += 100;
						progressDialog.setProgress(jumpTime);
						
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				progressDialog.dismiss();
				
				MusicBrowserActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mFilesFragment.loadDirectoryFilesName(NameHelper.ROOT_DIR);
					}
				});
			}
		};
		
		t.start();
	}
    
//    public void showProgressBarToast() {
//        Toast toast = new Toast(getApplicationContext());
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        toast.setDuration(Toast.LENGTH_LONG);
//        toast.setView(getLayoutInflater().inflate(R.layout.progressbar_toast, null));
//        toast.show();
//    }
    
	@Override
	public void onFilesListClick(int fileIndex, String currFilePath, int pos, String prevDir, long id, File[] files) {
		this.currFilePath = currFilePath;
		
		file_path.setText(currFilePath);
		mMusicFilesFragment.loadMusicFilesName(currFilePath);
	}
	
	@Override
	public void onFilesListSelection(String dir, String str1, String str2) {
		browserMode(dir);
	}
	
	public void setMusicListInfo(String dir) {
	   	String info;
	   	String audio_file = getResources().getString(R.string.audio_file);
	   	String audio_files = getResources().getString(R.string.audio_files);
	   	
    	if(FileUtils.findPlayableInFiles(FileUtils.FindFiles(dir, getResources())).length == 1)
    		info = FileUtils.findPlayableInFiles(FileUtils.FindFiles(dir, getResources())).length + " " + audio_file;
    	else	
    		info = FileUtils.findPlayableInFiles(FileUtils.FindFiles(dir, getResources())).length + " " + audio_files;
    	
    	numberOfSongs.setText(info);
	}
	
	public void browserMode(String dir) {
        FragmentTransaction ftInInterface = fm.beginTransaction();
 
        if(FileUtils.findPlayableInFiles(FileUtils.FindFiles(dir, getResources())).length > 0) {
        	ftInInterface.show(mMusicFilesFragment);
        	ftInInterface.show(mMediaPlayerFragment);
        	hori_seperator_in_second_panel.setVisibility(View.VISIBLE);
//        	vert_seperator_betw_second_and_third_panel.setVisibility(View.VISIBLE);
        	number_of_songs_title.setVisibility(View.VISIBLE);
        	numberOfSongs.setVisibility(View.VISIBLE);
        	setMusicListInfo(dir);
        }
        else if(mMusicFilesFragment.isAdded()) {
        	ftInInterface.hide(mMusicFilesFragment);
        	ftInInterface.hide(mMediaPlayerFragment);
        	hori_seperator_in_second_panel.setVisibility(View.GONE);
//        	vert_seperator_betw_second_and_third_panel.setVisibility(View.GONE);
        	number_of_songs_title.setVisibility(View.GONE);
        	numberOfSongs.setVisibility(View.GONE);
        }
        
        ftInInterface.commit();
		mMusicFilesFragment.loadMusicFilesName(dir);
	}
	
	Item item;
	
	@Override
	public void onMusicListClick(int pos, List<String> musicsFilePath, ArrayList<String> filesName, boolean playNext) {
		mMediaPlayerFragment.getAudioItem(musicsFilePath.get(pos));
		mMediaPlayerFragment.playFromFilePath(pos, musicsFilePath, playNext);
	}
	
	@Override
	public void onMusicListSelection(String str1, String str2) {
	}
	
    private int getScreenHeight()
    {
    	Display display = getWindowManager().getDefaultDisplay();
    	Point size = new Point();
    	display.getSize(size);
    	int height = size.y;
    	
    	return height;
    }
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.activity_menu, menu);
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		FragmentTransaction transaction = getFragmentManager().beginTransaction();
//		transaction.replace(R.id.first_panel, mFilesPlayListFragment);
//		transaction.addToBackStack(null);
//		transaction.commit();
		
//		Toast.makeText(getApplicationContext(), "This action provided by MusicBrowserActivity", Toast.LENGTH_SHORT).show();
//		return true;
		
//		switch (item.getItemId()) {
//			case R.id.activity_menu_item:
//				Toast.makeText(getApplicationContext(), "This action provided by MusicBrowserActivity", Toast.LENGTH_SHORT).show();
//				return true;
//			default:
//				return super.onOptionsItemSelected(item);
//		}
//	}
	
	@Override
	protected void onStop() {
//		if(mMediaPlayerFragment.mediaPlayer != null) {
//    		mMediaPlayerFragment.myHandler.removeCallbacks(mMediaPlayerFragment.UpdateSongTimeRunnable);
//    		mMediaPlayerFragment.myHandler.removeCallbacks(mMediaPlayerFragment.UpdateSongInfoRunnable);
//			mMediaPlayerFragment.mediaPlayer.release();
//		}
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
//		if(mMediaPlayerFragment.mediaPlayer != null) {
//    		mMediaPlayerFragment.myHandler.removeCallbacks(mMediaPlayerFragment.UpdateSongTimeRunnable);
//    		mMediaPlayerFragment.myHandler.removeCallbacks(mMediaPlayerFragment.UpdateSongInfoRunnable);
//			mMediaPlayerFragment.mediaPlayer.release();
//		}
		
		unregisterReceiver(USBReceiver);
		
		super.onDestroy();
	}
    
	@Override
	public void onBackPressed() {
		
		String leave_message = getResources().getString(R.string.leave_message);
		String app_name = getResources().getString(R.string.app_name);
		String yes = getResources().getString(R.string.yes);
		String no = getResources().getString(R.string.no);
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setTitle(app_name);
		alertDialog.setMessage(leave_message);
		
		DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {
			
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	
    			if(mMediaPlayerFragment.mediaPlayer != null) {
		    		mMediaPlayerFragment.myHandler.removeCallbacks(mMediaPlayerFragment.UpdateSongTimeRunnable);
		    		mMediaPlayerFragment.myHandler.removeCallbacks(mMediaPlayerFragment.UpdateSongInfoRunnable);
    				mMediaPlayerFragment.mediaPlayer.release();
    			}
    			
	    		finish();
	        }
	    };
		
		DialogInterface.OnClickListener dialogInterface2 = new DialogInterface.OnClickListener() {
			
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        }
	    };
	    
		alertDialog.setPositiveButton(yes, dialogInterface);
		alertDialog.setNegativeButton(no, dialogInterface2);
		alertDialog.show();
	}
	
//	public void checkResult() {
//		Log.e("FILE1_PATH", prefs.getString(FILE1_PATH, "null"));
//		Log.e("FILE2_PATH", prefs.getString(FILE2_PATH, "null"));
//		Log.e("FILE3_PATH", prefs.getString(FILE3_PATH, "null"));
//		Log.e("FILE1_NAME", prefs.getString(FILE1_NAME, ""));
//		Log.e("FILE2_NAME", prefs.getString(FILE2_NAME, ""));
//		Log.e("FILE3_NAME", prefs.getString(FILE3_NAME, ""));
//	}
	
	String curFilePath;
	
	@Override
	public void onClick(View v) {
		SharedPreferences.Editor editor = prefs.edit();
		String empty = getResources().getString(R.string.empty);
		
		switch(v.getId()){
		case R.id.add_to_play_list_button:
			getCurFilePath("" + file_path.getText());
			break;
		case R.id.button1:
			filesPath.set(0, null);
			filesName.set(0, "");
			editor.putString(FILE1_PATH, null);
			editor.putString(FILE1_NAME, "");
			editor.commit();
			loadFilesName(filesName);
			button1.setText(empty);
//			checkResult();
			break;
		case R.id.button2:
			filesPath.set(1, null);
			filesName.set(1, "");
			editor.putString(FILE2_PATH, null);
			editor.putString(FILE2_NAME, "");
			editor.commit();
			loadFilesName(filesName);
			button2.setText(empty);
//			checkResult();
			break;
		case R.id.button3:
			filesPath.set(2, null);
			filesName.set(2, "");
			editor.putString(FILE3_PATH, null);
			editor.putString(FILE3_NAME, "");
			editor.commit();
			loadFilesName(filesName);
			button3.setText(empty);
//			checkResult();
			break;
		}
	}
	
    public void loadFilesName(ArrayList<String> filesName) {
    	listViewAdapter = new ListViewAdapter(this, filesName, getScreenHeight());
    	list_in_activity.setAdapter(listViewAdapter);
    	listViewAdapter.notifyDataSetChanged();
    }
    
    public void getCurFilePath(String curFilePath) {
        String remove = getResources().getString(R.string.remove);
    	
    	if(filesPath.get(0) != null && filesPath.get(1) != null && filesPath.get(2) != null) {
    		return;
    	}
    	
    	String curFileName;
    	curFileName = new File(curFilePath).getName();
    	SharedPreferences.Editor editor = prefs.edit();
    	
    	if(filesPath.get(0) == null) {
    		file1_path = curFilePath;
    		file1_name = curFileName;
    		filesPath.set(0, file1_path);
    		filesName.set(0, file1_name);
			editor.putString(FILE1_PATH, file1_path);
			editor.putString(FILE1_NAME, file1_name);
        	button1.setText(remove + "\n" + filesName.get(0).toUpperCase(Locale.getDefault()));
    	}
    	else if(filesPath.get(1) == null) {
    		file2_path = curFilePath;
    		file2_name = curFileName;
    		filesPath.set(1, file2_path);
    		filesName.set(1, file2_name);
    		editor.putString(FILE2_PATH, file2_path);
    		editor.putString(FILE2_NAME, file2_name);
        	button2.setText(remove + "\n" + filesName.get(1).toUpperCase(Locale.getDefault()));
    	}
    	else {
    		file3_path = curFilePath;
    		file3_name = curFileName;
    		filesPath.set(2, file3_path);
    		filesName.set(2, file3_name);
    		editor.putString(FILE3_PATH, file3_path);
    		editor.putString(FILE3_NAME, file3_name);
        	button3.setText(remove + "\n" + filesName.get(2).toUpperCase(Locale.getDefault()));
    	}
    	editor.commit();
    	loadFilesName(filesName);
//    	checkResult();
    }
	

    
	public boolean onKeyDown(int keyCode, KeyEvent event){
			switch(keyCode) {
//	  		case KeyEvent.KEYCODE_BACK:
//	  			
//	  			if(progressDialog.getProgress() <= progressDialog.getMax()) {
//	  				Log.e("" + progressDialog.getProgress(), "" + progressDialog.getMax());
//	  				return false;
//	  			}
//	  			else {
//	  				return true;
//	  			}
	  			
	  		case KeyEvent.KEYCODE_DPAD_UP:
	  			if(mFilesFragment.listView.hasFocus()) {
	  				add_to_play_list_button.requestFocus();
	  			}
	  			else if(button1.hasFocus() || button2.hasFocus() || button3.hasFocus()) {
	  				mFilesFragment.listView.requestFocus();
//	  				mFilesFragment.listView.setSelection(mFilesFragment.listView.getAdapter().getCount() - 1);
	  			}
	  			else if(list_in_activity.hasFocus()) {
	  				button1.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.rewind_button.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.play_push_button.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.forward_button.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.next_button.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.loop_button.hasFocus()) {
	  				mMediaPlayerFragment.previous_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.extend_button.hasFocus()) {
	  				mMediaPlayerFragment.rewind_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.random_button.hasFocus()) {
	  				mMediaPlayerFragment.play_push_button.requestFocus();
	  			}
	  			return true;
	  		case KeyEvent.KEYCODE_DPAD_LEFT:
	  			if(mMusicFilesFragment.listView.hasFocus()) {
	  				mFilesFragment.listView.requestFocus();
	  			}
	  			else if(button2.hasFocus()) {
	  				button1.requestFocus();
	  			}
	  			else if(button3.hasFocus()) {
	  				button2.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.seekbar.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.previous_button.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.rewind_button.hasFocus()) {
	  				mMediaPlayerFragment.previous_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.play_push_button.hasFocus()) {
	  				mMediaPlayerFragment.rewind_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.forward_button.hasFocus()) {
	  				mMediaPlayerFragment.play_push_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.next_button.hasFocus()) {
	  				mMediaPlayerFragment.forward_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.loop_button.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.extend_button.hasFocus()) {
	  				mMediaPlayerFragment.loop_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.random_button.hasFocus()) {
	  				mMediaPlayerFragment.extend_button.requestFocus();
	  			}
	  			return true;
	  		case KeyEvent.KEYCODE_DPAD_RIGHT:
	  			if(add_to_play_list_button.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(mFilesFragment.listView.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(button1.hasFocus()) {
	  				button2.requestFocus();
	  			}
	  			else if(button2.hasFocus()) {
	  				button3.requestFocus();
	  			}
	  			else if(button3.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(list_in_activity.hasFocus()) {
	  				mMusicFilesFragment.listView.requestFocus();
	  			}
	  			else if(mMusicFilesFragment.listView.hasFocus()) {
	  				mMediaPlayerFragment.previous_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.previous_button.hasFocus()) {
	  				mMediaPlayerFragment.rewind_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.rewind_button.hasFocus()) {
	  				mMediaPlayerFragment.play_push_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.play_push_button.hasFocus()) {
	  				mMediaPlayerFragment.forward_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.forward_button.hasFocus()) {
	  				mMediaPlayerFragment.next_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.loop_button.hasFocus()) {
	  				mMediaPlayerFragment.extend_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.extend_button.hasFocus()) {
	  				mMediaPlayerFragment.random_button.requestFocus();
	  			}
	  			return true;
	  		case KeyEvent.KEYCODE_DPAD_DOWN:
	  			if(add_to_play_list_button.hasFocus()) {
	  				mFilesFragment.listView.requestFocus();
	  			}
	  			else if(mFilesFragment.listView.hasFocus()) {
	  				button1.requestFocus();
	  			}
	  			else if(button1.hasFocus() || button2.hasFocus() || button3.hasFocus()) {
	  				list_in_activity.requestFocus();
//	  				list_in_activity.setSelection(0);
	  			}
	  			else if(mMediaPlayerFragment.seekbar.hasFocus()) {
	  				mMediaPlayerFragment.previous_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.previous_button.hasFocus()) {
	  				mMediaPlayerFragment.loop_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.rewind_button.hasFocus()) {
	  				mMediaPlayerFragment.extend_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.play_push_button.hasFocus()) {
	  				mMediaPlayerFragment.random_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.forward_button.hasFocus()) {
	  				mMediaPlayerFragment.random_button.requestFocus();
	  			}
	  			else if(mMediaPlayerFragment.next_button.hasFocus()) {
	  				mMediaPlayerFragment.random_button.requestFocus();
	  			}
	  			return true;
	  		default:
	  			return super.onKeyDown(keyCode, event);
			}
	}
	
	boolean isOnbootSelection = true;
	
	private AdapterView.OnItemSelectedListener mOnSelectedListener = new AdapterView.OnItemSelectedListener() {
		
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {

			if(isOnbootSelection) {
				parent.requestFocus();
				v.requestFocus();
				parent.setSelection(0);
				isOnbootSelection = false;
			}
			
			if(filesPath.get(pos) == null)
				return;
//			else if(filesPath.get(1) != null) {
//				parent.requestFocus();
//				v.requestFocus();
//				parent.setSelection(1);
//				isOnbootSelection = false;
//			}
//			else if(filesPath.get(2) != null) {
//				parent.requestFocus();
//				v.requestFocus();
//				parent.setSelection(2);
//				isOnbootSelection = false;
//			}
//			else {
//				mFilesFragment.listView.requestFocus();
//				mFilesFragment.listView.setSelection(0);
//				isOnbootSelection = false;
//			}
			
			browserMode(filesPath.get(pos));
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			
			if(filesPath.get(pos) == null)
				return;
			
			browserMode(filesPath.get(pos));
    	}
	};
	
	public void secondAndThirdPanelHide() {
        FragmentTransaction ftInInterface = fm.beginTransaction();
    	ftInInterface.hide(mMusicFilesFragment);
    	ftInInterface.hide(mMediaPlayerFragment);
    	hori_seperator_in_second_panel.setVisibility(View.GONE);
//    	vert_seperator_betw_second_and_third_panel.setVisibility(View.GONE);
    	numberOfSongs.setVisibility(View.GONE);
    	number_of_songs_title.setVisibility(View.GONE);
    	ftInInterface.commit();
	}
}
