package yau.celine.golocal.utils.objects;

public class OrderItemObject extends ItemObject {
    private String requests;

    public OrderItemObject(ItemObject item){
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
