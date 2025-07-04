package com.voting.blockvote.service;

import com.voting.blockvote.dto.CreateElectionRequest;
import com.voting.blockvote.dto.ElectionResponse;
import com.voting.blockvote.model.Election;
import com.voting.blockvote.repository.ElectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ElectionServiceTest {

    @InjectMocks
    private ElectionService electionService;

    @Mock
    private ElectionRepository electionRepository;

    private CreateElectionRequest request;
    private Election election;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        //Common Test Data
        startTime=LocalDateTime.of(2025, 7, 10, 10, 0);
        endTime=LocalDateTime.of(2025, 7, 10, 12, 0);
        request = CreateElectionRequest.builder()
                .title("Test Election")
                .description("A test election")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        election = Election.builder()
                .id(1L)
                .title("Test Election")
                .description("A test election")
                .startTime(startTime)
                .endTime(endTime)
                .isEnded(false)
                .build();
    }

    @Test
    void createElection_ValidRequest_ReturnsElectionResponse() {
        //1st Arrange (When this should happen)
        when(electionRepository.save(any(Election.class))).thenReturn(election);

        //2nd Act (What to do)
        ElectionResponse electionResponse = electionService.createElection(request);

        //3rd Assert (Verify expected outcomes)
        assertNotNull(electionResponse);
        assertEquals(1L,electionResponse.getId());
        assertEquals("Test Election",electionResponse.getTitle());
        assertEquals("A test election",electionResponse.getDescription());
        assertEquals(startTime,electionResponse.getStartTime());
        assertEquals(endTime,electionResponse.getEndTime());
        assertFalse(electionResponse.getIsEnded());
        verify(electionRepository,times(1)).save(any(Election.class));
    }

    @Test
    void createElection_EndTimeBeforeStartTime_ThrowsIllegalArgumentException() {
        //Arrange
        CreateElectionRequest invalidRequest = CreateElectionRequest.builder()
                .title("Test Election")
                .description("A test election")
                .startTime(LocalDateTime.of(2025, 7, 10, 12, 0))
                .endTime(LocalDateTime.of(2025, 7, 10, 10,0))
                .build();

        //Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()->{
            electionService.createElection(invalidRequest);
                });
        assertEquals("End time must be before start time",exception.getMessage());
        verify(electionRepository,never()).save(any(Election.class));

    }

    @Test
    void createElection_NullRequest_ThrowsIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            electionService.createElection(null);
        });
        assertEquals("Request cannot be null", exception.getMessage());
        verify(electionRepository, never()).save(any(Election.class));
    }

    @Test
    void createElection_EmptyTitle_ThrowsIllegalArgumentException() {
        CreateElectionRequest invalidRequest = CreateElectionRequest.builder()
                .title("")
                .description("A test election")
                .startTime(startTime)
                .endTime(endTime)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            electionService.createElection(invalidRequest);
        });
        assertEquals("Title cannot be empty", exception.getMessage());
        verify(electionRepository, never()).save(any(Election.class));
    }

    @Test
    void getAllElections_ReturnsListOfElectionResponses() {
        //Arrange
        Election anotherElection = Election.builder()
                .id(2L)
                .title("Another Election")
                .description("Another test election")
                .startTime(startTime)
                .endTime(endTime)
                .isEnded(true)
                .build();
        when(electionRepository.findAll()).thenReturn(List.of(election,anotherElection));

        //Act
        List<ElectionResponse> electionResponses = electionService.getAllElections();

        //Assert
        assertNotNull(electionResponses);
        assertEquals(2,electionResponses.size());

        ElectionResponse firstResponse = electionResponses.getFirst();
        assertEquals(1L,firstResponse.getId());
        assertEquals("Test Election",firstResponse.getTitle());
        assertFalse(firstResponse.getIsEnded());

        ElectionResponse secondResponse = electionResponses.get(1);
        assertEquals(2L,secondResponse.getId());
        assertEquals("Another Election",secondResponse.getTitle());
        assertTrue(secondResponse.getIsEnded());

        verify(electionRepository,times(1)).findAll();
    }

    @Test
    void getAllElections_NoElections_ReturnsEmptyList() {
        //Arrange
        when(electionRepository.findAll()).thenReturn(List.of());

        //Act
        List<ElectionResponse> electionResponses = electionService.getAllElections();

        //Assert
        assertNotNull(electionResponses);
        assertEquals(0, electionResponses.size());
        verify(electionRepository,times(1)).findAll();
    }

    @Test
    void getAllElections_SingleElection_ReturnsSingleElectionResponse() {
        // Arrange
        when(electionRepository.findAll()).thenReturn(List.of(election));

        // Act
        List<ElectionResponse> electionResponses = electionService.getAllElections();

        // Assert
        assertNotNull(electionResponses);
        assertEquals(1, electionResponses.size());
        ElectionResponse response = electionResponses.getFirst();
        assertEquals(1L, response.getId());
        assertEquals("Test Election", response.getTitle());
        assertFalse(response.getIsEnded());
        verify(electionRepository, times(1)).findAll();
    }
}