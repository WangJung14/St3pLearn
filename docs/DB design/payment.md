IV. Payment & Finance Service:

PAYMENT ORDER 

1.  payment\_orders
    

FieldType 

idUUID 

student\_idUUID 

course\_idUUID 

order\_numbervarchar 

original\_amountdecimal(18,2) 

discount\_amountdecimal(18,2) 

final\_amountdecimal(18,2) 

currencyvarchar(10) 

statusvarchar 

created\_attimestamp 

updated\_attimestamp 

\*\*\*status : CREATED,PENDING\_PAYMENT,PAID,FAILED,CANCELLED,REFUNDED

\*\*\*unique : order\_number UNIQUE

1.  payment\_transactions: 
    

*   1 order có thể có nhiều transaction
    
*   example : Lần 1 thanh toán -> timeout->Lần 2 thanh toán->thành công
    

FieldType 

idUUID 

payment\_order\_idUUID 

gatewayvarchar 

gateway\_transaction\_idvarchar 

amountdecimal(18,2) 

statusvarchar 

request\_idempotency\_keyvarchar 

created\_attimestamp 

completed\_attimestamp

\*\*\* status: PENDING,SUCCESS,FAILED,CANCELLED

\*\*\*unique: request\_idempotency\_key UNIQUE

1.  invoices
    

Field

id

payment\_order\_id

invoice\_number

issue\_date

invoice\_url

Unique : invoice\_number

1.  coupons:
    

FieldType 

idUUID 

course\_id UUID

codevarchar 

discount\_typevarchar 

discount\_valuedecimal 

max\_discountdecimal 

usage\_limitint 

used\_countint 

start\_datetimestamp 

end\_datetimestamp 

is\_activebool

\*\*\*discount\_type:PERCENTAGE,FIXED\_AMOUNT

1.  coupon\_usages
    

Field

idcoupon\_id

student\_id

payment\_order\_id

used\_at

\*\*\* unique: (coupon\_id, student\_id)

\*\*\* rule : coupon chỉ dùng 1 lần

1.  refund\_requests
    

*   1 order có thể có nhiều refund(60% giá gốc)
    

FieldType 

idUUID 

payment\_order\_idUUID 

student\_idUUID 

refund\_amountdecimal(18,2) 

reasontext 

statusvarchar 

requested\_attimestamp 

processed\_attimestamp

\*\*\*status : REQUESTED ,APPROVED,REJECTED,PROCESSING,COMPLETED

1.  payment\_gateway\_logs
    

Field

id

transaction\_id

gateway

request\_payload

response\_payload

created\_at

\*\*\*jsonb {

  "gateway":"VNPay",

  "responseCode":"00"

}

1.  payment\_outbox\_events
    

Field

id

aggregate\_id

event\_type

payload

status

created\_at

Domain Events

1.  Checkout : Fieldidaggregate\_idevent\_typepayloadstatuscreated\_at
    
2.  Payment : PaymentStarted,PaymentSucceeded,PaymentFailed
    
3.  Coupon : CouponApplied,CouponExpired
    
4.  Refund: RefundRequested,RefundApproved,RefundCompleted,RefundReject