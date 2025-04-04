package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.BookingDTO;
import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.PagedResponse;
import com.digitalhouse.court_rental.entity.Booking;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.User;
import com.digitalhouse.court_rental.repository.BookingRepository;
import com.digitalhouse.court_rental.repository.CourtRepository;
import com.digitalhouse.court_rental.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user@example.com");
    }

    @Test
    void testSearchAvailableCourts() {
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, "Court A", "Soccer", "City A", "Available", "Description", 10, BigDecimal.valueOf(100), "Address", "Neighborhood", "image1.jpg", "Feature1", "FeatureImage1", 1L});

        when(courtRepository.getCourtsByFilters(anyInt(), anyInt(), anyString(), anyString(), any(), any()))
                .thenReturn(mockResults);

        PagedResponse<CourtDTO> response = bookingService.searchAvailableCourts(1, 10, List.of(1), List.of(1), LocalDate.now(), LocalTime.now());

        assertNotNull(response);
        assertEquals(1, response.getData().size());
        assertEquals("Court A", response.getData().getFirst().getName());
    }

    @Test
    void testCreateBooking_Success() {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setCourtId(1);
        bookingDTO.setBookingDate(LocalDate.now());
        bookingDTO.setStartTime(LocalTime.of(10, 0));
        bookingDTO.setEndTime(LocalTime.of(11, 0));
        bookingDTO.setCapacity(5);

        User mockUser = new User();
        mockUser.setId_user(1L);

        Court mockCourt = new Court();
        mockCourt.setIdCourt(1);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(courtRepository.findById(anyLong())).thenReturn(Optional.of(mockCourt));
        when(bookingRepository.existsOverlappingBooking(anyInt(), any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.createBooking(bookingDTO, authentication);

        assertNotNull(booking);
        assertEquals(mockUser, booking.getUser());
        assertEquals(mockCourt, booking.getCourt());
        assertEquals(bookingDTO.getBookingDate(), booking.getBookingDate());
    }

    @Test
    void testCreateBooking_CourtNotAvailable() {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setCourtId(1);
        bookingDTO.setBookingDate(LocalDate.now());
        bookingDTO.setStartTime(LocalTime.of(10, 0));
        bookingDTO.setEndTime(LocalTime.of(11, 0));

        User mockUser = new User();
        mockUser.setId_user(1L);

        Court mockCourt = new Court();
        mockCourt.setIdCourt(1);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(courtRepository.findById(anyLong())).thenReturn(Optional.of(mockCourt));
        when(bookingRepository.existsOverlappingBooking(anyInt(), any(), any(), any())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> bookingService.createBooking(bookingDTO, authentication));

        assertEquals("La cancha ya está reservada en este horario. Por favor, elige otro disponible.", exception.getReason());
    }

    @Test
    void testGetUserBookingHistory() {
        User mockUser = new User();
        mockUser.setId_user(1L);

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1, LocalDateTime.now(), LocalDate.now(), "10:00-11:00", 1, "Court A", 1, "Confirmed", 1, "Soccer"});

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(bookingRepository.getUserBookingHistory(anyInt())).thenReturn(mockResults);

        List<Map<String, Object>> bookingHistory = bookingService.getUserBookingHistory(authentication);

        assertNotNull(bookingHistory);
        assertEquals(1, bookingHistory.size());
        assertEquals("Court A", bookingHistory.getFirst().get("courtName"));
    }

    @Test
    void testGetAvailability() {
        List<Object[]> mockReservedTimes = new ArrayList<>();
        mockReservedTimes.add(new Object[]{LocalTime.of(10, 0), LocalTime.of(11, 0)});

        when(bookingRepository.findReservedTimesByCourtAndDate(anyLong(), any())).thenReturn(mockReservedTimes);

        Map<String, Map<LocalDate, List<LocalTime>>> availability = bookingService.getAvailability(1L);

        assertNotNull(availability);
        assertTrue(availability.containsKey("availableSlots"));
        assertTrue(availability.containsKey("reservedSlots"));
    }

}