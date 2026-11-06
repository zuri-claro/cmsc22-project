package model;

/*
 * Role enum
 * 	- roles for Manager and Clerk
 * 	- access by using Role.MANAGER or Role.CLERK
 */

public enum Role {
	
	// TYPES
	MANAGER("Manager"),
	CLERK("Clerk");
    
    // METHODS
    /*
     * Role(String)
     * 	- constructor method
     * 	- "Manager" becomes label for MANAGER
     * 	- "Clerk" becomes label for CLERK
     */
	private final String label;
    Role(String label) {
    	this.label = label;
    } // end of constructor method
    
    /*
     * toString()
     * 	- returns label for each role
     */
    @Override
    public String toString() {
    	return label;
    } // end of toString
	
} // end of Role enum