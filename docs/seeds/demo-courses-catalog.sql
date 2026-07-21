BEGIN;

INSERT INTO categories (id, name, slug) VALUES
  ('21000000-0000-0000-0000-000000000001', 'General English', 'general-english'),
  ('21000000-0000-0000-0000-000000000002', 'IELTS', 'ielts'),
  ('21000000-0000-0000-0000-000000000003', 'TOEIC', 'toeic'),
  ('21000000-0000-0000-0000-000000000004', 'Business English', 'business-english'),
  ('21000000-0000-0000-0000-000000000005', 'Communication', 'communication')
ON CONFLICT (slug) DO UPDATE SET name = EXCLUDED.name;

WITH seed(ord, id, instructor_id, title, slug, short_description, thumbnail_url, level, price, original_price) AS (
  VALUES
    (1, '11000000-0000-0000-0000-000000000001'::uuid, '4803a64b-41cd-409c-b1c5-8db0c589df53'::uuid, 'English Foundations A1', 'english-foundations-a1', 'Xây dựng nền tảng phát âm, từ vựng và mẫu câu tiếng Anh căn bản.', 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=1200&q=80', 'A1', 299000, 499000),
    (2, '11000000-0000-0000-0000-000000000002'::uuid, '4803a64b-41cd-409c-b1c5-8db0c589df53'::uuid, 'Everyday English A2', 'everyday-english-a2', 'Giao tiếp tự tin trong các tình huống hằng ngày với bài học thực hành.', 'https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=1200&q=80', 'A2', 399000, 599000),
    (3, '11000000-0000-0000-0000-000000000003'::uuid, 'b0e6d25f-8ec3-45fa-968d-84025b443ff6'::uuid, 'Intermediate English B1', 'intermediate-english-b1', 'Nâng cấp ngữ pháp, phản xạ nghe nói và kỹ năng viết ở trình độ B1.', 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?auto=format&fit=crop&w=1200&q=80', 'B1', 549000, 749000),
    (4, '11000000-0000-0000-0000-000000000004'::uuid, 'b0e6d25f-8ec3-45fa-968d-84025b443ff6'::uuid, 'Upper Intermediate English B2', 'upper-intermediate-english-b2', 'Làm chủ giao tiếp học thuật và thảo luận chuyên sâu ở trình độ B2.', 'https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&w=1200&q=80', 'B2', 699000, 899000),
    (5, '11000000-0000-0000-0000-000000000005'::uuid, '4803a64b-41cd-409c-b1c5-8db0c589df53'::uuid, 'Advanced English C1', 'advanced-english-c1', 'Diễn đạt tự nhiên, chính xác và thuyết phục trong môi trường quốc tế.', 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80', 'C1', 849000, 1099000),
    (6, '11000000-0000-0000-0000-000000000006'::uuid, 'b0e6d25f-8ec3-45fa-968d-84025b443ff6'::uuid, 'IELTS 6.5 Complete', 'ielts-65-complete', 'Lộ trình toàn diện bốn kỹ năng hướng tới IELTS Overall 6.5.', 'https://images.unsplash.com/photo-1457369804613-52c61a468e7d?auto=format&fit=crop&w=1200&q=80', 'IELTS', 999000, 1399000),
    (7, '11000000-0000-0000-0000-000000000007'::uuid, 'b0e6d25f-8ec3-45fa-968d-84025b443ff6'::uuid, 'IELTS Writing Intensive', 'ielts-writing-intensive', 'Phân tích đề, phát triển ý và sửa lỗi chi tiết cho Writing Task 1 và 2.', 'https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=1200&q=80', 'IELTS', 749000, 999000),
    (8, '11000000-0000-0000-0000-000000000008'::uuid, '4803a64b-41cd-409c-b1c5-8db0c589df53'::uuid, 'TOEIC 750 Strategy', 'toeic-750-strategy', 'Chiến thuật làm bài và luyện tập trọng tâm để đạt mục tiêu TOEIC 750+.', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=1200&q=80', 'TOEIC', 649000, 849000),
    (9, '11000000-0000-0000-0000-000000000009'::uuid, '4803a64b-41cd-409c-b1c5-8db0c589df53'::uuid, 'Business English Essentials', 'business-english-essentials', 'Email, họp, thuyết trình và đàm phán bằng tiếng Anh trong công việc.', 'https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=1200&q=80', 'B1', 599000, 799000),
    (10, '11000000-0000-0000-0000-000000000010'::uuid, 'b0e6d25f-8ec3-45fa-968d-84025b443ff6'::uuid, 'English Pronunciation Mastery', 'english-pronunciation-mastery', 'Luyện âm, trọng âm, nối âm và ngữ điệu để nói tiếng Anh rõ ràng hơn.', 'https://images.unsplash.com/photo-1475721027785-f74eccf877e2?auto=format&fit=crop&w=1200&q=80', 'A2', 449000, 649000),
    (11, '11000000-0000-0000-0000-000000000011'::uuid, '4803a64b-41cd-409c-b1c5-8db0c589df53'::uuid, 'English for Travel', 'english-for-travel', 'Mẫu câu và phản xạ giao tiếp cần thiết cho mọi chuyến đi nước ngoài.', 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=1200&q=80', 'A1', 0, 299000)
), slots AS (
  SELECT GREATEST(0, 20 - COUNT(*))::integer AS remaining FROM courses
), chosen AS (
  SELECT s.* FROM seed s
  WHERE NOT EXISTS (SELECT 1 FROM courses c WHERE c.slug = s.slug)
  ORDER BY s.ord
  LIMIT (SELECT remaining FROM slots)
)
INSERT INTO courses (id, instructor_id, title, slug, short_description, thumbnail_url, level, language, original_price, price, currency, status, avg_rating, total_reviews, total_students, created_at, updated_at)
SELECT id, instructor_id, title, slug, short_description, thumbnail_url, level, 'English', original_price, price, 'VND', 'PUBLISHED',
       (4.30 + ord * 0.05)::numeric(3,2), 8 + ord, 25 + ord * 7, NOW() - (12 - ord) * INTERVAL '5 days', NOW()
FROM chosen
ON CONFLICT (slug) DO NOTHING;

WITH seed(ord, id) AS (
  VALUES
    (1, '11000000-0000-0000-0000-000000000001'::uuid), (2, '11000000-0000-0000-0000-000000000002'::uuid),
    (3, '11000000-0000-0000-0000-000000000003'::uuid), (4, '11000000-0000-0000-0000-000000000004'::uuid),
    (5, '11000000-0000-0000-0000-000000000005'::uuid), (6, '11000000-0000-0000-0000-000000000006'::uuid),
    (7, '11000000-0000-0000-0000-000000000007'::uuid), (8, '11000000-0000-0000-0000-000000000008'::uuid),
    (9, '11000000-0000-0000-0000-000000000009'::uuid), (10, '11000000-0000-0000-0000-000000000010'::uuid),
    (11, '11000000-0000-0000-0000-000000000011'::uuid)
)
INSERT INTO course_categories (course_id, category_id)
SELECT s.id,
  CASE
    WHEN s.ord IN (6,7) THEN '21000000-0000-0000-0000-000000000002'::uuid
    WHEN s.ord = 8 THEN '21000000-0000-0000-0000-000000000003'::uuid
    WHEN s.ord = 9 THEN '21000000-0000-0000-0000-000000000004'::uuid
    WHEN s.ord IN (10,11) THEN '21000000-0000-0000-0000-000000000005'::uuid
    ELSE '21000000-0000-0000-0000-000000000001'::uuid
  END
FROM seed s JOIN courses c ON c.id = s.id
ON CONFLICT DO NOTHING;

WITH seed(id) AS (
  VALUES ('11000000-0000-0000-0000-000000000001'::uuid), ('11000000-0000-0000-0000-000000000002'::uuid),
         ('11000000-0000-0000-0000-000000000003'::uuid), ('11000000-0000-0000-0000-000000000004'::uuid),
         ('11000000-0000-0000-0000-000000000005'::uuid), ('11000000-0000-0000-0000-000000000006'::uuid),
         ('11000000-0000-0000-0000-000000000007'::uuid), ('11000000-0000-0000-0000-000000000008'::uuid),
         ('11000000-0000-0000-0000-000000000009'::uuid), ('11000000-0000-0000-0000-000000000010'::uuid),
         ('11000000-0000-0000-0000-000000000011'::uuid)
)
INSERT INTO course_chapters (id, course_id, title, display_order, created_at)
SELECT md5(s.id::text || ':chapter:' || chapter_no)::uuid, s.id,
       CASE chapter_no WHEN 1 THEN 'Khởi động và nền tảng' WHEN 2 THEN 'Phát triển kỹ năng' ELSE 'Thực hành và tổng kết' END,
       chapter_no, NOW()
FROM seed s JOIN courses c ON c.id = s.id CROSS JOIN generate_series(1, 3) chapter_no
ON CONFLICT (id) DO NOTHING;

WITH seed(id) AS (
  VALUES ('11000000-0000-0000-0000-000000000001'::uuid), ('11000000-0000-0000-0000-000000000002'::uuid),
         ('11000000-0000-0000-0000-000000000003'::uuid), ('11000000-0000-0000-0000-000000000004'::uuid),
         ('11000000-0000-0000-0000-000000000005'::uuid), ('11000000-0000-0000-0000-000000000006'::uuid),
         ('11000000-0000-0000-0000-000000000007'::uuid), ('11000000-0000-0000-0000-000000000008'::uuid),
         ('11000000-0000-0000-0000-000000000009'::uuid), ('11000000-0000-0000-0000-000000000010'::uuid),
         ('11000000-0000-0000-0000-000000000011'::uuid)
)
INSERT INTO course_lessons (id, chapter_id, title, lesson_type, duration_seconds, is_preview, display_order, created_at)
SELECT md5(s.id::text || ':lesson:' || chapter_no || ':' || lesson_no)::uuid,
       md5(s.id::text || ':chapter:' || chapter_no)::uuid,
       CASE lesson_no WHEN 1 THEN 'Bài học trọng tâm' WHEN 2 THEN 'Luyện tập có hướng dẫn' ELSE 'Bài kiểm tra ứng dụng' END,
       CASE lesson_no WHEN 1 THEN 'VIDEO' WHEN 2 THEN 'AUDIO' ELSE 'QUIZ' END,
       CASE lesson_no WHEN 1 THEN 720 WHEN 2 THEN 900 ELSE 1080 END,
       chapter_no = 1 AND lesson_no = 1, lesson_no, NOW()
FROM seed s JOIN courses c ON c.id = s.id
CROSS JOIN generate_series(1, 3) chapter_no CROSS JOIN generate_series(1, 3) lesson_no
ON CONFLICT (id) DO NOTHING;

INSERT INTO lesson_contents (id, lesson_id, content_type, storage_url, metadata, uploaded_at)
SELECT md5(l.id::text || ':content')::uuid, l.id, 'VIDEO',
       'https://res.cloudinary.com/demo/video/upload/v1692721306/samples/sea-turtle.mp4',
       '{"seeded":true,"quality":"demo"}'::jsonb, NOW()
FROM course_lessons l
JOIN course_chapters ch ON ch.id = l.chapter_id
WHERE ch.course_id::text LIKE '11000000-0000-0000-0000-%' AND l.is_preview = true
ON CONFLICT (lesson_id) DO NOTHING;

UPDATE courses SET thumbnail_url = '/images/course-placeholder.svg'
WHERE NULLIF(BTRIM(thumbnail_url), '') IS NULL;

COMMIT;
