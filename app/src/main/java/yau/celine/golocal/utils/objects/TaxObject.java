package yau.celine.golocal.utils.objects;

public class TaxObject {
    private String description;
    private double percentage;

    public TaxObject(String description, double percentage){
        this.description = description;
        this.percentage = percentage;
    }

    public double calculateTaxAmount(double amount) {
        return amount * percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getDescription() {
        return description;
    }
}
