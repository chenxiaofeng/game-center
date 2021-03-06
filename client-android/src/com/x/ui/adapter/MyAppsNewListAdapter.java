/**   
* @Title: MyAppsNewListAdapter.java
* @Package com.x.ui.adapter
* @Description: TODO(用一句话描述该文件做什么)

* @date 2014-7-7 下午3:41:27
* @version V1.0   
*/

package com.x.ui.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.x.R;
import com.x.business.favorite.FavoriteManage;
import com.x.business.skin.SkinConfigManager;
import com.x.business.skin.SkinConstan;
import com.x.business.statistic.DataEyeManager;
import com.x.business.statistic.StatisticConstan;
import com.x.business.update.UpdateManage;
import com.x.db.DownloadEntityManager;
import com.x.publics.download.DownloadManager;
import com.x.publics.download.DownloadTask;
import com.x.publics.model.DownloadBean;
import com.x.publics.model.MyAppsBean;
import com.x.publics.model.MyAppsBean.ListType;
import com.x.publics.utils.LogUtil;
import com.x.publics.utils.PackageUtil;
import com.x.publics.utils.ResourceUtil;
import com.x.publics.utils.StorageUtils;
import com.x.publics.utils.Utils;

/**
* @ClassName: MyAppsNewListAdapter
* @Description: TODO(这里用一句话描述这个类的作用)

* @date 2014-7-7 下午3:41:27
* 
*/

public class MyAppsNewListAdapter extends ArrayListBaseAdapter<MyAppsBean> implements SectionIndexer {

	WeakHashMap<Integer, TextView> mHeaderViewPool = new WeakHashMap<Integer, TextView>();

