
package com.hykj.gamecenter.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.hykj.gamecenter.App;
import com.hykj.gamecenter.GlobalConfigControllerManager;
import com.hykj.gamecenter.R;
import com.hykj.gamecenter.controller.ProtocolListener.KEY;
import com.hykj.gamecenter.download.ApkDownloadManager;
import com.hykj.gamecenter.download.DownloadService;
import com.hykj.gamecenter.download.DownloadTask;
import com.hykj.gamecenter.download.DownloadTaskStateListener;
import com.hykj.gamecenter.fragment.DownloadExpandListFragment;
import com.hykj.gamecenter.fragment.UpdateListFragment;
import com.hykj.gamecenter.logic.ApkInstalledManager;
import com.hykj.gamecenter.logic.ApkInstalledManager.InstallFinishListener;
import com.hykj.gamecenter.logic.BroadcastManager;
import com.hykj.gamecenter.logic.NotificationCenter;
import com.hykj.gamecenter.logic.entry.MsgDownload;
import com.hykj.gamecenter.mta.MtaUtils;
import com.hykj.gamecenter.protocol.Apps.AppInfo;
import com.hykj.gamecenter.statistic.ReportConstants;
import com.hykj.gamecenter.statistic.StatisticManager;
import com.hykj.gamecenter.ui.widget.AppInfoViewPager;
import com.hykj.gamecenter.ui.widget.CSCommonActionBar;
import com.hykj.gamecenter.ui.widget.CSDownloadManageActionBar;
import com.hykj.gamecenter.ui.widget.CSDownloadManageActionBar.OnActionBarClickListener;
import com.hykj.gamecenter.ui.widget.CSDownloadManageEditBar;
import com.hykj.gamecenter.ui.widget.CSDownloadManageEditBar.OnHeaderBarClickListener;
import com.hykj.gamecenter.ui.widget.CSToast;
import com.hykj.gamecenter.ui.widget.PagerSlidingTabStrip;
import com.hykj.gamecenter.ui.widget.PagerSlidingTabStrip.LayoutTabProvider;
import com.hykj.gamecenter.ui.widget.PagerSlidingTabStrip.OnPageChangedRefreshMainUIListener;
import com.hykj.gamecenter.utils.Logger;
import com.hykj.gamecenter.utils.SystemBarTintManager;
import com.hykj.gamecenter.utils.UITools;
import com.hykj.gamecenter.utilscs.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 下载管理页
 * 
 *
 */
public class AppManageActivity extends Activity implements InstallFinishListener {

    private CSDownloadManageActionBar mActionBar;
    private CSDownloadManageEditBar mCSHeaderBar;
    // tabs
    private PagerSlidingTabStrip mTabs;
    private AppInfoViewPager mViewPager;
    private SectionsPagerAdapter mTabsViewPagerAdapter;

    //    private DownloadListFragment mDownloadListFragment;
    private DownloadExpandListFragment mDownloadedFragment;
    private UpdateListFragment mUpdateListFragment;

    private ApkInstalledManager mApkInstalledManager;
    private ApkDownloadManager mApkDownloadManager;
    private StatisticManager mStatisticManager;

    public static final String GOTO_UPDATE = "goto_update";
    public static final String FROM_NOTIFICATION = "FROM_NOTIFICATION"; // 是否是应用主界面没有启动时从通知进入的该界面
    public static final String UPDATE_ALL = "update_all";   //一键更新

    public boolean mbHomeNotRunFromNotification = false; // 是否是应用主界面没有启动时从通知进入的该界面
    public static boolean mbHomeKeyPressed = false; // homekey是否按下

    private final static String TAG = "AppManageActivity";

    private final static int ACTION_BAR_FINISH = 1002;
    private final static int ACTION_BAR_EDIT = 1003;
    private final static int ACTION_BAR_UPGRADE = 1004;
    private final static int ACTION_BAR_DEL = 1005;

    // 按钮的状态
    private int actionBarLeftBtuStatus = ACTION_BAR_FINISH;
    private int actionBarRightBtuStatus = ACTION_BAR_DEL;

    private boolean switchToUpdate = false;

    private boolean actionToUpdate = false;

    /** 维护正在下载任务列表 */
    private final ArrayList<DownloadTask> mCheckToDelDownloadInfoList = new ArrayList<DownloadTask>();

    /** 维护下载历史任务列表 */
    private final ArrayList<DownloadTask> mCheckToDelDownloadHistoryList = new ArrayList<DownloadTask>();

    private final ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();

    private Context mContext;

    public static void resetHomeKeyPressed() {
        if (mbHomeKeyPressed) {
            mbHomeKeyPressed = false;
        }
    }

    IntentFilter intentFilter = null;

    NotificationMessageReceiver notificationMessageReceiver = null;

