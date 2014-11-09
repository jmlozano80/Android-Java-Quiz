package com.jose.javaquiz;

import java.util.Locale;




import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Result extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	static Context context;
	static String attend;
	static String total;
	static String correct;
	static String time;
	static String score;
	static String topScore; 
	//Method for the button play again
	
	public static void playAgain(View view)
	{
		Intent intent= new Intent(context, Sett.class);
		
		context.startActivity(intent);
		
	}//end method playAgain
	
	//Method for the button see questions
	
	public static void seeQuestions(View view)
	{
		Toast.makeText(context, "Sorry, not implemented yet", Toast.LENGTH_SHORT).show();
				
	}//end method seeQuestions
	
	
	
	
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	public void onBackPressed()
	{

	   // super.onBackPressed(); // Comment this super call to avoid calling finish()
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		
		Intent intent =getIntent();
		//set textviews
		total = intent.getExtras().getString(GameActivity.EXTRA_TOTAL_QUESTIONS);
		attend= intent.getStringExtra(GameActivity.EXTRA_ATTENTED);
		//total= intent.getStringExtra(GameActivity.EXTRA_TOTAL_QUESTIONS);
		correct= intent.getStringExtra(GameActivity.EXTRA_RIGHT);
		time= intent.getStringExtra(GameActivity.EXTRA_TIME);
		score= intent.getStringExtra(GameActivity.EXTRA_SCORE);
		topScore=intent.getStringExtra(GameActivity.EXTRA_TOPSCORE);
		
		Log.d("Results", "The results is attented= "+attend+" total = "+total+" correct = "+correct+" time = "+time+" score= "+score);
					
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		context = this;
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			if (position == 0) {
				fragment = new CurrentResultFragment();
				Bundle args = new Bundle();
				//args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
			} else if (position == 1) {
				fragment = new TopScoreFragment();
				Bundle args = new Bundle();
				//args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
			}
			
			 
			
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class CurrentResultFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public CurrentResultFragment() {
		}
		

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.current_result,
					container, false);
			
			TextView textViewAttented= (TextView) rootView.findViewById(R.id.textView4);
			textViewAttented.setText(attend);
			
			TextView textViewTotal =(TextView) rootView.findViewById(R.id.textView2);
			textViewTotal.setText(total);
			
			TextView textViewCorrect= (TextView) rootView.findViewById(R.id.textView7);
			textViewCorrect.setText(correct);
			
			TextView textViewTime= (TextView) rootView.findViewById(R.id.textViewTime);
			textViewTime.setText(time);
			
			TextView textViewScore= (TextView) rootView.findViewById(R.id.score);
			textViewScore.setText(score);
			
			return rootView;
		}
	}
	
	public static class TopScoreFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		
		public TopScoreFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.top_score,
					container, false);
				
			Log.d("Fragment", "TopScore = "  + topScore);
			TextView textView= (TextView) rootView.findViewById(R.id.textViewTopScore);
			textView.setText(topScore);
			
			return rootView;
		}
	}

}
