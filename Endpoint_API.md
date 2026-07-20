### Identity Service
searchUsers - [GET] /api/admin/users - Role: ADMIN
getUserDetail - [GET] /api/admin/users/{userId} - Role: ADMIN
assignRole - [POST] /api/admin/users/{userId}/roles - Role: ADMIN
removeRole - [DELETE] /api/admin/users/{userId}/roles/{roleName} - Role: ADMIN
suspendUser - [PUT] /api/admin/users/{userId}/suspend - Role: ADMIN
lockUser - [PUT] /api/admin/users/{userId}/lock - Role: ADMIN
activateUser - [PUT] /api/admin/users/{userId}/activate - Role: ADMIN
register - [POST] /api/auth/register - public api / authenticated
login - [POST] /api/auth/login - public api / authenticated
refresh - [POST] /api/auth/refresh - public api / authenticated
logout - [POST] /api/auth/logout - public api / authenticated
forgotPassword - [POST] /api/auth/forgot-password - public api / authenticated
resetPassword - [POST] /api/auth/reset-password - public api / authenticated
verifyEmail - [POST] /api/auth/verify-email - public api / authenticated
resendVerificationEmail - [POST] /api/auth/resend-verification-email - public api / authenticated
getMyProfile - [GET] /api/users/me - public api / authenticated
getPublicProfile - [GET] /api/users/p/{publicId} - public api / authenticated
updateMyProfile - [POST] /api/users/me - public api / authenticated
deactivateMyAccount - [DELETE] /api/users/me - public api / authenticated
getMyLoginHistory - [GET] /api/users/me/login-history - public api / authenticated
changePassword - [POST] /api/users/me/password - public api / authenticated

### Catalog Service
removeCourse - [POST] /api/admin/courses/{courseId}/remove - Role: ADMIN
getReports - [GET] /api/admin/reports - Role: ADMIN
processReport - [POST] /api/admin/reports/{reportId}/process - Role: ADMIN
getAll - [GET] /api/categories - public api / authenticated
create - [POST] /api/categories - public api / authenticated
update - [PUT] /api/categories/{id} - Role: ADMIN
delete - [DELETE] /api/categories/{id} - Role: ADMIN
getChapters - [GET] /api/courses/{courseId}/chapters - public api / authenticated
createChapter - [POST] /api/courses/{courseId}/chapters - public api / authenticated
updateChapter - [POST] /api/courses/{courseId}/chapters/{chapterId} - Role: TEACHER, ADMIN
deleteChapter - [DELETE] /api/courses/{courseId}/chapters/{chapterId} - Role: ADMIN,TEACHER
createCourse - [POST] /api/courses - public api / authenticated
updateCourse - [POST] /api/courses/{courseId} - public api / authenticated
getAllCourses - [GET] /api/courses - public api / authenticated
getCourse - [GET] /api/courses/{courseId} - Role: ADMIN
archiveCourse - [DELETE] /api/courses/{courseId}/archive - public api / authenticated
assignTaxonomy - [POST] /api/courses/{courseId}/taxonomy - Role: TEACHER, ADMIN
submitForApproval - [POST] /api/courses/{courseId}/submit - Role: TEACHER, ADMIN
processCourseApproval - [POST] /api/courses/approvals/{requestId}/process - Role: ADMIN,TEACHER
getPendingApprovals - [GET] /api/courses/approvals/pending - Role: ADMIN
getApprovalDetail - [GET] /api/courses/approvals/{requestId} - Role: ADMIN
searchPublicCourses - [GET] /api/courses/p/search - Role: ADMIN
getMyCourses - [GET] /api/courses/my-courses - public api / authenticated
cancelCourseApproval - [POST] /api/courses/{courseId}/cancel-submit - Role: TEACHER
publishCourse - [POST] /api/courses/{courseId}/publish - Role: TEACHER
getPublicCourseDetail - [GET] /api/courses/p/{slug} - Role: TEACHER
getBulkSummaries - [POST] /api/courses/bulk-summaries - public api / authenticated
migrateCourseStatuses - [POST] /api/courses/admin/migrate-status - public api / authenticated
submitReview - [POST] /api/courses/{courseId}/reviews - public api / authenticated
getCourseReviews - [GET] /api/courses/p/{courseId}/reviews - Role: STUDENT
updateReview - [POST] /api/courses/{courseId}/reviews/{reviewId} - public api / authenticated
deleteReview - [DELETE] /api/courses/{courseId}/reviews/{reviewId} - Role: STUDENT
replyToReview - [POST] /api/courses/{courseId}/reviews/{reviewId}/reply - Role: STUDENT
getLessons - [GET] /api/courses/{courseId}/chapters/{chapterId}/lessons - public api / authenticated
createLesson - [POST] /api/courses/{courseId}/chapters/{chapterId}/lessons - public api / authenticated
updateLesson - [POST] /api/courses/{courseId}/chapters/{chapterId}/lessons/{lessonId} - Role: ADMIN,TEACHER
deleteLesson - [DELETE] /api/courses/{courseId}/chapters/{chapterId}/lessons/{lessonId} - Role: ADMIN,TEACHER
getUploadSignature - [GET] /api/courses/{courseId}/chapters/{chapterId}/lessons/upload-signature - Role: ADMIN,TEACHER
saveLessonContent - [POST] /api/courses/{courseId}/chapters/{chapterId}/lessons/{lessonId}/content - Role: TEACHER, ADMIN
createReport - [POST] /api/reports - Role: STUDENT, TEACHER
getAll - [GET] /api/tags - public api / authenticated
create - [POST] /api/tags - public api / authenticated
update - [PUT] /api/tags/{id} - Role: ADMIN
delete - [DELETE] /api/tags/{id} - Role: ADMIN
saveCourse - [POST] /api/wishlists/course/{courseId} - public api / authenticated
removeCourse - [DELETE] /api/wishlists/courses/{courseId} - Role: STUDENT
getMyWishlist - [GET] /api/wishlists - Role: STUDENT

