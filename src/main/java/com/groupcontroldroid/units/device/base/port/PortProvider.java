package com.groupcontroldroid.units.device.base.port;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.groupcontroldroid.util.NetUtil;


/**
 * 端口号提供
 */
public class PortProvider {
	protected  Set<Integer> set = Collections.synchronizedSet(new TreeSet<Integer>());
	protected  int portStart,portEnd;
	protected  int portNums;
	
	public PortProvider(int portStart,int portEnd){
		this.portStart = portStart;
		this.portEnd = portEnd;
		this.portNums=portEnd-portStart;
		
		int port = portStart;
		//填充可用端口号
		while(port<=portEnd){
			if(NetUtil.isPortAvailable(port)){
				//如果端口没有被占用，那么就添加
				set.add(port);
			}
			port++;
		}
	}
	
	/**
	 * 获取可用端口号
	 * @return int 端口号。如果返回值>=0，则获取的端口号可用，如果端口号<0，则端口号获取失败
	 */
	public int pullPort(){
		Iterator<Integer> it = set.iterator();
		if(it.hasNext()){
			Integer port = it.next();
			set.remove(port);
			return port.intValue();
		}else{
			return -1;
		}
	}
	
	/**
	 * 归还端口号
	 * @param port 端口号
	 * @return boolean
	 */
	public boolean pushPort(int port){
		if(set.size()<portNums && port>=portStart && port<=portEnd){
			return set.add(port);
		}else{
			return false;
		}
	}
}
