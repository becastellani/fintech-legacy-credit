package br.com.nogueiranogueira.aularefatoracao.repository;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitacaoCreditoRepository extends JpaRepository<SolicitacaoCredito, Long> {

    List<SolicitacaoCredito> findByCliente(String cliente);

    List<SolicitacaoCredito> findByAprovado(Boolean aprovado);

    List<SolicitacaoCredito> findByTipoConta(String tipoConta);

    @Query("SELECT s FROM SolicitacaoCredito s WHERE s.dataSolicitacao BETWEEN :dataInicio AND :dataFim")
    List<SolicitacaoCredito> findByDataSolicitacaoBetween(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim);

    @Query("SELECT COUNT(s) FROM SolicitacaoCredito s WHERE s.aprovado = true AND s.tipoConta = :tipoConta")
    Long countAprovadosByTipoConta(@Param("tipoConta") String tipoConta);
}

