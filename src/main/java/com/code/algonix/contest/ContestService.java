package com.code.algonix.contest;

import com.code.algonix.contest.dto.*;
import com.code.algonix.exception.InvalidInputException;
import com.code.algonix.exception.ResourceNotFoundException;
import com.code.algonix.problems.*;
import com.code.algonix.problems.dto.SubmissionRequest;
import com.code.algonix.problems.dto.SubmissionResponse;
import com.code.algonix.user.UserEntity;
import com.code.algonix.user.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestService {
    
    private final ContestRepository contestRepository;
    private final ContestProblemRepository contestProblemRepository;
    private final ContestParticipantRepository participantRepository;
    private final ContestSubmissionRepository contestSubmissionRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionService submissionService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public ContestResponse createContest(CreateContestRequest request) {
        Contest contest = new Contest();
        contest.setNumber(request.getNumber());
        contest.setTitle(request.getTitle());
        contest.setDescription(request.getDescription());
        contest.setImageUrl(request.getImageUrl());
        contest.setStartTime(request.getStartTime());
        contest.setDurationSeconds(request.getDurationSeconds());
        contest.setPrizePool(request.getPrizePool());
        contest.setStatus(Contest.ContestStatus.UPCOMING);
        contest.setProblemCount(request.getProblems() != null ? request.getProblems().size() : 0);
        
        contest = contestRepository.save(contest);
        
        if (request.getProblems() != null) {
            for (CreateContestRequest.ContestProblemRequest pr : request.getProblems()) {
                Problem problem = problemRepository.findById(pr.getProblemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
                
                // Masalani contest bilan bog'lash - contestId ni olib tashladik
                // Endi masala va contest ContestProblem orqali bog'lanadi
                
                ContestProblem cp = new ContestProblem();
                cp.setContest(contest);
                cp.setProblem(problem);
                cp.setSymbol(pr.getSymbol());
                cp.setPoints(pr.getPoints());
                cp.setOrderIndex(pr.getOrderIndex());
                contestProblemRepository.save(cp);
            }
        }
        
        return mapToResponse(contest, null);
    }
    
    public List<ContestResponse> getAllContests(int page, int size, Long userId) {
        List<Contest> contests = contestRepository.findAll();
        contests.forEach(this::updateContestStatus);
        
        // Apply pagination
        int start = page * size;
        int end = Math.min(start + size, contests.size());
        
        if (start >= contests.size()) {
            return new ArrayList<>();
        }
        
        return contests.subList(start, end).stream()
                .map(c -> mapToResponse(c, userId))
                .collect(Collectors.toList());
    }
    
    public ContestResponse getContestById(Long contestId, Long userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        
        updateContestStatus(contest);
        return mapToResponse(contest, userId);
    }
    
    @Transactional
    public void registerForContest(Long contestId, Long userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        
        if (contest.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidInputException("Cannot register for started contest");
        }
        
        if (participantRepository.existsByContestIdAndUserId(contestId, userId)) {
            throw new InvalidInputException("Already registered");
        }
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        ContestParticipant participant = new ContestParticipant();
        participant.setContest(contest);
        participant.setUser(user);
        participant.setRegisteredAt(LocalDateTime.now());
        participantRepository.save(participant);
        
        contest.setParticipantsCount(contest.getParticipantsCount() + 1);
        contestRepository.save(contest);
    }

    
    public List<ContestProblemResponse> getContestProblems(Long contestId, Long userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        
        List<ContestProblem> problems = contestProblemRepository.findByContestIdOrderByOrderIndexAsc(contestId);
        
        return problems.stream()
                .map(cp -> {
                    ContestProblemResponse response = new ContestProblemResponse();
                    response.setId(cp.getId());
                    response.setProblemId(cp.getProblem().getId());
                    response.setProblemTitle(cp.getProblem().getTitle());
                    response.setSymbol(cp.getSymbol());
                    response.setBall(cp.getPoints());
                    response.setAttemptsCount(cp.getAttemptsCount());
                    response.setSolved(cp.getSolvedCount());
                    response.setUnsolved(cp.getUnsolvedCount());
                    response.setAttemptUsersCount(cp.getAttemptUsersCount());
                    response.setDelta(cp.getDelta());
                    
                    if (userId != null) {
                        boolean solved = contestSubmissionRepository.existsByContestIdAndUserIdAndContestProblemIdAndIsAcceptedTrue(
                                contestId, userId, cp.getId());
                        response.setIsSolved(solved);
                        
                        List<ContestSubmission> userSubmissions = contestSubmissionRepository
                                .findUserProblemSubmissions(contestId, userId, cp.getId());
                        response.setIsAttempted(!userSubmissions.isEmpty());
                    } else {
                        response.setIsSolved(false);
                        response.setIsAttempted(false);
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void submitSolution(ContestSubmitRequest request, Long userId) {
        Contest contest = contestRepository.findById(request.getContestId())
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = contest.getStartTime().plusSeconds(contest.getDurationSeconds());
        
        if (now.isBefore(contest.getStartTime()) || now.isAfter(endTime)) {
            throw new InvalidInputException("Contest is not active");
        }
        
        if (!participantRepository.existsByContestIdAndUserId(request.getContestId(), userId)) {
            throw new InvalidInputException("Not registered for this contest");
        }
        
        ContestProblem contestProblem = contestProblemRepository
                .findByContestIdAndProblemId(request.getContestId(), request.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not in contest"));
        
        // Submit to regular submission system
        SubmissionRequest submitRequest = new SubmissionRequest();
        submitRequest.setProblemId(request.getProblemId());
        submitRequest.setCode(request.getCode());
        submitRequest.setLanguage(request.getLanguage());
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        SubmissionResponse submissionResponse = submissionService.submitCode(submitRequest, user.getUsername());
        Submission submission = submissionRepository.findById(submissionResponse.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        
        // Create contest submission
        ContestSubmission contestSubmission = new ContestSubmission();
        contestSubmission.setContest(contest);
        contestSubmission.setContestProblem(contestProblem);
        contestSubmission.setUser(user);
        contestSubmission.setSubmission(submission);
        contestSubmission.setSubmittedAt(now);
        contestSubmission.setIsAccepted(submission.getStatus() == Submission.SubmissionStatus.ACCEPTED);
        
        long timeTaken = java.time.Duration.between(contest.getStartTime(), now).getSeconds();
        contestSubmission.setTimeTaken(timeTaken);
        
        if (submission.getStatus() == Submission.SubmissionStatus.ACCEPTED) {
            contestSubmission.setScore(contestProblem.getPoints());
        } else {
            contestSubmission.setScore(0);
        }
        
        contestSubmissionRepository.save(contestSubmission);
        
        // Update statistics
        updateContestProblemStats(contestProblem.getId());
        updateParticipantScore(request.getContestId(), userId);
    }
    
    public List<ContestStandingsResponse> getContestStandings(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        
        List<ContestParticipant> participants = participantRepository.findContestStandings(contestId);
        List<ContestProblem> problems = contestProblemRepository.findByContestIdOrderByOrderIndexAsc(contestId);
        
        return participants.stream()
                .map(p -> {
                    ContestStandingsResponse response = new ContestStandingsResponse();
                    response.setRank(p.getRank());
                    response.setUserId(p.getUser().getId());
                    response.setUsername(p.getUser().getUsername());
                    response.setAvatarUrl(p.getUser().getAvatarUrl());
                    response.setScore(p.getScore());
                    response.setProblemsSolved(p.getProblemsSolved());
                    response.setTotalPenalty(p.getTotalPenalty());
                    response.setRatingChange(p.getRatingChange());
                    
                    // Calculate total rating from all contests
                    List<ContestParticipant> userHistory = participantRepository.findUserContestHistory(p.getUser().getId());
                    int totalRating = userHistory.stream()
                            .filter(cp -> cp.getRatingChange() != null)
                            .mapToInt(ContestParticipant::getRatingChange)
                            .sum();
                    response.setTotalRating(totalRating);
                    
                    // Get problem results
                    List<ContestStandingsResponse.ProblemResult> problemResults = problems.stream()
                            .map(cp -> {
                                List<ContestSubmission> submissions = contestSubmissionRepository
                                        .findUserProblemSubmissions(contestId, p.getUser().getId(), cp.getId());
                                
                                ContestStandingsResponse.ProblemResult pr = new ContestStandingsResponse.ProblemResult();
                                pr.setSymbol(cp.getSymbol());
                                pr.setAttempts(submissions.size());
                                
                                Optional<ContestSubmission> accepted = submissions.stream()
                                        .filter(ContestSubmission::getIsAccepted)
                                        .findFirst();
                                
                                if (accepted.isPresent()) {
                                    pr.setSolved(true);
                                    pr.setScore(accepted.get().getScore());
                                    pr.setTimeTaken(accepted.get().getTimeTaken());
                                } else {
                                    pr.setSolved(false);
                                    pr.setScore(0);
                                    pr.setTimeTaken(null);
                                }
                                
                                return pr;
                            })
                            .collect(Collectors.toList());
                    
                    response.setProblems(problemResults);
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ContestFinalResultsResponse finalizeContestWithResults(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = contest.getStartTime().plusSeconds(contest.getDurationSeconds());
        
        // Contest tugaganligini tekshirish
        if (now.isBefore(endTime)) {
            throw new InvalidInputException("Contest is still active. Cannot finalize yet.");
        }
        
        // Barcha ishtirokchilarning natijalarini yangilash
        updateAllParticipantScores(contestId);
        
        // Ishtirokchilarni ball bo'yicha tartiblash
        List<ContestParticipant> participants = participantRepository.findContestStandings(contestId);
        
        // O'rinlarni belgilash (bir xil ball bo'lsa, kam penalty bo'lgan yuqorida)
        assignRanksToParticipants(participants);
        
        // Rating o'zgarishlarini hisoblash
        calculateRatingChanges(participants);
        
        // Ma'lumotlar bazasiga saqlash
        participantRepository.saveAll(participants);
        
        // Contest statusini FINALIZED ga o'zgartirish
        contest.setStatus(Contest.ContestStatus.FINISHED);
        contestRepository.save(contest);
        
        // Contest-only masalalarni public qilish
        publishContestProblems(contestId);
        
        // Natijalarni qaytarish
        return buildFinalResultsResponse(contest, participants, now);
    }
    
    private void updateAllParticipantScores(Long contestId) {
        List<ContestParticipant> participants = participantRepository.findByContestIdOrderByRegisteredAtAsc(contestId);
        
        for (ContestParticipant participant : participants) {
            updateParticipantScore(contestId, participant.getUser().getId());
        }
    }
    
    private void assignRanksToParticipants(List<ContestParticipant> participants) {
        // Participants allaqachon ball bo'yicha tartiblangan (findContestStandings)
        int currentRank = 1;
        
        for (int i = 0; i < participants.size(); i++) {
            ContestParticipant current = participants.get(i);
            
            if (i > 0) {
                ContestParticipant previous = participants.get(i - 1);
                
                // Agar ball va penalty bir xil bo'lsa, bir xil o'rin
                if (!current.getScore().equals(previous.getScore()) || 
                    !current.getTotalPenalty().equals(previous.getTotalPenalty())) {
                    currentRank = i + 1;
                }
            }
            
            current.setRank(currentRank);
        }
    }
    
    private void calculateRatingChanges(List<ContestParticipant> participants) {
        int totalParticipants = participants.size();
        
        for (ContestParticipant participant : participants) {
            int ratingChange = calculateRatingChange(
                participant.getRank(), 
                totalParticipants, 
                participant.getScore()
            );
            participant.setRatingChange(ratingChange);
        }
    }
    
    private ContestFinalResultsResponse buildFinalResultsResponse(
            Contest contest, 
            List<ContestParticipant> participants, 
            LocalDateTime finalizedAt) {
        
        ContestFinalResultsResponse response = new ContestFinalResultsResponse();
        response.setContestId(contest.getId());
        response.setContestTitle(contest.getTitle());
        response.setFinalizedAt(finalizedAt);
        response.setTotalParticipants(participants.size());
        
        List<ContestFinalResultsResponse.FinalParticipantResult> results = participants.stream()
                .map(participant -> buildParticipantResult(participant, contest.getId()))
                .collect(Collectors.toList());
        
        response.setResults(results);
        return response;
    }
    
    private ContestFinalResultsResponse.FinalParticipantResult buildParticipantResult(
            ContestParticipant participant, Long contestId) {
        
        ContestFinalResultsResponse.FinalParticipantResult result = 
                new ContestFinalResultsResponse.FinalParticipantResult();
        
        result.setRank(participant.getRank());
        result.setUserId(participant.getUser().getId());
        result.setUsername(participant.getUser().getUsername());
        result.setFirstName(participant.getUser().getFirstName());
        result.setLastName(participant.getUser().getLastName());
        result.setAvatarUrl(participant.getUser().getAvatarUrl());
        result.setTotalScore(participant.getScore());
        result.setProblemsSolved(participant.getProblemsSolved());
        result.setTotalPenalty(participant.getTotalPenalty());
        result.setRatingChange(participant.getRatingChange());
        
        // Jami ratingni hisoblash
        List<ContestParticipant> userHistory = participantRepository.findUserContestHistory(participant.getUser().getId());
        int totalRating = userHistory.stream()
                .filter(cp -> cp.getRatingChange() != null)
                .mapToInt(ContestParticipant::getRatingChange)
                .sum();
        result.setNewTotalRating(totalRating);
        
        // Masala natijalarini olish
        List<ContestFinalResultsResponse.ProblemResult> problemResults = 
                buildProblemResults(contestId, participant.getUser().getId());
        result.setProblemResults(problemResults);
        
        return result;
    }
    
    private List<ContestFinalResultsResponse.ProblemResult> buildProblemResults(Long contestId, Long userId) {
        List<ContestProblem> problems = contestProblemRepository.findByContestIdOrderByOrderIndexAsc(contestId);
        
        return problems.stream()
                .map(cp -> {
                    List<ContestSubmission> submissions = contestSubmissionRepository
                            .findUserProblemSubmissions(contestId, userId, cp.getId());
                    
                    ContestFinalResultsResponse.ProblemResult pr = 
                            new ContestFinalResultsResponse.ProblemResult();
                    
                    pr.setProblemSymbol(cp.getSymbol());
                    pr.setProblemTitle(cp.getProblem().getTitle());
                    pr.setPoints(cp.getPoints());
                    pr.setAttempts(submissions.size());
                    
                    Optional<ContestSubmission> accepted = submissions.stream()
                            .filter(ContestSubmission::getIsAccepted)
                            .findFirst();
                    
                    if (accepted.isPresent()) {
                        pr.setSolved(true);
                        pr.setTimeTaken(accepted.get().getTimeTaken());
                        
                        // Noto'g'ri urinishlar uchun penalty
                        long wrongAttempts = submissions.stream()
                                .filter(s -> !s.getIsAccepted() && 
                                       s.getSubmittedAt().isBefore(accepted.get().getSubmittedAt()))
                                .count();
                        pr.setPenalty((int) (wrongAttempts * 300)); // 5 daqiqa = 300 sekund
                    } else {
                        pr.setSolved(false);
                        pr.setTimeTaken(null);
                        pr.setPenalty(0);
                    }
                    
                    return pr;
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void finalizeContest(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        
        if (contest.getStatus() != Contest.ContestStatus.FINISHED) {
            throw new InvalidInputException("Contest is not finished yet");
        }
        
        List<ContestParticipant> participants = participantRepository.findContestStandings(contestId);
        
        // Assign ranks
        for (int i = 0; i < participants.size(); i++) {
            participants.get(i).setRank(i + 1);
        }
        
        // Calculate rating changes (simple algorithm)
        for (ContestParticipant p : participants) {
            int ratingChange = calculateRatingChange(p.getRank(), participants.size(), p.getScore());
            p.setRatingChange(ratingChange);
        }
        
        participantRepository.saveAll(participants);
        
        // Publish contest-only problems to public
        publishContestProblems(contestId);
    }
    
    public ContestRankingsResponse getContestRankings(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        
        // Barcha ishtirokchilarning natijalarini yangilash
        updateAllParticipantScores(contestId);
        
        // Ball bo'yicha tartiblangan ro'yxat
        List<ContestParticipant> participants = participantRepository.findContestStandings(contestId);
        
        // Vaqtinchalik o'rinlarni belgilash (finalize qilinmagan bo'lsa)
        assignRanksToParticipants(participants);
        
        ContestRankingsResponse response = new ContestRankingsResponse();
        response.setContestId(contest.getId());
        response.setContestTitle(contest.getTitle());
        response.setStatus(contest.getStatus().name());
        response.setTotalParticipants(participants.size());
        
        List<ContestRankingsResponse.RankingEntry> rankings = participants.stream()
                .map(p -> {
                    ContestRankingsResponse.RankingEntry entry = new ContestRankingsResponse.RankingEntry();
                    entry.setRank(p.getRank());
                    entry.setUserId(p.getUser().getId());
                    entry.setUsername(p.getUser().getUsername());
                    entry.setAvatarUrl(p.getUser().getAvatarUrl());
                    entry.setTotalScore(p.getScore());
                    entry.setProblemsSolved(p.getProblemsSolved());
                    entry.setTotalPenalty(p.getTotalPenalty());
                    entry.setRatingChange(p.getRatingChange()); // null bo'lishi mumkin
                    return entry;
                })
                .collect(Collectors.toList());
        
        response.setRankings(rankings);
        return response;
    }
    
    public List<ContestParticipantResponse> getContestParticipants(Long contestId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest not found"));
        
        List<ContestParticipant> participants = participantRepository.findByContestIdOrderByRegisteredAtAsc(contestId);
        
        return participants.stream()
                .map(this::mapToParticipantResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void publishContestProblems(Long contestId) {
        // Contest uchun yaratilgan masalalarni topish
        List<Problem> contestProblems = problemRepository.findContestOnlyProblemsByContestId(contestId);
        
        // Har bir masalani public qilish
        for (Problem problem : contestProblems) {
            problem.setPublishTime(LocalDateTime.now());
            problemRepository.save(problem);
        }
    }
    
    @Transactional
    public void linkProblemsToContest(Long contestId, List<Long> problemIds) {
        // Contest masalalarini contest bilan bog'lash - contestId ni olib tashladik
        // Endi masala va contest ContestProblem orqali bog'lanadi
        // Bu method endi ishlatilmaydi
    }
    
    private int calculateRatingChange(int rank, int totalParticipants, int score) {
        // Simple rating calculation: top 10% get positive, bottom 10% get negative
        double percentile = (double) rank / totalParticipants;
        
        if (percentile <= 0.1) {
            return 50 + (score / 2);
        } else if (percentile <= 0.25) {
            return 30 + (score / 3);
        } else if (percentile <= 0.5) {
            return 10 + (score / 5);
        } else if (percentile <= 0.75) {
            return 0;
        } else {
            return -10;
        }
    }
    
    private void updateContestProblemStats(Long contestProblemId) {
        ContestProblem cp = contestProblemRepository.findById(contestProblemId)
                .orElseThrow(() -> new ResourceNotFoundException("Contest problem not found"));
        
        List<ContestSubmission> submissions = contestSubmissionRepository
                .findByContestIdAndContestProblemId(cp.getContest().getId(), cp.getId());
        
        cp.setAttemptsCount(submissions.size());
        cp.setSolvedCount((int) submissions.stream().filter(ContestSubmission::getIsAccepted).count());
        cp.setUnsolvedCount((int) submissions.stream().filter(s -> !s.getIsAccepted()).count());
        
        Set<Long> uniqueUsers = submissions.stream()
                .map(s -> s.getUser().getId())
                .collect(Collectors.toSet());
        cp.setAttemptUsersCount(uniqueUsers.size());
        
        contestProblemRepository.save(cp);
    }
    
    private void updateParticipantScore(Long contestId, Long userId) {
        ContestParticipant participant = participantRepository.findByContestIdAndUserId(contestId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));
        
        List<ContestProblem> problems = contestProblemRepository.findByContestIdOrderByOrderIndexAsc(contestId);
        
        int totalScore = 0;
        int problemsSolved = 0;
        long totalPenalty = 0;
        
        for (ContestProblem cp : problems) {
            List<ContestSubmission> submissions = contestSubmissionRepository
                    .findUserProblemSubmissions(contestId, userId, cp.getId());
            
            Optional<ContestSubmission> accepted = submissions.stream()
                    .filter(ContestSubmission::getIsAccepted)
                    .findFirst();
            
            if (accepted.isPresent()) {
                totalScore += accepted.get().getScore();
                problemsSolved++;
                totalPenalty += accepted.get().getTimeTaken();
                
                // Add penalty for wrong attempts (5 minutes per wrong attempt)
                long wrongAttempts = submissions.stream()
                        .filter(s -> !s.getIsAccepted() && s.getSubmittedAt().isBefore(accepted.get().getSubmittedAt()))
                        .count();
                totalPenalty += wrongAttempts * 300; // 5 minutes = 300 seconds
            }
        }
        
        participant.setScore(totalScore);
        participant.setProblemsSolved(problemsSolved);
        participant.setTotalPenalty(totalPenalty);
        
        participantRepository.save(participant);
    }
    
    private void updateContestStatus(Contest contest) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = contest.getStartTime().plusSeconds(contest.getDurationSeconds());
        
        if (now.isBefore(contest.getStartTime())) {
            contest.setStatus(Contest.ContestStatus.UPCOMING);
        } else if (now.isAfter(endTime)) {
            contest.setStatus(Contest.ContestStatus.FINISHED);
        } else {
            contest.setStatus(Contest.ContestStatus.ACTIVE);
        }
        
        contestRepository.save(contest);
    }
    
    private ContestResponse mapToResponse(Contest contest, Long userId) {
        ContestResponse response = new ContestResponse();
        response.setId(contest.getId().toString());
        response.setNumber(contest.getNumber());
        response.setTitle(contest.getTitle());
        response.setDescription(contest.getDescription());
        response.setImageUrl(contest.getImageUrl());
        response.setStartTime(contest.getStartTime());
        response.setDurationSeconds(contest.getDurationSeconds());
        response.setProblemCount(contest.getProblemCount());
        response.setParticipantsCount(contest.getParticipantsCount());
        
        // Parse prize pool
        try {
            if (contest.getPrizePool() != null && !contest.getPrizePool().isEmpty()) {
                List<Object> prizePool = objectMapper.readValue(contest.getPrizePool(), new TypeReference<List<Object>>() {});
                response.setPrizePool(prizePool);
            } else {
                response.setPrizePool(new ArrayList<>());
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            response.setPrizePool(new ArrayList<>());
        }
        
        // Determine status for user
        String status = contest.getStatus().name().toLowerCase();
        if (userId != null && participantRepository.existsByContestIdAndUserId(contest.getId(), userId)) {
            status = "registered";
        }
        response.setStatus(status);
        
        return response;
    }
    
    private ContestParticipantResponse mapToParticipantResponse(ContestParticipant participant) {
        ContestParticipantResponse response = new ContestParticipantResponse();
        response.setId(participant.getId());
        response.setUserId(participant.getUser().getId());
        response.setUsername(participant.getUser().getUsername());
        response.setFirstName(participant.getUser().getFirstName());
        response.setLastName(participant.getUser().getLastName());
        response.setAvatarUrl(participant.getUser().getAvatarUrl());
        response.setRegisteredAt(participant.getRegisteredAt());
        response.setScore(participant.getScore());
        response.setRank(participant.getRank());
        response.setRatingChange(participant.getRatingChange());
        response.setProblemsSolved(participant.getProblemsSolved());
        response.setTotalPenalty(participant.getTotalPenalty());
        
        // Calculate total rating from all contests
        List<ContestParticipant> userHistory = participantRepository.findUserContestHistory(participant.getUser().getId());
        int totalRating = userHistory.stream()
                .filter(cp -> cp.getRatingChange() != null)
                .mapToInt(ContestParticipant::getRatingChange)
                .sum();
        response.setTotalRating(totalRating);
        
        return response;
    }
}
