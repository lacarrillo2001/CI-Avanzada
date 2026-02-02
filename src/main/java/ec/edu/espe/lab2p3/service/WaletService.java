package ec.edu.espe.lab2p3.service;

import ec.edu.espe.lab2p3.dto.WalletReponse;
import ec.edu.espe.lab2p3.model.Wallet;
import ec.edu.espe.lab2p3.repository.WalletRepository;

import java.util.Optional;

public class WaletService {
    private final WalletRepository walletRepository;
    private final RiskClient riskClient;

    public WaletService(WalletRepository walletRepository, RiskClient riskClient) {
        this.walletRepository = walletRepository;
        this.riskClient = riskClient;
    }

    //Crear una cuenta si cumple con las reglas del negocio
    public WalletReponse createWallet(String ownerEmail, double initialBalance) {
        if (ownerEmail == null || ownerEmail.isEmpty() || ownerEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }

        //Regla de negocio: Usuario Bloqueado
        if (riskClient.isBloqued(ownerEmail)) {
            throw new IllegalStateException("User already blocked");
        }

        //Regla de negocio: No duplicar cuenta por email

        if(walletRepository.existByOwnerEmail(ownerEmail)){
            throw new IllegalStateException("Wallet already exists");
        }

        Wallet wallet = new Wallet( ownerEmail, initialBalance);
        Wallet save= walletRepository.save(wallet);

        return new WalletReponse(save.getId(), save.getBalance());
    }

    //Depositar dinero en la wallet
    public double deposit(String walletId, double amount){
        if (amount <0){
            throw new IllegalArgumentException("Amount must be negative");
        }

        Optional<Wallet> found = walletRepository.findById(walletId);

        if(found.isEmpty()){
            throw new IllegalStateException("Wallet not found");
        }

        Wallet wallet = found.get();
        wallet.deposit(amount);

        //Persistimos el nuevo saldo
        walletRepository.save(wallet);
        return wallet.getBalance();
    }
}
