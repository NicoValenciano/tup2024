package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.ClienteAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaNotSupportedException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class CuentaServiceTest {
    @Mock
    private CuentaDao cuentaDao;
    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Generar casos de test para darDeAltaCuenta
    //    1 - cuenta existente
    //    2 - cuenta no soportada
    //    3 - cliente ya tiene cuenta de ese tipo
    //    4 - cuenta creada exitosamente

    //    1 - cuenta existente
    @Test
    public void testCuentaAlreadyExists() {
        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setNumeroCuenta(123456789);

        when(cuentaDao.find(123456789)).thenReturn(new Cuenta());
        assertThrows(CuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 26456439));

    }
    //    2 - cuenta no soportada
    @Test
    public void testTipoCuentaNoSoportada() {
        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.DOLARES)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setNumeroCuenta(123456789);

        assertThrows(TipoCuentaNotSupportedException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 26456439));

    }
    //    3 - cliente ya tiene cuenta de ese tipo
    @Test
    public void testClienteTieneCuentaDeEsteTipo() throws TipoCuentaAlreadyExistsException {
        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setNumeroCuenta(123456789);

        doThrow(TipoCuentaAlreadyExistsException.class).when(clienteService).agregarCuenta(cuenta, 29857643);
        assertThrows(TipoCuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta, 29857643));

    }
    //    4 - cuenta creada exitosamente
    @Test
    public void testCuentaCreadaExitosamente() throws TipoCuentaAlreadyExistsException, CuentaAlreadyExistsException, ClienteAlreadyExistsException, TipoCuentaNotSupportedException {
        Cuenta cuenta = new Cuenta()
                .setMoneda(TipoMoneda.PESOS)
                .setBalance(500000)
                .setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuenta.setNumeroCuenta(123456789);

        when(cuentaDao.find(123456789)).thenReturn(null);

        cuentaService.darDeAltaCuenta(cuenta, 29857643);
        verify(cuentaDao, times(1)).save(cuenta);

    }
}