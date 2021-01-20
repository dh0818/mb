package com.example.musicbrowser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.FragmentManager;

public class MediaPlayerFragment extends Fragment implements OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
	
	MediaPlayer mediaPlayer;
	SeekBar seekbar;
	Handler myHandler = new Handler();
	Handler myHandler2 = new Handler();
	Uri uri;
	Context mContext;
	TextView audio_title_title, audio_name, audio_info_title, audio_name_meta_data, start_time_field, end_time_field;
	TextView media_player_info_title, looping_info, random_info;
	ImageButton previous_button, rewind_button, play_push_button, forward_button, next_button;
	ImageButton loop_button, extend_button, random_button, loop_button4, loop_button5;
	View v;
	String musicFilePath;
	double startTime = 0;
	double endTime = 0;
	boolean once = false;
    ContentResolver mContentResolver;
    String TAG = "MediaPlayerFragment";
    LinearLayout media_play_ll;
    int background_index = 0, prev_random_num;
    private Integer[] mThumbIds = {
    		R.drawable.spring_a, R.drawable.spring_b, R.drawable.spring_c, R.drawable.spring_d, R.drawable.spring_e
    		};
    boolean looping = false;
    boolean random = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setHasOptionsMenu(true);
//		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return v = inflater.inflate(R.layout.activity_media_player, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		findTextview();
		findImageButton();
		findOthers();
		findLinearLayout();
	}
	
	public void findTextview() {
		audio_title_title = (TextView)v.findViewById(R.id.audio_title_title);
		audio_title_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.030f);
		audio_name = (TextView)v.findViewById(R.id.audio_name);
		audio_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.025f);
		audio_info_title = (TextView)v.findViewById(R.id.audio_info_title);
		audio_info_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.030f);
		audio_name_meta_data = (TextView)v.findViewById(R.id.audio_name_meta_data);
		audio_name_meta_data.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.025f);
		media_player_info_title = (TextView)v.findViewById(R.id.media_player_info_title);
		media_player_info_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.030f);
		looping_info = (TextView)v.findViewById(R.id.looping_info);
		looping_info.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.025f);
		looping_info.setText("Looping:" + looping);
		random_info = (TextView)v.findViewById(R.id.random_info);
		random_info.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.025f);
		random_info.setText("Random:" + random);
		start_time_field = (TextView)v.findViewById(R.id.start_time_field);
		start_time_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.025f);
		end_time_field = (TextView)v.findViewById(R.id.end_time_field);
		end_time_field.setTextSize(TypedValue.COMPLEX_UNIT_SP, screenHeight * 0.025f);
	}
	
	int screenHeight;
	
    public void getScreenHeight(int screenHeight) {
    	this.screenHeight = screenHeight;
    }
	
	public void findLinearLayout() {
		media_play_ll = (LinearLayout)v.findViewById(R.id.media_play_ll);
	}
	
	public void findImageButton() {
		play_push_button = (ImageButton)v.findViewById(R.id.play_push_button);
		play_push_button.setOnClickListener(this);
		play_push_button.setEnabled(false);
		
		forward_button = (ImageButton)v.findViewById(R.id.forward_button);
		forward_button.setOnClickListener(this);
		forward_button.setEnabled(false);
		
		rewind_button = (ImageButton)v.findViewById(R.id.rewind_button);
		rewind_button.setOnClickListener(this);
		rewind_button.setEnabled(false);
		
		previous_button = (ImageButton)v.findViewById(R.id.previous_button);
		previous_button.setOnClickListener(this);
		previous_button.setEnabled(false);
		
		next_button = (ImageButton)v.findViewById(R.id.next_button);
		next_button.setOnClickListener(this);
		next_button.setEnabled(false);
		
		loop_button = (ImageButton)v.findViewById(R.id.loop_button);
		loop_button.setOnClickListener(this);
		loop_button.setEnabled(false);
		
		extend_button = (ImageButton)v.findViewById(R.id.extend_button);
		extend_button.setOnClickListener(this);
		extend_button.setEnabled(false);
		
		random_button = (ImageButton)v.findViewById(R.id.random_button);
		random_button.setOnClickListener(this);
		random_button.setEnabled(false);
	}
	
	public void findOthers() {
		seekbar = (SeekBar)v.findViewById(R.id.seek_bar);
		seekbar.setClickable(true);
		seekbar.setOnSeekBarChangeListener(this);
	}

	public void getContext(Context mContext) {
		this.mContext = mContext;
	}
	
	String artist, album;
	
	public void getMusicFileMetaData(Item item) {
		artist = item.getArtist();
		album = item.getAlbum();
	}
	
	List<String> musicsFilePath, randomMusicsFilePath;
	int pos;
	boolean playNext;
	ArrayList<RecordItem> recordItems = new ArrayList<RecordItem>();
	
	public void playFromFilePath(int pos, List<String> musicsFilePath, boolean playNext) {
		play_push_button.setEnabled(true);
		this.musicsFilePath = musicsFilePath;
		this.playNext = playNext;
		this.pos = pos;
		String musicFilePath = null;
		musicFilePath = musicsFilePath.get(pos);
		recordItems = getRecordItems(musicsFilePath, pos);
		String album = getResources().getString(R.string.album);
		
		if(mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(mContext, Uri.parse(musicFilePath));
			mediaPlayer.setOnErrorListener(this);
			mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.setLooping(false);
			audio_name.setText(FileUtils.parseFileNameFromFilePath(musicFilePath));
			audio_name_meta_data.setText(album + ": " + item.getAlbum());
			background_index = setMediaPlayerBackground(musicFilePath, 0, true);
			recordItems.get(pos).setListened(true);
			play();
			forward_button.setEnabled(true);
			rewind_button.setEnabled(true);
			next_button.setEnabled(true);
			previous_button.setEnabled(true);
			loop_button.setEnabled(true);
			extend_button.setEnabled(true);
			random_button.setEnabled(true);
		}
//		else if(this.musicFilePath != musicFilePath) {
//			init(musicsFilePath, pos);
//		}
		else {
			init(musicsFilePath, pos);
//			if(!mediaPlayer.isPlaying()){
//				play();
//			}
//			else {
//				pause();
//			}
		}
		
		initUpdateSongInfoRunnable();
		
		this.musicFilePath = musicFilePath;
	}
	
	public ArrayList<RecordItem> getRecordItems(List<String> musicsFilePath, int pos) {
		ArrayList<RecordItem> recordItems = new ArrayList<RecordItem>();
		
		for(String musicFilePath:musicsFilePath) {
			int index = 0;
			recordItems.add(new RecordItem(index, musicFilePath));
			index++;
		}
		
//		List<String> randomMusicsFilePath;
//		randomMusicsFilePath = musicsFilePath;
//		randomMusicsFilePath.remove(pos);
//		Collections.shuffle(randomMusicsFilePath, new Random(System.nanoTime()));
//		randomMusicsFilePath.add(0, musicsFilePath.get(pos));
		
//		for(String randomMusicFilePath:randomMusicsFilePath) {
//			randomItems.add(new RandomItem(false, randomMusicFilePath));
//		}
		
		return recordItems;
	}
	
	public int setMediaPlayerBackground(String musicFilePath, int new_random, boolean first) {
		getAudioItem(musicFilePath);
		
		if(item.getPic() != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(item.getPic(), 0, item.getPic().length);
			BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
			media_play_ll.setBackground(ob);
		}
		else if(first) {
			media_play_ll.setBackgroundResource(mThumbIds[0]);
			background_index = 0;
		}
		else {
			background_index = RandomBackground(background_index);
		}
		
		return background_index;
	}
	
	Item item;
	
	public void getAudioItem(String musicFilePath) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever(); 
        retriever.setDataSource(musicFilePath);
        
	    String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
	    String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM); 
	    byte[] embeddedPicture = retriever.getEmbeddedPicture();
	    
	    retriever.release(); 
	    
		this.item = new Item(artist, album, embeddedPicture);
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}
    
