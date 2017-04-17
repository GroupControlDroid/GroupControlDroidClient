package com.groupcontroldroid.units.device.minitouch;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
public class EventQueue {
	private static final EventQueue eventQueue=new EventQueue();
	private static Queue<Object> queue=new ConcurrentLinkedQueue<Object>();;

	public EventQueue() {
		
	}
	
	public static EventQueue getinstance(){
		return eventQueue;
	}

	public static Queue<Object> getQueue() {
		return queue;
	}

	public static void setQueue(Queue<Object> queue) {
		EventQueue.queue = queue;
	}
	
	public synchronized void addObject(Object object){
		queue.add(object);
	}
	
	public synchronized Object pollObject(){
		return queue.poll();
	}
	
	public synchronized Object getObject(){
		return queue.peek();
	}
	
	public synchronized Boolean isEmpty(){
		return queue.isEmpty();
		
	}
	
}
