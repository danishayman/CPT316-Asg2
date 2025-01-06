import java.io.*;
import java.util.*;

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
    public String getName() { return name; }
    public int getId() { return id; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getRetailPrice() { return retailPrice; }
    public double getWholesalePrice() { return wholesalePrice; }
}

public class InventoryTracker {
    private static final String FILE_PATH = "inventory.txt";
    private Map<Integer, Item> inventory = new HashMap<>();
    private Scanner scanner;
    private BufferedReader reader;

    public InventoryTracker() {
        scanner = new Scanner(System.in);
        reader = new BufferedReader(new InputStreamReader(System.in));
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
            }
        } catch (IOException e) {
            System.out.println("No existing inventory found. Starting fresh.");
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
        System.out.println("║      INVENTORY MANAGEMENT SYSTEM   ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║  1. Add New Item                   ║");
        System.out.println("║  2. Update Item Quantity           ║");
        System.out.println("║  3. Display Current Inventory      ║");
        System.out.println("║  4. Exit System                    ║");
        System.out.println("╚════════════════════════════════════╝");
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        System.out.printf("%-5s %-20s %-10s %-12s %-12s%n", "ID", "Name", "Quantity", "Retail", "Wholesale");
        System.out.println("─".repeat(65));
        for (Item item : inventory.values()) {
            System.out.printf("%-5d %-20s %-10d RM%-11.2f RM%-11.2f%n",
                    item.getId(), item.getName(), item.getQuantity(),
                    item.getRetailPrice(), item.getWholesalePrice());
        }
    }

    public boolean checkForCtrlX() {
        try {
            if (System.in.available() > 0) {
                int input = reader.read();
                if (input == 24) { // Ctrl+X
                    clearScreen();
                    return true;
                }
            }
        } catch (IOException e) {
            // Ignore
        }
        return false;
    }

    public void addItem() {
        System.out.println("\nAdding New Item (Press Ctrl+X to return to main menu)");
        System.out.println("─".repeat(50));
        
        try {
            System.out.print("Enter item name: ");
            if (checkForCtrlX()) return;
            String name = scanner.nextLine();

            System.out.print("Enter item ID: ");
            if (checkForCtrlX()) return;
            int id = scanner.nextInt();

            System.out.print("Enter quantity: ");
            if (checkForCtrlX()) return;
            int quantity = scanner.nextInt();

            System.out.print("Enter retail price (RM): ");
            if (checkForCtrlX()) return;
            double retail = scanner.nextDouble();

            System.out.print("Enter wholesale price (RM): ");
            if (checkForCtrlX()) return;
            double wholesale = scanner.nextDouble();

            inventory.put(id, new Item(name, id, quantity, retail, wholesale));
            saveInventory();
            System.out.println("\nItem added successfully!");
            System.out.println("Press Enter to continue...");
            scanner.nextLine(); // Consume newline
            scanner.nextLine(); // Wait for Enter
        } catch (Exception e) {
            System.out.println("Invalid input. Press Enter to continue...");
            scanner.nextLine(); // Consume invalid input
            scanner.nextLine(); // Wait for Enter
        }
    }

    public void updateQuantity() {
        System.out.println("\nUpdating Quantity (Press Ctrl+X to return to main menu)");
        System.out.println("─".repeat(50));
        
        try {
            System.out.print("Enter item ID: ");
            if (checkForCtrlX()) return;
            int id = scanner.nextInt();

            if (!inventory.containsKey(id)) {
                System.out.println("Item not found. Press Enter to continue...");
                scanner.nextLine(); // Consume newline
                scanner.nextLine(); // Wait for Enter
                return;
            }

            System.out.print("Enter new quantity: ");
            if (checkForCtrlX()) return;
            int quantity = scanner.nextInt();

            inventory.get(id).setQuantity(quantity);
            saveInventory();
            System.out.println("\nQuantity updated successfully!");
            System.out.println("Press Enter to continue...");
            scanner.nextLine(); // Consume newline
            scanner.nextLine(); // Wait for Enter
        } catch (Exception e) {
            System.out.println("Invalid input. Press Enter to continue...");
            scanner.nextLine(); // Consume invalid input
            scanner.nextLine(); // Wait for Enter
        }
    }

    public void run() {
        loadInventory();

        while (true) {
            clearScreen();
            displayMenu();
            System.out.print("\nChoose an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                clearScreen();

                switch (choice) {
                    case 1:
                        addItem();
                        break;
                    case 2:
                        updateQuantity();
                        break;
                    case 3:
                        displayInventory();
                        System.out.print("\nPress Enter to continue...");
                        scanner.nextLine();
                        break;
                    case 4:
                        System.out.println("Thank you for using the Inventory Management System!");
                        scanner.close();
                        reader.close();
                        System.exit(0);
                    default:
                        System.out.println("Invalid option. Press Enter to continue...");
                        scanner.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Press Enter to continue...");
                scanner.nextLine(); // Consume invalid input
                scanner.nextLine(); // Wait for Enter
            }
        }
    }

    public static void main(String[] args) {
        new InventoryTracker().run();
    }
}