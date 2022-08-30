package com.bmoises.minhasfinancas.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bmoises.minhasfinancas.exceptions.ErroAutenticacao;
import com.bmoises.minhasfinancas.exceptions.RegraNegocioException;
import com.bmoises.minhasfinancas.model.entity.Usuario;
import com.bmoises.minhasfinancas.model.repository.UsuarioRepository;
import com.bmoises.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean
	UsuarioRepository repository;

	@Test
	public void deveSalvarUmUsuario() {
		Assertions.assertThrows( Throwable.class , () -> {
			// cenario
			Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
			Usuario usuario = Usuario.builder().nome("nome").email("email@email.com").senha("senha").build();

			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

			// acao
			Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

			// verificacao
			Assertions.assertNotNull(usuarioSalvo);
			Assertions.assertEquals(usuarioSalvo.getId(), 1l);
			Assertions.assertEquals(usuarioSalvo.getNome(), "nome");
			Assertions.assertEquals(usuarioSalvo.getEmail(), "email@email.com");
			Assertions.assertEquals(usuarioSalvo.getSenha(), "senha");
		});
	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			// cenario
			String email = "email@email.com";
			Usuario usuario = Usuario.builder().email(email).build();
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

			// acao
			service.salvarUsuario(usuario);

			// verificacao
			Mockito.verify(repository, Mockito.never()).save(usuario);
		});
	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		Assertions.assertDoesNotThrow(() -> {
			// cenario
			String email = "email@email.com";
			String senha = "senha";

			Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
			Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

			// acao
			Usuario result = service.autenticar(email, senha);

			// verificacao
			Assertions.assertNotNull(result);
		});
	}

	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		// cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		// acao
		// acao
		Throwable exception = Assertions.assertThrows(Throwable.class, () -> {
			service.autenticar("email@email.com", "senha");
		});

		// verificacao
		Assertions.assertInstanceOf(ErroAutenticacao.class, exception, "Usuário não encontrado.");
	}

	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		// cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

		// acao
		Throwable exception = Assertions.assertThrows(Throwable.class, () -> {
			service.autenticar("email@email.com", "123");
		});

		// verificacao
		Assertions.assertInstanceOf(ErroAutenticacao.class, exception, "Senha inválida.");
	}

	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> {
			// cenario
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

			// acao
			service.validarEmail("email@email.com");
		});
	}

	@Test
	public void deveLancarErroQuandoExistirEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			// cenario
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

			// acao
			service.validarEmail("email@email.com");
		});
	}
}
