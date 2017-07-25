package org.onap.msb.apiroute.wrapper.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class CommonUtil {

  public static final int SC_OK = 200;
  
  public static Object[] concat(Object[] a, Object[] b) {
    Object[] c = new Object[a.length + b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
  }

  public static boolean contain(String strArray, String str) {
    String[] array = StringUtils.split(strArray, ",");
    return contain(array, str);
  }

  public static boolean contain(String[] array, String str) {
    for (int i = 0; i < array.length; i++) {
      if (array[i].trim().equals(str)) {
        return true;
      }
    }
    return false;

  }

  public static boolean contain(String[] array, String value[]) {
    for (int i = 0; i < array.length; i++) {
      for (int n = 0; n < value.length; n++) {
        if (array[i].equals(value[n])) {
          return true;
        }
      }
    }
    return false;

  }

  /**
   * @param <T>
   * @Title getDiffrent
   * @Description TODO(Extract the list1 and list2 different data sets)
   * @param list1
   * @param list2
   * @return TODO（a new List in list2 but not in list1）
   * @return List<String>
   */
  public static <T> Set<T> getDiffrent(Set<T> list1, Set<T> list2) {

    HashSet<T> set_all = new HashSet<T>();

    for (T t1 : list1) {
      set_all.add(t1);
    }


    Set<T> diff = new HashSet<T>();

    for (T t2 : list2) {
      if (set_all.add(t2)) { // in list2 but not in list1
        diff.add(t2);
      }
    }


    return diff;
  }

}
