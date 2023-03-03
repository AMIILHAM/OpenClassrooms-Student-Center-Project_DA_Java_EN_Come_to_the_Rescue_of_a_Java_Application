package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	
	private static final double _0_95 = 0.95;
	private TicketDAO ticketDAO;
	
    public FareCalculatorService() {
		super();
	}

    public FareCalculatorService(TicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
	}

	public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = ticket.getInTime().getTime();
        double outHour = ticket.getOutTime().getTime();

        //TODO:   code corrigé
        double durationMs = outHour - inHour;
        double duration = durationMs/3600000;
       
        // Condition pour la gratuite de 30 min de stationnement
        
        if(duration<=0.5) { 
        	duration=0;
        }

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
            	if(isReccurent(ticket.getVehicleRegNumber())) {
            		ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR * _0_95);
            	} else {
            		ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
            	}
                break;
            }
            case BIKE: {
            	if(isReccurent(ticket.getVehicleRegNumber())) {
            		ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR * _0_95);
            	} else {
            		ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
            	}
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

	private boolean isReccurent(String vehiculeRegNumber) {
		return ticketDAO.countOccurrenceVehiculeRegNumber(vehiculeRegNumber) >= 1;
	}
}