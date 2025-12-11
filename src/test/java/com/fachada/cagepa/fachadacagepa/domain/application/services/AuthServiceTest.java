package com.fachada.cagepa.fachadacagepa.domain.application.services;

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

import static org.assertj.core.api.Assertions.*;

/**
 * Testes de Integração para AuthService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private IAdminJpaRepository adminJpaRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // Limpar dados antes de cada teste
        adminJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve realizar login com credenciais válidas")
    void testLoginComCredenciaisValidas() {
        // Arrange
        String username = "adminTeste";
        String password = "SenhaForte@123";

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setAtivo(true);
        adminJpaRepository.save(admin);

        // Act
        String token = authService.login(username, password);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(authService.isAuthenticated(token)).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar login com senha incorreta")
    void testLoginComSenhaIncorreta() {
        // Arrange
        String username = "adminTeste";
        String senhaCorreta = "SenhaForte@123";
        String senhaIncorreta = "SenhaErrada@123";

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(senhaCorreta));
        admin.setAtivo(true);
        adminJpaRepository.save(admin);

        // Act
        String token = authService.login(username, senhaIncorreta);

        // Assert
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("Deve rejeitar login com admin inexistente")
    void testLoginComAdminInexistente() {
        // Arrange
        String usernameInexistente = "usuarioNaoExiste";
        String password = "SenhaForte@123";

        // Act
        String token = authService.login(usernameInexistente, password);

        // Assert
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("Deve rejeitar login com admin desativado")
    void testLoginComAdminInativo() {
        // Arrange
        String username = "adminTeste";
        String password = "SenhaForte@123";

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setAtivo(false);  // Admin inativo
        adminJpaRepository.save(admin);

        // Act
        String token = authService.login(username, password);

        // Assert
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("Deve validar token gerado corretamente")
    void testValidarTokenGerado() {
        // Arrange
        String username = "adminTeste";
        String password = "SenhaForte@123";

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setAtivo(true);
        adminJpaRepository.save(admin);

        // Act
        String token = authService.login(username, password);

        // Assert
        assertThat(token).isNotNull();
        assertThat(authService.isAuthenticated(token)).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar token inválido")
    void testRejeiterTokenInvalido() {
        // Arrange
        String tokenInvalido = "token.invalido.aqui";

        // Act
        boolean isValid = authService.isAuthenticated(tokenInvalido);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Deve criar admin padrão se nenhum admin existir")
    void testInicializarAdminPadrao() {
        // Arrange
        adminJpaRepository.deleteAll();
        assertThat(adminJpaRepository.count()).isZero();

        // Act
        authService.initializeDefaultAdmin();

        // Assert
        assertThat(adminJpaRepository.count()).isOne();
        var adminPadrao = adminJpaRepository.findByUsername("admin");
        assertThat(adminPadrao).isPresent();
        assertThat(adminPadrao.get().getUsername()).isEqualTo("admin");
    }

    @Test
    @DisplayName("Não deve duplicar admin padrão se já existir")
    void testNaoDuplicarAdminPadrao() {
        // Arrange
        authService.initializeDefaultAdmin();
        long countAntes = adminJpaRepository.count();

        // Act
        authService.initializeDefaultAdmin();

        // Assert
        long countDepois = adminJpaRepository.count();
        assertThat(countDepois).isEqualTo(countAntes);
    }

}

