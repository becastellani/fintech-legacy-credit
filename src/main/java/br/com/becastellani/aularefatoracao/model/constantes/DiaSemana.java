package br.com.becastellani.aularefatoracao.model.constantes;

import java.time.DayOfWeek;
import java.time.LocalDate;

public enum DiaSemana {
    DOMINGO(DayOfWeek.SUNDAY),
    SABADO(DayOfWeek.SATURDAY);

    private final DayOfWeek dayOfWeek;

    DiaSemana(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public static boolean isFinaldeSemana() {
        DayOfWeek hoje = LocalDate.now().getDayOfWeek();
        return hoje == DayOfWeek.SATURDAY || hoje == DayOfWeek.SUNDAY;
    }
}

