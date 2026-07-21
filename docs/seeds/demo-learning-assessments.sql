BEGIN;

CREATE TEMP TABLE seed_courses (
  course_id uuid PRIMARY KEY,
  slug text NOT NULL,
  instructor_id uuid NOT NULL,
  cefr_level text NOT NULL
) ON COMMIT DROP;

INSERT INTO seed_courses (course_id, slug, instructor_id, cefr_level) VALUES
  ('11000000-0000-0000-0000-000000000001', 'english-foundations-a1', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'A1'),
  ('11000000-0000-0000-0000-000000000002', 'everyday-english-a2', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'A2'),
  ('11000000-0000-0000-0000-000000000003', 'intermediate-english-b1', 'b0e6d25f-8ec3-45fa-968d-84025b443ff6', 'B1'),
  ('11000000-0000-0000-0000-000000000004', 'upper-intermediate-english-b2', 'b0e6d25f-8ec3-45fa-968d-84025b443ff6', 'B2'),
  ('11000000-0000-0000-0000-000000000005', 'advanced-english-c1', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'C1'),
  ('11000000-0000-0000-0000-000000000006', 'ielts-65-complete', 'b0e6d25f-8ec3-45fa-968d-84025b443ff6', 'B2'),
  ('11000000-0000-0000-0000-000000000007', 'ielts-writing-intensive', 'b0e6d25f-8ec3-45fa-968d-84025b443ff6', 'B2'),
  ('11000000-0000-0000-0000-000000000008', 'toeic-750-strategy', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B2'),
  ('11000000-0000-0000-0000-000000000009', 'business-english-essentials', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B1'),
  ('11000000-0000-0000-0000-000000000010', 'english-pronunciation-mastery', 'b0e6d25f-8ec3-45fa-968d-84025b443ff6', 'A2'),
  ('11000000-0000-0000-0000-000000000011', 'english-for-travel', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'A1'),
  ('13bdde3d-8c35-4076-ae05-47e80baa3c7d', 'fdsafas', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B1'),
  ('1bfb126d-74b6-4ddc-86ef-6aa9c99c61f5', 'jfkasfdsa', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B1'),
  ('28810c66-d18d-4367-bac1-c91d9fb1571b', 'fdsaf', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B1'),
  ('60f738fa-73fd-4e9f-9c3d-173f5b6685d4', 'hjkgkj', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B1'),
  ('643c50fc-c6c7-4e2b-aed5-5649df51abfc', 'ab-test-1', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B1'),
  ('a660df14-4271-40d7-8cae-3af44853a834', 'dffsda', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B1'),
  ('dad0201b-c1b4-4a4e-832f-4fc2b40823ea', 'fdsagadsgas', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B1'),
  ('dcb3cd1d-9b2e-4b83-8104-643a56025646', 'ab-test', 'b0e6d25f-8ec3-45fa-968d-84025b443ff6', 'B1'),
  ('faa1f9b5-e038-4257-82ad-3c3b6873e0f5', 'hidsfasjkfhashlksfda', '4803a64b-41cd-409c-b1c5-8db0c589df53', 'B1');

INSERT INTO vocabulary (id, lemma, language, phonetic, part_of_speech, cefr_level, source, visibility, version, created_at, updated_at)
SELECT md5(c.course_id::text || ':seed-vocabulary:' || n)::uuid,
       (ARRAY['foundation', 'practice', 'fluency', 'comprehension', 'achievement'])[n],
       'English',
       (ARRAY['/faʊnˈdeɪʃən/', '/ˈpræktɪs/', '/ˈfluːənsi/', '/ˌkɒmprɪˈhenʃən/', '/əˈtʃiːvmənt/'])[n],
       (ARRAY['noun', 'noun', 'noun', 'noun', 'noun'])[n],
       c.cefr_level, 'SYSTEM', 'COURSE_ONLY', 0, NOW(), NOW()
FROM seed_courses c CROSS JOIN generate_series(1, 5) n
ON CONFLICT (id) DO UPDATE SET cefr_level = EXCLUDED.cefr_level, updated_at = NOW();

INSERT INTO vocabulary_meaning (id, vocabulary_id, definition, note)
SELECT md5(c.course_id::text || ':seed-vocabulary-meaning:' || n)::uuid,
       md5(c.course_id::text || ':seed-vocabulary:' || n)::uuid,
       (ARRAY[
         'Nền tảng hoặc cơ sở quan trọng để phát triển một kỹ năng.',
         'Sự luyện tập thường xuyên nhằm cải thiện năng lực.',
         'Khả năng sử dụng ngôn ngữ trôi chảy và tự nhiên.',
         'Khả năng hiểu thông tin được nghe hoặc đọc.',
         'Thành tựu đạt được sau quá trình học tập và nỗ lực.'
       ])[n],
       'Từ vựng mẫu cho khóa ' || c.slug
FROM seed_courses c CROSS JOIN generate_series(1, 5) n
ON CONFLICT (id) DO UPDATE SET definition = EXCLUDED.definition, note = EXCLUDED.note;

INSERT INTO vocabulary_example (id, vocabulary_id, sentence, translation)
SELECT md5(c.course_id::text || ':seed-vocabulary-example:' || n)::uuid,
       md5(c.course_id::text || ':seed-vocabulary:' || n)::uuid,
       (ARRAY[
         'A strong foundation makes language learning easier.',
         'Daily practice helps you remember new vocabulary.',
         'She speaks English with confidence and fluency.',
         'Reading every day improves comprehension.',
         'Completing the course is a meaningful achievement.'
       ])[n],
       (ARRAY[
         'Nền tảng vững chắc giúp việc học ngôn ngữ dễ dàng hơn.',
         'Luyện tập hằng ngày giúp bạn nhớ từ mới.',
         'Cô ấy nói tiếng Anh tự tin và trôi chảy.',
         'Đọc mỗi ngày cải thiện khả năng hiểu.',
         'Hoàn thành khóa học là một thành tựu ý nghĩa.'
       ])[n]
FROM seed_courses c CROSS JOIN generate_series(1, 5) n
ON CONFLICT (id) DO UPDATE SET sentence = EXCLUDED.sentence, translation = EXCLUDED.translation;

INSERT INTO vocabulary_image (id, vocabulary_id, image_url)
SELECT md5(c.course_id::text || ':seed-vocabulary-image:' || n)::uuid,
       md5(c.course_id::text || ':seed-vocabulary:' || n)::uuid,
       (ARRAY[
         'https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=800',
         'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800',
         'https://images.unsplash.com/photo-1523240795612-9a054b0db644?w=800',
         'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=800',
         'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=800'
       ])[n]
FROM seed_courses c CROSS JOIN generate_series(1, 5) n
ON CONFLICT (id) DO UPDATE SET image_url = EXCLUDED.image_url;

INSERT INTO flashcard_set (id, title, course_id, instructor_id, visibility, created_at, updated_at)
SELECT md5(c.course_id::text || ':seed-flashcard-set')::uuid,
       'Từ vựng trọng tâm - ' || c.slug, c.course_id, c.instructor_id, 'COURSE_ONLY', NOW(), NOW()
FROM seed_courses c
ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, instructor_id = EXCLUDED.instructor_id,
  visibility = EXCLUDED.visibility, updated_at = NOW();

INSERT INTO flashcard (id, vocabulary_id, front_type, back_type, created_by, created_at, updated_at)
SELECT md5(c.course_id::text || ':seed-flashcard:' || n)::uuid,
       md5(c.course_id::text || ':seed-vocabulary:' || n)::uuid,
       CASE WHEN n = 5 THEN 'IMAGE' ELSE 'WORD' END, CASE WHEN n = 4 THEN 'EXAMPLE' ELSE 'MEANING' END,
       c.instructor_id, NOW(), NOW()
FROM seed_courses c CROSS JOIN generate_series(1, 5) n
ON CONFLICT (id) DO UPDATE SET front_type = EXCLUDED.front_type, back_type = EXCLUDED.back_type, updated_at = NOW();

INSERT INTO flashcard_set_card (id, set_id, flashcard_id, display_order)
SELECT md5(c.course_id::text || ':seed-flashcard-set-card:' || n)::uuid,
       md5(c.course_id::text || ':seed-flashcard-set')::uuid,
       md5(c.course_id::text || ':seed-flashcard:' || n)::uuid, n
FROM seed_courses c CROSS JOIN generate_series(1, 5) n
ON CONFLICT (id) DO UPDATE SET display_order = EXCLUDED.display_order;

INSERT INTO question_banks (id, course_id, instructor_id, title, description, is_deleted, created_at, updated_at)
SELECT md5(c.course_id::text || ':seed-question-bank:' || n)::uuid,
       c.course_id, c.instructor_id, 'Ngân hàng câu hỏi ' || n || ' - ' || c.slug,
       CASE n WHEN 1 THEN 'Từ vựng' WHEN 2 THEN 'Ngữ pháp' WHEN 3 THEN 'Đọc hiểu' WHEN 4 THEN 'Giao tiếp' ELSE 'Tổng hợp' END,
       false, NOW(), NOW()
FROM seed_courses c CROSS JOIN generate_series(1, 5) n
ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, description = EXCLUDED.description,
  instructor_id = EXCLUDED.instructor_id, is_deleted = false, updated_at = NOW();

INSERT INTO questions (id, bank_id, question_type, content, metadata, difficulty, points, is_deleted, created_at, updated_at)
SELECT md5(c.course_id::text || ':seed-question:' || n)::uuid,
       md5(c.course_id::text || ':seed-question-bank:' || n)::uuid,
       'SINGLE_CHOICE',
       (ARRAY[
         'Which word means a strong base for future learning?',
         'Choose the correct sentence in the present simple tense.',
         'What is the main purpose of reading for comprehension?',
         'Which response is most appropriate in a formal conversation?',
         'Which study habit is the most effective for long-term progress?'
       ])[n],
       jsonb_build_object(
         'options', jsonb_build_array(
           jsonb_build_object('id', 'A', 'text', (ARRAY['foundation', 'She studies English every day.', 'To understand the main ideas and details', 'Thank you for your assistance.', 'Practising consistently'])[n], 'correct', true),
           jsonb_build_object('id', 'B', 'text', (ARRAY['temporary', 'She study English every day.', 'To copy every sentence', 'Hey, whatever.', 'Studying only before an exam'])[n], 'correct', false),
           jsonb_build_object('id', 'C', 'text', (ARRAY['confusion', 'She studying English every day.', 'To skip unfamiliar words', 'I do not care.', 'Avoiding feedback'])[n], 'correct', false),
           jsonb_build_object('id', 'D', 'text', (ARRAY['hesitation', 'She is study English every day.', 'To memorize page numbers', 'That is not my problem.', 'Never reviewing vocabulary'])[n], 'correct', false)
         ),
         'explanation', 'Phương án A là đáp án phù hợp nhất.'
       ),
       CASE WHEN n <= 2 THEN 'EASY' WHEN n <= 4 THEN 'MEDIUM' ELSE 'HARD' END,
       CASE WHEN n = 5 THEN 2.0 ELSE 1.0 END, false, NOW(), NOW()
FROM seed_courses c CROSS JOIN generate_series(1, 5) n
ON CONFLICT (id) DO UPDATE SET content = EXCLUDED.content, metadata = EXCLUDED.metadata,
  difficulty = EXCLUDED.difficulty, points = EXCLUDED.points, is_deleted = false, updated_at = NOW();

INSERT INTO exams (id, course_id, instructor_id, title, duration_minutes, passing_score, max_attempts, status, is_deleted, created_at, updated_at)
SELECT md5(c.course_id::text || ':seed-exam:' || n)::uuid,
       c.course_id, c.instructor_id,
       (ARRAY['Kiểm tra đầu vào', 'Kiểm tra từ vựng', 'Kiểm tra giữa khóa', 'Bài luyện tập tổng hợp', 'Kiểm tra cuối khóa'])[n] || ' - ' || c.slug,
       10 + n * 5, 60.0 + n * 2, 3, 'PUBLISHED', false, NOW(), NOW()
FROM seed_courses c CROSS JOIN generate_series(1, 5) n
ON CONFLICT (id) DO UPDATE SET title = EXCLUDED.title, duration_minutes = EXCLUDED.duration_minutes,
  passing_score = EXCLUDED.passing_score, max_attempts = EXCLUDED.max_attempts,
  status = 'PUBLISHED', is_deleted = false, updated_at = NOW();

INSERT INTO exam_questions (id, exam_id, question_id, display_order)
SELECT md5(c.course_id::text || ':seed-exam-question:' || exam_no || ':' || question_no)::uuid,
       md5(c.course_id::text || ':seed-exam:' || exam_no)::uuid,
       md5(c.course_id::text || ':seed-question:' || question_no)::uuid,
       question_no
FROM seed_courses c CROSS JOIN generate_series(1, 5) exam_no CROSS JOIN generate_series(1, 5) question_no
ON CONFLICT (id) DO UPDATE SET display_order = EXCLUDED.display_order;

COMMIT;
