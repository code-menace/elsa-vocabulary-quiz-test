package com.elsa.vocabulary_quiz.service;

import com.elsa.vocabulary_quiz.model.AnswerSubmission;
import com.elsa.vocabulary_quiz.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;

        // Dummy data
        quizRepository.addUserIdToUsernameMapping("1", "John");
        quizRepository.addUserIdToUsernameMapping("2", "Jane");
    }

    /**
     * Process answer submission, resolve userId to username, increment the user's score, and update leaderboard.
     *
     * @param roomId The room (quiz session) ID.
     * @param submission The answer submission.
     * @return Updated leaderboard for the room.
     */
    public Map<String, Long> processAnswer(String roomId, AnswerSubmission submission) {
        if (submission.getUserId() == null) {
            throw new IllegalArgumentException("Missing User ID");
        }

        if (submission.getQuestionId() == null) {
            throw new IllegalArgumentException("Missing Question ID");
        }

        if (submission.getAnswer() == null) {
            throw new IllegalArgumentException("Missing answer");
        }

        String username = quizRepository.getUsernameFromUserId(submission.getUserId());
        if (username == null) {
            throw new IllegalArgumentException("User ID not found in cache: " + submission.getUserId());
        }

        boolean isCorrect = checkAnswer(submission);
        if (isCorrect) {
            quizRepository.incrementUserScore(roomId, submission.getUserId(), 1);
        }

        Map<Long, Long> leaderboard = quizRepository.getLeaderboard(roomId);

        return leaderboard.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> quizRepository.getUsernameFromUserId(entry.getKey()),
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    boolean checkAnswer(AnswerSubmission submission) {
        // For demonstration, we'll assume all answers are correct
        // Actual logic would be more complex
        return true;
    }
}
