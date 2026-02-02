package ec.edu.espe.lab2p3.dto;

public class WalletReponse {

    private final String walletId;
    private final double balance;

    public WalletReponse(String walletId, double balance) {
        this.walletId = walletId;
        this.balance = balance;
    }
    public String getWalletId() {
        return walletId;
    }
    public double getBalance() {
        return balance;
    }
}
