package com.bmoises.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bmoises.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
	
}
