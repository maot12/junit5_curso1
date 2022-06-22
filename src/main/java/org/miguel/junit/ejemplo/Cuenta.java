package org.miguel.junit.ejemplo;

import org.miguel.junit.exceptions.DineroInsuficienteExceptions;

import java.math.BigDecimal;

/**
 * @author mortega2
 * @project junit_app
 * @date 21/06/2022
 */
public class Cuenta {

    public Cuenta(String persona, BigDecimal saldo) {
        this.saldo = saldo;
        this.persona = persona;
    }

    private String persona;
    private BigDecimal saldo;

    private Banco banco;

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public void setBanco(Banco banco) {
        this.banco = banco;
    }

    public void debito(BigDecimal monto) {
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0){
            throw new DineroInsuficienteExceptions("Dinero Insuficiente");
        }
        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Cuenta)){
            return false;
        }
        Cuenta cuenta = (Cuenta) obj;
        if (this.persona == null || saldo == null){
            return false;
        }
        return this.persona.equals(cuenta.getPersona()) && this.saldo.equals(cuenta.getSaldo());
    }
}
