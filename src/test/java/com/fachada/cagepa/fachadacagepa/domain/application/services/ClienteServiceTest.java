package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.EnderecoDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoCliente;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoEndereco;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.util.CpfCnpjValidator;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Endereco;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IClienteJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes Unitários para ClienteService
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private IClienteJpaRepository clienteJpaRepository;

    private EnderecoDTO enderecoDTO1;
    private EnderecoDTO enderecoDTO2;
    private EnderecoDTO enderecoDTO3;

    @BeforeEach
    void setUp() {
        enderecoDTO1 = new EnderecoDTO(
            "Rua Principal",
            "100",
            "",
            "Centro",
            "Recife",
            "PE",
            "52000000",
            TipoEndereco.RESIDENCIAL
        );

        enderecoDTO2 = new EnderecoDTO(
            "Rua Secundária",
            "200",
            "",
            "Boa Viagem",
            "Recife",
            "PE",
            "51000000",
            TipoEndereco.COMERCIAL
        );

        enderecoDTO3 = new EnderecoDTO(
            "Av Terceira",
            "300",
            "Apt 5",
            "Espinheiro",
            "Recife",
            "PE",
            "52100000",
            TipoEndereco.RESIDENCIAL
        );
    }

    @Test
    @DisplayName("UT-CLI-001: Deve criar cliente pessoa física com dados válidos")
    void testCriarClientePFComDadosValidos() throws Exception {
        // Arrange
        ClientePfDTO clientePfDTO = new ClientePfDTO(
            "111.444.777-35",  // CPF válido
            "João Silva",
            "joao@email.com",
            "81999999999",
            enderecoDTO1,
            TipoCliente.RESIDENCIAL
        );

        // Act
        clienteService.criarClientePessoaFisica(clientePfDTO);

        // Assert
        Optional<Cliente> clienteEncontrado = clienteJpaRepository.findByCpfCnpj("11144477735");
        assertThat(clienteEncontrado).isPresent();
        assertThat(clienteEncontrado.get().getNomeCompleto()).isEqualTo("João Silva");
        assertThat(clienteEncontrado.get().getEmail()).isEqualTo("joao@email.com");
        assertThat(clienteEncontrado.get().getAtivo()).isTrue();
    }

    @Test
    @DisplayName("UT-CLI-002: Deve rejeitar cliente com email inválido")
    void testCriarClientePFComEmailInvalido() throws Exception {
        // Arrange
        ClientePfDTO clientePfDTO = new ClientePfDTO(
            "111.444.777-35",  // CPF válido
            "João Silva",
            "email_invalido",
            "81999999999",
            enderecoDTO1,
            TipoCliente.RESIDENCIAL
        );

        // Act & Assert
        assertThrows(ValidationException.class, () -> clienteService.criarClientePessoaFisica(clientePfDTO));
    }

    @Test
    @DisplayName("UT-CLI-003: Deve impedir criar cliente com CPF já existente")
    void testNaoCriarClientePFComCPFDuplicado() throws ValidationException {
        // Arrange
        ClientePfDTO clientePf1 = new ClientePfDTO(
            "111.444.777-35",  // CPF válido
            "João Silva",
            "joao@email.com",
            "81999999999",
            enderecoDTO1,
            TipoCliente.RESIDENCIAL
        );

        ClientePfDTO clientePf2 = new ClientePfDTO(
            "111.444.777-35",  // Mesmo CPF
            "José Silva",
            "jose@email.com",
            "81988888888",
            enderecoDTO2,
            TipoCliente.RESIDENCIAL
        );

        // Act
        clienteService.criarClientePessoaFisica(clientePf1);

        // Assert
        assertThrows(RuntimeException.class, () -> {
            clienteService.criarClientePessoaFisica(clientePf2);
        });
    }

    @Test
    @DisplayName("UT-CLI-004: Deve limpar CPF removendo caracteres especiais")
    void testLimparCPF() {
        // Arrange
        String cpfFormatado = "111.444.777-35";

        // Act
        String cpfLimpo = CpfCnpjValidator.cleanCpfCnpj(cpfFormatado);

        // Assert
        assertThat(cpfLimpo).isEqualTo("11144477735");
        assertThat(cpfLimpo).doesNotContain(".", "-");
    }

    @Test
    @DisplayName("UT-CLI-005: Deve desativar cliente corretamente")
    void testDesativarCliente() throws ValidationException {
        // Arrange
        ClientePfDTO clientePfDTO = new ClientePfDTO(
            "111.444.777-35",  // CPF válido
            "João Silva",
            "joao@email.com",
            "81999999999",
            enderecoDTO1,
            TipoCliente.RESIDENCIAL
        );
        clienteService.criarClientePessoaFisica(clientePfDTO);

        // Act
        boolean resultado = clienteService.desativarCliente("11144477735");

        // Assert
        assertThat(resultado).isTrue();
        Optional<Cliente> clienteDesativado = clienteJpaRepository.findByCpfCnpj("11144477735");
        assertThat(clienteDesativado.get().getAtivo()).isFalse();
    }

    @Test
    @DisplayName("UT-CLI-006: Deve listar apenas clientes ativos")
    void testListarApenasClientesAtivos() throws ValidationException {
        // Arrange
        ClientePfDTO cliente1 = new ClientePfDTO(
            "111.444.777-35", "João", "joao@email.com", "81999999999", enderecoDTO1, null
        );
        ClientePfDTO cliente2 = new ClientePfDTO(
            "222.555.888-46", "Maria", "maria@email.com", "81988888888", enderecoDTO2, null
        );

        clienteService.criarClientePessoaFisica(cliente1);
        clienteService.criarClientePessoaFisica(cliente2);
        clienteService.desativarCliente("22255588846");

        // Act
        List<Cliente> clientesAtivos = clienteService.obterTodosClientes();

        // Assert
        assertThat(clientesAtivos).hasSize(1);
        assertThat(clientesAtivos.get(0).getNomeCompleto()).isEqualTo("João");
    }

    @Test
    @DisplayName("UT-CLI-007: Deve obter cliente completo por CPF")
    void testObterClientePorCPF() throws Exception {
        // Arrange
        ClientePfDTO clientePfDTO = new ClientePfDTO(
            "111.444.777-35",  // CPF válido
            "João Silva",
            "joao@email.com",
            "81999999999",
            enderecoDTO1,
            null
        );
        clienteService.criarClientePessoaFisica(clientePfDTO);

        // Act
        Optional<Cliente> cliente = clienteService.obterClientePorCpfCnpj("11144477735");

        // Assert
        assertThat(cliente).isPresent();
        assertThat(cliente.orElseThrow().getCpfCnpj()).isEqualTo("11144477735");
        assertThat(cliente.orElseThrow().getEnderecos()).isNotEmpty();
    }

    @Test
    @DisplayName("UT-CLI-008: Deve adicionar múltiplos endereços a um cliente")
    void testAdicionarMultiplosEnderecos() throws Exception {
        // Arrange
        ClientePfDTO clientePfDTO = new ClientePfDTO(
            "111.444.777-35",  // CPF válido
            "João Silva",
            "joao@email.com",
            "81999999999",
            enderecoDTO1,
            null
        );
        clienteService.criarClientePessoaFisica(clientePfDTO);

        // Act
        clienteService.adicionarEnderecoCliente("11144477735", enderecoDTO2);
        clienteService.adicionarEnderecoCliente("11144477735", enderecoDTO3);

        // Assert
        Optional<Cliente> cliente = clienteService.obterClientePorCpfCnpj("11144477735");
        assertThat(cliente).isPresent();
        assertThat(cliente.orElseThrow().getEnderecos()).hasSize(3);
        assertThat(cliente.orElseThrow().getEnderecos())
            .extracting(Endereco::getBairro)
            .contains("Centro", "Boa Viagem", "Espinheiro");
    }

    @Test
    @DisplayName("UT-CLI-009: Deve criar cliente pessoa jurídica")
    void testCriarClientePJComDadosValidos() throws Exception {
        // Arrange
        ClientePjDTO clientePjDTO = new ClientePjDTO(
            "11.222.333/0001-81",  // CNPJ válido
            "Empresa XYZ",
            "Empresa XYZ LTDA",
            "empresa@email.com",
            "8133333333",
            enderecoDTO1,
            null
        );

        // Act
        clienteService.criarClientePessoaJuridica(clientePjDTO);

        // Assert
        Optional<Cliente> clienteEncontrado = clienteJpaRepository.findByCpfCnpj("11222333000181");
        assertThat(clienteEncontrado).isPresent();
        assertThat(clienteEncontrado.orElseThrow().getNomeFantasia()).isEqualTo("Empresa XYZ");
        assertThat(clienteEncontrado.orElseThrow().getRazaoSocial()).isEqualTo("Empresa XYZ LTDA");
    }

    @Test
    @DisplayName("UT-CLI-010: Deve prevenir cliente com email duplicado")
    void testNaoCriarClienteComEmailDuplicado() throws Exception {
        // Arrange
        ClientePfDTO cliente1 = new ClientePfDTO(
            "111.444.777-35", "João", "joao@email.com", "81999999999", enderecoDTO1, null
        );
        ClientePfDTO cliente2 = new ClientePfDTO(
            "222.555.888-46", "Maria", "joao@email.com", "81988888888", enderecoDTO2, null
        );

        // Act & Assert
        clienteService.criarClientePessoaFisica(cliente1);
        assertThrows(RuntimeException.class, () -> clienteService.criarClientePessoaFisica(cliente2));
    }
}

