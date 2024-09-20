package com.elsa.vocabulary_quiz.service;

import com.elsa.vocabulary_quiz.model.AnswerSubmission;
import com.elsa.vocabulary_quiz.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizService quizService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize the mock behavior for the user mappings
        when(quizRepository.getUsernameFromUserId(1L)).thenReturn("John");
        when(quizRepository.getUsernameFromUserId(2L)).thenReturn("Jane");
    }

    @Test
    void shouldProcessCorrectAnswer() {
        AnswerSubmission submission = new AnswerSubmission();
        submission.setUserId(1L);
        submission.setQuestionId(1L);
        submission.setAnswer("Hanoi");

        Map<Long, Long> leaderboard = new LinkedHashMap<>();
        leaderboard.put(1L, 10L);  // John has 10 points
        leaderboard.put(2L, 5L);   // Jane has 5 points

        when(quizRepository.getLeaderboard("quiz-room-1")).thenReturn(leaderboard);

        Map<String, Long> result = quizService.processAnswer("quiz-room-1", submission);

        assertEquals(2, result.size());
        assertEquals(10L, result.get("John"));
        assertEquals(5L, result.get("Jane"));

        verify(quizRepository, times(1)).incrementUserScore("quiz-room-1", 1L, 1);
    }

    @Test
    void shouldProcessIncorrectAnswer() {
        AnswerSubmission submission = new AnswerSubmission();
        submission.setUserId(1L);
        submission.setQuestionId(1L);
        submission.setAnswer("Ho Chi Minh City");

        QuizService spyQuizService = spy(quizService);
        doReturn(false).when(spyQuizService).checkAnswer(submission);

        Map<Long, Long> leaderboard = new LinkedHashMap<>();
        leaderboard.put(1L, 10L);  // John has 10 points
        leaderboard.put(2L, 5L);   // Jane has 5 points

        when(quizRepository.getLeaderboard("quiz-room-1")).thenReturn(leaderboard);

        Map<String, Long> result = spyQuizService.processAnswer("quiz-room-1", submission);

        assertEquals(2, result.size());
        assertEquals(10L, result.get("John"));  // No score change for incorrect answer
        assertEquals(5L, result.get("Jane"));

        verify(quizRepository, never()).incrementUserScore(anyString(), anyLong(), anyInt());
    }

    @Test
    void shouldFailWhenMissingUserId() {
        AnswerSubmission submission = new AnswerSubmission();
        submission.setUserId(null);  // Missing userId
        submission.setQuestionId(1L);
        submission.setAnswer("Hanoi");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            quizService.processAnswer("quiz-room-1", submission);
        });

        assertEquals("Missing User ID", exception.getMessage());
    }

    @Test
    void shouldFailWhenMissingQuestionId() {
        AnswerSubmission submission = new AnswerSubmission();
        submission.setUserId(1L);
        submission.setQuestionId(null);  // Missing questionId
        submission.setAnswer("Hanoi");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            quizService.processAnswer("quiz-room-1", submission);
        });

        assertEquals("Missing Question ID", exception.getMessage());
    }

    @Test
    void shouldFailWhenMissingAnswer() {
        AnswerSubmission submission = new AnswerSubmission();
        submission.setUserId(1L);
        submission.setQuestionId(1L);
        submission.setAnswer(null);  // Missing answer

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            quizService.processAnswer("quiz-room-1", submission);
        });

        assertEquals("Missing answer", exception.getMessage());
    }

    @Test
    void shouldFailWhenMissingUserInCache() {
        AnswerSubmission submission = new AnswerSubmission();
        submission.setUserId(99L);  // Non-existent userId
        submission.setQuestionId(1L);
        submission.setAnswer("Hanoi");

        when(quizRepository.getUsernameFromUserId(99L)).thenReturn(null);  // Simulate missing user

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            quizService.processAnswer("quiz-room-1", submission);
        });

        assertEquals("User ID not found in cache: 99", exception.getMessage());
    }
}
