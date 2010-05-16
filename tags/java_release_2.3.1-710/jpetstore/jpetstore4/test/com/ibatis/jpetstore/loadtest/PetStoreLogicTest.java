package com.ibatis.jpetstore.loadtest;

/**
 * User: Clinton Begin
 * Date: Apr 3, 2003
 * Time: 6:15:32 PM
 */
import com.ibatis.jpetstore.domain.*;
import com.ibatis.jpetstore.service.*;
import com.ibatis.jpetstore.service.CatalogService;
import com.ibatis.common.util.*;

import java.util.*;

// VM Params:
// -server -Xms256M -Xmx256M -Xconcgc -XX:+UseAdaptiveSizePolicy -XX:+UseParallelGC

public class PetStoreLogicTest implements Runnable{

  private static final int THREAD_COUNT = 200;
  private static final int THREAD_SPACING = 200;
  private static int THREAD_PAUSE = 500;

  private static Stopwatch masterWatch = new Stopwatch();
  private static List availableUsernames = Collections.synchronizedList(new ArrayList());

  private static Object statsLock = new Object();
  private static int errorCount = 0;
  private static int activeThreads = 0;

  public static void main(String[] args) throws Exception {

    AccountService accountService = AccountService.getInstance();

    //BasicInspector.basicInspectWait(logic);

    availableUsernames = accountService.getUsernameList();

    // Flex environment
    System.out.println ("Flexing environment...");
    int oldThreadPause = THREAD_PAUSE;
    THREAD_PAUSE = 0;
    runScenario();
    THREAD_PAUSE = oldThreadPause;
    System.out.println ("Environment flexed.");

    // Reset stopwatch
    masterWatch = new Stopwatch();

    System.out.println ("Creating threads...");
    List threads = new ArrayList(THREAD_COUNT);
    ThreadGroup group = new ThreadGroup("PetStoreLogicTest");
    for (int i = 1; i <= THREAD_COUNT; i++) {
      Thread t = new Thread(group,new PetStoreLogicTest());
      threads.add(t);
      if (i % 100 == 0) {
        System.out.println(i + " threads created.");
        System.out.flush();
      }
    }

    System.out.println ("Starting threads...");
    for (int i = 1; i <= THREAD_COUNT; i++) {
      Thread.sleep(random(THREAD_SPACING));

      synchronized(statsLock) {
        activeThreads ++;
      }
      ((Thread)threads.get(i-1)).start();
      if (i % 100 == 0) {
        System.out.println(i + " threads started.");
        System.out.flush();
      }
    }
    System.out.println ("Active threads = " + activeThreads);

    Thread.sleep(1000);
    while (activeThreads > 1) {
      Date d = new Date();
      System.out.println ("Time: " + d);
      System.out.println("Error Count = " + errorCount);
      System.out.println ("Active Threads: "+activeThreads);
      System.out.flush();
      Thread.sleep(10000);
      System.out.flush();
    }

    Date d = new Date();
    System.out.println ("Time: " + d);
    System.out.println("Error Count = " + errorCount);
    System.out.println ("Active Threads: "+activeThreads);
    System.out.println (masterWatch);
//    System.out.println(BaseLogic.getPoolStatus());
//    System.out.println ("Data Cache Stats");
//    System.out.println (BaseLogic.getDataCacheStatus());
//    System.out.println ("----------------------------------------");
//    System.out.println ("Statement Cache Stats");
//    System.out.println (BaseLogic.getStatementCacheStatus());

    System.out.flush();

  }

  public void run() {
    try {
      runScenario();
    } catch (Exception e) {
      e.printStackTrace();
      synchronized (statsLock) {
        errorCount++;
      }
    } finally {
      synchronized (statsLock) {
        activeThreads--;
      }
    }
  }

  private static final Object STAT_LOCK = new Object();
  private static int running = 0;

  private static void runScenario() throws Exception {
    synchronized (STAT_LOCK) {
      System.out.println ("Start: "+(++running) +" now running.");
    }

    final Stopwatch WATCH = new Stopwatch();

    CatalogService catalogService = CatalogService.getInstance();
    AccountService accountService = AccountService.getInstance();
    OrderService orderService = OrderService.getInstance();

    // CATEGORY
    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getCategoryList");
    List categoryList = catalogService.getCategoryList();
    WATCH.stop();

    Category category = (Category)categoryList.get(random(categoryList.size()-1));

    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getCategory");
    category = catalogService.getCategory(category.getCategoryId());
    WATCH.stop();

    // PRODUCT
    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getProductRowCountByCategory");
    WATCH.stop();

    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getProductListByCategory");
    List productList = catalogService.getProductListByCategory(category.getCategoryId());
    WATCH.stop();

    Product product = (Product)productList.get(random(productList.size()-1));

    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getProduct");
    product = catalogService.getProduct(product.getProductId());
    WATCH.stop();

    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("productSearch");
    productList = catalogService.searchProductList(product.getDescription().substring(0,3) + " " + product.getDescription().substring(0,3));
    WATCH.stop();

    // ITEM
    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getItemRowCountByProduct");
    WATCH.stop();

    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getItemListByProduct");
    List itemList = catalogService.getItemListByProduct(product.getProductId());
    WATCH.stop();

    Item item = (Item)itemList.get(random(itemList.size()-1));

    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getItem");
    item = catalogService.getItem(item.getItemId());
    WATCH.stop();

    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("isItemInStock");
    catalogService.isItemInStock(item.getItemId());
    WATCH.stop();

    // ACCOUNT
    String username = (String)availableUsernames.get((random(availableUsernames.size()-1)));

    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getAccount");
    Account account = accountService.getAccount(username);
    WATCH.stop();

    String user = account.getUsername();
    String newUser = String.valueOf(nextUsername());
    account.setUsername(newUser);
    account.setPassword(newUser);

    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("insertAccount");
    accountService.insertAccount(account);
    WATCH.stop();

    // ORDER
    Thread.sleep(random(THREAD_PAUSE));
    WATCH.start("getOrdersByUsername");
    List orderList = orderService.getOrdersByUsername(user);
    WATCH.stop();

    int index = random(orderList.size()-1);
    if (index < orderList.size() && index > 0) {
      Order order = (Order) orderList.get(index);
      order.setUsername(newUser);

      Thread.sleep(random(THREAD_PAUSE));
      WATCH.start("insertOrder");
      orderService.insertOrder(order);
      WATCH.stop();

      availableUsernames.add(user);
    }


    masterWatch.mergeStopwatch(WATCH);

    synchronized (STAT_LOCK) {
      System.out.println ("Finish: "+(--running) +" now running.");
    }
  }

  private static int random (int max) {
    return (int)Math.round(Math.random() * max);
  }

  private static int nextUsername = 1000;
  private synchronized static String nextUsername() {
    return String.valueOf(nextUsername++);
  }

}