    public static class NotificationMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.i(TAG, "NotificationMessageReceiver onReceive action = " + intent.getAction());
            String action = intent.getAction();
            if (NotificationCenter.ACTION_NOTIFICATION_MSG.equals(action)) {
                String buttonID = intent.getStringExtra(NotificationCenter.KEY_BUTTON_ID);
                if (NotificationCenter.BUTTON_ID_ONEKEYUPDATE.equals(buttonID)) {
                    Log.d(TAG, "receive one key update msg");
                    // 一键升级按钮
                    /*
                     * Handler handler = UpdateListFragment.getHandler();
                     * Log.d(TAG, "handler = " + handler); if (handler != null)
                     * {
                     * handler.sendEmptyMessage(MSG_CONSTANTS.MSG_ONE_KEY_UPDATE
                     * ); }
                     */
                    UpdateListFragment.updateAll();
                    NotificationCenter.getInstance().cancelUpdateNotify();
                }
            }
            else if (NotificationCenter.ACTION_NOTIFICATION_HOMEKEY_MSG.equals(action)) {
                // homekey按下
                mbHomeKeyPressed = true;
            }
        }
    }

    public interface IRefreshListener {
        void refreshUI(Object obj);
    }

    public void refreshLeftActionBar(int status) {
        actionBarLeftBtuStatus = status;
    }

    public void refreshRightActionBar(int status) {
        actionBarRightBtuStatus = status;
    }

    public void setScroll(boolean b) {
        mViewPager.setCanScroll(b);
    }

    // //双击导航栏
    // private OnDoubleClickTabListener mOnDoubleClickTabListener = new
    // OnDoubleClickTabListener( )
    // {
    // @Override
    // public void onDoubleClickTabToHandle( int position )
    // {}
    // };

    //
    private final OnPageChangedRefreshMainUIListener mOnPageChangeRefreshMainUIListener = new OnPageChangedRefreshMainUIListener() {

        @Override
        public void onPageChangedRefreshMainUI(int position) {
            switch (position) {
                case 0:
                    mHandler.sendEmptyMessage(MsgDownload.MSG_SCROLL_TO_DOWNLOAD_MANAGER);
                    break;
                case 1:
                    mHandler.sendEmptyMessage(MsgDownload.MSG_SCROLL_TO_APP_UPGRADE);
                    break;
                default:
                    break;
            }
        }
    };

    private int chnNo = 0;

    private void handleAction() {
        String action = getIntent().getAction();
        LogUtils.e("appstore action=" + (action == null ? "" : action));
        if (action != null && !action.isEmpty()
                && action.equals("com.hykj.gamecenter.activity.AppManageActivity")) {
            chnNo = getIntent().getIntExtra("chnNo", 0);
            //            App.init();
            App.initChnNo(chnNo);
            actionToUpdate = true;
        }
    }

    private void initFragment() {

        /* if (mDownloadListFragment == null)
             mDownloadListFragment = new DownloadListFragment();
         mDownloadListFragment.setHandler(mHandler);*/
        if (mDownloadedFragment == null)
            mDownloadedFragment = new DownloadExpandListFragment();
        mDownloadedFragment.setHandler(mHandler);

        if (mUpdateListFragment == null)
            mUpdateListFragment = new UpdateListFragment();

        mFragmentList.clear();
        //        mFragmentList.add(mDownloadListFragment);
        mFragmentList.add(mDownloadedFragment);
        mFragmentList.add(mUpdateListFragment);

        if (mTabsViewPagerAdapter == null)
            mTabsViewPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), mFragmentList);
        setViewPager();

    }

    // 判断应用升级界面中是否有不在下载任务当中，确定全部更新按钮是否可编辑
    public boolean anyUpdateInfoIsNotInDownloadTask() {
        List<AppInfo> list = mApkInstalledManager.getAppUpdateInfo();

        if (list.size() > 0) {
            for (AppInfo ai : list) {
                if (!mApkDownloadManager.isAppInTaskList(ai.appId)) {
                    Logger.i("AppManageActivity", "isAppInTaskList true");
                    return true;
                }
            }
        }
        return false;
    }

    // 更新全部更新按钮的状态
    public void refreshActionBarWhenStatusIsAllUpgrade() {
        // LogUtils.e(
        // "refreshActionBarWhenStatusIsAllUpgrade anyUpdateInfoIsNotInDownloadTask ="
        // + anyUpdateInfoIsNotInDownloadTask( ) );
        if (actionBarRightBtuStatus == ACTION_BAR_UPGRADE) {
            if (anyUpdateInfoIsNotInDownloadTask()) {
                Logger.i("AppManageActivity",
                        "refreshActionBarWhenStatusIsAllUpgrade setEnabled = true");
                /* mActionBar.getmRightEditButton().setVisibility(View.VISIBLE); */
                mUpdateListFragment.ShowOnekeyUpdateButton(true);
            }
            else {
                Logger.i("AppManageActivity",
                        "refreshActionBarWhenStatusIsAllUpgrade setEnabled = false");
                /* mActionBar.getmRightEditButton().setVisibility(View.GONE); */
                mUpdateListFragment.ShowOnekeyUpdateButton(false);
            }
        }

    }

    // 更新编辑按钮的状态
    public void refreshActionBarWhenStatusIsEdit() {

        // LogUtils.e(
        // "refreshActionBarWhenStatusIsEdit mApkDownloadManager.getTaskList( )"
        // + mApkDownloadManager.getTaskList( ).size( ) );
        switch (actionBarRightBtuStatus){
            case ACTION_BAR_EDIT:
            if (mApkDownloadManager.getTaskCount() > 0
                    || mApkDownloadManager.getDownloadedTaskCount() > 0) {
                //            Logger.i("AppManageActivity", "refreshActionBarWhenStatusIsEdit setEnabled = true");
                mActionBar.getmRightEditIcon().setVisibility(View.VISIBLE);
                // mActionBar.getmRightEditButton( ).setTextColor( getResources(
                // ).getColor( R.color.white ) );
            }
            else {
                //            Logger.i("AppManageActivity", "refreshActionBarWhenStatusIsEdit setEnabled = false");
                mActionBar.getmRightEditIcon().setVisibility(View.GONE);
                // mActionBar.getmRightEditButton( ).setTextColor( getResources(
                // ).getColor( R.color.white_4c ) );
            }
            break;
        }



        /* if (mApkDownloadManager.getDownloadedTaskCount() > 0) {
             mActionBar.getmRightEditIcon().setVisibility(View.VISIBLE);
         }
         else {
             mActionBar.getmRightEditIcon().setVisibility(View.GONE);
         }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (App.getDevicesType() == App.PHONE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Logger.i("AppManagerActivity", "onCreate");
        setContentView(R.layout.activity_app_manage);
        SystemBarTintManager.useSystemBar(this, R.color.action_blue_color);
        mContext = this;

        mActionBar = (CSDownloadManageActionBar) findViewById(R.id.ActionBar);
        mActionBar.SetOnActionBarClickListener(actionBarListener);

        mCSHeaderBar = (CSDownloadManageEditBar) findViewById(R.id.headerbar);
        mCSHeaderBar.setOnHeaderBarClickListener(headerBarListener);
        // 设置标签页
        mTabs = mActionBar.getPagerSlidingTabStrip();
        // 自定义tab的样式
        mTabs.setUnderlineColorResource(R.color.transparent);
        mTabs.setIndicatorColorResource(R.color.transparent);
        mTabs.setDividerColorResource(R.color.transparent);
        mTabs.setmOnPageChangeRefreshMainUIListener(mOnPageChangeRefreshMainUIListener);
        mTabs.setTabPaddingLeftRight(0);

        mViewPager = (AppInfoViewPager) findViewById(R.id.tab_pager);

        mApkInstalledManager = ApkInstalledManager.getInstance();
        mApkDownloadManager = DownloadService.getDownloadManager();

        mApkInstalledManager.addInstallListener(this);

        mStatisticManager = StatisticManager.getInstance();

        mbHomeNotRunFromNotification = getIntent().getBooleanExtra(FROM_NOTIFICATION, false);
        handleAction();

        initFragment();

        resetHomeKeyPressed();
        notificationMessageReceiver = new NotificationMessageReceiver();
        intentFilter = new IntentFilter(NotificationCenter.ACTION_NOTIFICATION_HOMEKEY_MSG);

        switchToUpdate = getIntent().getBooleanExtra(GOTO_UPDATE, false);
        boolean updateAll = getIntent().getBooleanExtra(UPDATE_ALL, false);
        Logger.d("AppManagerActivity", "switchToUpdate = " + switchToUpdate);
        if (switchToUpdate) {
            // 当从通知栏进入管理中心时，需初始化
            //            App.init();// 这里会查询所有需要更新的应用
            mViewPager.setCurrentItem(1);
            mHandler.sendEmptyMessage(MsgDownload.MSG_SCROLL_TO_APP_UPGRADE);
            if (updateAll) {
                UpdateListFragment.updateAll();
                NotificationCenter.getInstance().cancelUpdateNotify();
            }
        }
        else if (actionToUpdate) {
            mViewPager.setCurrentItem(1);
            mHandler.sendEmptyMessage(MsgDownload.MSG_SCROLL_TO_APP_UPGRADE);
        }
        else {
            // 设置当前页显示
            setCurrentItem();
        }

        // 注册监听
        BroadcastManager.getInstance().registerReceiveres();

        // 开启traceView
        // Debug.startMethodTracing( "AppManagerActivity" );

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // homekey广播动态注册
        registerReceiver(notificationMessageReceiver, intentFilter);

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        unregisterReceiver(notificationMessageReceiver);

    }

    @Override
    public void onResume() {
        super.onResume();
        mApkDownloadManager.registerListener(downloadTaskStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // LogUtils.e( "onPause" );
        mApkDownloadManager.unregisterListener(downloadTaskStateListener);

        // 停止traceView
        // Debug.stopMethodTracing( );
    }

    public void setViewPager() {
        mViewPager.setAdapter(mTabsViewPagerAdapter);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources()
                        .getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);
        mTabs.setViewPager(mViewPager);

    }

    public void setCurrentItem() {

        int taskCount = mApkDownloadManager.getTaskCount();
        int downloadCount = mApkDownloadManager.getDownloadedTaskCount();

        int upgradeCount = mApkInstalledManager.getUpdateAppInfoCount();
        if (taskCount > 0 || downloadCount > 0) {
            mViewPager.setCurrentItem(0);
            mHandler.sendEmptyMessage(MsgDownload.MSG_SCROLL_TO_DOWNLOAD_MANAGER);
        }
        else if (taskCount == 0 && upgradeCount > 0) {
            mViewPager.setCurrentItem(1);
            // refreshActionBarWhenStatusIsAllUpgrade( );
            mHandler.sendEmptyMessage(MsgDownload.MSG_SCROLL_TO_APP_UPGRADE);
        }
        else {
            mViewPager.setCurrentItem(0);
            // 首次进入,初始化actionbar中按钮的状态
            // refreshActionBarWhenStatusIsEdit( );
            mHandler.sendEmptyMessage(MsgDownload.MSG_SCROLL_TO_DOWNLOAD_MANAGER);
        }

        /*if (downloadCount > 0) {
            mViewPager.setCurrentItem(0);
            mHandler.sendEmptyMessage(MSG_SCROLL_TO_DOWNLOAD_MANAGER);
        }*/
    }

    // luncher 为 singleTask 下不执行onCreate
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean switchToUpdate = intent.getBooleanExtra(GOTO_UPDATE, false);
        Logger.d("AppManagerActivity", "switchToUpdate = " + switchToUpdate);
        if (switchToUpdate) {
            mViewPager.setCurrentItem(1);
            mHandler.sendEmptyMessage(MsgDownload.MSG_SCROLL_TO_APP_UPGRADE);
        }
        else {
            // 设置当前页显示
            setCurrentItem();
        }
    }

    private final DownloadTaskStateListener downloadTaskStateListener = new DownloadTaskStateListener() {
        @Override
        public void onUpdateTaskState(DownloadTask task) {
            // LogUtils.e( "mDownloadListFragment=" + mDownloadListFragment );
            // LogUtils.e( "mUpdateListFragment=" + mUpdateListFragment );

            /*if (mDownloadListFragment != null) {
                mDownloadListFragment.onUpdateTaskState(task);
            }*/
            if (mDownloadedFragment != null) {
                mDownloadedFragment.onUpdateTaskState(task);
            }

            if (mUpdateListFragment != null) {
                mUpdateListFragment.onUpdateTaskState(task);
            }
        }

        @Override
        public void onUpdateTaskProgress(DownloadTask task) {
            // LogUtils.e( "mDownloadListFragment=" + mDownloadListFragment );
            // LogUtils.e( "mUpdateListFragment=" + mUpdateListFragment );

            /* if (mDownloadListFragment != null) {
                 mDownloadListFragment.onUpdateTaskProgress(task);
             }*/
            if (mDownloadedFragment != null) {
                mDownloadedFragment.onUpdateTaskProgress(task);
            }

            if (mUpdateListFragment != null) {
                mUpdateListFragment.onUpdateTaskProgress(task);
            }
        }

        @Override
        public void onUpdateTaskList(Object obj) {
            /* if (mDownloadListFragment != null) {
                 mDownloadListFragment.onUpdateTaskList(obj);
             }*/
            Logger.i(TAG, "mCheckToDelDownloadInfoList~~~~==" + mCheckToDelDownloadInfoList.size());
            Logger.i(TAG,
                    "mCheckToDelDownloadHistoryList~~~~==" + mCheckToDelDownloadHistoryList.size());
            if (mDownloadedFragment != null) {
                mDownloadedFragment.onUpdateTaskList(obj);
            }

            if (mUpdateListFragment != null) {
                mUpdateListFragment.onUpdateTaskList(obj);
            }

            if (obj != null) {

                if (obj instanceof String && !"".equals(obj)) {
                    CSToast.show(mContext, obj
                            + getString(R.string.unable_to_remove_the_task));
                    clearIsNeed((String) obj);
                }
                else {
                    CSToast.show(
                            mContext,
                            getString(R.string.success_to_remove_count_task,
                                    mCheckToDelDownloadInfoList.size()
                                            + mCheckToDelDownloadHistoryList.size()));
                    // 用户取消任务上报
                    batchReportCancelTask(mCheckToDelDownloadInfoList);
                }
                // LogUtils.e( "onUpdateTaskList mCheckToDelDownloadInfoList = "
                // + mCheckToDelDownloadInfoList.size( ) );
                //                mDownloadListFragment.updateAllCheckBoxNotChecked();
                /*mDownloadedFragment.updateAllCheckBoxNotChecked();
                changeBarLeftDelBtn();*/
            }
            //            deleteHistoryDownloadInfo();

            mHandler.sendEmptyMessage(MsgDownload.MSG_FINISH_EDIT);
            //            finishedEdit();

            /*if (mCheckToDelDownloadInfoList.size() <= 0) {
                mCSHeaderBar.setmHeaderTitle(getString(R.string.please_to_select_task));
            }*/
            // 判断任务列表是否全部删除，是 退出编辑状态
            // LogUtils.e( "编辑按钮的状态=" + actionBarRightBtuStatus );
            /*   if (mApkDownloadManager.getTaskCount() <= 0
                       && actionBarRightBtuStatus != ACTION_BAR_UPGRADE) {
                   finishedEdit();
               }*/
        }
    };

    public void clearIsNeed(String str) {
        String clearStr = str.substring(0, str.lastIndexOf(","));
        String[] clearStrs = clearStr.split(",");
        ArrayList<DownloadTask> needToClearList = new ArrayList<DownloadTask>();
        for (String s : clearStrs) {
            for (DownloadTask dt : mCheckToDelDownloadInfoList) {
                if (s.equals(dt.appName)) {
                    needToClearList.add(dt);
                    continue;
                }
            }
        }

        // LogUtils.e( "clearIsNeed needToClearList = " + needToClearList.size(
        // ) );
        // LogUtils.e( "clearIsNeed before mCheckToDelDownloadInfoList = " +
        // mCheckToDelDownloadInfoList.size( ) );
        mCheckToDelDownloadInfoList.removeAll(needToClearList);
        // LogUtils.e( "clearIsNeed after mCheckToDelDownloadInfoList = " +
        // mCheckToDelDownloadInfoList.size( ) );

        // 用户取消任务上报
        batchReportCancelTask(mCheckToDelDownloadInfoList);

        mCheckToDelDownloadInfoList.clear();
        // LogUtils.e( "clearIsNeed clear mCheckToDelDownloadInfoList = " +
        // mCheckToDelDownloadInfoList.size( ) );

        mCheckToDelDownloadInfoList.addAll(needToClearList);
        // LogUtils.e( "clearIsNeed add mCheckToDelDownloadInfoList = " +
        // mCheckToDelDownloadInfoList.size( ) );

        needToClearList.clear();

        // LogUtils.e( "clearIsNeed clear needToClearList = " +
        // needToClearList.size( ) );

    }

    // 批量上报用户取消下载任务
    public void batchReportCancelTask(ArrayList<DownloadTask> tasks) {
        for (DownloadTask dt : tasks) {
            ReportConstants.reportDownloadStop(dt.appId, dt.packId, dt.nFromPos,
                    ReportConstants.STAC_DOWNLOAD_APK_CANCEL_TASK, "用户批量删除下载任务");
            MtaUtils.trackDownloadCancel(dt.appName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Log.e( TAG , "AppManageActivity is detroying" );
        BroadcastManager.getInstance().unregisterReceiveres();

        // 在支持静默安装的版本上，安装完成后会删除list中的task，有时可能会出现如下问题
        // 不能在对一个List进行遍历的时候将其中的元素删除掉
        // 11-27 02:34:01.555: E/AndroidRuntime(2990): FATAL EXCEPTION:
        // Thread-442
        // 11-27 02:34:01.555: E/AndroidRuntime(2990):
        // java.util.ConcurrentModificationException
        // 11-27 02:34:01.555: E/AndroidRuntime(2990): at
        // java.util.ArrayList$ArrayListIterator.next(ArrayList.java:569)
        // 11-27 02:34:01.555: E/AndroidRuntime(2990): at
        // com.niuwan.gamecenter.download.DownloadTaskManager$SaveDataToDBRunnable.run(DownloadTaskManager.java:356)
        // 11-27 02:34:01.555: E/AndroidRuntime(2990): at
        // java.lang.Thread.run(Thread.java:856)
//        mApkDownloadManager.saveAllTaskToDB();
//        mApkDownloadManager.stopAllDownload();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter implements LayoutTabProvider {

        private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            if (fragmentList != null) {
                return fragmentList.get(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section_download).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section_update).toUpperCase(l);
            }
            return null;
        }

        @Override
        public int getPageLayoutResId(int position) {
            switch (position) {
                case 0:
                    return R.layout.download_manage_tab_item_left;
                case 1:
                    return R.layout.download_manage_tab_item_right;
            }
            return 0;
        }
    }

    private final OnActionBarClickListener actionBarListener = new OnActionBarClickListener() {

        @Override
        public void onActionBarClicked(int position) {

            switch (position) {
                case CSCommonActionBar.OnActionBarClickListener.RETURN_BNT:
                    onBackPressed();
                    break;
                case CSCommonActionBar.OnActionBarClickListener.LEFT_BNT:
                    handleLeftBtnClick();
                    break;
                case CSCommonActionBar.OnActionBarClickListener.RIGHT_EDIT_BNT:
                    handleRightBtnClick();
                    break;
                case CSCommonActionBar.OnActionBarClickListener.RIGHT_EDIT_ICON:
                    handleRightBtnClick();
                    break;
                default:
                    break;
            }

        }

    };

    private final OnHeaderBarClickListener headerBarListener = new OnHeaderBarClickListener() {

        @Override
        public void onHeaderBarClicked(int position) {
            switch (position) {
                case OnHeaderBarClickListener.LEFT_HANDLE_BNT:
                    handleLeftBtnClick();
                    break;
                case OnHeaderBarClickListener.RIGHT_HANDLE_BNT:
                    handleRightBtnClick();
                    break;
                default:
                    break;
            }

        }

    };

    public void handleLeftBtnClick() {
        switch (actionBarLeftBtuStatus) {
            case ACTION_BAR_FINISH:
                if (UITools.isFastDoubleClick()) {
                    return;
                }
                mHandler.sendEmptyMessage(MsgDownload.MSG_FINISH_EDIT);
                break;
            default:
                break;
        }
    }

    public void handleRightBtnClick() {
        Logger.i(TAG, "actionBarRightBtuStatus~~~~==" + actionBarRightBtuStatus);
        switch (actionBarRightBtuStatus) {
            case ACTION_BAR_EDIT: //可编辑状态   1003
                if (UITools.isFastDoubleClick()) {
                    return;
                }
                mHandler.sendEmptyMessage(MsgDownload.MSG_ACTION_EDIT);
                break;
            case ACTION_BAR_UPGRADE:
                if (UITools.isFastDoubleClick()) {
                    return;
                }
                // 全部更新升级
                UpdateListFragment.updateAll();
                break;
            case ACTION_BAR_DEL: //选中checkbox 点击删除状态   1005
                if (UITools.isFastDoubleClick()) {
                    return;
                }
                mApkDownloadManager.batchRemoveDownload(mCheckToDelDownloadInfoList,
                        mCheckToDelDownloadHistoryList);
                //                deleteHistoryDownloadInfo();
                break;
            default:
                break;
        }

    }

    /*private void deleteHistoryDownloadInfo() {
        int size = mCheckToDelDownloadHistoryList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                DownloadTask task = mCheckToDelDownloadHistoryList.get(i);
                int row = 0;
                try {
                    row = App.getAppContext().getContentResolver()
                            .delete(CSACContentProvider.DOWNLOADEDINFO_CONTENT_URI,
                                    DownloadInfoColumns.APP_ID + " = " + task.appId, null);
                    Logger.e(TAG, "移除任务  dinfo.appId = " + task.appId
                            + " dinfo.appName = "
                            + task.appName);
                    Logger.e(TAG, "移除任务  row === " + row);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }*/

    /*private void notifyAdapter() {
        mDownloadedFragment.updateAllCheckBoxNotChecked();
        changeBarLeftDelBtn();

        if (mCheckToDelDownloadHistoryList.size() <= 0) {
            mCSHeaderBar.setmHeaderTitle(getString(R.string.please_to_select_task));
        }
        // 判断任务列表是否全部删除，是 退出编辑状态
        // LogUtils.e( "编辑按钮的状态=" + actionBarRightBtuStatus );
        if (mApkDownloadManager.getTaskCount() <= 0
                && actionBarRightBtuStatus != ACTION_BAR_UPGRADE) {
            finishedEdit();
        }
    }*/

    public void finishedEdit() {
        Logger.i(TAG, "finishedEdit~~~~==");
        // LogUtils.e( "finishedEdit mCheckToDelDownloadInfoList" +
        // mCheckToDelDownloadInfoList.size( ) );
        if (mCSHeaderBar.isShown()) {
            mCSHeaderBar.startAnimation(AnimationUtils.loadAnimation(AppManageActivity.this,
                    R.anim.fade_out));
            mCSHeaderBar.setVisibility(View.GONE);
        }

        // 设置download list's checkbox 的状态 隐藏 
        //        mDownloadListFragment.downloadAdapterCheckBoxChanged(false);
        mDownloadedFragment.downloadAdapterCheckBoxChanged(false);
        mActionBar.getmRightEditIcon().setVisibility(View.VISIBLE);

        // 设置actionbar's right btn的状态
        refreshRightActionBar(ACTION_BAR_EDIT);

        // 设置actionbar's left btn的状态
        // refreshBarReturnBtn( false );

        // 设置viewPage 和 tabs 的状态
        updateViewPageAndTabState(true);
        mCheckToDelDownloadInfoList.clear();
        mCheckToDelDownloadHistoryList.clear();
        changeBarLeftDelBtn();

        refreshActionBarWhenStatusIsEdit();
        //        mDownloadListFragment.refreshDownloadList();
        mDownloadedFragment.refreshDownloadList();
    }

    // 改变viewPager and tabs 的状态
    public void updateViewPageAndTabState(boolean b) {
        setScroll(b);
        mTabs.enabledOrNotTabs(b);
    }

    public void refreshBarReturnBtn(boolean b) {
        mActionBar.getmLeftEditButton().setVisibility(b ? View.VISIBLE : View.GONE);
        mActionBar.getmReturnButton().setVisibility(b ? View.GONE : View.VISIBLE);
        mActionBar.getmLogoTextView().setVisibility(b ? View.GONE : View.VISIBLE);
        mActionBar.getmTitleView().setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {

        if (actionBarRightBtuStatus == ACTION_BAR_DEL) {
            mHandler.sendEmptyMessage(MsgDownload.MSG_FINISH_EDIT);//编辑状态退回到终止编辑状态
        }
        else {
            mHandler.sendEmptyMessage(MsgDownload.MSG_REFRESH_DOWNLOAD_LIST_ADAPTER);
            // super.onBackPressed();

            /*
             * Log.d(TAG, "mbHomeNotRunFromNotification = " +
             * mbHomeNotRunFromNotification + " mbHomeKeyPressed= " +
             * mbHomeKeyPressed); Log.d(TAG, "HomePageActivity isFinish = " +
             * HomePageActivity.IsHomePageFinish());
             * 
             * Log.d(TAG, "HomePageActivity is InRunningTasks:" +
             * Utils.isActivityInRunningTasksStack( AppManageActivity.this,
             * HomePageActivity.class.getName()));
             */
            // 如果程序没有运行
            // if (/* mbHomeNotRunFromNotification || mbHomeKeyPressed */
            // HomePageActivity.IsHomePageFinish()
            // || !Utils.isActivityInRunningTasksStack(
            // AppManageActivity.this,
            // HomePageActivity.class.getName()))

            // 判断当前的activity出栈后，ActivityName是否在是当期的栈顶
            // if (HomePageActivity.IsHomePageFinish()
            // || !Utils.isActivityInRunningTasksTopStack(
            // AppManageActivity.this,
            // HomePageActivity.class.getName()))
            // {
            // NotificationCenter.resetIsHomeNotRunFromNotification();
            // resetHomeKeyPressed();
            Intent intent = new Intent(this, HomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 重要1 ,xml中HomePageActivity要设置singleTop配合使用
            // intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            // 这个的意思是如果栈里已经存在，则将它拿到栈顶，不会启动新Activity
            // intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            // /*
            // * 现改为在xml中设置android:launchMode="singleInstance"，
            // * 这样HomePageActivity 进入 下载管理界面再返回时，HomePageActivity 的
            // * Fragment不会重新调用 OnCreateView ,只会调用
            // * OnResume,而用FLAG_ACTIVITY_REORDER_TO_FRONT，则
            // * 不管怎用都调用OnCreateView
            // */
            Log.d("HomePageActivity", "onBackPressed KEY.BACK_PRESSED = true");
            intent.putExtra(KEY.BACK_PRESSED, true);
            // 不显示通知栏通知(一键升级)
            GlobalConfigControllerManager.getInstance().setNotificationVisible(false);
            startActivity(intent);
            finish();// 重要2
            // }
            /*
             * else { // 没有finish则显示该界面 }
             */

        }

    }

    // 设置删除数
    public void changeBarLeftDelBtn() {
        if (mCheckToDelDownloadInfoList.size() <= 0 && mCheckToDelDownloadHistoryList.size() <= 0) {
            mCSHeaderBar.setmHeaderTitle(getString(R.string.please_to_select_task));
            mCSHeaderBar.getmRightHandleBtn().setEnabled(false);
        }
        else {
            mCSHeaderBar.setmHeaderTitle(getString(R.string.app_manager_bar_left_btn_del_num,
                    mCheckToDelDownloadInfoList.size() + mCheckToDelDownloadHistoryList.size()));
            mCSHeaderBar.getmRightHandleBtn().setEnabled(true);
        }

    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            // LogUtils.e( "handleMessage msg.what = " + msg.what );
            switch (msg.what) {
                case MsgDownload.DOWNLOAD_SIZE_EMPTY:
                    if (actionBarRightBtuStatus == ACTION_BAR_EDIT) {
                        mActionBar.getmRightEditIcon().setVisibility(View.GONE);
                        // mActionBar.getmLeftEditButton( ).setTextColor(
                        // getResources( ).getColor( R.color.white_4c ) );
                    }
                    break;
                case MsgDownload.DOWNLOAD_SIZE_NOT_EMPTY:
                    if (actionBarRightBtuStatus == ACTION_BAR_EDIT) {
                        mActionBar.getmRightEditIcon().setVisibility(View.VISIBLE);
                        // mActionBar.getmLeftEditButton( ).setTextColor(
                        // getResources( ).getColor( R.color.white ) );
                    }
                    break;
                case MsgDownload.DOWNLOAD_DEL_ADD:
                    mCheckToDelDownloadInfoList.add((DownloadTask) msg.obj);
                    Logger.i(
                            TAG,
                            "mCheckToDelDownloadInfoList~~~~=="
                                    + mCheckToDelDownloadInfoList.size());
                    changeBarLeftDelBtn();
                    break;
                case MsgDownload.HISTORY_DOWNLOAD_DEL_ADD:
                    mCheckToDelDownloadHistoryList.add((DownloadTask) msg.obj);
                    Logger.i(TAG, "mCheckToDelDownloadHistoryList~~~~=="
                            + mCheckToDelDownloadHistoryList.size());
                    changeBarLeftDelBtn();
                    break;
                case MsgDownload.DOWNLOAD_DEL_REMOVE:
                    mCheckToDelDownloadInfoList.remove(msg.obj);
                    Logger.i(
                            TAG,
                            "mCheckToDelDownloadInfoList~~~~=="
                                    + mCheckToDelDownloadInfoList.size());
                    changeBarLeftDelBtn();
                    if (mCheckToDelDownloadInfoList.size() == 0) {
                        mCSHeaderBar.setmHeaderTitle(getString(R.string.please_to_select_task));
                    }
                    break;
                case MsgDownload.HISTORY_DOWNLOAD_DEL_REMOVE:
                    mCheckToDelDownloadHistoryList.remove(msg.obj);
                    Logger.i(TAG, "mCheckToDelDownloadHistoryList~~~~=="
                            + mCheckToDelDownloadHistoryList.size());
                    changeBarLeftDelBtn();
                    if (mCheckToDelDownloadHistoryList.size() == 0) {
                        mCSHeaderBar.setmHeaderTitle(getString(R.string.please_to_select_task));
                    }
                    break;
                case MsgDownload.INSTALL_FINISH_TO_DO:
                    Logger.i( TAG,"------getTaskCount=====" + mApkDownloadManager.getTaskCount() );
                    Logger.i( TAG,"------actionBarRightBtuStatus=====" + actionBarRightBtuStatus );
                    // LogUtils.e(
                    // "INSTALL_FINISH_TO_DO , mApkDownloadManager.getTaskCount( ) = "
                    // + mApkDownloadManager.getTaskCount( ) );

//                    changeBarLeftDelBtn();   2015-12-01 tomqian

                    /*if (mDownloadListFragment != null)
                        mDownloadListFragment.refreshDownloadList();*/
                    if (mDownloadedFragment != null)
                        mDownloadedFragment.refreshDownloadList();
                    /*if (mApkDownloadManager.getTaskCount() <= 0
                            && actionBarRightBtuStatus != ACTION_BAR_UPGRADE) {
                        finishedEdit();
                    }*/

                    switch (actionBarRightBtuStatus){
                        case ACTION_BAR_EDIT:
                            if (mApkDownloadManager.getTaskCount() <= 0
                                    && actionBarRightBtuStatus != ACTION_BAR_UPGRADE) {
                                finishedEdit();
                            }
                            break;
                        case  ACTION_BAR_UPGRADE:
                            mActionBar.getmRightEditButton().setVisibility(View.GONE);
                            mActionBar.getmRightEditIcon().setVisibility(View.GONE);
                            Logger.i(TAG,"滑动到升级界面");
                            refreshActionBarWhenStatusIsAllUpgrade();
                            break;
                    }
                    break;
                case MsgDownload.MSG_SCROLL_TO_DOWNLOAD_MANAGER:

                    mActionBar.getmRightEditButton().setVisibility(View.GONE);
                    mActionBar.getmRightEditIcon().setVisibility(View.VISIBLE);
                    // mActionBar.getmRightEditButton( ).setBackgroundResource(
                    // R.drawable.csls_comm_actionbar_right_black_edit );
                    refreshRightActionBar(ACTION_BAR_EDIT);

                    // LogUtils.e( "滑动到下载编辑界面" );
                    if (actionBarRightBtuStatus == ACTION_BAR_EDIT) {
                        refreshActionBarWhenStatusIsEdit();
                    }
                    break;
                case MsgDownload.MSG_SCROLL_TO_APP_UPGRADE:
//                    mUpdateListFragment.ShowOnekeyUpdateButton(true);   2015-12-01 tomqian

                    mActionBar.getmRightEditButton().setVisibility(View.GONE);
                    /* mActionBar.getmRightEditButton().setVisibility(View.VISIBLE); */
                    mActionBar.getmRightEditIcon().setVisibility(View.GONE);
                    refreshRightActionBar(ACTION_BAR_UPGRADE);

                    // LogUtils.e( "滑动到升级界面" );
                    if (actionBarRightBtuStatus == ACTION_BAR_UPGRADE) {
                        refreshActionBarWhenStatusIsAllUpgrade();
                    }

                    break;
                case MsgDownload.MSG_FINISH_EDIT:
                    finishedEdit();
                    break;
                case MsgDownload.MSG_ACTION_EDIT:
                    // 设置download list's checkbox 的状态 显示
                    //                    mDownloadListFragment.downloadAdapterCheckBoxChanged(true);
                    mDownloadedFragment.downloadAdapterCheckBoxChanged(true);
                    mCSHeaderBar.setVisibility(View.VISIBLE);
                    mCSHeaderBar
                            .startAnimation(AnimationUtils.loadAnimation(AppManageActivity.this,
                                    R.anim.fade_in));
                    mCSHeaderBar.setmLeftHandleBtnBackground(R.drawable.csls_actionbar_button_red);
                    mCSHeaderBar
                            .setmRightHandleBtnTitle(getString(R.string.app_manager_bar_right_btn_finish));
                    // 设置actionbar's right btn的状态
                    refreshRightActionBar(ACTION_BAR_DEL);
                    // 设置actionbar's left btn的状态
                    // refreshBarReturnBtn( true );
                    // 设置viewPage 和 tabs 的状态
                    updateViewPageAndTabState(false);
                    // 删除按钮的是否可点击状态
                    changeBarLeftDelBtn();
                    // mActionBar.setTitle( getString(
                    // R.string.please_to_select_task ) );
                    break;
                case MsgDownload.MSG_REFRESH_DOWNLOAD_LIST_FRAGMENT:
                    /*if (mDownloadListFragment != null)
                        mDownloadListFragment.refreshDownloadList();*/
                    if (mDownloadedFragment != null)
                        mDownloadedFragment.refreshDownloadList();
                    break;
                case MsgDownload.MSG_REFRESH_DOWNLOAD_LIST_ADAPTER:
                    // if( mViewPager.getCurrentItem( ) == 0 )
                    // {
                    // if( mDownloadListFragment != null &&
                    // mDownloadListFragment.isInPatchMode( ) )
                    // {
                    // mDownloadListFragment.quitPatchMode( );
                    // return;
                    // }
                    // }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    // 完成安装后
    @Override
    public void apkInstallFinish(DownloadTask info) {
         Logger.i( "AppManageActivity","------info=====" + info );
        // LogUtils.e( "mCheckToDelDownloadInfoList.contains( info )= " +
        // mCheckToDelDownloadInfoList.contains( info ) );
        if (info != null && mCheckToDelDownloadInfoList.contains(info)) {
            // LogUtils.e( "------remove=" + info );
            mCheckToDelDownloadInfoList.remove(info);
        }
        mHandler.sendEmptyMessage(MsgDownload.INSTALL_FINISH_TO_DO);

    }

    // 更新DownLoadList
    public void refreshDownListFragment() {
        mHandler.sendEmptyMessage(MsgDownload.MSG_REFRESH_DOWNLOAD_LIST_FRAGMENT);
    }

    /*public void setmDownloadListFragment(DownloadListFragment mDownloadListFragment) {
        this.mDownloadListFragment = mDownloadListFragment;
    }*/

    public void setmDownloadedFragment(DownloadExpandListFragment mDownloadListFragment) {
        this.mDownloadedFragment = mDownloadListFragment;
    }

    public void setmUpdateListFragment(UpdateListFragment mUpdateListFragment) {
        this.mUpdateListFragment = mUpdateListFragment;
    }

    public class DepthPageTransformer implements PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        @SuppressLint("NewApi")
        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int halfPageWidth = pageWidth >> 1;
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            }
            else if (position <= 0) { // [-1,0]
                // Use the default slide transition when
                // moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            }
            else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);
                // Counteract the default slide transition
                // view.setTranslationX( pageWidth * -position );
                // // Scale the page down (between MIN_SCALE and 1)
                // float scaleFactor = MIN_SCALE + ( 1 - MIN_SCALE ) * ( 1 -
                // Math.abs( position ) );
                // view.setScaleX( scaleFactor );
                // view.setScaleY( scaleFactor );

                view.setTranslationX(-(halfPageWidth * position));
            }
            else { // (1,+Infinity]
                   // This page is way off-screen to the right.
                view.setAlpha(0);

            }
        }

    }
}
