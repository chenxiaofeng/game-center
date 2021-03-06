package com.x.ui.activity.wallpaper;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.x.R;
import com.nostra13.universalimageloader.core.assist.ImageType;
import com.x.business.skin.SkinConfigManager;
import com.x.business.skin.SkinConstan;
import com.x.publics.http.DataFetcher;
import com.x.publics.http.model.Pager;
import com.x.publics.http.model.WallpaperRequest;
import com.x.publics.http.model.WallpaperResponse;
import com.x.publics.http.model.WallpaperRequest.WallpaperRequestData;
import com.x.publics.http.volley.VolleyError;
import com.x.publics.http.volley.Response.ErrorListener;
import com.x.publics.http.volley.Response.Listener;
import com.x.publics.model.WallpaperBean;
import com.x.publics.utils.Constan;
import com.x.publics.utils.JsonUtil;
import com.x.publics.utils.LogUtil;
import com.x.publics.utils.NetworkImageUtils;
import com.x.publics.utils.NetworkUtils;
import com.x.publics.utils.ResourceUtil;
import com.x.publics.utils.ToastUtil;
import com.x.ui.activity.base.BaseFragment;
import com.x.ui.view.pulltorefresh.PullToRefreshBase;
import com.x.ui.view.pulltorefresh.PullToRefreshScrollView;
import com.x.ui.view.pulltorefresh.PullToRefreshBase.Mode;
import com.x.ui.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.x.ui.view.pulltorefresh.PullToRefreshBase.State;
import com.x.ui.view.pulltorefresh.extra.SoundPullEventListener;
import com.x.ui.view.quiltview.LQuiltView;

/**
 * @ClassName: WallpaperTopFragment
 * @Desciption: TODO
 
 * @Date: 2014-3-13 下午5:00:54
 */

public class WallpaperCategoryHotFragment extends BaseFragment {

	private LQuiltView mQuiltView;
	private View loadingPb, loadingLogo;
	private PullToRefreshScrollView mScrollView;
	private ArrayList<ImageView> imageViews = new ArrayList<ImageView>();
	private ArrayList<WallpaperBean> imageList = new ArrayList<WallpaperBean>();

	private WallpaperRequest request;
	private Pager pager;
	private View loadingView;
	private View errorView;
	private View emptyView;
	private int pageNum = 1;
	private int pageSize = 12;
	private View rootView = null;
	private int categoryId;

