package com.hiresmarter.jobportal.service;



import com.hiresmarter.jobportal.dto.ai.AIResult;
import com.hiresmarter.jobportal.entity.Resume;
import com.hiresmarter.jobportal.entity.User;
import com.hiresmarter.jobportal.exception.BadRequestException;
import com.hiresmarter.jobportal.repository.ResumeRepository;
import com.hiresmarter.jobportal.repository.UserRepository;
import com.hiresmarter.jobportal.service.ai.AIService;
import com.hiresmarter.jobportal.service.resume.ResumeServiceImpl;
import com.hiresmarter.jobportal.util.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeServiceTest {

    @Mock
    private ResumeRepository resumeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AIService aiService;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private User testUser;
    private Resume testResume;
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testResume = new Resume();
        testResume.setId(10L);
        testResume.setExtractedText("Java Developer with 5 years experience");

        mockFile = new MockMultipartFile("file", "resume.pdf", "application/pdf", "dummy content".getBytes());
    }

    @Test
    void uploadResume_Success() {
        // We use MockedStatic because FileUtil has static methods
        try (MockedStatic<FileUtil> fileUtil = mockStatic(FileUtil.class)) {
            // Arrange
            fileUtil.when(() -> FileUtil.isValidPdf(any())).thenReturn(true);
            fileUtil.when(() -> FileUtil.saveFile(any())).thenReturn("/path/to/resume.pdf");
            fileUtil.when(() -> FileUtil.extractText(any())).thenReturn("extracted text");

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(resumeRepository.findByUser(testUser)).thenReturn(List.of(new Resume()));
            when(resumeRepository.save(any(Resume.class))).thenReturn(testResume);

            // Act
            Resume result = resumeService.uploadResume(1L, mockFile);

            // Assert
            assertNotNull(result);
            verify(resumeRepository, atLeastOnce()).findByUser(testUser);
            verify(resumeRepository).save(any(Resume.class));
        }
    }

    @Test
    void uploadResume_InvalidFile_ThrowsException() {
        try (MockedStatic<FileUtil> fileUtil = mockStatic(FileUtil.class)) {
            // Arrange
            fileUtil.when(() -> FileUtil.isValidPdf(any())).thenReturn(false);

            // Act & Assert
            assertThrows(BadRequestException.class, () -> resumeService.uploadResume(1L, mockFile));
            verify(resumeRepository, never()).save(any());
        }
    }

    @Test
    void analyzeResumeForJob_Success() {
        // Arrange
        AIResult mockAiResult = AIResult.builder()
                .score(85.0)
                .feedback("Great profile")
                .matchedSkills("Java, Spring")
                .build();

        when(resumeRepository.findById(10L)).thenReturn(Optional.of(testResume));
        when(aiService.analyzeResume(anyString(), anyString())).thenReturn(mockAiResult);
        when(resumeRepository.save(any(Resume.class))).thenReturn(testResume);

        // Act
        Resume result = resumeService.analyzeResumeForJob(10L, "Java Job Description");

        // Assert
        assertEquals(85.0, result.getScore());
        assertEquals("Great profile", result.getFeedback());
        verify(aiService).analyzeResume(eq(testResume.getExtractedText()), anyString());
    }
}