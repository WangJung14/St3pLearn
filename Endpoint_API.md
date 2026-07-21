# System API Endpoints

## Identity Service

- **searchUsers** - `[GET] /api/admin/users` - *Role: ADMIN*
- **getUserDetail** - `[GET] /api/admin/users/{userId}` - *Role: ADMIN*
- **assignRole** - `[POST] /api/admin/users/{userId}/roles` - *Role: ADMIN*
- **removeRole** - `[DELETE] /api/admin/users/{userId}/roles/{roleName}` - *Role: ADMIN*
- **suspendUser** - `[PUT] /api/admin/users/{userId}/suspend` - *Role: ADMIN*
- **lockUser** - `[PUT] /api/admin/users/{userId}/lock` - *Role: ADMIN*
- **activateUser** - `[PUT] /api/admin/users/{userId}/activate` - *Role: ADMIN*
- **register** - `[POST] /api/auth/register` - *Public API / Authenticated*
- **login** - `[POST] /api/auth/login` - *Public API / Authenticated*
- **refresh** - `[POST] /api/auth/refresh` - *Public API / Authenticated*
- **logout** - `[POST] /api/auth/logout` - *Public API / Authenticated*
- **forgotPassword** - `[POST] /api/auth/forgot-password` - *Public API / Authenticated*
- **resetPassword** - `[POST] /api/auth/reset-password` - *Public API / Authenticated*
- **verifyEmail** - `[POST] /api/auth/verify-email` - *Public API / Authenticated*
- **resendVerificationEmail** - `[POST] /api/auth/resend-verification-email` - *Public API / Authenticated*
- **getMyProfile** - `[GET] /api/users/me` - *Public API / Authenticated*
- **getPublicProfile** - `[GET] /api/users/p/{publicId}` - *Public API / Authenticated*
- **updateMyProfile** - `[POST] /api/users/me` - *Public API / Authenticated*
- **deactivateMyAccount** - `[DELETE] /api/users/me` - *Public API / Authenticated*
- **getMyLoginHistory** - `[GET] /api/users/me/login-history` - *Public API / Authenticated*
- **changePassword** - `[POST] /api/users/me/password` - *Public API / Authenticated*


## Catalog Service

