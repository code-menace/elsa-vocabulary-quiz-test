package com.elsa.vocabulary_quiz.controller;

import com.elsa.vocabulary_quiz.model.AnswerSubmission;
import com.elsa.vocabulary_quiz.model.ErrorMessage;
import com.elsa.vocabulary_quiz.service.QuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class QuizWebSocketController {

    Logger logger = LoggerFactory.getLogger(QuizWebSocketController.class);

    private final SimpMessageSendingOperations messagingTemplate;
    private final QuizService quizService;

    public QuizWebSocketController(SimpMessageSendingOperations messagingTemplate, QuizService quizService) {
        this.messagingTemplate = messagingTemplate;
        this.quizService = quizService;
    }

    /**
     * Handles incoming quiz answer submissions from users. This method listens for messages sent
     * to the WebSocket endpoint mapped to "/submit-answer/{roomId}". Upon receiving an answer submission,
     * it processes the answer, updates the leaderboard, and broadcasts the updated leaderboard
     * to all clients in the specified quiz room.
     *
     * If an error occurs during processing (e.g., invalid user ID or missing data), an error message
     * is logged and sent back to the client who submitted the answer.
     *
     * @param roomId The ID of the quiz room where the answer was submitted.
     * @param submission The answer submission containing details such as user ID, question ID, and the answer.
     */
    @MessageMapping("/submit-answer/{roomId}")
    public void handleAnswer(@DestinationVariable String roomId, AnswerSubmission submission) {
        logger.info("Received answer from userId: " + submission.getUserId() + " in room: " + roomId);

        try {
            Map<String, Long> leaderboard = quizService.processAnswer(roomId, submission);

            messagingTemplate.convertAndSend("/topic/scores/" + roomId, leaderboard);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            sendErrorMessage(roomId, submission.getUserId(), e.getMessage());
        }
    }

    private void sendErrorMessage(String roomId, Long userId, String errorMessage) {
        String destination = "/topic/errors/" + roomId;
        messagingTemplate.convertAndSend(destination, new ErrorMessage(userId, errorMessage));
    }
}
