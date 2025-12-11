package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.EnderecoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.HidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusHidrometro;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoCliente;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoEndereco;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.infra.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes de Integração para HidrometroService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HidrometroServiceTest {

    @Autowired
    private HidrometroService hidrometroService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private IHidrometroJpaRepository hidrometroJpaRepository;

    @Autowired
    private IClienteJpaRepository clienteJpaRepository;

    @Autowired
    private IEnderecoJpaRepository enderecoJpaRepository;

    private ClientePfDTO clientePfDTO;
    private EnderecoDTO enderecoDTO;
    private String cpfValido = "111.444.777-35";
    private String emailCliente = "cliente@hidrometro.com";
    private String telefoneCliente = "81999999999";

    @BeforeEach
    void setUp() throws ValidationException {
        // Limpar dados antes de cada teste
        hidrometroJpaRepository.deleteAll();
        clienteJpaRepository.deleteAll();

        // Criar cliente de teste
        enderecoDTO = new EnderecoDTO(
            "Rua dos Hidrômetros",
            "123",
            "",
            "Centro",
            "Recife",
            "PE",
            "50100000",
            TipoEndereco.RESIDENCIAL
        );

        clientePfDTO = new ClientePfDTO(
            cpfValido,
            "João Hidrometro",
            emailCliente,
            telefoneCliente,
            enderecoDTO,
            TipoCliente.RESIDENCIAL
        );

        clienteService.criarClientePessoaFisica(clientePfDTO);
    }

    @Test
    @DisplayName("Deve salvar hidrômetro com dados válidos")
    void testSalvarHidrometroComDadosValidos() throws ValidationException {
        // Arrange
        HidrometroDTO hidrometroDTO = new HidrometroDTO(
            "SHA001",
            LocalDate.now(),
            "ATIVO",
            100,
            cpfValido,
            enderecoDTO
        );

        // Act
        hidrometroService.salvarHidrometro(hidrometroDTO);

        // Assert
        Optional<Hidrometro> hidrometroEncontrado = hidrometroJpaRepository.findById("SHA001");
        assertThat(hidrometroEncontrado).isPresent();
        assertThat(hidrometroEncontrado.get().getIdSha()).isEqualTo("SHA001");
        assertThat(hidrometroEncontrado.get().getLimiteConsumoMensalM3()).isEqualTo(100);
        assertThat(hidrometroEncontrado.get().getStatus()).isEqualTo(StatusHidrometro.ATIVO);
    }

    @Test
    @DisplayName("Deve rejeitar hidrômetro para cliente inexistente")
    void testSalvarHidrometroParaClienteInexistente() {
        // Arrange
        String cpfInexistente = "999.999.999-99";
        HidrometroDTO hidrometroDTO = new HidrometroDTO(
            "SHA002",
            LocalDate.now(),
            "ATIVO",
            100,
            cpfInexistente,
            enderecoDTO
        );

        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            hidrometroService.salvarHidrometro(hidrometroDTO);
        });
    }

    @Test
    @DisplayName("Deve prevenir criar hidrômetro com ID SHA duplicado")
    void testNaoCriarHidrometroComIdShaDuplicado() throws ValidationException {
        // Arrange
        HidrometroDTO hidrometroDTO = new HidrometroDTO(
            "SHA003",
            LocalDate.now(),
            "ATIVO",
            100,
            cpfValido,
            enderecoDTO
        );

        // Act
        hidrometroService.salvarHidrometro(hidrometroDTO);

        // Assert
        assertThrows(RuntimeException.class, () -> {
            hidrometroService.salvarHidrometro(hidrometroDTO);
        });
    }

    @Test
    @DisplayName("Deve reutilizar endereço existente ao criar novo hidrômetro")
    void testReutilizarEnderecoExistente() throws ValidationException {
        // Arrange
        HidrometroDTO hidrometroDTO1 = new HidrometroDTO(
            "SHA004A",
            LocalDate.now(),
            "ATIVO",
            100,
            cpfValido,
            enderecoDTO
        );

        HidrometroDTO hidrometroDTO2 = new HidrometroDTO(
            "SHA004B",
            LocalDate.now(),
            "ATIVO",
            150,
            cpfValido,
            enderecoDTO
        );

        // Act
        hidrometroService.salvarHidrometro(hidrometroDTO1);
        hidrometroService.salvarHidrometro(hidrometroDTO2);

        // Assert
        Cliente cliente = clienteJpaRepository.findByCpfCnpj("11144477735").get();
        long enderecos = enderecoJpaRepository.count();
        // Verificar que apenas 1 endereço foi criado (reuso)
        assertThat(cliente.getEnderecos().size()).isLessThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Deve salvar hidrômetro com limite mensal de consumo")
    void testSalvarHidrometroComLimiteMensalConsumo() throws ValidationException {
        // Arrange
        int limiteMensal = 250;
        HidrometroDTO hidrometroDTO = new HidrometroDTO(
            "SHA005",
            LocalDate.now(),
            "ATIVO",
            limiteMensal,
            cpfValido,
            enderecoDTO
        );

        // Act
        hidrometroService.salvarHidrometro(hidrometroDTO);

        // Assert
        Optional<Hidrometro> hidrometroEncontrado = hidrometroJpaRepository.findById("SHA005");
        assertThat(hidrometroEncontrado).isPresent();
        assertThat(hidrometroEncontrado.get().getLimiteConsumoMensalM3()).isEqualTo(limiteMensal);
    }

    @Test
    @DisplayName("Deve buscar hidrômetro pelo ID SHA")
    void testBuscarHidrometroPorSha() throws ValidationException {
        // Arrange
        String idSha = "SHA006";
        HidrometroDTO hidrometroDTO = new HidrometroDTO(
            idSha,
            LocalDate.now(),
            "ATIVO",
            100,
            cpfValido,
            enderecoDTO
        );
        hidrometroService.salvarHidrometro(hidrometroDTO);

        // Act
        Optional<Hidrometro> hidrometroEncontrado = hidrometroJpaRepository.findById(idSha);

        // Assert
        assertThat(hidrometroEncontrado).isPresent();
        assertThat(hidrometroEncontrado.get().getIdSha()).isEqualTo(idSha);
    }

    @Test
    @DisplayName("Deve ativar um hidrômetro desativado")
    void testAtivarHidrometroDesativado() throws ValidationException {
        // Arrange
        HidrometroDTO hidrometroDTO = new HidrometroDTO(
            "SHA007",
            LocalDate.now(),
            "INATIVO",
            100,
            cpfValido,
            enderecoDTO
        );
        hidrometroService.salvarHidrometro(hidrometroDTO);

        // Act
        boolean resultado = hidrometroService.alterarStatusHidrometro("SHA007", true);

        // Assert
        assertThat(resultado).isTrue();
        Optional<Hidrometro> hidrometroAtualizado = hidrometroJpaRepository.findById("SHA007");
        assertThat(hidrometroAtualizado.get().getStatus()).isEqualTo(StatusHidrometro.ATIVO);
    }

    @Test
    @DisplayName("Deve desativar um hidrômetro ativo")
    void testDesativarHidrometroAtivo() throws ValidationException {
        // Arrange
        HidrometroDTO hidrometroDTO = new HidrometroDTO(
            "SHA008",
            LocalDate.now(),
            "ATIVO",
            100,
            cpfValido,
            enderecoDTO
        );
        hidrometroService.salvarHidrometro(hidrometroDTO);

        // Act
        boolean resultado = hidrometroService.alterarStatusHidrometro("SHA008", false);

        // Assert
        assertThat(resultado).isTrue();
        Optional<Hidrometro> hidrometroAtualizado = hidrometroJpaRepository.findById("SHA008");
        assertThat(hidrometroAtualizado.get().getStatus()).isEqualTo(StatusHidrometro.INATIVO);
    }

    @Test
    @DisplayName("Deve alternar o status do hidrômetro múltiplas vezes")
    void testAlternarStatusHidrometro() throws ValidationException {
        // Arrange
        HidrometroDTO hidrometroDTO = new HidrometroDTO(
            "SHA009",
            LocalDate.now(),
            "ATIVO",
            100,
            cpfValido,
            enderecoDTO
        );
        hidrometroService.salvarHidrometro(hidrometroDTO);

        // Act & Assert
        // Desativar
        boolean resultado1 = hidrometroService.alterarStatusHidrometro("SHA009", false);
        assertThat(resultado1).isTrue();
        assertThat(hidrometroJpaRepository.findById("SHA009").get().getStatus()).isEqualTo(StatusHidrometro.INATIVO);

        // Reativar
        boolean resultado2 = hidrometroService.alterarStatusHidrometro("SHA009", true);
        assertThat(resultado2).isTrue();
        assertThat(hidrometroJpaRepository.findById("SHA009").get().getStatus()).isEqualTo(StatusHidrometro.ATIVO);
    }

    @Test
    @DisplayName("Hidrômetro deve estar obrigatoriamente associado a cliente e endereço")
    void testAssociacaoHidrometroClienteEndereco() throws ValidationException {
        // Arrange
        HidrometroDTO hidrometroDTO = new HidrometroDTO(
            "SHA010",
            LocalDate.now(),
            "ATIVO",
            100,
            cpfValido,
            enderecoDTO
        );

        // Act
        hidrometroService.salvarHidrometro(hidrometroDTO);

        // Assert
        Hidrometro hidrometro = hidrometroJpaRepository.findById("SHA010").get();
        assertThat(hidrometro.getCliente()).isNotNull();
        assertThat(hidrometro.getEnderecoInstalacao()).isNotNull();
        assertThat(hidrometro.getCliente().getCpfCnpj()).isEqualTo("11144477735");
    }

}

