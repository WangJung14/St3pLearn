BEGIN;

WITH seed(id) AS (
  VALUES ('11000000-0000-0000-0000-000000000001'::uuid), ('11000000-0000-0000-0000-000000000002'::uuid),
         ('11000000-0000-0000-0000-000000000003'::uuid), ('11000000-0000-0000-0000-000000000004'::uuid),
         ('11000000-0000-0000-0000-000000000005'::uuid), ('11000000-0000-0000-0000-000000000006'::uuid),
         ('11000000-0000-0000-0000-000000000007'::uuid), ('11000000-0000-0000-0000-000000000008'::uuid),
         ('11000000-0000-0000-0000-000000000009'::uuid), ('11000000-0000-0000-0000-000000000010'::uuid),
         ('11000000-0000-0000-0000-000000000011'::uuid)
), lessons AS (
  SELECT s.id AS course_id,
         jsonb_agg(to_jsonb(md5(s.id::text || ':lesson:' || chapter_no || ':' || lesson_no)::uuid) ORDER BY chapter_no, lesson_no) AS lesson_ids
  FROM seed s CROSS JOIN generate_series(1, 3) chapter_no CROSS JOIN generate_series(1, 3) lesson_no
  GROUP BY s.id
)
INSERT INTO course_replicas (id, status, total_lessons, total_duration, lesson_ids)
SELECT course_id, 'PUBLISHED', 9, 8100, lesson_ids FROM lessons
ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status, total_lessons = EXCLUDED.total_lessons,
  total_duration = EXCLUDED.total_duration, lesson_ids = EXCLUDED.lesson_ids;

WITH enrolled(ord, course_id, progress) AS (
  VALUES
    (1, '11000000-0000-0000-0000-000000000001'::uuid, 100.00::numeric),
    (2, '11000000-0000-0000-0000-000000000002'::uuid, 72.00::numeric),
    (3, '11000000-0000-0000-0000-000000000003'::uuid, 45.00::numeric),
    (4, '11000000-0000-0000-0000-000000000006'::uuid, 18.00::numeric),
    (5, '11000000-0000-0000-0000-000000000009'::uuid, 0.00::numeric),
    (6, '11000000-0000-0000-0000-000000000011'::uuid, 33.00::numeric)
)
INSERT INTO enrollments (id, student_id, course_id, status, progress_percent, enrolled_at, last_accessed_at, completed_at, last_accessed_lesson_id)
SELECT md5('d0ea3ced-fae0-41d5-a5cb-e4b468069cbb:' || course_id::text)::uuid,
       'd0ea3ced-fae0-41d5-a5cb-e4b468069cbb'::uuid, course_id,
       CASE WHEN progress = 100 THEN 'COMPLETED' ELSE 'ACTIVE' END, progress,
       NOW() - (30 - ord * 3) * INTERVAL '1 day', NOW() - ord * INTERVAL '3 hour',
       CASE WHEN progress = 100 THEN NOW() - INTERVAL '2 day' ELSE NULL END,
       md5(course_id::text || ':lesson:1:1')::uuid
FROM enrolled
ON CONFLICT (student_id, course_id) DO UPDATE SET progress_percent = EXCLUDED.progress_percent,
  status = EXCLUDED.status, last_accessed_at = EXCLUDED.last_accessed_at,
  last_accessed_lesson_id = EXCLUDED.last_accessed_lesson_id;

WITH enrolled(course_id, progress) AS (
  VALUES
    ('11000000-0000-0000-0000-000000000001'::uuid, 100.00::numeric),
    ('11000000-0000-0000-0000-000000000002'::uuid, 72.00::numeric),
    ('11000000-0000-0000-0000-000000000003'::uuid, 45.00::numeric),
    ('11000000-0000-0000-0000-000000000006'::uuid, 18.00::numeric),
    ('11000000-0000-0000-0000-000000000009'::uuid, 0.00::numeric),
    ('11000000-0000-0000-0000-000000000011'::uuid, 33.00::numeric)
), resolved AS (
  SELECT e.*, en.id AS enrollment_id FROM enrolled e
  JOIN enrollments en ON en.student_id = 'd0ea3ced-fae0-41d5-a5cb-e4b468069cbb'::uuid AND en.course_id = e.course_id
)
INSERT INTO learning_progress (enrollment_id, completed_lessons, total_lessons, progress_percent, watched_duration, total_duration, updated_at)
SELECT enrollment_id, LEAST(9, FLOOR(progress * 9 / 100)::integer), 9, progress,
       FLOOR(progress * 8100 / 100)::integer, 8100, NOW()
FROM resolved
ON CONFLICT (enrollment_id) DO UPDATE SET completed_lessons = EXCLUDED.completed_lessons,
  total_lessons = EXCLUDED.total_lessons, progress_percent = EXCLUDED.progress_percent,
  watched_duration = EXCLUDED.watched_duration, total_duration = EXCLUDED.total_duration, updated_at = NOW();

WITH enrolled(course_id, progress) AS (
  VALUES
    ('11000000-0000-0000-0000-000000000001'::uuid, 100.00::numeric),
    ('11000000-0000-0000-0000-000000000002'::uuid, 72.00::numeric),
    ('11000000-0000-0000-0000-000000000003'::uuid, 45.00::numeric),
    ('11000000-0000-0000-0000-000000000006'::uuid, 18.00::numeric),
    ('11000000-0000-0000-0000-000000000009'::uuid, 0.00::numeric),
    ('11000000-0000-0000-0000-000000000011'::uuid, 33.00::numeric)
), resolved AS (
  SELECT e.*, en.id AS enrollment_id FROM enrolled e
  JOIN enrollments en ON en.student_id = 'd0ea3ced-fae0-41d5-a5cb-e4b468069cbb'::uuid AND en.course_id = e.course_id
)
INSERT INTO lesson_progress (id, enrollment_id, lesson_id, status, watch_position_seconds, completed_at, updated_at)
SELECT md5(r.enrollment_id::text || ':lesson-progress:' || lesson_no)::uuid, r.enrollment_id,
       md5(r.course_id::text || ':lesson:1:' || lesson_no)::uuid,
       CASE WHEN r.progress >= lesson_no * 20 THEN 'COMPLETE' WHEN lesson_no = 1 AND r.progress > 0 THEN 'IN_PROGRESS' ELSE 'NOT_STARTED' END,
       CASE WHEN r.progress >= lesson_no * 20 THEN CASE lesson_no WHEN 1 THEN 720 WHEN 2 THEN 900 ELSE 1080 END ELSE FLOOR(r.progress * 7)::integer END,
       CASE WHEN r.progress >= lesson_no * 20 THEN NOW() - lesson_no * INTERVAL '1 day' ELSE NULL END, NOW()
FROM resolved r CROSS JOIN generate_series(1, 3) lesson_no
ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status, watch_position_seconds = EXCLUDED.watch_position_seconds,
  completed_at = EXCLUDED.completed_at, updated_at = NOW();

COMMIT;
