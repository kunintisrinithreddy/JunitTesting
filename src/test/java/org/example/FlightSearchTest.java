package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Comprehensive JUnit 5 test class for FlightSearch functionality
 * Tests all 11 validation conditions with boundary values and edge cases
 * Each test case includes multiple test data points to thoroughly validate the conditions
 */
public class FlightSearchTest {

    private FlightSearch flightSearch;
    private String validFutureDate;
    private String validReturnDate;

    @BeforeEach
    void setUp() {
        flightSearch = new FlightSearch();
        
        // Set up valid future dates for testing
        LocalDate futureDate = LocalDate.now().plusDays(30);
        LocalDate returnDate = LocalDate.now().plusDays(45);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        validFutureDate = futureDate.format(formatter);
        validReturnDate = returnDate.format(formatter);
    }

    /**
     * Test Case 1: Condition 1 - Total passenger count validation (1-9 passengers)
     * Test Data 1.1: Boundary value - exactly 1 passenger (minimum)
     * Test Data 1.2: Boundary value - exactly 9 passengers (maximum)
     */
    @Test
    void testPassengerCountValidation() {
        // Test Data 1.1: Minimum passengers (1 adult)
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            1, 0, 0), "Should accept minimum 1 passenger");

        // Test Data 1.2: Maximum passengers (9 adults)
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            9, 0, 0), "Should accept maximum 9 passengers");

        // Boundary test: 0 passengers (invalid)
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            0, 0, 0), "Should reject 0 passengers");

        // Boundary test: 10 passengers (invalid)
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            10, 0, 0), "Should reject 10 passengers");
    }

    /**
     * Test Case 2: Condition 2 - Children cannot be seated in emergency row or first class
     * Test Data 2.1: Children with emergency row seating (invalid)
     * Test Data 2.2: Children with first class seating (invalid)
     */
    @Test
    void testChildEmergencyRowAndFirstClassRestrictions() {
        // Test Data 2.1: Children with emergency row seating
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", true, validReturnDate, "mel", "economy",
            2, 1, 0), "Should reject children in emergency row seating");

        // Test Data 2.2: Children with first class seating
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "first",
            2, 1, 0), "Should reject children in first class");

        // Valid case: Children in economy without emergency row
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            2, 1, 0), "Should accept children in economy without emergency row");
    }

    /**
     * Test Case 3: Condition 3 - Infants cannot be seated in emergency row or business class
     * Test Data 3.1: Infants with emergency row seating (invalid)
     * Test Data 3.2: Infants with business class seating (invalid)
     */
    @Test
    void testInfantEmergencyRowAndBusinessClassRestrictions() {
        // Test Data 3.1: Infants with emergency row seating
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", true, validReturnDate, "mel", "economy",
            2, 0, 1), "Should reject infants in emergency row seating");

        // Test Data 3.2: Infants with business class seating
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "business",
            2, 0, 1), "Should reject infants in business class");

        // Valid case: Infants in economy without emergency row
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            2, 0, 1), "Should accept infants in economy without emergency row");
    }

    /**
     * Test Case 4: Condition 4 - Up to 2 children per adult (children must be seated next to adults)
     * Test Data 4.1: Valid ratio - 2 adults, 4 children (2:1 ratio)
     * Test Data 4.2: Invalid ratio - 1 adult, 3 children (exceeds 2:1 ratio)
     */
    @Test
    void testChildAdultRatioValidation() {
        // Test Data 4.1: Valid ratio - 2 adults, 4 children (maximum allowed)
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            2, 4, 0), "Should accept 4 children with 2 adults");

        // Test Data 4.2: Invalid ratio - 1 adult, 3 children (exceeds limit)
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            1, 3, 0), "Should reject 3 children with only 1 adult");

        // Boundary test: Children without adults
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            0, 2, 0), "Should reject children without accompanying adults");
    }

    /**
     * Test Case 5: Condition 5 - One infant per adult (infants sit on adult's lap)
     * Test Data 5.1: Valid ratio - 2 adults, 2 infants (1:1 ratio)
     * Test Data 5.2: Invalid ratio - 1 adult, 2 infants (exceeds 1:1 ratio)
     */
    @Test
    void testInfantAdultRatioValidation() {
        // Test Data 5.1: Valid ratio - 2 adults, 2 infants
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            2, 0, 2), "Should accept 2 infants with 2 adults");

        // Test Data 5.2: Invalid ratio - 1 adult, 2 infants
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            1, 0, 2), "Should reject 2 infants with only 1 adult");

        // Boundary test: Infants without adults
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            0, 0, 1), "Should reject infants without accompanying adults");
    }

    /**
     * Test Case 6: Condition 6 - Departure date cannot be in the past
     * Test Data 6.1: Past date (invalid)
     * Test Data 6.2: Today's date (valid)
     */
    @Test
    void testDepartureDateNotInPast() {
        // Test Data 6.1: Past date
        String pastDate = "01/01/2020";
        assertFalse(flightSearch.runFlightSearch(
            pastDate, "syd", false, validReturnDate, "mel", "economy",
            1, 0, 0), "Should reject past departure date");

        // Test Data 6.2: Today's date
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        assertTrue(flightSearch.runFlightSearch(
            today, "syd", false, validReturnDate, "mel", "economy",
            1, 0, 0), "Should accept today's departure date");
    }

    /**
     * Test Case 7: Condition 7 - Date format validation DD/MM/YYYY with strict validation
     * Test Data 7.1: Invalid format - wrong format
     * Test Data 7.2: Invalid date - non-existent date (29/02/2025 - not leap year)
     */
    @Test
    void testDateFormatValidation() {
        // Test Data 7.1: Invalid format
        assertFalse(flightSearch.runFlightSearch(
            "2025/12/25", "syd", false, validReturnDate, "mel", "economy",
            1, 0, 0), "Should reject invalid date format");

        // Test Data 7.2: Invalid date - 29/02/2025 (not a leap year)
        assertFalse(flightSearch.runFlightSearch(
            "29/02/2025", "syd", false, "29/03/2025", "mel", "economy",
            1, 0, 0), "Should reject invalid date (29/02/2025)");

        // Valid leap year date (using future date)
        assertTrue(flightSearch.runFlightSearch(
            "29/02/2028", "syd", false, "01/03/2028", "mel", "economy",
            1, 0, 0), "Should accept valid leap year date");
    }

    /**
     * Test Case 8: Condition 8 - Return date cannot be before departure date
     * Test Data 8.1: Return date before departure date (invalid)
     * Test Data 8.2: Return date same as departure date (valid)
     */
    @Test
    void testReturnDateAfterDepartureDate() {
        String departure = "15/12/2025";
        String returnDate = "10/12/2025";

        // Test Data 8.1: Return date before departure
        assertFalse(flightSearch.runFlightSearch(
            departure, "syd", false, returnDate, "mel", "economy",
            1, 0, 0), "Should reject return date before departure date");

        // Test Data 8.2: Return date same as departure
        assertTrue(flightSearch.runFlightSearch(
            departure, "syd", false, departure, "mel", "economy",
            1, 0, 0), "Should accept return date same as departure");
    }

    /**
     * Test Case 9: Condition 9 - Seating class validation
     * Test Data 9.1: Invalid seating class
     * Test Data 9.2: Valid seating class - "premium economy"
     */
    @Test
    void testSeatingClassValidation() {
        // Test Data 9.1: Invalid seating class
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "invalid",
            1, 0, 0), "Should reject invalid seating class");

        // Test Data 9.2: Valid seating class
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "premium economy",
            1, 0, 0), "Should accept valid seating class");
    }

    /**
     * Test Case 10: Condition 10 - Emergency row seating only available in economy class
     * Test Data 10.1: Emergency row in business class (invalid)
     * Test Data 10.2: Emergency row in economy class (valid)
     */
    @Test
    void testEmergencyRowSeatingClassRestriction() {
        // Test Data 10.1: Emergency row in business class (invalid)
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", true, validReturnDate, "mel", "business",
            1, 0, 0), "Should reject emergency row in business class");

        // Test Data 10.2: Emergency row in economy class (valid)
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", true, validReturnDate, "mel", "economy",
            1, 0, 0), "Should accept emergency row in economy class");
    }

    /**
     * Test Case 10b: Condition 10 - Additional emergency row tests for all seating classes
     * Test Data 10b.1: Emergency row in first class (invalid)
     * Test Data 10b.2: Emergency row in premium economy (invalid)
     */
    @Test
    void testEmergencyRowAllSeatingClasses() {
        // Test Data 10b.1: Emergency row in first class (invalid)
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", true, validReturnDate, "mel", "first",
            1, 0, 0), "Should reject emergency row in first class");

        // Test Data 10b.2: Emergency row in premium economy (invalid)
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", true, validReturnDate, "mel", "premium economy",
            1, 0, 0), "Should reject emergency row in premium economy");

        // Test Data 10b.3: Non-emergency row in first class (valid)
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "first",
            1, 0, 0), "Should accept non-emergency row in first class");

        // Test Data 10b.4: Non-emergency row in business class (valid)
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "business",
            1, 0, 0), "Should accept non-emergency row in business class");
    }

    /**
     * Test Case 11: Condition 11 - Airport code validation and different airports
     * Test Data 11.1: Invalid airport code
     * Test Data 11.2: Same departure and destination airport (invalid)
     */
    @Test
    void testAirportCodeValidation() {
        // Test Data 11.1: Invalid airport code
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "xxx", false, validReturnDate, "mel", "economy",
            1, 0, 0), "Should reject invalid airport code");

        // Test Data 11.2: Same departure and destination airport
        assertFalse(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "syd", "economy",
            1, 0, 0), "Should reject same departure and destination airport");

        // Valid case: Different valid airports
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", "economy",
            1, 0, 0), "Should accept different valid airports");
    }

    /**
     * Test Case 12: All valid input combinations (4 test data points)
     * Test Data 12.1: Single adult, economy class
     * Test Data 12.2: Family with children, premium economy
     * Test Data 12.3: Couple with infant, business class
     * Test Data 12.4: Group with mixed passengers, first class
     */
    @Test
    void testAllValidInputCombinations() {
        // Test Data 12.1: Single adult, economy class
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "lax", "economy",
            1, 0, 0), "Should accept single adult in economy");

        // Verify attributes are set correctly
        assertEquals("syd", flightSearch.getDepartureAirportCode());
        assertEquals("lax", flightSearch.getDestinationAirportCode());
        assertEquals(1, flightSearch.getAdultPassengerCount());

        // Test Data 12.2: Family with children, premium economy
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "mel", false, validReturnDate, "cdg", "premium economy",
            2, 2, 0), "Should accept family with children in premium economy");

        // Test Data 12.3: Couple with infant, economy class (business class not allowed for infants)
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "lax", false, validReturnDate, "del", "economy",
            2, 0, 1), "Should accept couple with infant in economy class");

        // Test Data 12.4: Group with mixed passengers, premium economy (first class not allowed for children)
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "del", false, validReturnDate, "pvg", "premium economy",
            3, 2, 1), "Should accept mixed group in premium economy");
    }

    /**
     * Parameterized test for all valid airport codes
     * Tests each valid airport code to ensure they are accepted
     */
    @ParameterizedTest
    @ValueSource(strings = {"syd", "mel", "lax", "cdg", "del", "pvg", "doh"})
    void testValidAirportCodes(String airportCode) {
        // Use a different destination airport to avoid same airport issue
        String destinationAirport = airportCode.equals("syd") ? "mel" : "syd";
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, airportCode, false, validReturnDate, destinationAirport, "economy",
            1, 0, 0), "Should accept valid airport code: " + airportCode);
    }

    /**
     * Parameterized test for all valid seating classes
     * Tests each valid seating class to ensure they are accepted
     */
    @ParameterizedTest
    @ValueSource(strings = {"economy", "premium economy", "business", "first"})
    void testValidSeatingClasses(String seatingClass) {
        assertTrue(flightSearch.runFlightSearch(
            validFutureDate, "syd", false, validReturnDate, "mel", seatingClass,
            1, 0, 0), "Should accept valid seating class: " + seatingClass);
    }

    /**
     * Test to verify that class attributes are properly initialized when validation passes
     */
    @Test
    void testClassAttributeInitialization() {
        String departureDate = validFutureDate;
        String returnDate = validReturnDate;
        String departureAirport = "syd";
        String destinationAirport = "mel";
        String seatingClass = "economy";
        boolean emergencyRow = false;
        int adults = 2;
        int children = 1;
        int infants = 1;

        boolean result = flightSearch.runFlightSearch(
            departureDate, departureAirport, emergencyRow, returnDate, 
            destinationAirport, seatingClass, adults, children, infants);

        assertTrue(result, "Flight search should succeed with valid parameters");
        
        // Verify all attributes are set correctly
        assertEquals(departureDate, flightSearch.getDepartureDate());
        assertEquals(departureAirport, flightSearch.getDepartureAirportCode());
        assertEquals(emergencyRow, flightSearch.isEmergencyRowSeating());
        assertEquals(returnDate, flightSearch.getReturnDate());
        assertEquals(destinationAirport, flightSearch.getDestinationAirportCode());
        assertEquals(seatingClass, flightSearch.getSeatingClass());
        assertEquals(adults, flightSearch.getAdultPassengerCount());
        assertEquals(children, flightSearch.getChildPassengerCount());
        assertEquals(infants, flightSearch.getInfantPassengerCount());
    }

    /**
     * Test to verify that class attributes are NOT initialized when validation fails
     */
    @Test
    void testClassAttributeNotInitializedOnFailure() {
        // Try invalid search that should fail
        boolean result = flightSearch.runFlightSearch(
            "01/01/2020", "syd", false, validReturnDate, "mel", "economy",
            1, 0, 0);

        assertFalse(result, "Flight search should fail with past date");
        
        // Verify attributes are not set (should be null or default values)
        assertNull(flightSearch.getDepartureDate());
        assertNull(flightSearch.getDepartureAirportCode());
        assertNull(flightSearch.getReturnDate());
        assertNull(flightSearch.getDestinationAirportCode());
        assertNull(flightSearch.getSeatingClass());
        assertEquals(0, flightSearch.getAdultPassengerCount());
        assertEquals(0, flightSearch.getChildPassengerCount());
        assertEquals(0, flightSearch.getInfantPassengerCount());
    }
}
