package com.x.business.zerodata.helper;


import com.x.business.zerodata.server.service.ServiceController;
import com.x.business.zerodata.server.service.ServerValues;
import com.x.business.zerodata.server.service.params.ServerParams;

import android.os.RemoteException;

public interface IServiceHelper {

	public static final int STATUS_RUNNING = ServerValues.STATUS_RUNNING;
	public static final int STATUS_STOPPED = ServerValues.STATUS_STOPPED;
	public static final int STATUS_CONNECTED = 514;
	public static final int STATUS_DISCONNECTED = 514 + 1;
	public static final int STATUS_UNKNOWN = 514 + 2;

	public static final int DEFAULT_STATUS_REFRESH_TIME = 5000;

	/**
	 * Connect to the service
	 */
	public void connect();

	/**
	 * Connect to the service and run a {@link Runnable} when the service is
	 * binded
	 * 
	 * @param runOnConnect
	 */
	public void connect(Runnable runOnConnect);

	/**
	 * Disconnect the service
	 */
	public void disconnect();

	/**
	 * Get if the service is connected
	 * 
	 * @return true if the service is connected, false otherwise
	 */
	public boolean isServiceConected();

	/**
	 * Start the server with the specified parameters
	 * 
	 * @param params
	 *            the parameters to run the server
	 * @return true if the server has been started
	 * @throws RemoteException
	 */
	public boolean startServer() throws RemoteException;

	/**
	 * Set the max amount of milliseconds that will take to get a new status
	 * event. By default it take {@link IServiceHelper}
	 * {@link #DEFAULT_STATUS_REFRESH_TIME} ms
	 * 
	 * @param time Time in ms
	 */
	public void setStatusTimeRefresh(int time);

	/**
	 * Stop the server
	 * 
	 * @throws RemoteException
	 */
	public void stopServer() throws RemoteException;

	/**
	 * Add a {@link Runnable} to be processed whe the service is connected
	 * 
	 * @param runable
	 */
	public void addRunnableOnConnect(Runnable runable);

	/**
	 * Get the {@link ServiceController} that allows to interact with the server
	 * 
	 * @return
	 */
	public ServiceController getServiceController();

	/**
	 * Add a {@link ServerStatusListener} to be triggered when the
	 * server/service status is changed
	 * 
	 * @param serverEventListener
	 */
	public void addServerStatusListener(ServerStatusListener serverEventListener);

	/**
	 * It is recommended to unregister a listener before disconnect.
	 * 
	 * @param serverEventListener
	 */
	public void removeServerStatusListener(ServerStatusListener serverEventListener);

	public interface ServerStatusListener {

		public void onServerStatusChanged(ServiceController serviceController, int status);
	}

}
