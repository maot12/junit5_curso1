package org.miguel.junit.ejemplo;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.miguel.junit.exceptions.DineroInsuficienteExceptions;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author mortega2
 * @project junit_app
 * @date 21/06/2022
 */
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    Cuenta cuenta;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando la clase Test.");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando la clase Test.");
    }

    @BeforeEach
    void initMetodoTest() {
        System.out.println("Iniciando el método.");
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el método.");
    }

    @Test
    @DisplayName("Probando el nombre de la cuenta.")
    void test_nombre_cuenta() {
        //cuenta.setPersona("Andres");

        String esperado = "Andres";
        String real = cuenta.getPersona();

        assertNotNull(real, () -> "La cuenta no puede ser null.");
        assertEquals(esperado,real, () ->  "El nombre de la cuenta no es el que se esperaba.");
        assertFalse(real.equals("Andre"), () ->  "Nombre cuenta esperada debe ser igual a la real.");
    }

    @Test
    @DisplayName("Probando el saldo de la cuenta.")
    void test_saldo_cuenta() {
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Probando las referencias.")
    void test_referencia_cuenta() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("8900.9997"));

        //assertNotEquals(cuenta1, cuenta2);
        assertEquals(cuenta1, cuenta2);

    }

    @Test
    @DisplayName("Probando el método debito.")
    void test_debito_cuenta() {
        cuenta.debito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Probando el método crédito.")
    void test_credito_cuenta() {
        cuenta.credito(new BigDecimal(100));

        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Lanzando una exception.")
    void test_dinero_insufiicente_exception() {

        Exception exception = assertThrows(DineroInsuficienteExceptions.class, ()-> {
            cuenta.debito(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";

        assertEquals(esperado, actual);
    }

    @Test
    @DisplayName("Operaciones con las cuentas.")
    void test_transferir_dinero_cuentas() {
        Cuenta cuentaUno = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuentaDos = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.setNombre("Banco del Estado");
        banco.transferir(cuentaDos, cuentaUno, new BigDecimal(500));

        assertEquals("1000.8989", cuentaDos.getSaldo().toPlainString());
        assertEquals("3000", cuentaUno.getSaldo().toPlainString());
    }

    @Test
    @Disabled
    @DisplayName("Ejercicios de relaciones.")
    void test_relacion_banco_cuenta() {
        //fail();
        Cuenta cuentaUno = new Cuenta("Jhon Doe", new BigDecimal("2500"));
        Cuenta cuentaDos = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuentaUno);
        banco.addCuenta(cuentaDos);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuentaDos, cuentaUno, new BigDecimal(500));

        assertAll(
                () -> {
                    assertEquals("1000.8989",
                            cuentaDos.getSaldo().toPlainString(),
                            ()-> "El valor del saldo de la cuentaDos no es el esperado.");
                },
                () -> {
                    assertEquals("3000",
                            cuentaUno.getSaldo().toPlainString());
                },
                () -> {
                    assertEquals(2,
                            banco.getCuentas().size(),
                            ()-> "El banco no tiene las cuentas esperadas.");
                },
                () -> {
                    assertEquals("Banco del Estado",
                            cuentaUno.getBanco().getNombre());
                },
                () -> {
                    assertEquals("Andres", banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Andres"))
                            .findFirst()
                            .get().getPersona());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .filter(c -> c.getPersona().equals("Andres"))
                            .findFirst().isPresent());
                },
                () -> {
                    assertTrue(banco.getCuentas().stream()
                            .anyMatch(c -> c.getPersona().equals("Andres")));
                }
        );

    }

    @Test
    @DisplayName("Ejecutar solo en Windows.")
    @EnabledOnOs(OS.WINDOWS)
    void test_solo_windows() {

    }

    @Test
    @DisplayName("Ejecutar solo en Mac y Linux.")
    @EnabledOnOs({OS.MAC, OS.LINUX})
    void test_solo_maclinux() {

    }

    @Test
    @DisplayName("No ejecutar en windows.")
    @DisabledOnOs(OS.WINDOWS)
    void test_no_windows() {

    }

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    void solo_jdk_8() {

    }

    @Test
    @EnabledOnJre(JRE.JAVA_15)
    void solo_jdk_15() {

    }

    @Test
    @DisabledOnJre(JRE.JAVA_8)
    void test_no_jdk8() {

    }

    @Test
    void imprimir_system_properties() {
        Properties properties = System.getProperties();
        properties.forEach((k,v)-> System.out.println(k + ": " + v));
    }

    @Test
    @EnabledIfSystemProperty(named = "java.version", matches = "1.8.0_321")
    void test_java_version() {

    }

    @Test
    @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void test_solo_64() {

    }

    @Test
    @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
    void test_no_64() {

    }

    @Test
    void imprimir_variables_ambiente() {
        Map<String, String> getenv = System.getenv();
        getenv.forEach((k,v)-> System.out.println(k + " = " + v));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-11.0.14.*")
    void test_javahome() {

    }

    @Test
    @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "4")
    void test_procesadores() {

    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
    void testEnv() {

    }

    @Test
    @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
    void testEnvProdDisabled() {

    }
}