	public MyAppsNewListAdapter(Activity context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.my_apps_new_item, null);
			}
			final MyAppsBean appsBean = mList.get(position);
			final MyAppsNewViewHolder holder = new MyAppsNewViewHolder(convertView);
			convertView.setTag(appsBean.getOriginalUrl());//全量包url
			holder.initData(appsBean, context);
			holder.setSkinTheme(context);

			holder.arrowIv.setBackgroundResource(R.drawable.ic_download_manager_arrow_down);
			holder.downloadPauseTv.setOnClickListener(new MyBtnListener(holder, appsBean));
			holder.downloadContinueTv.setOnClickListener(new MyBtnListener(holder, appsBean));
			holder.installTv.setOnClickListener(new MyBtnListener(holder, appsBean));
			holder.upgradeTv.setOnClickListener(new MyBtnListener(holder, appsBean));
			holder.appIconIv.setOnClickListener(new MyBtnListener(holder, appsBean));
			convertView.setOnClickListener(null);

			//根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);

			//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				holder.catalogRl.setVisibility(View.VISIBLE);
				if (appsBean.getListType() == ListType.UPDATE) { //update
					holder.catalogTypeTv.setText(ResourceUtil.getString(context, R.string.app_new_update));
					holder.catalogNumTv.setText(getCategoryNumStatusText(ListType.UPDATE));
					holder.catalogNumTv.setTextColor(context.getResources().getColor(R.color.white));
					SkinConfigManager.getInstance().setViewBackground(context, holder.catalogNumTv,
							SkinConstan.BTN_AND_PROGRESS_THEME_BG);
					mHeaderViewPool.put(ListType.UPDATE, holder.catalogNumTv);
				} else if (appsBean.getListType() == ListType.DOWNLOAD) { // downloading
					holder.catalogTypeTv.setText(ResourceUtil.getString(context, R.string.app_new_downloading));
					holder.catalogNumTv.setText(getCategoryNumStatusText(ListType.DOWNLOAD));
					holder.catalogNumTv.setTextColor(context.getResources().getColor(R.color.white));
					SkinConfigManager.getInstance().setViewBackground(context, holder.catalogNumTv,
							SkinConstan.BTN_AND_PROGRESS_THEME_BG);
					mHeaderViewPool.put(ListType.DOWNLOAD, holder.catalogNumTv);
				} else if (appsBean.getListType() == ListType.UNINSTALL) { // ready to install 
					holder.catalogTypeTv.setText(ResourceUtil.getString(context, R.string.app_new_install));
					holder.catalogNumTv.setText(""
							+ DownloadEntityManager.getInstance().getAllUnInstallAppsDownloadCount());
					holder.catalogNumTv.setTextColor(context.getResources().getColor(R.color.deep_s_gray));
					Utils.setBackgroundDrawable(holder.catalogNumTv, null);
				}
				holder.catalogNumTv.setOnClickListener(new CategoryNumOnClickListener());
			} else {
				holder.catalogRl.setVisibility(View.GONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		setSkinTheme(convertView); // set skin theme

		return convertView;
	}

	private void setSkinTheme(View convertView) {
		View view = convertView.findViewById(R.id.man_top_rl);
		SkinConfigManager.getInstance().setViewBackground(context, view, SkinConstan.LIST_VIEW_ITEM_BG);
	}

	private class MyBtnListener implements OnClickListener {
		MyAppsNewViewHolder viewHolder;
		MyAppsBean appsBean;

		public MyBtnListener(MyAppsNewViewHolder viewHolder, MyAppsBean appsBean) {
			this.viewHolder = viewHolder;
			this.appsBean = appsBean;
		}

		@Override
		public void onClick(View v) {
			DownloadBean downloadBean = DownloadEntityManager.getInstance().getDownloadBeanByResId(
					"" + appsBean.getApkId(), "" + appsBean.getVersionCode());
			switch (v.getId()) {
			case R.id.man_upgrade_tv:
				DataEyeManager.getInstance().source(StatisticConstan.SrcName.UPGRADE, 0, null, 0, null, null, false);
				UpdateManage.getInstance(context).addDownload(appsBean, context, false, true);
				break;
			case R.id.man_download_install_tv:
				// to install
				if (downloadBean == null)
					return;
				if (downloadBean.isPatch()) {
					LogUtil.getLogger().d(
							"test",
							"packageName:" + downloadBean.getPackageName() + ",versionName:"
									+ downloadBean.getVersionName());
					if (UpdateManage.getInstance(context).isNewApkFileExit(downloadBean.getPackageName(),
							downloadBean.getVersionName())) {
						PackageUtil.installApk(
								context,
								UpdateManage.getInstance(context).getNewPatchApkPath(downloadBean.getPackageName(),
										downloadBean.getVersionName()));
					} else if (!Utils.isAppExit(downloadBean.getPackageName(), context)) { // 低版本被卸载，下载全量包
						showReDownloadFulldoseDialog(
								downloadBean,
								ResourceUtil.getString(context, R.string.dialog_redownload_full_dose,
										downloadBean.getName()));
					} else {//合并失败，下载增量包
						showReDownloadPatchDialog(downloadBean, ResourceUtil.getString(context,
								R.string.dialog_redownload_incremental_upgrade, downloadBean.getName()));
					}
				} else if (StorageUtils.isFileExit(downloadBean.getLocalPath())) {
					PackageUtil.installApk(context, downloadBean.getLocalPath());
				} else { //文件不存在，正常重下流程
					showReDownloadDialog(
							downloadBean,
							ResourceUtil.getString(context, R.string.dialog_redownload_file_not_found,
									downloadBean.getName()));
				}

				break;
			case R.id.man_download_pause_tv:
				if (downloadBean == null)
					return;
				DownloadManager.getInstance().pauseDownload(context, downloadBean.getUrl());
				UpdateManage.getInstance(context).markManualDownloadNetwork(appsBean.getApkId());
				FavoriteManage.getInstance(context).markFavoriteManualDownloadNetwork(appsBean.getApkId());
				viewHolder.downloadPauseTv.setVisibility(View.INVISIBLE);
				viewHolder.downloadContinueTv.setVisibility(View.VISIBLE);

				break;
			case R.id.man_download_continue_tv:
				if (downloadBean == null)
					return;
				DownloadManager.getInstance().continueDownload(context, downloadBean.getUrl(), appsBean.getApkId());
				break;
			case R.id.man_app_icon_iv:
				Utils.launchAnotherApp(context, appsBean.getPackageName());
				break;
			}

		}
	}

	private class CategoryNumOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			TextView categoryNumtv = (TextView) v;
			String text = categoryNumtv.getText().toString();
			if (text.contains("Pause")) {
				pauseAllApp();
			} else if (text.contains("Download")) {
				downloadAllApp();
			} else if (text.contains("Update")) {
				downloadAllAppUpdate();
			}

		}

	}

	private class UpdateAllApp extends Thread {

		@Override
		public void run() {
			DownloadManager.getInstance().updateAllApp(context);
		}
	}

	private class PauseAllApp extends Thread {

		@Override
		public void run() {
			DownloadManager.getInstance().pauseAllApp(context);
		}
	}

	private class DownloadAllApp extends Thread {

		@Override
		public void run() {
			DownloadManager.getInstance().downloadAllApp(context);
		}
	}

	private void downloadAllAppUpdate() {
		new UpdateAllApp().start();
	}

	private void pauseAllApp() {
		new PauseAllApp().start();
	}

	private void downloadAllApp() {
		new DownloadAllApp().start();
	}

	/** 
	* @Title: updateCategoryNumStatusText 
	* @Description: 更新下载按钮文字 
	* @param     
	* @return void    
	*/

	public void updateCategoryNumStatusText() {
		if (mHeaderViewPool.isEmpty())
			return;
		Iterator<Entry<Integer, TextView>> iter = mHeaderViewPool.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, TextView> entry = iter.next();
			TextView categoryNumtv = entry.getValue();
			int type = entry.getKey();
			if (categoryNumtv != null) {
				categoryNumtv.setText(getCategoryNumStatusText(type));
			}
		}
	}

	/** 
	* @Title: getCategoryNumStatusText 
	* @Description: 获取分段头文字 
	* @param @param listType
	* @param @return    
	* @return String    
	*/

	private String getCategoryNumStatusText(int listType) {
		String result = "";
		if (listType == ListType.UPDATE) {
			result = ResourceUtil.getString(context, R.string.app_new_update_all, "" + getNotUpdateCount());
		} else if (listType == ListType.DOWNLOAD) {
			ArrayList<DownloadBean> pauseAppList = DownloadEntityManager.getInstance().getAllAppPaused();
			if (pauseAppList.isEmpty()) { //当所有都在下载时显示pause all
				result = ResourceUtil.getString(context, R.string.app_new_pause_all, ""
						+ DownloadEntityManager.getInstance().getAllUnFinishedAppsDownloadCount());
			} else { // 当有暂停时显示
				result = ResourceUtil.getString(context, R.string.app_new_download_all, ""
						+ DownloadEntityManager.getInstance().getAllUnFinishedAppsDownloadCount());
			}
		}
		return result;
	}

	private int getNotUpdateCount() {
		int i = 0;
		if (mList != null && !mList.isEmpty()) {
			for (MyAppsBean appsBean : mList) {
				if (appsBean.getListType() == ListType.UPDATE) {
					i++;
				}
			}
		}
		return i;
	}

	/** 
	* @Title: showReDownloadDialog 
	* @Description: 正常重新下载 
	* @param @param downloadBean
	* @param @param tips    
	* @return void    
	*/

	private void showReDownloadDialog(final DownloadBean downloadBean, String tips) {

		DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DownloadManager.getInstance().deleteDownload(context, downloadBean.getUrl());

				DownloadBean redownloadBean = new DownloadBean(downloadBean.getUrl(), downloadBean.getName(),
						downloadBean.getTotalBytes(), 0, downloadBean.getIconUrl(), downloadBean.getMediaType(),
						downloadBean.getResourceId(), downloadBean.getVersionName(), downloadBean.getPackageName(),
						DownloadTask.TASK_DOWNLOADING, downloadBean.getTotalBytes(), downloadBean.getVersionCode(),
						downloadBean.getAppId(), downloadBean.getCategoryId(), downloadBean.getStars(), false,
						downloadBean.getOriginalUrl());
				DownloadManager.getInstance().addDownload(context, redownloadBean);
			}
		};

		DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		Utils.showDialog(context, ResourceUtil.getString(context, R.string.warm_tips), tips,
				ResourceUtil.getString(context, R.string.confirm), positiveListener,
				ResourceUtil.getString(context, R.string.cancel), negativeListener);

	}

	/** 
	* @Title: showReDownloadFulldoseDialog 
	* @Description: 重新下载全量包 
	* @param @param downloadBean
	* @param @param tips    
	* @return void    
	*/

	private void showReDownloadFulldoseDialog(final DownloadBean downloadBean, String tips) {

		DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DownloadManager.getInstance().deleteDownload(context, downloadBean.getUrl());

				DownloadBean redownloadBean = new DownloadBean(downloadBean.getOriginalUrl(), downloadBean.getName(),
						downloadBean.getTotalBytes(), 0, downloadBean.getIconUrl(), downloadBean.getMediaType(),
						downloadBean.getResourceId(), downloadBean.getVersionName(), downloadBean.getPackageName(),
						DownloadTask.TASK_DOWNLOADING, downloadBean.getTotalBytes(), downloadBean.getVersionCode(),
						downloadBean.getAppId(), downloadBean.getCategoryId(), downloadBean.getStars(), false,
						downloadBean.getOriginalUrl());
				DownloadManager.getInstance().addDownload(context, redownloadBean);
			}
		};

		DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		Utils.showDialog(context, ResourceUtil.getString(context, R.string.warm_tips), tips,
				ResourceUtil.getString(context, R.string.confirm), positiveListener,
				ResourceUtil.getString(context, R.string.cancel), negativeListener);

	}

	/** 
	* @Title: showReDownloadPatchDialog 
	* @Description: 重下增量包
	* @param @param downloadBean
	* @param @param tips    
	* @return void    
	*/

	private void showReDownloadPatchDialog(final DownloadBean downloadBean, String tips) {

		DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DownloadManager.getInstance().deleteDownload(context, downloadBean.getUrl());

				DownloadBean redownloadBean = new DownloadBean(downloadBean.getUrl(), downloadBean.getName(),
						downloadBean.getTotalBytes(), 0, downloadBean.getIconUrl(), downloadBean.getMediaType(),
						downloadBean.getResourceId(), downloadBean.getVersionName(), downloadBean.getPackageName(),
						DownloadTask.TASK_DOWNLOADING, downloadBean.getTotalBytes(), downloadBean.getVersionCode(),
						downloadBean.getAppId(), downloadBean.getCategoryId(), downloadBean.getStars(), true,
						downloadBean.getOriginalUrl());
				DownloadManager.getInstance().addDownload(context, redownloadBean);
			}
		};

		DialogInterface.OnClickListener negativeListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		Utils.showDialog(context, ResourceUtil.getString(context, R.string.warm_tips), tips,
				ResourceUtil.getString(context, R.string.confirm), positiveListener,
				ResourceUtil.getString(context, R.string.cancel), negativeListener);

	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			int listType = mList.get(i).getListType();
			if (listType == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return mList.get(position).getListType();
	}

}
