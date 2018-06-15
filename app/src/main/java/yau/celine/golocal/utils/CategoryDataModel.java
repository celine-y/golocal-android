package yau.celine.golocal.utils;

import java.util.ArrayList;

public class CategoryDataModel {
    private String categoryName;
    private ArrayList<MenuItem> allItemsInCategory;

    public CategoryDataModel(){
    }

    public CategoryDataModel(String categoryName, ArrayList<MenuItem> allItemsInCategory){
        this.categoryName = categoryName;
        this.allItemsInCategory = allItemsInCategory;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public ArrayList<MenuItem> getAllItemsInCategory() {
        return allItemsInCategory;
    }

    public void setItemsInCategory(ArrayList<MenuItem> itemsInCategory) {
        this.allItemsInCategory = itemsInCategory;
    }
}
