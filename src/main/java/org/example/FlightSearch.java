package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * FlightSearch class for validating flight search criteria in WorldWanderer website
 * This class validates various flight search parameters according to business rules
 */
public class FlightSearch {
   private String  departureDate;
   private String  departureAirportCode;
   private boolean emergencyRowSeating;
   private String  returnDate;
   private String  destinationAirportCode; 
   private String  seatingClass;
   private int     adultPassengerCount;
   private int     childPassengerCount;
   private int     infantPassengerCount;

   // Valid airport codes as per requirement
   private static final Set<String> VALID_AIRPORTS = new HashSet<>(Arrays.asList(
       "syd", "mel", "lax", "cdg", "del", "pvg", "doh"
   ));

   // Valid seating classes as per requirement
   private static final Set<String> VALID_SEATING_CLASSES = new HashSet<>(Arrays.asList(
       "economy", "premium economy", "business", "first"
   ));

   /**
    * Validates and processes flight search parameters
    * @param departureDate departure date in DD/MM/YYYY format
    * @param departureAirportCode departure airport code (lowercase)
    * @param emergencyRowSeating whether emergency row seating is requested
    * @param returnDate return date in DD/MM/YYYY format
    * @param destinationAirportCode destination airport code (lowercase)
    * @param seatingClass seating class preference
    * @param adultPassengerCount number of adult passengers
    * @param childPassengerCount number of child passengers
    * @param infantPassengerCount number of infant passengers
    * @return true if all validation conditions are met, false otherwise
    */
   public boolean runFlightSearch(String departureDate,    String departureAirportCode,   boolean emergencyRowSeating, 
                                  String returnDate,       String destinationAirportCode, String seatingClass, 
                                  int adultPassengerCount, int childPassengerCount,       int infantPassengerCount) {
      
      // Validate all parameters according to business rules
      if (!validatePassengerCounts(adultPassengerCount, childPassengerCount, infantPassengerCount)) {
         return false;
      }

      if (!validateChildEmergencyRowBusinessRules(childPassengerCount, emergencyRowSeating, seatingClass)) {
         return false;
      }

      if (!validateInfantEmergencyRowBusinessRules(infantPassengerCount, emergencyRowSeating, seatingClass)) {
         return false;
      }

      if (!validateChildAdultRatio(childPassengerCount, adultPassengerCount)) {
         return false;
      }

      if (!validateInfantAdultRatio(infantPassengerCount, adultPassengerCount)) {
         return false;
      }

      if (!validateDepartureDate(departureDate)) {
         return false;
      }

      if (!validateDateFormat(departureDate) || !validateDateFormat(returnDate)) {
         return false;
      }

      if (!validateReturnDate(departureDate, returnDate)) {
         return false;
      }

      if (!validateSeatingClass(seatingClass)) {
         return false;
      }

      if (!validateEmergencyRowSeatingClass(emergencyRowSeating, seatingClass)) {
         return false;
      }

      if (!validateAirportCodes(departureAirportCode, destinationAirportCode)) {
         return false;
      }

      // All validations passed - initialize class attributes
      this.departureDate = departureDate;
      this.departureAirportCode = departureAirportCode;
      this.emergencyRowSeating = emergencyRowSeating;
      this.returnDate = returnDate;
      this.destinationAirportCode = destinationAirportCode;
      this.seatingClass = seatingClass;
      this.adultPassengerCount = adultPassengerCount;
      this.childPassengerCount = childPassengerCount;
      this.infantPassengerCount = infantPassengerCount;

      return true;
   }

   /**
    * Condition 1: Validate total passenger count (1-9 passengers)
    */
   private boolean validatePassengerCounts(int adultCount, int childCount, int infantCount) {
      int totalPassengers = adultCount + childCount + infantCount;
      return totalPassengers >= 1 && totalPassengers <= 9;
   }

