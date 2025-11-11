# StudyGroupAppProject

학습 강의 수강과 커뮤니티, 퀴즈 기능을 결합한 안드로이드 교육 앱입니다. `Java` 기반으로 작성되었고 오프라인 `SQLite` 저장소와 `SharedPreferences`를 통해 사용자 및 학습 데이터를 관리합니다.

## 주요 기능

- **스플래시 & 온보딩**: `SplashActivity`에서 로고를 노출한 뒤 `MainActivity`로 진입합니다.
- **회원 인증**: `SignupActivity`에서 사용자 정보를 수집하고 `UserDatabaseHelper`를 통해 `users` 테이블에 저장합니다. `LoginActivity`는 입력 계정 검증 후 `SharedPreferences(user_prefs)`에 `user_id`를 보관합니다.
- **홈 화면**: 광고 배너(`ImageAdapter`), 강의/커뮤니티 바로가기, `PostDatabaseHelper.getTopViewedPosts()`를 이용한 인기 게시글 목록을 제공합니다.
- **강의 카테고리 & 언어**: `CategoryActivity`의 그리드 → `LanguageActivity`의 ViewPager2가 언어별 강의를 노출합니다. 강의 아이템 클릭 시 상세 화면으로 이동합니다.
- **강의 상세/수강 관리**: `BeomguActivity`에서 수강 신청(테이블 `UserCourses`) 및 강의 목록을 보여주고, 신청된 경우 `LectureDetailActivity`에서 동영상 재생(`VideoView`/`FullScreenVideoActivity`)과 평점(`RatingDatabaseHelper`) 기록이 가능합니다.
- **커뮤니티 게시판**: `CommunityActivity` + `BoardFragment` 조합으로 탭별 게시글을 조회하며, `PostWriteActivity`를 통해 게시글을 작성하고 `PostDetailActivity`에서 조회수 증가·댓글(`CommentDatabaseHelper`)을 처리합니다.
- **퀴즈 시스템**: `QuizActivity`는 수강 중인 강의가 있을 때만 `ClassquizActivity`로 진입하도록 제한하며, 주관/객관/단답형 문제 채점 결과는 `ResultActivity`에서 확인합니다.
- **마이페이지**: `MyPageActivity`가 `UserCourses` 정보를 이용해 사용자의 수강 중인 강의를 카드 형태로 노출합니다.

## 아키텍처 개요

```
app/
 └── src/main/java/com/example/project/
     ├── activities (각 Activity 파일)
     ├── fragments (BoardFragment, LanguageFragments 등)
     ├── adapters (RecyclerView/ListView 어댑터)
     ├── db (SQLiteOpenHelper 기반 헬퍼)
     └── models (Post, Comment, LanguageItem 등)
```

- 화면은 `Activity` 중심으로 구성되며 일부 섹션은 `Fragment`를 활용합니다.
- UI는 `res/layout`의 XML과 `RecyclerView`/`ListView` 어댑터로 구성됩니다.
- 데이터 계층은 개별 `SQLiteOpenHelper` 구현(`UserDatabaseHelper`, `PostDatabaseHelper`, `CommentDatabaseHelper`, `RatingDatabaseHelper`)으로 분리돼 있어 기능별 DB 스키마를 독립적으로 유지합니다.

## 데이터 저장소

| 영역                     | 주요 테이블/키                     | 필드                                                               |
| ------------------------ | ---------------------------------- | ------------------------------------------------------------------ |
| 사용자(`users.db`)       | `users`                            | `id(PK)`, `name`, `password`, `birth`, `gender`                    |
|                          | `UserCourses`                      | `id(PK)`, `userId`, `courseName`                                   |
| 커뮤니티(`community.db`) | `posts`                            | `id(PK)`, `title`, `content`, `category`, `date`, `views`, `likes` |
| 댓글(`comments.db`)      | `comments`                         | `id(PK)`, `post_id`, `content`, `date`                             |
| 평점(`ratings.db`)       | `ratings`                          | `lectureNumber(PK)`, `totalRating`, `ratingCount`                  |
| 로컬 설정                | `SharedPreferences` (`user_prefs`) | `user_id` (현재 로그인 사용자)                                     |

## 빌드 및 실행

1. Android Studio Hedgehog 이상에서 `Open an Existing Project`로 본 디렉터리를 불러옵니다.
2. Gradle Sync 완료 후 에뮬레이터(또는 minSdk 24 이상의 기기)를 연결합니다.
3. `app` 모듈을 실행하면 `SplashActivity` → `MainActivity` 순으로 앱이 시작됩니다.

### 개발 환경

- Compile SDK: 35 / Target SDK: 34 / Min SDK: 24
- 언어: Java 11
- 주요 의존성: AndroidX AppCompat, Material Components, RecyclerView, ViewPager2

## 테스트

- 단위/계측 테스트 템플릿은 `app/src/test`와 `app/src/androidTest`에 기본 예제로만 존재합니다. 실제 기능 검증을 위해서는 `Espresso`나 `Robolectric` 기반 시나리오 테스트 추가가 필요합니다.

## 알려진 이슈 및 개선 사항

- `LoginActivity`에서 `dbHelper.checkUser()` 호출이 중복되어 불필요한 Toast/전환이 두 번 실행됩니다.
- `PostWriteActivity`의 카테고리 스피너는 문자열 리소스에 `HOT` 항목이 없어 선택할 수 없습니다.
- `HomeActivity`/`ImageAdapter`와 언어 `Fragment`에서 참조하는 `ad1/ad2/ad3`, `tc7~tc20` 등의 리소스가 `res/drawable`에 존재하지 않아 빌드 에러를 유발합니다.
- 다중 DB 파일을 사용하지만 마이그레이션 전략이 단순 드롭&크리에이트라 앱 업데이트 시 데이터가 초기화될 수 있습니다.
- 사용자 인증 정보가 평문 비밀번호로 저장되므로 보안 강화를 위해 해시 처리 및 서버 연동이 요구됩니다.

## 추가 개선 아이디어

- 강의/게시글 데이터를 서버 연동 구조로 확장하고, Retrofit + Room 기반으로 리팩터링.
- 커뮤니티 게시글에 이미지 업로드, 좋아요 기능 등 인터랙션 추가.
- 퀴즈 문제를 동적으로 불러오고, 결과를 사용자별로 저장하는 리더보드 도입.