- **removeCourse** - `[POST] /api/admin/courses/{courseId}/remove` - *Role: ADMIN*
- **getReports** - `[GET] /api/admin/reports` - *Role: ADMIN*
- **processReport** - `[POST] /api/admin/reports/{reportId}/process` - *Role: ADMIN*
- **getAll** - `[GET] /api/categories` - *Public API / Authenticated*
- **create** - `[POST] /api/categories` - *Public API / Authenticated*
- **update** - `[PUT] /api/categories/{id}` - *Role: ADMIN*
- **delete** - `[DELETE] /api/categories/{id}` - *Role: ADMIN*
- **getChapters** - `[GET] /api/courses/{courseId}/chapters` - *Public API / Authenticated*
- **createChapter** - `[POST] /api/courses/{courseId}/chapters` - *Public API / Authenticated*
- **updateChapter** - `[POST] /api/courses/{courseId}/chapters/{chapterId}` - *Role: TEACHER, ADMIN*
- **deleteChapter** - `[DELETE] /api/courses/{courseId}/chapters/{chapterId}` - *Role: ADMIN,TEACHER*
- **createCourse** - `[POST] /api/courses` - *Public API / Authenticated*
- **updateCourse** - `[POST] /api/courses/{courseId}` - *Public API / Authenticated*
- **getAllCourses** - `[GET] /api/courses` - *Public API / Authenticated*
- **getCourse** - `[GET] /api/courses/{courseId}` - *Role: ADMIN*
- **archiveCourse** - `[DELETE] /api/courses/{courseId}/archive` - *Public API / Authenticated*
- **assignTaxonomy** - `[POST] /api/courses/{courseId}/taxonomy` - *Role: TEACHER, ADMIN*
- **submitForApproval** - `[POST] /api/courses/{courseId}/submit` - *Role: TEACHER, ADMIN*
- **processCourseApproval** - `[POST] /api/courses/approvals/{requestId}/process` - *Role: ADMIN,TEACHER*
- **getPendingApprovals** - `[GET] /api/courses/approvals/pending` - *Role: ADMIN*
- **getApprovalDetail** - `[GET] /api/courses/approvals/{requestId}` - *Role: ADMIN*
- **searchPublicCourses** - `[GET] /api/courses/p/search` - *Role: ADMIN*
- **getMyCourses** - `[GET] /api/courses/my-courses` - *Public API / Authenticated*
- **cancelCourseApproval** - `[POST] /api/courses/{courseId}/cancel-submit` - *Role: TEACHER*
- **publishCourse** - `[POST] /api/courses/{courseId}/publish` - *Role: TEACHER*
- **getPublicCourseDetail** - `[GET] /api/courses/p/{slug}` - *Role: TEACHER*
- **getBulkSummaries** - `[POST] /api/courses/bulk-summaries` - *Public API / Authenticated*
- **migrateCourseStatuses** - `[POST] /api/courses/admin/migrate-status` - *Public API / Authenticated*
- **submitReview** - `[POST] /api/courses/{courseId}/reviews` - *Public API / Authenticated*
- **getCourseReviews** - `[GET] /api/courses/p/{courseId}/reviews` - *Role: STUDENT*
- **updateReview** - `[POST] /api/courses/{courseId}/reviews/{reviewId}` - *Public API / Authenticated*
- **deleteReview** - `[DELETE] /api/courses/{courseId}/reviews/{reviewId}` - *Role: STUDENT*
- **replyToReview** - `[POST] /api/courses/{courseId}/reviews/{reviewId}/reply` - *Role: STUDENT*
- **getLessons** - `[GET] /api/courses/{courseId}/chapters/{chapterId}/lessons` - *Public API / Authenticated*
- **createLesson** - `[POST] /api/courses/{courseId}/chapters/{chapterId}/lessons` - *Public API / Authenticated*
- **updateLesson** - `[POST] /api/courses/{courseId}/chapters/{chapterId}/lessons/{lessonId}` - *Role: ADMIN,TEACHER*
- **deleteLesson** - `[DELETE] /api/courses/{courseId}/chapters/{chapterId}/lessons/{lessonId}` - *Role: ADMIN,TEACHER*
- **getUploadSignature** - `[GET] /api/courses/{courseId}/chapters/{chapterId}/lessons/upload-signature` - *Role: ADMIN,TEACHER*
- **saveLessonContent** - `[POST] /api/courses/{courseId}/chapters/{chapterId}/lessons/{lessonId}/content` - *Role: TEACHER, ADMIN*
- **createReport** - `[POST] /api/reports` - *Role: STUDENT, TEACHER*
- **getAll** - `[GET] /api/tags` - *Public API / Authenticated*
- **create** - `[POST] /api/tags` - *Public API / Authenticated*
- **update** - `[PUT] /api/tags/{id}` - *Role: ADMIN*
- **delete** - `[DELETE] /api/tags/{id}` - *Role: ADMIN*
- **saveCourse** - `[POST] /api/wishlists/course/{courseId}` - *Public API / Authenticated*
- **removeCourse** - `[DELETE] /api/wishlists/courses/{courseId}` - *Role: STUDENT*
- **getMyWishlist** - `[GET] /api/wishlists` - *Role: STUDENT*


## Learning Service

