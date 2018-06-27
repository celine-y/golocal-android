package yau.celine.golocal.utils;

public class OrderMenuItem extends MenuItem {
    private String requests;

    public OrderMenuItem(MenuItem item){
        this.setId(item.getId());
        this.setName(item.getName());
        this.setDescription(item.getDescription());
        this.setShopId(item.getShopId());
        this.setPrice(item.getPrice());
        this.setImageUrl(item.getImageUrl());
    }

    public String getRequests() {
        return requests;
    }

    public void setRequests(String requests) {
        this.requests = requests;
    }
}
