package br.com.nogueiranogueira.aularefatoracao.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes_credito")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Cliente não pode ser vazio")
    @Column(nullable = false)
    private String cliente;

    @NotNull(message = "Valor não pode ser nulo")
    @Positive(message = "Valor deve ser positivo")
    @Column(nullable = false)
    private Double valor;

    @NotNull(message = "Score não pode ser nulo")
    @Min(value = 0, message = "Score deve ser maior ou igual a 0")
    @Max(value = 1000, message = "Score deve ser menor ou igual a 1000")
    @Column(nullable = false)
    private Integer score;

    @NotNull(message = "Status de negativação não pode ser nulo")
    @Column(nullable = false)
    private Boolean negativado;

    @NotBlank(message = "Tipo de conta não pode ser vazio")
    @Pattern(regexp = "^(PF|PJ)$", message = "Tipo de conta deve ser PF ou PJ")
    @Column(nullable = false)
    private String tipoConta;

    @Column(nullable = false)
    private Boolean aprovado;

    @Column(name = "motivo_reprovacao")
    private String motivoReprovacao;

    @Column(name = "data_solicitacao", nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;

    @PrePersist
    protected void onCreate() {
        dataSolicitacao = LocalDateTime.now();
    }
}

