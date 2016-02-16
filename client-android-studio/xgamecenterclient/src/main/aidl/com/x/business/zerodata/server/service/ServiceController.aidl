/*
 * Copyright (C) 2013 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.x.business.zerodata.server.service; 



import java.util.List;

interface ServiceController { 

	/**
	 * Use this method to set the security key. If this key is defined
	 * previously, it will prevent external process to close the remote
	 * service, unless the key is the same
	 * 
	 * @param secKey
	 *            The shared key needed to control the service.
	 */
	// void setSecurityKey(in String secKey);
	 
	/**
	 * Start the server with the defined parameters
	 *
	 * @param params
	 *	The parameter with the configuration of the server
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
	boolean startService();
	
	/**
	 * Restart the server with the defined parameters
	 *
	 * @param params
	 *	The parameter with the configuration of the server
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
	boolean restartService();
	
	 /**
	 * Stop the server.
	 *
	 * @return True if the server has been stopped, false otherwise
	 */
	boolean stopService(); 
	
	/**
	 * Get the status of the server:<br>
	 * {@link ServerValues.STATUS_RUNNING} The server is running <br>
	 * {@link ServerValues.STATUS_STOPED} The server is stopped <br>
	 *
	 * @return True if the server has been initialized, false otherwise
	 */
	int getStatus();
	
	
	/**
	 * Get the parameters in use by the server.
	 * @return The  {@link ServerParams} in use for the server
	 */
	
	 /**
	 * This is the default port opened when the user ask for opening a port
	 * under 1024. <br>
	 * The system will try to use iptables like this:<br>
	 * iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port
	 * DEFAULT_PORT_ON_ROOT
	 * @return The default port when the root permissions are required.
	 */
	int getDefaultPortOnRoot();
	
	/**
	 * Use this function to enable and disable the vibrations
	 * when a request is received.
	 * @param params 
	 *   True to vibrate if a petition is accepted, false otherwise.
	 */
	void setVibrate(in boolean vibrate);
	
	/**
	 * Get the servDroid software version
	 * @return The ServDroid.web version
	 */
	String getVersion();
	
	
}