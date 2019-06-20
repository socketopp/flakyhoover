package lab;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TestConditionalTestLogic {
	List<String> classtestValues = new ArrayList<String>(Arrays.asList("setUp", "tearDown"));
	private List allAirportIds;
	private List allFlights;

	protected void setUp() throws Exception {
		allAirportIds = new ArrayList();
		allFlights = new ArrayList();
	}

	protected void tearDown() throws Exception {
		removeObjects(allAirportIds, "Airport");
		removeObjects(allFlights, "Flight");
	}

	private BigDecimal createTestAirport(String airportName) throws FlightBookingException {
		BigDecimal newAirportId = facade.createAirport(airportName, " Airport" + airportName, "City" + airportName);
		allAirportIds.add(newAirportId);
		return newAirportId;
	}

	public void removeObjects(List objectsToDelete, String type) {
		Iterator i = objectsToDelete.iterator();
		while (i.hasNext()) {
			try {
				BigDecimal id = (BigDecimal) i.next();
				if ("Airport" == type) {
					facade.removeAirport(id);
				} else {
					facade.removeFlight(id);
				}
			} catch (Exception e) {
				// do nothing if the remove failed
			}
		}
	}

	public void testGetFlightsByOriginAirport_OneOutboundFlight() throws Exception {
		// Fixture Setup
		BigDecimal outboundAirport = createTestAirport("1OF");
		BigDecimal inboundAirport = createTestAirport("1IF");
		FlightDto expectedFlightDto = createTestFlight(outboundAirport, inboundAirport);
		// Exercise System
		List flightsAtOrigin = facade.getFlightsByOriginAirport(outboundAirport);
		// Verify Outcome
		assertOnly1FlightInDtoList("Flights at origin", expectedFlightDto, flightsAtOrigin);
	}

	// From xUnit book
	public void testMultipleValueSetsOriginal() {
		// Set Up Fixture
		Calculator sut = new Calculator();
		// special case!
		TestValues[] testValues = { new TestValues(1, 2, 3), new TestValues(2, 3, 5), new TestValues(3, 4, 8),
				new TestValues(4, 5, 9) };
		for (int i = 0; i < testValues.length; i++) {
			TestValues values = testValues[i];
			// Exercise SUT
			int actual = sut.calculate(values.a, values.b);
			// Verify Result
			assertEquals(message(i), values.expectedSum, actual);
		}
	}

	public void testMultipleValueSetsTestWithList() {
		// Set Up Fixture
		Calculator sut = new Calculator();

		List<String> testValues = new ArrayList<String>(Arrays.asList("setUp", "tearDown"));

		for (int i = 0; i < testValues.length; i++) {
			TestValues values = testValues.get(i);
			// Exercise SUT
			int actual = sut.calculate(values.a, values.b);
			// Verify Result
			assertEquals(message(i), values.expectedSum, actual);
		}
	}

	public void testMultipleValueSetsTestwithoutDeclarationOrAssignmentinBody() {
		// Set Up Fixture
		Calculator sut = new Calculator();

		List<String> testValues = new ArrayList<String>(Arrays.asList("setUp", "tearDown"));

		for (int i = 0; i < testValues.length; i++) {

			// Removed the indexing of either a[i] or a.get(i). Since we are not using it,
			// we are doing something else which may cause flaky behaviour.

			// Exercise SUT
			int actual = sut.calculate(values.a, values.b);
			// Verify Result
			assertEquals(message(i), values.expectedSum, actual);
		}
	}

	public void testMultipleValueSetsTestWithListAndClassVariables() {
		// Set Up Fixture
		Calculator sut = new Calculator();

		for (int i = 0; i < testValues.length; i++) {
			TestValues values = classtestValues.get(i);
			// Exercise SUT
			int actual = sut.calculate(values.a, values.b);
			// Verify Result
			assertEquals(message(i), values.expectedSum, actual);
		}
	}

	public void conditionalVerificationLogic() {
		// verify Vancouver is in the list
		actual = null;
		i = flightsFromCalgary.iterator();
		while (i.hasNext()) {
			FlightDto flightDto = (FlightDto) i.next();
			if (flightDto.getFlightNumber().equals(expectedCalgaryToVan.getFlightNumber())) {
				actual = flightDto;
				assertEquals("Flight from Calgary to Vancouver", expectedCalgaryToVan, flightDto);
				break;
			}
		}
	}

	public void testDisplayCurrentTime_whenever() {
		// fixture setup
		TimeDisplay sut = new TimeDisplay();
		// exercise SUT
		String result = sut.getCurrentTimeAsHtmlFragment();
		// verify outcome
		Calendar time = new DefaultTimeProvider().getTime();
		StringBuffer expectedTime = new StringBuffer();
		expectedTime.append("<span class=\"tinyBoldText\">");
		if ((time.get(Calendar.HOUR_OF_DAY) == 0) && (time.get(Calendar.MINUTE) <= 1)) {
			expectedTime.append("Midnight");
		} else if ((time.get(Calendar.HOUR_OF_DAY) == 12) && (time.get(Calendar.MINUTE) == 0)) { // noon
			expectedTime.append("Noon");
		} else {
			SimpleDateFormat fr = new SimpleDateFormat("h:mm a");
			expectedTime.append(fr.format(time.getTime()));
		}
		expectedTime.append("</span>");
		assertEquals(expectedTime, result);
	}

	public void testGetFlightsByOrigin_NoInboundFlight_SMRTD() throws Exception {
		// Set Up Fixture
		BigDecimal outboundAirport = createTestAirport("1OF");
		BigDecimal inboundAirport = null;
		FlightDto expFlightDto = null;
		try {
			inboundAirport = createTestAirport("1IF");
			expFlightDto = createTestFlight(outboundAirport, inboundAirport);
			// Exercise System
			List flightsAtDestination1 = facade.getFlightsByOriginAirport(inboundAirport);
			// Verify Outcome
			assertEquals(0, flightsAtDestination1.size());
		} finally {
			try {
				facade.removeFlight(expFlightDto.getFlightNumber());
			} finally {
				try {
					facade.removeAirport(inboundAirport);
				} finally {
					facade.removeAirport(outboundAirport);
				}
			}
		}
	}

	public void testCombinationsOfInputValues() {
		// Set up fixture
		Calculator sut = new Calculator();
		int expected; // TBD inside loops
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				// Exercise SUT
				int actual = sut.calculate(i, j);
				// Verify result
				if (i == 3 & j == 4) { // special case
					expected = 8;
				} else {
					expected = i + j;
				}
				assertEquals(message(i, j), expected, actual);
			}
		}
	}

	public void testMultipleValueSets() {
		// Set Up Fixture
		Calculator sut = new Calculator();
		TestValues[] testValues = { new TestValues(1, 2, 3), new TestValues(2, 3, 5), new TestValues(3, 4, 8), // special
																												// case!
				new TestValues(4, 5, 9) };
		for (int i = 0; i < testValues.length; i++) {
			TestValues values = testValues[i];
			if (values == "FlakyBehaviour") {
				assertFalse(values == "Cause error");
			} else {
				assertEquals(values.equals("If no conditions in for loop, then ur good"));
			}
			// Exercise SUT
			int actual = sut.calculate(values.a, values.b);
			// Verify Outcome
			assertEquals(message(i), values.expectedSum, actual);
		}
	}

}
