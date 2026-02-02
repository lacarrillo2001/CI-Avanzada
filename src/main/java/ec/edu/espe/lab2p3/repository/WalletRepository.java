package ec.edu.espe.lab2p3.repository;

import ec.edu.espe.lab2p3.model.Wallet;
import java.util.Optional;

public interface WalletRepository {

    Wallet save(Wallet wallet);

    Optional<Wallet> findById(String id);

    boolean existByOwnerEmail(String ownerEmail);



}
