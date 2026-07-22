package com.tommy.learning.presentation.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tommy.common.response.ApiResponse;
import com.tommy.common.security.RequireRole;
import com.tommy.learning.domain.entity.Question;
import com.tommy.learning.domain.entity.QuestionBank;
import com.tommy.learning.domain.entity.json.QuestionMetadata;
import com.tommy.learning.domain.entity.json.Option;
import com.tommy.learning.domain.entity.flashcard.Flashcard;
import com.tommy.learning.domain.entity.flashcard.FlashcardSet;
import com.tommy.learning.domain.entity.flashcard.FlashcardSetCard;
import com.tommy.learning.domain.entity.vocabulary.Vocabulary;
import com.tommy.learning.domain.enums.CefrLevel;
import com.tommy.learning.domain.enums.SourceEnum;
import com.tommy.learning.domain.enums.VisibilityEnum;
import com.tommy.learning.domain.enums.QuestionDifficulty;
import com.tommy.learning.domain.enums.QuestionType;
import com.tommy.learning.domain.enums.LearningState;
import com.tommy.learning.infrastructure.persistence.repository.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/learning/ai")
@RequiredArgsConstructor
@Slf4j
public class AIGenerationController {

    private final QuestionBankRepository questionBankRepository;
    private final QuestionRepository questionRepository;
    private final FlashcardSetRepository flashcardSetRepository;
    private final FlashcardRepository flashcardRepository;
    private final VocabularyRepository vocabularyRepository;
    private final FlashcardSetCardRepository flashcardSetCardRepository;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String aiServiceUrl = "http://127.0.0.1:7777/api/ai";

    // DTO cho yêu cầu tạo câu hỏi trắc nghiệm
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateQuizRequest {
        private UUID courseId;
        private UUID bankId;
        private String lessonTitle;
        private String lessonContent;
        private String questionType;
        private int numQuestions = 5;
    }

    // DTO phản hồi khi tạo câu hỏi trắc nghiệm thành công
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateQuizResponse {
        private List<Question> questions;
    }

    // DTO cho yêu cầu tạo flashcard
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateFlashcardRequest {
        private UUID courseId;
        private String lessonTitle;
        private String lessonContent;
        private int numFlashcards = 5;
    }

    // DTO phản hồi khi tạo flashcard thành công
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateFlashcardResponse {
        private FlashcardSet flashcardSet;
    }

