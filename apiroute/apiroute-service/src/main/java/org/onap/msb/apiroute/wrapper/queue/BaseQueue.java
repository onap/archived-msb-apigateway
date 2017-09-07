/*******************************************************************************
 * Copyright 2016-2017 ZTE, Inc. and others.
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
 *  limitations under the License.
 ******************************************************************************/
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