//	public void release() {
//		if(mediaPlayer != null)
//			mediaPlayer.release();
//	}
	
	public void play(){
    	String min = getResources().getString(R.string.min);
    	String sec = getResources().getString(R.string.sec);
    	
		play_push_button.setImageResource(android.R.drawable.ic_media_pause);
		
		mediaPlayer.start();
		endTime = mediaPlayer.getDuration();
		startTime = mediaPlayer.getCurrentPosition();
		seekbar.setMax((int) endTime);

		end_time_field.setText(String.format("%d " + min + ", %d " +  sec, TimeUnit.MILLISECONDS.toMinutes((long) endTime),
			TimeUnit.MILLISECONDS.toSeconds((long) endTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) endTime))));
		start_time_field.setText(String.format("%d " + min + ", %d " +  sec, TimeUnit.MILLISECONDS.toMinutes((long) startTime),
			TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
		seekbar.setProgress((int)startTime);
		myHandler.postDelayed(UpdateSongTimeRunnable, 100);
	}
	
	public void pause(){
		play_push_button.setImageResource(android.R.drawable.ic_media_play);
		mediaPlayer.pause();
	}

	public void forward(int forwardTime){
		int temp = (int)startTime;
		
		if((temp + forwardTime) <= endTime) {
			startTime = startTime + forwardTime;
			mediaPlayer.seekTo((int) startTime);
		}
		else
			Toast.makeText(mContext, "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
	}
	
	public void rewind(int backwardTime){
		int temp = (int)startTime;
		
		if((temp - backwardTime) > 0) {
			startTime = startTime - backwardTime;
			mediaPlayer.seekTo((int) startTime);
		}
		else
			Toast.makeText(mContext, "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
	}
	
	public int findRandomNotListened() {
		int random_item_index;
		
		do {
			random_item_index = generateRandomIndex(recordItems.size());
		}while(checkItemRecord(random_item_index));
		
		return random_item_index;
	}
	
	public int generateRandomIndex(int range) {
		Random r = new Random();
		return r.nextInt(range);
	}
	
	public boolean checkItemRecord(int random_item_index) {
		return recordItems.get(random_item_index).getListened();
	}
	
	public void clearItemsRecord() {
		for(RecordItem item:recordItems){
			item.setListened(false);
		}
	}
	
	int record_num = 0;
	
	public void initUpdateSongInfoRunnable () {
		once = true;
		myHandler2.removeCallbacks(UpdateSongInfoRunnable);
		myHandler2.postDelayed(UpdateSongInfoRunnable, 100);
	}
	
	public void next() {

		initUpdateSongInfoRunnable();
		
		if(looping)
			;
		else if(random){
			int random_temp = findRandomNotListened();
			
			if(pos == random_temp)
				pos = random_temp + 1;
			else
				pos = random_temp;
			
			if(pos >= musicsFilePath.size())
				pos = 0;
			
			recordItems.get(pos).setListened(true);
			record_num++;
		}
		else {
			pos++;
		}
		
		if(pos >= musicsFilePath.size())
			pos = 0;
		
		Log.e("pos", "" + pos);
		
		init(musicsFilePath, pos);
	}
	
	public void previous() {
		if(mediaPlayer.isLooping())
			;
		else
			pos--;
		
		if(pos < 0)
			pos = 0;
		
		init(musicsFilePath, pos);
	}
	
	int mode = 0;
	int record;
	
	Runnable UpdateSongTimeRunnable = new Runnable() {

		public void run() {
	    	String min = getResources().getString(R.string.min);
	    	String sec = getResources().getString(R.string.sec);
			startTime = mediaPlayer.getCurrentPosition();
			
			start_time_field.setText(String.format("%d " + min + ", %d " +  sec, TimeUnit.MILLISECONDS.toMinutes((long) startTime),
				TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));
			seekbar.setProgress((int)startTime);
			myHandler.postDelayed(this, 100);
		}
	};
	
	boolean once2 = true;
	boolean mode2 = true;
	
	Runnable UpdateSongInfoRunnable = new Runnable() {

		public void run() {

			if(once2 == true) {
				String album = getResources().getString(R.string.album);
				audio_name_meta_data.setText(album + ": " + item.getAlbum());
				once2 = false;
			}
			else {
				if(mode2 == true) {
					String album = getResources().getString(R.string.album);
					audio_name_meta_data.setText(album + ": " + item.getAlbum());
					mode2 = false;
				}
				else {
					String artist = getResources().getString(R.string.artist);
					audio_name_meta_data.setText(artist + ": " + item.getArtist());
					mode2 = true;
				}
			}
			
			myHandler2.postDelayed(this, 5000);
		}
	};
	
	public void infoLoop(int time) {
		
		if(time % 5 == 4) {
			if(!(record == time)) {
				mode++;
				
				if(mode > 1)
					mode = 0;
			
				if(mode == 0) {
					String album = getResources().getString(R.string.album);
					audio_name_meta_data.setText(album + ": " + item.getAlbum());
					
				}
				else if(mode == 1) {
					String artist = getResources().getString(R.string.artist);
					audio_name_meta_data.setText(artist + ": " + item.getArtist());
				}
			}
			
			record = time;
		}
	}

	public void init(List<String> musicsFilePath, int pos) {
		mediaPlayer.release();
		mediaPlayer = MediaPlayer.create(mContext, Uri.parse(musicsFilePath.get(pos)));
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setLooping(looping);
		audio_name.setText(FileUtils.parseFileNameFromFilePath(musicsFilePath.get(pos)));
		String album = getResources().getString(R.string.album);
		audio_name_meta_data.setText(album + ": " + item.getAlbum());
		
		
		double mid = Math.ceil(recordItems.size() / 2.0) ;
		
		if(record_num >= mid) {
			clearItemsRecord();
			record_num = 0;
		}
		
		play();
		background_index = setMediaPlayerBackground(musicsFilePath.get(pos), background_index, false);
	}
	
	public void loop() {
		if(mediaPlayer.isLooping()) {
			mediaPlayer.setLooping(false);
//			loop_button.setImageResource(android.R.drawable.ic_menu_revert);
			looping = false;
		}
		else {
			mediaPlayer.setLooping(true);
//			loop_button.setBackgroundColor(Color.parseColor("#FF9912"));
//			loop_button.setPressed(true);
			looping = true;
		}
		
//    	AnimatorSet set;
//		set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity().getBaseContext(),R.animator.rotate_hori_half);
//	    set.setTarget(loop_button);
//	    set.start();
		
		looping_info.setText("Looping:" + looping);
	}
	
	LinearLayout first_panel, second_panel;
	MusicFilesFragment mMusicFilesFragment;
	FilesFragment mFilesFragment;
	View vert_seperator;
	
	public void getLayoutForExtend(LinearLayout linearLayout1, LinearLayout linearLayout2, MusicFilesFragment mMusicFilesFragment, FilesFragment mFilesFragment, View v) {
		first_panel = linearLayout1;
		second_panel = linearLayout2;
		this.mMusicFilesFragment = mMusicFilesFragment;
		this.mFilesFragment = mFilesFragment;
		vert_seperator = v;
	}
	
	boolean extend = false;
	
	public void extendOrNot() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		if(mFilesFragment.isHidden())
			ft.show(mFilesFragment);
		else
			ft.hide(mFilesFragment);
		
		if(mMusicFilesFragment.isHidden())
			ft.show(mMusicFilesFragment);
		else
			ft.hide(mMusicFilesFragment);
		
		ft.commit();
		
		if(first_panel.getVisibility() == View.GONE) {
			first_panel.setVisibility(View.VISIBLE);
			vert_seperator.setVisibility(View.VISIBLE);
		}
		else {
			first_panel.setVisibility(View.GONE);
			vert_seperator.setVisibility(View.GONE);
		}
		
		if(second_panel.getVisibility() == View.GONE) {
			second_panel.setVisibility(View.VISIBLE);
		}
		else
			second_panel.setVisibility(View.GONE);
		
		if(extend) {
			extend_button.setImageResource(android.R.drawable.btn_plus);
			extend = false;
		}
		else {
			extend_button.setImageResource(android.R.drawable.btn_minus);
			extend = true;
		}
	}
	
	public void random() {
		if(random == true)
			random = false;
		else
			random = true;
		
		random_info.setText("Random:" + random);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.play_push_button:
			if(mediaPlayer.isPlaying())
				pause();
			else
				play();
			break;
		case R.id.forward_button:
			forward(5000);
			break;
		case R.id.rewind_button:
			rewind(5000);
			break;
		case R.id.next_button:
			next();
			break;
		case R.id.previous_button:
			previous();
			break;
		case R.id.loop_button:
			loop();
			break;
		case R.id.extend_button:
			extendOrNot();
			break;
		case R.id.random_button:
			random();
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		next();
	}
	
	public int RandomBackground(int prev_random_num) {
		int new_random;
		Random r = new Random();
		int range = mThumbIds.length;
		
		new_random = r.nextInt(range);
		
		if(prev_random_num == new_random)
			new_random++;
		
		if(new_random > mThumbIds.length - 1)
			new_random = 0;
		
		media_play_ll.setBackgroundResource(mThumbIds[new_random]);
		
		return new_random;
	}
	
	public boolean isMediaPlayerPlaying() {
		return mediaPlayer.isPlaying();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
	    if (what == 100) {
	    	Log.e("" + what, "" + extra);
	    }
	    else if (what == 1) {
	    	Log.e("" + what, "" + extra);
	    }
	    else if(what == 800) {
	    	Log.e("" + what, "" + extra);
	    }
	    else if (what == 701) {
	    	Log.e("" + what, "" + extra);
	    }
	    else if(what == 700) {
	    	Log.e("" + what, "" + extra);
	    }
	    else if (what == -38) {
	    	Log.e("" + what, "" + extra);
	    }
	    
	    return false;
	}
}