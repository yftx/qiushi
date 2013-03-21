/**
 * @author Douzi
 * @date 2011-10-25
 * @copyright beckysoft 
 */
package douzi.android.caodan;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

import douzi.android.caodan.Util.Logger;

/**
 * Post a new article
 */
public class PostActivity extends Activity {

	static final String TAG = "PostActivity";
	
	private static final String URL_VD_CODE = "http://www.caodan.cc/include/vdimgck.php";
	private static final String URL_POST =  "http://www.caodan.cc/member/article_add.php";

	// 对应网站id为2,3,4,5,6,7,8,9,10
	private static final String[] mCategroys = new String[] { "爱情", "钱", "工作", "家庭", "学校", "性", "健康", "其他", "小孩" };

	private EditText mEdNickName;
	private EditText mEdContent;
	private EditText mEdVd;
	private Spinner mSpCategory;
	private Button mBtnPost;
	private ImageView mImgVb;
	private ProgressBar mProgress;
	private ArrayAdapter<String> mCategroyAdapter;

	private HttpHelper mHttpHelper;
	private Logger mLogger = new Logger(true);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		setupView();
		new LoadVdTask().execute();
		restore();
	}

	private void setupView() {

		mEdNickName = (EditText) findViewById(R.id.post_etNickName);
		mEdContent = (EditText) findViewById(R.id.post_etContent);
		mEdVd = (EditText) findViewById(R.id.post_etVd);
		mSpCategory = (Spinner) findViewById(R.id.post_spCatagory);
		mBtnPost = (Button) findViewById(R.id.post_btnPost);
		mImgVb = (ImageView) findViewById(R.id.post_imgVd);
		mProgress = (ProgressBar) findViewById(R.id.post_progress);

		mBtnPost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// check input
				if (!checkInput()) {
					Toast.makeText(PostActivity.this, "请输入完整", Toast.LENGTH_SHORT).show();
					return;
				}
				int category = mSpCategory.getSelectedItemPosition() + 2;
				Toast.makeText(PostActivity.this, "正在发表,发表成功将自动关闭", Toast.LENGTH_SHORT).show();
				new PostTask().execute(String.valueOf(category));
			}
		});

		mCategroyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCategroys);
		mCategroyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpCategory.setAdapter(mCategroyAdapter);

	}

	private boolean checkInput() {
		if (mEdNickName.getText().toString().trim().equals("") || mEdContent.getText().toString().trim().equals("")
				|| mEdVd.getText().toString().trim().equals("")) {
			return false;
		}
		return true;
	}

	class PostTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			ArrayList<BasicNameValuePair> forms = new ArrayList<BasicNameValuePair>(5);
			forms.add(new BasicNameValuePair("title", mEdNickName.getText().toString().trim()));
			forms.add(new BasicNameValuePair("dopost", "save"));
			forms.add(new BasicNameValuePair("channelid", "1"));
			forms.add(new BasicNameValuePair("dede_addonfields", ""));
			forms.add(new BasicNameValuePair("typeid", params[0]));
			String vd = mEdVd.getText().toString();
			forms.add(new BasicNameValuePair("vdcode", vd.trim().toLowerCase()));
			String content;
			content = "<p>" + mEdContent.getText().toString().trim() + "</p>";
			forms.add(new BasicNameValuePair("body", content));

			String ret = mHttpHelper.post(URL_POST, forms,"gb2312");
			mLogger.d(TAG, "return from server : " + ret);
			if (ret != null && !ret.trim().equals("")) {
				return true;
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(PostActivity.this, "发表成功，请等待审核", Toast.LENGTH_SHORT).show();
				mEdContent.setText("");
				finish();
			} else {
				Toast.makeText(PostActivity.this, "发表失败，请稍候再试", Toast.LENGTH_SHORT).show();
			}
		}

	}

	// load validate code from server
	class LoadVdTask extends AsyncTask<Void, Void, Drawable> {

		@Override
		protected void onPreExecute() {
			// we must create a new instance for protect the session!
			mHttpHelper = new HttpHelper();
			mProgress.setVisibility(View.VISIBLE);
			mImgVb.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onPostExecute(Drawable result) {
			mProgress.setVisibility(View.INVISIBLE);
			mImgVb.setVisibility(View.VISIBLE);
			mImgVb.setImageDrawable(result);
		}

		@Override
		protected Drawable doInBackground(Void... params) {
			InputStream s = mHttpHelper.getForStream(URL_VD_CODE);
			BitmapDrawable vd = (BitmapDrawable) BitmapDrawable.createFromStream(s, "img");
			try {
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return vd;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		save();
	}

	private void save() {
		ConfigHelper.saveNickName(this, mEdNickName.getText().toString());
		ConfigHelper.saveContent(this, mEdContent.getText().toString());
	}

	private void restore() {
		mEdNickName.setText(ConfigHelper.getNickName(this));
		mEdContent.setText(ConfigHelper.getContent(this));
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

}
