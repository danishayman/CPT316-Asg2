// Class representing an item in the inventory
class Item {
    private String name;
    private int id;
    private int quantity;
    protected double retailPrice;
    protected double wholesalePrice;

    // Constructor to initialize an item
    public Item(String name, int id, int quantity, double retailPrice, double wholesalePrice) {
        this.name = name;
        this.id = id;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
        this.wholesalePrice = wholesalePrice;
    }

    // Convert item details to a string format for saving to file
    public String toString() {
        return String.format("%d,%s,%d,%.2f,%.2f", id, name, quantity, retailPrice, wholesalePrice);
    }

    // Create an item object from a string (used when loading from file)
    public static Item fromString(String line) {
        String[] parts = line.split(",");
        return new Item(parts[1], Integer.parseInt(parts[0]), Integer.parseInt(parts[2]),
                Double.parseDouble(parts[3]), Double.parseDouble(parts[4]));
    }

    // Getters and setters for item properties
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public double getWholesalePrice() {
        return wholesalePrice;
    }
}