   /**
    * Condition 2: Children cannot be seated in emergency row or first class
    */
   private boolean validateChildEmergencyRowBusinessRules(int childCount, boolean emergencyRow, String seatingClass) {
      if (childCount > 0 && (emergencyRow || "first".equals(seatingClass))) {
         return false;
      }
      return true;
   }

   /**
    * Condition 3: Infants cannot be seated in emergency row or business class
    */
   private boolean validateInfantEmergencyRowBusinessRules(int infantCount, boolean emergencyRow, String seatingClass) {
      if (infantCount > 0 && (emergencyRow || "business".equals(seatingClass))) {
         return false;
      }
      return true;
   }

   /**
    * Condition 4: Up to 2 children per adult (children must be seated next to adults)
    */
   private boolean validateChildAdultRatio(int childCount, int adultCount) {
      if (adultCount == 0 && childCount > 0) {
         return false; // No adults to accompany children
      }
      return childCount <= (adultCount * 2);
   }

   /**
    * Condition 5: One infant per adult (infants sit on adult's lap)
    */
   private boolean validateInfantAdultRatio(int infantCount, int adultCount) {
      return infantCount <= adultCount;
   }

   /**
    * Condition 6: Departure date cannot be in the past
    */
   private boolean validateDepartureDate(String departureDate) {
      try {
         LocalDate departure = parseDate(departureDate);
         LocalDate today = LocalDate.now();
         return !departure.isBefore(today);
      } catch (DateTimeParseException e) {
         return false;
      }
   }

   /**
    * Condition 7: Validate date format DD/MM/YYYY and ensure valid date
    */
   private boolean validateDateFormat(String date) {
      try {
         parseDate(date);
         return true;
      } catch (DateTimeParseException e) {
         return false;
      }
   }

   /**
    * Condition 8: Return date cannot be before departure date
    */
   private boolean validateReturnDate(String departureDate, String returnDate) {
      try {
         LocalDate departure = parseDate(departureDate);
         LocalDate returnDateParsed = parseDate(returnDate);
         return !returnDateParsed.isBefore(departure);
      } catch (DateTimeParseException e) {
         return false;
      }
   }

   /**
    * Condition 9: Seating class must be valid
    */
   private boolean validateSeatingClass(String seatingClass) {
      return VALID_SEATING_CLASSES.contains(seatingClass.toLowerCase());
   }

   /**
    * Condition 10: Only economy class seating can have an emergency row (all classes of seating can be non-emergency)
    */
   private boolean validateEmergencyRowSeatingClass(boolean emergencyRow, String seatingClass) {
      if (emergencyRow && !"economy".equalsIgnoreCase(seatingClass)) {
         return false;
      }
      return true;
   }

   /**
    * Condition 11: Validate airport codes and ensure they are different
    */
   private boolean validateAirportCodes(String departureAirport, String destinationAirport) {
      boolean validAirports = VALID_AIRPORTS.contains(departureAirport.toLowerCase()) && 
                             VALID_AIRPORTS.contains(destinationAirport.toLowerCase());
      boolean differentAirports = !departureAirport.toLowerCase().equals(destinationAirport.toLowerCase());
      return validAirports && differentAirports;
   }

   /**
    * Helper method to parse date from DD/MM/YYYY format
    */
   private LocalDate parseDate(String date) throws DateTimeParseException {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      return LocalDate.parse(date, formatter);
   }

   // Getter methods for testing purposes
   public String getDepartureDate() { return departureDate; }
   public String getDepartureAirportCode() { return departureAirportCode; }
   public boolean isEmergencyRowSeating() { return emergencyRowSeating; }
   public String getReturnDate() { return returnDate; }
   public String getDestinationAirportCode() { return destinationAirportCode; }
   public String getSeatingClass() { return seatingClass; }
   public int getAdultPassengerCount() { return adultPassengerCount; }
   public int getChildPassengerCount() { return childPassengerCount; }
   public int getInfantPassengerCount() { return infantPassengerCount; }
}