	public static Fragment newInstance(Bundle bundle) {
		WallpaperCategoryHotFragment fragment = new WallpaperCategoryHotFragment();
		if (bundle != null)
			fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		setSkinTheme();
		categoryId = mActivity.getIntent().getIntExtra("categoryId", 0);
		// 加载数据
		if (imageList.isEmpty()) {
			getData(1);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_wallpaper_category_hot, container, false);
		errorView = rootView.findViewById(R.id.e_error_rl);
		emptyView = rootView.findViewById(R.id.empty_rl);
		loadingView = rootView.findViewById(R.id.l_loading_rl);
		loadingPb = loadingView.findViewById(R.id.loading_progressbar);
		loadingLogo = loadingView.findViewById(R.id.loading_logo);

		mQuiltView = (LQuiltView) rootView.findViewById(R.id.quiltview);
		mQuiltView.setChildPadding(5);

		mScrollView = (PullToRefreshScrollView) rootView.findViewById(R.id.ptr_scrollview);
		mScrollView.setOnRefreshListener(onRefreshListener);
		mScrollView.setMode(Mode.DISABLED);

		SoundPullEventListener<ScrollView> soundListener = new SoundPullEventListener<ScrollView>(mActivity);
		soundListener.addSoundEvent(State.REFRESH_TO_RESET, R.raw.refresh);
		mScrollView.setOnPullEventListener(soundListener);

		mScrollView.setVisibility(View.GONE);
		loadingView.setVisibility(View.VISIBLE);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void getData(int page) {
		request = new WallpaperRequest();
		pager = new Pager(page);
		pager.setPs(pageSize);
		request.setPager(pager);
		request.setColumn(getCategory());
		request.setCategoryId(categoryId);
		request.setData(new WallpaperRequestData(getCt()));
		DataFetcher.getInstance().getWallpaperCategoryDetail(request, myResponseListent, myErrorListener, true);
	}

	public int getCt() {
		return Constan.Ct.WALLPAPER_CATEGORY;
	}

	public String getCategory() {
		return Constan.Category.CATEGORY_HOT;
	}

	private Listener<JSONObject> myResponseListent = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject response) {
			emptyView.setVisibility(View.GONE);
			loadingView.setVisibility(View.GONE);
			mScrollView.setVisibility(View.VISIBLE);
			mScrollView.onRefreshComplete();
			LogUtil.getLogger().d("response==>" + response.toString());
			WallpaperResponse wallpaperResponse = (WallpaperResponse) JsonUtil.jsonToBean(response,
					WallpaperResponse.class);
			if (wallpaperResponse != null && wallpaperResponse.state.code == 200) {
				if (!wallpaperResponse.imagelist.isEmpty()) {
					imageList.addAll(wallpaperResponse.imagelist);
					// 添加图片元素到容器中
					addItemToContainer();

					if (wallpaperResponse.isLast) {
						cancleGridViewScorllable();
					} else {
						mScrollView.setMode(Mode.BOTH);
					}
				} else {
					if (imageList.isEmpty()) {
						// 没有数据，显示emptyView
						showEmptyView();
					} else {
						cancleGridViewScorllable();
					}
				}

			} else {
				showErrorView();
			}
		}
	};

	private ErrorListener myErrorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			loadingView.setVisibility(View.GONE);
			mScrollView.setVisibility(View.VISIBLE);
			mScrollView.onRefreshComplete();
			showErrorView();
			error.printStackTrace();
		}
	};

	private OnRefreshListener2<ScrollView> onRefreshListener = new OnRefreshListener2<ScrollView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
			//			if (pageNum == 1) {
			//				if(!NetworkUtils.isNetworkAvailable(mActivity)) {
			//					showErrorView();
			//				}else{
			//					mScrollView.onRefreshComplete();
			//				}
			//			} else {
			mQuiltView.clear(); // 清空界面
			imageList.clear();
			pageNum = 1;
			getData(1);
			//			}
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
			getData(++pageNum);
		}

	};

	/**
	 * @desc: 显示没有数据界面
	 * @params: TODO
	 * @return: void
	 */
	private void showEmptyView() {
		emptyView.setVisibility(View.VISIBLE);
		errorView.setVisibility(View.GONE);
		mScrollView.setVisibility(View.GONE);
	}

	/**
	 * @desc: 显示错误界面
	 * @params: TODO
	 * @return: void
	 */
	private void showErrorView() {
		mQuiltView.clear(); // 清空界面
		imageList.clear();
		if (pageNum > 1) {
			--pageNum;
		}
		if (imageList.isEmpty()) {
			mScrollView.setVisibility(View.GONE);
			errorView.setVisibility(View.VISIBLE);
			errorView.findViewById(R.id.e_retry_btn).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!NetworkUtils.isNetworkAvailable(mActivity)) {
						ToastUtil.show(mActivity, ResourceUtil.getString(mActivity, R.string.network_canot_work),
								Toast.LENGTH_SHORT);
						return;
					}
					errorView.setVisibility(View.GONE);
					mScrollView.setVisibility(View.GONE);
					loadingView.setVisibility(View.VISIBLE);
					getData(1);
				}
			});
		}
	}

	private void cancleGridViewScorllable() {
		if (mScrollView != null)
			mScrollView.setMode(Mode.PULL_FROM_START);
	}

	public ViewPager getPargentViewPager() {
		return ((WallpaperFragment) getParentFragment()).mViewPager;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LogUtil.getLogger().d("FragmentA==============onDestroyView");
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 将图片元素到集合中
	 */
	private void addItemToContainer() {
		int size = imageList.size();
		imageViews.clear(); // 必须先清空，否则报错..
		for (int i = (pageNum - 1) * pageSize; i < pageSize * pageNum && i < size; i++) {
			imageViews.add(addImage(imageList.get(i), i));
		}
		handler.sendMessage(handler.obtainMessage(23, imageViews));
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 23:
				mQuiltView.addPatchImages((ArrayList<ImageView>) msg.obj, mActivity);
				break;
			}
		}
	};

	/**
	 * 添加图片
	 * 
	 * @param imageUrl
	 * @param columnIndex
	 */
	private ImageView addImage(final WallpaperBean wallpaperBean, final int index) {
		final ImageView imageView = (ImageView) LayoutInflater.from(mActivity).inflate(R.layout.quiltview_item, null);
		// 加载图片资源（中图）
		NetworkImageUtils.load(mActivity, ImageType.NETWORK, wallpaperBean.getBiglogo(),
				R.drawable.banner_default_picture, R.drawable.banner_default_picture, imageView);
		// 按钮触摸事件
		imageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					imageView.setAlpha(255);
					Intent intent = new Intent(mActivity, WallpaperDetailActivity.class);
					intent.putParcelableArrayListExtra("data", imageList);
					intent.putExtra("picIndex", index);
					mActivity.startActivity(intent);
					break;

				case MotionEvent.ACTION_MOVE:
					// imageView.setAlpha(255);
					break;

				case MotionEvent.ACTION_DOWN:
					imageView.setAlpha(180);
					break;

				case MotionEvent.ACTION_CANCEL:
					imageView.setAlpha(255);
					break;

				default:
					break;
				}

				return true;
			}
		});
		return imageView;
	}

	/**
	* @Title: setSkinTheme 
	* @Description: TODO 
	* @return void
	 */
	private void setSkinTheme() {
		SkinConfigManager.getInstance().setViewBackground(mActivity, loadingLogo, SkinConstan.LOADING_LOGO);
		SkinConfigManager.getInstance().setIndeterminateDrawable(mActivity, (ProgressBar) loadingPb,
				SkinConstan.LOADING_PROGRASS_BAR);
	}
}
