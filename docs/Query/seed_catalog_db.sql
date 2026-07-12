-- Seed data for catalog_db.
-- Run against database: catalog_db
-- Cross-service IDs match seed_identity_db.sql and seed_learning_db.sql.

BEGIN;

-- Remove conflicting sample courses with the same slugs but different IDs.
-- This keeps fixed course IDs stable for cross-service seed data.
DELETE FROM lesson_contents
WHERE lesson_id IN (
    SELECT l.id
    FROM course_lessons l
    JOIN course_chapters ch ON ch.id = l.chapter_id
    JOIN courses c ON c.id = ch.course_id
    WHERE c.slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND c.id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM course_lessons
WHERE chapter_id IN (
    SELECT ch.id
    FROM course_chapters ch
    JOIN courses c ON c.id = ch.course_id
    WHERE c.slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND c.id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM course_chapters
WHERE course_id IN (
    SELECT id
    FROM courses
    WHERE slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM review_replies
WHERE review_id IN (
    SELECT r.id
    FROM course_reviews r
    JOIN courses c ON c.id = r.course_id
    WHERE c.slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND c.id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM course_reviews
WHERE course_id IN (
    SELECT id
    FROM courses
    WHERE slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM course_wishlists
WHERE course_id IN (
    SELECT id
    FROM courses
    WHERE slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM student_enrolled_courses
WHERE course_id IN (
    SELECT id
    FROM courses
    WHERE slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM course_approval_requests
WHERE course_id IN (
    SELECT id
    FROM courses
    WHERE slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM course_categories
WHERE course_id IN (
    SELECT id
    FROM courses
    WHERE slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM course_tags
WHERE course_id IN (
    SELECT id
    FROM courses
    WHERE slug IN (
        'ielts-masterclass-step-by-step-7-5',
        'english-grammar-for-beginners-intermediate',
        'listening-pronunciation-secrets',
        'teacher-draft-pronunciation-lab'
    )
    AND id NOT IN (
        '40000000-0000-4000-8000-000000000001',
        '40000000-0000-4000-8000-000000000002',
        '40000000-0000-4000-8000-000000000003',
        '40000000-0000-4000-8000-000000000004'
    )
);

DELETE FROM courses
WHERE slug IN (
    'ielts-masterclass-step-by-step-7-5',
    'english-grammar-for-beginners-intermediate',
    'listening-pronunciation-secrets',
    'teacher-draft-pronunciation-lab'
)
AND id NOT IN (
    '40000000-0000-4000-8000-000000000001',
    '40000000-0000-4000-8000-000000000002',
    '40000000-0000-4000-8000-000000000003',
    '40000000-0000-4000-8000-000000000004'
);

INSERT INTO categories (id, name, slug) VALUES
    ('30000000-0000-4000-8000-000000000001', 'Speaking', 'speaking'),
    ('30000000-0000-4000-8000-000000000002', 'Listening', 'listening'),
    ('30000000-0000-4000-8000-000000000003', 'Grammar', 'grammar'),
    ('30000000-0000-4000-8000-000000000004', 'Vocabulary', 'vocabulary'),
    ('30000000-0000-4000-8000-000000000005', 'Writing', 'writing'),
    ('30000000-0000-4000-8000-000000000006', 'IELTS', 'ielts')
ON CONFLICT (slug) DO UPDATE
SET name = EXCLUDED.name;

INSERT INTO tags (id, name) VALUES
    ('31000000-0000-4000-8000-000000000001', 'IELTS'),
    ('31000000-0000-4000-8000-000000000002', 'TOEIC'),
    ('31000000-0000-4000-8000-000000000003', 'TOEFL'),
    ('31000000-0000-4000-8000-000000000004', 'A1'),
    ('31000000-0000-4000-8000-000000000005', 'A2'),
    ('31000000-0000-4000-8000-000000000006', 'B1'),
    ('31000000-0000-4000-8000-000000000007', 'B2'),
    ('31000000-0000-4000-8000-000000000008', 'C1'),
    ('31000000-0000-4000-8000-000000000009', 'Speaking'),
    ('31000000-0000-4000-8000-000000000010', 'Writing')
ON CONFLICT (name) DO NOTHING;

INSERT INTO courses (
    id,
    avg_rating,
    created_at,
    currency,
    current_version_id,
    instructor_id,
    language,
    level,
    original_price,
    price,
    short_description,
    slug,
    status,
    thumbnail_url,
    title,
    total_reviews,
    total_students,
    updated_at
) VALUES
    (
        '40000000-0000-4000-8000-000000000001',
        4.80,
        now() - interval '45 days',
        'VND',
        null,
        '00000000-0000-4000-8000-000000000002',
        'English',
        'IELTS',
        1500000.00,
        1200000.00,
        'Master all four IELTS skills with structured lessons, guided practice, and review workflows.',
        'ielts-masterclass-step-by-step-7-5',
        'PUBLISHED',
        'https://images.unsplash.com/photo-1544717305-2782549b5136?q=80&w=1200&auto=format&fit=crop',
        'IELTS Masterclass: Step-by-Step 7.5+',
        2,
        2,
        now()
    ),
    (
        '40000000-0000-4000-8000-000000000002',
        4.60,
        now() - interval '35 days',
        'VND',
        null,
        '00000000-0000-4000-8000-000000000002',
        'English',
        'B1',
        700000.00,
        500000.00,
        'Build a strong English grammar foundation from beginner to intermediate topics.',
        'english-grammar-for-beginners-intermediate',
        'PUBLISHED',
        'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?q=80&w=1200&auto=format&fit=crop',
        'English Grammar for Beginners & Intermediate',
        1,
        1,
        now()
    ),
    (
        '40000000-0000-4000-8000-000000000003',
        0.00,
        now() - interval '10 days',
        'VND',
        null,
        '00000000-0000-4000-8000-000000000002',
        'English',
        'A2',
        900000.00,
        750000.00,
        'Listening and pronunciation course waiting for admin approval.',
        'listening-pronunciation-secrets',
        'PENDING_REVIEW',
        'https://images.unsplash.com/photo-1522881197277-c6cf5246ca88?q=80&w=1200&auto=format&fit=crop',
        'Listening & Pronunciation Secrets',
        0,
        0,
        now()
    ),
    (
        '40000000-0000-4000-8000-000000000004',
        0.00,
        now() - interval '2 days',
        'VND',
        null,
        '00000000-0000-4000-8000-000000000002',
        'English',
        'A1',
        300000.00,
        0.00,
        'Draft course for teacher authoring screens.',
        'teacher-draft-pronunciation-lab',
        'DRAFT',
        'https://images.unsplash.com/photo-1455390582262-044cdead277a?q=80&w=1200&auto=format&fit=crop',
        'Teacher Draft Pronunciation Lab',
        0,
        0,
        now()
    )
ON CONFLICT (id) DO UPDATE
SET avg_rating = EXCLUDED.avg_rating,
    currency = EXCLUDED.currency,
    current_version_id = EXCLUDED.current_version_id,
    instructor_id = EXCLUDED.instructor_id,
    language = EXCLUDED.language,
    level = EXCLUDED.level,
    original_price = EXCLUDED.original_price,
    price = EXCLUDED.price,
    short_description = EXCLUDED.short_description,
    slug = EXCLUDED.slug,
    status = EXCLUDED.status,
    thumbnail_url = EXCLUDED.thumbnail_url,
    title = EXCLUDED.title,
    total_reviews = EXCLUDED.total_reviews,
    total_students = EXCLUDED.total_students,
    updated_at = now();

INSERT INTO course_categories (course_id, category_id)
SELECT '40000000-0000-4000-8000-000000000001'::uuid, id FROM categories WHERE slug IN ('ielts', 'speaking', 'writing')
ON CONFLICT (course_id, category_id) DO NOTHING;

INSERT INTO course_categories (course_id, category_id)
SELECT '40000000-0000-4000-8000-000000000002'::uuid, id FROM categories WHERE slug IN ('grammar', 'vocabulary')
ON CONFLICT (course_id, category_id) DO NOTHING;

INSERT INTO course_categories (course_id, category_id)
SELECT '40000000-0000-4000-8000-000000000003'::uuid, id FROM categories WHERE slug IN ('listening', 'speaking')
ON CONFLICT (course_id, category_id) DO NOTHING;

INSERT INTO course_categories (course_id, category_id)
SELECT '40000000-0000-4000-8000-000000000004'::uuid, id FROM categories WHERE slug IN ('speaking')
ON CONFLICT (course_id, category_id) DO NOTHING;

INSERT INTO course_tags (course_id, tag_id)
SELECT '40000000-0000-4000-8000-000000000001'::uuid, id FROM tags WHERE name IN ('IELTS', 'B2', 'C1', 'Speaking', 'Writing')
ON CONFLICT (course_id, tag_id) DO NOTHING;

INSERT INTO course_tags (course_id, tag_id)
SELECT '40000000-0000-4000-8000-000000000002'::uuid, id FROM tags WHERE name IN ('B1', 'A2')
ON CONFLICT (course_id, tag_id) DO NOTHING;

INSERT INTO course_tags (course_id, tag_id)
SELECT '40000000-0000-4000-8000-000000000003'::uuid, id FROM tags WHERE name IN ('A2', 'Speaking')
ON CONFLICT (course_id, tag_id) DO NOTHING;

INSERT INTO course_tags (course_id, tag_id)
SELECT '40000000-0000-4000-8000-000000000004'::uuid, id FROM tags WHERE name IN ('A1', 'Speaking')
ON CONFLICT (course_id, tag_id) DO NOTHING;

INSERT INTO course_chapters (
    id,
    course_id,
    created_at,
    display_order,
    title
) VALUES
    ('41000000-0000-4000-8000-000000000001', '40000000-0000-4000-8000-000000000001', now() - interval '44 days', 1, 'IELTS Orientation'),
    ('41000000-0000-4000-8000-000000000002', '40000000-0000-4000-8000-000000000001', now() - interval '43 days', 2, 'Writing Task 2 Strategy'),
    ('41000000-0000-4000-8000-000000000003', '40000000-0000-4000-8000-000000000002', now() - interval '34 days', 1, 'Grammar Foundations'),
    ('41000000-0000-4000-8000-000000000004', '40000000-0000-4000-8000-000000000003', now() - interval '9 days', 1, 'Pronunciation Warm-up'),
    ('41000000-0000-4000-8000-000000000005', '40000000-0000-4000-8000-000000000004', now() - interval '2 days', 1, 'Draft Lesson Plan')
ON CONFLICT (id) DO UPDATE
SET course_id = EXCLUDED.course_id,
    display_order = EXCLUDED.display_order,
    title = EXCLUDED.title;

INSERT INTO course_lessons (
    id,
    chapter_id,
    created_at,
    display_order,
    duration_seconds,
    is_preview,
    lesson_type,
    title
) VALUES
    ('42000000-0000-4000-8000-000000000001', '41000000-0000-4000-8000-000000000001', now() - interval '44 days', 1, 600, true, 'VIDEO', 'Welcome to IELTS 7.5+'),
    ('42000000-0000-4000-8000-000000000002', '41000000-0000-4000-8000-000000000001', now() - interval '44 days', 2, 900, false, 'PDF', 'IELTS Band Descriptor Notes'),
    ('42000000-0000-4000-8000-000000000003', '41000000-0000-4000-8000-000000000002', now() - interval '43 days', 1, 1200, false, 'VIDEO', 'Build a Band 7 Essay Structure'),
    ('42000000-0000-4000-8000-000000000004', '41000000-0000-4000-8000-000000000003', now() - interval '34 days', 1, 720, true, 'VIDEO', 'Tenses Quick Review'),
    ('42000000-0000-4000-8000-000000000005', '41000000-0000-4000-8000-000000000004', now() - interval '9 days', 1, 480, true, 'AUDIO', 'Shadowing Native Speech'),
    ('42000000-0000-4000-8000-000000000006', '41000000-0000-4000-8000-000000000005', now() - interval '2 days', 1, 300, false, 'VIDEO', 'Draft Pronunciation Drill')
ON CONFLICT (id) DO UPDATE
SET chapter_id = EXCLUDED.chapter_id,
    display_order = EXCLUDED.display_order,
    duration_seconds = EXCLUDED.duration_seconds,
    is_preview = EXCLUDED.is_preview,
    lesson_type = EXCLUDED.lesson_type,
    title = EXCLUDED.title;

INSERT INTO lesson_contents (
    id,
    checksum,
    content_type,
    file_size,
    metadata,
    storage_url,
    text_content,
    uploaded_at,
    lesson_id
) VALUES
    ('43000000-0000-4000-8000-000000000001', 'seed-video-001', 'VIDEO_CLOUDINARY', 10485760, '{"duration":600,"format":"mp4"}'::jsonb, 'https://res.cloudinary.com/demo/video/upload/sample.mp4', 'Welcome transcript for IELTS orientation.', now() - interval '44 days', '42000000-0000-4000-8000-000000000001'),
    ('43000000-0000-4000-8000-000000000002', 'seed-pdf-001', 'PDF_URL', 524288, '{"pages":12}'::jsonb, 'https://example.com/ielts-band-descriptors.pdf', 'PDF summary: IELTS public band descriptors.', now() - interval '44 days', '42000000-0000-4000-8000-000000000002'),
    ('43000000-0000-4000-8000-000000000003', 'seed-video-002', 'VIDEO_CLOUDINARY', 18874368, '{"duration":1200,"format":"mp4"}'::jsonb, 'https://res.cloudinary.com/demo/video/upload/sample.mp4', 'Essay structure transcript.', now() - interval '43 days', '42000000-0000-4000-8000-000000000003'),
    ('43000000-0000-4000-8000-000000000004', 'seed-video-003', 'VIDEO_CLOUDINARY', 9437184, '{"duration":720,"format":"mp4"}'::jsonb, 'https://res.cloudinary.com/demo/video/upload/sample.mp4', 'Grammar review transcript.', now() - interval '34 days', '42000000-0000-4000-8000-000000000004'),
    ('43000000-0000-4000-8000-000000000005', 'seed-audio-001', 'AUDIO_URL', 3145728, '{"duration":480,"format":"mp3"}'::jsonb, 'https://example.com/audio/shadowing-native-speech.mp3', 'Audio practice notes.', now() - interval '9 days', '42000000-0000-4000-8000-000000000005')
ON CONFLICT (lesson_id) DO UPDATE
SET checksum = EXCLUDED.checksum,
    content_type = EXCLUDED.content_type,
    file_size = EXCLUDED.file_size,
    metadata = EXCLUDED.metadata,
    storage_url = EXCLUDED.storage_url,
    text_content = EXCLUDED.text_content,
    uploaded_at = EXCLUDED.uploaded_at;

INSERT INTO course_approval_requests (
    id,
    course_id,
    course_version_id,
    review_note,
    reviewed_at,
    reviewer_id,
    status,
    submitted_at,
    submitted_by
) VALUES
    ('44000000-0000-4000-8000-000000000001', '40000000-0000-4000-8000-000000000001', null, 'Approved seed course.', now() - interval '42 days', '00000000-0000-4000-8000-000000000003', 'APPROVED', now() - interval '43 days', '00000000-0000-4000-8000-000000000002'),
    ('44000000-0000-4000-8000-000000000002', '40000000-0000-4000-8000-000000000002', null, 'Approved grammar seed course.', now() - interval '32 days', '00000000-0000-4000-8000-000000000003', 'APPROVED', now() - interval '33 days', '00000000-0000-4000-8000-000000000002'),
    ('44000000-0000-4000-8000-000000000003', '40000000-0000-4000-8000-000000000003', null, null, null, null, 'PENDING', now() - interval '8 days', '00000000-0000-4000-8000-000000000002')
ON CONFLICT (id) DO UPDATE
SET course_id = EXCLUDED.course_id,
    course_version_id = EXCLUDED.course_version_id,
    review_note = EXCLUDED.review_note,
    reviewed_at = EXCLUDED.reviewed_at,
    reviewer_id = EXCLUDED.reviewer_id,
    status = EXCLUDED.status,
    submitted_at = EXCLUDED.submitted_at,
    submitted_by = EXCLUDED.submitted_by;

INSERT INTO student_enrolled_courses (
    id,
    course_id,
    enrolled_at,
    student_id
) VALUES
    ('45000000-0000-4000-8000-000000000001', '40000000-0000-4000-8000-000000000001', now() - interval '20 days', '00000000-0000-4000-8000-000000000001'),
    ('45000000-0000-4000-8000-000000000002', '40000000-0000-4000-8000-000000000002', now() - interval '12 days', '00000000-0000-4000-8000-000000000001')
ON CONFLICT (student_id, course_id) DO UPDATE
SET enrolled_at = EXCLUDED.enrolled_at;

INSERT INTO course_wishlists (
    id,
    course_id,
    created_at,
    is_active,
    price_at_added,
    student_id,
    updated_at
) VALUES
    ('46000000-0000-4000-8000-000000000001', '40000000-0000-4000-8000-000000000003', now() - interval '5 days', true, 750000.00, '00000000-0000-4000-8000-000000000001', now()),
    ('46000000-0000-4000-8000-000000000002', '40000000-0000-4000-8000-000000000004', now() - interval '1 day', true, 0.00, '00000000-0000-4000-8000-000000000001', now())
ON CONFLICT (student_id, course_id) DO UPDATE
SET is_active = EXCLUDED.is_active,
    price_at_added = EXCLUDED.price_at_added,
    updated_at = now();

INSERT INTO course_reviews (
    id,
    course_id,
    created_at,
    rating,
    review_text,
    student_id,
    updated_at
) VALUES
    ('47000000-0000-4000-8000-000000000001', '40000000-0000-4000-8000-000000000001', now() - interval '15 days', 5, 'Great structure and useful writing feedback.', '00000000-0000-4000-8000-000000000001', now()),
    ('47000000-0000-4000-8000-000000000002', '40000000-0000-4000-8000-000000000002', now() - interval '7 days', 4, 'Clear grammar explanations for daily practice.', '00000000-0000-4000-8000-000000000001', now())
ON CONFLICT (course_id, student_id) DO UPDATE
SET rating = EXCLUDED.rating,
    review_text = EXCLUDED.review_text,
    updated_at = now();

INSERT INTO review_replies (
    id,
    author_id,
    content,
    created_at,
    review_id
) VALUES
    ('48000000-0000-4000-8000-000000000001', '00000000-0000-4000-8000-000000000002', 'Thank you. Keep practicing the weekly essay drills.', now() - interval '14 days', '47000000-0000-4000-8000-000000000001'),
    ('48000000-0000-4000-8000-000000000002', '00000000-0000-4000-8000-000000000005', 'Nice progress. Focus on tense consistency next.', now() - interval '6 days', '47000000-0000-4000-8000-000000000002')
ON CONFLICT (id) DO UPDATE
SET author_id = EXCLUDED.author_id,
    content = EXCLUDED.content,
    created_at = EXCLUDED.created_at,
    review_id = EXCLUDED.review_id;

COMMIT;
