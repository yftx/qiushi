package douzi.android.caodan;

import java.util.ArrayList;

import net.youmi.android.AdManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

import douzi.android.caodan.Util.Logger;

public class CaoDanActivity extends Activity implements OnClickListener {

	static {
		AdManager.init("849a7d3b06329f55", "d4d7db4c01cb8f8e", 50, false);
	}
	
	private Logger mLogger = new Logger(true);
	private boolean mIsLoading = false;
	private static final String TAG = "CaoDanActivity";
	private View mDock;
	private View mTitle;
	private View mAdView;
	private Animation mFadeProgressAnimation;
	private CaoDanDataParser mParser = new CaoDanDataParser();
	private CaoDanListAdapter mAdapter;
	private ListView mList;
	private String[] mUrls = new String[9];
	private int[] index = new int[9];
	int i = 0;
	private static final String URL_LOVE = "http://www.caodan.cc/caodan/aiqing/";
	{
		mUrls[i++] = URL_LOVE;
	}
	private static final String URL_MONEY = "http://www.caodan.cc/caodan/qian/";
	{
		mUrls[i++] = URL_MONEY;
	}
	private static final String URL_WORK = "http://www.caodan.cc/caodan/gongzuo/";
	{
		mUrls[i++] = URL_WORK;
	}
	private static final String URL_HOME = "http://www.caodan.cc/caodan/jiating/";
	{
		mUrls[i++] = URL_HOME;
	}
	private static final String URL_SCHOOL = "http://www.caodan.cc/caodan/xuexiao/";
	{
		mUrls[i++] = URL_SCHOOL;
	}
	private static final String URL_SEX = "http://www.caodan.cc/caodan/xing/";
	{
		mUrls[i++] = URL_SEX;
	}
	private static final String URL_CHILD = "http://www.caodan.cc/caodan/xiaohai/";
	{
		mUrls[i++] = URL_CHILD;
	}
	private static final String URL_HEALTH = "http://www.caodan.cc/caodan/jiankang/";
	{
		mUrls[i++] = URL_HEALTH;
	}
	private static final String URL_OTHER = "http://www.caodan.cc/caodan/luanqibazao/";
	{
		mUrls[i++] = URL_OTHER;
	}

	private final static int LOVE = 0;
	private final static int MONEY = 1;
	private final static int WORK = 2;
	private final static int HOME = 3;
	private final static int SCHOOL = 4;
	private final static int SEX = 5;
	private final static int CHILD = 6;
	private final static int HEALTH = 7;
	private final static int OTHER = 8;

