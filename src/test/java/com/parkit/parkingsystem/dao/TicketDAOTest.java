package com.parkit.parkingsystem.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@RunWith(MockitoJUnitRunner.class)
class TicketDAOTest {

	Ticket ticket;

	ParkingSpot parkingSpot;

	static DataBaseConfig dataBaseConfig = mock(DataBaseConfig.class);

	static Connection con = mock(Connection.class);

	static PreparedStatement ps = mock(PreparedStatement.class);

	static ResultSet rs = mock(ResultSet.class);

	TicketDAO ticketDAO = new TicketDAO(dataBaseConfig);

	@BeforeEach
	void setUpPerTest() throws ClassNotFoundException, SQLException {
		when(dataBaseConfig.getConnection()).thenReturn(con);
		when(con.prepareStatement(any())).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		ticket = new Ticket();
		ticket.setInTime(new Date());
		ticket.setOutTime(new Date());
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("12345");
	}

	@Test
	public void saveTicketOKTest() throws SQLException, ClassNotFoundException {
		when(ps.execute()).thenReturn(true);
		assertTrue(ticketDAO.saveTicket(ticket));
	}

	@Test
	public void saveTicketKOTest() throws SQLException {
		when(ps.execute()).thenReturn(false);
		assertFalse(ticketDAO.saveTicket(ticket));
	}

	@Test
	public void countOccurrenceVehiculeRegNumberTest() throws SQLException {
		when(rs.next()).thenReturn(true);
		when(rs.getInt(1)).thenReturn(2);
		assertEquals(2, ticketDAO.countOccurrenceVehiculeRegNumber(ticket.getVehicleRegNumber()));
	}

	@Test
	public void getTicketOKTest() throws SQLException {		
		when(rs.next()).thenReturn(true);
		when(rs.getInt(1)).thenReturn(1);
		when(rs.getString(6)).thenReturn(ParkingType.CAR.name());
		when(rs.getDouble(3)).thenReturn(11.1);
		when(rs.getTimestamp(4)).thenReturn(new Timestamp(new Date().getTime()));
		when(rs.getTimestamp(5)).thenReturn(new Timestamp(new Date().getTime()));
		assertNotNull(ticketDAO.getTicket(ticket.getVehicleRegNumber()));
	}
	
	@Test
	public void updateTicketOKTest() throws SQLException, ClassNotFoundException {
		when(ps.execute()).thenReturn(true);
		assertTrue(ticketDAO.updateTicket(ticket));
	}

	@Test
	public void updateTicketKOTest() throws SQLException {
		when(ps.execute()).thenReturn(false);
		assertFalse(ticketDAO.updateTicket(ticket));
	}
}
