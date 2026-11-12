package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class SalesTransaction {

	//attributes
	private final LocalDate transactionDate;
	private final LocalTime transactionTime;
    private final String clerkName;
    private final double totalAmount;
    private final double cashReceived;
    private final double changeGiven;

    //constructor
    public SalesTransaction(String clerkName, double totalAmount, double cashReceived, double changeGiven) {
    	//gives current time and date
    	this.transactionDate = LocalDate.now();
    	this.transactionTime = LocalTime.now();
        
    	//inputs from computer
        this.clerkName = clerkName;
        this.totalAmount = totalAmount;
        this.cashReceived = cashReceived;
        this.changeGiven = changeGiven;
    }

    //getters
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public LocalTime getTransactionTime() {
        return transactionTime;
    }
    
    public String getClerkName() {
        return clerkName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public double getChangeGiven() {
        return changeGiven;
    }
}