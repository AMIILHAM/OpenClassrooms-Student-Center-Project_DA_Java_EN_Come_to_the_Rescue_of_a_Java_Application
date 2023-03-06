package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static FareCalculatorService fareCalculatorService ;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        fareCalculatorService = new FareCalculatorService(ticketDAO);
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
 
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    @Test
      //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);
        
      // cette méthode permet de réserver la place numéro 1 et ajouter un ticket en base de données pour une voiture immatriculé ABCDEF (voir mock ci-dessus)
        parkingService.processIncomingVehicle();
        
     // check saved ticket

    	Ticket savedTicket = ticketDAO.getTicket("ABCDEF"); // permet de récupérer le ticket qu'on vient d'enregistrer

    	assertNotNull(savedTicket); // le ticket récupéré ne doit pas être null

    	// check parking table (on vient de résérver la place numéro 1 normalement la prochaine place dispo. est la 2

    	int nextAvailabelSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

    	assertEquals(nextAvailabelSlot, 2); // cela signifie que la table parking a bien été mise à jour

    	// fin du test

    	}
        	
        	
  

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO, fareCalculatorService);
        parkingService.processExitingVehicle();
        Ticket updatedTicket = ticketDAO.getTicket("ABCDEF"); 
        assertNotNull(updatedTicket);
        assertNotNull(updatedTicket.getOutTime());
        assertNotNull(updatedTicket.getPrice());
        assertNotEquals(0, updatedTicket.getPrice());
    }
   
		

}
