package org.onap.msb.apiroute.wrapper.util;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.onap.msb.apiroute.wrapper.util.CommonUtil;

public class CommonUtilTest {


  
  @Test
  public void test_concat() {
    Object[] str1 = new String[] {"test1", "test2"};
    Object[] str2 = new String[] {"test3"};
    Object[] str3 = CommonUtil.concat(str1, str2);

    Assert.assertEquals(3, str3.length);
  }

  @Test
  public void test_containStr() {
    String value = "1";
    String array[] = {"1", "2"};
    Assert.assertTrue(CommonUtil.contain(array, value));
    Assert.assertFalse(CommonUtil.contain(array, "3"));
  }

  @Test
  public void test_containArray() {
    String value[] = {"0"};
    String array[] = {"1", "2"};
    String array2[] = {"2", "1"};
    Assert.assertFalse(CommonUtil.contain(array, value));
    Assert.assertTrue(CommonUtil.contain(array, array2));
  }

  @Test
  public void test_containStrArray() {
    Assert.assertFalse(CommonUtil.contain("0,1,2", "3"));
    Assert.assertTrue(CommonUtil.contain("0,1,2", "1"));
  }

  @Test
  public void test_getDiffrent() {
    Set<String> list1 = new HashSet<String>();
    list1.add("test1");
    list1.add("test2");

    Set<String> list2 = new HashSet<String>();
    list2.add("test2");
    list2.add("test3");

    Set<String> diff = CommonUtil.getDiffrent(list1, list2);
    Assert.assertEquals(1, diff.size());
    Assert.assertTrue(diff.contains("test3"));
  }

}
