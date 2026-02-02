package ec.edu.espe.lab2p3;

import ec.edu.espe.lab2p3.dto.WalletReponse;
import ec.edu.espe.lab2p3.model.Wallet;
import ec.edu.espe.lab2p3.repository.WalletRepository;
import ec.edu.espe.lab2p3.service.RiskClient;
import ec.edu.espe.lab2p3.service.WaletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.internal.matchers.Null;
import org.mockito.internal.matchers.Or;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WalletServiceTest {
    private WalletRepository walletRepository;
    private WaletService walletService;
    private RiskClient riskClient;

    @BeforeEach
    public  void setUp(){
        walletRepository = org.mockito.Mockito.mock(WalletRepository.class);
        riskClient = org.mockito.Mockito.mock(RiskClient.class);
        walletService = new WaletService(walletRepository, riskClient);
    }

    @Test
    void createWallet_validData_shouldSaveAndReturnReponse() {
        // Arrange
        String ownerEmail = "Luis@espe.edu.ec";
        double initialBalance = 100.0;


        when(walletRepository.existByOwnerEmail(ownerEmail)).thenReturn(Boolean.FALSE);
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        // Act

        WalletReponse reponse = walletService.createWallet(ownerEmail, initialBalance);
        // Assert
        assertNotNull(reponse.getWalletId());
        assertEquals(100.0, reponse.getBalance());

        verify(riskClient).isBloqued(ownerEmail);
        verify(walletRepository).existByOwnerEmail(ownerEmail);
        verify(walletRepository).save(any(Wallet.class));

    }

    @Test
    void createWallet_invalidEmail_shouldThrow_andNotCallDependencies() {
        // Arrange
        String ownerEmail = "Luis-espe.edu.ec";

        // Act & Assert
         assertThrows(IllegalArgumentException.class, () ->
            walletService.createWallet(ownerEmail, 50.0));

        // No debe llamar a las dependencias por que falla la validacion
        verifyNoInteractions(walletRepository, riskClient);
    }

    @Test
    void deposit_walletNotFound_shouldThrow() {
        // Arrange
        String walletId = "no-exist-wallet";


       when(walletRepository.existByOwnerEmail(walletId)).thenReturn(Optional.empty().isEmpty());

       //Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,() -> walletService.deposit(walletId,60));

        assertEquals("Wallet not found", exception.getMessage());
        verify(walletRepository).findById(walletId);
        verify(walletRepository, never()).save(any(Wallet.class));

    }

    @Test
    void deposit_shouldUpdateBalance_andSave_usingCaptor() {
        // Arrange
        Wallet wallet = new Wallet("luis@espe.edu.ec", 300);
        String walletId = wallet.getId();

        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);

        // Act
        double newBalance = walletService.deposit(walletId, 300.0);

        // Assert
        assertEquals(600.0, newBalance);

        verify(walletRepository).save(captor.capture());
        Wallet saved = captor.getValue();
        assertEquals(600.0, saved.getBalance());
    }
}
