package yau.celine.golocal.utils.objects;

import java.util.ArrayList;

public class CategoryDataModel {
    private String categoryName;
    private ArrayList<ItemObject> allItemsInCategory;

    public CategoryDataModel(){
    }

    public CategoryDataModel(String categoryName, ArrayList<ItemObject> allItemsInCategory){
        this.categoryName = categoryName;
        this.allItemsInCategory = allItemsInCategory;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public ArrayList<ItemObject> getAllItemsInCategory() {
        return allItemsInCategory;
    }

    public void setItemsInCategory(ArrayList<ItemObject> itemsInCategory) {
        this.allItemsInCategory = itemsInCategory;
    }
}