### Learning Service
issueCertificate - [POST] /api/learning/certificates/issue - public api / authenticated
downloadCertificate - [GET] /api/learning/certificates/{certificateId}/download - Role: INSTRUCTOR, TEACHER
verifyCertificate - [GET] /api/learning/certificates/verify/{certificateCode} - Role: STUDENT
enrollCourse - [POST] /api/enrollments - public api / authenticated
getMyEnrolledCourses - [GET] /api/enrollments/my-courses - Role: STUDENT
createExam - [POST] /api/learning/exams - public api / authenticated
updateExamInfo - [PUT] /api/learning/exams/{examId} - Role: INSTRUCTOR, TEACHER
updateExamQuestions - [PUT] /api/learning/exams/{examId}/questions - Role: INSTRUCTOR, TEACHER
updateExamStatus - [PUT] /api/learning/exams/{examId}/status - Role: INSTRUCTOR, TEACHER
deleteExam - [DELETE] /api/learning/exams/{examId} - Role: INSTRUCTOR, TEACHER
getExamsByInstructor - [GET] /api/learning/exams - Role: INSTRUCTOR, TEACHER
getExamById - [GET] /api/learning/exams/{examId} - Role: INSTRUCTOR, TEACHER
startExam - [POST] /api/learning/exams/{examId}/attempts - Role: INSTRUCTOR, TEACHER
submitExam - [POST] /api/learning/exams/attempts/{attemptId}/submit - Role: STUDENT
getExamSubmissions - [GET] /api/learning/exams/{examId}/submissions - Role: STUDENT
gradeSubmission - [PUT] /api/learning/exams/submissions/{attemptId}/grade - Role: INSTRUCTOR, TEACHER
getExamResult - [GET] /api/learning/exams/attempts/{attemptId}/result - Role: INSTRUCTOR, TEACHER
getDueCards - [GET] /api/learning/flashcard-sets/{id}/due-cards - public api / authenticated
reviewFlashcard - [POST] /api/learning/flashcards/{id}/review - Role: STUDENT
getDashboardHistory - [GET] /api/learning/dashboard/history - Role: STUDENT
createFlashcardSet - [POST] /api/learning/flashcard-sets - public api / authenticated
cloneFlashcardSet - [POST] /api/learning/flashcard-sets/{id}/clone - Role: INSTRUCTOR, TEACHER
startLearning - [POST] /api/learning/courses/{courseId}/start - public api / authenticated
trackProgress - [POST] /api/learning/courses/{courseId}/lessons/{lessonId}/progress - Role: STUDENT, INSTRUCTOR, ADMIN
resumeLearning - [GET] /api/learning/courses/{courseId}/resume - Role: STUDENT
completeLesson - [POST] /api/learning/courses/{courseId}/lessons/{lessonId}/complete - Role: STUDENT
createBank - [POST] /api/learning/question-banks - public api / authenticated
updateBank - [PUT] /api/learning/question-banks/{bankId} - Role: INSTRUCTOR, TEACHER
deleteBank - [DELETE] /api/learning/question-banks/{bankId} - Role: INSTRUCTOR, TEACHER
getMyBanks - [GET] /api/learning/question-banks - Role: INSTRUCTOR, TEACHER
createQuestion - [POST] /api/learning/question-banks/{bankId}/questions - public api / authenticated
updateQuestion - [PUT] /api/learning/questions/{questionId} - Role: INSTRUCTOR, TEACHER
deleteQuestion - [DELETE] /api/learning/questions/{questionId} - Role: INSTRUCTOR, TEACHER
getQuestionsByBankId - [GET] /api/learning/question-banks/{bankId}/questions - Role: INSTRUCTOR, TEACHER
importVocabulary - [POST] /api/learning/vocabulary/import - public api / authenticated

### Payment Service
createCoupon - [POST] /api/payment/coupons - public api / authenticated
calculateDiscount - [POST] /api/payment/coupons/calculate - Role: ADMIN, INSTRUCTOR
checkout - [POST] /api/payment/orders/checkout - public api / authenticated
vnpayCallback - [GET] /api/payment/vnpay/callback - public api / authenticated
requestRefund - [POST] /api/payment/refunds - public api / authenticated
approveRefund - [POST] /api/payment/refunds/{id}/approve - Role: STUDENT

### Admin Service
getDashboard - [GET] /api/admin/dashboard - public api / authenticated
exportRevenueReport - [GET] /api/admin/reports/revenue/export - public api / authenticated
exportCourseReport - [GET] /api/admin/reports/course/export - public api / authenticated
