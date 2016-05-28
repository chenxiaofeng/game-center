package com.hykj.gamecenter.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * 网络状态工具类
 * @author Nurmuhammad
 *
 */
public class NetUtils
{

    /**
     * 判断Network是否开启(包括移动网络和Wifi)
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkEnabled( Context context )
    {
	return ( isNetEnabled( context ) || isWIFIEnabled( context ) );

    }

    /**
     * 判断Network是否连接成功(包括移动网络和Wifi)
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkconnected( Context context )
    {
	return ( isWifiContected( context ) || isNetContected( context ) || isEthernetContected( context ) );
    }

    /**
     * 判断移动网络是否开启
     * 
     * @param context
     * @return
     */
    public static boolean isNetEnabled( Context context )
    {
	boolean enable = false;
	TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService( Context.TELEPHONY_SERVICE );
	if( telephonyManager != null )
	{
	    if( telephonyManager.getNetworkType( ) != TelephonyManager.NETWORK_TYPE_UNKNOWN )
	    {
		enable = true;
	    }
	}
	return enable;
    }

    /**
     * 判断Wifi是否开启
     * 
     * @param context
     * @return
     */
    public static boolean isWIFIEnabled( Context context )
    {
	boolean enable = false;
	WifiManager wifiManager = (WifiManager)context.getSystemService( Context.WIFI_SERVICE );
	if( wifiManager.isWifiEnabled( ) )
	{
	    enable = true;
	}
	return enable;
    }

    /**
     * 判断移动网络连接是否成功
     * 
     * @param context
     * @return
     */
    public static boolean isNetContected( Context context )
    {
	ConnectivityManager connectivityManager = (ConnectivityManager)context
		.getSystemService( Context.CONNECTIVITY_SERVICE );
	NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo( ConnectivityManager.TYPE_MOBILE );
		return mobileNetworkInfo != null && mobileNetworkInfo.isConnected();
	}

    /**
     * 判断Wifi是否连接成功
     * 
     * @param context
     * @return
     */
    public static boolean isWifiContected( Context context )
    {
	ConnectivityManager connectivityManager = (ConnectivityManager)context
		.getSystemService( Context.CONNECTIVITY_SERVICE );
	NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo( ConnectivityManager.TYPE_WIFI );
		return wifiNetworkInfo != null && wifiNetworkInfo.isConnected();
	}

    /**
     * 判断是否有以太网网络
     * @return true表示有，false表示无
     */
    public static boolean isEthernetContected( Context context )
    {
	ConnectivityManager conn = (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );

	NetworkInfo [] networkInfo = conn.getAllNetworkInfo( );
	if( networkInfo != null )
	{
	    for( int i = 0 ; i < networkInfo.length ; i++ )
	    {
		if( networkInfo[i].getType( ) == ConnectivityManager.TYPE_ETHERNET )
		{
		    //有线网络连接成功，更新UI 
		    return true;
		}
	    }
	}
	return false;
    }
}