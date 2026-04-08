import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

// ─────────────────────────────────────────────────────────────
//  Transaction  –  immutable record of a single account event
// ─────────────────────────────────────────────────────────────
class Transaction {

    private final String type;
    private final double amount;
    private final double balanceAfter;
    private final Date   date;

    public Transaction(String type, double amount, double balanceAfter) {
        this.type         = type;
        this.amount       = amount;
        this.balanceAfter = balanceAfter;
        this.date         = new Date();
    }

    @Override
    public String toString() {
        return String.format("  [%s]  %-12s  Amount : Rs.%10.2f  |  Balance After : Rs.%10.2f",
                date, type, amount, balanceAfter);
    }
}

// ─────────────────────────────────────────────────────────────
//  BankAccount  –  core domain class (Encapsulation + OOP)
// ─────────────────────────────────────────────────────────────
class BankAccount {

    // Encapsulated attributes
    private final String            accountNumber;
    private final String            accountHolderName;
    private       double            balance;
    private final String            pin;                  // 4-digit PIN for login
    private final List<Transaction> transactionHistory;

    // Constructor
    public BankAccount(String accountNumber, String accountHolderName,
                       double initialDeposit, String pin) {
        this.accountNumber     = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance           = initialDeposit;
        this.pin               = pin;
        this.transactionHistory = new ArrayList<>();

        if (initialDeposit > 0) {
            transactionHistory.add(
                    new Transaction("OPEN/DEPOSIT", initialDeposit, balance));
        }
    }

    // ── Getters ──────────────────────────────────────────────
    public String getAccountNumber()     { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public double getBalance()           { return balance; }

    // ── PIN verification ─────────────────────────────────────
    public boolean verifyPin(String inputPin) {
        return this.pin.equals(inputPin);
    }

    // ── deposit(amount) ──────────────────────────────────────
    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("  [X]  Deposit amount must be greater than zero.");
            return false;
        }
        balance += amount;
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance));
        System.out.printf("  [OK] Rs.%.2f deposited successfully.  New balance : Rs.%.2f%n",
                amount, balance);
        return true;
    }

    // ── withdraw(amount) ─────────────────────────────────────
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("  [X]  Withdrawal amount must be greater than zero.");
            return false;
        }
        if (amount > balance) {
            System.out.printf("  [X]  Insufficient balance.  Available : Rs.%.2f%n", balance);
            return false;
        }
        balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance));
        System.out.printf("  [OK] Rs.%.2f withdrawn successfully.  New balance : Rs.%.2f%n",
                amount, balance);
        return true;
    }

    // ── checkBalance() ───────────────────────────────────────
    public void checkBalance() {
        System.out.printf("  Current Balance for [%s] : Rs.%.2f%n",
                accountNumber, balance);
    }

    // ── displayAccountDetails() ──────────────────────────────
    public void displayAccountDetails() {
        System.out.println();
        System.out.println("  +------------------------------------------+");
        System.out.println("  |           ACCOUNT  DETAILS               |");
        System.out.println("  +------------------------------------------+");
        System.out.printf( "  |  Account Number  : %-22s|%n", accountNumber);
        System.out.printf( "  |  Account Holder  : %-22s|%n", accountHolderName);
        System.out.printf( "  |  Balance         : Rs. %-19.2f|%n", balance);
        System.out.println("  +------------------------------------------+");
        System.out.println();
    }

    // ── displayTransactionHistory() ──────────────────────────
    public void displayTransactionHistory() {
        System.out.println();
        System.out.println("  --- Transaction History for Account ["
                + accountNumber + "] ---");
        if (transactionHistory.isEmpty()) {
            System.out.println("  No transactions recorded.");
        } else {
            for (Transaction t : transactionHistory) {
                System.out.println(t);
            }
        }
        System.out.println();
    }
}

// ─────────────────────────────────────────────────────────────
//  BankingInformationSystem  –  main / menu driver
// ─────────────────────────────────────────────────────────────
public class BankingInformationSystem {

    // Shared state
    private static final List<BankAccount> accounts = new ArrayList<>();
    private static int accountCounter = 1000;   // auto-incrementing seed
    private static final Scanner sc = new Scanner(System.in);

