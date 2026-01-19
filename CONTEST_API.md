# Contest API Documentation

## Overview
Contest system allows users to participate in programming competitions, solve problems, and earn ratings.

## Features
- Create and manage contests (Admin only)
- Register for contests
- Submit solutions during active contests
- Real-time standings and leaderboards
- Rating system with automatic calculation
- Problem statistics (attempts, solved count, etc.)
- Penalty system for wrong submissions
- Prize pool support

## Database Schema
The contest system uses 4 main tables:
- `contests` - Contest information
- `contest_problems` - Problems in each contest
- `contest_participants` - User registrations and scores
- `contest_submissions` - All submissions during contests

## Contest Status Flow
1. **UPCOMING** - Contest not started yet, users can register
2. **ACTIVE** - Contest is running, users can submit solutions
3. **FINISHED** - Contest ended, standings are final
4. **REGISTERED** - User-specific status showing they registered

## Endpoints

### 1. Create Contest (Admin only)
**POST** `/api/contests`

**Headers:**
```
Authorization: Bearer <admin_token>
```

**Request Body:**
```json
{
  "number": "Contest #1",
  "title": "Weekly Contest 1",
  "description": "First weekly programming contest",
  "imageUrl": "https://example.com/contest-image.jpg",
  "startTime": "2026-01-20T10:00:00",
  "durationSeconds": 7200,
  "prizePool": "[{\"place\": \"1st\", \"prize\": \"$500\"}, {\"place\": \"2nd\", \"prize\": \"$300\"}]",
  "problems": [
    {
      "problemId": 1,
      "symbol": "A",
      "points": 500,
      "orderIndex": 0
    },
    {
      "problemId": 2,
      "symbol": "B",
      "points": 1000,
      "orderIndex": 1
    }
  ]
}
```

**Response:**
```json
{
  "id": "1",
  "number": "Contest #1",
  "title": "Weekly Contest 1",
  "description": "First weekly programming contest",
  "imageUrl": "https://example.com/contest-image.jpg",
  "startTime": "2026-01-20T10:00:00",
  "durationSeconds": 7200,
  "problemCount": 2,
  "participantsCount": 0,
  "prizePool": [
    {"place": "1st", "prize": "$500"},
    {"place": "2nd", "prize": "$300"}
  ],
  "status": "upcoming"
}
```

### 2. Get All Contests
**GET** `/api/contests`

**Headers (Optional):**
```
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": "1",
    "number": "Contest #1",
    "title": "Weekly Contest 1",
    "description": "First weekly programming contest",
    "imageUrl": "https://example.com/contest-image.jpg",
    "startTime": "2026-01-20T10:00:00",
    "durationSeconds": 7200,
    "problemCount": 3,
    "participantsCount": 25,
    "prizePool": [...],
    "status": "registered"
  }
]
```

### 3. Get Contest by ID
**GET** `/api/contests/{contestId}`

**Headers (Optional):**
```
Authorization: Bearer <token>
```

**Response:** Same as single contest object above

### 4. Register for Contest
**POST** `/api/contests/{contestId}/register`

**Headers:**
```
Authorization: Bearer <token>
```

**Response:** 200 OK

**Errors:**
- 400 - Already registered
- 400 - Cannot register for started contest
- 404 - Contest not found

### 5. Get Contest Problems
**GET** `/api/contests/{contestId}/problems`

**Headers (Optional):**
```
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": 1,
    "problemId": 1,
    "problemTitle": "Two Sum",
    "symbol": "A",
    "ball": 500,
    "attemptsCount": 150,
    "solved": 80,
    "unsolved": 70,
    "attemptUsersCount": 45,
    "isSolved": true,
    "isAttempted": true,
    "delta": 0.53
  }
]
```

### 6. Submit Solution
**POST** `/api/contests/submit`

**Headers:**
```
Authorization: Bearer <token>
```

**Request Body:**
```json
{
  "contestId": 1,
  "problemId": 1,
  "code": "function twoSum(nums, target) { ... }",
  "language": "javascript"
}
```

**Response:** 200 OK

**Errors:**
- 400 - Contest is not active
- 400 - Not registered for this contest
- 404 - Problem not in contest

### 7. Get Contest Standings
**GET** `/api/contests/{contestId}/standings`

**Response:**
```json
[
  {
    "rank": 1,
    "userId": 123,
    "username": "john_doe",
    "avatarUrl": "https://example.com/avatar.jpg",
    "score": 2500,
    "problemsSolved": 3,
    "totalPenalty": 3600,
    "ratingChange": 50,
    "totalRating": 1550,
    "problems": [
      {
        "symbol": "A",
        "solved": true,
        "attempts": 1,
        "score": 500,
        "timeTaken": 600
      },
      {
        "symbol": "B",
        "solved": true,
        "attempts": 2,
        "score": 1000,
        "timeTaken": 1800
      },
      {
        "symbol": "C",
        "solved": true,
        "attempts": 1,
        "score": 1500,
        "timeTaken": 1200
      }
    ]
  }
]
```

### 8. Finalize Contest (Admin only)
**POST** `/api/contests/{contestId}/finalize`

**Headers:**
```
Authorization: Bearer <admin_token>
```

**Response:** 200 OK

**Description:** Calculates final rankings and rating changes for all participants.

**Errors:**
- 400 - Contest is not finished yet
- 404 - Contest not found

## Scoring System

### Points
- Each problem has a fixed point value (e.g., 500, 1000, 1500)
- Users earn full points for accepted solutions
- No partial credit for wrong submissions

### Penalty System
- Time penalty = seconds from contest start to accepted submission
- Wrong submission penalty = 5 minutes (300 seconds) per wrong attempt
- Total penalty = time penalty + (wrong attempts × 300)

### Ranking
Participants are ranked by:
1. Total score (descending)
2. Total penalty (ascending)
3. Problems solved (descending)

### Rating Calculation
After contest finalization:
- Top 10%: +50 to +100 rating
- Top 25%: +30 to +50 rating
- Top 50%: +10 to +30 rating
- Top 75%: 0 rating change
- Bottom 25%: -10 rating

## Implementation Details

### Key Classes
- `Contest` - Main contest entity
- `ContestProblem` - Problem in a contest
- `ContestParticipant` - User registration and score
- `ContestSubmission` - Submission during contest
- `ContestService` - Business logic
- `ContestController` - REST endpoints

### Automatic Status Updates
Contest status is automatically updated based on current time:
- Before start time → UPCOMING
- Between start and end time → ACTIVE
- After end time → FINISHED

### Statistics Tracking
The system automatically tracks:
- Total attempts per problem
- Solved/unsolved counts
- Unique users who attempted
- Delta (solve rate) for each problem

## Testing
Use the `test-contest.http` file to test all endpoints with sample data.

## Notes
- Users must register before the contest starts
- Submissions are only accepted during active contests
- Each submission is also saved in the regular submissions table
- Rating changes are only calculated after admin finalizes the contest
- Contest problems can be any existing problem in the system