    // FastAPI Response DTOs
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiOptionDto {
        private String id;
        private String text;
        @JsonProperty("is_correct")
        private boolean correct;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiQuestionDto {
        @JsonProperty("question_text")
        private String questionText;
        private List<FastApiOptionDto> options;
        private String explanation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiQuizResponse {
        private List<FastApiQuestionDto> questions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiFlashcardDto {
        private String front;
        private String back;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FastApiFlashcardResponse {
        private List<FastApiFlashcardDto> flashcards;
    }

    @PostMapping("/generate-quiz")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<GenerateQuizResponse>> generateQuiz(
            @RequestHeader("X-User-Id") UUID instructorId,
            @RequestBody GenerateQuizRequest request) {

        log.info("Instructor {} requested AI Quiz Generation for course: {}", instructorId, request.getCourseId());

        // 1. Tìm hoặc tạo QuestionBank của khóa học
        QuestionBank bank;
        if (request.getBankId() != null) {
            bank = questionBankRepository.findByIdAndIsDeletedFalse(request.getBankId())
                    .orElseThrow(() -> new IllegalArgumentException("Ngân hàng câu hỏi không tồn tại."));
        } else {
            String bankTitle = "Ngân hàng AI - " + (request.getLessonTitle() != null ? request.getLessonTitle() : "Bài học mới");
            QuestionBank newBank = QuestionBank.builder()
                    .courseId(request.getCourseId())
                    .title(bankTitle)
                    .instructorId(instructorId)
                    .isDeleted(false)
                    .build();
            bank = questionBankRepository.save(newBank);
        }

        // 2. Lấy danh sách câu hỏi đã tồn tại để tránh trùng lặp
        List<Question> existing = questionRepository.findByBankIdAndIsDeletedFalse(bank.getId());
        List<String> existingQuestions = existing.stream().map(Question::getContent).toList();

        // 3. Gửi yêu cầu sang FastAPI
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", request.getLessonContent());
        payload.put("num_questions", request.getNumQuestions());
        payload.put("question_type", request.getQuestionType() != null ? request.getQuestionType() : "SINGLE_CHOICE");
        payload.put("existing_questions", existingQuestions);

        try {
            FastApiQuizResponse aiResponse = restTemplate.postForObject(
                    aiServiceUrl + "/quiz/generate",
                    payload,
                    FastApiQuizResponse.class
            );

            if (aiResponse == null || aiResponse.getQuestions() == null) {
                return ResponseEntity.status(500).body(ApiResponse.error(500, "Không nhận được phản hồi hợp lệ từ AI."));
            }

            List<Question> savedQuestions = new ArrayList<>();

            // Xác định loại câu hỏi lưu trong DB
            QuestionType dbType = QuestionType.SINGLE_CHOICE;
            if ("MULTIPLE_CHOICE".equalsIgnoreCase(request.getQuestionType())) {
                dbType = QuestionType.MULTIPLE_CHOICE;
            } else if ("ESSAY".equalsIgnoreCase(request.getQuestionType())) {
                dbType = QuestionType.ESSAY;
            }

            // 4. Lưu các câu hỏi vào Question Bank
            for (FastApiQuestionDto aiQuestion : aiResponse.getQuestions()) {
                List<Option> options = new ArrayList<>();
                if (aiQuestion.getOptions() != null) {
                    for (FastApiOptionDto aiOption : aiQuestion.getOptions()) {
                        options.add(Option.builder()
                                .id(aiOption.getId())
                                .text(aiOption.getText())
                                .isCorrect(aiOption.isCorrect())
                                .build());
                    }
                }

                QuestionMetadata metadata = QuestionMetadata.builder()
                        .options(options)
                        .explanation(aiQuestion.getExplanation())
                        .build();

                Question question = Question.builder()
                        .bankId(bank.getId())
                        .type(dbType)
                        .content(aiQuestion.getQuestionText())
                        .metadata(metadata)
                        .difficulty(QuestionDifficulty.MEDIUM)
                        .points(1.0)
                        .isDeleted(false)
                        .build();

                savedQuestions.add(questionRepository.save(question));
            }

            GenerateQuizResponse response = new GenerateQuizResponse();
            response.setQuestions(savedQuestions);

            return ResponseEntity.ok(ApiResponse.success(200, "Tự động tạo câu hỏi trắc nghiệm vào ngân hàng thành công!", response));

        } catch (Exception e) {
            log.error("Lỗi khi kết nối sang AI Service để tạo quiz", e);
            return ResponseEntity.status(500).body(ApiResponse.error(500, "Lỗi kết nối AI Service: " + e.getMessage()));
        }
    }

    @PostMapping("/generate-flashcards")
    @RequireRole({"INSTRUCTOR", "TEACHER"})
    public ResponseEntity<ApiResponse<GenerateFlashcardResponse>> generateFlashcards(
            @RequestHeader("X-User-Id") UUID instructorId,
            @RequestBody GenerateFlashcardRequest request) {

        log.info("Instructor {} requested AI Flashcard Generation for course: {}", instructorId, request.getCourseId());

        // 1. Gửi yêu cầu sang FastAPI
        Map<String, Object> payload = new HashMap<>();
        payload.put("text", request.getLessonContent());
        payload.put("num_flashcards", request.getNumFlashcards());

        try {
            FastApiFlashcardResponse aiResponse = restTemplate.postForObject(
                    aiServiceUrl + "/flashcard/generate",
                    payload,
                    FastApiFlashcardResponse.class
            );

            if (aiResponse == null || aiResponse.getFlashcards() == null) {
                return ResponseEntity.status(500).body(ApiResponse.error(500, "Không nhận được phản hồi hợp lệ từ AI."));
            }

            // 2. Tạo một FlashcardSet mới của khóa học ở trạng thái PRIVATE để duyệt
            String title = "Thẻ ghi nhớ: " + (request.getLessonTitle() != null ? request.getLessonTitle() : "Bài học mới");
            FlashcardSet flashcardSet = FlashcardSet.builder()
                    .title(title)
                    .courseId(request.getCourseId())
                    .instructorId(instructorId)
                    .visibility(VisibilityEnum.PRIVATE)
                    .setCards(new ArrayList<>())
                    .build();

            flashcardSet = flashcardSetRepository.save(flashcardSet);

            // 3. Lưu từng Flashcard & Vocabulary
            for (FastApiFlashcardDto aiCard : aiResponse.getFlashcards()) {
                Vocabulary vocab = Vocabulary.builder()
                        .lemma(aiCard.getFront())
                        .language("en")
                        .phonetic("")
                        .partOfSpeech("Noun")
                        .cefrLevel(CefrLevel.B1)
                        .source(SourceEnum.SYSTEM)
                        .visibility(VisibilityEnum.PUBLIC)
                        .meanings(new ArrayList<>())
                        .examples(new ArrayList<>())
                        .images(new ArrayList<>())
                        .audios(new ArrayList<>())
                        .build();

                vocab = vocabularyRepository.save(vocab);

                Flashcard card = Flashcard.builder()
                        .vocabulary(vocab)
                        .frontType(com.tommy.learning.domain.enums.FrontType.WORD)
                        .backType(com.tommy.learning.domain.enums.BackType.MEANING)
                        .createdBy(instructorId)
                        .build();

                card = flashcardRepository.save(card);

                FlashcardSetCard setCard = FlashcardSetCard.builder()
                        .flashcardSet(flashcardSet)
                        .flashcard(card)
                        .build();

                flashcardSetCardRepository.save(setCard);
            }

            GenerateFlashcardResponse response = new GenerateFlashcardResponse();
            response.setFlashcardSet(flashcardSet);

            return ResponseEntity.ok(ApiResponse.success(200, "Tự động tạo bộ thẻ Flashcards thành công!", response));

        } catch (Exception e) {
            log.error("Lỗi khi kết nối sang AI Service để tạo flashcards", e);
            return ResponseEntity.status(500).body(ApiResponse.error(500, "Lỗi kết nối AI Service: " + e.getMessage()));
        }
    }
}
