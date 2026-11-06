package model;

/*
 * Category enum
 * 	- categories for Seeds, Fertilizers and Soil Amendments, 
 * 		Pesticides, Hand Tools, Watering Supplies, and Planting Materials
 */

public enum Category {

	// TYPES
    SEEDS("Seeds"),
    FERTILIZERS_SOIL("Fertilizers and Soil Amendments"),
    PESTICIDES("Pesticides"),
    HAND_TOOLS("Hand Tools"),
    WATERING("Watering Supplies"),
    PLANTING_MATERIALS("Planting Materials");

    // METHODS
    /*
     * Category(String)
     * 	- constructor method
     * 	- "Seeds" becomes label for SEEDS
     * 	- "Fertilizers and Soil Amendments" becomes label for FERTILIZERS_SOIL
     * 	- etc.
     */
    private final String label;
    Category(String label) {
        this.label = label;
    } // end of constructor method

    /*
     * toString()
     * 	- returns label for each category
     */
    @Override
    public String toString() {
        return label;
    } // end of toString
	
} // end of Category enum