- **issueCertificate** - `[POST] /api/learning/certificates/issue` - *Public API / Authenticated*
- **downloadCertificate** - `[GET] /api/learning/certificates/{certificateId}/download` - *Role: INSTRUCTOR, TEACHER*
- **verifyCertificate** - `[GET] /api/learning/certificates/verify/{certificateCode}` - *Role: STUDENT*
- **enrollCourse** - `[POST] /api/enrollments` - *Public API / Authenticated*
- **getMyEnrolledCourses** - `[GET] /api/enrollments/my-courses` - *Role: STUDENT*
- **createExam** - `[POST] /api/learning/exams` - *Public API / Authenticated*
- **updateExamInfo** - `[PUT] /api/learning/exams/{examId}` - *Role: INSTRUCTOR, TEACHER*
- **updateExamQuestions** - `[PUT] /api/learning/exams/{examId}/questions` - *Role: INSTRUCTOR, TEACHER*
- **updateExamStatus** - `[PUT] /api/learning/exams/{examId}/status` - *Role: INSTRUCTOR, TEACHER*
- **deleteExam** - `[DELETE] /api/learning/exams/{examId}` - *Role: INSTRUCTOR, TEACHER*
- **getExamsByInstructor** - `[GET] /api/learning/exams` - *Role: INSTRUCTOR, TEACHER*
- **getExamById** - `[GET] /api/learning/exams/{examId}` - *Role: INSTRUCTOR, TEACHER*
- **getAvailableExamsForStudent** - `[GET] /api/learning/student/exams` - *Role: STUDENT*
- **startExam** - `[POST] /api/learning/exams/{examId}/attempts` - *Role: INSTRUCTOR, TEACHER*
- **submitExam** - `[POST] /api/learning/exams/attempts/{attemptId}/submit` - *Role: STUDENT*
- **getExamSubmissions** - `[GET] /api/learning/exams/{examId}/submissions` - *Role: STUDENT*
- **gradeSubmission** - `[PUT] /api/learning/exams/submissions/{attemptId}/grade` - *Role: INSTRUCTOR, TEACHER*
- **getExamResult** - `[GET] /api/learning/exams/attempts/{attemptId}/result` - *Role: INSTRUCTOR, TEACHER*
- **getDueCards** - `[GET] /api/learning/flashcard-sets/{id}/due-cards` - *Public API / Authenticated*
- **reviewFlashcard** - `[POST] /api/learning/flashcards/{id}/review` - *Role: STUDENT*
- **getDashboardHistory** - `[GET] /api/learning/dashboard/history` - *Role: STUDENT*
- **createFlashcardSet** - `[POST] /api/learning/flashcard-sets` - *Public API / Authenticated*
- **getMyFlashcardSets** - `[GET] /api/learning/flashcard-sets/my-sets` - *Role: INSTRUCTOR, TEACHER*
- **getAvailableFlashcardSets** - `[GET] /api/learning/flashcard-sets/available` - *Role: STUDENT*
- **addCardToFlashcardSet** - `[POST] /api/learning/flashcard-sets/{id}/cards` - *Role: INSTRUCTOR, TEACHER*
- **cloneFlashcardSet** - `[POST] /api/learning/flashcard-sets/{id}/clone` - *Role: INSTRUCTOR, TEACHER*
- **startLearning** - `[POST] /api/learning/courses/{courseId}/start` - *Public API / Authenticated*
- **trackProgress** - `[POST] /api/learning/courses/{courseId}/lessons/{lessonId}/progress` - *Role: STUDENT, INSTRUCTOR, ADMIN*
- **resumeLearning** - `[GET] /api/learning/courses/{courseId}/resume` - *Role: STUDENT*
- **completeLesson** - `[POST] /api/learning/courses/{courseId}/lessons/{lessonId}/complete` - *Role: STUDENT*
- **createBank** - `[POST] /api/learning/question-banks` - *Public API / Authenticated*
- **updateBank** - `[PUT] /api/learning/question-banks/{bankId}` - *Role: INSTRUCTOR, TEACHER*
- **deleteBank** - `[DELETE] /api/learning/question-banks/{bankId}` - *Role: INSTRUCTOR, TEACHER*
- **getMyBanks** - `[GET] /api/learning/question-banks` - *Role: INSTRUCTOR, TEACHER*
- **createQuestion** - `[POST] /api/learning/question-banks/{bankId}/questions` - *Public API / Authenticated*
- **updateQuestion** - `[PUT] /api/learning/questions/{questionId}` - *Role: INSTRUCTOR, TEACHER*
- **deleteQuestion** - `[DELETE] /api/learning/questions/{questionId}` - *Role: INSTRUCTOR, TEACHER*
- **getQuestionsByBankId** - `[GET] /api/learning/question-banks/{bankId}/questions` - *Role: INSTRUCTOR, TEACHER*
- **importVocabulary** - `[POST] /api/learning/vocabulary/import` - *Public API / Authenticated*


## Payment Service

- **createCoupon** - `[POST] /api/payment/coupons` - *Public API / Authenticated*
- **calculateDiscount** - `[POST] /api/payment/coupons/calculate` - *Role: ADMIN, INSTRUCTOR*
- **getMyOrders** - `[GET] /api/payment/orders` - *Role: STUDENT*
- **checkout** - `[POST] /api/payment/orders/checkout` - *Role: STUDENT*
- **vnpayCallback** - `[GET] /api/payment/vnpay/callback` - *Public API*
- **requestRefund** - `[POST] /api/payment/refunds` - *Role: STUDENT*
- **approveRefund** - `[POST] /api/payment/refunds/{id}/approve` - *Role: ADMIN*


## Admin Service

- **getDashboard** - `[GET] /api/admin/dashboard` - *Public API / Authenticated*
- **exportRevenueReport** - `[GET] /api/admin/reports/revenue/export` - *Public API / Authenticated*
- **exportCourseReport** - `[GET] /api/admin/reports/course/export` - *Public API / Authenticated*