    // ── Entry point ──────────────────────────────────────────
    public static void main(String[] args) {
        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            String raw = sc.nextLine().trim();
            int choice = parseIntSafe(raw);

            switch (choice) {
                case 1  -> createAccount();
                case 2  -> depositMoney();
                case 3  -> withdrawMoney();
                case 4  -> checkBalance();
                case 5  -> displayAccountDetails();
                case 6  -> displayTransactionHistory();
                case 7  -> { running = false; printGoodbye(); }
                default -> System.out.println("  [X]  Invalid option. Please choose 1-7.\n");
            }
        }
        sc.close();
    }

    // ── Banner & Menu Prints ─────────────────────────────────

    private static void printBanner() {
        System.out.println();
        System.out.println("  +================================================+");
        System.out.println("  |    WELCOME TO BANKING INFORMATION SYSTEM       |");
        System.out.println("  |              Core Java Edition                 |");
        System.out.println("  +================================================+");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println("  +-----------------------------------+");
        System.out.println("  |            MAIN MENU             |");
        System.out.println("  +-----------------------------------+");
        System.out.println("  |  1. Create Account               |");
        System.out.println("  |  2. Deposit Money                |");
        System.out.println("  |  3. Withdraw Money               |");
        System.out.println("  |  4. Check Balance                |");
        System.out.println("  |  5. Display Account Details      |");
        System.out.println("  |  6. Transaction History          |");
        System.out.println("  |  7. Exit                         |");
        System.out.println("  +-----------------------------------+");
        System.out.print("  Enter your choice: ");
    }

    private static void printGoodbye() {
        System.out.println();
        System.out.println("  Thank you for using Banking Information System. Goodbye!");
        System.out.println();
    }

    // ── Feature : Create Account ─────────────────────────────
    private static void createAccount() {
        System.out.println("\n  -- Create New Account --");

        System.out.print("  Enter account holder name  : ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("  [X]  Name cannot be empty.\n");
            return;
        }

        double initialDeposit = promptDouble("  Enter initial deposit (Rs.): ");
        if (initialDeposit < 0) {
            System.out.println("  [X]  Initial deposit cannot be negative.\n");
            return;
        }

        String pin = promptPin("  Set a 4-digit PIN           : ");
        if (pin == null) return;   // user cancelled

        String accNo   = "ACC" + (++accountCounter);
        BankAccount ba = new BankAccount(accNo, name, initialDeposit, pin);
        accounts.add(ba);

        System.out.println();
        System.out.println("  [OK] Account created successfully!");
        System.out.println("  [OK] Your Account Number is : " + accNo
                + "  <-- please save this!");
        System.out.println();
    }

    // ── Feature : Deposit Money ──────────────────────────────
    private static void depositMoney() {
        System.out.println("\n  -- Deposit Money --");
        BankAccount ba = loginToAccount();
        if (ba == null) return;
        double amount = promptDouble("  Enter deposit amount (Rs.): ");
        ba.deposit(amount);
        System.out.println();
    }

    // ── Feature : Withdraw Money ─────────────────────────────
    private static void withdrawMoney() {
        System.out.println("\n  -- Withdraw Money --");
        BankAccount ba = loginToAccount();
        if (ba == null) return;
        double amount = promptDouble("  Enter withdrawal amount (Rs.): ");
        ba.withdraw(amount);
        System.out.println();
    }

    // ── Feature : Check Balance ──────────────────────────────
    private static void checkBalance() {
        System.out.println("\n  -- Check Balance --");
        BankAccount ba = loginToAccount();
        if (ba == null) return;
        ba.checkBalance();
        System.out.println();
    }

    // ── Feature : Display Account Details ────────────────────
    private static void displayAccountDetails() {
        System.out.println("\n  -- Account Details --");
        BankAccount ba = loginToAccount();
        if (ba == null) return;
        ba.displayAccountDetails();
    }

    // ── Feature : Transaction History ────────────────────────
    private static void displayTransactionHistory() {
        System.out.println("\n  -- Transaction History --");
        BankAccount ba = loginToAccount();
        if (ba == null) return;
        ba.displayTransactionHistory();
    }

    // ── Login Helper (account number + PIN) ──────────────────
    private static BankAccount loginToAccount() {
        if (accounts.isEmpty()) {
            System.out.println("  [X]  No accounts exist yet. Please create one first.\n");
            return null;
        }

        System.out.print("  Enter Account Number : ");
        String accNo = sc.nextLine().trim();

        BankAccount found = null;
        for (BankAccount ba : accounts) {
            if (ba.getAccountNumber().equalsIgnoreCase(accNo)) {
                found = ba;
                break;
            }
        }
        if (found == null) {
            System.out.println("  [X]  Account not found.\n");
            return null;
        }

        System.out.print("  Enter PIN            : ");
        String pin = sc.nextLine().trim();
        if (!found.verifyPin(pin)) {
            System.out.println("  [X]  Incorrect PIN. Access denied.\n");
            return null;
        }

        System.out.println("  [OK] Logged in. Welcome, " + found.getAccountHolderName() + "!");
        return found;
    }

    // ── Input Utility Methods ─────────────────────────────────

    /**
     * Prompts for a double, re-prompting on non-numeric input.
     */
    private static double promptDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = sc.nextLine().trim();
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                System.out.println("  [X]  Please enter a valid numeric amount.");
            }
        }
    }

    /**
     * Prompts for a 4-digit numeric PIN; returns null if user types 'q' to cancel.
     */
    private static String promptPin(String prompt) {
        while (true) {
            System.out.print(prompt);
            String pin = sc.nextLine().trim();
            if ("q".equalsIgnoreCase(pin)) return null;
            if (pin.matches("\\d{4}")) return pin;
            System.out.println("  [X]  PIN must be exactly 4 digits (type 'q' to cancel).");
        }
    }

    /**
     * Parses an integer safely; returns -1 on failure so the switch falls to default.
     */
    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}