	private int mCategroy = LOVE;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mFadeProgressAnimation = (Animation) AnimationUtils.loadAnimation(this, R.anim.fade_progress);
		mDock = findViewById(R.id.dock);
		mTitle = findViewById(R.id.imgTitle);
		mAdView = findViewById(R.id.adview);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		setupView();
		if(hasNetwork()){
			loadData(URL_LOVE);
		}else{
			Toast.makeText(CaoDanActivity.this, "没有网络,连接后点击分类重新获取", Toast.LENGTH_SHORT).show();
		}
	}

	private void loadData(String url, String... params) {
		if (params.length == 0)
			(new AsyncLoadData()).execute(url);
		else
			(new AsyncLoadData()).execute(url, params[0]);
	}

	private CheckedTextView mCheckedTextView;

	private void setupView() {
		mAdapter = new CaoDanListAdapter(this);
		mAdapter.setOnLoadMoreClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mIsLoading) {
					return;
				}
				loadData(getUrlForIndex(mCategroy, index[mCategroy] + 1), String.valueOf(mCategroy));
			}

		});
		mList = (ListView) findViewById(R.id.list);
		mList.setAdapter(mAdapter);

		CheckedTextView btnLove = (CheckedTextView) findViewById(R.id.btnLove);
		btnLove.setOnClickListener(this);
		btnLove.setChecked(true);
		mCheckedTextView = btnLove;
		CheckedTextView btnMoney = (CheckedTextView) findViewById(R.id.btnMoney);
		btnMoney.setOnClickListener(this);
		CheckedTextView btnWork = (CheckedTextView) findViewById(R.id.btnWork);
		btnWork.setOnClickListener(this);
		CheckedTextView btnHome = (CheckedTextView) findViewById(R.id.btnHome);
		btnHome.setOnClickListener(this);
		CheckedTextView btnSchool = (CheckedTextView) findViewById(R.id.btnSchool);
		btnSchool.setOnClickListener(this);
		CheckedTextView btnSex = (CheckedTextView) findViewById(R.id.btnSex);
		btnSex.setOnClickListener(this);
		CheckedTextView btnChild = (CheckedTextView) findViewById(R.id.btnChild);
		btnChild.setOnClickListener(this);
		CheckedTextView btnHealth = (CheckedTextView) findViewById(R.id.btnHealth);
		btnHealth.setOnClickListener(this);
		CheckedTextView btnOther = (CheckedTextView) findViewById(R.id.btnOther);
		btnOther.setOnClickListener(this);
		CheckedTextView btnAbout = (CheckedTextView) findViewById(R.id.btnAbout);
		btnAbout.setOnClickListener(this);
		CheckedTextView btnPost = (CheckedTextView) findViewById(R.id.btnPost);
		btnPost.setOnClickListener(this);

	}

	class AsyncLoadData extends AsyncTask<String, Void, ArrayList<String>> {

		int categroy = -1;

		@Override
		protected void onPreExecute() {
			startProgressAnimation();
		}

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			HttpHelper httpHelper = new HttpHelper();
			String html = httpHelper.getForString(params[0], "gb2312");
			if (html == null || html.trim().equals("")) {
				return null;
			}
			if (params.length == 2) {
				categroy = Integer.parseInt(params[1]);
			}
			try {
				return mParser.getCaoDanData(html);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

			stopProgressAnimation();

			if (result == null) {
				Toast.makeText(CaoDanActivity.this, "暂时无法获取糗事,点击分类重新获取", Toast.LENGTH_SHORT).show();
				mIsLoading = false;
				return;
			}
			if (categroy != -1) {
				index[categroy]++;
			}

			if(mTitle != null){
				((ViewGroup)findViewById(R.id.main_root)).removeView(mTitle);
				mTitle = null;
				mAdView.setVisibility(View.VISIBLE);
			}
			
			mAdapter.datas = result;
			mAdapter.notifyDataSetInvalidated();
			mList.setSelection(0);
			mIsLoading = false;
		}

	}

	private String getUrlForIndex(int categroy, int index) {
		int cate = 0;
		switch (categroy) {
		case LOVE:
			cate = 2;
			break;
		case MONEY:
			cate = 3;
			break;
		case WORK:
			cate = 4;
			break;
		case HOME:
			cate = 5;
			break;
		case SCHOOL:
			cate = 6;
			break;
		case SEX:
			cate = 7;
			break;
		case CHILD:
			cate = 10;
			break;
		case HEALTH:
			cate = 8;
			break;
		case OTHER:
			cate = 9;
			break;
		}
		return mUrls[categroy] + "list_" + cate + "_" + (index + 1) + ".html";
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnAbout) {
			showDialog(R.id.btnAbout);
			return;
		} else if (v.getId() == R.id.btnPost) {
			goPost();
			return;
		} 
		if (mIsLoading) {
			return;
		}
		mCheckedTextView.setChecked(false);
		mCheckedTextView = (CheckedTextView) v;
		mCheckedTextView.setChecked(true);
		switch (v.getId()) {
		case R.id.btnLove:
			mCategroy = LOVE;
			break;
		case R.id.btnMoney:
			mCategroy = MONEY;
			break;
		case R.id.btnWork:
			mCategroy = WORK;
			break;
		case R.id.btnHome:
			mCategroy = HOME;
			break;
		case R.id.btnSchool:
			mCategroy = SCHOOL;
			break;
		case R.id.btnSex:
			mCategroy = SEX;
			break;
		case R.id.btnChild:
			mCategroy = CHILD;
			break;
		case R.id.btnHealth:
			mCategroy = HEALTH;
			break;
		case R.id.btnOther:
			mCategroy = OTHER;
			break;

		default:
			return;
		}
		String url = getUrlForIndex(mCategroy, index[mCategroy]);
		mIsLoading = true;
		loadData(url);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.btnAbout) {
			return new AlertDialog.Builder(CaoDanActivity.this).setTitle("关于").setMessage("内容来自:http://www.caodan.cc/\n作者:douzifly@gmail.com")
					.setNeutralButton("关闭", null).create();
		}
		return null;
	}

	private void goPost() {
		Intent i = new Intent(this, PostActivity.class);
		startActivity(i);
	}

	public void startProgressAnimation() {
		mDock.startAnimation(mFadeProgressAnimation);
	}

	public void stopProgressAnimation() {
		mDock.clearAnimation();
	}
	
	private boolean hasNetwork() {
		ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}

		return false;
	}
	
	public boolean mIsPreesdBackOnce = false;
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(!mIsPreesdBackOnce){
				Toast.makeText(CaoDanActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
				mIsPreesdBackOnce = true;
				mHandler.sendEmptyMessageDelayed(0, 2000);
			}else {
				mIsPreesdBackOnce = false;
				finish();
				
			}
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0){
				mIsPreesdBackOnce = false;
			}
		};
	};

}