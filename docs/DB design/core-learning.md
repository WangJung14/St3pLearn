III. Core learning service

1.  Enrollment & Learning 
    
    1.  enrollments:
        

idUUID 

student\_idUUID 

course\_idUUID 

enrolled\_attimestamp 

completed\_attimestamp 

statusvarchar 

progress\_percentdecimal 

last\_accessed\_lesson\_idUUID 

last\_accessed\_attimestamp

\*\*\* status : ACTIVE , COMPLETED, DROPPED, EXPIRED

\*\*\* unique: (student\_id, course\_id)

\*\*\* 1 học viên chỉ được enroll 1 lần

1.  learning\_progress
    

*   Progress tổng khóa học:
    

enrollment\_idUUID 

total\_lessonsint 

completed\_lessonsint 

total\_durationint 

watched\_durationint 

progress\_percentdecimal 

updated\_attimestamp

1.  lesson\_progress 
    

FieldType 

idUUID 

enrollment\_idUUID 

lesson\_idUUID 

statusvarchar 

watch\_position\_secondsint 

completed\_attimestamp 

updated\_attimestamp

\*\*\* status: NOT\_STARTED,IN\_PROGRESS,COMPLETE

1.  Assessment
    
2.  question\_banks
    

Field

id

course\_id

title

created\_by

\*\*\*exmaple : IELTS Reading, TOEIC Part 5,Grammar Quiz 

1.  questions
    

FieldType 

idUUID 

bank\_idUUID 

question\_typevarchar 

audio\_urltext

contenttext 

explanationtext 

difficultyvarchar 

pointsdecimal

\*\*\* question\_type: SINGLE\_CHOICE,MULTIPLE\_CHOICE,TRUE\_FALSE,FILL\_BLANK,ESSAY,SPEAKING,

1.  question\_options
    

id

question\_id

content

is\_correct

1.  exams
    

FieldType 

idUUID 

course\_idUUID 

titlevarchar 

duration\_minutesint 

passing\_score decimal 

total\_scoredecimal 

created\_attimestamp

1.  exam\_questions 
    

Field

exam\_id

question\_id

display\_order

1.  exam\_attempts
    

FieldType 

idUUID 

exam\_idUUID 

student\_idUUID 

started\_attimestamp 

submitted\_attimestamp 

scoredecimal 

passed bool 

statusvarchar

\*\*\*status : STARTED,SUBMITTED,GRADED,EXPIRED

1.  exam\_submissions
    

*   Chi tiết từng câu trả lời 
    

Field

attempt\_id

question\_id

answer\_json

score

graded\_at

\*\*\*answer\_json: {

 "selectedOptions":\[1,3\],

"audioUrl": "https://s3.../record\_123.mp3" 

}

\*\*\*Essay: {

 "answer":"..."

}

3\. Vocabulary Learning:

1.  flashcard\_sets 
    

id 

course\_id

title

\*\*\*example : 1000 TOEIC words

1.  flashcards
    

id

set\_id

word

meaning

example 

pronunciation

1.  flashcard\_reviews 
    

FieldType 

idUUID 

flashcard\_idUUID 

interval int 

repetitionsint 

ease\_factordecimal

student\_idUUID 

review\_resultvarchar 

next\_review\_attimestamp 

reviewed\_attimestamp

\*\*\*review\_result: AGAIN,HARD,GOOD,EASY

thuật toán triển khai : SM-2,Anki Style Algorithm

4\. Certificate

1.  certificates FieldType 
    

idUUID 

student\_idUUID 

course\_idUUID 

certificate\_numbervarchar 

issue\_datetimestamp 

verification\_codevarchar 

certificate\_urltext

Unique:(student\_id, course\_id) 

rule : 1 khóa học chỉ có 1 certificate

Domain Event:

1.  Enrollment
    

StudentEnrolled,CourseCompleted,EnrollmentCancelled

1.  Learning
    

LessonStarted,LessonCompleted,LearningProgressUpdated

1.  Assessment:
    

ExamStarted,ExamSubmitted,ExamPassed,ExamFailed

1.  Vocabulary:
    

FlashcardReviewed, ReviewScheduleGenerated

1.  Certificate:
    

CertificateIssued,CertificateRevoked