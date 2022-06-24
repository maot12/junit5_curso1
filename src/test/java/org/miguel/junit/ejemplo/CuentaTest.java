package org.miguel.junit.ejemplo;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.miguel.junit.exceptions.DineroInsuficienteExceptions;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * @author mortega2
 * @project junit_app
 * @date 21/06/2022
 */
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando la clase Test.");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando la clase Test.");
    }

    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        System.out.println("Iniciando el método.");
        testReporter.publishEntry(" Ejecutando: "+testInfo.getDisplayName() +" "+testInfo.getTestMethod().get().getName()
                +" con las etiquetas "+testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando el método.");
    }

    @Tag("cuenta")
    @Nested
    @DisplayName("Probando atributos de la cuenta corriente.")
    class CuentaTestNombreSaldo{
        @Test
        @DisplayName("Probando el nombre.")
        void test_nombre_cuenta() {
            //cuenta.setPersona("Andres");
            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")){
                testReporter.publishEntry("Hacer algo con la etiqueta cuenta.");
            }

            String esperado = "Andres";
            String real = cuenta.getPersona();

            assertNotNull(real, () -> "La cuenta no puede ser null.");
            assertEquals(esperado,real, () ->  "El nombre de la cuenta no es el que se esperaba.");
            assertFalse(real.equals("Andre"), () ->  "Nombre cuenta esperada debe ser igual a la real.");
        }

        @Test
        @DisplayName("Probando el saldo.")
        void test_saldo_cuenta() {
            assertNotNull(cuenta.getSaldo());
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
    }

    @Nested
    class CuentaOperaciones{
        @Tag("cuenta")
        @Test
        @DisplayName("Probando el método debito.")
        void test_debito_cuenta() {
            cuenta.debito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue());
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Test
        @DisplayName("Probando el método crédito.")
        void test_credito_cuenta() {
            cuenta.credito(new BigDecimal(100));

            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Tag("banco")
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
    }

    @Tag("cuenta")
    @Tag("error")
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

    @Tag("cuenta")
    @Tag("banco")
    @Test
    //@Disabled
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

    @Nested
    class SistemaOperativoTest{
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
    }

    @Nested
    class JavaVersionTest{
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
    }

    @Nested
    class SistemPropertiesTest{
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
    }

    @Nested
    class VariableAmbiente{
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

    @Test
    @DisplayName("Probando el saldo de la cuenta DEV.")
    void test_saldo_cuentaDev() {
        boolean es_dev = "dev".equals(System.getProperty("ENV"));
        assumeFalse(es_dev);
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Probando el saldo de la cuenta DEV 2.")
    void test_saldo_cuentaDev2() {
        boolean es_dev = "dev".equals(System.getProperty("ENV"));
        assumingThat(es_dev, ()->{
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345 , cuenta.getSaldo().doubleValue());

        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @RepeatedTest(value = 5, name = "Repetición número {currentRepetition} de {totalRepetitions}")
    @DisplayName("Probando el método debito.")
    void test_debito_cuenta_repetido(RepetitionInfo info) {
        if(info.getCurrentRepetition() == 3) {
            System.out.println("Estamos en la repetición "+ info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadasTest{
        @ParameterizedTest(name = "número {index} ejecutando con valor {argumentsWithNames}")
        @ValueSource(strings = {"100","200","300","500","700","1000.12345"})
        @DisplayName("ValueSource.")
        void test_debito_cuenta(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {argumentsWithNames}")
        @CsvSource({"1,100","2,200","3,300","4,500","5,700","6,1000.12345"})
        @DisplayName("CsvSource.")
        void test_debito_cuenta_CsvSource(String index, String monto) {
            System.out.println(index +" -> "+monto);
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {argumentsWithNames}")
        @CsvSource({"200,100,John,Andres","250,200,Pepe,Pepe","300,300,maria,Maria","510,500,Pepa,Pepa","750,700,Lucas,Luca","1000.12345,1000.12345,Cata,Cata"})
        @DisplayName("CsvSource2.")
        void test_debito_cuenta_CsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo +" -> "+monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        @DisplayName("CsvFileSource.")
        void test_debito_cuenta_CsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));

            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "número {index} ejecutando con valor {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        @DisplayName("CsvFileSource2.")
        void test_debito_cuenta_CsvFileSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado,actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Tag("param")
    @ParameterizedTest(name = "número {index} ejecutando con valor {argumentsWithNames}")
    @MethodSource("montoList")
    @DisplayName("MethodSource.")
    void test_debito_cuenta_MethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));

        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    private static List<String> montoList() {
        return Arrays.asList("100","200","300","500","700","1000.12345");
    }

    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest {
        @Test
        @Timeout(1)
        void prueba_Timeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void prueba_Timeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void test_Timeout_assertions() {
            assertTimeout(Duration.ofSeconds(5),
                    ()->{ TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }

}