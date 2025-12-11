package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Admin;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IAdminJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes de Integração para AdminService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private IAdminJpaRepository adminJpaRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        adminJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar administrador com dados válidos")
    void testCriarAdminComDadosValidos() throws ValidationException {
        // Arrange
        String username = "adminTeste";
        String password = "SenhaForte@123";

        // Act
        Admin admin = adminService.createAdmin(username, password);

        // Assert
        assertThat(admin).isNotNull();
        assertThat(admin.getUsername()).isEqualTo(username);
        assertThat(passwordEncoder.matches(password, admin.getPassword())).isTrue();
        assertThat(admin.getAtivo()).isTrue();

        Optional<Admin> adminEncontrado = adminJpaRepository.findByUsername(username);
        assertThat(adminEncontrado).isPresent();
    }

    @Test
    @DisplayName("Deve rejeitar username com menos de 3 caracteres")
    void testRejeiterUsernameComMenosDeTresCaracteres() {
        // Arrange
        String username = "ab";
        String password = "SenhaForte@123";

        // Act & Assert
        assertThrows(ValidationException.class, () -> adminService.createAdmin(username, password));
    }

    @Test
    @DisplayName("Deve rejeitar username com mais de 50 caracteres")
    void testRejeiterUsernameComMaisDe50Caracteres() {
        // Arrange
        String username = "a".repeat(51);
        String password = "SenhaForte@123";

        // Act & Assert
        assertThrows(ValidationException.class, () -> adminService.createAdmin(username, password));
    }

    @Test
    @DisplayName("Deve rejeitar password com menos de 8 caracteres")
    void testRejeiterPasswordComMenosDe8Caracteres() {
        // Arrange
        String username = "adminTeste";
        String password = "Abc@12";

        // Act & Assert
        assertThrows(ValidationException.class, () -> adminService.createAdmin(username, password));
    }

    @Test
    @DisplayName("Deve rejeitar password sem letra maiúscula")
    void testRejeiterPasswordSemLetraMaiuscula() {
        // Arrange
        String username = "adminTeste";
        String password = "abcdef@123";

        // Act & Assert
        assertThrows(ValidationException.class, () -> adminService.createAdmin(username, password));
    }

    @Test
    @DisplayName("Deve rejeitar password sem letra minúscula")
    void testRejeiterPasswordSemLetraMinuscula() {
        // Arrange
        String username = "adminTeste";
        String password = "ABCDEF@123";

        // Act & Assert
        assertThrows(ValidationException.class, () -> adminService.createAdmin(username, password));
    }

    @Test
    @DisplayName("Deve rejeitar password sem número")
    void testRejeiterPasswordSemNumero() {
        // Arrange
        String username = "adminTeste";
        String password = "AbCdEf@xyz";

        // Act & Assert
        assertThrows(ValidationException.class, () -> adminService.createAdmin(username, password));
    }

    @Test
    @DisplayName("Deve rejeitar password sem caractere especial")
    void testRejeiterPasswordSemCaractereEspecial() {
        // Arrange
        String username = "adminTeste";
        String password = "Abcdef123";

        // Act & Assert
        assertThrows(ValidationException.class, () -> adminService.createAdmin(username, password));
    }

    @Test
    @DisplayName("Deve prevenir criar admin com username duplicado")
    void testNaoCriarAdminComUsernameDuplicado() throws ValidationException {
        // Arrange
        String username = "adminTeste";
        String password = "SenhaForte@123";

        // Act
        adminService.createAdmin(username, password);

        // Assert
        assertThrows(ValidationException.class, () -> adminService.createAdmin(username, "OutraSenha@123"));
    }

    @Test
    @DisplayName("Deve desativar administrador corretamente")
    void testDesativarAdmin() throws ValidationException {
        // Arrange
        String username = "adminTeste";
        String password = "SenhaForte@123";
        Admin admin = adminService.createAdmin(username, password);
        UUID adminId = admin.getId();

        // Act
        boolean resultado = adminService.desativarAdmin(adminId);

        // Assert
        assertThat(resultado).isTrue();
        Optional<Admin> adminEncontrado = adminJpaRepository.findById(adminId);
        assertThat(adminEncontrado).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar admin inexistente")
    void testDesativarAdminInexistente() {
        // Arrange
        UUID adminIdInexistente = UUID.randomUUID();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> adminService.desativarAdmin(adminIdInexistente));
    }

    @Test
    @DisplayName("UT-ADM-012: Deve desativar administrador por username")
    void testDesativarAdminPorUsername() throws ValidationException {
        // Arrange
        String username = "adminTeste";
        String password = "SenhaForte@123";
        adminService.createAdmin(username, password);

        // Act
        boolean resultado = adminService.desativarAdminPorUsername(username);

        // Assert
        assertThat(resultado).isTrue();
        Optional<Admin> adminEncontrado = adminJpaRepository.findByUsername(username);
        assertThat(adminEncontrado).isEmpty();
    }

}

