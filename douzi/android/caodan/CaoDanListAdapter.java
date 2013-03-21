/**
 *  @author douzifly@gmail.com
 *  @date 2011-9-24
 */
package douzi.android.caodan;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author douzifly@gmail.com
 * 
 */
public class CaoDanListAdapter extends BaseAdapter implements OnLongClickListener {

	public CaoDanListAdapter(Context context) {
		mContext = context;
	}

	final int VIEW_TYPE_DATA = 0;
	final int VIEW_TYPE_MORE = 1;

	private Context mContext;
	public ArrayList<String> datas;

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public int getCount() {
		if (datas == null || datas.size() == 0) {
			return 0;
		} else {
			return datas.size() + 1;
		}
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItemViewType(position);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		int count = getCount();
		if (count - 1 == position) {
			// last one
			return VIEW_TYPE_MORE;
		} else {
			return VIEW_TYPE_DATA;
		}
	}

	private OnClickListener mOnLoadMoreClick;

	public void setOnLoadMoreClick(OnClickListener l) {
		mOnLoadMoreClick = l;
	}

	AbsListView.LayoutParams lp = new AbsListView.LayoutParams(-1, -1);
	{

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (getItemViewType(position) == VIEW_TYPE_DATA) {
			View view = null;
			if (convertView == null) {
				view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.main_list_item, null);
				DataHolder holder = new DataHolder();
				holder.txtView = (TextView) view.findViewById(R.id.main_list_item_txt);
				view.setTag(holder);
				holder.txtView.setOnLongClickListener(this);
			} else {
				view = convertView;
			}
			DataHolder holder = (DataHolder) view.getTag();
			holder.txtView.setText(datas.get(position));
			return view;
		} else {
			View view = null;
			if (convertView == null) {
				view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.main_list_item_more, null);
				if (mOnLoadMoreClick != null) {
					view.findViewById(R.id.main_list_item_more_txt).setOnClickListener(mOnLoadMoreClick);
				}
			} else {
				view = convertView;
			}
			return view;
		}
	}

	class DataHolder {
		public TextView txtView;
	}

	@Override
	public boolean onLongClick(View v) {
		String text = (String)((TextView)v).getText();
		// Toast.makeText(mContext, holder.txtView.getText(),Toast.LENGTH_SHORT).show();

		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/plain");
		i.putExtra(Intent.EXTRA_SUBJECT, "·ÖÏí");
		i.putExtra(Intent.EXTRA_TEXT,text);
		try {
			mContext.startActivity(Intent.createChooser(i, "·ÖÏí"));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
