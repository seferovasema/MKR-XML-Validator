# MKR-XML-Validator
📊 StatisticsService – Yoxlayır və hesablayır

<Header> daxilində ReportingDate oxunur

<Credits> elementi varmı yoxlanılır

<Credit> elementi varmı yoxlanılır

Credit list (array və ya tək obyekt) formata salınır

Hesablamalar:

Ümumi kreditlərin sayı

DateOfGrant == ReportingDate olan kreditlərin sayı

CreditStatusCode == "001" olan (bağlanmış) kreditlərin sayı

🛡️ CreditValidatorService – Yoxlamalar
📁 Struktur yoxlamaları

<Credits> mövcuddurmu

<Credit> mövcuddurmu

Hər <Credit> daxilində <Borrower> varmı

👤 Borrower yoxlamaları

<id> boş olmamalıdır

👤 Fiziki şəxs üçün (şərt daxilində)

Əgər:

id 10 rəqəm deyil və ya

sonu 1 ilə bitmir

➡️ onda yoxlanılır:

<name> boş olmamalıdır

<DateOfBirth> boş olmamalıdır

<DateOfBirth> formatı düzgün olmalıdır (DD/MM/YYYY)

<PlaceOfBirth> boş olmamalıdır

<PinCode> boş olmamalıdır

💰 Kredit yoxlamaları

Əgər CreditStatusCode == "007" isə:

<DisoutAmountOfCredit> sıfır olmamalıdır

<DisoutAmountOfCredit> rəqəm olmalıdır

📅 Tarix yoxlaması

Əgər:

DateOfGrant == ReportingDate

və CreditStatusCode == "001"

➡️ kredit eyni gündə verilib və bağlanıb (warning)

📍 Texniki yoxlama

Hər xəta üçün XML-də line number (sətir nömrəsi) göstərilir

null və boş dəyərlər eyni kimi qəbul olunur

XML parsing xətaları tutulur

✅ Nəticə

Əgər xəta yoxdursa → "Bütün Credit-lər düzgün və validdir"

Əks halda → bütün xətalar list şəklində qaytarılır
