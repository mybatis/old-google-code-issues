package com.ibatis.jpetstore.service;

import com.ibatis.common.util.PaginatedList;
import com.ibatis.dao.client.DaoManager;
import com.ibatis.jpetstore.domain.Category;
import com.ibatis.jpetstore.domain.Item;
import com.ibatis.jpetstore.domain.Product;
import com.ibatis.jpetstore.persistence.DaoConfig;
import com.ibatis.jpetstore.persistence.iface.CategoryDao;
import com.ibatis.jpetstore.persistence.iface.ItemDao;
import com.ibatis.jpetstore.persistence.iface.ProductDao;

import java.util.List;

public class CatalogService {

  /* Constants */

  private static final CatalogService instance = new CatalogService();

  /* Private Fields */

  private DaoManager daoManager = DaoConfig.getDaomanager();

  private CategoryDao categoryDao;
  private ItemDao itemDao;
  private ProductDao productDao;

  /* Constructors */

  private CatalogService() {
    categoryDao = (CategoryDao) daoManager.getDao(CategoryDao.class);
    productDao = (ProductDao) daoManager.getDao(ProductDao.class);
    itemDao = (ItemDao) daoManager.getDao(ItemDao.class);
  }

  /* Public Methods */

  public static CatalogService getInstance() {
    return instance;
  }

  /* CATEGORY */

  public List getCategoryList() {
    return categoryDao.getCategoryList();
  }

  public Category getCategory(String categoryId) {
    return categoryDao.getCategory(categoryId);
  }

  /* PRODUCT */

  public Product getProduct(String productId) {
    return productDao.getProduct(productId);
  }

  public PaginatedList getProductListByCategory(String categoryId) {
    return productDao.getProductListByCategory(categoryId);
  }

  public PaginatedList searchProductList(String keywords) {
    return productDao.searchProductList(keywords);
  }

  /* ITEM */

  public PaginatedList getItemListByProduct(String productId) {
    return itemDao.getItemListByProduct(productId);
  }

  public Item getItem(String itemId) {
    return itemDao.getItem(itemId);
  }

  public boolean isItemInStock(String itemId) {
    return itemDao.isItemInStock(itemId);
  }

}