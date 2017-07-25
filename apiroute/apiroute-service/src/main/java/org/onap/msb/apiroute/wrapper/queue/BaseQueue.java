package org.onap.msb.apiroute.wrapper.queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseQueue<T> {
  
  private final List<BlockingQueue<ServiceData<T>>> queueArray= new ArrayList<BlockingQueue<ServiceData<T>>>(); 
  
  public BaseQueue(int queueNum,int queueCapacity)
  {   
      for(int i=0;queueNum>0 && i<queueNum;i++)
      {
          queueArray.add(new LinkedBlockingQueue<ServiceData<T>>(queueCapacity));
      }
  }
  
  public int getQueneNum(){
    return queueArray.size();
  }
  
  protected BlockingQueue<ServiceData<T>> getQueue(int index)
  {
      return queueArray.get(index);
  }

  public abstract void put(final ServiceData<T> data) throws InterruptedException;
  
  public abstract ServiceData<T> take(final int queueIndex) throws InterruptedException;
  
  
  

}
