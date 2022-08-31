package com.bmoises.minhasfinancas.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bmoises.minhasfinancas.api.dto.LancamentoDTO;
import com.bmoises.minhasfinancas.exceptions.RegraNegocioException;
import com.bmoises.minhasfinancas.model.entity.Lancamento;
import com.bmoises.minhasfinancas.model.entity.Usuario;
import com.bmoises.minhasfinancas.model.enums.StatusLancamento;
import com.bmoises.minhasfinancas.model.enums.TipoLancamento;
import com.bmoises.minhasfinancas.service.LancamentoService;
import com.bmoises.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

	private LancamentoService service;
	private UsuarioService usuarioService;

	public LancamentoResource(LancamentoService service) {
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity salvar ( @RequestBody LancamentoDTO dto ) {
		
	}
	
	private Lancamento converter(LancamentoDTO dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		
		Usuario usuario = usuarioService.obterPorId(dto.getUsuario()).orElseThrow( () -> new RegraNegocioException("Usuário não encontrado para o Id informado."));
		
		lancamento.setUsuario(usuario);
		lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		
		return lancamento;
	}
}
