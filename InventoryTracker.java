import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Item {
    private String name;
    private int id;
    private int quantity;
    private double retailPrice;
    private double wholesalePrice;

    public Item(String name, int id, int quantity, double retailPrice, double wholesalePrice) {
        this.name = name;
        this.id = id;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
        this.wholesalePrice = wholesalePrice;
    }

    public String toString() {
        return String.format("%d,%s,%d,%.2f,%.2f", id, name, quantity, retailPrice, wholesalePrice);
    }

    public static Item fromString(String line) {
        String[] parts = line.split(",");
        return new Item(parts[1], Integer.parseInt(parts[0]), Integer.parseInt(parts[2]),
                Double.parseDouble(parts[3]), Double.parseDouble(parts[4]));
    }

    // Getters and setters
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

public class InventoryTracker {
    private static final String FILE_PATH = "inventory.txt";
    private Map<Integer, Item> inventory = new HashMap<>();
    private Scanner scanner;
    private int nextId = 1;

    public InventoryTracker() {
        scanner = new Scanner(System.in);
    }

    public void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("\n".repeat(50));
        }
    }

    public void loadInventory() {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                Item item = Item.fromString(line);
                inventory.put(item.getId(), item);
                // Update nextId to be one more than the highest existing ID
                nextId = Math.max(nextId, item.getId() + 1);
            }
        } catch (IOException e) {
            System.out.println("No existing inventory found. Starting fresh.");
            nextId = 1;
        }
    }

    public void saveInventory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Item item : inventory.values()) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving inventory: " + e.getMessage());
        }
    }

    public void displayMenu() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║    INVENTORY MANAGEMENT SYSTEM     ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║  [1] Add New Item                  ║");
        System.out.println("║  [2] Update Item Quantity          ║");
        System.out.println("║  [3] Display Current Inventory     ║");
        System.out.println("║  [4] Delete Item                   ║");
        System.out.println("║  [5] Exit System                   ║");
        System.out.println("╚════════════════════════════════════╝");
    }

    public void displayInventory() {
    if (inventory.isEmpty()) {
        System.out.println("\nInventory is empty!");
        return;
    }
    
    // Display main inventory
    System.out.println("\nCurrent Inventory:");
    System.out.printf("%-5s %-20s %-10s %-12s %-12s%n", "ID", "Name", "Quantity", "Retail", "Wholesale");
    System.out.println("─".repeat(65));
    for (Item item : inventory.values()) {
        System.out.printf("%-5d %-20s %-10d RM %-11.2f RM %-11.2f%n",
                item.getId(), item.getName(), item.getQuantity(),
                item.getRetailPrice(), item.getWholesalePrice());
    }

    // Display low stock items
    List<Item> lowStockItems = inventory.values().stream()
            .filter(item -> item.getQuantity() < 10)
            .collect(Collectors.toList());

    if (!lowStockItems.isEmpty()) {
        System.out.println("\n\nLOW STOCK ALERT (Items below 10 units):");
        System.out.printf("%-5s %-20s %-10s%n", "ID", "Name", "Quantity");
        System.out.println("─".repeat(35));
        for (Item item : lowStockItems) {
            System.out.printf("%-5d %-20s %-10d%n",
                    item.getId(), item.getName(), item.getQuantity());
        }
    }
}

    public String getInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                // Check for '0' to return to main menu
                if (input.equals("0")) {
                    return null;
                }
                return input;
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public void addItem() {
        System.out.println("\nAdding New Item (Enter '0' to return to main menu)");
        System.out.println("─".repeat(50));
        System.out.println("Item ID: " + nextId);

        String name = getInput("Enter item name: ");
        if (name == null)
            return;

        String quantityStr = getInput("Enter quantity: ");
        if (quantityStr == null)
            return;

        String retailStr = getInput("Enter retail price (RM): ");
        if (retailStr == null)
            return;

        String wholesaleStr = getInput("Enter wholesale price (RM): ");
        if (wholesaleStr == null)
            return;

        try {
            int quantity = Integer.parseInt(quantityStr);
            double retail = Double.parseDouble(retailStr);
            double wholesale = Double.parseDouble(wholesaleStr);

            // Validate inputs
            if (quantity < 0) {
                System.out.println("Quantity cannot be negative. Press Enter to continue...");
                scanner.nextLine();
                return;
            }
            if (retail < 0 || wholesale < 0) {
                System.out.println("Prices cannot be negative. Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            inventory.put(nextId, new Item(name, nextId, quantity, retail, wholesale));
            saveInventory();
            nextId++;

            System.out.println("\nItem added successfully!");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Press Enter to continue...");
            scanner.nextLine();
        }
    }

    public void updateQuantity() {
        System.out.println("\nUpdating Quantity (Enter '0' to return to main menu)");
        System.out.println("─".repeat(50));

        // First display current inventory for reference
        displayInventory();
        System.out.println("\n");

        String idStr = getInput("Enter item ID: ");
        if (idStr == null)
            return;

        try {
            int id = Integer.parseInt(idStr);
            if (!inventory.containsKey(id)) {
                System.out.println("Item not found. Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Show current quantity for reference
            System.out.println("Current quantity: " + inventory.get(id).getQuantity());
            String quantityStr = getInput("Enter new quantity: ");
            if (quantityStr == null)
                return;

            int quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                System.out.println("Quantity cannot be negative. Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            inventory.get(id).setQuantity(quantity);
            saveInventory();

            System.out.println("\nQuantity updated successfully!");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Press Enter to continue...");
            scanner.nextLine();
        }
    }

    public void deleteItem() {
        System.out.println("\nDeleting Item (Enter '0' to return to main menu)");
        System.out.println("─".repeat(50));

        // First display current inventory for reference
        displayInventory();
        System.out.println("\n");

        String idStr = getInput("Enter item ID to delete: ");
        if (idStr == null)
            return;

        try {
            int id = Integer.parseInt(idStr);
            if (!inventory.containsKey(id)) {
                System.out.println("Item not found. Press Enter to continue...");
                scanner.nextLine();
                return;
            }

            // Show item details and confirm deletion
            Item itemToDelete = inventory.get(id);
            System.out.println("\nItem to delete:");
            System.out.printf("ID: %d\nName: %s\nQuantity: %d\nRetail Price: RM%.2f\nWholesale Price: RM%.2f\n",
                    itemToDelete.getId(),
                    itemToDelete.getName(),
                    itemToDelete.getQuantity(),
                    itemToDelete.getRetailPrice(),
                    itemToDelete.getWholesalePrice());

            String confirm = getInput("\nAre you sure you want to delete this item? (Y/N): ");
            if (confirm == null)
                return;

            if (confirm.equalsIgnoreCase("Y")) {
                inventory.remove(id);
                saveInventory();
                System.out.println("\nItem deleted successfully!");
            } else {
                System.out.println("\nDeletion cancelled.");
            }

            System.out.println("Press Enter to continue...");
            scanner.nextLine();

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Press Enter to continue...");
            scanner.nextLine();
        }
    }

    public void run() {
        loadInventory();

        while (true) {
            clearScreen();
            displayMenu();

            try {
                System.out.print("\nChoose an option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        clearScreen();
                        addItem();
                        break;
                    case "2":
                        clearScreen();
                        updateQuantity();
                        break;
                    case "3":
                        clearScreen();
                        displayInventory();
                        System.out.print("\nPress Enter to continue...");
                        scanner.nextLine();
                        break;
                    case "4":
                        clearScreen();
                        deleteItem();
                        break;
                    case "5":
                        System.out.println("Thank you for using the Inventory Management System!");
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid option. Press Enter to continue...");
                        scanner.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    public static void main(String[] args) {
        new InventoryTracker().run();
    }
}